package regalowl.hyperconomy;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Frameshopcommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		FrameShopHandler fsh = hc.getFrameShopHandler();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);;
			return true;
		}
		Player p = null;
		if (sender instanceof Player) {
			p = (Player)sender;
		} else {
			return true;
		}
		HyperEconomy he = em.getHyperPlayer(p).getHyperEconomy();
		if (args.length == 1) {
			@SuppressWarnings("deprecation")
			List<Block> ltb = p.getLastTwoTargetBlocks(null, 500);
			Block b = ltb.get(0);
			Location bl = b.getLocation();
			String name = he.fixName(args[0]);
			if (he.itemTest(name)) {
				//itdi.testDisplay(bl.getX(), bl.getY() + 1, bl.getZ(), bl.getWorld(), name);
				HyperObject ho = he.getHyperObject(name);
				if (ho == null) {
					p.sendMessage(L.get("INVALID_ITEM_NAME"));
					return true;
				} else {
					if (em.inAnyShop(p)) {
						Shop s = em.getShop(p);
						if (s instanceof PlayerShop) {
							PlayerShop ps = (PlayerShop)s;
							if (p.hasPermission("hyperconomy.admin") || ps.isAllowed(em.getHyperPlayer(p))) {
								fsh.createFrameShop(bl, ho, ps);
							} else {
								p.sendMessage("You don't have permission to create a frameshop here.");
							}
						} else {
							if (p.hasPermission("hyperconomy.admin")) {
								fsh.createFrameShop(bl, ho, s);
							} else {
								p.sendMessage("You don't have permission to create a frameshop here.");
							}
						}
					} else {
						if (p.hasPermission("hyperconomy.admin")) {
							fsh.createFrameShop(bl, ho, null);
						} else {
							p.sendMessage("You don't have permission to create a frameshop here.");
						}
					}
					
					
				}

			} else {
				p.sendMessage(L.get("INVALID_ITEM_NAME"));
			}
		} else {
			p.sendMessage(L.get("MAKEFRAMESHOP_INVALID"));
		}
		return true;
	}
}
