package regalowl.hyperconomy.api;

import java.util.List;




public interface HEconomyProvider {

	void createAccount(String accountName);
	boolean hasAccount(String accountName);
	double getAccountBalance(String accountName);
	boolean accountHasBalance(String accountName, double amount);
	void setAccountBalance(String accountName, double amount);
	void withdrawAccount(String accountName, double amount);
	void depositAccount(String accountName, double amount);
	void deleteAccount(String accountName);

	
	void createBank(String bankName, String ownerName);
	boolean hasBank(String bankName);
	double getBankBalance(String bankName);
	boolean bankHasBalance(String bankName, double amount);
	void setBankBalance(String bankName, double amount);
	void withdrawBank(String bankName, double amount);
	void depositBank(String bankName, double amount);
	void deleteBank(String bankName);
	boolean isBankOwner(String bankName, String ownerName);
	boolean isBankMember(String bankName, String ownerName);
	List<String> getBanks();
	boolean hasBankSupport();

	
	String getEconomyName();
	boolean isEnabled();
	int fractionalDigits();
	String getAmountAsString(double amount);
	String currencyNameSingular();
	String currencyNamePlural();
	
}
