package regalowl.hyperconomy.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.simpledatalib.sql.Field;
import regalowl.simpledatalib.sql.FieldType;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.simpledatalib.sql.SQLWrite;
import regalowl.simpledatalib.sql.Table;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.bukkit.BukkitConnector;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableItemStack;

public class DatabaseUpdater {

	private transient HyperConomy hc;
	private transient SQLWrite sw;
	private transient SQLRead sr;
	public final double requiredDbVersion = 1.4;
	private double currentDbVersion;
	ArrayList<String> tables = new ArrayList<String>();
	
	public DatabaseUpdater(HyperConomy hc) {
		this.hc = hc;
		tables.add("settings");
		tables.add("objects");
		tables.add("players");
		tables.add("log");
		tables.add("history");
		tables.add("audit_log");
		tables.add("shop_objects");
		tables.add("frame_shops");
		tables.add("banks");
		tables.add("shops");
		tables.add("info_signs");
		tables.add("item_displays");
		tables.add("economies");
		tables.add("time_effects");
	}
	
	
	public ArrayList<String> getTablesList() {
		return tables;
	}
	
	/*
	public double getCurrentDatabaseVersion() {
		return currentDbVersion;
	}
	*/

	private void loadTables() {
		for (String table:tables) {
			hc.getSQLManager().addTable("hyperconomy_"+table);
		}
		hc.getSQLManager().loadTables();
	}
	

