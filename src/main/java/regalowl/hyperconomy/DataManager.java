package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.simpledatalib.file.FileTools;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.simpledatalib.sql.SQLWrite;
import regalowl.simpledatalib.sql.WriteStatement;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperBankManager;
import regalowl.hyperconomy.account.HyperPlayerManager;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.HyperEconomyCreationEvent;
import regalowl.hyperconomy.event.HyperEconomyDeletionEvent;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.ChestShopHandler;
import regalowl.hyperconomy.shop.HyperShopManager;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.util.Backup;
import regalowl.hyperconomy.util.DatabaseUpdater;

public class DataManager implements HyperEventListener {

	private transient HyperConomy hc;
	private transient DatabaseUpdater du;
	private transient FileConfiguration config;

	
	
	private boolean loadActive;
	private ConcurrentHashMap<String, HyperEconomy> economies = new ConcurrentHashMap<String, HyperEconomy>();
	private CopyOnWriteArrayList<String> categories = new CopyOnWriteArrayList<String>();
	private HashMap<Integer, String> itemDataIdMap = new HashMap<Integer, String>();
	private HashMap<String, Integer> itemDataDataMap = new HashMap<String, Integer>();
	private HashMap<HItemStack, Integer> itemStackMap = new HashMap<HItemStack, Integer>();
	private String defaultServerShopAccount;
	private HyperPlayerManager hpm;
	private HyperBankManager hbm;
	private HyperShopManager hsm;
	private ChestShopHandler csh;
	
	private int nextObjectDataId = 1;



	public DataManager(HyperConomy hc) {
		this.hc = hc;
		loadActive = false;
	}
	
