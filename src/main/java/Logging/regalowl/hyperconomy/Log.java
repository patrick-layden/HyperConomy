package regalowl.hyperconomy;





/**
 * 
 * 
 * 
 * 
 */
public class Log {
	
	
	private HyperConomy hc;

	
	Log(HyperConomy hyperc) {
		hc = hyperc;
	}
	
	
	public void writeSQLLog(String playername, String action, String object, Double amount, Double money, Double tax, String store, String type) {
		String statement = "";
		if (hc.useMySQL()) {
			statement = "Insert Into hyperconomy_log (TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE)"
		            + " Values (NOW(),'" + playername + "','" + action + "','" + object + "','" + amount + "','" + hc.getCalculation().twoDecimals(money) + "','" + hc.getCalculation().twoDecimals(tax) + "','" + store + 
		        "','" + type + "')";
		} else {
			statement = "Insert Into hyperconomy_log (TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE)"
		            + " Values (datetime('NOW', 'localtime'),'" + playername + "','" + action + "','" + object + "','" + amount + "','" + hc.getCalculation().twoDecimals(money) + "','" + hc.getCalculation().twoDecimals(tax) + "','" + store + 
		        "','" + type + "')";
		}
		hc.getSQLWrite().executeSQL(statement);
	}
	
	public void writeAuditLog(String account, String action, Double amount, String economy) {
		String statement = "";
		if (hc.useMySQL()) {
			statement = "Insert Into hyperconomy_audit_log (TIME, ACCOUNT, ACTION, AMOUNT, ECONOMY) Values (NOW(),'" + account + "','" + action + "','" + amount + "','" + economy + "')";
		} else {
			statement = "Insert Into hyperconomy_audit_log (TIME, ACCOUNT, ACTION, AMOUNT, ECONOMY) Values (datetime('NOW', 'localtime'),'" + account + "','" + action + "','" + amount + "','" + economy + "')";
		}
		hc.getSQLWrite().executeSQL(statement);

	}


  	
}
