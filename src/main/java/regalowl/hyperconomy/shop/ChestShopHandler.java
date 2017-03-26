package regalowl.hyperconomy.shop;




import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;


import java.util.concurrent.ConcurrentHashMap;


import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;

import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.minecraft.ChestShopClickEvent;
import regalowl.hyperconomy.event.minecraft.ChestShopCloseEvent;
import regalowl.hyperconomy.event.minecraft.ChestShopOpenEvent;
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
import regalowl.hyperconomy.tradeobject.TempTradeItem;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;

public class ChestShopHandler implements HyperEventListener {

	private HyperConomy hc;
	private LanguageFile L;
	private DataManager em;
	//private HashMap<HLocation, ChestShop> openShops = new HashMap<HLocation, ChestShop>();
	private ConcurrentHashMap<HLocation, ChestShop> chestShops = new ConcurrentHashMap<HLocation, ChestShop>();

	public ChestShopHandler(HyperConomy hc) {
		this.hc = hc;
		em = hc.getDataManager();
		L = hc.getLanguageFile();
		if (hc.getConf().getBoolean("enable-feature.chest-shops")) hc.getHyperEventHandler().registerListener(this);
	}
	

	
	private void loadChestShops() {
		try {
			chestShops.clear();
			new Thread(new Runnable() {
				public void run() {
					SQLRead sr = hc.getSQLRead();
					QueryResult dbData = sr.select("SELECT * FROM hyperconomy_chest_shops");
					while (dbData.next()) {
						String w = dbData.getString("WORLD");
						int x = dbData.getInt("X");
						int y = dbData.getInt("Y");
						int z = dbData.getInt("Z");
						HyperAccount owner = hc.getDataManager().getAccount(dbData.getString("OWNER"));
						long priceIncrement = Long.parseLong(dbData.getString("PRICE_INCREMENT"));
						HLocation l = new HLocation(w,x,y,z);
						chestShops.put(l, new ChestShop(hc, l, owner, priceIncrement));
					}
					dbData.close();
					dbData = sr.select("SELECT * FROM hyperconomy_chest_shop_items");
					while (dbData.next()) {
						String chestId = dbData.getString("CHEST_ID");
						int slot = dbData.getInt("SLOT");
						String data = dbData.getString("DATA");
						double buyPrice = dbData.getDouble("BUY_PRICE");
						double sellPrice = dbData.getDouble("SELL_PRICE");
						ChestShopType type = ChestShopType.fromString(dbData.getString("TYPE"));
						HLocation l = HLocation.fromBlockString(chestId);
						ChestShop cs = chestShops.get(l);
						if (cs == null) continue;
						cs.setCustomPriceItem(chestId, slot, data, buyPrice, sellPrice, type);
					}
					dbData.close();
					dbData = null;	
				}
			}).start();
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}
	

	
	public ChestShop getChestShop(HLocation loc) {
		if (!hc.enabled()) return null;
		ChestShop cs = chestShops.get(loc);
		if (cs == null) return cs;
		if (!cs.isInitialized()) cs.initialize();
		return cs;
	}
	
	
	private void handleClick(ChestShopClickEvent event) {
		
		//try {
			event.cancel();
			HyperPlayer clicker = event.getClicker();
			if (hc.getHyperLock().isLocked(clicker)) {
				hc.getHyperLock().sendLockMessage(clicker);
				return;
			}
			ChestShop cs = event.getChestShop(); 

			
			HItemStack clickedItem = null;


			int slot = event.getClickedSlot();
			HInventory shopInventory = cs.getInventory();
			HyperAccount owner = cs.getOwner();
			if (clicker.getName().equals(owner.getName())) {
				handleOwnerClick(event);
				return;
			}
			HInventory playerInventory = clicker.getInventory();


			boolean isChestInventory = false;
			boolean isControlButton = false;
			boolean isPlayerInventory = false;
			if (slot < 27) isChestInventory = true;
			if (slot >= 27 && slot < 36) isControlButton = true;
			if (slot >= 36) {
				isPlayerInventory = true;
				return;
			}
			if (isChestInventory) clickedItem = shopInventory.getItem(event.getClickedSlot());
			if (isPlayerInventory) {
				int translatedSlot = slot - 36;
				if (translatedSlot >= 27) {
					translatedSlot -= 27;
				} else {
					translatedSlot += 9;
				}
				clickedItem = playerInventory.getItem(translatedSlot);
			}
			if (clickedItem == null) return;
			//if (isPlayerInventory) clickedItem = clicker.getInventory().getItem(slot);
			
			

			/*
			if (clickedItem.isDamaged()) {
				clicker.sendMessage(L.get("CHESTSHOP_CANT_TRADE_DAMAGED"));
				return;
			}
			*/
			HyperEconomy chestOwnerEconomy = em.getDefaultEconomy();
			if (owner instanceof HyperPlayer) {
				HyperPlayer hPlayer = (HyperPlayer)owner;
				chestOwnerEconomy = hPlayer.getHyperEconomy();
			}
			TradeObject tradeObject = chestOwnerEconomy.getTradeObject(clickedItem);
			if (tradeObject == null) {
				tradeObject = TempTradeItem.generate(hc, clickedItem);
			}
			
			int stackQuantity = 1;
			if (event.isShiftClick()) stackQuantity = clickedItem.getAmount();

			
			if (event.isRightClick()) {
				if (!cs.isSellSlot(slot)) {
					clicker.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
					return;
				}
				if (clicker.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
					clicker.sendMessage(L.get("CANT_SELL_CREATIVE"));
					return;
				}
				int itemamount = shopInventory.count(tradeObject.getItem());
				if (itemamount <= 0) {
					clicker.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
					return;
				}
				
				
				int playerItemCount = clicker.getInventory().count(tradeObject.getItem());
				if (playerItemCount < stackQuantity) {
					clicker.sendMessage(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), tradeObject.getDisplayName()));
					return;
				}
				
				int space = shopInventory.getAvailableSpace(tradeObject.getItem());
				if (space < stackQuantity) {
					clicker.sendMessage(L.get("CHEST_SHOP_NOT_ENOUGH_SPACE"));
					return;
				}
				
				
							
				double bal = owner.getBalance();
				double cost = tradeObject.getSellPrice(stackQuantity);
				//if (hasStaticPrice) cost = staticPrice;
				if (bal < cost) {
					clicker.sendMessage(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), owner.getName()));
					return;
				}
					
				PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
				pt.setHyperObject(tradeObject);
				pt.setTradePartner(owner);
				pt.setAmount(stackQuantity);
				pt.setReceiveInventory(shopInventory);
				pt.setMoney(cs.getSellPrice(slot));
				pt.setSetPrice(true);
				//if (hasStaticPrice) {
				//	pt.setMoney(cost);
				//	pt.setSetPrice(true);
				//}
				TransactionResponse response = clicker.processTransaction(pt);
				response.sendMessages();
				if (response.successful()) cs.refreshMenu();

			} else if (event.isLeftClick()) {
				if (!cs.isBuySlot(slot)) {
					clicker.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
					return;
				}
				PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
				pt.setHyperObject(tradeObject);
				pt.setTradePartner(owner);
				pt.setAmount(stackQuantity);
				pt.setGiveInventory(shopInventory);
				pt.setMoney(cs.getBuyPrice(slot));
				pt.setSetPrice(true);
				//if (hasStaticPrice) {
				//	pt.setMoney(staticPrice);
				//	pt.setSetPrice(true);
				//}
				TransactionResponse response = clicker.processTransaction(pt);
				response.sendMessages();
				if (response.successful()) cs.refreshMenu();

			}
		//} catch (Exception e) {
		//	hc.gSDL().getErrorWriter().writeError(e);
		//}
			
	}
	
	private void handleOwnerClick(ChestShopClickEvent event) {
		HItemStack clickedItem = null;
		
		HyperPlayer clicker = event.getClicker();
		ChestShop cs = event.getChestShop();
		HInventory shopInventory = cs.getInventory();
		HInventory playerInventory = clicker.getInventory();
		int slot = event.getClickedSlot();
		boolean isChestInventory = false;
		boolean isControlButton = false;
		boolean isPlayerInventory = false;
		if (slot < 27) isChestInventory = true;
		if (slot >= 27 && slot < 36) isControlButton = true;
		if (slot >= 36) isPlayerInventory = true;
		if (isChestInventory) clickedItem = shopInventory.getItem(slot);
		if (isPlayerInventory) {
			int translatedSlot = slot - 36;
			if (translatedSlot >= 27) {
				translatedSlot -= 27;
			} else {
				translatedSlot += 9;
			}
			clickedItem = playerInventory.getItem(translatedSlot);
		}
		if ((clickedItem == null || clickedItem.isBlank()) && !isControlButton) return;
		
		int stackQuantity = 1;
		if (event.isShiftClick()) {
			stackQuantity = clickedItem.getAmount();
		} 

		if (isChestInventory) {
			if (cs.inSelectItemMode()) {
				cs.setEditSlot(slot);
				cs.setEditMode(true, false);
				cs.setSelectItemMode(false);
			} else if (cs.inEditItemMode() || cs.inPreviewMode() || cs.inDeleteMode()) {
				//do nothing, maybe add a notice
			} else {
				if (playerInventory.getAvailableSpace(clickedItem) >= stackQuantity) {
					playerInventory.add(1, clickedItem);
					shopInventory.remove(1, clickedItem);
				}
			}
		} else if (isPlayerInventory) {
			if (cs.inSelectItemMode() || cs.inEditItemMode() || cs.inPreviewMode() || cs.inDeleteMode()) {
				//do nothing, maybe add a notice
			} else {
				if (shopInventory.getAvailableSpace(clickedItem) >= stackQuantity) {
					playerInventory.remove(stackQuantity, clickedItem);
					shopInventory.add(stackQuantity, clickedItem);
				}
			}
		} else if (isControlButton) {
			switch (slot) {
				case 35:
					if (!cs.inEditItemMode() && !cs.inSelectItemMode() && !cs.inPreviewMode() && !cs.inDeleteMode()) {
						cs.setSelectItemMode(true);
					} else if (cs.inPreviewMode())  {
						cs.setInPreviewMode(false);
					} else if (cs.inDeleteMode())  {
						cs.setInDeleteMode(false);
					} else {
						cs.setSelectItemMode(false);
						cs.setEditMode(false, false);
					}
					break;
				case 34:
					if (cs.inEditItemMode()) {
						if (cs.editAllItems()) {
							cs.toggleTypeAll();
						} else {
							cs.toggleType(cs.getEditSlot());
						}
					} else if (cs.inSelectItemMode()) {
						cs.setEditMode(true, true);
						cs.setSelectItemMode(false);
						cs.setEditSlot(1000);
					} else {
						cs.setInPreviewMode(true);
					}
					break;
				case 33:
					if (!cs.inEditItemMode()) return;
					if (cs.editingBuyPrice() && cs.editingSellPrice()) {
						cs.setEditBuyPrice(false);
						cs.setEditSellPrice(true);
					} else if (!cs.editingBuyPrice() && cs.editingSellPrice()) {
						cs.setEditBuyPrice(true);
						cs.setEditSellPrice(false);
					} else if (cs.editingBuyPrice() && !cs.editingSellPrice()) {
						cs.setEditBuyPrice(true);
						cs.setEditSellPrice(true);
					}
					break;
				case 32:
					if (!cs.inEditItemMode()) return;
					cs.setPriceIncrement(cs.getPriceIncrement()/10);
					break;
				case 31:
					if (!cs.inEditItemMode()) return;
					cs.setPriceIncrement(cs.getPriceIncrement()*10);
					break;	
				case 30:
					if (!cs.inEditItemMode()) return;
					if (cs.editAllItems()) {
						int firstItemSlot = cs.getSlotOfFirstItem();
						if (firstItemSlot < 0) return;
						if (cs.editingBuyPrice()) {
							cs.setBuyPriceAll(cs.getBuyPrice(firstItemSlot) - cs.getFractionalPriceIncrement());
						} 
						if (cs.editingSellPrice()) {
							cs.setSellPriceAll(cs.getSellPrice(firstItemSlot) - cs.getFractionalPriceIncrement());
						}
					} else {
						if (cs.editingBuyPrice()) {
							cs.setBuyPrice(cs.getEditSlot(), cs.getBuyPrice(cs.getEditSlot()) - cs.getFractionalPriceIncrement());
						} 
						if (cs.editingSellPrice()) {
							cs.setSellPrice(cs.getEditSlot(), cs.getSellPrice(cs.getEditSlot()) - cs.getFractionalPriceIncrement());
						}
					}

					break;
				case 29:
					if (!cs.inEditItemMode()) return;
					if (cs.editAllItems()) {
						int firstItemSlot = cs.getSlotOfFirstItem();
						if (firstItemSlot < 0) return;
						if (cs.editingBuyPrice()) {
							cs.setBuyPriceAll(cs.getBuyPrice(firstItemSlot) + cs.getFractionalPriceIncrement());
						} 
						if (cs.editingSellPrice()) {
							cs.setSellPriceAll(cs.getSellPrice(firstItemSlot) + cs.getFractionalPriceIncrement());
						}
					} else {
						if (cs.editingBuyPrice()) {
							cs.setBuyPrice(cs.getEditSlot(), cs.getBuyPrice(cs.getEditSlot()) + cs.getFractionalPriceIncrement());
						} 
						if (cs.editingSellPrice()) {
							cs.setSellPrice(cs.getEditSlot(), cs.getSellPrice(cs.getEditSlot()) + cs.getFractionalPriceIncrement());
						}
					}

					break;
				case 27:
					if (cs.inEditItemMode() || cs.inPreviewMode() || cs.inEditItemMode() || cs.inSelectItemMode()) return;
					if (cs.inDeleteMode()) {
						cs.delete();				
						chestShops.remove(cs.getChestLocation());
						return;
					} else {
						cs.setInDeleteMode(true);
					}

					break;
			}
			
		}

		cs.refreshMenu();
	}
	
	

	
	private void handleOpen(ChestShopOpenEvent event) {
		event.cancel();
		ChestShop cs = event.getChestShop();
		if (!cs.hasViewers()) {//no current viewers
			cs.addViewer(event.getOpener());
			if (event.getOpener().equals(cs.getOwner())) {//if the viewer is the owner, set the flag
				cs.setOpenedByOwner(true);
				hc.getMC().openInventory(cs.getOwnerShopMenu(), event.getOpener(), cs.getMenuName());
				//Bukkit.broadcastMessage("new chest shop opened by owner");
				
			} else {//if the viewer isn't the owner, open the shop menu
				hc.getMC().openInventory(cs.getShopMenu(), event.getOpener(), cs.getMenuName());
				//Bukkit.broadcastMessage("new chest shop opened by shopper");
				
			}
			

		} else { //shop is already open
			
			if (event.getOpener().equals(cs.getOwner())) {//if shop is open already and owner tries to open it, cancel the event
				//Bukkit.broadcastMessage("shop already opened by a shopper, owner can't open, canceling");
				return;
			}
			if (cs.openedByOwner()) {
				//Bukkit.broadcastMessage("shop already opened by owner, can't shop");
				return;
			}
			//add the new viewer if not the owner
			cs.addViewer(event.getOpener());
			hc.getMC().openInventory(cs.getShopMenu(), event.getOpener(), cs.getMenuName());
			//Bukkit.broadcastMessage("chest shop opened by second player");
			
			
		}
	}
	

	private void handleClose(ChestShopCloseEvent event) {
		ChestShop cs = event.getChestShop();
		cs.removeViewer(event.getCloser());//remove closing viewer
		if (cs.openedByOwner() && event.getCloser().equals(cs.getOwner())) {
			cs.setOpenedByOwner(false);
			cs.setEditMode(false, false);
			cs.setSelectItemMode(false);
			//Bukkit.broadcastMessage("owner closed shop");
			
		} 


	}

	/*
	private ChestShop getOpenChestShop(ChestShop eventShop) {
		ChestShop cs = openShops.get(eventShop.getChestLocation());
		if (cs == null) {
			cs = eventShop;

		}
		return cs;
	}
	*/

	public ChestShop addNewChestShop(HLocation loc, HyperAccount owner) {
		if (!hc.enabled()) return null;
		HLocation signLocation = new HLocation(loc);
		signLocation.setY(loc.getY() + 1);
		HSign sign = hc.getMC().getSign(signLocation);
		if (sign == null || owner == null) return null;
		ChestShop cs = new ChestShop(hc, loc, owner, 100);
		cs.initialize();
		if (!cs.isValid()) return null;
		cs.save();
		chestShops.put(loc, cs);
		return cs;
	}
	


	
	
	public boolean isChestShopOpen(HLocation loc) {
		if (!hc.enabled()) return false;
		return getChestShop(loc).hasViewers();
	}
	
	public ChestShop getOpenShop(HyperPlayer hp) {
		if (!hc.enabled()) return null;
		for (ChestShop cs:chestShops.values()) {
			if (cs.hasViewer(hp)) {
				if (!cs.isInitialized()) cs.initialize();
				return cs;
			}
		}
		return null;
	}

	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (!hc.enabled()) return;
		if (event instanceof HBlockBreakEvent) {
			HBlockBreakEvent bbevent = (HBlockBreakEvent) event;
			handleBlockBreak(bbevent);
		} else if (event instanceof HEntityExplodeEvent) {
			HEntityExplodeEvent eeevent = (HEntityExplodeEvent) event;
			for (HBlock b : eeevent.getBrokenBlocks()) {
				if (chestShops.containsKey(b.getLocation())) eeevent.cancel();
			}
		} else if (event instanceof HBlockPistonExtendEvent) {
			HBlockPistonExtendEvent bpeevent = (HBlockPistonExtendEvent) event;
			for (HBlock b : bpeevent.getBlocks()) {
				if (chestShops.containsKey(b.getLocation())) bpeevent.cancel();
			}
		} else if (event instanceof HBlockPistonRetractEvent) {
			HBlockPistonRetractEvent bprevent = (HBlockPistonRetractEvent) event;
			if (chestShops.containsKey(bprevent.getRetractedBlock().getLocation())) bprevent.cancel();
		} else if (event instanceof HBlockPlaceEvent) {
			HBlockPlaceEvent bpevent = (HBlockPlaceEvent) event;
			HBlock block = bpevent.getBlock();
			for (HBlock b : block.getSurroundingBlocks()) {
				if (hc.getMC().isChestShopChest(b.getLocation())) {
					bpevent.cancel();
				}
			}
		} else if (event instanceof HSignChangeEvent) {
			HSignChangeEvent hevent = (HSignChangeEvent) event;
			handleSignChange(hevent);
		} else if (event instanceof ChestShopClickEvent) {
			ChestShopClickEvent hevent = (ChestShopClickEvent) event;
			handleClick(hevent);
		} else if (event instanceof ChestShopOpenEvent) {
			ChestShopOpenEvent hevent = (ChestShopOpenEvent) event;
			handleOpen(hevent);
		} else if (event instanceof ChestShopCloseEvent) {
			ChestShopCloseEvent hevent = (ChestShopCloseEvent) event;
			handleClose(hevent);
		} else if (event instanceof DataLoadEvent) {
			DataLoadEvent dle = (DataLoadEvent) event;
			if (dle.loadType == DataLoadType.COMPLETE) {
				loadChestShops();
			}
		}
	}
	


	
	
	private void handleBlockBreak(HBlockBreakEvent event) {
		HLocation loc = event.getBlock().getLocation();
		if (hc.getMC().isChestShopChest(loc)) {
			event.cancel();
		} else if (hc.getMC().isChestShopSign(loc)) {
			boolean delete = false;
			if (event.getPlayer().hasPermission("hyperconomy.admin") && event.getPlayer().isSneaking()) delete = true;
			ChestShop cs = chestShops.get(loc);
			if (cs != null && cs.getOwner().equals(event.getPlayer()) && event.getPlayer().isSneaking()) delete = true;
			if (delete) {
				chestShops.remove(loc);
				if (cs != null) cs.delete();
				return;
			}
			event.cancel();
		} else if (hc.getMC().isChestShopSignBlock(loc)) {
			event.cancel();
		}
	}
	
	private void handleSignChange(HSignChangeEvent event) {
		try {
			HSign sign = event.getSign();
			HyperPlayer hp = event.getHyperPlayer();
			String line1 = hc.getMC().removeColor(sign.getLine(0)).trim();
			if (!line1.equalsIgnoreCase("ChestShop")) return;
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
			if (isDoubleChest(cLoc)) {
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
			HInventory inv = hc.getMC().getChestInventory(cLoc);
			int count = 0;
			boolean empty = true;
			while (count < inv.getSize()) {
				if (!inv.getItem(count).isBlank()) empty = false;
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
			
			if (!hc.getMC().canHoldChestShopSign(sign.getAttachedBlock().getLocation())) {
				sign.setLine(0, "&4You can't");
				sign.setLine(1, "&4attach your");
				sign.setLine(2, "&4sign to that");
				sign.setLine(3, "&4block!");
				sign.update();
				return;
			}

			ChestShop newShop = new ChestShop(hc, cLoc, (HyperAccount)hp, 100);
			newShop.initialize();
			chestShops.put(cLoc, newShop);
			newShop.deleteFromDatabase();
			newShop.save();
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}


	public boolean isDoubleChest(HLocation location) {
		if (!hc.getMC().isChest(location)) return false;
		HBlock cBlock = new HBlock(hc, location);
		HBlock[] surrounding = cBlock.getNorthSouthEastWestBlocks();
		for (HBlock b:surrounding) {
			if (hc.getMC().isChest(b.getLocation())) return true;
		}
		return false;
	}

	

}
