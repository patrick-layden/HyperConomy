package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HyperObjectAPI implements ObjectAPI {
	public double getTheoreticalPurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		HyperObject ho = he.getHyperObject(id, durability);
		if (ho == null) {
			return 0.0;
		}
		Double price = ho.getCost(amount);
		price = calc.twoDecimals(price);
		return price;
	}

	public double getTheoreticalSaleValue(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		HyperObject ho = he.getHyperObject(id, durability);
		if (ho == null) {
			return 0.0;
		}
		Double value = ho.getValue(amount);
		value = calc.twoDecimals(value);
		return value;
	}

	public double getTruePurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		HyperObject ho = he.getHyperObject(id, durability);
		if (ho == null) {
			return 0.0;
		}
		Double price = ho.getCost(amount);
		double tax = ho.getPurchaseTax(price);
		price = tax + price;
		price = calc.twoDecimals(price);
		return price;
	}

	public double getTrueSaleValue(int id, int durability, int amount, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperEconomy he = hc.getEconomyManager().getHyperPlayer(player.getName()).getHyperEconomy();
		HyperObject ho = he.getHyperObject(id, durability);
		if (ho == null) {
			return 0.0;
		}
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player.getName());
		Double value = ho.getValue(amount, hp);
		double salestax = hp.getSalesTax(value);
		value = value - salestax;
		value = calc.twoDecimals(value);
		return value;
	}
	
	
	public double getTruePurchasePrice(HyperObject hyperObject, EnchantmentClass enchantClass, int amount) {
		if (hyperObject == null) {
			return 0.0;
		}
		if (enchantClass == null || enchantClass == EnchantmentClass.NONE) {
			enchantClass = EnchantmentClass.DIAMOND;
		}
		if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
			double cost = hyperObject.getCost(enchantClass);
			cost += hyperObject.getPurchaseTax(cost);
			return cost;
		} else {
			double cost = hyperObject.getCost(amount);
			cost += hyperObject.getPurchaseTax(cost);
			return cost;
		}
	}

	public double getTrueSaleValue(HyperObject hyperObject, HyperPlayer hyperPlayer, EnchantmentClass enchantClass, int amount) {
		if (hyperObject == null || hyperPlayer == null) {
			return 0.0;
		}
		if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
			if (enchantClass == null || enchantClass == EnchantmentClass.NONE) {
				enchantClass = EnchantmentClass.DIAMOND;
			}
			double value = hyperObject.getValue(enchantClass, hyperPlayer);
			value -= hyperPlayer.getSalesTax(value);
			return value;
		} else {
			double value = hyperObject.getValue(amount, hyperPlayer);
			value -= hyperPlayer.getSalesTax(value);
			return value;
		}
	}
	
	
	public double getTheoreticalSaleValue(HyperObject hyperObject, EnchantmentClass enchantClass, int amount) {
		if (hyperObject == null) {
			return 0.0;
		}
		if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
			if (enchantClass == null || enchantClass == EnchantmentClass.NONE) {
				enchantClass = EnchantmentClass.DIAMOND;
			}
			double value = hyperObject.getValue(enchantClass);
			value -= hyperObject.getSalesTaxEstimate(value);
			return value;
		} else {
			double value = hyperObject.getValue(amount);
			value -= hyperObject.getSalesTaxEstimate(value);
			return value;
		}
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

	public String getCategory(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getCategory();
	}

	public String getMaterial(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getMaterial();
	}

	public int getId(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getId();
	}

	public int getData(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getData();
	}

	public int getDurability(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name).getDurability();
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

	public void setCategory(String name, String economy, String newcategory) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setCategory(newcategory);
	}

	public void setMaterial(String name, String economy, String newmaterial) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setMaterial(newmaterial);
	}

	public void setId(String name, String economy, int newid) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setId(newid);
	}

	public void setData(String name, String economy, int newdata) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setData(newdata);
	}

	public void setDurability(String name, String economy, int newdurability) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		he.getHyperObject(name).setDurability(newdurability);
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

	public double getItemPurchasePrice(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperEconomy he = hc.getEconomyManager().getEconomy("default");
		HyperObject ho = he.getHyperObject(id, data);
		if (ho == null) {
			return 0.0;
		}
		Double price = ho.getCost(amount);
		price = calc.twoDecimals(price);
		return price;
	}

	public double getItemSaleValue(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperEconomy he = hc.getEconomyManager().getEconomy("default");
		HyperObject ho = he.getHyperObject(id, data);
		if (ho == null) {
			return 0.0;
		}
		Double value = ho.getValue(amount);
		value = calc.twoDecimals(value);
		return value;
	}

	public HyperObject getHyperObject(ItemStack stack, Player player) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getHyperPlayer(player.getName()).getHyperEconomy();
		int id = stack.getTypeId();
		int damageValue = hc.getInventoryManipulation().getDamageValue(stack);
		HyperObject ho = he.getHyperObject(id, damageValue);
		return ho;
	}
	
	public HyperObject getHyperObject(ItemStack stack, String player) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getHyperPlayer(player).getHyperEconomy();
		int id = stack.getTypeId();
		int damageValue = hc.getInventoryManipulation().getDamageValue(stack);
		HyperObject ho = he.getHyperObject(id, damageValue);
		return ho;
	}
	
	
	public HyperObject getHyperObject(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		return he.getHyperObject(name);
	}
	
	public HyperPlayer getHyperPlayer(String name) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getEconomyManager().getHyperPlayer(name);
	}
	
	public ArrayList<HyperObject> getEnchantmentHyperObjects(ItemStack stack, String player) {
		HyperConomy hc = HyperConomy.hc;
		InventoryManipulation im = hc.getInventoryManipulation();
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player);
		return im.getEnchantmentObjects(stack, hp.getEconomy());
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
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		HyperEconomy he = hp.getHyperEconomy();
		ArrayList<HyperObject> hyperObjects = he.getHyperObjects();
		ArrayList<HyperObject> availableObjects = new ArrayList<HyperObject>();
		Shop s = he.getShop(p);
		if (s != null) {
			for (HyperObject ho:hyperObjects) {
				if (s.has(ho.getName())) {
					availableObjects.add(ho);
				}
			}
		}
		return availableObjects;
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

	public TransactionResponse sellAll(Player p, Inventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_ALL);
		pt.setGiveInventory(inventory);
		return hp.processTransaction(pt);
	}

	public EnchantmentClass getEnchantmentClass(ItemStack stack) {
		HyperConomy hc = HyperConomy.hc;
		InventoryManipulation im = hc.getInventoryManipulation();
		return im.getEnchantmentClass(stack);
	}











}
