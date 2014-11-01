package regalowl.hyperconomy.api;


import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;

public class HyperEconAPI implements EconomyAPI {

	public boolean checkFunds(double money, HyperPlayer player) {
		HC hc = HC.hc;
		if (!HC.hc.getDataManager().accountExists(player.getName())) {
			return false;
		}
		return HC.hc.getDataManager().getAccount(player.getName()).hasBalance(money);
	}
	
	public boolean checkFunds(double money, String name) {
		HC hc = HC.hc;
		if (!HC.hc.getDataManager().accountExists(name)) {
			return false;
		}
		return HC.hc.getDataManager().getAccount(name).hasBalance(money);
	}

	public void withdraw(double money, HyperPlayer player) {
		HC hc = HC.hc;
		if (!HC.hc.getDataManager().hyperPlayerExists(player.getName())) {
			return;
		}
		player.withdraw(money);
	}

	public void withdrawAccount(double money, String name) {
		HC hc = HC.hc;
		if (!HC.hc.getDataManager().accountExists(name)) {
			return;
		}
		HC.hc.getDataManager().getAccount(name).withdraw(money);
	}

	public void deposit(double money, HyperPlayer player) {
		HC hc = HC.hc;
		if (!HC.hc.getDataManager().hyperPlayerExists(player.getName())) {
			return;
		}
		player.deposit(money);
	}

	public void depositAccount(double money, String name) {
		HC hc = HC.hc;
		if (!HC.hc.getDataManager().accountExists(name)) {
			return;
		}
		HC.hc.getDataManager().getAccount(name).deposit(money);
	}

	public void setBalance(double balance, String name) {
		HC hc = HC.hc;
		if (!HC.hc.getDataManager().accountExists(name)) {
			return;
		}
		HC.hc.getDataManager().getAccount(name).setBalance(balance);
	}

	public boolean checkAccount(String name) {
		HC hc = HC.hc;
		return HC.hc.getDataManager().accountExists(name);
	}

	public double getBalance(String account) {
		HC hc = HC.hc;
		if (!HC.hc.getDataManager().accountExists(account)) {
			return 0.0;
		}
		return HC.hc.getDataManager().getAccount(account).getBalance();
	}

	public boolean createAccount(String account) {
		HC hc = HC.hc;
		if (!HC.hc.getDataManager().accountExists(account)) {
			HyperPlayer hp = hc.getHyperPlayerManager().addPlayer(account);
			if (hp != null) {
				return true;
			}
		}
		return false;
	}
	
	public String formatMoney(double money) {
		HC hc = HC.hc;
		LanguageFile L = hc.getLanguageFile();
		return (L.formatMoney(money));
	}

	public int fractionalDigits() {
		return -1;
	}

	public String currencyName() {
		return "";
	}

	public String currencyNamePlural() {
		return "";
	}
}
