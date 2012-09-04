package regalowl.hyperconomy;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class Iteminfo {
	Iteminfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		ETransaction ench = hc.getETransaction();
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
				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
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
				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				return;
			}
			String mat = player.getItemInHand().getType().toString();
			int itemid = player.getItemInHand().getTypeId();
			int dv = calc.getpotionDV(player.getItemInHand());
			int newdat = calc.newData(itemid, dv);
			String ke = itemid + ":" + newdat;
			String nam = hc.getnameData(ke);

			if (nam == null) {
				nam = "Item not in database.";
			}
			String enchantments = "";
			if (ench.hasenchants(player.getItemInHand())) {
			Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
			while (ite.hasNext()) {
				String rawstring = ite.next().toString();
				String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
				Enchantment en = null;
				en = Enchantment.getByName(enchname);
				int lvl = player.getItemInHand().getEnchantmentLevel(en);
				String na = hc.getenchantData(enchname);
				String fnam = na + lvl;
				if (enchantments.length() == 0) {
					enchantments = fnam;
				} else {
					enchantments = enchantments + ", " + fnam;
				}
			}
			} else {
				enchantments = "None";
			}
			double dura = player.getItemInHand().getDurability();
			double maxdura = player.getItemInHand().getType().getMaxDurability();
			double durp = (1 - dura/maxdura) * 100;
			if  (calc.testId(itemid)) {
				durp = (long)Math.floor(durp + .5);
			} else {
				durp = 100;
			}
				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				player.sendMessage(ChatColor.BLUE + "Material: " + ChatColor.AQUA + "" + mat);
				player.sendMessage(ChatColor.BLUE + "ID: " + ChatColor.GREEN + "" + itemid);
				player.sendMessage(ChatColor.BLUE + "Damage Value: " + ChatColor.GREEN + "" + player.getItemInHand().getData().getData());
				player.sendMessage(ChatColor.BLUE + "Durability: " + ChatColor.GREEN + "" + (int)dura);
				player.sendMessage(ChatColor.BLUE + "Durability Percent: " + ChatColor.GREEN + "" + durp + "%");
				player.sendMessage(ChatColor.BLUE + "Enchantments: " + ChatColor.AQUA + "" + enchantments);
				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
			return;
		} catch (Exception e) {
			player.sendMessage(ChatColor.DARK_RED + "Invalid item or parameters.  Hold an item and use /iteminfo (id) (damage value)");
		}
	}
}
