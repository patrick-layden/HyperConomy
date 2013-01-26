package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;

public class RestoreSQL {
	
	private CommandSender sender;
	
	public void restore(CommandSender s) {
		sender = s;
		HyperConomy hc = HyperConomy.hc;
    	hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
		    public void run() {
		    	restoreSQL(sender);
		    }
		}, 5L);
	}
	
	
	private void restoreSQL(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		SQLEconomy sqe = hc.getSQLEconomy();
		LanguageFile L = hc.getLanguageFile();
		FileTools ft = new FileTools();
		SerializeArrayList sal = new SerializeArrayList();
		ArrayList<String> statements = new ArrayList<String>();
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "SQL";
		sqe.deleteTables();
		sqe.checkTables();
		
		
		String hyperobjects = ft.getStringFromFile(path + File.separator + "HyperObjects.txt");
		if (hyperobjects.equalsIgnoreCase("error") || hyperobjects.equalsIgnoreCase("") || hyperobjects == null) {
			sender.sendMessage(L.get("SQL_BACKUP_NOT_FOUND"));
			return;
		}
		ArrayList<String> names = sal.stringToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<String> economies = sal.stringToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<String> type = sal.stringToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<String> category = sal.stringToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<String> material = sal.stringToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<Integer> id = sal.intToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<Integer> data = sal.intToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<Integer> durability = sal.intToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<Double> value = sal.doubleToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<String> isstatic = sal.stringToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<Double> staticprice = sal.doubleToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<Double> stock = sal.doubleToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<Double> median = sal.doubleToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<String> isinitial = sal.stringToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		ArrayList<Double> startprice = sal.doubleToArray(hyperobjects.substring(0, hyperobjects.indexOf(";")));
		//hyperobjects = hyperobjects.substring(hyperobjects.indexOf(";") + 1, hyperobjects.length());
		
		for (int i = 0; i < names.size(); i++) {
			statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE)"
		            + " Values ('" + names.get(i) + "','" + economies.get(i) + "','"
		        + type.get(i) + "','" + category.get(i) + "','" + material.get(i) + "','" + id.get(i) + "','" + data.get(i) + 
		        "','" + durability.get(i) + "','" + value.get(i) + "','" + 
		        isstatic.get(i) + "','" + staticprice.get(i) + "','" + 
		        stock.get(i) + "','" + median.get(i) + "','" + 
		        isinitial.get(i) + "','" + startprice.get(i) + "')");
		}
		

		
		String hyperplayers = ft.getStringFromFile(path + File.separator + "HyperPlayers.txt");
		ArrayList<String> pplayer = sal.stringToArray(hyperplayers.substring(0, hyperplayers.indexOf(";")));
		hyperplayers = hyperplayers.substring(hyperplayers.indexOf(";") + 1, hyperplayers.length());
		ArrayList<String> peconomy = sal.stringToArray(hyperplayers.substring(0, hyperplayers.indexOf(";")));
		
		
		for (int i = 0; i < pplayer.size(); i++) {
			statements.add("Insert Into hyperplayers (PLAYER, ECONOMY)"
		            + " Values ('" + pplayer.get(i) + "','" + peconomy.get(i) + "')");
		}
		
		
		
		
		String hyperlog = ft.getStringFromFile(path + File.separator + "HyperLog.txt");
		ArrayList<Integer> lid = sal.intToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		hyperlog = hyperlog.substring(hyperlog.indexOf(";") + 1, hyperlog.length());
		ArrayList<String> ltime = sal.stringToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		hyperlog = hyperlog.substring(hyperlog.indexOf(";") + 1, hyperlog.length());
		ArrayList<String> lcustomer = sal.stringToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		hyperlog = hyperlog.substring(hyperlog.indexOf(";") + 1, hyperlog.length());
		ArrayList<String> laction = sal.stringToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		hyperlog = hyperlog.substring(hyperlog.indexOf(";") + 1, hyperlog.length());
		ArrayList<String> lobject = sal.stringToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		hyperlog = hyperlog.substring(hyperlog.indexOf(";") + 1, hyperlog.length());
		ArrayList<Double> lamount = sal.doubleToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		hyperlog = hyperlog.substring(hyperlog.indexOf(";") + 1, hyperlog.length());
		ArrayList<Double> lmoney = sal.doubleToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		hyperlog = hyperlog.substring(hyperlog.indexOf(";") + 1, hyperlog.length());
		ArrayList<Double> ltax = sal.doubleToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		hyperlog = hyperlog.substring(hyperlog.indexOf(";") + 1, hyperlog.length());
		ArrayList<String> lstore = sal.stringToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		hyperlog = hyperlog.substring(hyperlog.indexOf(";") + 1, hyperlog.length());
		ArrayList<String> ltype = sal.stringToArray(hyperlog.substring(0, hyperlog.indexOf(";")));
		
		
		for (int i = 0; i < lid.size(); i++) {
			statements.add("Insert Into hyperlog (ID, TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE)"
		            + " Values ('" + lid.get(i) + "','" + ltime.get(i) + "','"
		        + lcustomer.get(i) + "','" + laction.get(i) + "','" + lobject.get(i) + "','" + lamount.get(i) + "','" + lmoney.get(i) + 
		        "','" + ltax.get(i) + "','" + lstore.get(i) + "','" + 
		        ltype.get(i) + "')");
		}
		
		
		
		
		

		
		
		
		
		String hyperhistory = ft.getStringFromFile(path + File.separator + "HyperHistory.txt");
		ArrayList<Integer> hid = sal.intToArray(hyperhistory.substring(0, hyperhistory.indexOf(";")));
		hyperhistory = hyperhistory.substring(hyperhistory.indexOf(";") + 1, hyperhistory.length());
		ArrayList<String> hobject = sal.stringToArray(hyperhistory.substring(0, hyperhistory.indexOf(";")));
		hyperhistory = hyperhistory.substring(hyperhistory.indexOf(";") + 1, hyperhistory.length());
		ArrayList<String> heconomy = sal.stringToArray(hyperhistory.substring(0, hyperhistory.indexOf(";")));
		hyperhistory = hyperhistory.substring(hyperhistory.indexOf(";") + 1, hyperhistory.length());
		ArrayList<String> htime = sal.stringToArray(hyperhistory.substring(0, hyperhistory.indexOf(";")));
		hyperhistory = hyperhistory.substring(hyperhistory.indexOf(";") + 1, hyperhistory.length());
		ArrayList<Double> hprice = sal.doubleToArray(hyperhistory.substring(0, hyperhistory.indexOf(";")));
		hyperhistory = hyperhistory.substring(hyperhistory.indexOf(";") + 1, hyperhistory.length());
		ArrayList<Integer> hcount = sal.intToArray(hyperhistory.substring(0, hyperhistory.indexOf(";")));
		
		
		for (int i = 0; i < hid.size(); i++) {
			statements.add("Insert Into hyperhistory (ID, OBJECT, ECONOMY, TIME, PRICE, COUNT)"
		            + " Values ('" + hid.get(i) + "','" + hobject.get(i) + "','"
		        + heconomy.get(i) + "','" + htime.get(i) + "','" + hprice.get(i) + "','" + hcount.get(i) + "')");
		}
		
		
		

		SQLWrite sw = hc.getSQLWrite();
		sw.writeData(statements);
		hc.getDataFunctions().load();
		sender.sendMessage(L.get("SQL_TABLES_IMPORTED"));
	}
}
