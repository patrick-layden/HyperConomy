package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.simpledatalib.file.FileTools;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.simpledatalib.sql.SQLWrite;
import regalowl.simpledatalib.sql.SyncSQLWrite;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperBankManager;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.account.HyperPlayerManager;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.HyperEconomyCreationEvent;
import regalowl.hyperconomy.shop.HyperShopManager;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.util.DatabaseUpdater;

public class DataManager {

	private transient HyperConomy hc;
	private transient SQLRead sr;
	private transient SyncSQLWrite ssw;
	private transient DatabaseUpdater du;
	private transient FileConfiguration config;

	
	
	private boolean loadActive;
	private ConcurrentHashMap<String, HyperEconomy> economies = new ConcurrentHashMap<String, HyperEconomy>();
	private String defaultServerShopAccount;
	private HyperPlayerManager hpm;
	private HyperBankManager hbm;
	private HyperShopManager hsm;



	public DataManager(HyperConomy hc) {
		this.hc = hc;
		loadActive = false;
		config = hc.getConf();
		defaultServerShopAccount = config.getString("shop.default-server-shop-account");
		hc.getHyperEventHandler().registerListener(this);
		sr = hc.getSQLRead();
		ssw = hc.getSimpleDataLib().getSQLManager().getSyncSQLWrite();
		hpm = new HyperPlayerManager(hc);
		hbm = new HyperBankManager(hc);
		hsm = new HyperShopManager(hc);
		du = new DatabaseUpdater(hc);
	}


	public ArrayList<String> getTablesList() {
		return du.getTablesList();
	}
	
	public DatabaseUpdater getDatabaseUpdater() {
		return du;
	}
	public HyperPlayerManager getHyperPlayerManager() {
		return hpm;
	}
	public HyperBankManager getHyperBankManager() {
		return hbm;
	}
	public HyperShopManager getHyperShopManager() {
		return hsm;
	}
	

