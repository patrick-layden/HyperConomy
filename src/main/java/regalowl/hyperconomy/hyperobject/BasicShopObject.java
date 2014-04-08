package regalowl.hyperconomy.hyperobject;

import java.awt.Image;
import java.util.ArrayList;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.sql.SQLWrite;
import regalowl.databukkit.sql.WriteStatement;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.shop.PlayerShop;

public class BasicShopObject extends BasicObject implements HyperObject {

	protected HyperConomy hc;
	protected SQLWrite sw;
	protected PlayerShop playerShop;
	protected HyperObject ho;
	protected double stock;
	protected double buyPrice;
	protected double sellPrice;
	protected HyperObjectStatus status;
	protected int maxStock;
	
	public BasicShopObject(PlayerShop playerShop, HyperObject ho, double stock, double buyPrice, double sellPrice, int maxStock, HyperObjectStatus status) {
		hc = HyperConomy.hc;
		sw = hc.getSQLWrite();
		this.playerShop = playerShop;
		this.ho = ho;
		this.stock = stock;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.maxStock = maxStock;
		this.status = status;
	}
	

	@Override
	public PlayerShop getShop() {
		return playerShop;
	}
	@Override
	public HyperObject getHyperObject() {
		return ho;
	}
	@Override
	public double getStock() {
		return stock;
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
	public HyperObjectStatus getStatus() {
		return status;
	}
	@Override
	public int getMaxStock() {
		return maxStock;
	}
	@Override
	public void setHyperObject(HyperObject ho) {
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET HYPEROBJECT=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(ho.getName());
		ws.addParameter(playerShop.getName());
		ws.addParameter(this.ho.getName());
		sw.addToQueue(ws);
		this.ho = ho;
	}
	@Override
	public void setShop(PlayerShop playerShop) {
		this.playerShop = playerShop;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET SHOP=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(playerShop.getName());
		ws.addParameter(playerShop.getName());
		ws.addParameter(ho.getName());
		sw.addToQueue(ws);
	}
	@Override
	public void setStock(double stock) {
		if (stock < 0.0) {stock = 0.0;}
		this.stock = stock;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET QUANTITY=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(stock);
		ws.addParameter(playerShop.getName());
		ws.addParameter(ho.getName());
		sw.addToQueue(ws);
	}
	@Override
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET BUY_PRICE=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(buyPrice);
		ws.addParameter(playerShop.getName());
		ws.addParameter(ho.getName());
		sw.addToQueue(ws);
	}
	@Override
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET SELL_PRICE=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(sellPrice);
		ws.addParameter(playerShop.getName());
		ws.addParameter(ho.getName());
		sw.addToQueue(ws);
	}
	@Override
	public void setMaxStock(int maxStock) {
		this.maxStock = maxStock;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET MAX_STOCK=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(maxStock);
		ws.addParameter(playerShop.getName());
		ws.addParameter(ho.getName());
		sw.addToQueue(ws);
	}
	@Override
	public void setStatus(HyperObjectStatus status) {
		this.status = status;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET STATUS=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(status.toString());
		ws.addParameter(playerShop.getName());
		ws.addParameter(ho.getName());
		sw.addToQueue(ws);
	}
	
	
	



	@Override
	public double getSellPriceWithTax(int amount, HyperPlayer hp) {
		return getSellPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getSellPrice(int amount, HyperPlayer hp) {
		return getSellPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getBuyPriceWithTax(int amount) {
		return getBuyPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getBuyPrice(int amount) {
		if (getBuyPrice() != 0.0) {
			return getBuyPrice() * amount;
		} else {
			return ho.getBuyPrice(amount);
		}
	}
	@Override
	public double getSellPrice(int amount) {
		if (getSellPrice() != 0.0) {
			return getSellPrice() * amount;
		} else {
			return ho.getSellPrice(amount);
		}
	}
	@Override
	public boolean isShopObject() {return true;}

	
	
	
	//The following methods don't apply to PlayerShop objects and have been overridden to prevent database changes.
	@Override
	public double applyCeilingFloor(double value) {return value;}
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
	public void setType(HyperObjectType type) {}
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
		return ho.getData();
	}
	@Override
	public void setData(String data) {
		ho.setData(data);
	}
	@Override
	public Image getImage(int width, int height) {
		return ho.getImage(width, height);
	}
	@Override
	public double getTotalStock() {
		return ho.getTotalStock();
	}
	@Override
	public String getName() {
		return ho.getName();
	}
	@Override
	public String getDisplayName() {
		return ho.getDisplayName();
	}
	@Override
	public ArrayList<String> getAliases() {
		return ho.getAliases();
	}
	@Override
	public String getAliasesString() {
		return ho.getAliasesString();
	}
	@Override
	public boolean hasName(String testName) {
		return ho.hasName(testName);
	}
	@Override
	public String getEconomy() {
		return ho.getEconomy();
	}
	@Override
	public HyperObjectType getType() {
		return ho.getType();
	}
	@Override
	public double getValue() {
		return ho.getValue();
	}
	@Override
	public String getIsstatic() {
		return ho.getIsstatic();
	}
	@Override
	public double getStaticprice() {
		return ho.getStaticprice();
	}
	@Override
	public double getMedian() {
		return ho.getMedian();
	}
	@Override
	public String getInitiation() {
		return ho.getInitiation();
	}
	@Override
	public double getStartprice() {
		return ho.getStartprice();
	}
	@Override
	public double getCeiling() {
		return ho.getCeiling();
	}
	@Override
	public double getFloor() {
		return ho.getFloor();
	}
	@Override
	public double getMaxstock() {
		return ho.getMaxstock();
	}
	@Override
	public int getMaxInitial() {
		return ho.getMaxInitial();
	}
	@Override
	public boolean nameStartsWith(String part) {
		return ho.nameStartsWith(part);
	}
	@Override
	public boolean nameContains(String part) {
		return ho.nameContains(part);
	}
	
	
	@Override
	public boolean isDurable() {
		return ho.isDurable();
	}
	@Override
	public int count(Inventory inventory) {
		return ho.count(inventory);
	}
	@Override
	public int getAvailableSpace(Inventory inventory) {
		return ho.getAvailableSpace(inventory);
	}
	@Override
	public void add(int amount, HyperPlayer hp) {
		ho.add(amount, hp);
	}
	@Override
	public double remove(int amount, HyperPlayer hp) {
		return ho.remove(amount, hp);
	}
	@Override
	public ItemStack getItemStack() {
		return ho.getItemStack();
	}
	@Override
	public ItemStack getItemStack(int amount) {
		return ho.getItemStack(amount);
	}
	@Override
	public void add(int amount, Inventory inventory) {
		ho.add(amount, inventory);
	}
	@Override
	public double remove(int amount, Inventory inventory) {
		return ho.remove(amount, inventory);
	}
	@Override
	public double getDamageMultiplier(int amount, Inventory inventory) {
		return ho.getDamageMultiplier(amount, inventory);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
