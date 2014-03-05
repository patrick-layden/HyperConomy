package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import regalowl.databukkit.FileTools;
import regalowl.databukkit.QueryResult;
import regalowl.databukkit.SQLWrite;

public class Hcdata implements CommandExecutor {
	
	private HyperConomy hc;
	private LanguageFile L;
	private CommandSender cSender;
	private ArrayList<String> tables;
	private String table;
	private FileTools ft;
	private String folderPath;
	
	Hcdata() {
		hc = HyperConomy.hc;
		tables = hc.getEconomyManager().getTablesList();
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
		EconomyManager em = hc.getEconomyManager();
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
		} else if (args[0].equalsIgnoreCase("importyml")) {
			try {
				String economy = args[1];
				if (!em.economyExists(economy)) {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
					return true;
				}
				if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
					new Backup();
				}
				SQLWrite sw = hc.getSQLWrite();
				sw.addToQueue("DELETE FROM hyperconomy_objects WHERE ECONOMY = '" + economy + "'");
				em.createEconomyFromYml(economy, true);
				sender.sendMessage(L.get("ECONOMY_IMPORTED"));	
			} catch (Exception e) {
				sender.sendMessage(L.get("HCDATA_IMPORTYML_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("exportyml")) {
			try {
				String economy = args[1];
				if (!em.economyExists(economy)) {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
					return true;
				}
				if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
					new Backup();
				}
				em.getEconomy(economy).exportToYml();
				sender.sendMessage(L.get("ECONOMY_EXPORTED"));
			} catch (Exception e) {
				sender.sendMessage(L.get("HCDATA_EXPORTYML_INVALID"));
			}
		} else {
			sender.sendMessage(L.get("HCDATA_INVALID"));
		}
		return true;
	}

}
