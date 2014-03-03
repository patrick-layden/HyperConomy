package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class HyperEconAPI implements EconomyAPI {

	public boolean checkFunds(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().accountExists(player.getName())) {
			return false;
		}
		return hc.getEconomyManager().getAccount(player.getName()).hasBalance(money);
	}
	
	public boolean checkFunds(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().accountExists(name)) {
			return false;
		}
		return hc.getEconomyManager().getAccount(name).hasBalance(money);
	}

	public void withdraw(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().hyperPlayerExists(player.getName())) {
			return;
		}
		hc.getEconomyManager().getHyperPlayer(player.getName()).withdraw(money);
	}

	public void withdrawAccount(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().accountExists(name)) {
			return;
		}
		hc.getEconomyManager().getAccount(name).withdraw(money);
	}

	public void deposit(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().hyperPlayerExists(player.getName())) {
			return;
		}
		hc.getEconomyManager().getHyperPlayer(player.getName()).deposit(money);
	}

	public void depositAccount(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().accountExists(name)) {
			return;
		}
		hc.getEconomyManager().getAccount(name).deposit(money);
	}

	public void withdrawShop(double money) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().getGlobalShopAccount().hasBalance(money)) {
			hc.getEconomyManager().getGlobalShopAccount().withdraw(money);
		}
	}

	public void depositShop(double money) {
		HyperConomy hc = HyperConomy.hc;
		hc.getEconomyManager().getGlobalShopAccount().deposit(money);
	}

	public void setBalance(double balance, String name) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().accountExists(name)) {
			return;
		}
		hc.getEconomyManager().getAccount(name).setBalance(balance);
	}

	public boolean checkAccount(String name) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getEconomyManager().accountExists(name);
	}

	public boolean checkshopBalance(double money) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getEconomyManager().getGlobalShopAccount().hasBalance(money);
	}

	public void checkshopAccount() {
		HyperConomy hc = HyperConomy.hc;
		hc.getEconomyManager().createGlobalShopAccount();
	}

	public double getBalance(String account) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().accountExists(account)) {
			return 0.0;
		}
		return hc.getEconomyManager().getAccount(account).getBalance();
	}

	public boolean createAccount(String account) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().accountExists(account)) {
			HyperPlayer hp = hc.getEconomyManager().addPlayer(account);
			if (hp != null) {
				return true;
			}
		}
		return false;
	}
	
	public String formatMoney(double money) {
		HyperConomy hc = HyperConomy.hc;
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
