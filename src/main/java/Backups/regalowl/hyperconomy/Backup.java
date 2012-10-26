package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;

public class Backup {
	Backup() {
		HyperConomy hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		ArrayList<String> backupFiles = new ArrayList<String>();
		backupFiles.add("config.yml");
		backupFiles.add("displays.yml");
		backupFiles.add("enchants.yml");
		backupFiles.add("history.yml");
		backupFiles.add("items.yml");
		backupFiles.add("log.yml");
		backupFiles.add("shops.yml");
		backupFiles.add("signs.yml");
		backupFiles.add("categories.yml");
		String spath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy";
		String dpath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "backups";
		ft.makeFolder(dpath);
		dpath = dpath + File.separator + ft.getTimeStamp();
		ft.makeFolder(dpath);
		for (int i = 0; i < backupFiles.size(); i++) {
			ft.copyFile(spath + File.separator + backupFiles.get(i), dpath + File.separator + backupFiles.get(i));
		}
		
		
		backupFiles.clear();
		backupFiles = ft.getFolderContents(spath + File.separator + "Languages");

		spath += File.separator + "Languages";
		dpath += File.separator + "Languages";
		ft.makeFolder(dpath);
		for (int i = 0; i < backupFiles.size(); i++) {
			ft.copyFile(spath + File.separator + backupFiles.get(i), dpath + File.separator + backupFiles.get(i));
		}
		
		
		if (hc.useSQL()) {
			new HyperPlayersBackup();
			new HyperHistoryBackup();
			new HyperLogBackup();
			new HyperObjectsBackup();
		}
	}
}
