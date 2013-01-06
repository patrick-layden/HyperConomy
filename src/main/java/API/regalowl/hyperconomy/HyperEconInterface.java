package regalowl.hyperconomy;

import org.bukkit.entity.Player;


public interface HyperEconInterface {

	boolean checkFunds(double money, Player player);
	boolean checkFunds(double money, String name);
	void withdraw(double money, Player player);
	void withdrawAccount(double money, String name);
	void deposit(double money, Player player);
	void depositAccount(double money, String name);
	void withdrawShop(double money);
	void depositShop(double money);
	void setBalance(double balance, String name);
	boolean checkAccount(String name);
	boolean checkshopBalance(double money);
	void checkshopAccount();
	double getBalance(String account);
	boolean createAccount(String account);
	String formatMoney(double amount);

}
