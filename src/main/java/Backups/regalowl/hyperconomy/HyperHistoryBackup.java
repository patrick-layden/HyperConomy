package regalowl.hyperconomy;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HyperHistoryBackup {
	HyperHistoryBackup() {
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
				backupHyperHistory(dpath);
			}
		}, 0L);
	}
	
	
	
	/*
	private void backupHyperHistory(String dpath) {
		dpath = dpath + File.separator + "SQL";
		HyperConomy hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		ft.makeFolder(dpath);
		SQLFunctions sf = hc.getSQLFunctions();
		SerializeArrayList sal = new SerializeArrayList();
		String hyperhistory = "";
		

		
		ArrayList<Integer> hid = sf.getIntColumn("SELECT ID FROM hyperhistory");
		hyperhistory = sal.intArrayToString(hid) + ";";
		hid.clear();
		ft.writeStringToFile(hyperhistory, dpath + File.separator + "HyperHistory.txt");
		hyperhistory = "";
		
		ArrayList<String> hobject = sf.getStringColumn("SELECT OBJECT FROM hyperhistory");
		hyperhistory = sal.stringArrayToString(hobject) + ";";
		hobject.clear();
		ft.writeStringToFile(hyperhistory, dpath + File.separator + "HyperHistory.txt");
		hyperhistory = "";
		
		ArrayList<String> heconomy = sf.getStringColumn("SELECT ECONOMY FROM hyperhistory");
		hyperhistory = sal.stringArrayToString(heconomy) + ";";
		heconomy.clear();
		ft.writeStringToFile(hyperhistory, dpath + File.separator + "HyperHistory.txt");
		hyperhistory = "";
		
		ArrayList<String> htime = sf.getStringColumn("SELECT TIME FROM hyperhistory");
		hyperhistory = sal.stringArrayToString(htime) + ";";
		htime.clear();
		ft.writeStringToFile(hyperhistory, dpath + File.separator + "HyperHistory.txt");
		hyperhistory = "";
		
		ArrayList<Double> hprice = sf.getDoubleColumn("SELECT PRICE FROM hyperhistory");
		hyperhistory = sal.doubleArrayToString(hprice) + ";";
		hprice.clear();
		ft.writeStringToFile(hyperhistory, dpath + File.separator + "HyperHistory.txt");
		hyperhistory = "";
		
		ArrayList<Integer> hcount = sf.getIntColumn("SELECT COUNT FROM hyperhistory");
		hyperhistory = sal.intArrayToString(hcount) + ";";
		hcount.clear();
		ft.writeStringToFile(hyperhistory, dpath + File.separator + "HyperHistory.txt");
		hyperhistory = "";
	}
	*/
	
	
	private void backupHyperHistory(String dpath) {
		dpath = dpath + File.separator + "SQL";
		HyperConomy hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		ft.makeFolder(dpath);
		SQLFunctions sf = hc.getSQLFunctions();
		SerializeArrayList sal = new SerializeArrayList();
		String hyperhistory = "";
		ArrayList<Integer> hid = sf.getIntColumn("SELECT ID FROM hyperhistory");
		ArrayList<String> hobject = sf.getStringColumn("SELECT OBJECT FROM hyperhistory");
		ArrayList<String> heconomy = sf.getStringColumn("SELECT ECONOMY FROM hyperhistory");
		ArrayList<String> htime = sf.getStringColumn("SELECT TIME FROM hyperhistory");
		ArrayList<Double> hprice = sf.getDoubleColumn("SELECT PRICE FROM hyperhistory");
		ArrayList<Integer> hcount = sf.getIntColumn("SELECT COUNT FROM hyperhistory");
		hyperhistory += sal.intArrayToString(hid) + ";";
		hyperhistory += sal.stringArrayToString(hobject) + ";";
		hyperhistory += sal.stringArrayToString(heconomy) + ";";
		hyperhistory += sal.stringArrayToString(htime) + ";";
		hyperhistory += sal.doubleArrayToString(hprice) + ";";
		hyperhistory += sal.intArrayToString(hcount) + ";";
		ft.writeStringToFile(hyperhistory, dpath + File.separator + "HyperHistory.txt");
	}
	
}
