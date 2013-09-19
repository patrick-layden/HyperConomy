package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class HyperEconAPI implements EconomyAPI {

	public boolean checkFunds(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getDataFunctions().hasAccount(player.getName())) {
			return false;
		}
		return hc.getDataFunctions().getHyperPlayer(player.getName()).hasBalance(money);
	}
	
	public boolean checkFunds(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getDataFunctions().hasAccount(name)) {
			return false;
		}
		return hc.getDataFunctions().getHyperPlayer(name).hasBalance(money);
	}

	public void withdraw(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getDataFunctions().hasAccount(player.getName())) {
			return;
		}
		hc.getDataFunctions().getHyperPlayer(player.getName()).withdraw(money);
	}

	public void withdrawAccount(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getDataFunctions().hasAccount(name)) {
			return;
		}
		hc.getDataFunctions().getHyperPlayer(name).withdraw(money);
	}

	public void deposit(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getDataFunctions().hasAccount(player.getName())) {
			return;
		}
		hc.getDataFunctions().getHyperPlayer(player.getName()).deposit(money);
	}

	public void depositAccount(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getDataFunctions().hasAccount(name)) {
			return;
		}
		hc.getDataFunctions().getHyperPlayer(name).deposit(money);
	}

	public void withdrawShop(double money) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getDataFunctions().getGlobalShopAccount().hasBalance(money)) {
			hc.getDataFunctions().getGlobalShopAccount().withdraw(money);
		}
	}

	public void depositShop(double money) {
		HyperConomy hc = HyperConomy.hc;
		hc.getDataFunctions().getGlobalShopAccount().deposit(money);
	}

	public void setBalance(double balance, String name) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getDataFunctions().hasAccount(name)) {
			return;
		}
		hc.getDataFunctions().getHyperPlayer(name).setBalance(balance);
	}

	public boolean checkAccount(String name) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getDataFunctions().hasAccount(name);
	}

	public boolean checkshopBalance(double money) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getDataFunctions().getGlobalShopAccount().hasBalance(money);
	}

	public void checkshopAccount() {
		HyperConomy hc = HyperConomy.hc;
		hc.getDataFunctions().createGlobalShopAccount();
	}

	public double getBalance(String account) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getDataFunctions().hasAccount(account)) {
			return 0.0;
		}
		return hc.getDataFunctions().getHyperPlayer(account).getBalance();
	}

	public boolean createAccount(String account) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getDataFunctions().createPlayerAccount(account);
	}
	
	public String formatMoney(double money) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		return (calc.formatMoney(money));
	}
}
