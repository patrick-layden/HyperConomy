package regalowl.hyperconomy;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class Backup {

	
	public void BackupData() {
		HyperConomy hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		ArrayList<String> backupFiles = new ArrayList<String>();
		backupFiles.add("config.yml");
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
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		String datetime = dateFormat.format(cal.getTime());
		dpath = dpath + File.separator + datetime;
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
