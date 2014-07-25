package regalowl.hyperconomy.command;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.file.FileTools;
import regalowl.databukkit.sql.QueryResult;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.util.Backup;
import regalowl.hyperconomy.util.LanguageFile;

public class Hcdata implements CommandExecutor {
	
	private HyperConomy hc;
	private LanguageFile L;
	private CommandSender cSender;
	private ArrayList<String> tables;
	private String table;
	private FileTools ft;
	private String folderPath;
	
	public Hcdata() {
		hc = HyperConomy.hc;
		tables = hc.getDataManager().getTablesList();
	}

	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		hc = HyperConomy.hc;
		L = hc.getLanguageFile();
		ft = hc.getFileTools();
		cSender = sender;
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);;
			return true;
		}
		DataManager em = hc.getDataManager();
		if (args.length == 0) {
			sender.sendMessage(L.get("HCDATA_INVALID"));
			return true;
		}
		folderPath = hc.getDataBukkit().getPluginFolderPath();
		if (args[0].equalsIgnoreCase("exportcsv")) {
			try {
				ft.makeFolder(folderPath + File.separator + "import_export");
				table = args[1];
				if (table.equalsIgnoreCase("all")) {
					hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
						public void run() {
							for (String table:tables) {
								QueryResult data = hc.getSQLRead().select("SELECT * FROM hyperconomy_" + table);
								String path = folderPath + File.separator + "import_export" + File.separator + table + ".csv";
								hc.getFileTools().writeCSV(data, path);
							}
						}
					});
					cSender.sendMessage(L.get("CSVS_CREATED"));
					return true;
				}
				if (!tables.contains(table)) {
					sender.sendMessage(L.get("TABLE_NOT_EXIST"));
					return true;
				}
				hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
					public void run() {
						QueryResult data = hc.getSQLRead().select("SELECT * FROM hyperconomy_" + table);
						ft.makeFolder(folderPath + File.separator + "import_export");
						String path = folderPath + File.separator + "import_export" + File.separator + table + ".csv";
						hc.getFileTools().writeCSV(data, path);
					}
				});
				cSender.sendMessage(L.get("CSV_CREATED"));
			} catch (Exception e) {
				sender.sendMessage(L.get("HCDATA_EXPORTCSV_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("importcsv")) {
			try {
				ft.makeFolder(folderPath + File.separator + "import_export");
				table = args[1];
				if (table.equalsIgnoreCase("all")) {
					for (String table:tables) {
						String path = folderPath + File.separator + "import_export" + File.separator + table + ".csv";
						if (!ft.fileExists(path)) {continue;}
						QueryResult data = hc.getFileTools().readCSV(path);
						ArrayList<String> columns = data.getColumnNames();
						hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_" + table);
						while (data.next()) {
							HashMap<String, String> values = new HashMap<String, String>();
							for (String column : columns) {
								values.put(column, data.getString(column));
							}
							hc.getSQLWrite().performInsert("hyperconomy_" + table, values);
						}
					}
					cSender.sendMessage(L.get("CSVS_IMPORTED"));
					hc.restart();
					return true;
				}
				if (!tables.contains(table)) {
					sender.sendMessage(L.get("TABLE_NOT_EXIST"));
					return true;
				}
				
				String path = folderPath + File.separator + "import_export" + File.separator + table + ".csv";
				if (!ft.fileExists(path)) {
					sender.sendMessage(L.get("IMPORT_FILE_NOT_EXIST"));
					return true;
				}
				QueryResult data = hc.getFileTools().readCSV(path);
				ArrayList<String> columns = data.getColumnNames();
				hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_" + table);
				while (data.next()) {
					HashMap<String, String> values = new HashMap<String, String>();
					for (String column : columns) {
						values.put(column, data.getString(column));
					}
					hc.getSQLWrite().performInsert("hyperconomy_" + table, values);
				}
				cSender.sendMessage(L.get("CSV_IMPORTED"));
				hc.restart();
			} catch (Exception e) {
				sender.sendMessage(L.get("HCDATA_IMPORTCSV_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("importmissing")) { 
			try {
				String economy = "default";
				if (args.length > 1) {
					economy = args[1];
				}
				if (em.economyExists(economy)) {
					if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup();}
					ArrayList<String> added = em.getEconomy(economy).loadNewItems();
					sender.sendMessage(ChatColor.GOLD + added.toString() + " " + L.get("LOADED_INTO_ECONOMY"));
				} else {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				sender.sendMessage(L.get("HCDATA_IMPORTMISSING_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("setdefaultprices")) { 
			try {
				String economy = "default";
				if (args.length > 1) {
					economy = args[1];
				}
				if (em.economyExists(economy)) {
					if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup();}
					String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
					FileTools ft = hc.getFileTools();
					if (!ft.fileExists(defaultObjectsPath)) {
						ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
					}
					QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
					while (data.next()) {
						String objectName = data.getString("NAME");
						HyperObject ho = em.getEconomy(economy).getHyperObject(objectName);
						ho.setStartprice(data.getDouble("STARTPRICE"));
						ho.setStaticprice(data.getDouble("STATICPRICE"));
						ho.setValue(data.getDouble("VALUE"));
					}
					ft.deleteFile(defaultObjectsPath);
					sender.sendMessage(L.get("PRICES_IMPORTED"));
				} else {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
				}
			} catch (Exception e) {
				sender.sendMessage(L.get("HCDATA_SETDEFAULTPRICES_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("clearhistory")) {
			String statement = "DELETE FROM hyperconomy_history";
			hc.getSQLWrite().addToQueue(statement);
			sender.sendMessage(L.get("HCCLEARHISTORY_CLEARED"));
		} else if (args[0].equalsIgnoreCase("clearlogs")) {
			if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {
				new Backup();
			}
			String statement = "DELETE FROM hyperconomy_audit_log";
			hc.getSQLWrite().addToQueue(statement);
			statement = "DELETE FROM hyperconomy_log";
			hc.getSQLWrite().addToQueue(statement);
			sender.sendMessage(L.get("LOGS_CLEARED"));
		} else if (args[0].equalsIgnoreCase("repairnames")) {
			try {
				if (hc.getConf().getBoolean("enable-feature.composite-items")) {
					sender.sendMessage(L.get("MUST_DISABLE_COMPOSITES"));
					return true;
				}
				if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup();}
				for (HyperEconomy he : hc.getDataManager().getEconomies()) {
					he.updateNamesFromYml();
				}
				sender.sendMessage(L.get("NAME_REPAIR_ATTEMPTED"));
				hc.restart();
			} catch (Exception e) {
				hc.gDB().writeError(e);
			}
		} else if (args[0].equalsIgnoreCase("updateitemstack") || args[0].equalsIgnoreCase("uis")) {
			try {
				if (args.length != 2) {
					sender.sendMessage(L.get("HCDATA_UPDATEITEMSTACK_INVALID"));
					return true;
				}
				Player p = null;
				if (sender instanceof Player) {
					p = (Player)sender;
				}
				if (p == null) {
					sender.sendMessage(L.get("HCDATA_UPDATEITEMSTACK_INVALID"));
					return true;
				}
				HyperObject ho = em.getHyperPlayer(p).getHyperEconomy().getHyperObject(args[1]);
				if (ho == null) {
					sender.sendMessage(L.get("OBJECT_NOT_FOUND"));
					return true;
				}
				ItemStack stack = p.getItemInHand();
				if (stack.getType() == Material.AIR) {
					sender.sendMessage(L.get("AIR_CANT_BE_TRADED"));
					return true;
				}
				SerializableItemStack sis = new SerializableItemStack(stack);
				ho.setData(sis.serialize());
				sender.sendMessage(L.get("HCDATA_ITEMSTACK_UPDATED"));
			} catch (Exception e) {
				sender.sendMessage(L.get("HCDATA_UPDATEITEMSTACK_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("backup")) {
			try {
				new Backup();
				sender.sendMessage(L.get("ALL_BACKED_UP"));
			} catch (Exception e) {
				hc.gDB().writeError(e);
			}
		} else if (args[0].equalsIgnoreCase("purgeaccounts")) {
			try {
				sender.sendMessage(L.f(L.get("HCDATA_ACCOUNTS_PURGED"), hc.getHyperPlayerManager().purgeDeadAccounts()));
			} catch (Exception e) {
				hc.gDB().writeError(e);
			}
		} else {
			sender.sendMessage(L.get("HCDATA_INVALID"));
		}
		return true;
	}
}
