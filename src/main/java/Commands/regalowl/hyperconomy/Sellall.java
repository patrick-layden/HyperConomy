package regalowl.hyperconomy;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Sellall {
	private HyperConomy hc;
	private ShopFactory s;
	private Calculation calc;
	private Transaction tran;
	private ETransaction ench;
	private Player player;
	private HashMap<Player, String> messages = new HashMap<Player, String>();
	private boolean messageActive = false;

	Sellall(String args[], Player p) {
		hc = HyperConomy.hc;
		s = hc.getShopFactory();
		calc = hc.getCalculation();
		tran = hc.getTransaction();
		LanguageFile L = hc.getLanguageFile();
		ench = hc.getETransaction();
		player = p;
		try {
			if (s.inAnyShop(player)) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".sell")) {
					if (args.length == 0) {
						int slotn = 0;
						Inventory invent = player.getInventory();
						int heldslot = player.getInventory().getHeldItemSlot();
						int itd = 0;
						// Sells the held item slot first.
						if (invent.getItem(heldslot) != null) {
							itd = invent.getItem(heldslot).getTypeId();
						}
						if (itd != 0) {
							int da = calc.getDamageValue(invent.getItem(heldslot));
							HyperObject ho =  hc.getDataFunctions().getHyperObject(itd, da);
							int amount = tran.countInvitems(itd, da, player);
							if (ho != null) {
								String nam = ho.getName();
								if (s.getShop(player).has(nam)) {
									tran.sell(nam, itd, da, amount, player);
								} else {
									sendMessage(L.get("ONE_OR_MORE_CANT_BE_TRADED"));
								}
							}
						}
						// Sells remaining items after the held slot.
						while (slotn < 36) {
							if (invent.getItem(slotn) == null) {
								itd = 0;
							} else {
								itd = invent.getItem(slotn).getTypeId();
							}
							if (itd != 0) {
								ItemStack itemn = invent.getItem(slotn);
								if (ench.hasenchants(itemn) == false) {
									int da = calc.getDamageValue(invent.getItem(slotn));
									HyperObject ho =  hc.getDataFunctions().getHyperObject(itd, da);
									if (ho != null) {
										String nam = ho.getName();
										int amount = tran.countInvitems(itd, da, player);
										if (s.getShop(player).has(nam)) {
											tran.sell(nam, itd, da, amount, player);
										} else {
											sendMessage(L.get("ONE_OR_MORE_CANT_BE_TRADED"));
										}
									}
								} else {
									sendMessage(L.get("CANT_BUY_SELL_ENCHANTED_ITEMS"));
								}
							}
							slotn++;
						}
					} else {
						player.sendMessage(L.get("SELLALL_INVALID"));
						return;
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
					return;
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
				return;
			}
		} catch (Exception e) {
			player.sendMessage(L.get("SELLALL_INVALID"));
			return;
		}
	}

	/**
	 * 
	 * Limits messages sent to player to 1 per second.
	 */
	private void sendMessage(String message) {
		messages.put(player, message);
		if (!messageActive) {
			messageActive = true;
			hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
				public void run() {
					player.sendMessage(messages.get(player));
					messages.remove(player);
					messageActive = false;
				}
			}, 20L);
		}
	}
}
