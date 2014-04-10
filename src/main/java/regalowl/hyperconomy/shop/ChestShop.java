package regalowl.hyperconomy.shop;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperItemStack;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;

public class ChestShop implements Listener {

	private HyperConomy hc;
	private CommonFunctions cf;
	private LanguageFile L;
	private DataManager em;

	private ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
	private ArrayList<BlockFace> allfaces = new ArrayList<BlockFace>();

	public ChestShop() {

		hc = HyperConomy.hc;
		em = hc.getDataManager();
		cf = hc.gCF();
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

		if (hc.gYH().gFC("config").getBoolean("enable-feature.chest-shops")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
		}

	}

	public boolean isChestShopSign(Block b) {
		try {
			if (b == null) {
				return false;
			}
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
							if (relative.getRelative(attachedface.getOppositeFace()).equals(b)) {
								return true;
							}
						}
					}
				}
			}
			return false;
		} catch (Exception e) {
			hc.gDB().writeError(e);
			return false;
		}
	}

	public Sign getChestShopSign(Block b) {
		try {
			if (b == null) {
				return null;
			}
			if (b.getState() instanceof Chest) {
				Chest chest = (Chest) b.getState();
				String world = chest.getBlock().getWorld().getName();
				BlockState signblock = Bukkit.getWorld(world).getBlockAt(chest.getX(), chest.getY() + 1, chest.getZ()).getState();
				if (signblock instanceof Sign) {
					Sign s = (Sign) signblock;
					String line2 = ChatColor.stripColor(s.getLine(1)).trim();
					if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
						return s;
					}
				}
			} else if (b.getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) b.getState();
				String line2 = s.getLine(1).trim();
				if (line2.equalsIgnoreCase(ChatColor.AQUA + "[Trade]") || line2.equalsIgnoreCase(ChatColor.AQUA + "[Buy]") || line2.equalsIgnoreCase(ChatColor.AQUA + "[Sell]")) {
					BlockState chestblock = Bukkit.getWorld(s.getBlock().getWorld().getName()).getBlockAt(s.getX(), s.getY() - 1, s.getZ()).getState();
					if (chestblock instanceof Chest) {
						s.update();
						return s;
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
							if (relative.getRelative(attachedface.getOppositeFace()).equals(b)) {
								return s;
							}
						}
					}
				}
			}
			return null;
		} catch (Exception e) {
			hc.gDB().writeError(e);
			return null;
		}
	}

	public boolean isChestShop(Block b, boolean includeSign) {
		try {
			if (b == null) {
				return false;
			}
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
			} else {
				if (includeSign && isChestShopSign(b)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			hc.gDB().writeError(e);
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
			hc.gDB().writeError(e);
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
			hc.gDB().writeError(e);
			return null;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent bbevent) {
		if (isChestShop(bbevent.getBlock(), true)) {
			if (isChestShopSign(bbevent.getBlock()) && bbevent.getPlayer().hasPermission("hyperconomy.admin") && bbevent.getPlayer().isSneaking()) {
				return;
			}
			Sign s = getChestShopSign(bbevent.getBlock());
			String line34 = ChatColor.stripColor(s.getLine(2)).trim() + ChatColor.stripColor(s.getLine(3)).trim();
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

			if (hc.gYH().gFC("config").getBoolean("shop.require-chest-shops-to-be-in-shop") && !em.inAnyShop(scevent.getPlayer())) {
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
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClickEvent(InventoryClickEvent icevent) {
		try {
			Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
			if (hc.getHyperLock().isLocked(p)) {
				if (isChestShop(icevent.getInventory().getHolder())) {
					hc.getHyperLock().sendLockMessage(p);
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

			String chestOwnerName = ChatColor.stripColor(s.getLine(2)).trim() + ChatColor.stripColor(s.getLine(3)).trim();
			String clicker = icevent.getWhoClicked().getName();
			// if clicker is owner of chest return
			if (clicker.equalsIgnoreCase(chestOwnerName)) {
				return;
			}
			ItemStack clickedItem = icevent.getCurrentItem();
			if (clickedItem == null) {
				icevent.setCancelled(true);
				return;
			}
			HyperItemStack his = new HyperItemStack(clickedItem);
			if (his.isDamaged()) {
				icevent.setCancelled(true);
				p.sendMessage(L.get("CHESTSHOP_CANT_TRADE_DAMAGED"));
				return;
			}

			boolean setprice = false;
			double staticprice = 0.0;
			String line1 = ChatColor.stripColor(s.getLine(0)).trim();
			if (line1.startsWith(L.gC(false))) {
				try {
					String price = line1.substring(1, line1.length());
					staticprice = cf.twoDecimals(Double.parseDouble(price));
					setprice = true;
				} catch (Exception e) {
					setprice = false;
				}
			} else if (line1.endsWith(L.gC(false))) {
				try {
					String price = line1.substring(0, line1.length() - 1);
					staticprice = cf.twoDecimals(Double.parseDouble(price));
					setprice = true;
				} catch (Exception e) {
					setprice = false;
				}
			}

			HyperAccount chestOwner = em.getAccount(chestOwnerName);
			
			HyperEconomy chestOwnerEconomy = em.getDefaultEconomy();
			if (chestOwner instanceof HyperPlayer) {
				HyperPlayer hPlayer = (HyperPlayer)chestOwner;
				chestOwnerEconomy = hPlayer.getHyperEconomy();
			}
			HyperPlayer clickPlayer = em.getHyperPlayer(p);
			HyperObject hyperObject = null;
			if (!his.hasEnchants()) {
				hyperObject = chestOwnerEconomy.getHyperObject(clickedItem);
				if (hyperObject == null) {
					if (setprice) {
						hyperObject = his.generateTempItem();
					} else {
						icevent.setCancelled(true);
						return;
					}
				}
			}

			Inventory shopInventory = icevent.getView().getTopInventory();
			if (icevent.isShiftClick()) {
				if (his.hasEnchants()) {
					icevent.setCancelled(true);
					return;
				}
				int camount = clickedItem.getAmount();
				if (slot < 27) {
					if (buy) {
						PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
						pt.setHyperObject(hyperObject);
						pt.setTradePartner(chestOwner);
						pt.setAmount(camount);
						pt.setGiveInventory(shopInventory);
						if (setprice) {
							pt.setMoney(cf.twoDecimals((camount * staticprice)));
							pt.setSetPrice(true);
						}
						TransactionResponse response = clickPlayer.processTransaction(pt);
						response.sendMessages();
					} else {
						p.sendMessage(L.get("CANNOT_PURCHASE_ENCHANTMENTS_FROM_CHEST"));
					}
				} else if (slot >= 27) {
					if (sell) {
						if (p.getGameMode() == GameMode.CREATIVE && hc.gYH().gQFC("config").gB("shop.block-selling-in-creative-mode")) {
							p.sendMessage(L.get("CANT_SELL_CREATIVE"));
							icevent.setCancelled(true);
							return;
						}
						int itemamount = hyperObject.count(shopInventory);
						if (itemamount > 0) {
							int space = hyperObject.getAvailableSpace(shopInventory);
							if (space >= camount) {
								double bal = chestOwner.getBalance();
								double cost = hyperObject.getSellPrice(camount);
								if (setprice) {
									cost = staticprice * camount;
								}
								if (bal >= cost) {
									PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
									pt.setHyperObject(hyperObject);
									pt.setTradePartner(chestOwner);
									pt.setAmount(camount);
									pt.setReceiveInventory(shopInventory);
									if (setprice) {
										pt.setMoney(cf.twoDecimals(cost));
										pt.setSetPrice(true);
									}
									TransactionResponse response = clickPlayer.processTransaction(pt);
									response.sendMessages();
								} else {
									L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), chestOwner.getName());
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
				if (!his.hasEnchants()) {
					if (slot < 27 && hyperObject != null) {
						String name = hyperObject.getDisplayName();
						if (buy) {
							double price = hyperObject.getSellPrice(1);
							if (setprice) {
								price = staticprice;
							}
							p.sendMessage(L.get("LINE_BREAK"));
							p.sendMessage(L.f(L.get("CHEST_SHOP_BUY_VALUE"), 1, price, name, chestOwner.getName()));
							p.sendMessage(L.get("LINE_BREAK"));
						} else {
							p.sendMessage(L.get("CANNOT_PURCHASE_ITEMS_FROM_CHEST"));
						}

					} else if (slot >= 27 && hyperObject != null) {
						String name = hyperObject.getDisplayName();
						if (sell) {
							int itemamount = hyperObject.count(shopInventory);

							if (itemamount > 0) {
								double price = hyperObject.getSellPrice(1, clickPlayer);
								if (setprice) {
									price = staticprice;
								}
								p.sendMessage(L.get("LINE_BREAK"));
								p.sendMessage(L.f(L.get("CHEST_SHOP_SELL_VALUE"), 1, price, name, chestOwner.getName()));
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
							for (Enchantment enchantment : his.listEnchantments()) {
								int lvl = his.getEnchantmentLevel(enchantment);
								String nam = chestOwnerEconomy.getEnchantNameWithoutLevel(enchantment.getName());
								if (nam == null) {
									icevent.setCancelled(true);
									return;
								}
								String fnam = nam + lvl;
								HyperObject ho = chestOwnerEconomy.getHyperObject(fnam);
								price += ho.getSellPrice(EnchantmentClass.fromString(p.getItemInHand().getType().name()), clickPlayer);
								if (setprice) {
									price = staticprice;
								}
							}
							price = cf.twoDecimals(price);
							if (new HyperItemStack(p.getItemInHand()).canEnchantItem()) {
								p.sendMessage(L.get("LINE_BREAK"));
								p.sendMessage(L.f(L.get("CHEST_SHOP_ENCHANTMENT_VALUE"), price, chestOwner.getName()));
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
				if (!his.hasEnchants()) {
					if (slot < 27 && hyperObject != null) {
						if (buy) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
							pt.setHyperObject(hyperObject);
							pt.setTradePartner(chestOwner);
							pt.setAmount(1);
							pt.setGiveInventory(shopInventory);
							if (setprice) {
								pt.setMoney(staticprice);
								pt.setSetPrice(true);
							}
							TransactionResponse response = clickPlayer.processTransaction(pt);
							response.sendMessages();

						} else {
							p.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
						}

					} else if (slot >= 27 && hyperObject != null) {
						if (sell) {
							if (p.getGameMode() == GameMode.CREATIVE && hc.gYH().gQFC("config").gB("shop.block-selling-in-creative-mode")) {
								p.sendMessage(L.get("CANT_SELL_CREATIVE"));
								icevent.setCancelled(true);
								return;
							}
							int itemamount = hyperObject.count(shopInventory);

							if (itemamount > 0) {
								int space = hyperObject.getAvailableSpace(shopInventory);
								if (space >= 1) {
									double bal = chestOwner.getBalance();
									double cost = hyperObject.getSellPrice(1);
									if (setprice) {
										cost = staticprice;
									}
									if (bal >= cost) {
										PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
										pt.setHyperObject(hyperObject);
										pt.setTradePartner(chestOwner);
										pt.setAmount(1);
										pt.setReceiveInventory(shopInventory);
										if (setprice) {
											pt.setMoney(cost);
											pt.setSetPrice(true);
										}
										TransactionResponse response = clickPlayer.processTransaction(pt);
										response.sendMessages();
									} else {
										p.sendMessage(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), chestOwner.getName()));
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
							for (Enchantment enchantment : his.listEnchantments()) {
								int lvl = his.getEnchantmentLevel(enchantment);
								String nam = chestOwnerEconomy.getEnchantNameWithoutLevel(enchantment.getName());
								String fnam = nam + lvl;
								HyperObject ho = chestOwnerEconomy.getHyperObject(fnam);
								PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_ITEM);
								pt.setHyperObject(ho);
								pt.setTradePartner(chestOwner);
								pt.setGiveItem(clickedItem);
								if (setprice) {
									pt.setMoney(staticprice);
									pt.setSetPrice(true);
								}
								TransactionResponse response = clickPlayer.processTransaction(pt);
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
			hc.gDB().writeError(e);
		}
	}

}
