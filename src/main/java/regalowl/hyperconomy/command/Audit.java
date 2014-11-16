package regalowl.hyperconomy.command;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;

public class Audit extends BaseCommand implements HyperCommand {
	
	private String account;
	private double cbalance;
	private double logbalance;
	private double auditbalance;
	private HyperPlayer hp;
	
	public Audit(HyperConomy hc) {
		super(hc, false);

	}
	

	@Override
	public CommandData onCommand(CommandData d) {
		this.data = d;
		if (!validate(data)) return data;
		hp = null;
		if (data.isPlayer()) {
			hp = data.getHyperPlayer();
		}
		try {
			if (args.length < 1) {
				data.addResponse(L.get("AUDIT_INVALID"));
				return data;
			}
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
	    			hc.getMC().runTask(new Runnable() {
	    	    		public void run() {
	    	    			if (hp == null) {
	    	    				hc.getMC().logInfo(L.get("LINE_BREAK"));
	    	    				hc.getMC().logInfo(L.f(L.get("AUDIT_TRUE"), cbalance));
	    	    				hc.getMC().logInfo(L.f(L.get("AUDIT_THEORETICAL1"), CommonFunctions.twoDecimals(logbalance)));
	    	    				hc.getMC().logInfo(L.f(L.get("AUDIT_THEORETICAL2"), CommonFunctions.twoDecimals(auditbalance)));
	    	    				hc.getMC().logInfo(L.get("LINE_BREAK"));
	    	    			} else {
		    	    			hp.sendMessage(L.get("LINE_BREAK"));
		    	    			hp.sendMessage(L.f(L.get("AUDIT_TRUE"), cbalance));
		    	    			hp.sendMessage(L.f(L.get("AUDIT_THEORETICAL1"), CommonFunctions.twoDecimals(logbalance)));
		    	    			hp.sendMessage(L.f(L.get("AUDIT_THEORETICAL2"), CommonFunctions.twoDecimals(auditbalance)));
		    	    			hp.sendMessage(L.get("LINE_BREAK"));
	    	    			}

	    	    		}
	    	    	});
	    		}
	    	}).start();
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
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
