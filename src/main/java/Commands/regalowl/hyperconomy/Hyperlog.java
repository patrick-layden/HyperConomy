package regalowl.hyperconomy;


import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Hyperlog {

	private CommandSender sender;
	private String statement;
	private ArrayList<String> result;
	private HyperConomy hc;
	
	Hyperlog(String args[], CommandSender csender) {
		sender = csender;
		hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataHandler df = hc.getDataFunctions();
		try {
			if (args.length % 2 != 0 || args.length == 0) {
				sender.sendMessage(L.get("HYPERLOG_INVALID"));
				return;
			}

			statement = "SELECT * FROM hyperconomy_log WHERE";
			for (int i = 0; i < args.length; i += 2) {

				String type = args[i];
				String value = args[i + 1];

				if (i >= 2) {
					statement += " AND";
				}

				if (type.equalsIgnoreCase("player") || type.equalsIgnoreCase("p")) {
					statement += " CUSTOMER LIKE '%" + value + "%'";
				} else if (type.equalsIgnoreCase("since") || type.equalsIgnoreCase("s")) {
					String increment = value.substring(value.length() - 1, value.length());
					Integer quantity = Integer.parseInt(value.substring(0, value.length() - 1));
					if (increment.equalsIgnoreCase("m")) {
						// do nothing
					} else if (increment.equalsIgnoreCase("h")) {
						quantity = quantity * 60;
					} else if (increment.equalsIgnoreCase("d")) {
						quantity = quantity * 60 * 24;
					} else {
						sender.sendMessage(L.get("HYPERLOG_INVALID_INCREMENT"));
						return;
					}
					if (hc.useMySQL()) {
						statement += " TIME > DATE_SUB(NOW(), INTERVAL " + quantity + " MINUTE)";
					} else {
						statement += " TIME > date('now','" + df.formatSQLiteTime(quantity * -1) + " minute')";
					}
					
				} else if (type.equalsIgnoreCase("before") || type.equalsIgnoreCase("b")) {
					String increment = value.substring(value.length() - 1, value.length());
					Integer quantity = Integer.parseInt(value.substring(0, value.length() - 1));
					if (increment.equalsIgnoreCase("m")) {
						// do nothing
					} else if (increment.equalsIgnoreCase("h")) {
						quantity = quantity * 60;
					} else if (increment.equalsIgnoreCase("d")) {
						quantity = quantity * 60 * 24;
					} else {
						sender.sendMessage(L.get("HYPERLOG_INVALID_INCREMENT"));
						return;
					}

					if (hc.useMySQL()) {
						statement += " TIME < DATE_SUB(NOW(), INTERVAL " + quantity + " MINUTE)";
					} else {
						statement += " TIME < date('now','" + df.formatSQLiteTime(quantity * -1) + " minute')";
					}
				} else if (type.equalsIgnoreCase("action") || type.equalsIgnoreCase("a")) {
					statement += " ACTION LIKE '%" + value + "%'";
				} else if (type.equalsIgnoreCase("object") || type.equalsIgnoreCase("o")) {
					statement += " OBJECT LIKE '%" + value + "%'";
				} else if (type.equalsIgnoreCase(">amount") || type.equalsIgnoreCase(">a")) {
					statement += " AMOUNT > '" + value + "'";
				} else if (type.equalsIgnoreCase("<amount") || type.equalsIgnoreCase("<a")) {
					statement += " AMOUNT < '" + value + "'";
				} else if (type.equalsIgnoreCase(">money") || type.equalsIgnoreCase(">m")) {
					statement += " MONEY > '" + value + "'";
				} else if (type.equalsIgnoreCase("<money") || type.equalsIgnoreCase("<m")) {
					statement += " MONEY < '" + value + "'";
				} else if (type.equalsIgnoreCase(">tax") || type.equalsIgnoreCase(">t")) {
					statement += " TAX > '" + value + "'";
				} else if (type.equalsIgnoreCase("<tax") || type.equalsIgnoreCase("<t")) {
					statement += " TAX < '" + value + "'";
				} else if (type.equalsIgnoreCase("store") || type.equalsIgnoreCase("st")) {
					statement += " STORE LIKE '%" + value + "%'";
				} else if (type.equalsIgnoreCase("type") || type.equalsIgnoreCase("ty")) {
					statement += " TYPE LIKE '%" + value + "%'";
				} else if (type.equalsIgnoreCase(">id")) {
					statement += " ID > '" + value + "'";
				} else if (type.equalsIgnoreCase("<id")) {
					statement += " ID < '" + value + "'";
				} else {
					sender.sendMessage(L.get("HYPERLOG_INVALID"));
					return;
				}

			}

			statement += " ORDER BY TIME DESC";
			
			hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
	    		public void run() {
	    			result = getHyperLog(statement);
	    			hc.getServer().getScheduler().runTask(hc, new Runnable() {
	    	    		public void run() {
	    	    			int m = result.size();
	    	    			if (m > 100) {
	    	    				m = 100;
	    	    			}
	    	    			sender.sendMessage(hc.getLanguageFile().get("LINE_BREAK"));
	    	    			for (String message:result) {
	    	    				sender.sendMessage(message);
	    	    			}
	    	    			if (result.size() == 0) {
	    	    				sender.sendMessage(hc.getLanguageFile().get("HYPERLOG_NORESULT"));
	    	    			}
	    	    		}
	    	    	});
	    		}
	    	});
			


		} catch (Exception e) {
			sender.sendMessage(L.get("HYPERLOG_INVALID"));
		}
	}

	/**
	 * This function must be called from an asynchronous thread!
	 * @param statement
	 * @return a display of the selected HyperLog entry
	 */
	private ArrayList<String> getHyperLog(String statement) {
		SQLRead sr = HyperConomy.hc.getSQLRead();
		ArrayList<String> entries = new ArrayList<String>();
		QueryResult result = sr.getDatabaseConnection().read(statement);
		while (result.next()) {
			// int id = result.getInt(1);
			String time = result.getString(2);
			String customer = result.getString(3);
			String action = result.getString(4);
			String object = result.getString(5);
			String amount = result.getString(6);
			double money = result.getDouble(7);
			// double tax = result.getDouble(8);
			String store = result.getString(9);
			// String type = result.getString(10);
			String entry = "";
			time = time.substring(0, time.indexOf(" "));
			time = time.substring(time.indexOf("-") + 1, time.length());
			if (action.equalsIgnoreCase("purchase")) {
				entry = "[" + ChatColor.RED + time + ChatColor.WHITE + "]" + ChatColor.YELLOW + store + ChatColor.WHITE + "->" + ChatColor.AQUA + customer + ChatColor.WHITE + "[" + ChatColor.BLUE + amount + " " + ChatColor.BLUE + object + ChatColor.WHITE + "]" + "[" + ChatColor.GREEN + HyperConomy.currency + money + ChatColor.WHITE + "]";
			} else if (action.equalsIgnoreCase("sale")) {
				entry = "[" + ChatColor.RED + time + ChatColor.WHITE + "]" + ChatColor.AQUA + customer + ChatColor.WHITE + "->" + ChatColor.YELLOW + store + ChatColor.WHITE + "[" + ChatColor.BLUE + amount + " " + ChatColor.BLUE + object + ChatColor.WHITE + "]" + "[" + ChatColor.GREEN + HyperConomy.currency + money + ChatColor.WHITE + "]";
			}
			entries.add(entry);
		}
		result.close();
		return entries;
	}

}
