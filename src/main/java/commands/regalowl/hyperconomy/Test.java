package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Test {

	private HyperConomy hc;
	Test(Player player, String args[]) {
		if (args.length == 1) {
			player.sendMessage("Type /hc test confirm to proceed.  This command will test HyperConomy and should only be used on test installs.");
			player.sendMessage("Make one InfoSign and on ItemDisplay near you.");
		} else if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
			hc = HyperConomy.hc;
			hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
				public void run() {
					ArrayList<InfoSign> infoSigns = hc.getInfoSignHandler().getInfoSigns();
					int size = infoSigns.size();
					if (size > 0) {
						InfoSign isign = infoSigns.get(0);
						String key = isign.getKey();
						isign.setData(key, SignType.BUY, "emerald", "default");
						hc.getInfoSignHandler().updateSigns();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						isign.setData(key, SignType.HISTORY, "diamond", "default");
						hc.getInfoSignHandler().updateSigns();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						isign.setData(key, SignType.HISTORY, "diamond", "default");
						hc.getInfoSignHandler().updateSigns();
					}
				}
			});

			
		}
	}
}
