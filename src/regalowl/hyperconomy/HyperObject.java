package regalowl.hyperconomy;

import net.milkbowl.vault.economy.Economy;

public class HyperObject {
	
	private HyperConomy hc;
	private YamlFile yaml;
	private Transaction tran;
	private Calculation calc;
	private ETransaction ench;
	private Message m;
	private Log l;
	private Shop s;
	private Account acc;
	private InfoSign isign;
	private Cmd commandhandler;
	private History hist;
	private Notify not;
	private TransactionSign tsign;
	private Economy economy;
	
	
	HyperObject(HyperConomy hyperc, YamlFile yam, Transaction trans, Calculation cal, ETransaction enchant, Message mess, 
			Log log, Shop shop, Account account, InfoSign infosign, Cmd cmd, History history, Notify notify, TransactionSign transign, Economy econ) {
		
		hc = hyperc;
		yaml = yam;
		tran = trans;
		calc = cal;
		ench = enchant;
		m = mess;
		l = log;
		s = shop;
		acc = account;
		isign = infosign;
		commandhandler = cmd;
		hist = history;
		not = notify;
		tsign = transign;
		economy = econ;
				
		
	}
	
	
	public HyperConomy getHyperConomy() {
		return hc;
	}
	
	public YamlFile getYamlFile() {
		return yaml;
	}
	
	public Transaction getTransaction() {
		return tran;
	}
	
	public Calculation getCalculation() {
		return calc;
	}
	
	public ETransaction getETransaction() {
		return ench;
	}
	
	public Message getMessage() {
		return m;
	}
	
	public Log getLog() {
		return l;
	}
	
	public Shop getShop() {
		return s;
	}
	
	public Account getAccount() {
		return acc;
	}
	
	public InfoSign getInfoSign() {
		return isign;
	}
	
	public Cmd getCmd() {
		return commandhandler;
	}
	
	public History getHistory() {
		return hist;
	}
	
	public Notify getNotify() {
		return not;
	}
	
	public TransactionSign getTransactioSign() {
		return tsign;
	}
	
	public Economy getEconomy() {
		return economy;
	}
}
