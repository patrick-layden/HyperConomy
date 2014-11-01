package regalowl.hyperconomy.command;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.SQLRead;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperAccount;

public class Audit extends BaseCommand implements HyperCommand {
	
	private String account;
	private double cbalance;
	private double logbalance;
	private double auditbalance;
	
	public Audit() {
		super(false);

	}
	

	@Override
	public CommandData onCommand(CommandData d) {
		this.data = d;
		if (!validate(data)) return data;
		
		try {
			account = args[0];
			if (!dm.accountExists(account)) {
				data.addResponse(L.get("ACCOUNT_NOT_FOUND"));
				return data;
			}
			new Thread(new Runnable() {
	    		public void run() {
	    			HyperAccount ha = dm.getAccount(account);
	    			account = ha.getName();
	    			cbalance = ha.getBalance();
	    			logbalance = getHyperLogTotal(account, "sale") - getHyperLogTotal(account, "purchase");
	    			auditbalance = getAuditLogTotal(account);
	    			HC.mc.runTask(new Runnable() {
	    	    		public void run() {
	    	    			data.addResponse(L.get("LINE_BREAK"));
	    	    			data.addResponse(L.f(L.get("AUDIT_TRUE"), cbalance));
	    	    			data.addResponse(L.f(L.get("AUDIT_THEORETICAL1"), CommonFunctions.twoDecimals(logbalance)));
	    	    			data.addResponse(L.f(L.get("AUDIT_THEORETICAL2"), CommonFunctions.twoDecimals(auditbalance)));
	    	    			data.addResponse(L.get("LINE_BREAK"));
	    	    		}
	    	    	});
	    		}
	    	}).start();
		} catch (Exception e) {
			data.addResponse(L.get("AUDIT_INVALID"));
		}
		return data;
	}
	
	
	
	/**
	 * This function must be called from an asynchronous thread!
	 * @param account
	 * @param type
	 * @return returns the theoretical amount of money an account should have after all logged (buy or sell individually) transactions in the hyperconomy_log
	 */
	private Double getHyperLogTotal(String account, String type) {
		HC hc = HC.hc;
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
		HC hc = HC.hc;
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
