package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HyperObjectAPI implements HyperObjectInterface {
	public double getTheoreticalPurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperObject ho = hc.getDataFunctions().getHyperObject(id, durability, economy);
		if (ho == null) {
			return 0.0;
		}
		String name = ho.getName();
		Double price = calc.getCost(name, amount, economy);
		price = calc.twoDecimals(price);
		return price;
	}

	public double getTheoreticalSaleValue(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperObject ho = hc.getDataFunctions().getHyperObject(id, durability, economy);
		if (ho == null) {
			return 0.0;
		}
		String name = ho.getName();
		Double value = calc.getTvalue(name, amount, economy);
		value = calc.twoDecimals(value);
		return value;
	}

	public double getTruePurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		double durabilityPercent = 1.0;
		if (calc.isDurable(id)) {
			ItemStack item = new ItemStack(id, durability);
			durabilityPercent = 1.0 - ((double) durability / item.getType().getMaxDurability());
			durability = 0;
		}
		HyperObject ho = hc.getDataFunctions().getHyperObject(id, durability, economy);
		if (ho == null) {
			return 0.0;
		}
		String name = ho.getName();
		Double price = calc.getCost(name, amount, economy) * durabilityPercent;
		double tax = calc.getPurchaseTax(name, economy, price);
		price = tax + price;
		price = calc.twoDecimals(price);
		return price;
	}

	public double getTrueSaleValue(int id, int durability, int amount, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		double durabilityPercent = 1.0;
		if (calc.isDurable(id)) {
			ItemStack item = new ItemStack(id, durability);
			durabilityPercent = 1.0 - ((double) durability / item.getType().getMaxDurability());
			durability = 0;
		}
		HyperObject ho = hc.getDataFunctions().getHyperObject(id, durability, hc.getDataFunctions().getHyperPlayer(player).getEconomy());
		if (ho == null) {
			return 0.0;
		}
		String name = ho.getName();
		Double value = calc.getValue(name, amount, player) * durabilityPercent;
		double salestax = calc.getSalesTax(player, value);
		value = value - salestax;
		value = calc.twoDecimals(value);
		return value;
	}

	public String getName(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getName();
	}

	public String getEconomy(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getEconomy();
	}

	public String getType(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getType();
	}

	public String getCategory(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getCategory();
	}

	public String getMaterial(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getMaterial();
	}

	public int getId(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getId();
	}

	public int getData(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getData();
	}

	public int getDurability(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getDurability();
	}

	public double getValue(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getValue();
	}

	public String getStatic(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getIsstatic();
	}

	public double getStaticPrice(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getStaticprice();
	}

	public double getStock(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getStock();
	}

	public double getMedian(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getMedian();
	}

	public String getInitiation(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getInitiation();
	}

	public double getStartPrice(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getStartprice();
	}

	public void setName(String name, String economy, String newname) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setName(newname);
	}

	public void setEconomy(String name, String economy, String neweconomy) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setEconomy(neweconomy);
	}

	public void setType(String name, String economy, String newtype) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setType(newtype);
	}

	public void setCategory(String name, String economy, String newcategory) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setCategory(newcategory);
	}

	public void setMaterial(String name, String economy, String newmaterial) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setMaterial(newmaterial);
	}

	public void setId(String name, String economy, int newid) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setId(newid);
	}

	public void setData(String name, String economy, int newdata) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setData(newdata);
	}

	public void setDurability(String name, String economy, int newdurability) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setDurability(newdurability);
	}

	public void setValue(String name, String economy, double newvalue) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setValue(newvalue);
	}

	public void setStatic(String name, String economy, String newstatic) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setIsstatic(newstatic);
	}

	public void setStaticPrice(String name, String economy, double newstaticprice) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setStaticprice(newstaticprice);
	}

	public void setStock(String name, String economy, double newstock) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setStock(newstock);
	}

	public void setMedian(String name, String economy, double newmedian) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setMedian(newmedian);
	}

	public void setInitiation(String name, String economy, String newinitiation) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setInitiation(newinitiation);
	}

	public void setStartPrice(String name, String economy, double newstartprice) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setStartprice(newstartprice);
	}

	public double getItemPurchasePrice(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperObject ho = hc.getDataFunctions().getHyperObject(id, data, "default");
		if (ho == null) {
			return 0.0;
		}
		String name = ho.getName();
		Double price = calc.getCost(name, amount, "default");
		price = calc.twoDecimals(price);
		return price;
	}

	public double getItemSaleValue(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		HyperObject ho = hc.getDataFunctions().getHyperObject(id, data, "default");
		if (ho == null) {
			return 0.0;
		}
		String name = ho.getName();
		Double value = calc.getTvalue(name, amount, "default");
		value = calc.twoDecimals(value);
		return value;
	}

	public HyperObject getHyperObject(ItemStack stack, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		DataHandler dh = hc.getDataFunctions();
		String economy = dh.getHyperPlayer(player).getEconomy();
		int id = stack.getTypeId();
		int damageValue = calc.getDamageValue(stack);
		HyperObject ho = dh.getHyperObject(id, damageValue, economy);
		return ho;
	}

	public TransactionResponse buy(Player p, HyperObject o, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Transaction tran = hc.getTransaction();
		return tran.buy(o, amount, p, null);
	}

	public TransactionResponse sellAll(Player p) {
		HyperConomy hc = HyperConomy.hc;
		Transaction tran = hc.getTransaction();
		return tran.sellAll(p, null);
	}

	public ArrayList<HyperObject> getAvailableObjects(Player p) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler dh = hc.getDataFunctions();
		ShopFactory sf = hc.getShopFactory();
		HyperPlayer hp = dh.getHyperPlayer(p);
		String economy = hp.getEconomy();
		ArrayList<HyperObject> hyperObjects = dh.getHyperObjects(economy);
		ArrayList<HyperObject> availableObjects = new ArrayList<HyperObject>();
		Shop s = sf.getShop(p);
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
		DataHandler sf = hc.getDataFunctions();
		List<HyperObject> lObjects = sf.getHyperObjects();
		HyperPlayer hp = sf.getHyperPlayer(pPlayer);
		// For each object
		for (HyperObject lObject : lObjects) {
			// If the object is from the economy
			if (lObject.getEconomy().equals(hp.getEconomy())) {
				int lId = lObject.getId();
				double lStock = lObject.getStock();
				String lType = lObject.getType();
				double lMaxStock = lObject.getMaxstock();
				int lData = lObject.getData();
				int lDurability = lObject.getDurability();
				String lName = lObject.getName();
				double lPurchase = getTruePurchasePrice(lId, lDurability, 1, hp.getEconomy());
				double lSale = getTrueSaleValue(lId, lDurability, 1, pPlayer);

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
		Transaction tran = hc.getTransaction();
		return tran.sellAll(p, inventory);
	}
}
