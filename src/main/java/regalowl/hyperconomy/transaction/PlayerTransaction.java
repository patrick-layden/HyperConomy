package regalowl.hyperconomy.transaction;

import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class PlayerTransaction {
	
	private TransactionType transactionType;
	private HyperAccount tradePartner;
	private TradeObject hyperObject;
	private int amount;
	private HInventory giveInventory;
	private HInventory receiveInventory;
	private double money;
	private boolean setPrice;
	private boolean chargeTax;
	//private HItemStack giveItem;
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
	public TradeObject getHyperObject() {
		return hyperObject;
	}
	public int getAmount() {
		return amount;
	}
	public HInventory getGiveInventory() {
		return giveInventory;
	}
	public HInventory getReceiveInventory() {
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
	//public HItemStack getGiveItem() {
	//	return giveItem;
	//}
	public boolean obeyShops() {
		return obeyShops;
	}
	
	
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public void setTradePartner(HyperAccount tradePartner) {
		this.tradePartner = tradePartner;
	}
	public void setHyperObject(TradeObject hyperObject) {
		this.hyperObject = hyperObject;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public void setGiveInventory(HInventory giveInventory) {
		this.giveInventory = giveInventory;
	}
	public void setReceiveInventory(HInventory receiveInventory) {
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
	//public void setGiveItem(HItemStack giveItem) {
	//	this.giveItem = giveItem;
	//}
	public void setObeyShops(boolean obeyShops) {
		this.obeyShops = obeyShops;
	}

	

	
	
}
