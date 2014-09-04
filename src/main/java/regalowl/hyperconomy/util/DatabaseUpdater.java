package regalowl.hyperconomy.util;

import java.util.ArrayList;

import regalowl.databukkit.sql.Field;
import regalowl.databukkit.sql.FieldType;
import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.SyncSQLWrite;
import regalowl.databukkit.sql.Table;
import regalowl.hyperconomy.HyperConomy;

public class DatabaseUpdater {

	private HyperConomy hc;
	private ArrayList<String> tables = new ArrayList<String>();
	public final double version = 1.35;
	
	public DatabaseUpdater() {
		hc = HyperConomy.hc;
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
		tables.add("composites");
	}
	
	
	public ArrayList<String> getTablesList() {
		return tables;
	}
	
	public double getVersion() {
		return version;
	}


	

	public void updateTables(QueryResult qr) {
		hc = HyperConomy.hc;
		SyncSQLWrite sw = hc.getDataBukkit().getSyncSQLWrite();
		if (qr.next()) {
			double version = Double.parseDouble(qr.getString("VALUE"));
			new LegacyDatabaseUpdates().applyLegacyUpdates(version, sw);
			if (version < 1.35) {
				//adds ability for player shops to behave like server shops
				Table t = hc.getDataBukkit().generateTable("hyperconomy_shops");
				Field f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setPrimaryKey();
				f = t.addField("TYPE", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
				f = t.addField("ECONOMY", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
				f = t.addField("OWNER", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
				f = t.addField("WORLD", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
				Field afterField = f;
				f = t.addField("MESSAGE", FieldType.TEXT);f.setNotNull();
				f = t.addField("BANNED_OBJECTS", FieldType.TEXT);f.setNotNull();
				f = t.addField("ALLOWED_PLAYERS", FieldType.TEXT);f.setNotNull();
				f = t.addField("P1X", FieldType.DOUBLE);f.setNotNull();
				f = t.addField("P1Y", FieldType.DOUBLE);f.setNotNull();
				f = t.addField("P1Z", FieldType.DOUBLE);f.setNotNull();
				f = t.addField("P2X", FieldType.DOUBLE);f.setNotNull();
				f = t.addField("P2Y", FieldType.DOUBLE);f.setNotNull();
				f = t.addField("P2Z", FieldType.DOUBLE);f.setNotNull();
				f = t.generateField("USE_ECONOMY_STOCK", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setDefault("1");
				t.addField(f, afterField);
				sw.queue("UPDATE hyperconomy_shops SET USE_ECONOMY_STOCK = '0' WHERE TYPE = 'player'");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.35' WHERE SETTING = 'version'");
			}
		} else {
			createTables(false);
		}
		sw.writeQueue();
	}

	public void createTables(boolean copydatabase) {
		hc.getDebugMode().ayncDebugConsoleMessage("Creating database tables.");
		SyncSQLWrite sw = HyperConomy.hc.getDataBukkit().getSyncSQLWrite();
		if (copydatabase) {
			for (String table:tables) {
				sw.convertQueue("DROP TABLE IF EXISTS hyperconomy_"+table);
			}
		}

		Table t = hc.getDataBukkit().addTable("hyperconomy_settings");
		Field f = t.addField("SETTING", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setPrimaryKey();
		f = t.addField("VALUE", FieldType.TEXT);
		f = t.addField("TIME", FieldType.DATETIME);f.setNotNull();
		
		
		t = hc.getDataBukkit().addTable("hyperconomy_objects");
		ArrayList<Field> compositeKey = new ArrayList<Field>();
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("ECONOMY", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();
		compositeKey.add(f);
		f = t.addField("DISPLAY_NAME", FieldType.VARCHAR);f.setFieldSize(255);
		f = t.addField("ALIASES", FieldType.VARCHAR);f.setFieldSize(1000);
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
		f = t.addField("DATA", FieldType.TEXT);
		t.setCompositeKey(compositeKey);
		
		
		t = hc.getDataBukkit().addTable("hyperconomy_players");
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
		
		
		t = hc.getDataBukkit().addTable("hyperconomy_log");
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
		
		
		t = hc.getDataBukkit().addTable("hyperconomy_history");
		f = t.addField("ID", FieldType.INTEGER);f.setNotNull();f.setPrimaryKey();f.setAutoIncrement();
		f = t.addField("OBJECT", FieldType.TINYTEXT);
		f = t.addField("ECONOMY", FieldType.TINYTEXT);
		f = t.addField("TIME", FieldType.DATETIME);
		f = t.addField("PRICE", FieldType.DOUBLE);
		

		t = hc.getDataBukkit().addTable("hyperconomy_audit_log");
		f = t.addField("ID", FieldType.INTEGER);f.setNotNull();f.setPrimaryKey();f.setAutoIncrement();
		f = t.addField("TIME", FieldType.DATETIME);f.setNotNull();
		f = t.addField("ACCOUNT", FieldType.TINYTEXT);f.setNotNull();
		f = t.addField("ACTION", FieldType.TINYTEXT);f.setNotNull();
		f = t.addField("AMOUNT", FieldType.DOUBLE);f.setNotNull();
		f = t.addField("ECONOMY", FieldType.TINYTEXT);f.setNotNull();
		

		t = hc.getDataBukkit().addTable("hyperconomy_shop_objects");
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
		
		
		t = hc.getDataBukkit().addTable("hyperconomy_frame_shops");
		f = t.addField("ID", FieldType.INTEGER);f.setNotNull();f.setPrimaryKey();f.setAutoIncrement();
		f = t.addField("HYPEROBJECT", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		f = t.addField("ECONOMY", FieldType.TINYTEXT);
		f = t.addField("SHOP", FieldType.VARCHAR);f.setFieldSize(255);
		f = t.addField("TRADE_AMOUNT", FieldType.INTEGER);f.setNotNull();
		f = t.addField("X", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("Y", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("Z", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("WORLD", FieldType.TINYTEXT);f.setNotNull();
		

		t = hc.getDataBukkit().addTable("hyperconomy_banks");
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setPrimaryKey();
		f = t.addField("BALANCE", FieldType.DOUBLE);f.setNotNull();f.setDefault("0");
		f = t.addField("OWNERS", FieldType.VARCHAR);f.setFieldSize(255);
		f = t.addField("MEMBERS", FieldType.VARCHAR);f.setFieldSize(255);
		
		
		t = hc.getDataBukkit().addTable("hyperconomy_shops");
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
		
		
		t = hc.getDataBukkit().addTable("hyperconomy_info_signs");
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
		

		t = hc.getDataBukkit().addTable("hyperconomy_item_displays");
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
		

		t = hc.getDataBukkit().addTable("hyperconomy_economies");
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setPrimaryKey();
		f = t.addField("HYPERACCOUNT", FieldType.VARCHAR);f.setFieldSize(255);f.setNotNull();
		

		t = hc.getDataBukkit().addTable("hyperconomy_composites");
		f = t.addField("NAME", FieldType.VARCHAR);f.setFieldSize(100);f.setNotNull();f.setPrimaryKey();
		f = t.addField("DISPLAY_NAME", FieldType.VARCHAR);f.setFieldSize(255);
		f = t.addField("COMPONENTS", FieldType.VARCHAR);f.setFieldSize(1000);
		
		HyperConomy.hc.getDataBukkit().saveTables();
		
		if (!copydatabase) {
			sw.convertQueue("DELETE FROM hyperconomy_settings");
			sw.convertQueue("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('version', '"+hc.getDataManager().getDatabaseUpdater().getVersion()+"', NOW() )");
			sw.writeQueue();
		}
	}
	
	
	
}
