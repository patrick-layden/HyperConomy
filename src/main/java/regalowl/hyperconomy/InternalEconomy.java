package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.List;

import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperBankManager;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.account.HyperPlayerManager;
import regalowl.hyperconomy.api.HEconomyProvider;
import regalowl.hyperconomy.util.LanguageFile;

public class InternalEconomy implements HEconomyProvider {

	private transient HyperConomy hc;
	
	public InternalEconomy(HyperConomy hc) {
		this.hc = hc;
	}
	
	private DataManager dm() {
		return hc.getDataManager();
	}
	private HyperPlayerManager hpm() {
		return dm().getHyperPlayerManager();
	}
	private HyperBankManager hbm() {
		return dm().getHyperBankManager();
	}

	@Override
	public void createAccount(String name) {
		hpm().addPlayer(name);
	}

	@Override
	public boolean hasAccount(String name) {
		return dm().accountExists(name);
	}

	@Override
	public double getAccountBalance(String accountName) {
		if (!dm().accountExists(accountName)) return 0;
		return dm().getAccount(accountName).getBalance();
	}

	@Override
	public boolean accountHasBalance(String accountName, double amount) {
		if (!dm().accountExists(accountName)) return false;
		return dm().getAccount(accountName).hasBalance(amount);
	}

	@Override
	public void setAccountBalance(String accountName, double balance) {
		if (!dm().accountExists(accountName)) return;
		dm().getAccount(accountName).setBalance(balance);
	}

	@Override
	public void withdrawAccount(String accountName, double amount) {
		if (!dm().accountExists(accountName)) return;
		dm().getAccount(accountName).withdraw(amount);
	}

	@Override
	public void depositAccount(String accountName, double amount) {
		if (!dm().accountExists(accountName)) return;
		dm().getAccount(accountName).deposit(amount);
	}

	@Override
	public void deleteAccount(String accountName) {
		if (!hpm().hyperPlayerExists(accountName)) return;
		hpm().getHyperPlayer(accountName).delete();
	}

	@Override
	public void createBank(String bankName, String ownerName) {
		if (hbm().hasBank(bankName)) return;
		if (!hpm().hyperPlayerExists(ownerName)) return;
		HyperPlayer hp = hpm().getHyperPlayer(ownerName);
		hbm().addHyperBank(new HyperBank(hc, ownerName, hp));
	}

	@Override
	public boolean hasBank(String bankName) {
		return hbm().hasBank(bankName);
	}

	@Override
	public double getBankBalance(String bankName) {
		if (!hbm().hasBank(bankName)) return 0;
		return hbm().getHyperBank(bankName).getBalance();
	}

	@Override
	public boolean bankHasBalance(String bankName, double amount) {
		if (!hbm().hasBank(bankName)) return false;
		return hbm().getHyperBank(bankName).hasBalance(amount);
	}

	@Override
	public void setBankBalance(String bankName, double amount) {
		if (!hbm().hasBank(bankName)) return;
		hbm().getHyperBank(bankName).setBalance(amount);
	}

	@Override
	public void withdrawBank(String bankName, double amount) {
		if (!hbm().hasBank(bankName)) return;
		hbm().getHyperBank(bankName).withdraw(amount);
	}

	@Override
	public void depositBank(String bankName, double amount) {
		if (!hbm().hasBank(bankName)) return;
		hbm().getHyperBank(bankName).deposit(amount);
	}

	@Override
	public void deleteBank(String bankName) {
		if (!hbm().hasBank(bankName)) return;
		hbm().getHyperBank(bankName).delete();
	}

	@Override
	public boolean isBankOwner(String bankName, String playerName) {
		if (!hbm().hasBank(bankName)) return false;
		if (!hpm().hyperPlayerExists(playerName)) return false;
		HyperPlayer hp = hpm().getHyperPlayer(playerName);
		return hbm().getHyperBank(bankName).isOwner(hp);
	}

	@Override
	public boolean isBankMember(String bankName, String playerName) {
		if (!hbm().hasBank(bankName)) return false;
		if (!hpm().hyperPlayerExists(playerName)) return false;
		HyperPlayer hp = hpm().getHyperPlayer(playerName);
		return hbm().getHyperBank(bankName).isMember(hp);
	}

	@Override
	public List<String> getBanks() {
		ArrayList<HyperBank> allBanks = hbm().getHyperBanks();
		ArrayList<String> bankString = new ArrayList<String>();
		for (HyperBank hb:allBanks) {
			bankString.add(hb.getName());
		}
		return bankString;
	}

	@Override
	public boolean hasBankSupport() {
		return true;
	}

	@Override
	public String getEconomyName() {
		return "HyperConomy";
	}

	@Override
	public boolean isEnabled() {
		return hc.loaded();
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public String getAmountAsString(double amount) {
		LanguageFile L = hc.getLanguageFile();
		return L.formatMoney(amount);
	}

	@Override
	public String currencyNameSingular() {
		return "";
	}

	@Override
	public String currencyNamePlural() {
		return "";
	}
	
}
