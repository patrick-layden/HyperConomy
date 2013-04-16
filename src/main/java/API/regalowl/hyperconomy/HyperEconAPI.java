package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class HyperEconAPI implements EconomyAPI {

	public boolean checkFunds(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		if (!acc.checkAccount(player.getName())) {
			return false;
		}
		return acc.checkFunds(money, player);
	}
	
	public boolean checkFunds(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		if (!acc.checkAccount(name)) {
			return false;
		}
		return acc.checkFunds(money, name);
	}

	public void withdraw(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		if (!acc.checkAccount(player.getName())) {
			return;
		}
		acc.withdraw(money, player);
	}

	public void withdrawAccount(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		if (!acc.checkAccount(name)) {
			return;
		}
		acc.withdrawAccount(money, name);
	}

	public void deposit(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		if (!acc.checkAccount(player.getName())) {
			return;
		}
		acc.deposit(money, player);
	}

	public void depositAccount(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		if (!acc.checkAccount(name)) {
			return;
		}
		acc.depositAccount(money, name);
	}

	public void withdrawShop(double money) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		if (acc.checkshopBalance(money)) {
			acc.withdrawShop(money);
		}
	}

	public void depositShop(double money) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.depositShop(money);
	}

	public void setBalance(double balance, String name) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		if (!acc.checkAccount(name)) {
			return;
		}
		acc.setBalance(balance, name);
	}

	public boolean checkAccount(String name) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		return acc.checkAccount(name);
	}

	public boolean checkshopBalance(double money) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		return acc.checkshopBalance(money);
	}

	public void checkshopAccount() {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.checkshopAccount();
	}

	public double getBalance(String account) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		if (!acc.checkAccount(account)) {
			return 0.0;
		}
		return acc.getBalance(account);
	}

	public boolean createAccount(String account) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		return acc.createAccount(account);
	}
	
	public String formatMoney(double money) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		return (calc.formatMoney(money));
	}
}
