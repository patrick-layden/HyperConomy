package regalowl.hyperconomy.command;


import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.display.ItemDisplayFactory;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.SimpleLocation;

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
			double x = Math.floor(b.getLocation().getX()) + .5;
			double y = b.getLocation().getBlockY() + 1;
			double z = Math.floor(b.getLocation().getZ()) + .5;
			World w = player.getLocation().getWorld();
			SimpleLocation sl = new SimpleLocation(w.getName(),x,y,z);
			boolean success = itdi.removeDisplay(sl);
			if (success) {
				player.sendMessage(L.get("DISPLAY_REMOVED"));
			} else {
				player.sendMessage(L.get("NO_DISPLAY_DETECTED"));
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("u")) {
			double x = Math.floor(player.getLocation().getX()) + .5;
			double z = Math.floor(player.getLocation().getZ()) + .5;
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
