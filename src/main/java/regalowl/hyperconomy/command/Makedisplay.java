package regalowl.hyperconomy.command;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.display.ItemDisplayFactory;
import regalowl.hyperconomy.util.LanguageFile;

public class Makedisplay {
	Makedisplay(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (!hc.getConf().getBoolean("enable-feature.item-displays")) {
			player.sendMessage(L.get("ENABLE_ITEM_DISPLAYS"));
			return;
		}
		DataManager em = hc.getDataManager();
		ItemDisplayFactory itdi = hc.getItemDisplay();
		HyperPlayer hp = em.getHyperPlayer(player);
		HyperEconomy he = hp.getHyperEconomy();
		
		if (args.length == 1) {
			@SuppressWarnings("deprecation")
			Block b = player.getTargetBlock(null, 500);
			Location bl = b.getLocation();
			String name = he.fixName(args[0]);
			if (he.itemTest(name)) {
				itdi.addDisplay(bl.getX(), bl.getY() + 1, bl.getZ(), bl.getWorld().getName(), name);
			} else {
				player.sendMessage(L.get("INVALID_ITEM_NAME"));
			}
		} else if (args.length == 2 && args[1].equalsIgnoreCase("u")) {
			String name = he.fixName(args[0]);
			if (he.itemTest(name)) {
				double x = player.getLocation().getX();
				double y = player.getLocation().getY();
				double z = player.getLocation().getZ();
				World w = player.getLocation().getWorld();
				itdi.addDisplay(x, y, z, w.getName(), name);
			} else {
				player.sendMessage(L.get("INVALID_ITEM_NAME"));
			}
		} else {
			player.sendMessage(L.get("MAKEDISPLAY_INVALID"));
		}
	}
}
