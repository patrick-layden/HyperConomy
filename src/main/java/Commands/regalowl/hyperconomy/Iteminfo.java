package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;

public class Iteminfo {
	Iteminfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		ETransaction ench = hc.getETransaction();
		LanguageFile L = hc.getLanguageFile();
		try {		
			if (args.length == 1) {
				int givenid = Integer.parseInt(args[0]);
				int dv = 0;
				int newdat = calc.newData(givenid, dv);
				String ke = givenid + ":" + newdat;
				String nam = hc.getnameData(ke);
				
				if (nam == null) {
					nam = "Item not in database.";
				}
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				player.sendMessage(L.get("LINE_BREAK"));
				return;
			} else if (args.length == 2) {
				int givenid = Integer.parseInt(args[0]);;
				int givendam = Integer.parseInt(args[1]);
				int newdat = calc.newData(givenid, givendam);
				String ke = givenid + ":" + newdat;
				String nam = hc.getnameData(ke);
				
				if (nam == null) {
					nam = "Item not in database.";
				}
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				player.sendMessage(L.get("LINE_BREAK"));
				return;
			}
			String mat = player.getItemInHand().getType().toString();
			int itemid = player.getItemInHand().getTypeId();
			int dv = calc.getDamageValue(player.getItemInHand());
			String ke = itemid + ":" + dv;
			String nam = hc.getnameData(ke);

			if (nam == null) {
				nam = "Item not in database.";
			}
			
			String enchantments = "";
			ItemStack inhand = player.getItemInHand();
			SerializeArrayList sal = new SerializeArrayList(); 
			if (inhand.getType().equals(Material.ENCHANTED_BOOK)) {
				
				EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)inhand.getItemMeta();
				ArrayList<String> enchants = ench.convertEnchantmentMapToNames(emeta.getStoredEnchants());
				if (enchants.size() == 0) {
					enchantments = "None";
				} else {
					enchantments = sal.stringArrayToString(enchants);
				}
			} else {
				if (ench.hasenchants(inhand)) {
					ArrayList<String> enchants = ench.convertEnchantmentMapToNames(inhand.getEnchantments());
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
			if  (calc.isDurable(itemid)) {
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
