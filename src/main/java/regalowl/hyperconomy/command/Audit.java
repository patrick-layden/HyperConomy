package regalowl.hyperconomy.command;


import org.bukkit.command.CommandSender;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.SQLRead;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.util.LanguageFile;

public class Audit {
	
	private String account;
	private CommandSender sender;
	private LanguageFile L;
	private HyperConomy hc;
	private double cbalance;
	private double logbalance;
	private double auditbalance;
	private DataManager em;
	
	Audit(String args[], CommandSender csender) {
		sender = csender;
		hc = HyperConomy.hc;
		L = hc.getLanguageFile();
		em = hc.getDataManager();
		try {
			account = args[0];
			if (!em.accountExists(account)) {
				sender.sendMessage(L.get("ACCOUNT_NOT_FOUND"));
				return;
			}

			hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
	    		public void run() {
	    			HyperAccount ha = em.getAccount(account);
	    			account = ha.getName();
	    			cbalance = ha.getBalance();
	    			logbalance = getHyperLogTotal(account, "sale") - getHyperLogTotal(account, "purchase");
	    			auditbalance = getAuditLogTotal(account);
	    			hc.getServer().getScheduler().runTask(hc, new Runnable() {
	    	    		public void run() {
	    	    			CommonFunctions cf = hc.gCF();
	    	    			sender.sendMessage(L.get("LINE_BREAK"));
	    	    			sender.sendMessage(L.f(L.get("AUDIT_TRUE"), cbalance));
	    	    			sender.sendMessage(L.f(L.get("AUDIT_THEORETICAL1"), cf.twoDecimals(logbalance)));
	    	    			sender.sendMessage(L.f(L.get("AUDIT_THEORETICAL2"), cf.twoDecimals(auditbalance)));
	    	    			//sender.sendMessage("True balance: " + cbalance);
	    	    			//sender.sendMessage("Theoretical balance condsidering only sales/purchases: " + calc.twoDecimals(logbalance));
	    	    			//sender.sendMessage("Theoretical balance condsidering all logged balance changes: " + calc.twoDecimals(auditbalance));
	    	    			sender.sendMessage(L.get("LINE_BREAK"));
	    	    		}
	    	    	});
	    		}
	    	});

		} catch (Exception e) {
			sender.sendMessage(L.get("AUDIT_INVALID"));
		}

	}
	
	
	
	/**
	 * This function must be called from an asynchronous thread!
	 * @param account
	 * @param type
	 * @return returns the theoretical amount of money an account should have after all logged (buy or sell individually) transactions in the hyperconomy_log
	 */
	private Double getHyperLogTotal(String account, String type) {
		HyperConomy hc = HyperConomy.hc;
		SQLRead sr = hc.getSQLRead();
		String query = "";
		if (type.equalsIgnoreCase("sale")) {
			query = "SELECT SUM(MONEY) AS total FROM hyperconomy_log WHERE CUSTOMER = '" + account + "' AND ACTION = 'sale'";
		} else if (type.equalsIgnoreCase("purchase")) {
			query = "SELECT SUM(MONEY) AS total FROM hyperconomy_log WHERE CUSTOMER = '" + account + "' AND ACTION = 'purchase'";
		}
		QueryResult result = sr.select(query);
		double amount = 0.0;
		if (result.next()) {
			amount = result.getDouble("total");
		}
		result.close();
		return amount;
	}
	
	
	/**
	 * This function must be called from an asynchronous thread!
	 * @param account
	 * @return returns the theoretical amount of money an account should have after all logged transactions in the hyperconomy_audit_log
	 */
	private Double getAuditLogTotal(String account) {
		HyperConomy hc = HyperConomy.hc;
		SQLRead sr = hc.getSQLRead();
		QueryResult result = sr.select("SELECT * FROM hyperconomy_audit_log WHERE ACCOUNT = '" + account + "' ORDER BY TIME ASC");
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
		return tBalance;
	}
	
}
