package regalowl.hyperconomy.command;



import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.ComponentItem;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.util.LanguageFile;

public class Additem extends BaseCommand implements HyperCommand {
	
	private ArrayList<String> usedNames = new ArrayList<String>();
	
	public Additem() {
		super(true);
	}

	
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		String[] args = data.getArgs();
		
		try {
			String displayName = "";
			if (args.length >= 1) {
				displayName = args[0];
				if (displayName.equalsIgnoreCase("all")) {
					addAll(player);
					player.sendMessage(L.get("INVENTORY_ADDED"));
					return true;
				}
				if (displayName.equalsIgnoreCase("help")) {
					player.sendMessage(L.get("ADDITEM_INVALID"));
					return true;
				}
			}
			double value = 0;
			if (args.length >= 2) {
				value = Double.parseDouble(args[1]);
			}
			if (player.getItemInHand().getType() == Material.AIR) {
				player.sendMessage(L.get("AIR_CANT_BE_TRADED"));
				return true;
			}
			HyperEconomy econ = dm.getHyperPlayer(player).getHyperEconomy();
			HyperObject ho =  econ.getHyperObject(player.getItemInHand());
			if (ho != null) {
				player.sendMessage(L.get("ALREADY_IN_DATABASE"));
				return true;
			}
			ItemStack stack = player.getItemInHand();
			HyperObject hobj = generateNewHyperObject(stack, econ.getName(), displayName, value);
			addItem(hobj, econ.getName());
			player.sendMessage(L.get("ITEM_ADDED"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(L.get("ADDITEM_INVALID"));
			return true;
		}
	}

	
	
	public boolean addItem(HyperObject hobj, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		if (hobj == null || economy == null) {return false;}
		HyperEconomy he = em.getEconomy(economy);
		if (he == null) {return false;}
		if (he.objectTest(hobj.getName())) {return false;}
		if (he.objectTest(hobj.getDisplayName())) {return false;}
		for (String alias:hobj.getAliases()) {
			if (he.objectTest(alias)) {return false;}
		}
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", hobj.getName());
		values.put("DISPLAY_NAME", hobj.getDisplayName());
		values.put("ALIASES", hobj.getAliasesString());
		values.put("ECONOMY", hobj.getEconomy());
		values.put("TYPE", hobj.getType().toString());
		values.put("VALUE", hobj.getValue()+"");
		values.put("STATIC", hobj.getIsstatic());
		values.put("STATICPRICE", hobj.getStaticprice()+"");
		values.put("STOCK", hobj.getStock()+"");
		values.put("MEDIAN", hobj.getMedian()+"");
		values.put("INITIATION", hobj.getInitiation());
		values.put("STARTPRICE",hobj.getStartprice()+"");
		values.put("CEILING", hobj.getCeiling()+"");
		values.put("FLOOR", hobj.getFloor()+"");
		values.put("MAXSTOCK", hobj.getMaxstock()+"");
		values.put("DATA", hobj.getData());
		hc.getSQLWrite().performInsert("hyperconomy_objects", values);
		he.addHyperObject(hobj);
		return true;
	}
	
	public HyperObject generateNewHyperObject(ItemStack stack, String economy) {
		return generateNewHyperObject(stack, economy, "", 0);
	}
	
	public HyperObject generateNewHyperObject(ItemStack stack, String economy, String displayName, double value) {
		if (stack == null || economy == null || displayName == null) {return null;}
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		if (stack.getType() == Material.AIR) {return null;}
		HyperEconomy econ = em.getEconomy(economy);
		if (econ == null) {return null;}
		HyperObject ho =  econ.getHyperObject(stack);
		if (ho != null) {return null;}
		SerializableItemStack sis = new SerializableItemStack(stack);
		String name = stack.getType() + "_" + stack.getDurability();
		if (econ.objectTest(name) || name.equalsIgnoreCase("")) {
			name = generateName(stack);
		}
		if (econ.objectTest(displayName) || displayName.equalsIgnoreCase("")) {
			displayName = generateName(stack);
		}
		String aliases = displayName.replace("_", "");
		if (econ.objectTest(aliases)) {
			aliases = "";
		} else {
			aliases += ",";
		}
		if (value <= 0) {
			value = 10.0;
		}
		int median = 0;
		if (value >= 100000) {
			median = 10;
		} else if (value >= 10000) {
			median = 100;
		} else if (value >= 1000) {
			median = 500;
		} else if (value >= 100) {
			median = 1000;
		} else if (value >= 50) {
			median = 5000;
		} else if (value >= 1) {
			median = 10000;
		} else {
			median = 25000;
		}
		HyperObject hobj = new ComponentItem(name, economy, displayName, aliases, "item", value, "false", value*2,
				0, median, "true", value*2, 1000000000,0, 1000000000, sis.serialize());
		return hobj;
	}
	
	
	
	private String generateName(ItemStack stack) {
		String name = stack.getData().toString().toLowerCase();
		if (name.contains("(")) {
			name = name.substring(0, name.lastIndexOf("(")).replace("_", "").replace(" ", "");
		} else {
			name = name.replace("_", "").replace(" ", "");
		}
		if (nameInUse(name)) {
			return generateGenericName();
		}
		usedNames.add(name);
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
		if (HyperConomy.hc.getDataManager().getDefaultEconomy().objectTest(name)) {
			return true;
		}
		for (String cName:usedNames) {
			if (cName.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
		
	}
	
	private void addAll(Player p) {
		Inventory inventory = p.getInventory();
		HyperPlayer hp = HyperConomy.hc.getDataManager().getHyperPlayer(p);
		String economy = hp.getEconomy();
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			HyperObject hobj = generateNewHyperObject(stack, economy);
			addItem(hobj, economy);
		}
	}
}
