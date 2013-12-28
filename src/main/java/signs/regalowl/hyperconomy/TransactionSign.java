package regalowl.hyperconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class TransactionSign implements Listener {
	private HyperConomy hc;
	private EconomyManager em;
	TransactionSign() {
		hc = HyperConomy.hc;
		em = hc.getEconomyManager();
		if (hc.gYH().gFC("config").getBoolean("config.use-transaction-signs")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
		try {
			if (hc.gYH().gFC("config").getBoolean("config.allow-scrolling-transaction-signs")) {
				Player p = event.getPlayer();
				HyperEconomy he = em.getHyperPlayer(p.getName()).getHyperEconomy();
				Block b = null;
				try {
					b = p.getTargetBlock(null, 500);
				} catch (Exception e) {
					// do nothing, this method seems to be bugged in bukkit
					return;
				}

				if (b != null && (b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.WALL_SIGN))) {
					Sign s = (Sign) b.getState();
					String line3 = ChatColor.stripColor(s.getLine(2)).trim();
					if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
						String line12 = ChatColor.stripColor(s.getLine(0)).trim() + ChatColor.stripColor(s.getLine(1)).trim();
						line12 = he.fixName(line12);
						if (he.objectTest(line12)) {
							String line4 = ChatColor.stripColor(s.getLine(3)).trim();
							int amount = 0;
							try {
								amount = Integer.parseInt(line4);
							} catch (Exception e) {
								amount = 0;
							}
							int change = 1;
							if (p.isSneaking()) {
								change = 10;
							}
							int ps = event.getPreviousSlot();
							int ns = event.getNewSlot();
							if (ns == 0 && ps == 8) {
								ns = 9;
							} else if (ns == 8 && ps == 0) {
								ns = -1;
							}
							if (ns > ps) {
								amount -= change;
							} else if (ns < ps) {
								amount += change;
							}
							if (amount < 0) {
								amount = 0;
							} else if (amount > 512) {
								amount = 512;
							}
							s.setLine(3, "\u00A7a" + amount);
							s.update();
						}
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent scevent) {
		try {
			if (hc.gYH().gFC("config").getBoolean("config.use-transaction-signs")) {
				String line3 = ChatColor.stripColor(scevent.getLine(2)).trim();
				if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
					String line4 = ChatColor.stripColor(scevent.getLine(3)).trim();
					int amount = 0;
					try {
						amount = Integer.parseInt(line4);
					} catch (Exception e) {
						amount = 0;
					}
					String line12 = ChatColor.stripColor(scevent.getLine(0)).trim() + ChatColor.stripColor(scevent.getLine(1)).trim();
					line12 = em.getEconomy("default").fixName(line12);
					if (em.getEconomy("default").objectTest(line12)) {
						if (scevent.getPlayer().hasPermission("hyperconomy.createsign")) {
							String line1 = ChatColor.stripColor(scevent.getLine(0).trim());
							String line2 = ChatColor.stripColor(scevent.getLine(1).trim());
							if (line1.length() > 13) {
								line2 = ChatColor.DARK_BLUE + line1.substring(13, line1.length()) + line2;
								line1 = ChatColor.DARK_BLUE + line1.substring(0, 13);
							} else {
								line1 = ChatColor.DARK_BLUE + line1;
								line2 = ChatColor.DARK_BLUE + line2;
							}
							scevent.setLine(0, line1);
							scevent.setLine(1, line2);
							if (line3.equalsIgnoreCase("[sell:buy]")) {
								scevent.setLine(2, "\u00A7f[Sell:Buy]");
							} else if (line3.equalsIgnoreCase("[sell]")) {
								scevent.setLine(2, "\u00A7f[Sell]");
							} else if (line3.equalsIgnoreCase("[buy]")) {
								scevent.setLine(2, "\u00A7f[Buy]");
							}
							scevent.setLine(3, "\u00A7a" + amount);
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
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEvent(PlayerInteractEvent ievent) {
		try {
			if (!hc.gYH().gFC("config").getBoolean("config.use-transaction-signs")) {return;}
			Player p = ievent.getPlayer();
			if (p == null) {return;}
			HyperEconomy he = em.getHyperPlayer(p.getName()).getHyperEconomy();
			if (p.isSneaking() && p.hasPermission("hyperconomy.admin")) {return;}
			LanguageFile L = hc.getLanguageFile();
			boolean requireShop = hc.gYH().gFC("config").getBoolean("config.require-transaction-signs-to-be-in-shop");

			Block b = null;
			if (!ievent.hasBlock()) {
				try {
					b = ievent.getPlayer().getTargetBlock(null, 5);
				} catch (Exception e) {
					//silence bukkit IllegalStateException: start block missed in iterator
					return;
				}
			} else {
				b = ievent.getClickedBlock();
			}
			if (b == null) {return;}
			if (b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.WALL_SIGN)) {
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
					line12 = he.fixName(line12);
					if (he.objectTest(line12)) {
						if (!s.getLine(0).startsWith("\u00A7")) {
							s.setLine(0, "\u00A71" + s.getLine(0));
							s.setLine(1, "\u00A71" + s.getLine(1));
							s.setLine(2, "\u00A7f" + s.getLine(2));
							s.setLine(3, "\u00A7a" + s.getLine(3));
							s.update();
						}
						Action action = ievent.getAction();
						if (action == Action.RIGHT_CLICK_BLOCK) {
							if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[buy]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								if (p.hasPermission("hyperconomy.buysign")) {
									if ((em.inAnyShop(p) && requireShop) || !requireShop) {
										HyperPlayer hp = em.getHyperPlayer(p);
										if (hp == null) {
											ievent.setCancelled(true);
											return;
										}
										if (!requireShop || hp.hasBuyPermission(em.getShop(p))) {
											HyperObject ho = he.getHyperObject(line12);
											if (!hc.getHyperLock().isLocked(p)) {
												PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
												pt.setAmount(amount);
												pt.setHyperObject(ho);
												TransactionResponse response = hp.processTransaction(pt);
												response.sendMessages();
											} else {
												p.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
											}
										} else {
											p.sendMessage(L.get("NO_TRADE_PERMISSION"));
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
						} else if (action == Action.LEFT_CLICK_BLOCK) {
							if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								if (p.hasPermission("hyperconomy.sellsign")) {
									if ((em.inAnyShop(p) && requireShop) || !requireShop) {
										HyperPlayer hp = em.getHyperPlayer(p);
										if (hp == null) {
											ievent.setCancelled(true);
											return;
										}
										if (!requireShop || hp.hasSellPermission(em.getShop(p))) {
											if (p.getGameMode() == GameMode.CREATIVE && hc.gYH().gQFC("config").gB("block-selling-in-creative-mode")) {
												p.sendMessage(L.get("CANT_SELL_CREATIVE"));
												ievent.setCancelled(true);
												return;
											}
											HyperObject ho = he.getHyperObject(line12);
											if (!hc.getHyperLock().isLocked(p)) {
												PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
												pt.setAmount(amount);
												pt.setHyperObject(ho);
												TransactionResponse response = hp.processTransaction(pt);
												response.sendMessages();
											} else {
												p.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
											}
										} else {
											p.sendMessage(L.get("NO_TRADE_PERMISSION"));
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

		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
}
