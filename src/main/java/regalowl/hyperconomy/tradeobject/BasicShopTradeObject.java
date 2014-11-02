package regalowl.hyperconomy.tradeobject;

import java.awt.Image;
import java.util.ArrayList;

import regalowl.databukkit.sql.SQLWrite;
import regalowl.databukkit.sql.WriteStatement;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperObjectModificationEvent;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.PlayerShop;

public class BasicShopTradeObject extends BasicTradeObject implements TradeObject {

	private static final long serialVersionUID = -8506945265990355676L;
	protected String playerShop;
	protected String hyperObject;
	protected String economy;
	protected double stock;
	protected double buyPrice;
	protected double sellPrice;
	protected TradeObjectStatus status;
	protected int maxStock;
	protected boolean useEconomyStock;
	
	public BasicShopTradeObject(String playerShop, TradeObject ho, double stock, double buyPrice, double sellPrice, int maxStock, TradeObjectStatus status, boolean useEconomyStock) {
		this.playerShop = playerShop;
		this.hyperObject = ho.getName();
		this.economy = ho.getEconomy();
		this.stock = stock;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.maxStock = maxStock;
		this.status = status;
		this.useEconomyStock = useEconomyStock;
	}
	

	@Override
	public PlayerShop getShop() {
		HC hc = HC.hc;
		return (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
	}
	@Override
	public TradeObject getTradeObject() {
		TradeObject ho = HC.hc.getDataManager().getEconomy(economy).getHyperObject(hyperObject);
		return ho;
	}
	@Override
	public double getStock() {
		if (useEconomyStock) {
			return getTradeObject().getStock();
		} else {
			return stock;
		}
	}
	@Override
	public double getBuyPrice() {
		return buyPrice;
	}
	@Override
	public double getSellPrice() {
		return sellPrice;
	}
	@Override
	public TradeObjectStatus getStatus() {
		return status;
	}
	@Override
	public int getMaxStock() {
		return maxStock;
	}
	@Override
	public boolean useEconomyStock() {
		return useEconomyStock;
	}
	@Override
	public void setTradeObject(TradeObject ho) {
		HC hc = HC.hc;
		SQLWrite sw = hc.getSQLWrite();
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET HYPEROBJECT=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(ho.getName());
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setShop(PlayerShop playerShop) {
		HC hc = HC.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.playerShop = playerShop.getName();
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET SHOP=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(playerShop.getName());
		ws.addParameter(playerShop.getName());
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setStock(double stock) {
		HC hc = HC.hc;
		SQLWrite sw = hc.getSQLWrite();
		if (useEconomyStock) {
			getTradeObject().setStock(stock);
		} else {
			if (stock < 0.0) {stock = 0.0;}
			this.stock = stock;
			WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET QUANTITY=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
			ws.addParameter(stock);
			ws.addParameter(playerShop);
			ws.addParameter(hyperObject);
			sw.addToQueue(ws);
			hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
		}
	}
	@Override
	public void setBuyPrice(double buyPrice) {
		HC hc = HC.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.buyPrice = buyPrice;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET BUY_PRICE=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(buyPrice);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setSellPrice(double sellPrice) {
		HC hc = HC.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.sellPrice = sellPrice;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET SELL_PRICE=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(sellPrice);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setMaxStock(int maxStock) {
		HC hc = HC.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.maxStock = maxStock;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET MAX_STOCK=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(maxStock);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setStatus(TradeObjectStatus status) {
		HC hc = HC.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.status = status;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET STATUS=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(status.toString());
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setUseEconomyStock(boolean state) {
		this.useEconomyStock = state;
	}
	
	
	



	@Override
	public double getSellPriceWithTax(double amount, HyperPlayer hp) {
		return getSellPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		return getSellPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getBuyPriceWithTax(double amount) {
		return getBuyPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getBuyPrice(double amount) {
		if (getBuyPrice() != 0.0) {
			return getBuyPrice() * amount;
		} else {
			return getTradeObject().getBuyPrice(amount);
		}
	}
	@Override
	public double getSellPrice(double amount) {
		if (getSellPrice() != 0.0) {
			return getSellPrice() * amount;
		} else {
			return getTradeObject().getSellPrice(amount);
		}
	}
	@Override
	public boolean isShopObject() {return true;}
	
	@Override
	public void checkInitiationStatus() {
		getTradeObject().checkInitiationStatus();
	}
	
	
	
	//The following methods don't apply to PlayerShop objects and have been overridden to prevent database changes.
	@Override
	public double applyCeilingFloor(double value, double quantity) {return value;}
	@Override
	public void setName(String name) {}
	@Override
	public void setEconomy(String economy) {}
	@Override
	public void setDisplayName(String displayName) {}
	@Override
	public void setAliases(ArrayList<String> newAliases) {}
	@Override
	public void addAlias(String addAlias) {}
	@Override
	public void removeAlias(String removeAlias) {}
	@Override
	public void setType(TradeObjectType type) {}
	@Override
	public void setValue(double value) {}
	@Override
	public void setIsstatic(String isstatic) {}
	@Override
	public void setStaticprice(double staticprice) {}
	@Override
	public void setMedian(double median) {}
	@Override
	public void setInitiation(String initiation) {}
	@Override
	public void setStartprice(double startprice) {}
	@Override
	public void setCeiling(double ceiling) {}
	@Override
	public void setFloor(double floor) {}
	@Override
	public void setMaxstock(double maxstock) {}
	@Override
	public double getPurchaseTax(double cost) {return 0;}
	@Override
	public double getSalesTaxEstimate(double value) {return 0;}
	@Override
	public void delete() {}



	
	
	//The following methods don't apply to PlayerShop objects and have been overridden to forward the request to the PlayerShop object's parent object.
	@Override
	public String getData() {
		return getTradeObject().getData();
	}
	@Override
	public void setData(String data) {
		getTradeObject().setData(data);
	}
	@Override
	public Image getImage(int width, int height) {
		return getTradeObject().getImage(width, height);
	}
	@Override
	public double getTotalStock() {
		return getTradeObject().getTotalStock();
	}
	@Override
	public String getName() {
		return getTradeObject().getName();
	}
	@Override
	public String getDisplayName() {
		return getTradeObject().getDisplayName();
	}
	@Override
	public ArrayList<String> getAliases() {
		return getTradeObject().getAliases();
	}
	@Override
	public String getAliasesString() {
		return getTradeObject().getAliasesString();
	}
	@Override
	public boolean hasName(String testName) {
		return getTradeObject().hasName(testName);
	}
	@Override
	public String getEconomy() {
		return getTradeObject().getEconomy();
	}
	@Override
	public TradeObjectType getType() {
		return getTradeObject().getType();
	}
	@Override
	public double getValue() {
		return getTradeObject().getValue();
	}
	@Override
	public String getIsstatic() {
		return getTradeObject().getIsstatic();
	}
	@Override
	public double getStaticprice() {
		return getTradeObject().getStaticprice();
	}
	@Override
	public double getMedian() {
		return getTradeObject().getMedian();
	}
	@Override
	public String getInitiation() {
		return getTradeObject().getInitiation();
	}
	@Override
	public double getStartprice() {
		return getTradeObject().getStartprice();
	}
	@Override
	public double getCeiling() {
		return getTradeObject().getCeiling();
	}
	@Override
	public double getFloor() {
		return getTradeObject().getFloor();
	}
	@Override
	public double getMaxstock() {
		return getTradeObject().getMaxstock();
	}
	@Override
	public int getMaxInitial() {
		return getTradeObject().getMaxInitial();
	}
	@Override
	public boolean nameStartsWith(String part) {
		return getTradeObject().nameStartsWith(part);
	}
	@Override
	public boolean nameContains(String part) {
		return getTradeObject().nameContains(part);
	}
	
	
	@Override
	public boolean isDurable() {
		return getTradeObject().isDurable();
	}
	@Override
	public int count(HInventory inventory) {
		return getTradeObject().count(inventory);
	}
	@Override
	public int getAvailableSpace(HInventory inventory) {
		return getTradeObject().getAvailableSpace(inventory);
	}
	@Override
	public void add(int amount, HyperPlayer hp) {
		getTradeObject().add(amount, hp);
	}
	@Override
	public double remove(int amount, HyperPlayer hp) {
		return getTradeObject().remove(amount, hp);
	}
	@Override
	public HItemStack getItem() {
		return getTradeObject().getItem();
	}
	@Override
	public HItemStack getItemStack(int amount) {
		return getTradeObject().getItemStack(amount);
	}
	@Override
	public void add(int amount, HInventory inventory) {
		getTradeObject().add(amount, inventory);
	}
	@Override
	public double remove(int amount, HInventory inventory) {
		return getTradeObject().remove(amount, inventory);
	}
	@Override
	public double getDamageMultiplier(int amount, HInventory inventory) {
		return getTradeObject().getDamageMultiplier(amount, inventory);
	}
	@Override
	public boolean matchesItemStack(HItemStack stack) {
		return getTradeObject().matchesItemStack(stack);
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
