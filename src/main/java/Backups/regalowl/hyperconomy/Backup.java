package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;

public class Backup {
	Backup() {
		HyperConomy hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		
			FileTools ft = new FileTools();
			
			ArrayList<String> backupFiles = new ArrayList<String>();
			backupFiles.add("config.yml");
			backupFiles.add("displays.yml");
			backupFiles.add("enchants.yml");
			backupFiles.add("items.yml");
			backupFiles.add("shops.yml");
			backupFiles.add("signs.yml");
			backupFiles.add("categories.yml");
			if (hc.useMySQL()) {
				sw.executeSQL("CREATE TABLE hyperconomy_objects_backup LIKE hyperconomy_objects; INSERT INTO hyperconomy_objects_backup SELECT * FROM hyperconomy_objects");
				sw.executeSQL("CREATE TABLE hyperconomy_players_backup LIKE hyperconomy_players; INSERT INTO hyperconomy_players_backup SELECT * FROM hyperconomy_players");
				sw.executeSQL("CREATE TABLE hyperconomy_log_backup LIKE hyperconomy_log; INSERT INTO hyperconomy_log_backup SELECT * FROM hyperconomy_log");
				sw.executeSQL("CREATE TABLE hyperconomy_audit_log_backup LIKE hyperconomy_audit_log; INSERT INTO hyperconomy_audit_log_backup SELECT * FROM hyperconomy_audit_log");
			}
			
			backupFiles.add("HyperConomy.db");


			String spath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy";
			String dpath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "backups";
			ft.makeFolder(dpath);
			dpath = dpath + File.separator + ft.getTimeStamp();
			ft.makeFolder(dpath);
			for (int i = 0; i < backupFiles.size(); i++) {
				if (ft.fileExists(spath + File.separator + backupFiles.get(i))) {
					ft.copyFile(spath + File.separator + backupFiles.get(i), dpath + File.separator + backupFiles.get(i));
				}
			}
			
			
			backupFiles.clear();
			backupFiles = ft.getFolderContents(spath + File.separator + "Languages");
	
			spath += File.separator + "Languages";
			dpath += File.separator + "Languages";
			ft.makeFolder(dpath);
			for (int i = 0; i < backupFiles.size(); i++) {
				ft.copyFile(spath + File.separator + backupFiles.get(i), dpath + File.separator + backupFiles.get(i));
			}
	}
}
