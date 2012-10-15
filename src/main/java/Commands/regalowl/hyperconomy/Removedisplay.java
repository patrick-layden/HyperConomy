package regalowl.hyperconomy;


import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Removedisplay {
	Removedisplay(Player player) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getYaml().getConfig().getBoolean("config.use-item-displays")) {
			player.sendMessage(ChatColor.DARK_RED + "Item displays must be enabled to use this command.");
			return;
		}
		ItemDisplay itdi = hc.getItemDisplay();
		int x = player.getLocation().getBlockX();
		int y = player.getLocation().getBlockY();
		int z = player.getLocation().getBlockZ();
		World w = player.getLocation().getWorld();
		boolean success = itdi.removeDisplay(x, y, z, w);
		if (success) {
			player.sendMessage(ChatColor.GOLD + "Display removed!");
		} else {
			player.sendMessage(ChatColor.DARK_RED + "There was no display detected where you're standing.");
		}
	}
}
