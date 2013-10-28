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
		String statement = "Insert Into hyperconomy_log (TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE)"
		            + " Values (NOW(),'" + playername + "','" + action + "','" + object + "','" + amount + "','" + hc.gCF().twoDecimals(money) + "','" + hc.gCF().twoDecimals(tax) + "','" + store + 
		        "','" + type + "')";
		hc.getSQLWrite().convertExecuteSQL(statement);
	}
	
	public void writeAuditLog(String account, String action, Double amount, String economy) {
		String statement = "Insert Into hyperconomy_audit_log (TIME, ACCOUNT, ACTION, AMOUNT, ECONOMY) Values (NOW(),'" + account + "','" + action + "','" + amount + "','" + economy + "')";
		hc.getSQLWrite().convertExecuteSQL(statement);

	}


  	
}
