package regalowl.hyperconomy.tradeobject;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;

import regalowl.simpledatalib.sql.WriteStatement;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.event.TradeObjectModificationType;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.PlayerShop;

public class BasicShopTradeObject extends BasicTradeObject implements TradeObject {

	private static final long serialVersionUID = -8506945265990355676L;
	protected String playerShop;
	protected String hyperObject;
	protected double buyPrice;
	protected double sellPrice;
	protected TradeObjectStatus status;
	protected int maxStock;
	protected boolean useEconomyStock;
	
	public BasicShopTradeObject(HyperConomy hc, String playerShop, TradeObject ho, double stock, double buyPrice, double sellPrice, int maxStock, TradeObjectStatus status, boolean useEconomyStock) {
		super(hc);
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
	public void save() {
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("HYPEROBJECT", hyperObject);
		conditions.put("SHOP", playerShop);
		hc.getSQLWrite().performDelete("hyperconomy_shop_objects", conditions);
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("SHOP", playerShop);
		values.put("HYPEROBJECT", hyperObject);
		values.put("QUANTITY", stock+"");
		values.put("BUY_PRICE", getShopObjectBuyPrice()+"");
		values.put("SELL_PRICE", getShopObjectSellPrice()+"");
		values.put("MAX_STOCK", getShopObjectMaxStock()+"");
		values.put("STATUS", getShopObjectStatus()+"");
		hc.getSQLWrite().performInsert("hyperconomy_shop_objects", values);
	}
	
	@Override
	public PlayerShop getShopObjectShop() {
		return (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
	}
	@Override
	public TradeObject getParentTradeObject() {
		TradeObject ho = hc.getDataManager().getEconomyIB(economy).getTradeObject(hyperObject);
		return ho;
	}
	@Override
	public double getStock() {
		if (useEconomyStock) {
			return getParentTradeObject().getStock();
		} else {
			return stock;
		}
	}
	@Override
	public double getShopObjectBuyPrice() {
		return buyPrice;
	}
	@Override
	public double getShopObjectSellPrice() {
		return sellPrice;
	}
	@Override
	public TradeObjectStatus getShopObjectStatus() {
		return status;
	}
	@Override
	public int getShopObjectMaxStock() {
		return maxStock;
	}
	@Override
	public boolean useEconomyStock() {
		return useEconomyStock;
	}
	@Override
	public void setParentTradeObject(TradeObject to) {
		setParentTradeObject(to.getName());
	}
	@Override
	public void setParentTradeObject(String to) {
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET HYPEROBJECT=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getSimpleDataLib());
		ws.addParameter(to);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		this.hyperObject = to;
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this, TradeObjectModificationType.PARENT));
	}
	@Override
	public void setShopObjectShop(PlayerShop playerShop) {
		setShopObjectShop(playerShop.getName());
	}
	@Override
	public void setShopObjectShop(String playerShop) {
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET SHOP=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getSimpleDataLib());
		ws.addParameter(playerShop);
		ws.addParameter(this.playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		this.playerShop = playerShop;
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this, TradeObjectModificationType.SHOP));
	}
	@Override
	public void setStock(double stock) {
		if (useEconomyStock) {
			getParentTradeObject().setStock(stock);
		} else {
			if (stock < 0.0) {stock = 0.0;}
			this.stock = stock;
			WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET QUANTITY=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getSimpleDataLib());
			ws.addParameter(stock);
			ws.addParameter(playerShop);
			ws.addParameter(hyperObject);
			sw.addToQueue(ws);
			hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this, TradeObjectModificationType.STOCK));
		}
	}
	@Override
	public void setShopObjectBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET BUY_PRICE=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getSimpleDataLib());
		ws.addParameter(buyPrice);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this, TradeObjectModificationType.BUY_PRICE));
	}
	@Override
	public void setShopObjectSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET SELL_PRICE=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getSimpleDataLib());
		ws.addParameter(sellPrice);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this, TradeObjectModificationType.SELL_PRICE));
	}
	@Override
	public void setShopObjectMaxStock(int maxStock) {
		this.maxStock = maxStock;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET MAX_STOCK=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getSimpleDataLib());
		ws.addParameter(maxStock);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this, TradeObjectModificationType.MAX_STOCK));
	}
	@Override
	public void setShopObjectStatus(TradeObjectStatus status) {
		this.status = status;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET STATUS=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getSimpleDataLib());
		ws.addParameter(status.toString());
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this, TradeObjectModificationType.STATUS));
	}
	@Override
	public void setUseEconomyStock(boolean state) {
		this.useEconomyStock = state;
	}
	
	
	



	@Override
	public double getSellPriceWithTax(double amount, HyperPlayer hp) {
		return getSellPrice(amount, hp);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		if (getShopObjectSellPrice() != 0.0) {
			return getShopObjectSellPrice() * amount;
		} else {
			return getParentTradeObject().getSellPrice(amount, hp);
		}
	}
	@Override
	public double getBuyPriceWithTax(double amount) {
		return getBuyPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getBuyPrice(double amount) {
		if (getShopObjectBuyPrice() != 0.0) {
			return getShopObjectBuyPrice() * amount;
		} else {
			return getParentTradeObject().getBuyPrice(amount);
		}
	}
	@Override
	public double getSellPrice(double amount) {
		if (getShopObjectSellPrice() != 0.0) {
			return getShopObjectSellPrice() * amount;
		} else {
			return getParentTradeObject().getSellPrice(amount);
		}
	}
	@Override
	public boolean isShopObject() {return true;}
	
	@Override
	public void checkInitiationStatus() {
		getParentTradeObject().checkInitiationStatus();
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
	public void setStatic(boolean isstatic) {}
	@Override
	public void setStaticPrice(double staticprice) {}
	@Override
	public void setMedian(double median) {}
	@Override
	public void setUseInitialPricing(boolean initiation) {}
	@Override
	public void setStartPrice(double startprice) {}
	@Override
	public void setCeiling(double ceiling) {}
	@Override
	public void setFloor(double floor) {}
	@Override
	public void setMaxStock(double maxstock) {}
	@Override
	public double getPurchaseTax(double cost) {return 0;}
	@Override
	public double getSalesTaxEstimate(double value) {return 0;}
	@Override
	public void delete() {}



	
	
	//The following methods don't apply to PlayerShop objects and have been overridden to forward the request to the PlayerShop object's parent object.
	@Override
	public String getData() {
		return getParentTradeObject().getData();
	}
	@Override
	public void setData(String data) {
		getParentTradeObject().setData(data);
	}
	@Override
	public Image getImage(int width, int height) {
		return getParentTradeObject().getImage(width, height);
	}
	@Override
	public double getTotalStock() {
		return getParentTradeObject().getTotalStock();
	}
	@Override
	public String getName() {
		return getParentTradeObject().getName();
	}
	@Override
	public String getDisplayName() {
		return getParentTradeObject().getDisplayName();
	}
	@Override
	public ArrayList<String> getAliases() {
		return getParentTradeObject().getAliases();
	}
	@Override
	public String getAliasesString() {
		return getParentTradeObject().getAliasesString();
	}
	@Override
	public boolean hasName(String testName) {
		return getParentTradeObject().hasName(testName);
	}
	@Override
	public String getEconomy() {
		return getParentTradeObject().getEconomy();
	}
	@Override
	public TradeObjectType getType() {
		return getParentTradeObject().getType();
	}
	@Override
	public double getValue() {
		return getParentTradeObject().getValue();
	}
	@Override
	public boolean isStatic() {
		return getParentTradeObject().isStatic();
	}
	@Override
	public double getStaticPrice() {
		return getParentTradeObject().getStaticPrice();
	}
	@Override
	public double getMedian() {
		return getParentTradeObject().getMedian();
	}
	@Override
	public boolean useInitialPricing() {
		return getParentTradeObject().useInitialPricing();
	}
	@Override
	public double getStartPrice() {
		return getParentTradeObject().getStartPrice();
	}
	@Override
	public double getCeiling() {
		return getParentTradeObject().getCeiling();
	}
	@Override
	public double getFloor() {
		return getParentTradeObject().getFloor();
	}
	@Override
	public double getMaxStock() {
		return getParentTradeObject().getMaxStock();
	}
	@Override
	public int getMaxInitial() {
		return getParentTradeObject().getMaxInitial();
	}
	@Override
	public boolean nameStartsWith(String part) {
		return getParentTradeObject().nameStartsWith(part);
	}
	@Override
	public boolean nameContains(String part) {
		return getParentTradeObject().nameContains(part);
	}
	

	@Override
	public void add(int amount, HyperPlayer hp) {
		getParentTradeObject().add(amount, hp);
	}
	@Override
	public double remove(int amount, HyperPlayer hp) {
		return getParentTradeObject().remove(amount, hp);
	}
	@Override
	public HItemStack getItem() {
		return getParentTradeObject().getItem();
	}
	@Override
	public HItemStack getItemStack(int amount) {
		return getParentTradeObject().getItemStack(amount);
	}
	@Override
	public boolean matchesItemStack(HItemStack stack) {
		return getParentTradeObject().matchesItemStack(stack);
	}


}
