package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;

import regalowl.databukkit.FileTools;
import regalowl.databukkit.QueryResult;

public class Backup {

	private String destinationPath;
	
	Backup() {
		HyperConomy hc = HyperConomy.hc;
		FileTools ft = hc.getFileTools();

		ArrayList<String> backupFiles = new ArrayList<String>();
		backupFiles.add("config.yml");
		backupFiles.add("displays.yml");
		backupFiles.add("objects.yml");
		backupFiles.add("shops.yml");
		backupFiles.add("signs.yml");
		backupFiles.add("categories.yml");
		backupFiles.add("composites.yml");
		backupFiles.add("HyperConomy.db");
		backupFiles.add("errors.log");
		backupFiles.add("SQL.log");
		String spath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy";
		destinationPath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "backups";
		ft.makeFolder(destinationPath);
		destinationPath = destinationPath + File.separator + ft.getTimeStamp();
		ft.makeFolder(destinationPath);
		for (int i = 0; i < backupFiles.size(); i++) {
			if (ft.fileExists(spath + File.separator + backupFiles.get(i))) {
				ft.copyFile(spath + File.separator + backupFiles.get(i), destinationPath + File.separator + backupFiles.get(i));
			}
		}

		backupFiles.clear();
		backupFiles = ft.getFolderContents(spath + File.separator + "Languages");

		spath += File.separator + "Languages";
		String languagePath = destinationPath + File.separator + "Languages";
		ft.makeFolder(languagePath);
		for (int i = 0; i < backupFiles.size(); i++) {
			ft.copyFile(spath + File.separator + backupFiles.get(i), languagePath + File.separator + backupFiles.get(i));
		}
		
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				HyperConomy hc = HyperConomy.hc;
				ArrayList<String> tables = hc.getEconomyManager().getTablesList();
				FileTools ft = hc.getFileTools();
				String folderPath = destinationPath + File.separator + "SQL_Tables";
				ft.makeFolder(folderPath);
				for (String table:tables) {
					if (table.equalsIgnoreCase("history")) {continue;}
					QueryResult data = hc.getSQLRead().select("SELECT * FROM hyperconomy_" + table);
					String writePath = folderPath + File.separator + table + ".csv";
					hc.getFileTools().writeCSV(data, writePath);
				}
			}
		});
		
	}
}
