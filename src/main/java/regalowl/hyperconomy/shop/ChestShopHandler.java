package regalowl.hyperconomy.shop;

import java.util.ArrayList;












import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.event.EventHandler;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.MineCraftConnector;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.minecraft.ChestShopClickEvent;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HyperSignChangeEvent;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperItemStack;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.TempItem;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.HBlock;
import regalowl.hyperconomy.util.HSign;
import regalowl.hyperconomy.util.LanguageFile;

public class ChestShopHandler {

	private HyperConomy hc;
	private CommonFunctions cf;
	private LanguageFile L;
	private DataManager em;


	public ChestShopHandler() {
		hc = HyperConomy.hc;
		em = hc.getDataManager();
		cf = hc.gCF();
		L = hc.getLanguageFile();
		if (hc.getConf().getBoolean("enable-feature.chest-shops")) hc.getHyperEventHandler().registerListener(this);
	}


	@EventHandler
	public void onBlockBreakEvent(HBlockBreakEvent bbevent) {
		if (isChestShop(bbevent.getBlock(), true)) {
			if (isChestShopSign(bbevent.getBlock()) && bbevent.getPlayer().hasPermission("hyperconomy.admin") && bbevent.getPlayer().isSneaking()) {
				return;
			}
			Sign s = getChestShopSign(bbevent.getBlock());
			String line34 = HyperConomy.mc.removeColor(s.getLine(2)).trim() + HyperConomy.mc.removeColor(s.getLine(3)).trim();
			if (bbevent.getPlayer().getName().equalsIgnoreCase(line34) && bbevent.getPlayer().isSneaking()) {
				return;
			}
			bbevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent eeevent) {
		for (Block b : eeevent.blockList()) {
			if (isChestShop(b, true)) {
				eeevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent bpeevent) {
		for (Block b : bpeevent.getBlocks()) {
			if (isChestShop(b, true)) {
				bpeevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent bprevent) {
		if (isChestShop(bprevent.getRetractLocation().getBlock(), true)) {
			bprevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent bpevent) {
		Block block = bpevent.getBlock();
		for (BlockFace bf : allfaces) {
			if (isChestShop(block.getRelative(bf), false)) {
				bpevent.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onSignChangeEvent(HyperSignChangeEvent event) {
		try {
			HSign sign = event.getSign();
			HyperPlayer hp = event.getHyperPlayer();
			String line2 = HyperConomy.mc.removeColor(sign.getLine(1)).trim();
			if (!line2.equalsIgnoreCase("[Trade]") && !line2.equalsIgnoreCase("[Buy]") && !line2.equalsIgnoreCase("[Sell]")) {
				return;
			}

			if (!hp.hasPermission("hyperconomy.chestshop")) {
				sign.setLine(1, "");
				return;
			}

			HBlock attachedblock = sign.getAttachedBlock();
			signblock.
			org.bukkit.material.Sign msign = (org.bukkit.material.Sign) signblock.getState().getData();
			BlockFace attachedface = msign.getAttachedFace();
			Block attachedblock = signblock.getRelative(attachedface);
			Material am = attachedblock.getType();
			BlockState chestblock = signblock.getRelative(BlockFace.DOWN).getState();
			if (!(chestblock instanceof Chest)) {
				scevent.setLine(1, "");
				return;
			}

			Block cblock = chestblock.getBlock();
			BlockState pchest1 = cblock.getRelative(BlockFace.EAST).getState();
			BlockState pchest2 = cblock.getRelative(BlockFace.WEST).getState();
			BlockState pchest3 = cblock.getRelative(BlockFace.NORTH).getState();
			BlockState pchest4 = cblock.getRelative(BlockFace.SOUTH).getState();
			if ((pchest1 instanceof Chest) || (pchest2 instanceof Chest) || (pchest3 instanceof Chest) || (pchest4 instanceof Chest)) {
				sign.setLine(0, "&4You can't");
				sign.setLine(1, "&4use a");
				sign.setLine(2, "&4double");
				sign.setLine(3, "&4chest.");
				return;
			}

			if (hc.getConf().getBoolean("shop.require-chest-shops-to-be-in-shop") && !em.getHyperShopManager().inAnyShop(hp)) {
				sign.setLine(0, "&4You must");
				sign.setLine(1, "&4place your");
				sign.setLine(2, "&4chest shop");
				sign.setLine(3, "&4in a shop.");
				return;
			}

			Chest c = (Chest) chestblock;
			int count = 0;
			int emptyslots = 0;
			while (count < 27) {
				if (c.getInventory().getItem(count) == null) {
					emptyslots++;
				}
				count++;
			}
			if (emptyslots != 27) {
				sign.setLine(0, "&4You must");
				sign.setLine(1, "&4use an");
				sign.setLine(2, "&4empty");
				sign.setLine(3, "&4chest.");
				return;
			}

			if (am == Material.ICE || am == Material.LEAVES || am == Material.SAND || am == Material.GRAVEL || am == Material.SIGN || am == Material.SIGN_POST || am == Material.TNT) {
				sign.setLine(0, "&4You can't");
				sign.setLine(1, "&4attach your");
				sign.setLine(2, "&4sign to that");
				sign.setLine(3, "&4block!");
				return;
			}

			String line1 = scevent.getLine(0);
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
			String pname = scevent.getPlayer().getName();
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

			if (scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.SIGN_POST) || scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) scevent.getBlock().getState();
				s.update();
			}

		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler
	public void onInventoryClickEvent(ChestShopClickEvent event) {
		try {
			HyperPlayer clicker = event.getClicker();
			ChestShop cs = event.getChestShop();
			ChestShopType type = cs.getType();
			HyperAccount owner = cs.getOwner();
			HSign sign = cs.getSign();
			boolean hasStaticPrice = cs.hasStaticPrice();
			double staticPrice = 0.0;
			if (hasStaticPrice) staticPrice = cs.getStaticPrice();
			SerializableInventory shopInventory = cs.getInventory();
			int slot = event.getClickedSlot();
			if (sign == null) return;
			if (hc.getHyperLock().isLocked(clicker)) {
				hc.getHyperLock().sendLockMessage(clicker);
				event.cancel();
				return;
			}
			if (clicker.getName().equals(owner.getName())) return; // if clicker is owner of chest return

			SerializableItemStack clickedItem = event.getClickedItem();
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
			HyperObject hyperObject = null;
			if (!clickedItem.hasEnchantments()) {
				hyperObject = chestOwnerEconomy.getHyperObject(clickedItem);
				if (hyperObject == null) {
					if (hasStaticPrice) {
						hyperObject = TempItem.generate(clickedItem);
					} else {
						event.cancel();
						return;
					}
				}
			}
			if (event.isShiftClick()) {
				if (clickedItem.hasEnchantments()) {
					event.cancel();
					return;
				}
				int camount = clickedItem.getAmount();
				if (slot < 27) {
					if (type == ChestShopType.BUY) {
						PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
						pt.setHyperObject(hyperObject);
						pt.setTradePartner(owner);
						pt.setAmount(camount);
						pt.setGiveInventory(shopInventory);
						if (hasStaticPrice) {
							pt.setMoney(cf.twoDecimals((camount * staticPrice)));
							pt.setSetPrice(true);
						}
						TransactionResponse response = clicker.processTransaction(pt);
						response.sendMessages();
					} else {
						clicker.sendMessage(L.get("CANNOT_PURCHASE_ENCHANTMENTS_FROM_CHEST"));
					}
				} else if (slot >= 27) {
					if (type == ChestShopType.SELL) {
						if (clicker.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
							clicker.sendMessage(L.get("CANT_SELL_CREATIVE"));
							event.cancel();
							return;
						}
						int itemamount = hyperObject.count(shopInventory);
						if (itemamount > 0) {
							int space = hyperObject.getAvailableSpace(shopInventory);
							if (space >= camount) {
								double bal = owner.getBalance();
								double cost = hyperObject.getSellPrice(camount);
								if (hasStaticPrice) {
									cost = staticPrice * camount;
								}
								if (bal >= cost) {
									PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
									pt.setHyperObject(hyperObject);
									pt.setTradePartner(owner);
									pt.setAmount(camount);
									pt.setReceiveInventory(shopInventory);
									if (hasStaticPrice) {
										pt.setMoney(cf.twoDecimals(cost));
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
				if (!clickedItem.hasEnchantments()) {
					if (slot < 27 && hyperObject != null) {
						String name = hyperObject.getDisplayName();
						if (type == ChestShopType.BUY) {
							double price = hyperObject.getSellPrice(1);
							if (hasStaticPrice) {
								price = staticPrice;
							}
							clicker.sendMessage(L.get("LINE_BREAK"));
							clicker.sendMessage(L.f(L.get("CHEST_SHOP_BUY_VALUE"), 1, price, name, owner.getName()));
							clicker.sendMessage(L.get("LINE_BREAK"));
						} else {
							clicker.sendMessage(L.get("CANNOT_PURCHASE_ITEMS_FROM_CHEST"));
						}
					} else if (slot >= 27 && hyperObject != null) {
						String name = hyperObject.getDisplayName();
						if (type == ChestShopType.SELL) {
							int itemamount = hyperObject.count(shopInventory);
							if (itemamount > 0) {
								double price = hyperObject.getSellPrice(1, clicker);
								if (hasStaticPrice) {
									price = staticPrice;
								}
								clicker.sendMessage(L.get("LINE_BREAK"));
								clicker.sendMessage(L.f(L.get("CHEST_SHOP_SELL_VALUE"), 1, price, name, owner.getName()));
								clicker.sendMessage(L.get("LINE_BREAK"));
							} else {
								clicker.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
							}
						} else {
							clicker.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
						}
					}
				} else {
					if (slot < 27) {
						if (type == ChestShopType.BUY) {
							double price = 0;
							for (SerializableEnchantment enchantment : clickedItem.getItemMeta().getEnchantments()) {
								String fnam = enchantment.getEnchantmentName() + enchantment.getLvl();
								HyperObject ho = chestOwnerEconomy.getHyperObject(fnam);
								price += ho.getSellPrice(EnchantmentClass.fromString(clicker.getItemInHand().getMaterial()), clicker);
								if (hasStaticPrice) {
									price = staticPrice;
								}
							}
							price = cf.twoDecimals(price);
							if (clicker.getItemInHand().canEnchantItem()) {
								clicker.sendMessage(L.get("LINE_BREAK"));
								clicker.sendMessage(L.f(L.get("CHEST_SHOP_ENCHANTMENT_VALUE"), price, owner.getName()));
								clicker.sendMessage(L.get("LINE_BREAK"));
							} else {
								clicker.sendMessage(L.get("ITEM_CANNOT_ACCEPT_ENCHANTMENTS"));
							}
						} else {
							clicker.sendMessage(L.get("CANNOT_PURCHASE_ENCHANTMENTS_FROM_CHEST"));
						}
					} else {
						clicker.sendMessage(L.get("CANNOT_SELL_ENCHANTMENTS_HERE"));
					}
				}
				event.cancel();
				return;
			} else if (event.isRightClick()) {
				if (!clickedItem.hasEnchantments()) {
					if (slot < 27 && hyperObject != null) {
						if (type == ChestShopType.BUY) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
							pt.setHyperObject(hyperObject);
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

					} else if (slot >= 27 && hyperObject != null) {
						if (type == ChestShopType.SELL) {
							if (clicker.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
								clicker.sendMessage(L.get("CANT_SELL_CREATIVE"));
								event.cancel();
								return;
							}
							int itemamount = hyperObject.count(shopInventory);

							if (itemamount > 0) {
								int space = hyperObject.getAvailableSpace(shopInventory);
								if (space >= 1) {
									double bal = owner.getBalance();
									double cost = hyperObject.getSellPrice(1);
									if (hasStaticPrice) {
										cost = staticPrice;
									}
									if (bal >= cost) {
										PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
										pt.setHyperObject(hyperObject);
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
				} else {
					if (slot < 27) {
						if (type == ChestShopType.BUY) {
							for (SerializableEnchantment enchantment : clickedItem.getItemMeta().getEnchantments()) {
								String fnam = enchantment.getEnchantmentName() + enchantment.getLvl();
								HyperObject ho = chestOwnerEconomy.getHyperObject(fnam);
								PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_ITEM);
								pt.setHyperObject(ho);
								pt.setTradePartner(owner);
								pt.setGiveItem(clickedItem);
								if (hasStaticPrice) {
									pt.setMoney(staticPrice);
									pt.setSetPrice(true);
								}
								TransactionResponse response = clicker.processTransaction(pt);
								response.sendMessages();
							}
						} else {
							clicker.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
						}
					} else if (slot >= 27) {
						clicker.sendMessage(L.get("CANNOT_SELL_ENCHANTMENTS_HERE"));
					}
				}
				event.cancel();
				return;
			} else {
				event.cancel();
				return;
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

}
