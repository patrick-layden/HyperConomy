package regalowl.hyperconomy;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Setshop implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);
			return true;
		}
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (player == null) {
			return true;
		}
		EconomyManager em = hc.getEconomyManager();
		if (args.length == 0) {
			player.sendMessage(L.get("SETSHOP_INVALID"));
			return true;
		}
		if (args[0].equalsIgnoreCase("p1")) {
			String name = args[1].replace(".", "").replace(":", "");
			if (em.shopExists(name)) {
				em.getShop(name).setPoint1(player.getLocation());
			} else {
				HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player);
				Shop shop = new ServerShop(name, hp.getEconomy(), hc.getEconomyManager().getGlobalShopAccount());
				shop.setPoint1(player.getLocation());
				shop.setPoint2(player.getLocation());
				shop.setDefaultMessages();
				em.addShop(shop);
			}
			player.sendMessage(L.get("P1_SET"));
		} else if (args[0].equalsIgnoreCase("p2")) {
			String name = args[1].replace(".", "").replace(":", "");
			if (em.shopExists(name)) {
				em.getShop(name).setPoint2(player.getLocation());
			} else {
				HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player);
				Shop shop = new ServerShop(name, hp.getEconomy(), hc.getEconomyManager().getGlobalShopAccount());
				shop.setPoint1(player.getLocation());
				shop.setPoint2(player.getLocation());
				shop.setDefaultMessages();
				em.addShop(shop);
			}
			player.sendMessage(L.get("P2_SET"));
		} else if (args[0].equalsIgnoreCase("owner") || args[0].equalsIgnoreCase("o")) {
			if (args.length < 3) {
				return true;
			}
			HyperAccount owner = null;
			if (em.hasAccount(args[1])) {
				owner = em.getHyperPlayer(args[1]);
			} else {
				if (em.hasBank(args[1])) {
					owner = em.getHyperBank(args[1]);
				} else {
					player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				}
			}
			String name = args[2].replace(".", "").replace(":", "");
			if (em.shopExists(name)) {
				em.getShop(name).setOwner(owner);
			} else {
				player.sendMessage(L.get("SHOP_NOT_EXIST"));

			}
			player.sendMessage(L.get("OWNER_SET"));
		}
		return true;
	}
}
