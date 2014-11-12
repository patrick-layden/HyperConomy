package regalowl.hyperconomy.util;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HC;



public class Log {
	
	public void writeSQLLog(String playername, String action, String object, Double amount, Double money, Double tax, String store, String type) {
		HC hc = HC.hc;
		String statement = "Insert Into hyperconomy_log (TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE)"
		            + " Values (NOW(),'" + playername + "','" + action + "','" + object + "','" + amount + "','" + CommonFunctions.twoDecimals(money) + "','" + CommonFunctions.twoDecimals(tax) + "','" + store + 
		        "','" + type + "')";
		hc.getSQLWrite().addToQueue(statement);
	}
	
	public void writeAuditLog(String account, String action, Double amount, String economy) {
		HC hc = HC.hc;
		String statement = "Insert Into hyperconomy_audit_log (TIME, ACCOUNT, ACTION, AMOUNT, ECONOMY) Values (NOW(),'" + account + "','" + action + "','" + amount + "','" + economy + "')";
		hc.getSQLWrite().addToQueue(statement);
	}


  	
}