	@EventHandler
	public void onDataLoad(DataLoadEvent event) {
		if (event.loadType == DataLoadType.START) {
			if (loadActive) {return;}
			loadActive = true;
			new Thread(new Runnable() {
				public void run() {
					loadEconomies();
				}
			}).start();
		} else if (event.loadType == DataLoadType.SHOP) {
			hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.COMPLETE));
		} else if (event.loadType == DataLoadType.COMPLETE) {
			hc.getHyperLock().setLoadLock(false);
			loadActive = false;
		}
	}
	
	private void loadEconomies() {
		hc.getSQLRead().setErrorLogging(false);
		QueryResult qr = sr.select("SELECT VALUE FROM hyperconomy_settings WHERE SETTING = 'version'");
		hc.getSQLRead().setErrorLogging(true);
		du.updateTables(qr);
		qr = sr.select("SELECT * FROM hyperconomy_objects WHERE economy = 'default'");
		if (!qr.next()) {setupDefaultEconomy();}
		economies.clear();
		ArrayList<String> econs = sr.getStringList("hyperconomy_economies", "NAME", null);
		for (String e : econs) {
			economies.put(e, new HyperEconomy(hc, e));
		}
		hc.getDebugMode().ayncDebugConsoleMessage("Economies loaded.");
		hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.ECONOMY));
	}
	
	private void setupDefaultEconomy() {
		//set up default hyperconomy_objects and economies if they don't exist
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		if (ft.fileExists(defaultObjectsPath)) {ft.deleteFile(defaultObjectsPath);}
		ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", "default");
		values.put("HYPERACCOUNT", config.getString("shop.default-server-shop-account"));
		ssw.queueInsert("hyperconomy_economies", values);
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		while (data.next()) {
			values = new HashMap<String, String>();
			for (String column : columns) {
				values.put(column, data.getString(column));
			}
			ssw.queueInsert("hyperconomy_objects", values);
		}
		ft.deleteFile(defaultObjectsPath);

		//set up default hyperconomy_composites if they don't exist
		defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultComposites.csv";
		if (ft.fileExists(defaultObjectsPath)) {ft.deleteFile(defaultObjectsPath);}
		ft.copyFileFromJar("defaultComposites.csv", defaultObjectsPath);
		data = hc.getFileTools().readCSV(defaultObjectsPath);
		columns = data.getColumnNames();
		while (data.next()) {
			values = new HashMap<String, String>();
			for (String column : columns) {
				values.put(column, data.getString(column));
			}
			ssw.queueInsert("hyperconomy_composites", values);
		}
		ft.deleteFile(defaultObjectsPath);
		ssw.writeQueue();
		hc.getDebugMode().ayncDebugConsoleMessage("Default economy created.");
	}

	
	
	public HyperEconomy getEconomy(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getName().equalsIgnoreCase(name)) {
				return he;
			}
		}
		return null;
	}
	public HyperEconomy getDefaultEconomy() {
		return getEconomy("default");
	}
	
	
	public boolean economyExists(String economy) {
		if (economy == null || economy == "") {return false;}
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getName().equalsIgnoreCase(economy)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<HyperEconomy> getEconomies() {
		ArrayList<HyperEconomy> econs = new ArrayList<HyperEconomy>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			econs.add(entry.getValue());
		}
		return econs;
	}
	
	public void shutDown() {
		hpm.purgeDeadAccounts();
		for (HyperEconomy he: economies.values()) {
			he.clearData();
		}
		economies.clear();
	}


	public ArrayList<String> getEconomyList() {
		ArrayList<String> econs = new ArrayList<String>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			econs.add(he.getName());
		}
		return econs;
	}


	
	public ArrayList<TradeObject> getTradeObjects() {
		ArrayList<TradeObject> hyperObjects = new ArrayList<TradeObject>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			for (TradeObject ho:he.getTradeObjects()) {
				hyperObjects.add(ho);
			}
		}
		return hyperObjects;
	}



	

	
	public void createNewEconomy(String name, String templateEconomy, boolean cloneAll) {
		if (!economyExists(templateEconomy)) {
			templateEconomy = "default";
		}
		HyperEconomy template = getEconomy(templateEconomy);
		SQLWrite sw = hc.getSQLWrite();
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", name);
		values.put("HYPERACCOUNT", defaultServerShopAccount);
		sw.performInsert("hyperconomy_economies", values);
		for (TradeObject ho:template.getTradeObjects()) {
			values = new HashMap<String,String>();
			values.put("NAME", ho.getName());
			values.put("DISPLAY_NAME", ho.getDisplayName());
			values.put("ALIASES", ho.getAliasesString());
			values.put("ECONOMY", name);
			values.put("TYPE", ho.getType().toString());
			values.put("VALUE", ho.getValue()+"");
			values.put("STATIC", ho.isStatic()+"");
			values.put("STATICPRICE", ho.getStaticPrice()+"");
			values.put("MEDIAN", ho.getMedian()+"");
			values.put("STARTPRICE", ho.getStartPrice()+"");
			values.put("CEILING", ho.getCeiling()+"");
			values.put("FLOOR", ho.getFloor()+"");
			values.put("MAXSTOCK", ho.getMaxStock()+"");
			values.put("DATA", ho.getData());
			if (cloneAll) {
				values.put("INITIATION", ho.useInitialPricing()+"");
				values.put("STOCK", ho.getStock()+"");
			} else {
				values.put("INITIATION", "true");
				values.put("STOCK", 0+"");
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		hc.restart();
		hc.getHyperEventHandler().fireEvent(new HyperEconomyCreationEvent());
	}
	
	public void deleteEconomy(String economy) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("ECONOMY", economy);
		hc.getSQLWrite().performDelete("hyperconomy_objects", conditions);
		conditions = new HashMap<String,String>();
		conditions.put("NAME", economy);
		hc.getSQLWrite().performDelete("hyperconomy_economies", conditions);
		hc.restart();
	}

	
	/*
	public void createEconomyFromDefaultCSV(String econ, boolean restart) {
		if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {
			new Backup();
		}
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		if (!ft.fileExists(defaultObjectsPath)) {
			ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.addToQueue("DELETE FROM hyperconomy_economies WHERE NAME = '"+econ+"'");
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", econ);
		values.put("HYPERACCOUNT", hc.getConf().getString("shop.default-server-shop-account"));
		sw.performInsert("hyperconomy_economies", values);
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		sw.addToQueue("DELETE FROM hyperconomy_objects WHERE ECONOMY = '"+econ+"'");
		while (data.next()) {
			values = new HashMap<String, String>();
			for (String column : columns) {
				values.put(column, data.getString(column));
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		ft.deleteFile(defaultObjectsPath);
		if (restart) {
			hc.restart();
		}
	}
	*/



	
	//TODO
	//remove these methods eventually
	public boolean accountExists(String name) {
		return hpm.accountExists(name);
	}
	public HyperAccount getAccount(String name) {
		return hpm.getAccount(name);
	}
	public boolean hyperPlayerExists(String name) {
		return hpm.playerAccountExists(name);
	}
	public HyperPlayer getHyperPlayer(String player) {
		return hpm.getHyperPlayer(player);
	}
	
	
}
