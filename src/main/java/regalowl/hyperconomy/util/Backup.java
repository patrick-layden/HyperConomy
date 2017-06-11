package regalowl.hyperconomy.util;

import java.io.File;
import java.util.ArrayList;

import regalowl.simpledatalib.file.FileTools;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.hyperconomy.HyperConomy;

public class Backup {

	private transient HyperConomy hc;
	private String destinationPath;
	
	public Backup(HyperConomy hc) {
		this.hc = hc;
		FileTools ft = hc.getFileTools();

		ArrayList<String> backupFiles = new ArrayList<String>();
		backupFiles.add("config.yml");
		backupFiles.add("categories.yml");
		backupFiles.add("HyperConomy.db");
		backupFiles.add("errors.log");
		backupFiles.add("SQL.log");
		String spath = hc.getFolderPath();
		destinationPath = spath + File.separator + "backups";
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
		new Thread(new BackupTask()).start();
	}
	
	private class BackupTask implements Runnable {
		@Override
		public void run() {
			ArrayList<String> tables = hc.getDataManager().getTablesList();
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
	}
	
	
}
