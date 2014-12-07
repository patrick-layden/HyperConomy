package regalowl.hyperconomy.shop;



import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.event.EventHandler;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.minecraft.ChestShopClickEvent;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonExtendEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonRetractEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPlaceEvent;
import regalowl.hyperconomy.event.minecraft.HEntityExplodeEvent;
import regalowl.hyperconomy.event.minecraft.HSignChangeEvent;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TempTradeItem;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;

public class ChestShopHandler {

	private HyperConomy hc;
	private LanguageFile L;
	private DataManager em;


	public ChestShopHandler(HyperConomy hc) {
		this.hc = hc;
		em = hc.getDataManager();
		L = hc.getLanguageFile();
		if (hc.getConf().getBoolean("enable-feature.chest-shops")) hc.getHyperEventHandler().registerListener(this);
	}


	@EventHandler
	public void onBlockBreakEvent(HBlockBreakEvent bbevent) {
		if (hc.getMC().isChestShopChest(bbevent.getBlock().getLocation())) {
			bbevent.cancel();
		} else if (hc.getMC().isChestShopSign(bbevent.getBlock().getLocation())) {
			if (bbevent.getPlayer().hasPermission("hyperconomy.admin") && bbevent.getPlayer().isSneaking()) return;
			bbevent.cancel();
		} else if (hc.getMC().isChestShopSignBlock(bbevent.getBlock().getLocation())) {
			bbevent.cancel();
		}
	}
	@EventHandler
	public void onEntityExplodeEvent(HEntityExplodeEvent eeevent) {
		for (HBlock b : eeevent.getBrokenBlocks()) {
			if (new ChestShop(hc, b.getLocation()).isValid()) {
				eeevent.cancel();
			}
		}
	}
	@EventHandler
	public void onBlockPistonExtendEvent(HBlockPistonExtendEvent bpeevent) {
		for (HBlock b : bpeevent.getBlocks()) {
			if (new ChestShop(hc, b.getLocation()).isValid()) {
				bpeevent.cancel();
			}
		}
	}
	@EventHandler
	public void onBlockPistonRetractEvent(HBlockPistonRetractEvent bprevent) {
		if (new ChestShop(hc, bprevent.getRetractedBlock().getLocation()).isValid()) {
			bprevent.cancel();
		}
	}
	@EventHandler
	public void onBlockPlaceEvent(HBlockPlaceEvent bpevent) {
		HBlock block = bpevent.getBlock();
		for (HBlock b : block.getSurroundingBlocks()) {
			if (hc.getMC().isChestShopChest(b.getLocation())) {
				bpevent.cancel();
			}
		}
	}


