package regalowl.hyperconomy;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;

public class Iteminfo {
	Iteminfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		DataHandler dh = hc.getDataFunctions();
		InventoryManipulation im = hc.getInventoryManipulation();
		try {		
			HyperPlayer hp = dh.getHyperPlayer(player);
			if (args.length == 1) {
				int givenid = Integer.parseInt(args[0]);
				int dv = 0;
				int newdat = calc.newData(givenid, dv);
				HyperObject ho = dh.getHyperObject(givenid, newdat, hp.getEconomy());
				String nam = "";
				if (ho == null) {
					nam = "Item not in database.";
				} else {
					nam = ho.getName();
				}
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				player.sendMessage(L.get("LINE_BREAK"));
				return;
			} else if (args.length == 2) {
				int givenid = Integer.parseInt(args[0]);;
				int givendam = Integer.parseInt(args[1]);
				int newdat = calc.newData(givenid, givendam);
				HyperObject ho = dh.getHyperObject(givenid, newdat, hp.getEconomy());
				String nam = "";
				if (ho == null) {
					nam = "Item not in database.";
				} else {
					nam = ho.getName();
				}
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				player.sendMessage(L.get("LINE_BREAK"));
				return;
			}
			String mat = player.getItemInHand().getType().toString();
			int itemid = player.getItemInHand().getTypeId();
			int dv = calc.getDamageValue(player.getItemInHand());
			HyperObject ho = dh.getHyperObject(itemid, dv, hp.getEconomy());
			String nam = "";
			if (ho == null) {
				nam = "Item not in database.";
			} else {
				nam = ho.getName();
			}
			
			String enchantments = "";
			ItemStack inhand = player.getItemInHand();
			SerializeArrayList sal = new SerializeArrayList(); 
			if (inhand.getType().equals(Material.ENCHANTED_BOOK)) {
				
				EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)inhand.getItemMeta();
				ArrayList<String> enchants = im.convertEnchantmentMapToNames(emeta.getStoredEnchants());
				if (enchants.size() == 0) {
					enchantments = "None";
				} else {
					enchantments = sal.stringArrayToString(enchants);
				}
			} else {
				if (im.hasenchants(inhand)) {
					ArrayList<String> enchants = im.convertEnchantmentMapToNames(inhand.getEnchantments());
					enchantments = sal.stringArrayToString(enchants);
				} else {
					enchantments = "None";
				}
			}
			
			
			if (player.getItemInHand().getType().equals(Material.FIREWORK)) {
				FireworkMeta meta = (FireworkMeta)inhand.getItemMeta();
				meta.getEffects();
				meta.getPower();
				meta.getEffectsSize();
			}
			

			double dura = player.getItemInHand().getDurability();
			double maxdura = player.getItemInHand().getType().getMaxDurability();
			double durp = (1 - dura/maxdura) * 100;
			if  (ho != null && ho.isDurable()) {
				durp = (long)Math.floor(durp + .5);
			} else {
				durp = 100;
			}
				

			
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				player.sendMessage(ChatColor.BLUE + "Material: " + ChatColor.AQUA + "" + mat);
				player.sendMessage(ChatColor.BLUE + "ID: " + ChatColor.GREEN + "" + itemid);
				player.sendMessage(ChatColor.BLUE + "Damage Value: " + ChatColor.GREEN + "" + player.getItemInHand().getData().getData());
				player.sendMessage(ChatColor.BLUE + "Durability: " + ChatColor.GREEN + "" + (int)dura);
				player.sendMessage(ChatColor.BLUE + "Durability Percent: " + ChatColor.GREEN + "" + durp + "%");
				player.sendMessage(ChatColor.BLUE + "Enchantments: " + ChatColor.AQUA + "" + enchantments);
				player.sendMessage(L.get("LINE_BREAK"));
			return;
		} catch (Exception e) {
			player.sendMessage(ChatColor.DARK_RED + "Invalid item or parameters.  Hold an item and use /iteminfo (id) (damage value)");
		}
	}
}
