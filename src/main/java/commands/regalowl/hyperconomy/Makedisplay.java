package regalowl.hyperconomy;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Makedisplay {
	Makedisplay(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (!hc.gYH().gFC("config").getBoolean("config.use-item-displays")) {
			player.sendMessage(L.get("ENABLE_ITEM_DISPLAYS"));
			return;
		}
		EconomyManager em = hc.getEconomyManager();
		ItemDisplayFactory itdi = hc.getItemDisplay();
		HyperPlayer hp = em.getHyperPlayer(player.getName());
		HyperEconomy he = hp.getHyperEconomy();
		
		if (args.length == 1) {
			Block b = player.getTargetBlock(null, 500);
			Location bl = b.getLocation();
			String name = he.fixName(args[0]);
			if (he.itemTest(name)) {
				String economy = hp.getEconomy();
				itdi.testDisplay(bl.getX(), bl.getY() + 1, bl.getZ(), bl.getWorld(), name);
			} else {
				player.sendMessage(L.get("INVALID_ITEM_NAME"));
			}
		} else if (args.length == 2 && args[1].equalsIgnoreCase("u")) {
			String name = he.fixName(args[0]);
			if (he.itemTest(name)) {
				String economy = hp.getEconomy();
				double x = player.getLocation().getX();
				double y = player.getLocation().getY();
				double z = player.getLocation().getZ();
				World w = player.getLocation().getWorld();
				itdi.testDisplay(x, y, z, w, name);
			} else {
				player.sendMessage(L.get("INVALID_ITEM_NAME"));
			}
		} else {
			player.sendMessage(L.get("MAKEDISPLAY_INVALID"));
		}
	}
}
