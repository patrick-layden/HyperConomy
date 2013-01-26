package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Audit {
	
	Audit(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Account acc = hc.getAccount();
		try {
			if (hc.useSQL()) {
				String account = args[0];
				if (!acc.checkAccount(account)) {
					sender.sendMessage(L.get("ACCOUNT_NOT_FOUND"));
					return;
				}
				sender.sendMessage(L.get("LINE_BREAK"));
				double cbalance = acc.getBalance(account);
				sender.sendMessage("Current Balance: " + cbalance);
				double logbalance = getHyperLogTotal(account, "sale") - getHyperLogTotal(account, "purchase");
				sender.sendMessage("Theoretical Balance condsidering sales/purchases: " + logbalance);
				double auditbalance = getAuditLogTotal(account);
				sender.sendMessage("Theoretical Balance condsidering everything: " + auditbalance);
				sender.sendMessage(L.get("LINE_BREAK"));
			} else {
				sender.sendMessage(L.get("ONLY_AVAILABLE_SQL"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("AUDIT_INVALID"));
		}
	}
	
	
	
	public Double getHyperLogTotal(String account, String type) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions df = hc.getDataFunctions();
		String query = "";
		if (type.equalsIgnoreCase("sale")) {
			query = "SELECT SUM(MONEY) AS total FROM hyperlog WHERE CUSTOMER = '" + account + "' AND ACTION = 'sale'";
		} else if (type.equalsIgnoreCase("purchase")) {
			query = "SELECT SUM(MONEY) AS total FROM hyperlog WHERE CUSTOMER = '" + account + "' AND ACTION = 'purchase'";
		}
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + df.getHost() + ":" + df.getPort() + "/" + df.getDatabase(), df.getUserName(), df.getPassword());
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(query);
			double amount = 0.0;
			if (result.next()) {
				amount = result.getDouble("total");
			}
			result.close();
			state.close();
			connect.close();
			return amount;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return 0.0;
		}
	}
	
	
	public Double getAuditLogTotal(String account) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions df = hc.getDataFunctions();
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + df.getHost() + ":" + df.getPort() + "/" + df.getDatabase(), df.getUserName(), df.getPassword());
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery("SELECT * FROM hyperauditlog WHERE ACCOUNT = '" + account + "' ORDER BY TIME ASC");
			double tBalance = 0.0;
			//double lastSetBalance = -1;
			while (result.next()) {
				String action = result.getString("ACTION");
				double amount = result.getDouble("AMOUNT");
				if (action.equalsIgnoreCase("deposit")) {
					tBalance += amount;
				} else if (action.equalsIgnoreCase("withdrawal")) {
					tBalance -= amount;
				} else if (action.equalsIgnoreCase("setbalance")) {
					tBalance = amount;
				} else if (action.equalsIgnoreCase("initialization")) {
					tBalance = amount;
				}
			}
			result.close();
			state.close();
			connect.close();
			return tBalance;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return 0.0;
		}
	}
	
}
