package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class HyperEconAPI implements EconomyAPI {

	public boolean checkFunds(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().hyperPlayerExists(player.getName())) {
			return false;
		}
		return hc.getEconomyManager().getHyperPlayer(player.getName()).hasBalance(money);
	}
	
	public boolean checkFunds(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().hyperPlayerExists(name)) {
			return false;
		}
		return hc.getEconomyManager().getHyperPlayer(name).hasBalance(money);
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
		if (!hc.getEconomyManager().hyperPlayerExists(name)) {
			return;
		}
		hc.getEconomyManager().getHyperPlayer(name).withdraw(money);
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
		if (!hc.getEconomyManager().hyperPlayerExists(name)) {
			return;
		}
		hc.getEconomyManager().getHyperPlayer(name).deposit(money);
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
		if (!hc.getEconomyManager().hyperPlayerExists(name)) {
			return;
		}
		hc.getEconomyManager().getHyperPlayer(name).setBalance(balance);
	}

	public boolean checkAccount(String name) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getEconomyManager().hyperPlayerExists(name);
	}

	public boolean checkshopBalance(double money) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getEconomyManager().getGlobalShopAccount().hasBalance(money);
	}

	public void checkshopAccount() {
		HyperConomy hc = HyperConomy.hc;
		hc.getEconomyManager().getEconomy("default").createGlobalShopAccount();
	}

	public double getBalance(String account) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getEconomyManager().hyperPlayerExists(account)) {
			return 0.0;
		}
		return hc.getEconomyManager().getHyperPlayer(account).getBalance();
	}

	public boolean createAccount(String account) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getEconomyManager().getEconomy("default").createPlayerAccount(account);
	}
	
	public String formatMoney(double money) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		return (calc.formatMoney(money));
	}
}
