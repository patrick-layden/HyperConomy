package regalowl.hyperconomy.transaction;

import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;

public class PlayerTransaction {
	
	private TransactionType transactionType;
	private HyperAccount tradePartner;
	private HyperObject hyperObject;
	private int amount;
	private SerializableInventory giveInventory;
	private SerializableInventory receiveInventory;
	private double money;
	private boolean setPrice;
	private boolean chargeTax;
	private SerializableItemStack giveItem;
	private boolean obeyShops;
	
	
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
	public SerializableInventory getGiveInventory() {
		return giveInventory;
	}
	public SerializableInventory getReceiveInventory() {
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
	public SerializableItemStack getGiveItem() {
		return giveItem;
	}
	public boolean obeyShops() {
		return obeyShops;
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
	public void setGiveInventory(SerializableInventory giveInventory) {
		this.giveInventory = giveInventory;
	}
	public void setReceiveInventory(SerializableInventory receiveInventory) {
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
	public void setGiveItem(SerializableItemStack giveItem) {
		this.giveItem = giveItem;
	}
	public void setObeyShops(boolean obeyShops) {
		this.obeyShops = obeyShops;
	}

	

	
	
}
