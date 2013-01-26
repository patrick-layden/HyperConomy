package regalowl.hyperconomy;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HyperObjectsBackup {
	@SuppressWarnings("deprecation")
	HyperObjectsBackup() {
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
				backupHyperObjects(dpath);
			}
		}, 0L);
	}
	
	private void backupHyperObjects(String dpath) {
		dpath = dpath + File.separator + "SQL";
		HyperConomy hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		ft.makeFolder(dpath);
		DataFunctions sf = hc.getDataFunctions();
		SerializeArrayList sal = new SerializeArrayList();
		String hyperobjects = "";
		ArrayList<String> names = sf.getStringColumn("SELECT NAME FROM hyperobjects");
		ArrayList<String> economies = sf.getStringColumn("SELECT ECONOMY FROM hyperobjects");
		ArrayList<String> type = sf.getStringColumn("SELECT TYPE FROM hyperobjects");
		ArrayList<String> category = sf.getStringColumn("SELECT CATEGORY FROM hyperobjects");
		ArrayList<String> material = sf.getStringColumn("SELECT MATERIAL FROM hyperobjects");
		ArrayList<Integer> id = sf.getIntColumn("SELECT ID FROM hyperobjects");
		ArrayList<Integer> data = sf.getIntColumn("SELECT DATA FROM hyperobjects");
		ArrayList<Integer> durability = sf.getIntColumn("SELECT DURABILITY FROM hyperobjects");
		ArrayList<Double> value = sf.getDoubleColumn("SELECT VALUE FROM hyperobjects");
		ArrayList<String> isstatic = sf.getStringColumn("SELECT STATIC FROM hyperobjects");
		ArrayList<Double> staticprice = sf.getDoubleColumn("SELECT STATICPRICE FROM hyperobjects");
		ArrayList<Double> stock = sf.getDoubleColumn("SELECT STOCK FROM hyperobjects");
		ArrayList<Double> median = sf.getDoubleColumn("SELECT MEDIAN FROM hyperobjects");
		ArrayList<String> isinitial = sf.getStringColumn("SELECT INITIATION FROM hyperobjects");
		ArrayList<Double> startprice = sf.getDoubleColumn("SELECT STARTPRICE FROM hyperobjects");
		ArrayList<Double> ceiling = sf.getDoubleColumn("SELECT CEILING FROM hyperobjects");
		ArrayList<Double> floor = sf.getDoubleColumn("SELECT FLOOR FROM hyperobjects");
		hyperobjects += sal.stringArrayToString(names) + ";";
		hyperobjects += sal.stringArrayToString(economies) + ";";
		hyperobjects += sal.stringArrayToString(type) + ";";
		hyperobjects += sal.stringArrayToString(category) + ";";
		hyperobjects += sal.stringArrayToString(material) + ";";
		hyperobjects += sal.intArrayToString(id) + ";";
		hyperobjects += sal.intArrayToString(data) + ";";
		hyperobjects += sal.intArrayToString(durability) + ";";
		hyperobjects += sal.doubleArrayToString(value) + ";";
		hyperobjects += sal.stringArrayToString(isstatic) + ";";
		hyperobjects += sal.doubleArrayToString(staticprice) + ";";
		hyperobjects += sal.doubleArrayToString(stock) + ";";
		hyperobjects += sal.doubleArrayToString(median) + ";";
		hyperobjects += sal.stringArrayToString(isinitial) + ";";
		hyperobjects += sal.doubleArrayToString(startprice) + ";";
		hyperobjects += sal.doubleArrayToString(ceiling) + ";";
		hyperobjects += sal.doubleArrayToString(floor) + ";";
		ft.writeStringToFile(hyperobjects, dpath + File.separator + "HyperObjects.txt");
	}
}
