package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;

public class HyperObjectAPI implements ObjectAPI {
	public double getTheoreticalPurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		@SuppressWarnings("deprecation")
		ItemStack stack = new ItemStack(id, durability);
		HyperItem ho = he.getHyperItem(stack);
		if (ho == null) {
			return 0.0;
		}
		Double price = ho.getCost(amount);
		price = cf.twoDecimals(price);
		return price;
	}

	public double getTheoreticalSaleValue(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		@SuppressWarnings("deprecation")
		ItemStack stack = new ItemStack(id, durability);
		HyperItem ho = he.getHyperItem(stack);
		if (ho == null) {
			return 0.0;
		}
		Double value = ho.getValue(amount);
		value = cf.twoDecimals(value);
		return value;
	}

	public double getTruePurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		@SuppressWarnings("deprecation")
		ItemStack stack = new ItemStack(id, durability);
		HyperItem ho = he.getHyperItem(stack);
		if (ho == null) {
			return 0.0;
		}
		Double price = ho.getCost(amount);
		double tax = ho.getPurchaseTax(price);
		price = tax + price;
		price = cf.twoDecimals(price);
		return price;
	}

	public double getTrueSaleValue(int id, int durability, int amount, Player player) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		HyperEconomy he = hc.getEconomyManager().getHyperPlayer(player.getName()).getHyperEconomy();
		@SuppressWarnings("deprecation")
		ItemStack stack = new ItemStack(id, durability);
		HyperItem ho = he.getHyperItem(stack);
		if (ho == null) {
			return 0.0;
		}
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player.getName());
		Double value = ho.getValue(amount, hp);
		double salestax = hp.getSalesTax(value);
		value = value - salestax;
		value = cf.twoDecimals(value);
		return value;
	}
	
	
	public double getTruePurchasePrice(HyperObject hyperObject, EnchantmentClass enchantClass, int amount) {
		if (hyperObject == null) {
			return 0.0;
		}
		if (enchantClass == null || enchantClass == EnchantmentClass.NONE) {
			enchantClass = EnchantmentClass.DIAMOND;
		}
		if (hyperObject instanceof HyperEnchant) {
			HyperEnchant he = (HyperEnchant)hyperObject;
			double cost = he.getCost(enchantClass);
			cost += hyperObject.getPurchaseTax(cost);
			return cost;
		} else if (hyperObject instanceof HyperItem) {
			HyperItem hi = (HyperItem)hyperObject;
			double cost = hi.getCost(amount);
			cost += hyperObject.getPurchaseTax(cost);
			return cost;
		} else if (hyperObject instanceof BasicObject) {
			BasicObject bo = (BasicObject)hyperObject;
			double cost = bo.getCost(amount);
			cost += hyperObject.getPurchaseTax(cost);
			return cost;
		}
		return 0;
	}

	public double getTrueSaleValue(HyperObject hyperObject, HyperPlayer hyperPlayer, EnchantmentClass enchantClass, int amount) {
		if (hyperObject == null || hyperPlayer == null) {
			return 0.0;
		}
		if (hyperObject instanceof HyperEnchant) {
			HyperEnchant he = (HyperEnchant)hyperObject;
			if (enchantClass == null || enchantClass == EnchantmentClass.NONE) {
				enchantClass = EnchantmentClass.DIAMOND;
			}
			double value = he.getValue(enchantClass, hyperPlayer);
			value -= hyperPlayer.getSalesTax(value);
			return value;
		} else if (hyperObject instanceof HyperItem) {
			HyperItem hi = (HyperItem)hyperObject;
			double value = hi.getValue(amount, hyperPlayer);
			value -= hyperPlayer.getSalesTax(value);
			return value;
		} else if (hyperObject instanceof BasicObject) {
			BasicObject bo = (BasicObject)hyperObject;
			double value = bo.getValue(amount);
			value -= hyperPlayer.getSalesTax(value);
			return value;
		}
		return 0;
	}
	
	
	public double getTheoreticalSaleValue(HyperObject hyperObject, EnchantmentClass enchantClass, int amount) {
		if (hyperObject == null) {
			return 0.0;
		}
		if (hyperObject instanceof HyperEnchant) {
			HyperEnchant he = (HyperEnchant)hyperObject;
			if (enchantClass == null || enchantClass == EnchantmentClass.NONE) {
				enchantClass = EnchantmentClass.DIAMOND;
			}
			double value = he.getValue(enchantClass);
			value -= hyperObject.getSalesTaxEstimate(value);
			return value;
		} else if (hyperObject instanceof HyperItem) {
			HyperItem hi = (HyperItem)hyperObject;
			double value = hi.getValue(amount);
			value -= hyperObject.getSalesTaxEstimate(value);
			return value;
		} else if (hyperObject instanceof BasicObject) {
			BasicObject bo = (BasicObject)hyperObject;
			double value = bo.getValue(amount);
			value -= hyperObject.getSalesTaxEstimate(value);
			return value;
		}
		return 0;
	}
	

	public String getName(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getName();
	}

	public String getEconomy(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getEconomy();
	}

	public HyperObjectType getType(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getType();
	}

	public String getMaterial(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperItem(name).getMaterial();
	}

	public int getData(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperItem(name).getData();
	}

	public int getDurability(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperItem(name).getDurability();
	}

	public double getValue(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getValue();
	}

	public String getStatic(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getIsstatic();
	}

	public double getStaticPrice(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getStaticprice();
	}

	public double getStock(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getStock();
	}

	public double getMedian(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getMedian();
	}

	public String getInitiation(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getInitiation();
	}

	public double getStartPrice(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getStartprice();
	}

	public void setName(String name, String economy, String newname) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setName(newname);
	}

	public void setEconomy(String name, String economy, String neweconomy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setEconomy(neweconomy);
	}

	public void setType(String name, String economy, String newtype) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setType(newtype);
	}

	public void setMaterial(String name, String economy, String newmaterial) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperItem(name).setMaterial(newmaterial);
	}

	public void setData(String name, String economy, int newdata) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperItem(name).setData(newdata);
	}

	public void setDurability(String name, String economy, int newdurability) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperItem(name).setDurability(newdurability);
	}

	public void setValue(String name, String economy, double newvalue) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setValue(newvalue);
	}

	public void setStatic(String name, String economy, String newstatic) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setIsstatic(newstatic);
	}

	public void setStaticPrice(String name, String economy, double newstaticprice) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setStaticprice(newstaticprice);
	}

	public void setStock(String name, String economy, double newstock) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setStock(newstock);
	}

	public void setMedian(String name, String economy, double newmedian) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setMedian(newmedian);
	}

	public void setInitiation(String name, String economy, String newinitiation) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setInitiation(newinitiation);
	}

	public void setStartPrice(String name, String economy, double newstartprice) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setStartprice(newstartprice);
	}

	
	
	public HyperObject getHyperObject(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name);
	}
	
	public HyperObject getHyperObject(ItemStack stack, String economy) { 
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(stack);
	}
	public HyperObject getHyperObject(ItemStack stack, String economy, Shop s) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(stack, s);
	}
	public HyperObject getHyperObject(String name, String economy, Shop s) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name, s);
	}
	

	
	public HyperItem getHyperItem(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperItem(name);
	}

	public HyperEnchant getHyperEnchant(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperEnchant(name);
	}

	public BasicObject getBasicObject(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getBasicObject(name);
	}

	public HyperXP getHyperXP(String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperXP();
	}

	public PlayerShopObject getPlayerShopObject(String name, PlayerShop s) {
		return s.getPlayerShopObject(getHyperObject(name, s.getEconomy()));
	}

	public PlayerShopItem getPlayerShopItem(String name, PlayerShop s) {
		return s.getPlayerShopItem(getHyperObject(name, s.getEconomy()));
	}

	public PlayerShopEnchant getPlayerShopEnchant(String name, PlayerShop s) {
		return s.getPlayerShopEnchant(getHyperObject(name, s.getEconomy()));
	}

	public BasicShopObject getBasicShopObject(String name, PlayerShop s) {
		return s.getBasicShopObject(getHyperObject(name, s.getEconomy()));
	}

	public ShopXp getShopXp(String name, PlayerShop s) {
		return s.getShopXp(getHyperObject(name, s.getEconomy()));
	}
	
	public HyperPlayer getHyperPlayer(String name) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getEconomyManager().getHyperPlayer(name);
	}
	
	public ArrayList<HyperObject> getEnchantmentHyperObjects(ItemStack stack, String player) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player);
		return new HyperItemStack(stack).getEnchantmentObjects(hp.getEconomy());
	}

	public TransactionResponse buy(Player p, HyperObject o, int amount) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		return hp.processTransaction(pt);
	}
	
	public TransactionResponse sell(Player p, HyperObject o, int amount ) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		return hp.processTransaction(pt);
	}

	public TransactionResponse sellAll(Player p) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_ALL);
		return hp.processTransaction(pt);
	}

	public ArrayList<HyperObject> getAvailableObjects(Player p) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getEconomyManager().getShop(p);
		if (s != null) {
			return s.getAvailableObjects();
		}
		return new ArrayList<HyperObject>();
	}

	public ArrayList<HyperObject> getAvailableObjects(Player p, int startingPosition, int limit) {
		ArrayList<HyperObject> availableObjects = getAvailableObjects(p);
		ArrayList<HyperObject> availableSubset = new ArrayList<HyperObject>();
		for (int i = startingPosition; i <= limit; i++) {
			if (availableObjects.indexOf(i) != -1) {
				availableSubset.add(availableObjects.get(i));
			}
		}
		return availableSubset;
	}
	
	public ArrayList<HyperObject> getAvailableObjects(String shopname) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getEconomyManager().getShop(shopname);
		if (s != null) {
			return s.getAvailableObjects();
		}
		return new ArrayList<HyperObject>();
	}

	public ArrayList<HyperObject> getAvailableObjects(String shopname, int startingPosition, int limit) {
		ArrayList<HyperObject> availableObjects = getAvailableObjects(shopname);
		ArrayList<HyperObject> availableSubset = new ArrayList<HyperObject>();
		for (int i = startingPosition; i <= limit; i++) {
			if (availableObjects.indexOf(i) != -1) {
				availableSubset.add(availableObjects.get(i));
			}
		}
		return availableSubset;
	}
	
	
	/*
	public List<Map<String, String>> getAllStockPlayer(Player pPlayer) {
		List<Map<String, String>> lAllStock = new ArrayList<Map<String, String>>();
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(pPlayer.getName());
		HyperEconomy he = hp.getHyperEconomy();
		List<HyperObject> lObjects = he.getHyperObjects();
		// For each object
		for (HyperObject lObject : lObjects) {
			// If the object is from the economy
			if (lObject.getEconomy().equals(hp.getEconomy())) {
				int lId = lObject.getId();
				double lStock = lObject.getStock();
				String lType = HyperObjectType.getString(lObject.getType());
				double lMaxStock = lObject.getMaxstock();
				int lData = lObject.getData();
				int lDurability = lObject.getDurability();
				String lName = lObject.getName();
				double lPurchase = getTruePurchasePrice(lObject, EnchantmentClass.DIAMOND, 1);
				double lSale = getTrueSaleValue(lObject, hp, EnchantmentClass.DIAMOND, 1);

				// Add information to MAP
				Map<String, String> lMapObject = new HashMap<String, String>();
				lMapObject.put("id", "" + lId);
				lMapObject.put("stock", "" + lStock);
				lMapObject.put("type", lType);
				lMapObject.put("maxStock", "" + lMaxStock);
				lMapObject.put("purchasePrice", "" + lPurchase);
				lMapObject.put("salePrice", "" + lSale);
				lMapObject.put("data", "" + lData);
				lMapObject.put("durability", "" + lDurability);
				lMapObject.put("name", "" + lName);
				lAllStock.add(lMapObject);
			}
		}
		return lAllStock;
	}
	*/
	/*
	public List<Map<String, String>> getAllStockEconomy(String economy) {
		List<Map<String, String>> lAllStock = new ArrayList<Map<String, String>>();
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		List<HyperObject> lObjects = he.getHyperObjects();
		// For each object
		for (HyperObject lObject : lObjects) {
			// If the object is from the economy
			if (lObject.getEconomy().equals(economy)) {
				int lId = lObject.getId();
				double lStock = lObject.getStock();
				String lType = HyperObjectType.getString(lObject.getType());
				double lMaxStock = lObject.getMaxstock();
				int lData = lObject.getData();
				int lDurability = lObject.getDurability();
				String lName = lObject.getName();
				double lPurchase = getTruePurchasePrice(lObject, EnchantmentClass.DIAMOND, 1);
				double lSale = getTheoreticalSaleValue(lObject, EnchantmentClass.DIAMOND, 1);

				// Add information to MAP
				Map<String, String> lMapObject = new HashMap<String, String>();
				lMapObject.put("id", "" + lId);
				lMapObject.put("stock", "" + lStock);
				lMapObject.put("type", lType);
				lMapObject.put("maxStock", "" + lMaxStock);
				lMapObject.put("purchasePrice", "" + lPurchase);
				lMapObject.put("salePrice", "" + lSale);
				lMapObject.put("data", "" + lData);
				lMapObject.put("durability", "" + lDurability);
				lMapObject.put("name", "" + lName);
				lAllStock.add(lMapObject);
			}
		}
		return lAllStock;
	}
	*/

	public TransactionResponse sellAll(Player p, Inventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_ALL);
		pt.setGiveInventory(inventory);
		return hp.processTransaction(pt);
	}

	public EnchantmentClass getEnchantmentClass(ItemStack stack) {
		return new HyperItemStack(stack).getEnchantmentClass();
	}















}
