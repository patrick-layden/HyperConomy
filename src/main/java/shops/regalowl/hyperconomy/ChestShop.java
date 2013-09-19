package regalowl.hyperconomy;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class ChestShop implements Listener {

	private HyperConomy hc;
	private Calculation calc;
	private ShopFactory s;
	private LanguageFile L;
	private DataHandler dh;
	private InventoryManipulation im;

	private ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
	private ArrayList<BlockFace> allfaces = new ArrayList<BlockFace>();

	ChestShop() {

		hc = HyperConomy.hc;
		im = hc.getInventoryManipulation();
		dh = hc.getDataFunctions();
		calc = hc.getCalculation();
		s = hc.getShopFactory();
		L = hc.getLanguageFile();

		faces.add(BlockFace.EAST);
		faces.add(BlockFace.WEST);
		faces.add(BlockFace.NORTH);
		faces.add(BlockFace.SOUTH);

		allfaces.add(BlockFace.EAST);
		allfaces.add(BlockFace.WEST);
		allfaces.add(BlockFace.NORTH);
		allfaces.add(BlockFace.SOUTH);
		allfaces.add(BlockFace.DOWN);
		allfaces.add(BlockFace.UP);

		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
		}

	}

	public boolean isChestShopSign(Block b) {
		try {
			if (b == null) {return false;}
			if (b.getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) b.getState();
				String line2 = s.getLine(1).trim();
				if (line2.equalsIgnoreCase(ChatColor.AQUA + "[Trade]") || line2.equalsIgnoreCase(ChatColor.AQUA + "[Buy]") || line2.equalsIgnoreCase(ChatColor.AQUA + "[Sell]")) {
					BlockState chestblock = Bukkit.getWorld(s.getBlock().getWorld().getName()).getBlockAt(s.getX(), s.getY() - 1, s.getZ()).getState();
					if (chestblock instanceof Chest) {
						s.update();
						return true;
					}
				}
			} else {
				for (BlockFace cface : faces) {
					Block relative = b.getRelative(cface);
					if (relative.getType().equals(Material.WALL_SIGN)) {
						Sign s = (Sign) relative.getState();
						String line2 = s.getLine(1).trim();
						if (line2.equalsIgnoreCase(ChatColor.AQUA + "[Trade]") || line2.equalsIgnoreCase(ChatColor.AQUA + "[Buy]") || line2.equalsIgnoreCase(ChatColor.AQUA + "[Sell]")) {
							org.bukkit.material.Sign sign = (org.bukkit.material.Sign) relative.getState().getData();
							BlockFace attachedface = sign.getFacing();
							if (attachedface == cface) {
								return true;
							}
						}
					}
				}
			}
			return false;
		} catch (Exception e) {
			new HyperError(e);
			return false;
		}
	}
	
	public boolean isChestShop(Block b) {
		try {
			if (b == null) {return false;}
			if (b.getState() instanceof Chest) {
				Chest chest = (Chest) b.getState();
				String world = chest.getBlock().getWorld().getName();
				BlockState signblock = Bukkit.getWorld(world).getBlockAt(chest.getX(), chest.getY() + 1, chest.getZ()).getState();
				if (signblock instanceof Sign) {
					Sign s = (Sign) signblock;
					String line2 = ChatColor.stripColor(s.getLine(1)).trim();
					if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
						return true;
					}
				}
			} else if (isChestShopSign(b)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			new HyperError(e);
			return false;
		}
	}

	public boolean isChestShop(InventoryHolder ih) {
		try {
			if (ih instanceof Chest) {
				Chest chest = (Chest) ih;
				int x = chest.getX();
				int y = chest.getY() + 1;
				int z = chest.getZ();
				String world = chest.getBlock().getWorld().getName();
				BlockState signblock = Bukkit.getWorld(world).getBlockAt(x, y, z).getState();
				if (signblock instanceof Sign) {
					Sign s = (Sign) signblock;
					String line2 = ChatColor.stripColor(s.getLine(1)).trim();
					if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			new HyperError(e);
			return false;
		}
	}

	public Sign getChestShopSign(InventoryHolder ih) {
		try {
			if (ih instanceof Chest) {
				Chest chest = (Chest) ih;
				int x = chest.getX();
				int y = chest.getY() + 1;
				int z = chest.getZ();
				String world = chest.getBlock().getWorld().getName();
				BlockState signblock = Bukkit.getWorld(world).getBlockAt(x, y, z).getState();
				if (signblock instanceof Sign) {
					Sign s = (Sign) signblock;
					String line2 = ChatColor.stripColor(s.getLine(1)).trim();
					if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
						return s;
					}
				}
			}
			return null;
		} catch (Exception e) {
			new HyperError(e);
			return null;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent bbevent) {
		if (isChestShop(bbevent.getBlock())) {
			if (isChestShopSign(bbevent.getBlock()) && bbevent.getPlayer().hasPermission("hyperconomy.admin") && bbevent.getPlayer().isSneaking()) {
				return;
			}
			bbevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent eeevent) {
		for (Block b : eeevent.blockList()) {
			if (isChestShop(b)) {
				eeevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent bpeevent) {
		for (Block b : bpeevent.getBlocks()) {
			if (isChestShop(b)) {
				bpeevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent bprevent) {
		if (isChestShop(bprevent.getRetractLocation().getBlock())) {
			bprevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent bpevent) {
		Block block = bpevent.getBlock();
		if (isChestShop(block)) {
			bpevent.setCancelled(true);
		}
		for (BlockFace bf : allfaces) {
			if (isChestShop(block.getRelative(bf))) {
				bpevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent scevent) {
		try {
			String line2 = ChatColor.stripColor(scevent.getLine(1)).trim();
			if (!line2.equalsIgnoreCase("[Trade]") && !line2.equalsIgnoreCase("[Buy]") && !line2.equalsIgnoreCase("[Sell]")) {
				return;
			}

			if (!scevent.getPlayer().hasPermission("hyperconomy.chestshop")) {
				scevent.setLine(1, "");
				return;
			}

			Block signblock = scevent.getBlock();
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
				scevent.setLine(0, ChatColor.DARK_RED + "You can't");
				scevent.setLine(1, ChatColor.DARK_RED + "use a");
				scevent.setLine(2, ChatColor.DARK_RED + "double");
				scevent.setLine(3, ChatColor.DARK_RED + "chest.");
				return;
			}

			if (hc.getYaml().getConfig().getBoolean("config.require-chest-shops-to-be-in-shop") && !s.inAnyShop(scevent.getPlayer())) {
				scevent.setLine(0, ChatColor.DARK_RED + "You must");
				scevent.setLine(1, ChatColor.DARK_RED + "place your");
				scevent.setLine(2, ChatColor.DARK_RED + "chest shop");
				scevent.setLine(3, ChatColor.DARK_RED + "in a shop.");
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
				scevent.setLine(0, ChatColor.DARK_RED + "You must");
				scevent.setLine(1, ChatColor.DARK_RED + "use an");
				scevent.setLine(2, ChatColor.DARK_RED + "empty");
				scevent.setLine(3, ChatColor.DARK_RED + "chest.");
				return;
			}

			if (am == Material.ICE || am == Material.LEAVES || am == Material.SAND || am == Material.GRAVEL || am == Material.SIGN || am == Material.SIGN_POST || am == Material.TNT) {
				scevent.setLine(0, ChatColor.DARK_RED + "You can't");
				scevent.setLine(1, ChatColor.DARK_RED + "attach your");
				scevent.setLine(2, ChatColor.DARK_RED + "sign to that");
				scevent.setLine(3, ChatColor.DARK_RED + "block!");
				return;
			}

			String line1 = scevent.getLine(0);
			if (line1.startsWith(L.gC(false))) {
				try {
					String price = line1.substring(1, line1.length());
					Double.parseDouble(price);
					scevent.setLine(0, ChatColor.GREEN + L.fCS(price));
				} catch (Exception e) {
					scevent.setLine(0, "");
				}
			} else {
				try {
					String price = line1.substring(0, line1.length());
					Double.parseDouble(price);
					scevent.setLine(0, ChatColor.GREEN + L.fCS(price));
				} catch (Exception e) {
					try {
						String price = line1.substring(0, line1.length() - 1);
						Double.parseDouble(price);
						scevent.setLine(0, ChatColor.GREEN + L.fCS(price));
					} catch (Exception e2) {
						scevent.setLine(0, "");
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

			scevent.setLine(1, ChatColor.AQUA + fline);
			scevent.setLine(2, ChatColor.WHITE + line3);
			scevent.setLine(3, ChatColor.WHITE + line4);

			if (scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.SIGN_POST) || scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) scevent.getBlock().getState();
				s.update();
			}

		} catch (Exception e) {
			new HyperError(e);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClickEvent(InventoryClickEvent icevent) {
		try {
			if (hc.isLocked() || hc.loadLock()) {
				if (isChestShop(icevent.getInventory().getHolder())) {
					Bukkit.getPlayer(icevent.getWhoClicked().getName()).sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
					icevent.setCancelled(true);
				}
				return;
			}

			Sign s = getChestShopSign(icevent.getInventory().getHolder());
			if (s == null) {
				return;
			}
			String line2 = ChatColor.stripColor(s.getLine(1)).trim();
			int slot = icevent.getRawSlot();
			boolean buy = false;
			boolean sell = false;
			if (line2.equalsIgnoreCase("[Trade]")) {
				buy = true;
				sell = true;
			} else if (line2.equalsIgnoreCase("[Buy]")) {
				buy = true;
			} else if (line2.equalsIgnoreCase("[Sell]")) {
				sell = true;
			}

			String line34 = ChatColor.stripColor(s.getLine(2)).trim() + ChatColor.stripColor(s.getLine(3)).trim();
			String clicker = icevent.getWhoClicked().getName();
			// if clicker is owner of chest return
			if (clicker.equalsIgnoreCase(line34)) {
				return;
			}

			if (icevent.getCurrentItem() == null) {
				icevent.setCancelled(true);
				return;
			}

			boolean setprice = false;
			double staticprice = 0.0;
			String line1 = ChatColor.stripColor(s.getLine(0)).trim();
			if (line1.startsWith(L.gC(false))) {
				try {
					String price = line1.substring(1, line1.length());
					staticprice = calc.twoDecimals(Double.parseDouble(price));
					setprice = true;
				} catch (Exception e) {
					setprice = false;
				}
			} else if (line1.endsWith(L.gC(false))) {
				try {
					String price = line1.substring(0, line1.length() - 1);
					staticprice = calc.twoDecimals(Double.parseDouble(price));
					setprice = true;
				} catch (Exception e) {
					setprice = false;
				}
			}
			if (icevent.isShiftClick()) {

				Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
				HyperPlayer hp = dh.getHyperPlayer(p);
				if (im.hasenchants(icevent.getCurrentItem())) {
					icevent.setCancelled(true);
					return;
				}

				HyperObject ho = dh.getHyperObject(icevent.getCurrentItem().getTypeId(), im.getDamageValue(icevent.getCurrentItem()), hp.getEconomy());
				if (ho == null) {
					icevent.setCancelled(true);
					return;
				}

				int camount = icevent.getCurrentItem().getAmount();

				if (slot < 27) {
					if (buy) {
						PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
						pt.setHyperObject(ho);
						pt.setTradePartner(dh.getHyperPlayer(line34));
						pt.setAmount(camount);
						pt.setGiveInventory(icevent.getView().getTopInventory());
						if (setprice) {
							pt.setMoney(calc.twoDecimals((camount * staticprice)));
							pt.setSetPrice(true);
						}
						TransactionResponse response = hp.processTransaction(pt);
						response.sendMessages();
					} else {
						p.sendMessage(L.get("CANNOT_PURCHASE_ENCHANTMENTS_FROM_CHEST"));
					}

				} else if (slot >= 27) {
					if (sell) {
						if (p.getGameMode() == GameMode.CREATIVE && hc.s().gB("block-selling-in-creative-mode")) {
							p.sendMessage(L.get("CANT_SELL_CREATIVE"));
							icevent.setCancelled(true);
							return;
						}
						int itemamount = im.countItems(ho.getId(), ho.getData(), icevent.getView().getTopInventory());

						if (itemamount > 0) {
							int space = im.getAvailableSpace(ho.getId(), ho.getData(), icevent.getView().getTopInventory());
							if (space >= camount) {
								if (dh.hasAccount(line34)) {
									double bal = dh.getHyperPlayer(line34).getBalance();
									double cost = ho.getValue(camount);
									if (setprice) {
										cost = staticprice * camount;
									}

									if (bal >= cost) {
										PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
										pt.setHyperObject(ho);
										pt.setTradePartner(dh.getHyperPlayer(line34));
										pt.setAmount(camount);
										pt.setReceiveInventory(icevent.getView().getTopInventory());
										if (setprice) {
											pt.setMoney(calc.twoDecimals(cost));
											pt.setSetPrice(true);
										}
										TransactionResponse response = hp.processTransaction(pt);
										response.sendMessages();
									} else {
										L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), line34);
									}
								}
							} else {
								p.sendMessage(L.get("CHEST_SHOP_NOT_ENOUGH_SPACE"));
							}
						} else {
							p.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
						}

					} else {
						p.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
					}

				}

				icevent.setCancelled(true);
				return;

			} else if (icevent.isLeftClick()) {
				Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
				HyperPlayer hp = dh.getHyperPlayer(p);
				if (!im.hasenchants(icevent.getCurrentItem())) {
					HyperObject ho = dh.getHyperObject(icevent.getCurrentItem().getTypeId(), im.getDamageValue(icevent.getCurrentItem()), hp.getEconomy());

					if (slot < 27 && ho != null) {
						String name = ho.getName();
						if (buy) {
							double price = ho.getValue(1);
							if (setprice) {
								price = staticprice;
							}
							p.sendMessage(L.get("LINE_BREAK"));
							p.sendMessage(L.f(L.get("CHEST_SHOP_BUY_VALUE"), 1, price, name, line34));
							p.sendMessage(L.get("LINE_BREAK"));
						} else {
							p.sendMessage(L.get("CANNOT_PURCHASE_ITEMS_FROM_CHEST"));
						}

					} else if (slot >= 27 && ho != null) {
						String name = ho.getName();
						if (sell) {
							int itemamount = im.countItems(ho.getId(), ho.getData(), icevent.getView().getTopInventory());

							if (itemamount > 0) {
								double price = ho.getValue(1);
								if (setprice) {
									price = staticprice;
								}
								p.sendMessage(L.get("LINE_BREAK"));
								p.sendMessage(L.f(L.get("CHEST_SHOP_SELL_VALUE"), 1, price, name, line34));
								p.sendMessage(L.get("LINE_BREAK"));
							} else {
								p.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
							}
						} else {
							p.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
						}

					}
				} else {
					if (slot < 27) {
						if (buy) {
							double price = 0;
							for (Enchantment enchantment : im.listEnchantments(icevent.getCurrentItem())) {
								int lvl = im.getEnchantmentLevel(icevent.getCurrentItem(), enchantment);
								String nam = dh.getEnchantNameWithoutLevel(enchantment.getName());
								String fnam = nam + lvl;
								HyperObject ho = dh.getHyperObject(fnam, hp.getEconomy());
								price += ho.getValue(EnchantmentClass.fromString(p.getItemInHand().getType().name()), hp);
								if (setprice) {
									price = staticprice;
								}
							}
							price = calc.twoDecimals(price);
							if (im.canEnchantItem(p.getItemInHand())) {
								p.sendMessage(L.get("LINE_BREAK"));
								p.sendMessage(L.f(L.get("CHEST_SHOP_ENCHANTMENT_VALUE"), price, line34));
								p.sendMessage(L.get("LINE_BREAK"));
							} else {
								p.sendMessage(L.get("ITEM_CANNOT_ACCEPT_ENCHANTMENTS"));
							}

						} else {
							p.sendMessage(L.get("CANNOT_PURCHASE_ENCHANTMENTS_FROM_CHEST"));
						}

					} else {

						p.sendMessage(L.get("CANNOT_SELL_ENCHANTMENTS_HERE"));

					}
				}

				icevent.setCancelled(true);
				return;
			} else if (icevent.isRightClick()) {
				Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
				HyperPlayer hp = dh.getHyperPlayer(p);
				if (!im.hasenchants(icevent.getCurrentItem())) {
					HyperObject ho = dh.getHyperObject(icevent.getCurrentItem().getTypeId(), im.getDamageValue(icevent.getCurrentItem()), hp.getEconomy());

					if (slot < 27 && ho != null) {
						if (buy) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
							pt.setHyperObject(ho);
							pt.setTradePartner(dh.getHyperPlayer(line34));
							pt.setAmount(1);
							pt.setGiveInventory(icevent.getView().getTopInventory());
							if (setprice) {
								pt.setMoney(staticprice);
								pt.setSetPrice(true);
							}
							TransactionResponse response = hp.processTransaction(pt);
							response.sendMessages();

						} else {
							p.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
						}

					} else if (slot >= 27 && ho != null) {
						if (sell) {
							if (p.getGameMode() == GameMode.CREATIVE && hc.s().gB("block-selling-in-creative-mode")) {
								p.sendMessage(L.get("CANT_SELL_CREATIVE"));
								icevent.setCancelled(true);
								return;
							}
							int itemamount = im.countItems(ho.getId(), ho.getData(), icevent.getView().getTopInventory());

							if (itemamount > 0) {
								int space = im.getAvailableSpace(ho.getId(), ho.getData(), icevent.getView().getTopInventory());
								if (space >= 1) {
									if (dh.hasAccount(line34)) {
										double bal = dh.getHyperPlayer(line34).getBalance();
										double cost = ho.getValue(1);
										if (setprice) {
											cost = staticprice;
										}
										if (bal >= cost) {
											PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
											pt.setHyperObject(ho);
											pt.setTradePartner(dh.getHyperPlayer(line34));
											pt.setAmount(1);
											pt.setReceiveInventory(icevent.getView().getTopInventory());
											if (setprice) {
												pt.setMoney(cost);
												pt.setSetPrice(true);
											}
											TransactionResponse response = hp.processTransaction(pt);
											response.sendMessages();
										} else {
											p.sendMessage(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), line34));
										}
									}
								} else {
									p.sendMessage(L.get("CHEST_SHOP_NOT_ENOUGH_SPACE"));
								}
							} else {
								p.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
							}
						} else {
							p.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
						}

					}
				} else {
					if (slot < 27) {
						if (buy) {
							for (Enchantment enchantment : im.listEnchantments(icevent.getCurrentItem())) {
								int lvl = im.getEnchantmentLevel(icevent.getCurrentItem(), enchantment);
								String nam = dh.getEnchantNameWithoutLevel(enchantment.getName());
								String fnam = nam + lvl;
								HyperObject ho = dh.getHyperObject(fnam, hp.getEconomy());
								PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_ITEM);
								pt.setHyperObject(ho);
								pt.setTradePartner(dh.getHyperPlayer(line34));
								pt.setGiveItem(icevent.getCurrentItem());
								if (setprice) {
									pt.setMoney(staticprice);
									pt.setSetPrice(true);
								}
								TransactionResponse response = hp.processTransaction(pt);
								response.sendMessages();
							}
						} else {
							p.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
						}
					} else if (slot >= 27) {
						p.sendMessage(L.get("CANNOT_SELL_ENCHANTMENTS_HERE"));
					}
				}
				icevent.setCancelled(true);
				return;
			} else {
				icevent.setCancelled(true);
				return;
			}

		} catch (Exception e) {
			new HyperError(e);
		}
	}

}
