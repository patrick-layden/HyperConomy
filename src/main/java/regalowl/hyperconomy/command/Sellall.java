package regalowl.hyperconomy.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;


public class Sellall implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);;
			return true;
		}
		LanguageFile L = hc.getLanguageFile();
		DataManager em = hc.getDataManager();

		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		} else {
			return true;
		}

		if (em.getHyperShopManager().inAnyShop(player)) {
			Shop s = em.getHyperShopManager().getShop(player);
			if (em.getHyperPlayer(player).hasSellPermission(em.getHyperShopManager().getShop(player))) {
				if (args.length == 0) {
					TransactionResponse response = sellAll(em.getHyperPlayer(player), s.getOwner());
					response.sendMessages();
					if (response.getFailedObjects().size() == 0) {
						player.sendMessage(L.get("LINE_BREAK"));
						player.sendMessage(L.get("ALL_ITEMS_SOLD"));
						player.sendMessage(L.f(L.get("SOLD_ITEMS_FOR"), response.getTotalPrice()));
						player.sendMessage(L.get("LINE_BREAK"));
					} else {
						player.sendMessage(L.get("LINE_BREAK"));
						player.sendMessage(L.get("ONE_OR_MORE_CANT_BE_TRADED"));
						player.sendMessage(L.f(L.get("SOLD_ITEMS_FOR"), response.getTotalPrice()));
						player.sendMessage(L.get("LINE_BREAK"));
					}
				} else {
					player.sendMessage(L.get("SELLALL_INVALID"));
				}
			} else {
				player.sendMessage(L.get("NO_TRADE_PERMISSION"));
			}
		} else {
			player.sendMessage(L.get("MUST_BE_IN_SHOP"));
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
	public TransactionResponse sellAll(HyperPlayer trader, HyperAccount tradePartner) {
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		Inventory inventory = trader.getPlayer().getInventory();
		HyperEconomy he = trader.getHyperEconomy();
		TransactionResponse totalResponse = new TransactionResponse(trader);
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			if (inventory.getItem(slot) == null) {continue;}
			ItemStack stack = inventory.getItem(slot);
			HyperObject ho = he.getHyperObject(stack, em.getHyperShopManager().getShop(trader.getPlayer()));
			if (ho == null) {continue;}
			int amount = ho.count(inventory);
			PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
			pt.setObeyShops(true);
			pt.setTradePartner(tradePartner);
			pt.setHyperObject(ho);
			pt.setAmount(amount);
			TransactionResponse response = trader.processTransaction(pt);
			if (response.successful()) {
				totalResponse.addSuccess(response.getMessage(), response.getPrice(), response.getSuccessfulObjects().get(0));
			} else {
				totalResponse.addFailed(response.getMessage(), response.getFailedObjects().get(0));
			}
		}
		return totalResponse;
	}

	
	
	
	
	
}
