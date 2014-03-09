package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HyperObject;

public class HyperObjectAPI implements ObjectAPI {

	@Deprecated
	public double getTheoreticalPurchasePrice(Material material, short durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		ItemStack stack = new ItemStack(material);
		stack.setDurability(durability);
		HyperObject ho = he.getHyperObject(stack);
		if (ho == null) {
			return 0.0;
		}
		Double price = ho.getBuyPrice(amount);
		price = cf.twoDecimals(price);
		return price;
	}


	public double getTheoreticalSaleValue(Material material, short durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		ItemStack stack = new ItemStack(material);
		stack.setDurability(durability);
		HyperObject ho = he.getHyperObject(stack);
		if (ho == null) {
			return 0.0;
		}
		Double value = ho.getSellPrice(amount);
		value = cf.twoDecimals(value);
		return value;
	}
	

    @Deprecated
    public double getTruePurchasePrice(Material material, short durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		//@SuppressWarnings("deprecation")
		ItemStack stack = new ItemStack(material);
		stack.setDurability(durability);
		HyperObject ho = he.getHyperObject(stack);
		if (ho == null) {
			return 0.0;
		}
		Double price = ho.getBuyPrice(amount);
		double tax = ho.getPurchaseTax(price);
		price = tax + price;
		price = cf.twoDecimals(price);
		return price;
	}
    
	@Override
	public double getPurchasePrice(String name, String economy, int amount) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		EconomyManager em = hc.getEconomyManager();
		HyperEconomy he = em.getEconomy(economy);
		if (he == null) {
			he = em.getEconomy("default");
		}
		HyperObject ho = he.getHyperObject(name);
		if (ho == null) {
			return 0.0;
		}
		Double price = ho.getBuyPrice(amount);
		double tax = ho.getPurchaseTax(price);
		price = tax + price;
		price = cf.twoDecimals(price);
		return price;
	}




	
	public double getTrueSaleValue(Material material, short durability, int amount, Player player, String economy) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
		ItemStack stack = new ItemStack(material);
		stack.setDurability(durability);
		HyperObject ho = he.getHyperObject(stack);
		if (ho == null) {
			return 0.0;
		}
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player.getName());
		Double value = ho.getSellPrice(amount, hp);
		double salestax = hp.getSalesTax(value);
		value = value - salestax;
		value = cf.twoDecimals(value);
		return value;
	}
	
	@Override
	public double getTrueSaleValue(String name, String economy, String player, int amount) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		EconomyManager em = hc.getEconomyManager();
		HyperEconomy he = em.getEconomy(economy);
		if (he == null) {
			he = em.getEconomy("default");
		}
		HyperObject ho = he.getHyperObject(name);
		if (ho == null) {
			return 0.0;
		}
		HyperPlayer hp = em.getHyperPlayer(player);
		if (hp == null) {
			return 0.0;
		}
		Double value = ho.getSellPrice(amount);
		if (ho.getType() == HyperObjectType.ITEM) {
			value = ho.getSellPrice(amount, hp);
		} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
			value = ho.getSellPrice(EnchantmentClass.DIAMOND, hp);
		} else {
			value = ho.getSellPrice(amount, hp);
		}
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
		if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
			double cost = hyperObject.getBuyPrice(enchantClass);
			cost += hyperObject.getPurchaseTax(cost);
			return cost;
		} else if (hyperObject.getType() == HyperObjectType.ITEM) {
			double cost = hyperObject.getBuyPrice(amount);
			cost += hyperObject.getPurchaseTax(cost);
			return cost;
		} else {
			double cost = hyperObject.getBuyPrice(amount);
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
			double value = hyperObject.getSellPrice(enchantClass, hyperPlayer);
			value -= hyperPlayer.getSalesTax(value);
			return value;
		} else if (hyperObject.getType() == HyperObjectType.ITEM) {
			double value = hyperObject.getSellPrice(amount, hyperPlayer);
			value -= hyperPlayer.getSalesTax(value);
			return value;
		} else {
			double value = hyperObject.getSellPrice(amount);
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
			double value = hyperObject.getSellPrice(enchantClass);
			value -= hyperObject.getSalesTaxEstimate(value);
			return value;
		} else if (hyperObject.getType() == HyperObjectType.ITEM) {
			double value = hyperObject.getSellPrice(amount);
			value -= hyperObject.getSalesTaxEstimate(value);
			return value;
		} else {
			double value = hyperObject.getSellPrice(amount);
			value -= hyperObject.getSalesTaxEstimate(value);
			return value;
		}
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

	public TransactionResponse buy(Player p, HyperObject o, int amount, Shop shop) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		pt.setTradePartner(shop.getOwner());
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
	
	public TransactionResponse sell(Player p, HyperObject o, int amount, Shop shop) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		pt.setTradePartner(shop.getOwner());
		return hp.processTransaction(pt);
	}

	public TransactionResponse sellAll(Player p) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		Sellall sa = new Sellall();
		return sa.sellAll(hp, null);
	}

	public ArrayList<HyperObject> getAvailableObjects(Player p) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getEconomyManager().getShop(p);
		if (s != null) {
			return s.getTradeableObjects();
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
			return s.getTradeableObjects();
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
	


	public TransactionResponse sellAll(Player p, Inventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(p.getName());
		EconomyManager em = hc.getEconomyManager();
		HyperEconomy he = hp.getHyperEconomy();
		TransactionResponse totalResponse = new TransactionResponse(hp);
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			if (inventory.getItem(slot) == null) {continue;}
			ItemStack stack = inventory.getItem(slot);
			HyperObject hyperItem = he.getHyperObject(stack, em.getShop(hp.getPlayer()));
			PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
			pt.setGiveInventory(inventory);
			pt.setHyperObject(hyperItem);
			pt.setAmount(stack.getAmount());
			TransactionResponse response = hp.processTransaction(pt);
			if (response.successful()) {
				totalResponse.addSuccess(response.getMessage(), response.getPrice(), response.getSuccessfulObjects().get(0));
			} else {
				totalResponse.addFailed(response.getMessage(), response.getFailedObjects().get(0));
			}
		}
		return totalResponse;
	}

	public EnchantmentClass getEnchantmentClass(ItemStack stack) {
		return new HyperItemStack(stack).getEnchantmentClass();
	}


	@Override
	public double getPurchasePrice(String name, String economy, String enchantmentClass, int amount) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double getEstimatedSaleValue(String name, String economy, String player, int amount) {
		// TODO Auto-generated method stub
		return 0;
	}













}
