package regalowl.hyperconomy;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Makedisplay {
	Makedisplay(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (!hc.getYaml().getConfig().getBoolean("config.use-item-displays")) {
			player.sendMessage(L.get("ENABLE_ITEM_DISPLAYS"));
			return;
		}
		DataHandler sf = hc.getDataFunctions();
		ItemDisplayFactory itdi = hc.getItemDisplay();
		if (args.length == 1) {
			Block b = player.getTargetBlock(null, 500);
			Location bl = b.getLocation();
			String name = hc.getDataFunctions().fixName(args[0]);
			if (hc.getDataFunctions().itemTest(name)) {
				String economy = sf.getHyperPlayer(player).getEconomy();
				itdi.testDisplay(bl.getX(), bl.getY() + 1, bl.getZ(), bl.getWorld(), name, economy);
			} else {
				player.sendMessage(L.get("INVALID_ITEM_NAME"));
			}
		} else {
			player.sendMessage(L.get("MAKEDISPLAY_INVALID"));
		}
	}
}
