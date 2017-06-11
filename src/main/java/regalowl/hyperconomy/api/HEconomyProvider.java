package regalowl.hyperconomy.api;

import java.util.List;




public interface HEconomyProvider {

	public void createAccount(String accountName);
	public boolean hasAccount(String accountName);
	public double getAccountBalance(String accountName);
	public boolean accountHasBalance(String accountName, double amount);
	public void setAccountBalance(String accountName, double amount);
	public void withdrawAccount(String accountName, double amount);
	public void depositAccount(String accountName, double amount);
	public void deleteAccount(String accountName);

	
	public void createBank(String bankName, String ownerName);
	public boolean hasBank(String bankName);
	public double getBankBalance(String bankName);
	public boolean bankHasBalance(String bankName, double amount);
	public void setBankBalance(String bankName, double amount);
	public void withdrawBank(String bankName, double amount);
	public void depositBank(String bankName, double amount);
	public void deleteBank(String bankName);
	public boolean isBankOwner(String bankName, String ownerName);
	public boolean isBankMember(String bankName, String ownerName);
	public List<String> getBanks();
	public boolean hasBankSupport();

	
	public String getEconomyName();
	public boolean isEnabled();
	public int fractionalDigits();
	public String getAmountAsString(double amount);
	public String currencyNameSingular();
	public String currencyNamePlural();
	
}
