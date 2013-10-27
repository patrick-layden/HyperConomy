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
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {		
			HyperPlayer hp = em.getHyperPlayer(player.getName());
			HyperEconomy he = hp.getHyperEconomy();
			String mat = player.getItemInHand().getType().toString();
			HyperItem ho = he.getHyperItem(player.getItemInHand());
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
				ArrayList<String> enchants = new HyperItemStack(player.getItemInHand()).convertEnchantmentMapToNames(emeta.getStoredEnchants());
				if (enchants.size() == 0) {
					enchantments = "None";
				} else {
					enchantments = sal.stringArrayToString(enchants);
				}
			} else {
				if (new HyperItemStack(inhand).hasenchants()) {
					ArrayList<String> enchants = new HyperItemStack(player.getItemInHand()).convertEnchantmentMapToNames(inhand.getEnchantments());
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
			double durp = 100;
			
			if (ho != null && ho instanceof HyperItem) {
				HyperItem hi = (HyperItem)ho;
				if  (hi.isDurable()) {
					durp = (1 - dura/maxdura) * 100;
					durp = (long)Math.floor(durp + .5);
				}
			}
				

			
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				player.sendMessage(ChatColor.BLUE + "Material: " + ChatColor.AQUA + "" + mat);
				player.sendMessage(ChatColor.BLUE + "ID: " + ChatColor.GREEN + "" + player.getItemInHand().getTypeId());
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
