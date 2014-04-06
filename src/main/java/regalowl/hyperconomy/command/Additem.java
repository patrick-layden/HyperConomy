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
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.util.LanguageFile;

public class Additem implements CommandExecutor {
	
	
	private ArrayList<String> usedNames = new ArrayList<String>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender == null) {return true;}
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		} else {
			return true;
		}
		
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataManager em = hc.getDataManager();
		
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
			
			double value = 10.0;
			int median = 0;
			if (args.length >= 2) {
				value = Double.parseDouble(args[1]);
			}
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
			if (args.length >= 3) {
				median = Integer.parseInt(args[2]);
			}
			

			if (player.getItemInHand().getType() == Material.AIR) {
				player.sendMessage(L.get("AIR_CANT_BE_TRADED"));
				return true;
			}
			HyperEconomy econ = em.getHyperPlayer(player.getName()).getHyperEconomy();
			HyperObject ho =  econ.getHyperObject(player.getItemInHand());
			if (ho != null) {
				player.sendMessage(L.get("ALREADY_IN_DATABASE"));
				return true;
			}
			ItemStack stack = player.getItemInHand();
			SerializableItemStack sis = new SerializableItemStack(stack);
			String name = stack.getType() + "_" + stack.getDurability();
			if (econ.objectTest(name) || name.equalsIgnoreCase("")) {
				name = generateName(stack);
			}
			if (displayName.equalsIgnoreCase("") || econ.objectTest(displayName)) {
				displayName = generateName(stack);
			}
			String aliases = displayName.replace("_", "");
			if (econ.objectTest(aliases)) {
				aliases = "";
			} else {
				aliases += ",";
			}
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("NAME", name);
			values.put("DISPLAY_NAME", displayName);
			values.put("ALIASES", aliases);
			values.put("ECONOMY", econ.getName());
			values.put("TYPE", "item");
			values.put("VALUE", value+"");
			values.put("STATIC", "false");
			values.put("STATICPRICE", value*2+"");
			values.put("STOCK", 0+"");
			values.put("MEDIAN", median+"");
			values.put("INITIATION", "true");
			values.put("STARTPRICE", value*2+"");
			values.put("CEILING", "1000000000");
			values.put("FLOOR", "0");
			values.put("MAXSTOCK", "1000000000");
			values.put("DATA", sis.serialize());
			hc.getSQLWrite().performInsert("hyperconomy_objects", values);
			player.sendMessage(L.get("ITEM_ADDED"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(L.get("ADDITEM_INVALID"));
			return true;
		}
	}
	
	private void addItem(ItemStack stack, Player player) {
		if (stack == null || player == null) {return;}
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		if (stack.getType() == Material.AIR) {return;}
		HyperEconomy econ = em.getHyperPlayer(player.getName()).getHyperEconomy();
		HyperObject ho =  econ.getHyperObject(stack);
		if (ho != null) {return;}
		SerializableItemStack sis = new SerializableItemStack(stack);
		String name = stack.getType() + "_" + stack.getDurability();
		if (econ.objectTest(name) || name.equalsIgnoreCase("")) {
			name = generateName(stack);
		}
		String displayName = generateName(stack);
		String aliases = displayName.replace("_", "");
		if (econ.objectTest(aliases)) {
			aliases = "";
		} else {
			aliases += ",";
		}
		double value = 10.0;
		int median = 10000;
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", name);
		values.put("DISPLAY_NAME", displayName);
		values.put("ALIASES", aliases);
		values.put("ECONOMY", econ.getName());
		values.put("TYPE", "item");
		values.put("VALUE", value+"");
		values.put("STATIC", "false");
		values.put("STATICPRICE", value*2+"");
		values.put("STOCK", 0+"");
		values.put("MEDIAN", median+"");
		values.put("INITIATION", "true");
		values.put("STARTPRICE", value*2+"");
		values.put("CEILING", "1000000000");
		values.put("FLOOR", "0");
		values.put("MAXSTOCK", "1000000000");
		values.put("DATA", sis.serialize());
		hc.getSQLWrite().performInsert("hyperconomy_objects", values);
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
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			addItem(stack, p);
		}
	}
}