	public boolean updateTables(QueryResult qr) {
		sw = hc.getSQLManager().getSQLWrite();
		sr = hc.getSQLManager().getSQLRead();
		boolean writeState = sw.writeSync();
		sw.writeSync(true);
		if (qr.next()) {
			currentDbVersion = Double.parseDouble(qr.getString("VALUE"));
			loadTables();
			if (currentDbVersion == 1.34) {//adds ability for player shops to behave like server shops
				Table t = hc.getSQLManager().getTable("hyperconomy_shops");
				Field f = t.generateField("USE_ECONOMY_STOCK", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setDefault("1");
				t.addFieldToDatabase(f, t.getField("WORLD"));
				sw.addToQueue("UPDATE hyperconomy_shops SET USE_ECONOMY_STOCK = '0' WHERE TYPE = 'player'");
				setDBVersion(1.35);
				sw.writeSyncQueue();
			}
			if (currentDbVersion == 1.35) {//converts to new object data storage format and moves categories to database
				BukkitConnector bc = (BukkitConnector)hc.getMC();
				QueryResult result = sr.select("SELECT NAME, TYPE, DATA FROM hyperconomy_objects WHERE ECONOMY = 'default'");
				while (result.next()) {
					String name = result.getString("NAME");
					String type = result.getString("TYPE");
					String data = result.getString("DATA");
					if (type.equalsIgnoreCase("ITEM")) {
						SerializableItemStack sis = new SerializableItemStack(data);
						HItemStack n = bc.getBukkitCommon().getSerializableItemStack(sis.getItem());
						sw.addToQueue("UPDATE hyperconomy_objects SET DATA = '"+n.serialize()+"' WHERE NAME = '"+name+"'");
					} else if (type.equalsIgnoreCase("ENCHANTMENT")) {
						SerializableEnchantment sis = new SerializableEnchantment(data);
						HEnchantment n = new HEnchantment(sis.getEnchantmentName(), sis.getLvl());
						sw.addToQueue("UPDATE hyperconomy_objects SET DATA = '"+n.serialize()+"' WHERE NAME = '"+name+"'");
					}
				}
				sw.writeSyncQueue();
				Table t = hc.getSQLManager().getTable("hyperconomy_objects");
				Field f = t.generateField("CATEGORIES", FieldType.TEXT);
				t.addFieldToDatabase(f, t.getField("ALIASES"));
				hc.getYamlHandler().registerFileConfiguration("categories");
				FileConfiguration cat = hc.getYamlHandler().getFileConfiguration("categories");
				HashMap<String,String> data = new HashMap<String,String>();
				if (cat != null) {
					for (String key:cat.getTopLevelKeys()) {
						ArrayList<String> names = CommonFunctions.explode(cat.getString(key));
						for (String name:names) {
							String cString = "";
							if (data.containsKey(name)) {
								cString = data.get(name);
							}
							data.put(name, cString + key + ",");
						}
					}
				}
				for (Map.Entry<String,String> entry : data.entrySet()) {
					sw.addToQueue("UPDATE hyperconomy_objects SET CATEGORIES = '"+entry.getValue()+"'"
							+ " WHERE (NAME = '"+entry.getKey()+"' OR DISPLAY_NAME = '"+entry.getKey()+"' OR ALIASES LIKE '%"+entry.getKey()+",%')");
				}
				new Backup(hc);
				hc.getYamlHandler().unRegisterFileConfiguration("categories");
				hc.getFileTools().deleteFile(hc.getSimpleDataLib().getStoragePath() + File.separator + "categories.yml");
				setDBVersion(1.36);
				sw.writeSyncQueue();
			}
			if (currentDbVersion == 1.36) {//moves composites to objects table 
				Table compositesTable = hc.getSQLManager().addTable("hyperconomy_composites");
				compositesTable.loadTable();
				Table t = hc.getSQLManager().getTable("hyperconomy_objects");
				Field f = t.generateField("COMPONENTS", FieldType.VARCHAR);f.setFieldSize(1000);f.setNotNull();f.setDefault("");
				t.addFieldToDatabase(f, t.getField("MAXSTOCK"));
				QueryResult result = sr.select("SELECT NAME, ECONOMY FROM hyperconomy_objects");
				while (result.next()) {
					String name = result.getString("NAME");
					String economy = result.getString("ECONOMY");
					QueryResult result2 = sr.select("SELECT COMPONENTS FROM hyperconomy_composites WHERE NAME = '"+name+"'");
					while (result2.next()) {
						String components = result2.getString("COMPONENTS");
						sw.addToQueue("UPDATE hyperconomy_objects SET COMPONENTS = '"+components+"' WHERE NAME = '"+name+"' AND ECONOMY = '"+economy+"'");
					}
				}
				sw.addToQueue("DROP TABLE hyperconomy_composites");
				setDBVersion(1.37);
				sw.writeSyncQueue();
			}
			if (currentDbVersion == 1.37) {//adds time effects table
				Table t = hc.getSQLManager().addTable("hyperconomy_time_effects");
				ArrayList<Field> compositeKey = new ArrayList<Field>();
				Field f = t.addField("TYPE", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
				compositeKey.add(f);
				f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
				compositeKey.add(f);
				f = t.addField("ECONOMY", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
				compositeKey.add(f);
				f = t.addField("VALUE", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
				f = t.addField("SECONDS", FieldType.INTEGER);f.setNotNull();f.setDefault("0");
				f = t.addField("INCREMENT", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
				f = t.addField("TIME_REMAINING", FieldType.INTEGER);f.setNotNull();f.setDefault("0");
				t.setCompositeKey(compositeKey);
				t.save();
				setDBVersion(1.38);
				sw.writeSyncQueue();
			}
			if (currentDbVersion == 1.38) {//adds object versioning
				Table t = hc.getSQLManager().getTable("hyperconomy_objects");
				Field f = t.generateField("VERSION", FieldType.DOUBLE);f.setNotNull();f.setDefault("1");
				t.addFieldToDatabase(f, t.getField("DATA"));
				setDBVersion(1.39);
				sw.writeSyncQueue();
			}
			if (currentDbVersion == 1.39) {//adds chest shop
				Table t = hc.getSQLManager().addTable("hyperconomy_chest_shops");
				Field f = t.addField("ID", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();f.setPrimaryKey();
				f = t.addField("WORLD", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
				f = t.addField("X", FieldType.INTEGER);f.setNotNull();
				f = t.addField("Y", FieldType.INTEGER);f.setNotNull();
				f = t.addField("Z", FieldType.INTEGER);f.setNotNull();
				f = t.addField("OWNER", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
				f = t.addField("PRICE_INCREMENT", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
				t.save();
				t = hc.getSQLManager().addTable("hyperconomy_chest_shop_items");
				ArrayList<Field> compositeKey = new ArrayList<Field>();
				f = t.addField("CHEST_ID", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
				compositeKey.add(f);
				f = t.addField("SLOT", FieldType.INTEGER);f.setNotNull();
				compositeKey.add(f);
				f = t.addField("DATA", FieldType.TEXT);f.setNotNull();
				f = t.addField("BUY_PRICE", FieldType.DOUBLE);f.setNotNull();
				f = t.addField("SELL_PRICE", FieldType.DOUBLE);f.setNotNull();
				f = t.addField("TYPE", FieldType.VARCHAR);f.setFieldSize(40);f.setNotNull();
				t.setCompositeKey(compositeKey);
				t.save();
				setDBVersion(1.4);
				sw.writeSyncQueue();
			}
		} else {
			createTables();
		}
		sw.writeSyncQueue();
		sw.writeSync(writeState);
		if (requiredDbVersion != currentDbVersion) {
			hc.getMC().logSevere("[HyperConomy]Your database (version "+currentDbVersion+") is not compatible with this version of HyperConomy.");
			hc.getMC().logSevere("[HyperConomy]Please read the upgrading page in the HyperConomy wiki for more information.");
			hc.getMC().logSevere("[HyperConomy]Shutting down...");
			return false;
		}
		return true;
	}
	
	private void setDBVersion(double version) {
		this.currentDbVersion = version;
		sw.addToQueue("UPDATE hyperconomy_settings SET VALUE = '"+version+"' WHERE SETTING = 'version'");
	}

	public void createTables() {
		hc.getDebugMode().ayncDebugConsoleMessage("Creating database tables.");
		SQLWrite sw = hc.getSQLManager().getSQLWrite();
		boolean writeState = sw.writeSync();
		sw.writeSync(true);

		Table t = hc.getSQLManager().addTable("hyperconomy_settings");
		Field f = t.addField("SETTING", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setPrimaryKey();
		f = t.addField("VALUE", FieldType.TEXT);
		f = t.addField("TIME", FieldType.DATETIME);f.setNotNull();
		
		
		t = hc.getSQLManager().addTable("hyperconomy_objects");
		ArrayList<Field> compositeKey = new ArrayList<Field>();
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("ECONOMY", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("DISPLAY_NAME", FieldType.VARCHAR);f.setFieldSize(255);
		f = t.addField("ALIASES", FieldType.VARCHAR);f.setFieldSize(1000);
		f = t.addField("CATEGORIES", FieldType.TEXT);
		f = t.addField("TYPE", FieldType.TINYTEXT);
		f = t.addField("VALUE", FieldType.DOUBLE);
		f = t.addField("STATIC", FieldType.TINYTEXT);
		f = t.addField("STATICPRICE", FieldType.DOUBLE);
		f = t.addField("STOCK", FieldType.DOUBLE);
		f = t.addField("MEDIAN", FieldType.DOUBLE);
		f = t.addField("INITIATION", FieldType.TINYTEXT);
		f = t.addField("STARTPRICE", FieldType.DOUBLE);
		f = t.addField("CEILING", FieldType.DOUBLE);
		f = t.addField("FLOOR", FieldType.DOUBLE);
		f = t.addField("MAXSTOCK", FieldType.DOUBLE);f.setNotNull();f.setDefault("1000000");
		f = t.addField("COMPONENTS", FieldType.VARCHAR);f.setFieldSize(1000);f.setNotNull();f.setDefault("");
		f = t.addField("DATA", FieldType.TEXT);
		f = t.addField("VERSION", FieldType.DOUBLE);f.setNotNull();f.setDefault("1");
		t.setCompositeKey(compositeKey);
		
		
		t = hc.getSQLManager().addTable("hyperconomy_players");
		f = t.addField("ID", FieldType.INTEGER);f.setNotNull();f.setPrimaryKey();f.setAutoIncrement();
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(255);f.setUnique();
		f = t.addField("UUID", FieldType.VARCHAR);f.setFieldSize(255);f.setUnique();
		f = t.addField("ECONOMY", FieldType.TINYTEXT);
		f = t.addField("BALANCE", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("X", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("Y", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("Z", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("WORLD", FieldType.TINYTEXT);f.setNotNull();
		f = t.addField("HASH", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();f.setDefault("");
		f = t.addField("SALT", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();f.setDefault("");
		
		
		t = hc.getSQLManager().addTable("hyperconomy_log");
		f = t.addField("ID", FieldType.INTEGER);f.setNotNull();f.setPrimaryKey();f.setAutoIncrement();
		f = t.addField("TIME", FieldType.DATETIME);
		f = t.addField("CUSTOMER", FieldType.TINYTEXT);
		f = t.addField("ACTION", FieldType.TINYTEXT);
		f = t.addField("OBJECT", FieldType.TINYTEXT);
		f = t.addField("AMOUNT", FieldType.DOUBLE);
		f = t.addField("MONEY", FieldType.DOUBLE);
		f = t.addField("TAX", FieldType.DOUBLE);
		f = t.addField("STORE", FieldType.TINYTEXT);
		f = t.addField("TYPE", FieldType.TINYTEXT);
		
		
		t = hc.getSQLManager().addTable("hyperconomy_history");
		f = t.addField("ID", FieldType.INTEGER);f.setNotNull();f.setPrimaryKey();f.setAutoIncrement();
		f = t.addField("OBJECT", FieldType.TINYTEXT);
		f = t.addField("ECONOMY", FieldType.TINYTEXT);
		f = t.addField("TIME", FieldType.DATETIME);
		f = t.addField("PRICE", FieldType.DOUBLE);
		

		t = hc.getSQLManager().addTable("hyperconomy_audit_log");
		f = t.addField("ID", FieldType.INTEGER);f.setNotNull();f.setPrimaryKey();f.setAutoIncrement();
		f = t.addField("TIME", FieldType.DATETIME);f.setNotNull();
		f = t.addField("ACCOUNT", FieldType.TINYTEXT);f.setNotNull();
		f = t.addField("ACTION", FieldType.TINYTEXT);f.setNotNull();
		f = t.addField("AMOUNT", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("ECONOMY", FieldType.TINYTEXT);f.setNotNull();
		

		t = hc.getSQLManager().addTable("hyperconomy_shop_objects");
		compositeKey = new ArrayList<Field>();
		f = t.addField("SHOP", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("HYPEROBJECT", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("QUANTITY", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("SELL_PRICE", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("BUY_PRICE", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("MAX_STOCK", FieldType.INTEGER);f.setNotNull();f.setDefault("1000000");
		f = t.addField("STATUS", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		t.setCompositeKey(compositeKey);
		
		
		t = hc.getSQLManager().addTable("hyperconomy_frame_shops");
		f = t.addField("ID", FieldType.INTEGER);f.setNotNull();f.setPrimaryKey();f.setAutoIncrement();
		f = t.addField("HYPEROBJECT", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("ECONOMY", FieldType.TINYTEXT);
		f = t.addField("SHOP", FieldType.VARCHAR);f.setFieldSize(255);
		f = t.addField("TRADE_AMOUNT", FieldType.INTEGER);f.setNotNull();
		f = t.addField("X", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("Y", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("Z", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("WORLD", FieldType.TINYTEXT);f.setNotNull();
		

		t = hc.getSQLManager().addTable("hyperconomy_banks");
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setPrimaryKey();
		f = t.addField("BALANCE", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("OWNERS", FieldType.VARCHAR);f.setFieldSize(255);
		f = t.addField("MEMBERS", FieldType.VARCHAR);f.setFieldSize(255);
		
		
		t = hc.getSQLManager().addTable("hyperconomy_shops");
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setPrimaryKey();
		f = t.addField("TYPE", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("ECONOMY", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("OWNER", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("WORLD", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("USE_ECONOMY_STOCK", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setDefault("1");
		f = t.addField("MESSAGE", FieldType.TEXT);f.setNotNull();
		f = t.addField("BANNED_OBJECTS", FieldType.TEXT);f.setNotNull();
		f = t.addField("ALLOWED_PLAYERS", FieldType.TEXT);f.setNotNull();
		f = t.addField("P1X", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("P1Y", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("P1Z", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("P2X", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("P2Y", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("P2Z", FieldType.DOUBLE);f.setNotNull();
		
		
		t = hc.getSQLManager().addTable("hyperconomy_info_signs");
		compositeKey = new ArrayList<Field>();
		f = t.addField("WORLD", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("X", FieldType.INTEGER);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("Y", FieldType.INTEGER);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("Z", FieldType.INTEGER);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("HYPEROBJECT", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("TYPE", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("MULTIPLIER", FieldType.INTEGER);f.setNotNull();
		f = t.addField("ECONOMY", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("ECLASS", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		t.setCompositeKey(compositeKey);
		

		t = hc.getSQLManager().addTable("hyperconomy_item_displays");
		compositeKey = new ArrayList<Field>();
		f = t.addField("WORLD", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("X", FieldType.DOUBLE);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("Y", FieldType.DOUBLE);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("Z", FieldType.DOUBLE);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("HYPEROBJECT", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		t.setCompositeKey(compositeKey);

		t = hc.getSQLManager().addTable("hyperconomy_economies");
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setPrimaryKey();
		f = t.addField("HYPERACCOUNT", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		
		
		t = hc.getSQLManager().addTable("hyperconomy_time_effects");
		compositeKey = new ArrayList<Field>();
		f = t.addField("TYPE", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("ECONOMY", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("VALUE", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("SECONDS", FieldType.INTEGER);f.setNotNull();f.setDefault("0");
		f = t.addField("INCREMENT", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("TIME_REMAINING", FieldType.INTEGER);f.setNotNull();f.setDefault("0");
		t.setCompositeKey(compositeKey);
		
		
		t = hc.getSQLManager().addTable("hyperconomy_chest_shops");
		f = t.addField("ID", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();f.setPrimaryKey();
		f = t.addField("WORLD", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
		f = t.addField("X", FieldType.INTEGER);f.setNotNull();
		f = t.addField("Y", FieldType.INTEGER);f.setNotNull();
		f = t.addField("Z", FieldType.INTEGER);f.setNotNull();
		f = t.addField("OWNER", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("PRICE_INCREMENT", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();

		
		t = hc.getSQLManager().addTable("hyperconomy_chest_shop_items");
		compositeKey = new ArrayList<Field>();
		f = t.addField("CHEST_ID", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("SLOT", FieldType.INTEGER);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("DATA", FieldType.TEXT);f.setNotNull();
		f = t.addField("BUY_PRICE", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("SELL_PRICE", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("TYPE", FieldType.VARCHAR);f.setFieldSize(40);f.setNotNull();
		t.setCompositeKey(compositeKey);
		hc.getSQLManager().saveTables();
		

		sw.addToQueue("DELETE FROM hyperconomy_settings");
		sw.addToQueue("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('version', '"+requiredDbVersion+"', NOW() )");
		currentDbVersion = requiredDbVersion;
		sw.writeSyncQueue();
		sw.writeSync(writeState);
	}
	
	
	
}
