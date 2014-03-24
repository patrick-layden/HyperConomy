package regalowl.hyperconomy.command;

import org.bukkit.GameMode;
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
		
		if (player.getGameMode() == GameMode.CREATIVE && hc.gYH().gQFC("config").gB("shop.block-selling-in-creative-mode")) {
			player.sendMessage(L.get("CANT_SELL_CREATIVE"));
			return true;
		}
		try {
			if (em.inAnyShop(player)) {
				Shop s = em.getShop(player);
				if (em.getHyperPlayer(player).hasSellPermission(em.getShop(player))) {
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
		} catch (Exception e) {
			player.sendMessage(L.get("SELLALL_INVALID"));
			return true;
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
			HyperObject hyperItem = he.getHyperObject(stack, em.getShop(trader.getPlayer()));
			PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
			pt.setTradePartner(tradePartner);
			pt.setHyperObject(hyperItem);
			pt.setAmount(stack.getAmount());
			TransactionResponse response = trader.processTransaction(pt);
			if (response.successful()) {
				totalResponse.addSuccess(response.getMessage(), response.getPrice(), response.getSuccessfulObjects().get(0));
			} else {
				totalResponse.addFailed(response.getMessage(), response.getFailedObjects().get(0));
			}
		}
		return totalResponse;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public TransactionResponse sellAll() {
		try {
			LanguageFile L = hc.getLanguageFile();
			TransactionResponse response = new TransactionResponse(hp);
			if (hp == null) {
				response.setFailed();
				response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			response.setSuccessful();
			HyperEconomy econ = em.getEconomy(hp.getEconomy());
			Inventory invent = null;
			if (giveInventory == null) {
				invent = hp.getPlayer().getInventory();
			} else {
				invent = giveInventory;
			}
			for (int slot = 0; slot < invent.getSize(); slot++) {
				if (invent.getItem(slot) != null) {
					ItemStack stack = invent.getItem(slot);
					hyperItem = econ.getHyperItem(stack, em.getShop(hp.getPlayer()));
					if (new HyperItemStack(stack).hasenchants() == false) {
						if (hyperItem != null) {
							if (!em.getShop(hp.getPlayer()).isBanned(hyperItem)) {
								amount = hyperItem.count(hp.getInventory());
								pt.setHyperObject(hyperItem);
								TransactionResponse sresponse = sell();
								if (sresponse.successful()) {
									response.addSuccess(sresponse.getMessage(), sresponse.getPrice(), hyperItem);
								} else {
									response.addFailed(sresponse.getMessage(), hyperItem, stack);
								}
							} else {
								response.addFailed(L.get("CANT_BE_TRADED"), hyperItem, stack);
							}
						} else {
							response.addFailed(L.get("CANT_BE_TRADED"), hyperItem, stack);
						}
					} else {
						response.addFailed(L.get("CANT_BUY_SELL_ENCHANTED_ITEMS"), hyperItem, stack);
					}
				} 
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			hc.gDB().writeError(e);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}
	*/
	
	
	
	
	
	
	
	
}
