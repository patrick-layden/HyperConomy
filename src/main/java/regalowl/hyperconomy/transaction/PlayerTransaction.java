package regalowl.hyperconomy.transaction;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.hyperobject.HyperObject;

public class PlayerTransaction {
	
	private TransactionType transactionType;
	private HyperAccount tradePartner;
	private HyperObject hyperObject;
	private int amount;
	private Inventory giveInventory;
	private Inventory receiveInventory;
	private double money;
	private boolean setPrice;
	private boolean chargeTax;
	private ItemStack giveItem;
	
	
	public PlayerTransaction(TransactionType type) {
		transactionType = type;
	}
	
	
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public HyperAccount getTradePartner() {
		return tradePartner;
	}
	public HyperObject getHyperObject() {
		return hyperObject;
	}
	public int getAmount() {
		return amount;
	}
	public Inventory getGiveInventory() {
		return giveInventory;
	}
	public Inventory getReceiveInventory() {
		return receiveInventory;
	}
	public double getMoney() {
		return money;
	}
	public boolean isSetPrice() {
		return setPrice;
	}
	public boolean isChargeTax() {
		return chargeTax;
	}
	public ItemStack getGiveItem() {
		return giveItem;
	}
	
	
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public void setTradePartner(HyperAccount tradePartner) {
		this.tradePartner = tradePartner;
	}
	public void setHyperObject(HyperObject hyperObject) {
		this.hyperObject = hyperObject;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public void setGiveInventory(Inventory giveInventory) {
		this.giveInventory = giveInventory;
	}
	public void setReceiveInventory(Inventory receiveInventory) {
		this.receiveInventory = receiveInventory;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public void setSetPrice(boolean setPrice) {
		this.setPrice = setPrice;
	}
	public void setChargeTax(boolean chargeTax) {
		this.chargeTax = chargeTax;
	}
	public void setGiveItem(ItemStack giveItem) {
		this.giveItem = giveItem;
	}

	

	
	
}
