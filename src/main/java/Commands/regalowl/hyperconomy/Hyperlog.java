package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Hyperlog {
	
	Hyperlog(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		try {
			if (hc.useSQL()) {
				if (args.length % 2 != 0 || args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /hyperlog [Please read about this command's arguments on bukkit.]");
					return;
				}
				
				String statement = "SELECT * FROM hyperlog WHERE";
				for (int i = 0; i < args.length; i+=2) {
					
					String type = args[i];
					String value = args[i+1];
					
					if (i >= 2) {
						statement += " AND";
					}
					
					if (type.equalsIgnoreCase("player") || type.equalsIgnoreCase("p")) {
						statement += " CUSTOMER LIKE '%" + value + "%'";
					} else if (type.equalsIgnoreCase("since") || type.equalsIgnoreCase("s")) {
						String increment = value.substring(value.length() - 1, value.length());
						Integer quantity = Integer.parseInt(value.substring(0, value.length() - 1));
						if (increment.equalsIgnoreCase("m")) {
							//do nothing
						} else if (increment.equalsIgnoreCase("h")) {
							quantity = quantity * 60;
						} else if (increment.equalsIgnoreCase("d")) {
							quantity = quantity * 60 * 24;
						} else {
							sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Valid time increments are 'm', 'h', and 'd' such as '3d' or '1h'");
							return;
						}
						statement += " TIME > DATE_SUB(NOW(), INTERVAL " + quantity + " MINUTE)";
					} else if (type.equalsIgnoreCase("before") || type.equalsIgnoreCase("b")) {
						String increment = value.substring(value.length() - 1, value.length());
						Integer quantity = Integer.parseInt(value.substring(0, value.length() - 1));
						if (increment.equalsIgnoreCase("m")) {
							//do nothing
						} else if (increment.equalsIgnoreCase("h")) {
							quantity = quantity * 60;
						} else if (increment.equalsIgnoreCase("d")) {
							quantity = quantity * 60 * 24;
						} else {
							sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Valid time increments are 'm', 'h', and 'd' such as '3d' or '1h'");
							return;
						}
						statement += " TIME < DATE_SUB(NOW(), INTERVAL " + quantity + " MINUTE)";
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
						sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /hyperlog [Please read about this command's arguments on bukkit.]");
						return;
					}

				}
				
				statement += " ORDER BY TIME DESC";
				SQLFunctions sf = hc.getSQLFunctions();
				ArrayList<String> result = sf.getHyperLog(statement);
				//sender.sendMessage(ChatColor.RED + statement);
				
				int m = result.size();
				if (m > 100) {
					m = 100;
				}
				for (int k = 0; k < m; k++) {
					sender.sendMessage(result.get(k));
				}

			} else {
				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /hyperlog [Please read about this command's arguments on bukkit.]");
		}
	}
	
	
	
	
	
}
