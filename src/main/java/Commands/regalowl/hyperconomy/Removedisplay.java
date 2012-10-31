package regalowl.hyperconomy;


import org.bukkit.World;
import org.bukkit.entity.Player;

public class Removedisplay {
	Removedisplay(Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (!hc.getYaml().getConfig().getBoolean("config.use-item-displays")) {
			player.sendMessage(L.get("ITEMDISPLAYS_MUST_BE_ENABLED"));
			return;
		}
		ItemDisplay itdi = hc.getItemDisplay();
		int x = player.getLocation().getBlockX();
		int z = player.getLocation().getBlockZ();
		World w = player.getLocation().getWorld();
		boolean success = itdi.removeDisplay(x, z, w);
		if (success) {
			player.sendMessage(L.get("DISPLAY_REMOVED"));
		} else {
			player.sendMessage(L.get("NO_DISPLAY_DETECTED"));
		}
	}
}
