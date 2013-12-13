package regalowl.hyperconomy;



import org.bukkit.Material;
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
			String displayName = "";
			if (args.length >= 1) {
				displayName = args[0];
				if (displayName.equalsIgnoreCase("all")) {
					addAll(player);
					player.sendMessage(L.get("INVENTORY_ADDED"));
					return true;
				}
				if (displayName.equalsIgnoreCase("help")) {
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
			HyperItemStack his = new HyperItemStack(stack);
			String name = stack.getType() + "_" + his.getDamageValue();
			if (econ.objectTest(name) || name.equalsIgnoreCase("")) {
				name = generateName(stack);
			}
			FileConfiguration objects = hc.gYH().gFC("objects");
			objects.set(name + ".information.type", "item");
			objects.set(name + ".information.material", stack.getType().toString());
			String aliases = displayName.replace("_", "") + ",";
			int data = his.getDamageValue();
			objects.set(name + ".information.data", data);
			objects.set(name + ".value", value);
			objects.set(name + ".price.static", false);
			objects.set(name + ".price.staticprice", value*2);
			objects.set(name + ".stock.stock", 0);
			objects.set(name + ".stock.median", median);
			objects.set(name + ".initiation.initiation", true);
			objects.set(name + ".initiation.startprice", value*2);
			objects.set(name + ".name.display", displayName);
			objects.set(name + ".name.aliases", aliases);
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
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		if (stack.getType() == Material.AIR) {return;}
		String displayName = generateName(stack);
		HyperItemStack his = new HyperItemStack(stack);
		String name = stack.getType() + "_" + his.getDamageValue();
		String aliases = displayName.replace("_", "") + ",";
		double value = 10.0;
		int median = 10000;
		double startprice = 20.0;
		int data = his.getDamageValue();
		HyperEconomy econ = em.getHyperPlayer(player.getName()).getHyperEconomy();
		HyperObject ho =  econ.getHyperObject(stack);
		if (ho != null) {return;}
		FileConfiguration objects = hc.gYH().gFC("objects");
		objects.set(name + ".information.type", "item");
		objects.set(name + ".information.material", player.getItemInHand().getType().toString());
		objects.set(name + ".information.data", data);
		objects.set(name + ".value", value);
		objects.set(name + ".price.static", false);
		objects.set(name + ".price.staticprice", startprice);
		objects.set(name + ".stock.stock", 0);
		objects.set(name + ".stock.median", median);
		objects.set(name + ".initiation.initiation", true);
		objects.set(name + ".initiation.startprice", startprice);
		objects.set(name + ".name.display", displayName);
		objects.set(name + ".name.aliases", aliases);
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
		if (HyperConomy.hc.gYH().gFC("objects").isSet(name)) {
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
