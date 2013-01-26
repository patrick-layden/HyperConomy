package regalowl.hyperconomy;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HyperLogBackup {
	
	
	@SuppressWarnings("deprecation")
	HyperLogBackup() {
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
				backupHyperLog(dpath);
			}
		}, 0L);
	}
	
	private void backupHyperLog(String dpath) {
		dpath = dpath + File.separator + "SQL";
		HyperConomy hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		ft.makeFolder(dpath);
		DataFunctions sf = hc.getDataFunctions();
		SerializeArrayList sal = new SerializeArrayList();
		String hyperlog = "";
		ArrayList<Integer> lid = sf.getIntColumn("SELECT ID FROM hyperlog");
		ArrayList<String> ltime = sf.getStringColumn("SELECT TIME FROM hyperlog");
		ArrayList<String> lcustomer = sf.getStringColumn("SELECT CUSTOMER FROM hyperlog");
		ArrayList<String> laction = sf.getStringColumn("SELECT ACTION FROM hyperlog");
		ArrayList<String> lobject = sf.getStringColumn("SELECT OBJECT FROM hyperlog");
		ArrayList<Double> lamount = sf.getDoubleColumn("SELECT AMOUNT FROM hyperlog");
		ArrayList<Double> lmoney = sf.getDoubleColumn("SELECT MONEY FROM hyperlog");
		ArrayList<Double> ltax = sf.getDoubleColumn("SELECT TAX FROM hyperlog");
		ArrayList<String> lstore = sf.getStringColumn("SELECT STORE FROM hyperlog");
		ArrayList<String> ltype = sf.getStringColumn("SELECT TYPE FROM hyperlog");
		hyperlog += sal.intArrayToString(lid) + ";";
		hyperlog += sal.stringArrayToString(ltime) + ";";
		hyperlog += sal.stringArrayToString(lcustomer) + ";";
		hyperlog += sal.stringArrayToString(laction) + ";";
		hyperlog += sal.stringArrayToString(lobject) + ";";
		hyperlog += sal.doubleArrayToString(lamount) + ";";
		hyperlog += sal.doubleArrayToString(lmoney) + ";";
		hyperlog += sal.doubleArrayToString(ltax) + ";";
		hyperlog += sal.stringArrayToString(lstore) + ";";
		hyperlog += sal.stringArrayToString(ltype) + ";";
		ft.writeStringToFile(hyperlog, dpath + File.separator + "HyperLog.txt");
	}
}
