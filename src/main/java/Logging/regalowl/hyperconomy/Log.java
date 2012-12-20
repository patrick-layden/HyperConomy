package regalowl.hyperconomy;



import java.util.Date;
import org.bukkit.configuration.file.FileConfiguration;


/**
 * 
 * 
 * This class logs all transactions to log.txt and lists all items/enchantments on command.
 * 
 */
public class Log {
	
	
	private HyperConomy hc;
	private int logsize;

	
	Log(HyperConomy hyperc) {
		hc = hyperc;
    	logsize = hc.getYaml().getLog().getKeys(false).size();
	}
	
	
	public void writeSQLLog(String playername, String action, String object, Double amount, Double money, Double tax, String store, String type) {
		String statement = "Insert Into hyperlog (TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE)"
	            + " Values (NOW(),'" + playername + "','" + action + "','" + object + "','" + amount + "','" + hc.getCalculation().twoDecimals(money) + "','" + hc.getCalculation().twoDecimals(tax) + "','" + store + 
	        "','" + type + "')";
		hc.getSQLWrite().writeData(statement);
	}
	
	public void writeAuditLog(String account, String action, Double amount, String economy) {
		if (hc.useSQL()) {
			String statement = "Insert Into hyperauditlog (TIME, ACCOUNT, ACTION, AMOUNT, ECONOMY) Values (NOW(),'" + account + "','" + action + "','" + amount + "','" + economy + "')";
			hc.getSQLWrite().writeData(statement);
		} else {
			String entry = "[Audit] " + "Account = " + account + " Action = " + action + " Amount = " + amount + " Type = " + economy;
			writeLog(entry);
		}
	}

  	public void writeLog(String entry) {
  		Date currentDate = new Date();
  		FileConfiguration l = hc.getYaml().getLog();
		l.set("["+ logsize + "] " + currentDate.toString(), entry);
		logsize++;  		
  	}
  	
    public int getlogSize() {
    	return logsize;
    }
  	
}
