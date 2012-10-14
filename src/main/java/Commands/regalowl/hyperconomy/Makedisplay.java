package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Makedisplay {
	Makedisplay(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		ItemDisplay itdi = hc.getItemDisplay();
		if (args.length == 1) {
			String name = hc.fixName(args[0]);
			if (hc.itemTest(name)) {
				String economy = sf.getPlayerEconomy(player);
				double x = player.getLocation().getX();
				double y = player.getLocation().getY();
				double z = player.getLocation().getZ();
				World w = player.getLocation().getWorld();
				itdi.testDisplay(x, y, z, w, name, economy);
			} else {
				player.sendMessage(ChatColor.DARK_RED + "Invalid item name.");
			}
		} else {
			player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters. Use /makedisplay [name]");
		}
	}
}
