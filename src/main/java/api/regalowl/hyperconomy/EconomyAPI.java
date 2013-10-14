package regalowl.hyperconomy;

import org.bukkit.entity.Player;


public interface EconomyAPI {
	boolean checkFunds(double money, Player player);
	boolean checkFunds(double money, String name);
	void withdraw(double money, Player player);
	void withdrawAccount(double money, String name);
	void deposit(double money, Player player);
	void depositAccount(double money, String name);
	void setBalance(double balance, String name);
	boolean checkAccount(String name);
	double getBalance(String account);
	boolean createAccount(String account);
	String formatMoney(double amount);
	int fractionalDigits();
	String currencyName();
	String currencyNamePlural();

	
	@Deprecated
	boolean checkshopBalance(double money);
	@Deprecated
	void checkshopAccount();
	@Deprecated
	void withdrawShop(double money);
	@Deprecated
	void depositShop(double money);
}