	@EventHandler
	public void onSignChangeEvent(HSignChangeEvent event) {
		try {
			HSign sign = event.getSign();
			HyperPlayer hp = event.getHyperPlayer();
			String line2 = hc.getMC().removeColor(sign.getLine(1)).trim();
			if (!line2.equalsIgnoreCase("[Trade]") && !line2.equalsIgnoreCase("[Buy]") && !line2.equalsIgnoreCase("[Sell]")) {
				return;
			}
			HLocation cLoc = new HLocation(sign.getLocation());
			cLoc.setY(cLoc.getY() - 1);
			if (!hc.getMC().isChest(cLoc)) {
				sign.setLine(0, "&4Place a");
				sign.setLine(1, "&4chest");
				sign.setLine(2, "&4below");
				sign.setLine(3, "&4first.");
				sign.update();
				return;
			}
			if (!hp.hasPermission("hyperconomy.chestshop")) {
				sign.setLine(0, "&4No");
				sign.setLine(1, "&4Permission");
				sign.update();
				return;
			}
			ChestShop cShop = hc.getMC().getChestShop(cLoc);
			if (cShop.isDoubleChest()) {
				sign.setLine(0, "&4You can't");
				sign.setLine(1, "&4use a");
				sign.setLine(2, "&4double");
				sign.setLine(3, "&4chest.");
				sign.update();
				return;
			}
			if (hc.getConf().getBoolean("shop.require-chest-shops-to-be-in-shop") && !em.getHyperShopManager().inAnyShop(hp)) {
				sign.setLine(0, "&4You must");
				sign.setLine(1, "&4place your");
				sign.setLine(2, "&4chest shop");
				sign.setLine(3, "&4in a shop.");
				sign.update();
				return;
			}
			int count = 0;
			HInventory inv = cShop.getInventory();
			boolean empty = true;
			while (count < inv.getSize()) {
				if (!cShop.getInventory().getItem(count).isBlank()) empty = false;
				count++;
			}
			if (!empty) {
				sign.setLine(0, "&4You must");
				sign.setLine(1, "&4use an");
				sign.setLine(2, "&4empty");
				sign.setLine(3, "&4chest.");
				sign.update();
				return;
			}

			if (!cShop.isSignAttachedToValidBlock()) {
				sign.setLine(0, "&4You can't");
				sign.setLine(1, "&4attach your");
				sign.setLine(2, "&4sign to that");
				sign.setLine(3, "&4block!");
				sign.update();
				return;
			}

			String line1 = sign.getLine(0);
			if (line1.startsWith(L.gC(false))) {
				try {
					String price = line1.substring(1, line1.length());
					Double.parseDouble(price);
					sign.setLine(0, "&a" + L.fCS(price));
				} catch (Exception e) {
					sign.setLine(0, "");
				}
			} else {
				try {
					String price = line1.substring(0, line1.length());
					Double.parseDouble(price);
					sign.setLine(0, "&a" + L.fCS(price));
				} catch (Exception e) {
					try {
						String price = line1.substring(0, line1.length() - 1);
						Double.parseDouble(price);
						sign.setLine(0, "&a" + L.fCS(price));
					} catch (Exception e2) {
						sign.setLine(0, "");
					}
				}
			}

			String fline = "";
			if (line2.equalsIgnoreCase("[Trade]")) {
				fline = "[Trade]";
			} else if (line2.equalsIgnoreCase("[Buy]")) {
				fline = "[Buy]";
			} else if (line2.equalsIgnoreCase("[Sell]")) {
				fline = "[Sell]";
			}
			String pname = event.getHyperPlayer().getName();
			String line3 = "";
			String line4 = "";
			if (pname.length() > 12) {
				line3 = pname.substring(0, 12);
				line4 = pname.substring(12, pname.length());
			} else {
				line3 = pname;
			}

			sign.setLine(1, "&3" + fline);
			sign.setLine(2, "&f" + line3);
			sign.setLine(3, "&f" + line4);
			
			sign.update();

		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}

	@EventHandler
	public void onInventoryClickEvent(ChestShopClickEvent event) {
		try {
			HyperPlayer clicker = event.getClicker();
			ChestShop cs = event.getChestShop();
			HyperAccount owner = cs.getOwner();
			HSign sign = cs.getSign();
			boolean hasStaticPrice = cs.hasStaticPrice();
			double staticPrice = 0.0;
			if (hasStaticPrice) staticPrice = cs.getStaticPrice();
			HInventory shopInventory = cs.getInventory();
			int slot = event.getClickedSlot();
			if (sign == null) return;
			if (hc.getHyperLock().isLocked(clicker)) {
				hc.getHyperLock().sendLockMessage(clicker);
				event.cancel();
				return;
			}
			if (clicker.getName().equals(owner.getName())) return; // if clicker is owner of chest return

			HItemStack clickedItem = event.getClickedItem();
			if (clickedItem == null) {
				event.cancel();
				return;
			}
			if (clickedItem.isDamaged()) {
				event.cancel();
				clicker.sendMessage(L.get("CHESTSHOP_CANT_TRADE_DAMAGED"));
				return;
			}
			HyperEconomy chestOwnerEconomy = em.getDefaultEconomy();
			if (owner instanceof HyperPlayer) {
				HyperPlayer hPlayer = (HyperPlayer)owner;
				chestOwnerEconomy = hPlayer.getHyperEconomy();
			}
			TradeObject tradeObject = chestOwnerEconomy.getTradeObject(clickedItem);
			if (tradeObject == null) {
				if (hasStaticPrice) {
					tradeObject = TempTradeItem.generate(hc, clickedItem);
				} else {
					event.cancel();
					return;
				}
			}
			
			if (event.isShiftClick()) {
				int stackQuantity = clickedItem.getAmount();
				if (slot < 27) {
					if (cs.isBuyChest()) {
						PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
						pt.setHyperObject(tradeObject);
						pt.setTradePartner(owner);
						pt.setAmount(stackQuantity);
						pt.setGiveInventory(shopInventory);
						if (hasStaticPrice) {
							pt.setMoney(CommonFunctions.twoDecimals((stackQuantity * staticPrice)));
							pt.setSetPrice(true);
						}
						TransactionResponse response = clicker.processTransaction(pt);
						response.sendMessages();
					} else {
						clicker.sendMessage(L.get("CANNOT_PURCHASE_ITEMS_FROM_CHEST"));
					}
				} else if (slot >= 27) {
					if (cs.isSellChest()) {
						if (clicker.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
							clicker.sendMessage(L.get("CANT_SELL_CREATIVE"));
							event.cancel();
							return;
						}
						int itemamount = shopInventory.count(tradeObject.getItem());
						if (itemamount > 0) {
							int space = shopInventory.getAvailableSpace(tradeObject.getItem());
							if (space >= stackQuantity) {
								double bal = owner.getBalance();
								double cost = tradeObject.getSellPrice(stackQuantity);
								if (hasStaticPrice) {
									cost = staticPrice * stackQuantity;
								}
								if (bal >= cost) {
									PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
									pt.setHyperObject(tradeObject);
									pt.setTradePartner(owner);
									pt.setAmount(stackQuantity);
									pt.setReceiveInventory(shopInventory);
									if (hasStaticPrice) {
										pt.setMoney(CommonFunctions.twoDecimals(cost));
										pt.setSetPrice(true);
									}
									TransactionResponse response = clicker.processTransaction(pt);
									response.sendMessages();
								} else {
									L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), owner.getName());
								}

							} else {
								clicker.sendMessage(L.get("CHEST_SHOP_NOT_ENOUGH_SPACE"));
							}
						} else {
							clicker.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
						}

					} else {
						clicker.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
					}

				}
				event.cancel();
				return;
			} else if (event.isLeftClick()) {
				if (slot < 27 && tradeObject != null) {
					String name = tradeObject.getDisplayName();
					if (cs.isBuyChest()) {
						double price = tradeObject.getSellPrice(1);
						if (hasStaticPrice) {
							price = staticPrice;
						}
						clicker.sendMessage(L.get("LINE_BREAK"));
						clicker.sendMessage(L.f(L.get("CHEST_SHOP_BUY_VALUE"), 1, CommonFunctions.twoDecimals(price), name, owner.getName()));
						clicker.sendMessage(L.get("LINE_BREAK"));
					} else {
						clicker.sendMessage(L.get("CANNOT_PURCHASE_ITEMS_FROM_CHEST"));
					}
				} else if (slot >= 27 && tradeObject != null) {
					String name = tradeObject.getDisplayName();
					if (cs.isSellChest()) {
						int itemamount = shopInventory.count(tradeObject.getItem());
						if (itemamount > 0) {
							double price = tradeObject.getSellPrice(1, clicker);
							if (hasStaticPrice) {
								price = staticPrice;
							}
							clicker.sendMessage(L.get("LINE_BREAK"));
							clicker.sendMessage(L.f(L.get("CHEST_SHOP_SELL_VALUE"), 1, CommonFunctions.twoDecimals(price), name, owner.getName()));
							clicker.sendMessage(L.get("LINE_BREAK"));
						} else {
							clicker.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
						}
					} else {
						clicker.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
					}
				}
				event.cancel();
				return;
			} else if (event.isRightClick()) {
				if (slot < 27 && tradeObject != null) {
					if (cs.isBuyChest()) {
						PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
						pt.setHyperObject(tradeObject);
						pt.setTradePartner(owner);
						pt.setAmount(1);
						pt.setGiveInventory(shopInventory);
						if (hasStaticPrice) {
							pt.setMoney(staticPrice);
							pt.setSetPrice(true);
						}
						TransactionResponse response = clicker.processTransaction(pt);
						response.sendMessages();

					} else {
						clicker.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
					}

				} else if (slot >= 27 && tradeObject != null) {
					if (cs.isSellChest()) {
						if (clicker.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
							clicker.sendMessage(L.get("CANT_SELL_CREATIVE"));
							event.cancel();
							return;
						}
						int itemamount = shopInventory.count(tradeObject.getItem());

						if (itemamount > 0) {
							int space = shopInventory.getAvailableSpace(tradeObject.getItem());
							if (space >= 1) {
								double bal = owner.getBalance();
								double cost = tradeObject.getSellPrice(1);
								if (hasStaticPrice) {
									cost = staticPrice;
								}
								if (bal >= cost) {
									PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
									pt.setHyperObject(tradeObject);
									pt.setTradePartner(owner);
									pt.setAmount(1);
									pt.setReceiveInventory(shopInventory);
									if (hasStaticPrice) {
										pt.setMoney(cost);
										pt.setSetPrice(true);
									}
									TransactionResponse response = clicker.processTransaction(pt);
									response.sendMessages();
								} else {
									clicker.sendMessage(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), owner.getName()));
								}

							} else {
								clicker.sendMessage(L.get("CHEST_SHOP_NOT_ENOUGH_SPACE"));
							}
						} else {
							clicker.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
						}
					} else {
						clicker.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
					}
				}
				event.cancel();
				return;
			} else {
				event.cancel();
				return;
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}

}
