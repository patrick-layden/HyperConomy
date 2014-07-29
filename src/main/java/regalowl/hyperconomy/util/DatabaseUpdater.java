package regalowl.hyperconomy.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import regalowl.databukkit.file.FileTools;
import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.SyncSQLWrite;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.display.SignType;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableItemStack;

public class DatabaseUpdater {

	private HyperConomy hc;
	private ArrayList<String> tables = new ArrayList<String>();
	public final double version = 1.34;
	
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


	

	@SuppressWarnings("deprecation")
	public void updateTables(QueryResult qr) {
		hc = HyperConomy.hc;
		SyncSQLWrite sw = hc.getDataBukkit().getSyncSQLWrite();
		if (qr.next()) {
			double version = Double.parseDouble(qr.getString("VALUE"));
			if (version < 1.24) {
				//update fixes frameshop table
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.24");
				sw.queue("DROP TABLE hyperconomy_frame_shops");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_frame_shops (ID INTEGER NOT NULL PRIMARY KEY, HYPEROBJECT VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, SHOP VARCHAR(255), TRADE_AMOUNT INTEGER NOT NULL, X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL)");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.24' WHERE SETTING = 'version'");
			}
			if (version < 1.25) {
				//update adds buy/sell prices to player shops and a max stock setting
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.25");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects_temp (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, SELL_PRICE DOUBLE NOT NULL, BUY_PRICE DOUBLE NOT NULL, MAX_STOCK INTEGER NOT NULL DEFAULT '1000000', STATUS VARCHAR(255) NOT NULL)");
				sw.writeQueue();
				sw.convertQueue("INSERT INTO hyperconomy_shop_objects_temp (SHOP,HYPEROBJECT,QUANTITY,SELL_PRICE,BUY_PRICE,STATUS) SELECT SHOP,HYPEROBJECT,QUANTITY,PRICE,PRICE,STATUS FROM hyperconomy_shop_objects");
				sw.queue("DROP TABLE hyperconomy_shop_objects");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, SELL_PRICE DOUBLE NOT NULL, BUY_PRICE DOUBLE NOT NULL, MAX_STOCK INTEGER NOT NULL DEFAULT '1000000', STATUS VARCHAR(255) NOT NULL)");
				sw.writeQueue();
				sw.convertQueue("INSERT INTO hyperconomy_shop_objects (SHOP,HYPEROBJECT,QUANTITY,SELL_PRICE,BUY_PRICE,MAX_STOCK,STATUS) SELECT SHOP,HYPEROBJECT,QUANTITY,SELL_PRICE,BUY_PRICE,MAX_STOCK,STATUS FROM hyperconomy_shop_objects_temp");
				sw.queue("DROP TABLE hyperconomy_shop_objects_temp");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.25' WHERE SETTING = 'version'");
			}
			if (version < 1.26) {
				//adds banks
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.26");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_banks (NAME VARCHAR(255) NOT NULL PRIMARY KEY, BALANCE DOUBLE NOT NULL DEFAULT '0', OWNERS VARCHAR(255), MEMBERS VARCHAR(255))");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.26' WHERE SETTING = 'version'");
			}
			if (version < 1.27) {
				//moves shops, infosigns, economies, and item displays to the database
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.27");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_shops (NAME VARCHAR(255) NOT NULL PRIMARY KEY, TYPE VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, OWNER VARCHAR(255) NOT NULL, WORLD VARCHAR(255) NOT NULL, MESSAGE TEXT NOT NULL, BANNED_OBJECTS TEXT NOT NULL, ALLOWED_PLAYERS TEXT NOT NULL, P1X DOUBLE NOT NULL, P1Y DOUBLE NOT NULL, P1Z DOUBLE NOT NULL, P2X DOUBLE NOT NULL, P2Y DOUBLE NOT NULL, P2Z DOUBLE NOT NULL)");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_info_signs (WORLD VARCHAR(255) NOT NULL, X INTEGER NOT NULL, Y INTEGER NOT NULL, Z INTEGER NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, TYPE VARCHAR(255) NOT NULL, MULTIPLIER INTEGER NOT NULL, ECONOMY VARCHAR(255) NOT NULL, ECLASS VARCHAR(255) NOT NULL, PRIMARY KEY(WORLD, X, Y, Z))");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_item_displays (WORLD VARCHAR(255) NOT NULL, X DOUBLE NOT NULL, Y DOUBLE NOT NULL, Z DOUBLE NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, PRIMARY KEY(WORLD, X, Y, Z))");	
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_economies (NAME VARCHAR(255) NOT NULL PRIMARY KEY, HYPERACCOUNT VARCHAR(255) NOT NULL)");
				

				hc.gYH().registerFileConfiguration("shops");
				FileConfiguration sh = hc.gYH().gFC("shops");
				//LanguageFile L = hc.getLanguageFile();
				Iterator<String> it = sh.getKeys(false).iterator();
				String defaultServerShopAccount = hc.getConf().getString("shop.default-server-shop-account");
				while (it.hasNext()) {
					HashMap<String,String> values = new HashMap<String,String>();
					Object element = it.next();
					String name = element.toString(); 
					String owner = sh.getString(name + ".owner");
					if (owner == null || owner == "") {
						owner = "hyperconomy";
					}
					String type = "player";
					if (owner.equalsIgnoreCase(defaultServerShopAccount)) {
						type = "server";
					}
					values.put("NAME", name);
					values.put("TYPE", type);
					String economy = sh.getString(name + ".economy");
					if (economy == null || economy == "") {
						economy = "default";
					}
					values.put("ECONOMY", economy);
					values.put("OWNER", owner);
					String world = sh.getString(name + ".world");
					if (world == null || world == "") {
						world = "world";
					}
					values.put("WORLD", world);
					String message1 = sh.getString(name + ".shopmessage1");
					if (message1 == null || message1 == "") {
						message1 = "&aWelcome to "+name+"";
					}
					message1 = message1.replace("%n", name);
					String message2 = sh.getString(name + ".shopmessage2");
					if (message2 == null || message2 == "") {
						message2 = "&9Type &b/hc &9for help.";
					}
					message2 = message2.replace("%n", name);
					String message = "&8--------------------%n"+message1+"%n"+message2+"%n&8--------------------";
					values.put("MESSAGE", message);
					values.put("P1X", sh.getString(name + ".p1.x"));
					values.put("P1Y", sh.getString(name + ".p1.y"));
					values.put("P1Z", sh.getString(name + ".p1.z"));
					values.put("P2X", sh.getString(name + ".p2.x"));
					values.put("P2Y", sh.getString(name + ".p2.y"));
					values.put("P2Z", sh.getString(name + ".p2.z"));
					String banned = sh.getString(name + ".unavailable");
					if (banned == null) {
						banned = "";
					}
					values.put("BANNED_OBJECTS", banned);
					String allowed = sh.getString(name + ".allowed");
					if (allowed == null) {
						allowed = "";
					}
					values.put("ALLOWED_PLAYERS", allowed);
					sw.queueInsert("hyperconomy_shops", values);
				}
				hc.gYH().unRegisterFileConfiguration("shops");
				hc.gYH().registerFileConfiguration("signs");
				FileConfiguration sns = hc.gYH().gFC("signs");
				Iterator<String> iterat = sns.getKeys(false).iterator();
				while (iterat.hasNext()) {
					String signKey = iterat.next().toString();
					String key = signKey;
					String world = signKey.substring(0, signKey.indexOf("|"));
					signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
					int x = Integer.parseInt(signKey.substring(0, signKey.indexOf("|")));
					signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
					int y = Integer.parseInt(signKey.substring(0, signKey.indexOf("|")));
					signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
					int z = Integer.parseInt(signKey);
					
					String name = sns.getString(key + ".itemname");
					SignType type = SignType.fromString(sns.getString(key + ".type"));
					String economy = sns.getString(key + ".economy");
					EnchantmentClass enchantClass = EnchantmentClass.fromString(sns.getString(key + ".enchantclass"));
					int multiplier = sns.getInt(key + ".multiplier");
					if (multiplier < 1) {
						multiplier = 1;
					}
					HashMap<String,String> values = new HashMap<String,String>();
					values.put("WORLD", world);
					values.put("X", x+"");
					values.put("Y", y+"");
					values.put("Z", z+"");
					values.put("HYPEROBJECT", name);
					values.put("TYPE", type.toString());
					values.put("MULTIPLIER", multiplier+"");
					values.put("ECONOMY", economy);
					values.put("ECLASS", enchantClass.toString());
					sw.queueInsert("hyperconomy_info_signs", values);
				}
				hc.gYH().unRegisterFileConfiguration("signs");
				hc.gYH().registerFileConfiguration("displays");
				FileConfiguration displays = hc.gYH().gFC("displays");
				iterat = displays.getKeys(false).iterator();
				while (iterat.hasNext()) {
					String key = iterat.next().toString();
					String name = displays.getString(key + ".name");
					String world = displays.getString(key + ".world");
					String x = displays.getString(key + ".x");
					String y = displays.getString(key + ".y");
					String z = displays.getString(key + ".z");
					HashMap<String,String> values = new HashMap<String,String>();
					values.put("WORLD", world);
					values.put("X", x);
					values.put("Y", y);
					values.put("Z", z);
					values.put("HYPEROBJECT", name);
					sw.queueInsert("hyperconomy_item_displays", values);
				}
				hc.gYH().unRegisterFileConfiguration("displays");
				sw.writeQueue();
				ArrayList<String> econs = hc.getSQLRead().getStringList("hyperconomy_objects", "DISTINCT ECONOMY", null);
				for (String econ:econs) {
					HashMap<String,String> values = new HashMap<String,String>();
					values.put("NAME", econ);
					values.put("HYPERACCOUNT", "hyperconomy");
					sw.queueInsert("hyperconomy_economies", values);
				}

				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.27' WHERE SETTING = 'version'");
			}
			if (version < 1.28) {
				//removes id increment field and adds SHOP, HYPEROBJECT primary key to shop objects table to guarantee no duplicates
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.28");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects_temp (SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, SELL_PRICE DOUBLE NOT NULL, BUY_PRICE DOUBLE NOT NULL, MAX_STOCK INTEGER NOT NULL DEFAULT '1000000', STATUS VARCHAR(255) NOT NULL, PRIMARY KEY(SHOP, HYPEROBJECT))");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_shop_objects_temp (SHOP, HYPEROBJECT, QUANTITY, SELL_PRICE, BUY_PRICE, MAX_STOCK, STATUS) SELECT SHOP, HYPEROBJECT, QUANTITY, SELL_PRICE, BUY_PRICE, MAX_STOCK, STATUS FROM hyperconomy_shop_objects");
				sw.queue("DROP TABLE hyperconomy_shop_objects");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, SELL_PRICE DOUBLE NOT NULL, BUY_PRICE DOUBLE NOT NULL, MAX_STOCK INTEGER NOT NULL DEFAULT '1000000', STATUS VARCHAR(255) NOT NULL, PRIMARY KEY(SHOP, HYPEROBJECT))");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, SELL_PRICE, BUY_PRICE, MAX_STOCK, STATUS) SELECT SHOP, HYPEROBJECT, QUANTITY, SELL_PRICE, BUY_PRICE, MAX_STOCK, STATUS FROM hyperconomy_shop_objects_temp");
				sw.queue("DROP TABLE hyperconomy_shop_objects_temp");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.28' WHERE SETTING = 'version'");
			}
			if (version < 1.29) {
				//removes item data fields and adds single item data text field to hyperobjects table; imports composites.yml to database
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.29");
				sw.convertQueue(hc.getSQLWrite().longText("CREATE TABLE IF NOT EXISTS hyperconomy_composites (NAME VARCHAR(255) NOT NULL PRIMARY KEY, DISPLAY_NAME VARCHAR(255), ALIASES VARCHAR(1000), COMPONENTS VARCHAR(1000), TYPE TINYTEXT, DATA TEXT)"));
				hc.gYH().registerFileConfiguration("composites");
				FileConfiguration cps = hc.gYH().gFC("composites");
				Iterator<String> iterat = cps.getKeys(false).iterator();
				while (iterat.hasNext()) {
					String name = iterat.next().toString();
					String type = cps.getString(name + ".information.type");
					String material = cps.getString(name + ".information.material");
					int data = cps.getInt(name + ".information.data");
					Material m = Material.matchMaterial(material);
					ItemStack stack = new ItemStack(m, 1);
					MaterialData md = stack.getData();
					md.setData((byte) data);
					stack.setData(md);
					stack.setDurability((short) data);
					SerializableItemStack sis = new SerializableItemStack(stack);
					String base64 = sis.serialize();
					String displayName = cps.getString(name + ".name.display");
					String aliases = cps.getString(name + ".name.aliases");
					String components = cps.getString(name + ".components");
					HashMap<String,String> values = new HashMap<String,String>();
					values.put("NAME", name);
					values.put("DISPLAY_NAME", displayName);
					values.put("ALIASES", aliases);
					values.put("COMPONENTS", components);
					values.put("TYPE", type);
					values.put("DATA", base64);
					sw.queueInsert("hyperconomy_composites", values);
				}
				hc.gYH().unRegisterFileConfiguration("composites");
				
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_objects_temp (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, DISPLAY_NAME VARCHAR(255), ALIASES VARCHAR(1000), TYPE TINYTEXT, MATERIAL TINYTEXT, DATA INTEGER, DURABILITY INTEGER, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_objects_temp (NAME, ECONOMY, DISPLAY_NAME, ALIASES, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK) SELECT NAME, ECONOMY, DISPLAY_NAME, ALIASES, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK FROM hyperconomy_objects");
				sw.queue("DROP TABLE hyperconomy_objects");
				sw.convertQueue(hc.getSQLWrite().longText("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, DISPLAY_NAME VARCHAR(255), ALIASES VARCHAR(1000), TYPE TINYTEXT, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', DATA TEXT, PRIMARY KEY (NAME, ECONOMY))"));
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_objects (NAME, ECONOMY, DISPLAY_NAME, ALIASES, TYPE, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK) SELECT NAME, ECONOMY, DISPLAY_NAME, ALIASES, TYPE, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK FROM hyperconomy_objects_temp");
				sw.writeQueue();
				QueryResult result = hc.getSQLRead().select("SELECT * FROM hyperconomy_objects_temp");
				while (result.next()) {
					String name = result.getString("NAME");
					String economy = result.getString("ECONOMY");
					HyperObjectType type = HyperObjectType.fromString(result.getString("TYPE"));
					int data = result.getInt("DATA");
					int durability = result.getInt("DURABILITY");
					String newData = "";
					if (type == HyperObjectType.ITEM) {
						Material m = Material.matchMaterial(result.getString("MATERIAL"));
						ItemStack stack = new ItemStack(m, 1);
						MaterialData md = stack.getData();
						md.setData((byte) data);
						stack.setData(md);
						stack.setDurability((short) durability);
						SerializableItemStack sis = new SerializableItemStack(stack);
						newData = sis.serialize();
					} else if (type == HyperObjectType.ENCHANTMENT) {
						int l = name.length();
						String lvl = name.substring(l - 1, l);
						int level = Integer.parseInt(lvl);
						SerializableEnchantment se = new SerializableEnchantment(Enchantment.getByName(result.getString("MATERIAL")), level);
						newData = se.serialize();
					}
					sw.queue("UPDATE hyperconomy_objects SET DATA = '"+newData+"' WHERE NAME = '"+name+"' AND ECONOMY = '"+economy+"'");
				}
				result.close();
				sw.queue("DROP TABLE hyperconomy_objects_temp");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.29' WHERE SETTING = 'version'");
			}
			if (version < 1.30) {
				//removes unnecessary fields from composites table
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.30");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_composites_temp (NAME VARCHAR(255) NOT NULL PRIMARY KEY, DISPLAY_NAME VARCHAR(255), COMPONENTS VARCHAR(1000))");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_composites_temp (NAME, DISPLAY_NAME, COMPONENTS) SELECT NAME, DISPLAY_NAME, COMPONENTS FROM hyperconomy_composites");
				sw.queue("DROP TABLE hyperconomy_composites");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_composites (NAME VARCHAR(255) NOT NULL PRIMARY KEY, DISPLAY_NAME VARCHAR(255), COMPONENTS VARCHAR(1000))");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_composites (NAME, DISPLAY_NAME, COMPONENTS) SELECT NAME, DISPLAY_NAME, COMPONENTS FROM hyperconomy_composites_temp");
				sw.queue("DROP TABLE hyperconomy_composites_temp");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.30' WHERE SETTING = 'version'");
			}
			if (version < 1.31) {
				//removes unnecessary YML files
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.31");
				FileTools ft = hc.getFileTools();
				String folderPath = hc.getFolderPath();
				ArrayList<String> removeFiles = new ArrayList<String>();
				removeFiles.add("composites.yml");
				removeFiles.add("displays.yml");
				removeFiles.add("objects.yml");
				removeFiles.add("shops.yml");
				removeFiles.add("signs.yml");
				removeFiles.add("temp.yml");
				for (String file:removeFiles) {
					if (ft.fileExists(folderPath + File.separator + file)) {
						ft.deleteFile(folderPath + File.separator + file);
					}
				}
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.31' WHERE SETTING = 'version'");
			}
			if (version < 1.32) {
				//adds uuid support
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.32");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_players_temp (NAME VARCHAR(255) NOT NULL PRIMARY KEY, UUID VARCHAR(255) UNIQUE, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_players_temp (NAME, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT) SELECT PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT FROM hyperconomy_players");
				sw.queue("DROP TABLE hyperconomy_players");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_players (NAME VARCHAR(255) NOT NULL PRIMARY KEY, UUID VARCHAR(255) UNIQUE, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_players (NAME, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT) SELECT NAME, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT FROM hyperconomy_players_temp");
				sw.queue("DROP TABLE hyperconomy_players_temp");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.32' WHERE SETTING = 'version'");
			}
			if (version < 1.33) {
				//remove unique requirement from uuid
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.33");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_players_temp (NAME VARCHAR(255) NOT NULL PRIMARY KEY, UUID VARCHAR(255), ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_players_temp (NAME, UUID, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT) SELECT NAME, UUID, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT FROM hyperconomy_players");
				sw.queue("DROP TABLE hyperconomy_players");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_players (NAME VARCHAR(255) NOT NULL PRIMARY KEY, UUID VARCHAR(255), ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_players (NAME, UUID, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT) SELECT NAME, UUID, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT FROM hyperconomy_players_temp");
				sw.queue("DROP TABLE hyperconomy_players_temp");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.33' WHERE SETTING = 'version'");
			}
			if (version < 1.34) {
				//allow player name to be null in hyperplayer table and add new autoincrement primary key
				hc.getDebugMode().ayncDebugConsoleMessage("Updating database to version 1.34");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_players_temp (ID INTEGER NOT NULL PRIMARY KEY, NAME VARCHAR(255) UNIQUE, UUID VARCHAR(255) UNIQUE, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_players_temp (NAME, UUID, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT) SELECT NAME, UUID, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT FROM hyperconomy_players");
				sw.queue("DROP TABLE hyperconomy_players");
				sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_players (ID INTEGER NOT NULL PRIMARY KEY, NAME VARCHAR(255) UNIQUE, UUID VARCHAR(255) UNIQUE, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
				sw.writeQueue();
				sw.queue("INSERT INTO hyperconomy_players (NAME, UUID, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT) SELECT NAME, UUID, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT FROM hyperconomy_players_temp");
				sw.queue("DROP TABLE hyperconomy_players_temp");
				sw.queue("UPDATE hyperconomy_settings SET VALUE = '1.34' WHERE SETTING = 'version'");
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
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_settings (SETTING VARCHAR(100) NOT NULL PRIMARY KEY, VALUE TEXT, TIME DATETIME NOT NULL)");
		sw.convertQueue(hc.getSQLWrite().longText("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(100) NOT NULL, ECONOMY VARCHAR(100) NOT NULL, DISPLAY_NAME VARCHAR(255), ALIASES VARCHAR(1000), TYPE TINYTEXT, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', DATA TEXT, PRIMARY KEY (NAME, ECONOMY))"));
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_players (ID INTEGER NOT NULL PRIMARY KEY, NAME VARCHAR(255) UNIQUE, UUID VARCHAR(255) UNIQUE, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT)");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_history (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE)");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_audit_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME NOT NULL, ACCOUNT TINYTEXT NOT NULL, ACTION TINYTEXT NOT NULL, AMOUNT DOUBLE NOT NULL, ECONOMY TINYTEXT NOT NULL)");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (SHOP VARCHAR(100) NOT NULL, HYPEROBJECT VARCHAR(100) NOT NULL, QUANTITY DOUBLE NOT NULL, SELL_PRICE DOUBLE NOT NULL, BUY_PRICE DOUBLE NOT NULL, MAX_STOCK INTEGER NOT NULL DEFAULT '1000000', STATUS VARCHAR(255) NOT NULL, PRIMARY KEY(SHOP, HYPEROBJECT))");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_frame_shops (ID INTEGER NOT NULL PRIMARY KEY, HYPEROBJECT VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, SHOP VARCHAR(255), TRADE_AMOUNT INTEGER NOT NULL, X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL)");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_banks (NAME VARCHAR(100) NOT NULL PRIMARY KEY, BALANCE DOUBLE NOT NULL DEFAULT '0', OWNERS VARCHAR(255), MEMBERS VARCHAR(255))");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_shops (NAME VARCHAR(100) NOT NULL PRIMARY KEY, TYPE VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, OWNER VARCHAR(255) NOT NULL, WORLD VARCHAR(255) NOT NULL, MESSAGE TEXT NOT NULL, BANNED_OBJECTS TEXT NOT NULL, ALLOWED_PLAYERS TEXT NOT NULL, P1X DOUBLE NOT NULL, P1Y DOUBLE NOT NULL, P1Z DOUBLE NOT NULL, P2X DOUBLE NOT NULL, P2Y DOUBLE NOT NULL, P2Z DOUBLE NOT NULL)");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_info_signs (WORLD VARCHAR(100) NOT NULL, X INTEGER NOT NULL, Y INTEGER NOT NULL, Z INTEGER NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, TYPE VARCHAR(255) NOT NULL, MULTIPLIER INTEGER NOT NULL, ECONOMY VARCHAR(255) NOT NULL, ECLASS VARCHAR(255) NOT NULL, PRIMARY KEY(WORLD, X, Y, Z))");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_item_displays (WORLD VARCHAR(100) NOT NULL, X DOUBLE NOT NULL, Y DOUBLE NOT NULL, Z DOUBLE NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, PRIMARY KEY(WORLD, X, Y, Z))");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_economies (NAME VARCHAR(100) NOT NULL PRIMARY KEY, HYPERACCOUNT VARCHAR(255) NOT NULL)");
		sw.convertQueue("CREATE TABLE IF NOT EXISTS hyperconomy_composites (NAME VARCHAR(100) NOT NULL PRIMARY KEY, DISPLAY_NAME VARCHAR(255), COMPONENTS VARCHAR(1000))");
		if (!copydatabase) {
			sw.convertQueue("DELETE FROM hyperconomy_settings");
			sw.convertQueue("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('version', '"+hc.getDataManager().getDatabaseUpdater().getVersion()+"', NOW() )");
		}
	}
	
	
	
}
