package regalowl.hyperconomy;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HyperPlayersBackup {
	HyperPlayersBackup() {
		HyperConomy hc = HyperConomy.hc;
		hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
			public void run() {
				FileTools ft = new FileTools();
				String dpath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "backups";
				ft.makeFolder(dpath);
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				Calendar cal = Calendar.getInstance();
				String datetime = dateFormat.format(cal.getTime());
				dpath = dpath + File.separator + datetime;
				ft.makeFolder(dpath);
				backupHyperPlayers(dpath);
			}
		}, 0L);
	}
	private void backupHyperPlayers(String dpath) {
		dpath = dpath + File.separator + "SQL";
		HyperConomy hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		ft.makeFolder(dpath);
		DataFunctions sf = hc.getSQLFunctions();
		SerializeArrayList sal = new SerializeArrayList();
		String hyperplayers = "";
		ArrayList<String> pplayer = sf.getStringColumn("SELECT PLAYER FROM hyperplayers");
		ArrayList<String> peconomy = sf.getStringColumn("SELECT ECONOMY FROM hyperplayers");
		ArrayList<String> pbalance = sf.getStringColumn("SELECT BALANCE FROM hyperplayers");
		hyperplayers += sal.stringArrayToString(pplayer) + ";";
		hyperplayers += sal.stringArrayToString(peconomy) + ";";
		hyperplayers += sal.stringArrayToString(pbalance) + ";";
		ft.writeStringToFile(hyperplayers, dpath + File.separator + "HyperPlayers.txt");
	}
}
