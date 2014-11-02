package regalowl.hyperconomy.display;



import regalowl.databukkit.event.EventHandler;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.minecraft.HPlayerInteractEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerItemHeldEvent;
import regalowl.hyperconomy.event.minecraft.HSignChangeEvent;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;

public class TransactionSign {
	private HC hc;
	private DataManager em;
	public TransactionSign() {
		hc = HC.hc;
		em = HC.hc.getDataManager();
		if (hc.getConf().getBoolean("enable-feature.transaction-signs")) {
			hc.getHyperEventHandler().registerListener(this);
		}
	}


	@EventHandler
	public void onPlayerItemHeldEvent(HPlayerItemHeldEvent event) {
		try {
			if (hc.getConf().getBoolean("enable-feature.scrolling-transaction-signs")) {
				HyperPlayer hp = event.getHyperPlayer();
				if (hc.getHyperLock().loadLock()) return;
				HyperEconomy he = hp.getHyperEconomy();
				HLocation target = hp.getTargetLocation();
				HSign sign = HC.mc.getSign(target);
				if (sign != null) {
					String line3 = HC.mc.removeColor(sign.getLine(2)).trim();
					if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
						String line12 = HC.mc.removeColor(sign.getLine(0)).trim() + HC.mc.removeColor(sign.getLine(1)).trim();
						line12 = he.fixName(line12);
						if (he.objectTest(line12)) {
							String line4 = HC.mc.removeColor(sign.getLine(3)).trim();
							int amount = 0;
							try {
								amount = Integer.parseInt(line4);
							} catch (Exception e) {
								amount = 0;
							}
							int change = 1;
							if (hp.isSneaking()) {
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
							if (amount < 1) {
								amount = 1;
							} else if (amount > 512) {
								amount = 512;
							}
							sign.setLine(3, "\u00A7a" + amount);
							sign.update();
						}
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler
	public void onSignChangeEvent(HSignChangeEvent scevent) {
		try {
			if (hc.getConf().getBoolean("enable-feature.transaction-signs")) {
				HSign sign = scevent.getSign();
				String line3 = HC.mc.removeColor(sign.getLine(2)).trim();
				if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
					String line4 = HC.mc.removeColor(sign.getLine(3)).trim();
					int amount = 0;
					try {
						amount = Integer.parseInt(line4);
					} catch (Exception e) {
						amount = 0;
					}
					String line12 = HC.mc.removeColor(sign.getLine(0)).trim() + HC.mc.removeColor(sign.getLine(1)).trim();
					line12 = em.getEconomy("default").fixName(line12);
					if (em.getEconomy("default").objectTest(line12)) {
						if (scevent.getHyperPlayer().hasPermission("hyperconomy.createsign")) {
							String line1 = HC.mc.removeColor(sign.getLine(0).trim());
							String line2 = HC.mc.removeColor(sign.getLine(1).trim());
							if (line1.length() > 13) {
								line2 = HC.mc.applyColor("&1" + line1.substring(13, line1.length()) + line2);
								line1 = HC.mc.applyColor("&1" + line1.substring(0, 13));
							} else {
								line1 = HC.mc.applyColor("&1" + line1);
								line2 = HC.mc.applyColor("&1" + line2);
							}
							sign.setLine(0, line1);
							sign.setLine(1, line2);
							if (line3.equalsIgnoreCase("[sell:buy]")) {
								sign.setLine(2, "\u00A7f[Sell:Buy]");
							} else if (line3.equalsIgnoreCase("[sell]")) {
								sign.setLine(2, "\u00A7f[Sell]");
							} else if (line3.equalsIgnoreCase("[buy]")) {
								sign.setLine(2, "\u00A7f[Buy]");
							}
							sign.setLine(3, "\u00A7a" + amount);
						} else if (!scevent.getHyperPlayer().hasPermission("hyperconomy.createsign")) {
							sign.setLine(0, "");
							sign.setLine(1, "");
							sign.setLine(2, "");
							sign.setLine(3, "");
						}
						sign.update();
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(HPlayerInteractEvent ievent) {
		if (ievent == null) {return;}
		try {
			if (!hc.getConf().getBoolean("enable-feature.transaction-signs")) {return;}
			HyperPlayer hp = ievent.getHyperPlayer();
			if (hp == null) {return;}
			HyperEconomy he = null;
			if (!hc.getHyperLock().loadLock()) {
				he = hp.getHyperEconomy();
			}
			if (hp.isSneaking() && hp.hasPermission("hyperconomy.admin")) {return;}
			LanguageFile L = hc.getLanguageFile();
			boolean requireShop = hc.getConf().getBoolean("shop.require-transaction-signs-to-be-in-shop");

			HBlock b = ievent.getBlock();
			if (b == null) {return;}
			if (b.isTransactionSign()) {
				HSign s = HC.mc.getSign(b.getLocation());
				String line3 = HC.mc.removeColor(s.getLine(2)).trim();
				if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
					String line4 = HC.mc.removeColor(s.getLine(3)).trim();
					int amount = 0;
					try {
						amount = Integer.parseInt(line4);
					} catch (Exception e) {
						return;
					}
					String line12 = HC.mc.removeColor(s.getLine(0)).trim() + HC.mc.removeColor(s.getLine(1)).trim();
					line12 = he.fixName(line12);
					if (he.objectTest(line12)) {
						if (!s.getLine(0).startsWith("\u00A7")) {
							s.setLine(0, "\u00A71" + s.getLine(0));
							s.setLine(1, "\u00A71" + s.getLine(1));
							s.setLine(2, "\u00A7f" + s.getLine(2));
							s.setLine(3, "\u00A7a" + s.getLine(3));
							s.update();
						}
						if (!ievent.isLeftClick()) {
							if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[buy]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								if (hp.hasPermission("hyperconomy.buysign")) {
									if ((em.getHyperShopManager().inAnyShop(hp) && requireShop) || !requireShop) {
										if (!requireShop || hp.hasBuyPermission(em.getHyperShopManager().getShop(hp))) {
											TradeObject ho = he.getHyperObject(line12);
											if (!hc.getHyperLock().isLocked(hp)) {
												PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
												pt.setAmount(amount);
												pt.setHyperObject(ho);
												TransactionResponse response = hp.processTransaction(pt);
												response.sendMessages();
											} else {
												hp.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
											}
										} else {
											hp.sendMessage(L.get("NO_TRADE_PERMISSION"));
										}
									} else {
										hp.sendMessage(L.get("TRANSACTION_SIGN_MUST_BE_IN_SHOP"));
									}
								} else {
									hp.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
								}
								ievent.cancel();;
								s.setLine(0, l1);
								s.setLine(1, l2);
								s.setLine(2, l3);
								s.setLine(3, l4);
								s.update();
							}
						} else if (ievent.isLeftClick()) {
							if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								if (hp.hasPermission("hyperconomy.sellsign")) {
									if ((em.getHyperShopManager().inAnyShop(hp) && requireShop) || !requireShop) {
										if (!requireShop || hp.hasSellPermission(em.getHyperShopManager().getShop(hp))) {
											if (hp.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
												hp.sendMessage(L.get("CANT_SELL_CREATIVE"));
												ievent.cancel();
												return;
											}
											TradeObject ho = he.getHyperObject(line12);
											if (!hc.getHyperLock().isLocked(hp)) {
												PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
												pt.setAmount(amount);
												pt.setHyperObject(ho);
												TransactionResponse response = hp.processTransaction(pt);
												response.sendMessages();
											} else {
												hp.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
											}
										} else {
											hp.sendMessage(L.get("NO_TRADE_PERMISSION"));
										}
									} else {
										hp.sendMessage(L.get("TRANSACTION_SIGN_MUST_BE_IN_SHOP"));
									}
								} else {
									hp.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
								}
								ievent.cancel();
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
								ievent.cancel();
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
