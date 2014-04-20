package regalowl.hyperconomy.command;


import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.display.ItemDisplayFactory;
import regalowl.hyperconomy.util.LanguageFile;

public class Removedisplay {
	Removedisplay(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (!hc.getConf().getBoolean("enable-feature.item-displays")) {
			player.sendMessage(L.get("ITEMDISPLAYS_MUST_BE_ENABLED"));
			return;
		}
		ItemDisplayFactory itdi = hc.getItemDisplay();

		if (args.length == 0) {
			@SuppressWarnings("deprecation")
			Block b = player.getTargetBlock(null, 500);
			int x = (int) Math.floor(b.getLocation().getX());
			int y = b.getLocation().getBlockY() + 1;
			int z = (int) Math.floor(b.getLocation().getZ());
			World w = player.getLocation().getWorld();
			boolean success = itdi.removeDisplay(x, y, z, w);
			if (success) {
				player.sendMessage(L.get("DISPLAY_REMOVED"));
			} else {
				player.sendMessage(L.get("NO_DISPLAY_DETECTED"));
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("u")) {
			int x = player.getLocation().getBlockX();
			int z = player.getLocation().getBlockZ();
			World w = player.getLocation().getWorld();
			boolean success = itdi.removeDisplay(x, z, w);
			if (success) {
				player.sendMessage(L.get("DISPLAY_REMOVED"));
			} else {
				player.sendMessage(L.get("NO_DISPLAY_DETECTED_HERE"));
			}
		}
	}
}
