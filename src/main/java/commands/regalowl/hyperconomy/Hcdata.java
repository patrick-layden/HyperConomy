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
	private ArrayList<String> tables = new ArrayList<String>();
	private String table;
	
	Hcdata() {
		tables.add("settings");
		tables.add("objects");
		tables.add("players");
		tables.add("log");
		tables.add("history");
		tables.add("audit_log");
		tables.add("shop_objects");
		tables.add("frame_shops");
		tables.add("banks");
	}

	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		hc = HyperConomy.hc;
		L = hc.getLanguageFile();
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
		if (args[0].equalsIgnoreCase("exportcsv")) {
			try {
				table = args[1];
				if (!tables.contains(table)) {
					sender.sendMessage(L.get("TABLE_NOT_EXIST"));
					return true;
				}
				hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
					public void run() {
						QueryResult data = hc.getSQLRead().aSyncSelect("SELECT * FROM hyperconomy_" + table);
						FileTools ft = hc.getFileTools();
						String path = hc.getDataBukkit().getPluginFolderPath();
						ft.makeFolder(path + File.separator + "import_export");
						path += File.separator + "import_export" + File.separator + table + ".csv";
						hc.getFileTools().writeCSV(data, path);
						cSender.sendMessage(L.get("CSV_CREATED"));
					}
				});
			} catch (Exception e) {
				sender.sendMessage(L.get("HCDATA_EXPORTCSV_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("importcsv")) {
			try {
				table = args[1];
				if (!tables.contains(table)) {
					sender.sendMessage(L.get("TABLE_NOT_EXIST"));
					return true;
				}
				FileTools ft = hc.getFileTools();
				String path = hc.getDataBukkit().getPluginFolderPath();
				ft.makeFolder(path + File.separator + "import_export");
				path += File.separator + "import_export" + File.separator + table + ".csv";
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
				hc.restart();
				cSender.sendMessage(L.get("CSV_IMPORTED"));
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
