package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;

import regalowl.databukkit.CommonFunctions;

public class Iteminfo {
	@SuppressWarnings("deprecation")
	Iteminfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			HyperPlayer hp = em.getHyperPlayer(player.getName());
			HyperEconomy he = hp.getHyperEconomy();
			if (args.length == 0) {
				String mat = player.getItemInHand().getType().toString();
				HyperItem ho = he.getHyperItem(player.getItemInHand());
				String displayName = "";
				if (ho == null) {
					displayName = "Item not in database.";
				} else {
					displayName = ho.getDisplayName();
				}

				String enchantments = "";
				ItemStack inhand = player.getItemInHand();
				CommonFunctions cf = hc.gCF();
				if (inhand.getType().equals(Material.ENCHANTED_BOOK)) {

					EnchantmentStorageMeta emeta = (EnchantmentStorageMeta) inhand.getItemMeta();
					ArrayList<String> enchants = new HyperItemStack(player.getItemInHand()).convertEnchantmentMapToNames(emeta.getStoredEnchants());
					if (enchants.size() == 0) {
						enchantments = "None";
					} else {
						enchantments = cf.implode(enchants, ",");
					}
				} else {
					if (new HyperItemStack(inhand).hasenchants()) {
						ArrayList<String> enchants = new HyperItemStack(player.getItemInHand()).convertEnchantmentMapToNames(inhand.getEnchantments());
						enchantments = cf.implode(enchants, "");
					} else {
						enchantments = "None";
					}
				}

				if (player.getItemInHand().getType().equals(Material.FIREWORK)) {
					FireworkMeta meta = (FireworkMeta) inhand.getItemMeta();
					meta.getEffects();
					meta.getPower();
					meta.getEffectsSize();
				}

				double dura = player.getItemInHand().getDurability();
				double maxdura = player.getItemInHand().getType().getMaxDurability();
				double durp = 100;

				if (ho != null && ho instanceof HyperItem) {
					HyperItem hi = (HyperItem) ho;
					if (hi.isDurable()) {
						durp = (1 - dura / maxdura) * 100;
						durp = (long) Math.floor(durp + .5);
					}
				}

				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + displayName);
				player.sendMessage(ChatColor.BLUE + "Material: " + ChatColor.AQUA + "" + mat);
				player.sendMessage(ChatColor.BLUE + "ID: " + ChatColor.AQUA + "" + player.getItemInHand().getTypeId());
				player.sendMessage(ChatColor.BLUE + "Damage Value: " + ChatColor.GREEN + "" + player.getItemInHand().getData().getData());
				player.sendMessage(ChatColor.BLUE + "Durability: " + ChatColor.GREEN + "" + (int) dura);
				player.sendMessage(ChatColor.BLUE + "Durability Percent: " + ChatColor.GREEN + "" + durp + "%");
				player.sendMessage(ChatColor.BLUE + "Enchantments: " + ChatColor.AQUA + "" + enchantments);
				player.sendMessage(L.get("LINE_BREAK"));
				return;
			} else {
				HyperObject ho = he.getHyperObject(args[0]);
				if (ho == null) {
					player.sendMessage(ChatColor.BLUE + "Object not found.");
					return;
				}
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + ho.getDisplayName());
				if (ho != null && ho instanceof HyperItem) {
					HyperItem hi = (HyperItem) ho;
					player.sendMessage(ChatColor.BLUE + "Material: " + ChatColor.AQUA + "" + hi.getMaterial());
					player.sendMessage(ChatColor.BLUE + "Damage Value: " + ChatColor.GREEN + "" + hi.getData());
					player.sendMessage(ChatColor.BLUE + "Durability: " + ChatColor.GREEN + "" + hi.getDurability());
				}
				player.sendMessage(L.get("LINE_BREAK"));
				return;
			}
		} catch (Exception e) {
			player.sendMessage(ChatColor.DARK_RED + "Invalid item or parameters.  Hold an item and use /iteminfo (name)");
		}
	}
}
