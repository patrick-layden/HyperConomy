package regalowl.hyperconomy.command;


import java.util.ArrayList;

import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;

public class Hyperlog extends BaseCommand implements HyperCommand {

	private String statement;
	private ArrayList<String> result;
	
	public Hyperlog(HyperConomy hc) {
		super(hc, false);
	}
	
	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length % 2 != 0 || args.length == 0) {
				data.addResponse(L.get("HYPERLOG_INVALID"));
				return data;
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
						data.addResponse(L.get("HYPERLOG_INVALID_INCREMENT"));
						return data;
					}
					if (hc.gSDL().getSQLManager().useMySQL()) {
						statement += " TIME > DATE_SUB(NOW(), INTERVAL " + quantity + " MINUTE)";
					} else {
						statement += " TIME > date('now','" + formatSQLiteTime(quantity * -1) + " minute')";
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
						data.addResponse(L.get("HYPERLOG_INVALID_INCREMENT"));
						return data;
					}

					if (hc.gSDL().getSQLManager().useMySQL()) {
						statement += " TIME < DATE_SUB(NOW(), INTERVAL " + quantity + " MINUTE)";
					} else {
						statement += " TIME < date('now','" + formatSQLiteTime(quantity * -1) + " minute')";
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
					data.addResponse(L.get("HYPERLOG_INVALID"));
					return data;
				}

			}

			statement += " ORDER BY TIME DESC";
			new Thread(new Runnable() {
	    		public void run() {
	    			result = getHyperLog(statement);
	    			hc.getMC().runTask(new Runnable() {
	    	    		public void run() {
	    	    			int m = result.size();
	    	    			if (m > 100) {
	    	    				m = 100;
	    	    			}
	    	    			hp.sendMessage(hc.getLanguageFile().get("LINE_BREAK"));
	    	    			for (String message:result) {
	    	    				hp.sendMessage(message);
	    	    			}
	    	    			if (result.size() == 0) {
	    	    				hp.sendMessage(hc.getLanguageFile().get("HYPERLOG_NORESULT"));
	    	    			}
	    	    		}
	    	    	});
	    		}
	    	}).start();
		} catch (Exception e) {
			data.addResponse(L.get("HYPERLOG_INVALID"));
		}
		return data;
	}
	

	/**
	 * This function must be called from an asynchronous thread!
	 * @param statement
	 * @return a display of the selected HyperLog entry
	 */
	private ArrayList<String> getHyperLog(String statement) {
		SQLRead sr = hc.getSQLRead();
		ArrayList<String> entries = new ArrayList<String>();
		LanguageFile L = hc.getLanguageFile();
		QueryResult result = sr.select(statement);
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
				entry = "[" + "&c" + time + "&f" + "]" + "&e" + store + "&f" + "->" + "&b" + customer + "&f" + "[" + "&9" + amount + " " + "&9" + object + "&f" + "]" + "[" + "&a" + L.fC(money) + "&f" + "]";
			} else if (action.equalsIgnoreCase("sale")) {
				entry = "[" + "&c" + time + "&f" + "]" + "&b" + customer + "&f" + "->" + "&e" + store + "&f" + "[" + "&9" + amount + " " + "&9" + object + "&f" + "]" + "[" + "&a" + L.fC(money) + "&f" + "]";
			}
			entries.add(entry);
		}
		result.close();
		return entries;
	}
	
	public String formatSQLiteTime(int time) {
		if (time < 0) {
			return "-" + Math.abs(time);
		} else if (time > 0) {
			return "+" + time;
		} else {
			return "0";
		}
	}



}
