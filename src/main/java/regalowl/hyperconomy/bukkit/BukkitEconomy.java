package regalowl.hyperconomy.bukkit;

import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import regalowl.hyperconomy.api.HEconomyProvider;

public class BukkitEconomy implements HEconomyProvider {

	private Economy e;
	
	public BukkitEconomy(Economy e) {
		this.e = e;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void createAccount(String name) {
		if (name == null || name.equals("")) return;
		e.createPlayerAccount(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean hasAccount(String name) {
		if (name == null || name.equals("")) return false;
		return e.hasAccount(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public double getAccountBalance(String accountName) {
		if (accountName == null || accountName.equals("")) return 0;
		return e.getBalance(accountName);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean accountHasBalance(String accountName, double amount) {
		return (e.getBalance(accountName) >= amount) ? true:false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setAccountBalance(String accountName, double balance) {
		if (accountName == null || accountName.equals("")) return;
		e.withdrawPlayer(accountName, e.getBalance(accountName));
		e.depositPlayer(accountName, balance);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void withdrawAccount(String accountName, double amount) {
		if (accountName == null || accountName.equals("")) return;
		e.withdrawPlayer(accountName, amount);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void depositAccount(String accountName, double amount) {
		if (accountName == null || accountName.equals("")) return;
		e.depositPlayer(accountName, amount);
	}

	@Override
	public void deleteAccount(String accountName) {
		//not possible
	}

	@SuppressWarnings("deprecation")
	@Override
	public void createBank(String bankName, String ownerName) {
		if (bankName == null || bankName.equals("")) return;
		if (ownerName == null || ownerName.equals("")) return;
		e.createBank(bankName, ownerName);
	}

	@Override
	public boolean hasBank(String bankName) {
		if (bankName == null || bankName.equals("")) return false;
		EconomyResponse response = e.bankBalance(bankName);
		return (response.type.equals(ResponseType.SUCCESS)) ? true:false;
	}

	@Override
	public double getBankBalance(String bankName) {
		if (bankName == null || bankName.equals("")) return 0;
		EconomyResponse response = e.bankBalance(bankName);
		return response.type.equals(ResponseType.SUCCESS) ? response.balance : 0;
	}

	@Override
	public boolean bankHasBalance(String bankName, double amount) {
		if (bankName == null || bankName.equals("")) return false;
		return (getBankBalance(bankName) >= amount) ? true:false;
	}

	@Override
	public void setBankBalance(String bankName, double balance) {
		if (!hasBank(bankName)) return;
		withdrawBank(bankName, getBankBalance(bankName));
		depositBank(bankName, balance);
	}

	@Override
	public void withdrawBank(String bankName, double amount) {
		if (!hasBank(bankName)) return;
		e.bankWithdraw(bankName, amount);
	}

	@Override
	public void depositBank(String bankName, double amount) {
		if (!hasBank(bankName)) return;
		e.bankDeposit(bankName, amount);
	}

	@Override
	public void deleteBank(String name) {
		if (name == null || name.equals("")) return;
		e.deleteBank(name);
	}

	@Override
	public boolean isBankOwner(String bankName, String playerName) {
		if (bankName == null || bankName.equals("")) return false;
		if (playerName == null || playerName.equals("")) return false;
		@SuppressWarnings("deprecation")
		EconomyResponse response = e.isBankOwner(bankName, playerName);
		return (ResponseType.SUCCESS == response.type) ? true:false;
	}

	@Override
	public boolean isBankMember(String bankName, String playerName) {
		if (bankName == null || bankName.equals("")) return false;
		if (playerName == null || playerName.equals("")) return false;
		@SuppressWarnings("deprecation")
		EconomyResponse response = e.isBankMember(bankName, playerName);
		return (ResponseType.SUCCESS == response.type) ? true:false;
	}

	@Override
	public List<String> getBanks() {
		return e.getBanks();
	}

	@Override
	public boolean hasBankSupport() {
		return e.hasBankSupport();
	}

	@Override
	public String getEconomyName() {
		return e.getName();
	}

	@Override
	public boolean isEnabled() {
		return e.isEnabled();
	}

	@Override
	public int fractionalDigits() {
		return e.fractionalDigits();
	}

	@Override
	public String getAmountAsString(double amount) {
		return e.format(amount);
	}

	@Override
	public String currencyNameSingular() {
		return e.currencyNameSingular();
	}

	@Override
	public String currencyNamePlural() {
		return e.currencyNamePlural();
	}

	
	
}
