package regalowl.hyperconomy.hyperobject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableItemStack;

public class HyperItemStack {

	/*
	private HyperConomy hc;
	private ItemStack stack;
	
	public HyperItemStack(ItemStack stack) {
		hc = HyperConomy.hc;
		this.stack = stack;
	}
	

	public String getKey() {
		return stack.getType().toString() + "|" + getDamageValue();
	}



	public boolean canEnchantItem() {
		if (stack == null || stack.getType().equals(Material.AIR)) {
			return false;
		}
		if (stack.getType().equals(Material.BOOK)) {
			return true;
		} else {
			boolean enchantable = false;
			for (Enchantment enchant:Enchantment.values()) {
				if (enchant.canEnchantItem(stack)) {
					enchantable = true;
				}
			}
			return enchantable;
		}
	}
	
	public boolean canAcceptEnchantment(Enchantment e) {
		if (e == null || stack == null || stack.getType().equals(Material.AIR)) {
			return false;
		}
		if (stack.getAmount() > 1) {
			return false;
		}
		if (stack.getType().equals(Material.BOOK)) {
			return true;
		} else if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
			return false;
		} else {
			ArrayList<Enchantment> enchants = listEnchantments();
			for (Enchantment en:enchants) {
				if (en.conflictsWith(e)) {
					return false;
				}
			}
			return e.canEnchantItem(stack);
		}
	}
	
	
	
	public ArrayList<HyperObject> getEnchantmentObjects(String economy) {
		ArrayList<HyperObject> enchantmentObjects = new ArrayList<HyperObject>();
		for(String enchantment:convertEnchantmentMapToNames(getEnchantmentMap())) {
			HyperObject eo = hc.getDataManager().getEconomy(economy).getHyperObject(enchantment);
			if (eo != null) {
				enchantmentObjects.add(eo);
			}
		}
		return enchantmentObjects;
	}
	

	public ArrayList<String> getEnchants() {
		return convertEnchantmentMapToNames(getEnchantmentMap());
	}
	
	public ArrayList<String> convertEnchantmentMapToNames(Map<Enchantment, Integer> enchants) {
		ArrayList<String> enchantments = new ArrayList<String>();
		if (enchants.isEmpty()) {
			return enchantments;
		}
		Iterator<Enchantment> ite = enchants.keySet().iterator();
		while (ite.hasNext()) {
			Enchantment e = ite.next();
			enchantments.add(hc.getDataManager().getEconomy("default").getEnchantNameWithoutLevel(e.getName()) + enchants.get(e));
		}
		return enchantments;
	}
	

	public ArrayList<Enchantment> listEnchantments() {
		ArrayList<Enchantment> enchantments = new ArrayList<Enchantment>();
		for (Enchantment ench:getEnchantmentMap().keySet()) {
			enchantments.add(ench);
		}
		return enchantments;
	}
	
	public Map<Enchantment, Integer> getEnchantmentMap() {
		if (stack != null) {
			if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
				EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
				return emeta.getStoredEnchants();
			} else {
				return stack.getEnchantments();
			}
		} else {
			return null;
		}
	}
	
	public int getEnchantmentLevel(Enchantment e) {
		if (e == null || stack == null) {
			return 0;
		}
		if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
			return emeta.getStoredEnchantLevel(e);
		} else {
			return stack.getEnchantmentLevel(e);
		}
	}
	
	
	public boolean containsEnchantment(Enchantment e) {
		if (e == null || stack == null) {
			return false;
		}
		if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
			return emeta.hasStoredEnchant(e);
		} else {
			return stack.containsEnchantment(e);
		}
	}

	public void removeEnchant(Enchantment e) {
		if (e == null || stack == null) {
			return;
		}
		if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
			stack.setType(Material.BOOK);
		} else {
			stack.removeEnchantment(e);
		}
	}
	
	public void addEnchantment(Enchantment e, int lvl) {
		if (e == null || stack == null) {
			return;
		}
		if (stack.getType().equals(Material.BOOK)) {
			stack.setType(Material.ENCHANTED_BOOK);
			EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
			emeta.addStoredEnchant(e, lvl, true);
			stack.setItemMeta(emeta);
		} else {
			stack.addEnchantment(e, lvl);
		}
	}
	
	

	
	
	public boolean hasEnchants() {
		try {
			boolean hasenchants = false;
			if (stack != null && !stack.getType().equals(Material.AIR)) {
				if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
					EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
					if (emeta != null) {
						hasenchants = emeta.hasStoredEnchants();
					}
				} else {
					ItemMeta imeta = stack.getItemMeta();
					if (imeta != null) {
						hasenchants = imeta.hasEnchants();
					}
				}
			}
			return hasenchants;
		} catch (Exception e) {
			hc.gDB().writeError(e, "Passed stack: type = '" + stack.getType().toString() + "', amount = '" + stack.getAmount() + "'");
			return false;
		}
	}
	

	public boolean isDamaged() {
		if (stack.getType().getMaxDurability() > 0) {
			if (stack.getDurability() > 0) {
				return true;
			}
		}
		return false;
	}
	

	public int cleanDamageValue() {
		if (stack.getType().getMaxDurability() > 0) {return 0;}
		return stack.getDurability();
	}
	public int getDamageValue() {
		if (stack == null) {return 0;}
		return cleanDamageValue();
	}

	public double getDurabilityMultiplier() {
		double duramult = 1;
		if (isDurable()) {
			double dura = stack.getDurability();
			double maxdura = stack.getType().getMaxDurability();
			duramult = (1 - dura / maxdura);
		}
		return duramult;
	}
	
	public boolean isDurable() {
		Material m = stack.getType();
		if (m != null && m.getMaxDurability() > 0) {
			return true;
		}
		return false;
	}
	
	

	public EnchantmentClass getEnchantmentClass() {
		return EnchantmentClass.fromString(stack.getType().name());
	}
	
	
	
	public HyperObject generateTempItem(){
		if (stack.getType() == Material.AIR) {return null;}
		String name = generateName(stack);
		double value = 10.0;
		double median = 10000;
		double startprice = 20.0;
		SerializableItemStack sis = new SerializableItemStack(stack);
		return new TempItem(name, "default", name, "", "item", value, "false", startprice, 0.0, median, "true", startprice, 0.0, 0.0, 0.0, sis.serialize());
	}
	public String generateName(ItemStack stack) {
		String name = stack.getData().toString().toLowerCase();
		if (name.contains("(")) {
			name = name.substring(0, name.lastIndexOf("(")).replace("_", "").replace(" ", "");
		} else {
			name = name.replace("_", "").replace(" ", "");
		}
		if (nameInUse(name)) {
			return generateGenericName();
		}
		return name;
	}
	
	private String generateGenericName() {
		String name = "object1";
		int counter = 1;
		while (nameInUse(name)) {
			name = "object" + counter;
			counter++;
		}
		return name;
	}
	
	private boolean nameInUse(String name) {
		if (HyperConomy.hc.getDataManager().getEconomy("default").objectTest(name)) {
			return true;
		} else {
			return false;
		}
	}
	

*/
	
}
