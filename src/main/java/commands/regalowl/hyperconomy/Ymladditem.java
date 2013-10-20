package regalowl.hyperconomy;



import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Ymladditem implements CommandExecutor {
	
	
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
		EconomyManager em = hc.getEconomyManager();

		
		try {
			String name = "";
			if (args.length >= 1) {
				name = args[0];
				if (name.equalsIgnoreCase("all")) {
					addAll(player);
					player.sendMessage(L.get("INVENTORY_ADDED"));
					return true;
				}
				if (name.equalsIgnoreCase("help")) {
					player.sendMessage(L.get("YMLADDITEM_INVALID"));
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
			
			
			int id = player.getItemInHand().getTypeId();
			if (id == 0) {
				player.sendMessage(L.get("AIR_CANT_BE_TRADED"));
				return true;
			}
			int data = hc.getInventoryManipulation().getDamageValue(player.getItemInHand());
			HyperEconomy econ = em.getHyperPlayer(player.getName()).getHyperEconomy();
			HyperObject ho =  econ.getHyperObject(id, data);
			if (ho != null) {
				player.sendMessage(L.get("ALREADY_IN_DATABASE"));
				return true;
			}
			if (econ.objectTest(name) || name.equalsIgnoreCase("")) {
				name = generateName(player.getItemInHand());
			}
			FileConfiguration items = hc.gYH().gFC("items");
			items.set(name + ".information.type", "item");
			items.set(name + ".information.category", "unknown");
			items.set(name + ".information.material", player.getItemInHand().getType().toString());
			items.set(name + ".information.id", id);
			items.set(name + ".information.data", data);
			items.set(name + ".value", value);
			items.set(name + ".price.static", false);
			items.set(name + ".price.staticprice", value*2);
			items.set(name + ".stock.stock", 0);
			items.set(name + ".stock.median", median);
			items.set(name + ".initiation.initiation", true);
			items.set(name + ".initiation.startprice", value*2);
			player.sendMessage(L.get("ITEM_ADDED"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(L.get("YMLADDITEM_INVALID"));
			return true;
		}
	}
	
	private void addItem(ItemStack stack, Player player) {
		if (stack == null || player == null) {return;}
		int id = stack.getTypeId();
		if (id == 0) {return;}
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		String name = generateName(stack);
		double value = 10.0;
		int median = 10000;
		double startprice = 20.0;
		int data = HyperConomy.hc.getInventoryManipulation().getDamageValue(stack);
		HyperEconomy econ = em.getHyperPlayer(player.getName()).getHyperEconomy();
		HyperObject ho =  econ.getHyperObject(id, data);
		if (ho != null) {return;}
		FileConfiguration items = hc.gYH().gFC("items");
		items.set(name + ".information.type", "item");
		items.set(name + ".information.category", "unknown");
		items.set(name + ".information.material", player.getItemInHand().getType().toString());
		items.set(name + ".information.id", id);
		items.set(name + ".information.data", data);
		items.set(name + ".value", value);
		items.set(name + ".price.static", false);
		items.set(name + ".price.staticprice", startprice);
		items.set(name + ".stock.stock", 0);
		items.set(name + ".stock.median", median);
		items.set(name + ".initiation.initiation", true);
		items.set(name + ".initiation.startprice", startprice);
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
		if (HyperConomy.hc.gYH().gFC("items").isSet(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void addAll(Player p) {
		Inventory inventory = p.getInventory();
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			addItem(stack, p);
		}
	}
}
