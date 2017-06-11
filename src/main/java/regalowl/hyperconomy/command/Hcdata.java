package regalowl.hyperconomy.command;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.file.FileTools;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.util.Backup;


public class Hcdata extends BaseCommand implements HyperCommand {
	
	private ArrayList<String> tables;
	private String table;
	private FileTools ft;
	private String folderPath;
	
	public Hcdata(HyperConomy hc) {
		super(hc, false);
		tables = hc.getDataManager().getTablesList();
	}


	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		ft = hc.getFileTools();
		if (args.length == 0) {
			data.addResponse(L.get("HCDATA_INVALID"));
			return data;
		}
		folderPath = hc.getFolderPath();
		if (args[0].equalsIgnoreCase("exportcsv")) {
			try {
				ft.makeFolder(folderPath + File.separator + "import_export");
				table = args[1];
				if (table.equalsIgnoreCase("all")) {
					new Thread(new Runnable() {
						public void run() {
							for (String table:tables) {
								QueryResult data = hc.getSQLRead().select("SELECT * FROM hyperconomy_" + table);
								String path = folderPath + File.separator + "import_export" + File.separator + table + ".csv";
								hc.getFileTools().writeCSV(data, path);
							}
						}
					}).start();
					data.addResponse(L.get("CSVS_CREATED"));
					return data;
				}
				if (!tables.contains(table)) {
					data.addResponse(L.get("TABLE_NOT_EXIST"));
					return data;
				}
				new Thread(new Runnable() {
					public void run() {
						QueryResult data = hc.getSQLRead().select("SELECT * FROM hyperconomy_" + table);
						ft.makeFolder(folderPath + File.separator + "import_export");
						String path = folderPath + File.separator + "import_export" + File.separator + table + ".csv";
						hc.getFileTools().writeCSV(data, path);
					}
				}).start();
				data.addResponse(L.get("CSV_CREATED"));
			} catch (Exception e) {
				data.addResponse(L.get("HCDATA_EXPORTCSV_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("importcsv")) {
			try {
				ft.makeFolder(folderPath + File.separator + "import_export");
				table = args[1];
				if (table.equalsIgnoreCase("all")) {
					for (String table:tables) {
						String path = folderPath + File.separator + "import_export" + File.separator + table + ".csv";
						if (!ft.fileExists(path)) {continue;}
						QueryResult qr = hc.getFileTools().readCSV(path);
						ArrayList<String> columns = qr.getColumnNames();
						hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_" + table);
						while (qr.next()) {
							HashMap<String, String> values = new HashMap<String, String>();
							for (String column : columns) {
								values.put(column, qr.getString(column));
							}
							hc.getSQLWrite().performInsert("hyperconomy_" + table, values);
						}
					}
					data.addResponse(L.get("CSVS_IMPORTED"));
					hc.restart();
					return data;
				}
				if (!tables.contains(table)) {
					data.addResponse(L.get("TABLE_NOT_EXIST"));
					return data;
				}
				
				String path = folderPath + File.separator + "import_export" + File.separator + table + ".csv";
				if (!ft.fileExists(path)) {
					data.addResponse(L.get("IMPORT_FILE_NOT_EXIST"));
					return data;
				}
				QueryResult qr = hc.getFileTools().readCSV(path);
				ArrayList<String> columns = qr.getColumnNames();
				hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_" + table);
				while (qr.next()) {
					HashMap<String, String> values = new HashMap<String, String>();
					for (String column : columns) {
						values.put(column, qr.getString(column));
					}
					hc.getSQLWrite().performInsert("hyperconomy_" + table, values);
				}
				data.addResponse(L.get("CSV_IMPORTED"));
				hc.restart();
			} catch (Exception e) {
				data.addResponse(L.get("HCDATA_IMPORTCSV_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("importmissing")) { 
			try {
				String economy = "default";
				if (args.length > 1) {
					economy = args[1];
				}
				if (dm.economyExists(economy)) {
					if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
					ArrayList<String> added = dm.loadNewItems(economy);
					data.addResponse("&6" + added.toString() + " " + L.get("LOADED_INTO_ECONOMY"));
				} else {
					data.addResponse(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				data.addResponse(L.get("HCDATA_IMPORTMISSING_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("setdefaultprices")) { 
			try {
				String economy = "default";
				if (args.length > 1) economy = args[1];
				if (dm.economyExists(economy)) {
					hc.getDataManager().setDefaultPrices(economy);
					data.addResponse(L.get("PRICES_IMPORTED"));
				} else {
					data.addResponse(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
				data.addResponse(L.get("HCDATA_SETDEFAULTPRICES_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("updateitems")) { 
			try {
				String economy = "default";
				if (args.length > 1) economy = args[1];
				if (dm.economyExists(economy)) {
					hc.getDataManager().updateItems(economy);
					data.addResponse(L.get("ITEMS_UPDATED"));
				} else {
					data.addResponse(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
				data.addResponse(L.get("HCDATA_UPDATEITEMS_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("autosetcompositeprices")) { 
			try {
				String economy = "default";
				if (args.length > 1) economy = args[1];
				if (dm.economyExists(economy)) {
					if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
					for (TradeObject to:dm.getEconomy(economy).getTradeObjects()) {
						if (!to.isCompositeObject()) continue;
						to.save();
					}
					data.addResponse(L.get("COMPOSITE_PRICING_SET"));
				} else {
					data.addResponse(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
				data.addResponse(L.get("HCDATA_UPDATEITEMS_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("addgeneratedaliases")) { 
			try {
				String economy = "default";
				if (args.length > 1) economy = args[1];
				if (dm.economyExists(economy)) {
					if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
					HyperEconomy he = dm.getEconomy(economy);
					for (TradeObject to:he.getTradeObjects()) {
						String displayName = to.getDisplayName();
						String alias1 = displayName.replace(" ", "_").toLowerCase();
						String alias2 = alias1.replace("_", "");
						if (!he.objectTest(alias1)) to.addAlias(alias1);
						if (!he.objectTest(alias2)) to.addAlias(alias2);
					}
					data.addResponse(L.get("HCDATA_GENERATED_ALIASES_ADDED"));
				} else {
					data.addResponse(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
				data.addResponse(L.get("HCDATA_UPDATEITEMS_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("clearhistory")) {
			String statement = "DELETE FROM hyperconomy_history";
			hc.getSQLWrite().addToQueue(statement);
			data.addResponse(L.get("HCCLEARHISTORY_CLEARED"));
		} else if (args[0].equalsIgnoreCase("clearlogs")) {
			if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {
				new Backup(hc);
			}
			String statement = "DELETE FROM hyperconomy_audit_log";
			hc.getSQLWrite().addToQueue(statement);
			statement = "DELETE FROM hyperconomy_log";
			hc.getSQLWrite().addToQueue(statement);
			data.addResponse(L.get("LOGS_CLEARED"));
		} else if (args[0].equalsIgnoreCase("compactdb")) {
			if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {
				new Backup(hc);
			}
			hc.getSimpleDataLib().getSQLManager().shrinkDatabase();
			data.addResponse(L.get("DB_COMPACTED"));
		} else if (args[0].equalsIgnoreCase("repairnames")) {
			try {
				if (hc.getConf().getBoolean("enable-feature.composite-items")) {
					data.addResponse(L.get("MUST_DISABLE_COMPOSITES"));
					return data;
				}
				if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
				for (HyperEconomy he : hc.getDataManager().getEconomies()) {
					hc.getDataManager().updateNamesFromCSV(he.getName());
				}
				data.addResponse(L.get("NAME_REPAIR_ATTEMPTED"));
				hc.restart();
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
			}
		} else if (args[0].equalsIgnoreCase("updateitemstack") || args[0].equalsIgnoreCase("uis")) {
			try {
				if (args.length < 1) {
					data.addResponse(L.get("HCDATA_UPDATEITEMSTACK_INVALID"));
					return data;
				}
				if (!data.isPlayer()) {
					data.addResponse(L.get("HCDATA_UPDATEITEMSTACK_INVALID"));
					return data;
				}
				HItemStack stack = hp.getItemInHand();
				TradeObject ho = hp.getHyperEconomy().getTradeObject(stack);
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("all")) {
						HInventory inventory = hp.getInventory();
						for (int slot = 0; slot < inventory.getSize(); slot++) {
							HItemStack cStack = inventory.getItem(slot);
							ho = hp.getHyperEconomy().getTradeObject(cStack);
							if (ho == null) continue;
							ho.setData(cStack.serialize());
						}
						data.addResponse(L.get("HCDATA_ITEMSTACK_UPDATED_ALL"));
						return data;
					}
					ho = hp.getHyperEconomy().getTradeObject(args[1]);
				}
				if (ho == null) {
					data.addResponse(L.get("OBJECT_NOT_FOUND"));
					return data;
				}
				if (stack.isBlank()) {
					data.addResponse(L.get("AIR_CANT_BE_TRADED"));
					return data;
				}
				ho.setData(stack.serialize());
				data.addResponse(L.get("HCDATA_ITEMSTACK_UPDATED"));
			} catch (Exception e) {
				data.addResponse(L.get("HCDATA_UPDATEITEMSTACK_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("updatedisplayname") || args[0].equalsIgnoreCase("udn")) {
			try {
				if (args.length < 1) {
					data.addResponse(L.get("HCDATA_UPDATEDISPLAYNAME_INVALID"));
					return data;
				}
				if (!data.isPlayer()) {
					data.addResponse(L.get("HCDATA_UPDATEDISPLAYNAME_INVALID"));
					return data;
				}
				HItemStack stack = hp.getItemInHand();
				TradeObject ho = hp.getHyperEconomy().getTradeObject(stack);
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("all")) {
						HInventory inventory = hp.getInventory();
						for (int slot = 0; slot < inventory.getSize(); slot++) {
							HItemStack cStack = inventory.getItem(slot);
							ho =  hp.getHyperEconomy().getTradeObject(cStack);
							if (ho == null) continue;
							ho.setDisplayName(hc.getMC().getMinecraftItemName(ho.getItem()));
						}
						data.addResponse(L.get("DISPLAYNAME_SET_ALL"));
						return data;
					}
					ho = hp.getHyperEconomy().getTradeObject(args[1]);
				}
				if (ho == null) {
					data.addResponse(L.get("OBJECT_NOT_FOUND"));
					return data;
				}
				ho.setDisplayName(hc.getMC().getMinecraftItemName(ho.getItem()));
				data.addResponse(L.f(L.get("DISPLAYNAME_SET"), ho.getDisplayName()));
			} catch (Exception e) {
				data.addResponse(L.get("HCDATA_UPDATEDISPLAYNAME_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("backup")) {
			try {
				new Backup(hc);
				data.addResponse(L.get("ALL_BACKED_UP"));
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
			}
		} else if (args[0].equalsIgnoreCase("roundvalues")) {
			try {
				String economy = "default";
				if (args.length > 1) economy = args[1];
				if (dm.economyExists(economy)) {
					if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
					for (TradeObject to:dm.getEconomy(economy).getTradeObjects()) {
						to.setValue(CommonFunctions.twoDecimals(to.getValue()));
						to.setStartPrice(CommonFunctions.twoDecimals(to.getStartPrice()));
						to.setStaticPrice(CommonFunctions.twoDecimals(to.getStaticPrice()));
					}
					data.addResponse(L.get("HCDATA_VALUES_ROUNDED"));
				} else {
					data.addResponse(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				data.addResponse(L.get("HCDATA_ROUNDVALUES_INVALID"));
				hc.gSDL().getErrorWriter().writeError(e);
			}
		} else if (args[0].equalsIgnoreCase("formatdisplaynames")) {
			try {
				String economy = "default";
				if (args.length > 1) economy = args[1];
				if (dm.economyExists(economy)) {
					if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
					for (TradeObject to:dm.getEconomy(economy).getTradeObjects()) {
						String oldDisplayName = to.getDisplayName();
						if (oldDisplayName.contains(" ")) continue;
					    String[] parts = oldDisplayName.split("_");
					    String newDisplayName = "";
					    for (String part : parts) {
					    	String camelPart = part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase();
					    	newDisplayName = newDisplayName + " " + camelPart;
					    }
						to.setDisplayName(newDisplayName.trim());
						if (!to.getAliases().contains(oldDisplayName.toLowerCase())) {
							to.addAlias(oldDisplayName.toLowerCase());
						}
					}
					data.addResponse(L.get("HCDATA_FORMAT_DISPLAY_NAMES"));
				} else {
					data.addResponse(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				data.addResponse(L.get("HCDATA_ROUNDVALUES_INVALID"));
				hc.gSDL().getErrorWriter().writeError(e);
			}
		} else if (args[0].equalsIgnoreCase("incrementobjectversion")) {
			try {
				String economy = "default";
				if (args.length > 1) economy = args[1];
				if (dm.economyExists(economy)) {
					if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
					for (TradeObject to:dm.getEconomy(economy).getTradeObjects()) {
						to.setVersion(to.getVersion() + .1);
					}
					data.addResponse(L.get("HCDATA_VERSION_INCREMENTED"));
				} else {
					data.addResponse(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				data.addResponse(L.get("HCDATA_ROUNDVALUES_INVALID"));
				hc.gSDL().getErrorWriter().writeError(e);
			}
		} else if (args[0].equalsIgnoreCase("purgeaccounts")) {
			try {
				data.addResponse(L.f(L.get("HCDATA_ACCOUNTS_PURGED"), hc.getHyperPlayerManager().purgeDeadAccounts()));
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
			}
		} else {
			data.addResponse(L.get("HCDATA_INVALID"));
		}
		return data;
	}
}
