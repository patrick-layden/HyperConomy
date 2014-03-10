package regalowl.hyperconomy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperObject;

public class Buy implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);;
			return true;
		}
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if (player == null) {return true;}
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		HyperPlayer hp = em.getHyperPlayer(player);
		HyperEconomy he = hp.getHyperEconomy();
		try {
			if (!em.inAnyShop(player)) {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
				return true;
			}
			Shop s = em.getShop(player);
			if (!hp.hasBuyPermission(em.getShop(player))) {
				player.sendMessage(L.get("NO_TRADE_PERMISSION"));
				return true;
			}
			String name = he.fixName(args[0]);
			HyperObject ho = he.getHyperObject(name, em.getShop(player));
			if (s.isBanned(ho)) {
				player.sendMessage(L.get("CANT_BE_TRADED"));
				return true;
			}
			int amount = 1;
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("max")) {
					if (ho.getType() == HyperObjectType.ITEM) {
						amount = ho.getAvailableSpace(player.getInventory());
					} else if (ho.getType() == HyperObjectType.EXPERIENCE) {
						amount = (int) ho.getStock();
					} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
						amount = 1;
					}
				} else {
					amount = Integer.parseInt(args[1]);
				}
			}
			PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
			pt.setHyperObject(ho);
			pt.setAmount(amount);
			pt.setTradePartner(s.getOwner());
			TransactionResponse response = hp.processTransaction(pt);
			response.sendMessages();
		} catch (Exception e) {
			player.sendMessage(L.get("BUY_INVALID"));
			return true;
		}
		return true;
	}
}