	public void initialize() {
		config = hc.getConf();
		defaultServerShopAccount = config.getString("shop.default-server-shop-account");
		hc.getHyperEventHandler().registerListener(this);
		hpm = new HyperPlayerManager(hc);
		hbm = new HyperBankManager(hc);
		hsm = new HyperShopManager(hc);
		csh = new ChestShopHandler(hc);
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
	public ChestShopHandler getChestShopHandler() {
		return csh;
	}

	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof DataLoadEvent) {
			DataLoadEvent devent = (DataLoadEvent)event;
			if (devent.loadType == DataLoadType.START) {
				if (loadActive) {return;}
				loadActive = true;
				new Thread(new Runnable() {
					public void run() {
						loadEconomies();
					}
				}).start();
			} else if (devent.loadType == DataLoadType.CHEST_SHOPS) {
				loadAllCategories();
				hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.COMPLETE));
				hc.getHyperLock().setLoadLock(false);
				loadActive = false;
				runDatabaseMaintenance();
			}
		} else if (event instanceof TradeObjectModificationEvent) {
			TradeObjectModificationEvent tevent = (TradeObjectModificationEvent)event;
			TradeObject to = tevent.getTradeObject();
			if (to != null) {
				for (String cat:to.getCategories()) {
					if (!categories.contains(cat)) {
						categories.add(cat);
					}
				}
			}
		}
	}

	
	private void loadAllCategories() {
		categories.clear();
		for (TradeObject to:getTradeObjects()) {
			for (String cat:to.getCategories()) {
				if (!categories.contains(cat)) {
					categories.add(cat);
				}
			}
		}
	}
	
	public ArrayList<String> getCategories() {
		ArrayList<String> cats = new ArrayList<String>();
		cats.addAll(categories);
		return cats;
	}
	
	public boolean categoryExists(String category) {
		return categories.contains(category);
	}
	
	public void runDatabaseMaintenance() {
		hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_object_data WHERE ID NOT IN (SELECT DATA_ID FROM hyperconomy_objects) AND ID NOT IN (SELECT DATA_ID FROM hyperconomy_chest_shop_items)");
		hpm.purgeDeadAccounts();
	}
	
	private void loadEconomies() {
		SQLRead sr = hc.getSQLRead();
		sr.setErrorLogging(false);
		QueryResult qr = sr.select("SELECT VALUE FROM hyperconomy_settings WHERE SETTING = 'version'");
		sr.setErrorLogging(true);
		boolean success = du.updateTables(qr);
		if (!success) {
			hc.disable(false);
			return;
		}
		qr = sr.select("SELECT NAME FROM hyperconomy_objects LIMIT 1");
		if (!qr.next()) {setupDefaultEconomies();}
		qr = sr.select("SELECT MAX(ID) AS MAX FROM hyperconomy_object_data");
		while (qr.next()) {
			nextObjectDataId = qr.getInt("MAX") + 1;
		}
		qr = sr.select("SELECT * FROM hyperconomy_object_data");
		while (qr.next()) {
			int id = qr.getInt("ID");
			String data = qr.getString("DATA");
			itemDataIdMap.put(id, data);
			itemDataDataMap.put(data, id);
			HItemStack stack = new HItemStack(data);
			if (stack.isBlank()) continue;
			itemStackMap.put(stack, id);
		}
		if (du.refreshBaseEconomy()) restoreEconomy("base");
		economies.clear();
		qr = sr.select("SELECT * FROM hyperconomy_economies");
		boolean successfulLoad = true;
		while (qr.next()) {
			String name = qr.getString("NAME");
			HyperEconomy econ = new HyperEconomy(hc, name, qr.getString("HYPERACCOUNT"));
			if (!econ.successfulLoad()) successfulLoad = false;
			economies.put(name, econ);
		}
		if (!successfulLoad) return;
		hc.getDebugMode().ayncDebugConsoleMessage("Economies loaded.");
		hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.ECONOMY));
	}
	
	
	private void restoreEconomy(String economy) {
		SQLWrite sw = hc.getSimpleDataLib().getSQLManager().getSQLWrite();
		boolean writeState = sw.writeSync();
		sw.writeSync(true);
		
		//delete and recreate default object CSV files
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		String defaultObjectDataPath = hc.getFolderPath() + File.separator + "defaultObjectData.csv";
		FileTools ft = hc.getFileTools();
		if (ft.fileExists(defaultObjectsPath)) ft.deleteFile(defaultObjectsPath);
		if (ft.fileExists(defaultObjectDataPath)) ft.deleteFile(defaultObjectDataPath);
		ft.copyZippedFileFromJar("defaultObjects.csv.zip", hc.getFolderPath());
		ft.copyZippedFileFromJar("defaultObjectData.csv.zip", hc.getFolderPath());
		
		//clear economy and object table of any existing economy data
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("NAME", economy);
		hc.getSQLWrite().performDelete("hyperconomy_economies", conditions);
		conditions = new HashMap<String,String>();
		conditions.put("ECONOMY", economy);
		hc.getSQLWrite().performDelete("hyperconomy_objects", conditions);
		
		//add economy to economy table
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", economy);
		values.put("HYPERACCOUNT", config.getString("shop.default-server-shop-account"));
		sw.performInsert("hyperconomy_economies", values);
		
		//cache original object data CSV in hashmap
		HashMap<Integer, String> defaultObjectData = new HashMap<Integer, String>();
		QueryResult data = hc.getFileTools().readCSV(defaultObjectDataPath);
		while (data.next()) {
			defaultObjectData.put(data.getInt("ID"), data.getString("DATA"));
		}
		
		data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		while (data.next()) {
			HashMap<String,String> objectValues = new HashMap<String,String>();
			for (String column : columns) {
				if (column.equalsIgnoreCase("DATA_ID")) {
					int originalDataId = data.getInt(column);
					String originalDataString = defaultObjectData.get(originalDataId);
					Integer existingDataId = getItemDataId(originalDataString); //check if this data already exists
					if (existingDataId != null) { //if data already exists, use existing data's id
						objectValues.put(column, existingDataId+"");
					} else { //if data doesn't exist, use new id and add data to object_data table
						int newId = addItemDataString(originalDataString);
						objectValues.put(column, newId+"");
					}
				} else if (column.equalsIgnoreCase("ECONOMY")) {
					objectValues.put(column, economy);
				} else {
					objectValues.put(column, data.getString(column));
				}
			}
			sw.performInsert("hyperconomy_objects", objectValues);
		}
		
		//clean up
		defaultObjectData.clear();
		ft.deleteFile(defaultObjectsPath);
		ft.deleteFile(defaultObjectDataPath);
		
		sw.writeSyncQueue();
		sw.writeSync(writeState);
		hc.getDebugMode().ayncDebugConsoleMessage(economy + " economy recreated.");
	}
	
	
	
	
	public synchronized ArrayList<String> loadNewItems(String economy) {
		HyperEconomy econ = getEconomyIB(economy);
		ArrayList<String> objectsAdded = new ArrayList<String>();
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		String defaultObjectDataPath = hc.getFolderPath() + File.separator + "defaultObjectData.csv";
		FileTools ft = hc.getFileTools();
		ft.copyZippedFileFromJar("defaultObjects.csv.zip", hc.getFolderPath());
		ft.copyZippedFileFromJar("defaultObjectData.csv.zip", hc.getFolderPath());
		SQLWrite sw = hc.getSQLWrite();
		
		HashMap<Integer, String> defaultObjectData = new HashMap<Integer, String>(); //cache original object data CSV in hashmap
		QueryResult data = hc.getFileTools().readCSV(defaultObjectDataPath);
		while (data.next()) {
			defaultObjectData.put(data.getInt("ID"), data.getString("DATA"));
		}
		
		data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		while (data.next()) {
			String objectName = data.getString("NAME");
			if (econ.objectTest(objectName.toLowerCase())) continue; //skip objects already in economy
			objectsAdded.add(objectName);
			HashMap<String, String> values = new HashMap<String, String>();
			for (String column : columns) {
				if (column.equalsIgnoreCase("DATA_ID")) { //remap data id if necessary
					int originalDataId = data.getInt(column);
					String originalDataString = defaultObjectData.get(originalDataId);
					Integer existingDataId = getItemDataId(originalDataString); //check if this data already exists
					if (existingDataId != null) { //if data already exists, use existing data's id
						values.put(column, existingDataId+"");
					} else { //if data doesn't exist, use new id and add data to object_data table
						int newId = addItemDataString(originalDataString);
						values.put(column, newId+"");
					}
				} else if (column.equalsIgnoreCase("ECONOMY")) {
					values.put(column, econ.getName());
				} else {
					values.put(column, data.getString(column));
				}
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		ft.deleteFile(defaultObjectsPath);
		ft.deleteFile(defaultObjectDataPath);
		hc.restart();
		return objectsAdded;
	}
	
	
	
	public void setDefaultPrices(String economy) {
		if (!economyExists(economy)) return;
		if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		ft.copyZippedFileFromJar("defaultObjects.csv.zip", hc.getFolderPath());
		QueryResult qr = hc.getFileTools().readCSV(defaultObjectsPath);
		while (qr.next()) {
			String objectName = qr.getString("NAME");
			TradeObject to = getEconomyIB(economy).getTradeObject(objectName);
			if (to == null) continue;
			to.setStartPrice(qr.getDouble("STARTPRICE"));
			to.setStaticPrice(qr.getDouble("STATICPRICE"));
			to.setValue(qr.getDouble("VALUE"));
		}
		ft.deleteFile(defaultObjectsPath);
	}
	
	
	public void updateItems(String economy) {
		if (!economyExists(economy)) return;
		if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		String defaultObjectDataPath = hc.getFolderPath() + File.separator + "defaultObjectData.csv";
		FileTools ft = hc.getFileTools();
		ft.copyZippedFileFromJar("defaultObjects.csv.zip", hc.getFolderPath());
		ft.copyZippedFileFromJar("defaultObjectData.csv.zip", hc.getFolderPath());	
		HashMap<Integer, String> defaultObjectData = new HashMap<Integer, String>(); //cache original object data CSV in hashmap
		QueryResult data = hc.getFileTools().readCSV(defaultObjectDataPath);
		while (data.next()) {
			defaultObjectData.put(data.getInt("ID"), data.getString("DATA"));
		}
		QueryResult qr = hc.getFileTools().readCSV(defaultObjectsPath);
		while (qr.next()) {
			String objectName = qr.getString("NAME");
			TradeObject to = getEconomyIB(economy).getTradeObject(objectName);
			if (to == null) continue;
			if (to.getVersion() == qr.getDouble("VERSION")) continue;
			
			to.setDisplayName(qr.getString("DISPLAY_NAME"));
			to.setAliases(CommonFunctions.explode(qr.getString("ALIASES")));
			to.setCategories(CommonFunctions.explode(qr.getString("CATEGORIES")));
			String newData = defaultObjectData.get(qr.getInt("DATA_ID"));
			to.setData(newData);
			to.setCompositeData(qr.getString("COMPONENTS"));
			to.setVersion(qr.getDouble("VERSION"));
			to.setName(qr.getString("NAME"));
		}
		ft.deleteFile(defaultObjectsPath);
		ft.deleteFile(defaultObjectDataPath);
	}
	
	public synchronized void updateNamesFromCSV(String economy) {
		if (!economyExists(economy)) return;
		HyperEconomy he = getEconomyIB(economy);
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		ft.copyZippedFileFromJar("defaultObjects.csv.zip", hc.getFolderPath());
		
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		while (data.next()) {
			String objectName = data.getString("NAME");
			String aliasString = data.getString("ALIASES");
			ArrayList<String> names = CommonFunctions.explode(aliasString);
			String displayName = data.getString("DISPLAY_NAME");
			names.add(displayName);
			names.add(objectName);
			for (String cname:names) {
				TradeObject ho = he.getTradeObject(cname);
				if (ho == null) {continue;}
				ho.setAliases(CommonFunctions.explode(aliasString));
				ho.setDisplayName(displayName);
				ho.setName(objectName);
				break;
			}
		}
		for (Shop s:hc.getHyperShopManager().getShops()) {
			if (s instanceof PlayerShop) {
				PlayerShop ps = (PlayerShop)s;
				for (TradeObject ho:ps.getShopObjects()) {
					ho.setParentTradeObject(ho.getParentTradeObject());
				}
			}
		}
		ft.deleteFile(defaultObjectsPath);
	}
	
	
	
	private void setupDefaultEconomies() {
		restoreEconomy("base");
		restoreEconomy("default");
		hc.getDebugMode().ayncDebugConsoleMessage("Default economies created.");
	}
	
	
	public HyperEconomy getBaseEconomy() {
		return economies.get("base");
	}
	
	public HyperEconomy getEconomy(String name) {
		if (name.equals("base")) return null;
		return getEconomyIB(name);
	}
	
	/*
	 * Includes base economy
	 */
	public HyperEconomy getEconomyIB(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getName().equalsIgnoreCase(name)) {
				return he;
			}
		}
		return null;
	}
	
	public HyperEconomy getDefaultEconomy() {
		return getEconomyIB("default");
	}
	
	
	public boolean economyExists(String economy) {
		if (economy.equalsIgnoreCase("base")) return false;
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
			if (entry.getKey().equals("base")) continue;
			econs.add(entry.getValue());
		}
		return econs;
	}
	
	public void shutDown() {
		for (HyperEconomy he: economies.values()) {
			he.clearData();
		}
		economies.clear();
	}


	public ArrayList<String> getEconomyList() {
		ArrayList<String> econs = new ArrayList<String>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			if (entry.getKey().equals("base")) continue;
			HyperEconomy he = entry.getValue();
			econs.add(he.getName());
		}
		return econs;
	}


	
	public ArrayList<TradeObject> getTradeObjects() {
		ArrayList<TradeObject> hyperObjects = new ArrayList<TradeObject>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			if (entry.getKey().equals("base")) continue;
			HyperEconomy he = entry.getValue();
			for (TradeObject ho:he.getTradeObjects()) {
				hyperObjects.add(ho);
			}
		}
		return hyperObjects;
	}



	
	/*
	public void addEconomy(HyperEconomy econ) {
		new Thread(new EconomyBuilder(econ)).start();
	}
	 */
	
	public void createNewEconomy(String name, String templateEconomy, boolean cloneAll) {
		new Thread(new EconomyBuilder(name, templateEconomy, cloneAll)).start();
	}
	
	private class EconomyBuilder implements Runnable {
		private String name;
		private String templateEconomyName = "";
		private boolean cloneAll = false;
		private HyperEconomy templateEconomy = null;
		
		//public EconomyBuilder(HyperEconomy templateEconomy) {
		//	this.templateEconomy = templateEconomy;
		//}
		public EconomyBuilder(String name, String templateEconomy, boolean cloneAll) {
			this.name = name;
			this.templateEconomyName = templateEconomy;
			this.cloneAll = cloneAll;
		}
		
		@Override
		public void run() {
			if (!economyExists(templateEconomyName)) {
				templateEconomy = getBaseEconomy();
			} else {
				templateEconomy = getEconomyIB(templateEconomyName);
			}
			if (name == null || name.equals("base")) return;
			SQLWrite sw = hc.getSQLWrite();
			boolean writeState = sw.writeSync();
			sw.writeSync(true);
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("NAME", name);
			values.put("HYPERACCOUNT", defaultServerShopAccount);
			sw.performInsert("hyperconomy_economies", values);
			for (TradeObject ho:templateEconomy.getTradeObjects()) {
				values = new HashMap<String,String>();
				values.put("NAME", ho.getName());
				values.put("DISPLAY_NAME", ho.getDisplayName());
				values.put("ALIASES", ho.getAliasesString());
				values.put("CATEGORIES", ho.getCategoriesString());
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
				values.put("COMPONENTS", ho.getCompositeData());
				values.put("DATA_ID", ho.getDataId()+"");
				if (cloneAll) {
					values.put("INITIATION", ho.useInitialPricing()+"");
					values.put("STOCK", ho.getStock()+"");
				} else {
					values.put("INITIATION", "true");
					values.put("STOCK", 0+"");
				}
				sw.performInsert("hyperconomy_objects", values);
			}
			sw.writeSyncQueue();
			sw.writeSync(writeState);
			HyperEconomy newEconomy = new HyperEconomy(hc, name, templateEconomy.getAccountData());
			economies.put(name, newEconomy);
			hc.getHyperEventHandler().fireEvent(new HyperEconomyCreationEvent(newEconomy));
		}
	}
	
	public void deleteEconomy(String economy) {
		if (economy.equalsIgnoreCase("base")) return;
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("ECONOMY", economy);
		hc.getSQLWrite().performDelete("hyperconomy_objects", conditions);
		conditions = new HashMap<String,String>();
		conditions.put("NAME", economy);
		hc.getSQLWrite().performDelete("hyperconomy_economies", conditions);
		economies.remove(economy);
		hc.getHyperEventHandler().fireEvent(new HyperEconomyDeletionEvent(economy));
	}



	public boolean accountExists(String name) {
		if (name.contains(":")) {
			String[] accountData = name.split(Pattern.quote(":"));
			String accountName = accountData[1];
			if (accountData[0].equalsIgnoreCase("PLAYER")) {
				return hc.getHyperPlayerManager().hyperPlayerExists(accountName);
			} else if (accountData[0].equalsIgnoreCase("BANK")) {
				return hc.getHyperBankManager().hasBank(accountName);
			} else {
				return false;
			}
		} else {
			if (hc.getHyperPlayerManager().hyperPlayerExists(name) || hc.getHyperBankManager().hasBank(name)) return true;
			return false;
		}
	}
	public HyperAccount getAccount(String name) {
		if (name.contains(":")) {
			String[] accountData = name.split(Pattern.quote(":"));
			String accountName = accountData[1];
			if (accountData[0].equalsIgnoreCase("PLAYER")) {
				return hc.getHyperPlayerManager().getHyperPlayer(accountName);
			} else if (accountData[0].equalsIgnoreCase("BANK")) {
				return hc.getHyperBankManager().getHyperBank(accountName);
			} else {
				return null;
			}
		} else {
			String accountName = name;
			if (hc.getHyperPlayerManager().hyperPlayerExists(accountName)) {
				return hc.getHyperPlayerManager().getHyperPlayer(accountName);
			} else if (hc.getHyperBankManager().hasBank(accountName)) {
				return hc.getHyperBankManager().getHyperBank(accountName);
			}
			return null;
		}
	}
	
	public ArrayList<HyperAccount> getAccounts() {
		ArrayList<HyperAccount> accts = new ArrayList<HyperAccount>();
		accts.addAll(hc.getHyperPlayerManager().getHyperPlayers());
		accts.addAll(hc.getHyperBankManager().getHyperBanks());
		return accts;
	}
	
	/**
	 * Replaces all economies with the given economy ArrayList.  Doesn't save the replacement economies to the database.
	 * @param econArray
	 */
	public void setEconomies(ArrayList<HyperEconomy> econArray) {
		economies.clear();
		for(HyperEconomy he:econArray) {
			he.setHyperConomy(hc);
			economies.put(he.getName(), he);
		}
	}
	
	public void addEconomy(HyperEconomy econ) {
		econ.setHyperConomy(hc);
		economies.put(econ.getName(), econ);
		loadAllCategories();
	}

	public String getItemDataString(int id) {
		return itemDataIdMap.get(id);
	}
	
	public Integer getItemDataId(String data) {
		return itemDataDataMap.get(data);
	}
	
	public Integer getItemDataIdFromStack(HItemStack stack) {
		return itemStackMap.get(stack);
	}

	
	public synchronized Integer addItemDataString(String data) {
		if (getItemDataId(data) != null) return getItemDataId(data);
		int newId = nextObjectDataId;
		nextObjectDataId++;
		itemDataIdMap.put(newId, data);
		itemDataDataMap.put(data, newId);
		itemStackMap.put(new HItemStack(data), newId);
		String statement = "INSERT INTO hyperconomy_object_data (ID, DATA) VALUES ('" + newId + "', ?)";
		WriteStatement ws = new WriteStatement(statement, hc.getSimpleDataLib());
		ws.addParameter(data);
		hc.getSQLWrite().addToQueue(ws);
		return newId;
	}

	
}
