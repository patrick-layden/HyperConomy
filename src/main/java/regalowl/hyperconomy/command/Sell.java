package regalowl.hyperconomy.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;

public class Sell implements CommandExecutor {
	

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
		DataManager em = hc.getDataManager();
		HyperPlayer hp = em.getHyperPlayer(player);
		HyperEconomy he = hp.getHyperEconomy();

		try {
			Shop s = em.getHyperShopManager().getShop(player);
			String name = he.fixName(args[0]);
			HyperObject ho = he.getHyperObject(name, em.getHyperShopManager().getShop(player));
			int amount = 1;
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("max")) {
					if (ho.getType() == HyperObjectType.ITEM) {
						amount = ho.count(player.getInventory());
					} else if (ho.getType() == HyperObjectType.EXPERIENCE) {
						amount = hp.getTotalXpPoints();
					} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
						amount = 1;
					}
				} else {
					amount = Integer.parseInt(args[1]);
				}
			}
			if (amount > 10000) {
				amount = 10000;
			}
			PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
			pt.setObeyShops(true);
			pt.setHyperObject(ho);
			pt.setAmount(amount);
			pt.setTradePartner(s.getOwner());
			TransactionResponse response = hp.processTransaction(pt);
			response.sendMessages(); 
		} catch (Exception e) {
			player.sendMessage(L.get("SELL_INVALID"));
			return true;
		}
		return true;
	}
}
