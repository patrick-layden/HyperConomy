package regalowl.hyperconomy.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.util.LanguageFile;

public class Iteminfo {
	Iteminfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataManager em = hc.getDataManager();
		HyperPlayer hp = em.getHyperPlayer(player.getName());
		HyperEconomy he = hp.getHyperEconomy();
		SerializableItemStack sis = null;
		HyperObject ho = null;
		if (args.length == 0) {
			ho = he.getHyperObject(player.getItemInHand());
			sis = new SerializableItemStack(player.getItemInHand());
		} else {
			ho = he.getHyperObject(args[0]);
			if (ho == null) {
				player.sendMessage(ChatColor.BLUE + "Object not found.");
				return;
			}
			ItemStack s = ho.getItemStack();
			sis = new SerializableItemStack(s);
		}
		player.sendMessage(L.get("LINE_BREAK"));
		if (ho == null) {
			player.sendMessage(ChatColor.RED + "Item not in database.");
		} else {
			player.sendMessage(ChatColor.BLUE + "Identifier: " + ChatColor.AQUA + "" + ho.getName());
			player.sendMessage(ChatColor.BLUE + "HyperConomy Name: " + ChatColor.AQUA + "" + ho.getDisplayName());
			player.sendMessage(ChatColor.BLUE + "Aliases: " + ChatColor.AQUA + "" + ho.getAliasesString());
		}
		sis.displayInfo(player, ChatColor.BLUE, ChatColor.AQUA);
		player.sendMessage(L.get("LINE_BREAK"));
	}
}
