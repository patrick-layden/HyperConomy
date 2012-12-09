package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TransactionSign implements Listener {
	private HyperConomy hc;
	private Transaction tran;
	private ETransaction ench;
	private SQLFunctions sf;
	private Set<String> names;
	private String playerecon;
	private Shop shop;

	public void setTransactionSign(HyperConomy hyperc, Transaction trans, Calculation cal, ETransaction enchant, Log lo, Account account, InfoSign infosign, Notification notify) {
		hc = hyperc;
		tran = trans;
		ench = enchant;
		sf = hc.getSQLFunctions();
		names = new HashSet<String>();
		shop = hc.getShop();
		ArrayList<String> anames = hc.getNames();
		for (int i = 0; i < anames.size(); i++) {
			names.add(anames.get(i));
		}
		if (hc.getYaml().getConfig().getBoolean("config.use-transaction-signs")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent scevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-transaction-signs")) {
			String line3 = ChatColor.stripColor(scevent.getLine(2)).trim();
			if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
				String line4 = ChatColor.stripColor(scevent.getLine(3)).trim();
				try {
					Integer.parseInt(line4);
					String line12 = ChatColor.stripColor(scevent.getLine(0)).trim() + ChatColor.stripColor(scevent.getLine(1)).trim();
					line12 = hc.fixName(line12);
					if (names.contains(line12.toLowerCase())) {
						if (scevent.getPlayer().hasPermission("hyperconomy.createsign")) {
							scevent.setLine(0, "\u00A71" + scevent.getLine(0));
							scevent.setLine(1, "\u00A71" + scevent.getLine(1));
							if (line3.equalsIgnoreCase("[sell:buy]")) {
								scevent.setLine(2, "\u00A7f[Sell:Buy]");
							} else if (line3.equalsIgnoreCase("[sell]")) {
								scevent.setLine(2, "\u00A7f[Sell]");
							} else if (line3.equalsIgnoreCase("[buy]")) {
								scevent.setLine(2, "\u00A7f[Buy]");
							}
							scevent.setLine(3, "\u00A7a" + scevent.getLine(3));
						} else if (!scevent.getPlayer().hasPermission("hyperconomy.createsign")) {
							scevent.setLine(0, "");
							scevent.setLine(1, "");
							scevent.setLine(2, "");
							scevent.setLine(3, "");
						}
						if (scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.SIGN_POST) || scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.WALL_SIGN)) {
							Sign s = (Sign) scevent.getBlock().getState();
							s.update();
						}
					}
				} catch (Exception e) {
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEvent(PlayerInteractEvent ievent) {
		LanguageFile L = hc.getLanguageFile();
		boolean requireShop = hc.getYaml().getConfig().getBoolean("config.require-transaction-signs-to-be-in-shop");
		if (hc.getYaml().getConfig().getBoolean("config.use-transaction-signs")) {
			Player p = ievent.getPlayer();
			playerecon = sf.getPlayerEconomy(p.getName());
			boolean sneak = false;
			if (p.isSneaking()) {
				sneak = true;
			}
			if (sneak && p.hasPermission("hyperconomy.admin")) {
				ievent.setCancelled(false);
				return;
			}
			Block b = ievent.getClickedBlock();
			if (b != null && b.getType().equals(Material.SIGN_POST) || b != null && b.getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) b.getState();
				String line3 = ChatColor.stripColor(s.getLine(2)).trim();
				if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
					String line4 = ChatColor.stripColor(s.getLine(3)).trim();
					int amount = 0;
					try {
						amount = Integer.parseInt(line4);
					} catch (Exception e) {
						return;
					}
					String line12 = ChatColor.stripColor(s.getLine(0)).trim() + ChatColor.stripColor(s.getLine(1)).trim();
					line12 = hc.fixName(line12);
					if (names.contains(line12.toLowerCase())) {
						if (!s.getLine(0).startsWith("\u00A7")) {
							s.setLine(0, "\u00A71" + s.getLine(0));
							s.setLine(1, "\u00A71" + s.getLine(1));
							s.setLine(2, "\u00A7f" + s.getLine(2));
							s.setLine(3, "\u00A7a" + s.getLine(3));
							s.update();
						}
						String action = ievent.getAction().name();
						if (action.equalsIgnoreCase("RIGHT_CLICK_BLOCK")) {
							if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[buy]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								if (p.hasPermission("hyperconomy.buysign")) {
									shop.setinShop(p);
									if ((shop.inShop() != -1 && requireShop) || !requireShop) {
										if (hc.itemTest(line12)) {
											int id = sf.getId(line12, playerecon);
											if (id >= 0) {
												if (!hc.isLocked()) {
													tran.buy(line12, amount, id, sf.getData(line12, playerecon), p);
												} else {
													p.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
												}
											} else if (id == -1) {
												if (!hc.isLocked()) {
													tran.buyXP(line12, amount, p);
												} else {
													p.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
												}
											}
										} else if (hc.enchantTest(line12)) {
											if (!hc.isLocked()) {
												ench.buyEnchant(line12, p);
											} else {
												p.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
											}
										}
									} else {
										p.sendMessage(L.get("TRANSACTION_SIGN_MUST_BE_IN_SHOP"));
									}
								} else {
									p.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
								}
								ievent.setCancelled(true);
								s.setLine(0, l1);
								s.setLine(1, l2);
								s.setLine(2, l3);
								s.setLine(3, l4);
								s.update();
							}
						} else if (action.equalsIgnoreCase("LEFT_CLICK_BLOCK")) {
							if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								if (p.hasPermission("hyperconomy.sellsign")) {
									if ((shop.inShop() != -1 && requireShop) || !requireShop) {
										if (hc.itemTest(line12)) {
											int id = sf.getId(line12, playerecon);
											if (id >= 0) {
												if (!hc.isLocked()) {
													tran.sell(line12, id, sf.getData(line12, playerecon), amount, p);
												} else {
													p.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
												}
											} else if (id == -1) {
												if (!hc.isLocked()) {
													tran.sellXP(line12, amount, p);
												} else {
													p.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
												}
											}
										} else if (hc.enchantTest(line12)) {
											if (!hc.isLocked()) {
												ench.sellEnchant(line12, p);
											} else {
												p.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
											}
										}
									} else {
										p.sendMessage(L.get("TRANSACTION_SIGN_MUST_BE_IN_SHOP"));
									}
								} else {
									p.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
								}
								ievent.setCancelled(true);
								s.setLine(0, l1);
								s.setLine(1, l2);
								s.setLine(2, l3);
								s.setLine(3, l4);
								s.update();
							} else if (line3.equalsIgnoreCase("[buy]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								ievent.setCancelled(true);
								s.setLine(0, l1);
								s.setLine(1, l2);
								s.setLine(2, l3);
								s.setLine(3, l4);
								s.update();
							}
						}
					}
				}
			}
		}
	}
}
