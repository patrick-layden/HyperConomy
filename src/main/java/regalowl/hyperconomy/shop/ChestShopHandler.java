package regalowl.hyperconomy.shop;




import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;

import java.util.ArrayList;
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
import regalowl.hyperconomy.event.minecraft.HPlayerInteractEvent;
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
	private ConcurrentHashMap<HLocation, ChestShop> chestShops = new ConcurrentHashMap<HLocation, ChestShop>();
	private int maxChestShopsPerPlayer;
	private boolean limitChestShops;

	public ChestShopHandler(HyperConomy hc) {
		this.hc = hc;
		em = hc.getDataManager();
		L = hc.getLanguageFile();
		if (hc.getConf().getBoolean("enable-feature.chest-shops")) {
			hc.getHyperEventHandler().registerListener(this);
			maxChestShopsPerPlayer = hc.getConf().getInt("chest-shop.max-per-player");
			limitChestShops = hc.getConf().getBoolean("chest-shop.limit-chest-shops");
		}		
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
					dbData = sr.select("SELECT * FROM hyperconomy_chest_shop_items csi INNER JOIN hyperconomy_object_data od ON csi.DATA_ID = od.ID");
					while (dbData.next()) {
						String chestId = dbData.getString("CHEST_ID");
						int dataId = dbData.getInt("DATA_ID");
						String data = dbData.getString("DATA");
						double buyPrice = dbData.getDouble("BUY_PRICE");
						double sellPrice = dbData.getDouble("SELL_PRICE");
						ChestShopType type = ChestShopType.fromString(dbData.getString("TYPE"));
						HLocation l = HLocation.fromBlockString(chestId);
						ChestShop cs = chestShops.get(l);
						if (cs == null) continue;
						cs.setCustomPriceItem(chestId, dataId, data, buyPrice, sellPrice, type);
					}
					dbData.close();
					dbData = null;	
					hc.getDebugMode().ayncDebugConsoleMessage("Chest shops loaded.");
					hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.CHEST_SHOPS));
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
		if (!cs.isValid()) {
			deleteChestShop(cs);
			return null;
		}
		return cs;
	}
	
	public ChestShop getChestShopFromAnyPart(HLocation loc) {
		ChestShop returnShop = null;
		for (ChestShop cs:chestShops.values()) {
			if (cs.isPartOfChestShop(loc)) {
				returnShop = cs;
				break;
			}
		}
		if (returnShop != null) {
			if (!returnShop.isInitialized()) returnShop.initialize();
			if (!returnShop.isValid()) {
				deleteChestShop(returnShop);
				return null;
			}
		}
		return returnShop;
	}
	
	private void handleClick(ChestShopClickEvent event) {
		
		//try {
			
			//event.cancel();
		
			HyperPlayer clicker = event.getClicker();
			if (hc.getHyperLock().isLocked(clicker)) {
				hc.getHyperLock().sendLockMessage(clicker);
				return;
			}
			
			//HANDLE OWNER CLICK
			ChestShop cs = event.getChestShop();
			HyperAccount owner = cs.getOwner();
			if (clicker.getName().equals(owner.getName())) {
				handleOwnerClick(event);
				return;
			}
		
			
			
			
			
			
			
			
			
			HItemStack clickedItem = event.getInvItem();
			HItemStack cursorItem = event.getCursorItem();
			HInventory shopInventory = cs.getInventory();
			HInventory playerInventory = clicker.getInventory();
			int slot = event.getClickedSlot();
			String action = event.getAction();
			boolean isChestInventory = false;
			boolean isPlayerInventory = false;
			if (slot < 0) {
				event.cancel();
				return;
			}
			if (slot < 27) {
				isChestInventory = true;
				event.cancel();
			}
			if (slot >= 27) {
				isPlayerInventory = true;
			}

			/*
			if (isPlayerInventory) {
				int translatedSlot = slot - 36;
				if (translatedSlot >= 27) {
					translatedSlot -= 27;
				} else {
					translatedSlot += 9;
				}
			}
			*/

			HyperEconomy chestOwnerEconomy = em.getDefaultEconomy();
			if (owner instanceof HyperPlayer) {
				HyperPlayer hPlayer = (HyperPlayer)owner;
				chestOwnerEconomy = hPlayer.getHyperEconomy();
			}

			if (isChestInventory) {
				if (action.equals("PICKUP_ALL") || action.equals("PICKUP_HALF")) {
					HItemStack tradeItem = shopInventory.getItem(event.getClickedSlot());
					TradeObject tradeObject = chestOwnerEconomy.getTradeObject(tradeItem);
					if (tradeObject == null) tradeObject = TempTradeItem.generate(hc, tradeItem);
					
					if (!cs.isBuyStack(tradeItem)) {
						clicker.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
						cs.setViewerMenuTitle(clicker, "You can't buy that.");
						cs.refreshMenu();
						return;
					}
					int space = clicker.getInventory().getAvailableSpace(tradeObject.getItem());
					if (space < 1) {
						clicker.sendMessage(L.f(L.get("ONLY_ROOM_TO_BUY"), space, tradeObject.getDisplayName()));
						cs.setViewerMenuTitle(clicker, "Your inventory is too full.");
						cs.refreshMenu();
						return;
					}
					
					
					PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_CUSTOM);
					pt.setHyperObject(tradeObject);
					pt.setTradePartner(owner);
					pt.setAmount(1);
					pt.setGiveInventory(shopInventory);
					pt.setMoney(cs.getBuyPrice(tradeItem));
					pt.setSetPrice(true);
					TransactionResponse response = clicker.processTransaction(pt);
					response.sendMessages();
					if (response.successful()) {
						shopInventory.remove(1, tradeItem);
						playerInventory.add(1, tradeItem);
						cs.setViewerMenuTitle(clicker, "Bought for: " + hc.getLanguageFile().gC(false) + CommonFunctions.twoDecimals(pt.getMoney()));
						cs.refreshMenu();
					}
				} else if (action.equals("MOVE_TO_OTHER_INVENTORY")) {
					HItemStack tradeItem = shopInventory.getItem(event.getClickedSlot());
					TradeObject tradeObject = chestOwnerEconomy.getTradeObject(tradeItem);
					if (tradeObject == null) tradeObject = TempTradeItem.generate(hc, tradeItem);
					
					
					if (!cs.isBuyStack(tradeItem)) {
						clicker.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
						cs.setViewerMenuTitle(clicker, "You can't buy that.");
						cs.refreshMenu();
						return;
					}
					int space = clicker.getInventory().getAvailableSpace(tradeObject.getItem());
					if (space < tradeItem.getAmount()) {
						clicker.sendMessage(L.f(L.get("ONLY_ROOM_TO_BUY"), space, tradeObject.getDisplayName()));
						cs.setViewerMenuTitle(clicker, "Your inventory is too full.");
						cs.refreshMenu();
						return;
					}
					
					PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_CUSTOM);
					pt.setHyperObject(tradeObject);
					pt.setTradePartner(owner);
					pt.setAmount(tradeItem.getAmount());
					pt.setGiveInventory(shopInventory);
					pt.setMoney(cs.getBuyPrice(tradeItem) * tradeItem.getAmount());
					pt.setSetPrice(true);
					TransactionResponse response = clicker.processTransaction(pt);
					response.sendMessages();
					if (response.successful()) {
						shopInventory.remove(tradeItem.getAmount(), tradeItem);
						playerInventory.add(tradeItem.getAmount(), tradeItem);
						cs.setViewerMenuTitle(clicker, "Bought for: " + hc.getLanguageFile().gC(false) + CommonFunctions.twoDecimals(pt.getMoney()));
						cs.refreshMenu();
					}
				} else if (action.equals("PLACE_ALL")) {
					HItemStack tradeItem = cursorItem;
					TradeObject tradeObject = chestOwnerEconomy.getTradeObject(tradeItem);
					if (tradeObject == null) tradeObject = TempTradeItem.generate(hc, tradeItem);
					
					if (!cs.isSellStack(tradeItem)) {
						clicker.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
						cs.setViewerMenuTitle(clicker, "You can't sell that.");
						clicker.setItemOnCursor(null);
						playerInventory.add(tradeItem.getAmount(), tradeItem);
						cs.refreshMenu();
						return;
					}
					if (clicker.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
						clicker.sendMessage(L.get("CANT_SELL_CREATIVE"));
						cs.setViewerMenuTitle(clicker, "You cannot be in creative.");
						clicker.setItemOnCursor(null);
						playerInventory.add(tradeItem.getAmount(), tradeItem);
						cs.refreshMenu();
						return;
					}
					int itemamount = shopInventory.count(tradeObject.getItem());
					if (itemamount <= 0) {
						clicker.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
						cs.setViewerMenuTitle(clicker, "You can't sell that.");
						clicker.setItemOnCursor(null);
						playerInventory.add(tradeItem.getAmount(), tradeItem);
						cs.refreshMenu();
						return;
					}

					
					int space = shopInventory.getAvailableSpace(tradeObject.getItem());
					if (space < tradeItem.getAmount()) {
						clicker.sendMessage(L.get("CHEST_SHOP_NOT_ENOUGH_SPACE"));
						cs.setViewerMenuTitle(clicker, "Not enough space.");
						clicker.setItemOnCursor(null);
						playerInventory.add(tradeItem.getAmount(), tradeItem);
						cs.refreshMenu();
						return;
					}
					
					
								
					double bal = owner.getBalance();
					double cost = tradeObject.getSellPrice(tradeItem.getAmount());
					if (bal < cost) {
						clicker.sendMessage(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), owner.getName()));
						cs.setViewerMenuTitle(clicker, "Shop has insufficient funds.");
						clicker.setItemOnCursor(null);
						playerInventory.add(tradeItem.getAmount(), tradeItem);
						cs.refreshMenu();
						return;
					}
						
					PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_CUSTOM);
					pt.setHyperObject(tradeObject);
					pt.setTradePartner(owner);
					pt.setAmount(tradeItem.getAmount());
					pt.setReceiveInventory(shopInventory);
					pt.setMoney(cs.getSellPrice(tradeItem) * tradeItem.getAmount());
					pt.setSetPrice(true);
					TransactionResponse response = clicker.processTransaction(pt);
					response.sendMessages();
					if (response.successful()) {
						shopInventory.add(tradeItem.getAmount(), tradeItem);
						clicker.setItemOnCursor(null);
						cs.setViewerMenuTitle(clicker, "Sold to chest for: " + hc.getLanguageFile().gC(false) + CommonFunctions.twoDecimals(pt.getMoney()));
						cs.refreshMenu();
					}

				}
				
			} else if (isPlayerInventory) {

				if (action.equals("MOVE_TO_OTHER_INVENTORY")) {
					HItemStack tradeItem = clickedItem;
					TradeObject tradeObject = chestOwnerEconomy.getTradeObject(tradeItem);
					if (tradeObject == null) tradeObject = TempTradeItem.generate(hc, tradeItem);

					
					if (!cs.isSellStack(tradeItem)) {
						clicker.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
						cs.setViewerMenuTitle(clicker, "You can't sell that.");
						cs.refreshMenu();
						event.cancel();
						return;
					}
					if (clicker.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
						clicker.sendMessage(L.get("CANT_SELL_CREATIVE"));
						cs.setViewerMenuTitle(clicker, "You cannot be in creative.");
						cs.refreshMenu();
						event.cancel();
						return;
					}
					int itemamount = shopInventory.count(tradeObject.getItem());
					if (itemamount <= 0) {
						clicker.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
						cs.setViewerMenuTitle(clicker, "You can't sell that.");
						cs.refreshMenu();
						event.cancel();
						return;
					}

					
					int space = shopInventory.getAvailableSpace(tradeObject.getItem());
					if (space < tradeItem.getAmount()) {
						clicker.sendMessage(L.get("CHEST_SHOP_NOT_ENOUGH_SPACE"));
						cs.setViewerMenuTitle(clicker, "Not enough space.");
						cs.refreshMenu();
						event.cancel();
						return;
					}
					
					
								
					double bal = owner.getBalance();
					double cost = tradeObject.getSellPrice(tradeItem.getAmount());
					if (bal < cost) {
						clicker.sendMessage(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), owner.getName()));
						cs.setViewerMenuTitle(clicker, "Shop has insufficient funds.");
						cs.refreshMenu();
						event.cancel();
						return;
					}
						
					PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_CUSTOM);
					pt.setHyperObject(tradeObject);
					pt.setTradePartner(owner);
					pt.setAmount(tradeItem.getAmount());
					pt.setReceiveInventory(shopInventory);
					pt.setMoney(cs.getSellPrice(tradeItem) * tradeItem.getAmount());
					pt.setSetPrice(true);
					TransactionResponse response = clicker.processTransaction(pt);
					response.sendMessages();
					if (response.successful()) {
						
						playerInventory.remove(tradeItem.getAmount(), tradeItem);
						shopInventory.add(tradeItem.getAmount(), tradeItem);
						cs.setViewerMenuTitle(clicker, "Sold to chest for: " + hc.getLanguageFile().gC(false) + CommonFunctions.twoDecimals(pt.getMoney()));
						cs.refreshMenu();
					}

					event.cancel();
					cs.refreshMenu();
				} else if (action.equals("PICKUP_ALL") || action.equals("PICKUP_HALF")) {
					//allow
				} else if (action.equals("PLACE_ALL") || action.equals("PLACE_ONE")) {
					//allow
				} else {
					event.cancel();
				}
			
			}
		
			
	}
	
	private void handleOwnerClick(ChestShopClickEvent event) {
		
		HItemStack clickedItem = event.getInvItem();
		HItemStack cursorItem = event.getCursorItem();
		HyperPlayer clicker = event.getClicker();
		ChestShop cs = event.getChestShop();
		HInventory shopInventory = cs.getInventory();
		HInventory playerInventory = clicker.getInventory();
		int slot = event.getClickedSlot();
		String action = event.getAction();
		boolean isChestInventory = false;
		boolean isControlButton = false;
		boolean isPlayerInventory = false;
		if (slot < 0) {
			event.cancel();
			return;
		}
		if (slot < 27) {
			isChestInventory = true;
			clickedItem = cs.getInventory().getItem(slot);
			event.cancel();
		}
		if (slot >= 27 && slot < 36) {
			isControlButton = true;
			event.cancel();
		}
		if (slot >= 36) isPlayerInventory = true;

		if (isChestInventory) {
			if (cs.inSelectItemMode()) {
				cs.setEditStack(clickedItem);
				cs.setEditMode(true, false);
				cs.setSelectItemMode(false);
				cs.refreshMenu();
			} else if (cs.inEditItemMode() || cs.inPreviewMode() || cs.inDeleteMode()) {
				
			} else {
				if (action.equals("PICKUP_ALL") || action.equals("PICKUP_HALF")) {
					int quantity = (event.isRightClick()) ? clickedItem.getAmount() : 1;
					shopInventory.remove(quantity, clickedItem);
					playerInventory.add(quantity, clickedItem);
					cs.refreshMenu();
				} else if (action.equals("MOVE_TO_OTHER_INVENTORY")) {
					shopInventory.remove(clickedItem.getAmount(), clickedItem);
					playerInventory.add(clickedItem.getAmount(), clickedItem);
					cs.refreshMenu();
				} else if (action.equals("PLACE_ALL")) {
					shopInventory.add(cursorItem.getAmount(), cursorItem);
					clicker.setItemOnCursor(null);
					cs.refreshMenu();
				}
			}
		} else if (isPlayerInventory) {
			if (cs.inSelectItemMode() || cs.inEditItemMode() || cs.inPreviewMode() || cs.inDeleteMode()) {
				event.cancel();
			} else {
				if (action.equals("MOVE_TO_OTHER_INVENTORY")) {
					playerInventory.remove(clickedItem.getAmount(), clickedItem);
					shopInventory.add(clickedItem.getAmount(), clickedItem);
					event.cancel();
					cs.refreshMenu();
				} else if (action.equals("PICKUP_ALL") || action.equals("PICKUP_HALF")) {
					//allow
				} else if (action.equals("PLACE_ALL") || action.equals("PLACE_ONE")) {
					//allow
				} else {
					event.cancel();
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
							cs.toggleType(cs.getEditStack());
						}
					} else if (cs.inSelectItemMode()) {
						cs.setEditMode(true, true);
						cs.setSelectItemMode(false);
						cs.setEditStack(cs.getFirstItem());
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
						HItemStack firstItem = cs.getFirstItem();
						if (firstItem == null) return;
						if (cs.editingBuyPrice()) {
							cs.setBuyPriceAll(cs.getBuyPrice(firstItem) - cs.getFractionalPriceIncrement());
						} 
						if (cs.editingSellPrice()) {
							cs.setSellPriceAll(cs.getSellPrice(firstItem) - cs.getFractionalPriceIncrement());
						}
					} else {
						if (cs.editingBuyPrice()) {
							cs.setBuyPrice(cs.getEditStack(), cs.getBuyPrice(cs.getEditStack()) - cs.getFractionalPriceIncrement());
						} 
						if (cs.editingSellPrice()) {
							cs.setSellPrice(cs.getEditStack(), cs.getSellPrice(cs.getEditStack()) - cs.getFractionalPriceIncrement());
						}
					}

					break;
				case 29:
					if (!cs.inEditItemMode()) return;
					if (cs.editAllItems()) {
						HItemStack firstItem = cs.getFirstItem();
						if (firstItem == null) return;
						if (cs.editingBuyPrice()) {
							cs.setBuyPriceAll(cs.getBuyPrice(firstItem) + cs.getFractionalPriceIncrement());
						} 
						if (cs.editingSellPrice()) {
							cs.setSellPriceAll(cs.getSellPrice(firstItem) + cs.getFractionalPriceIncrement());
						}
					} else {
						if (cs.editingBuyPrice()) {
							cs.setBuyPrice(cs.getEditStack(), cs.getBuyPrice(cs.getEditStack()) + cs.getFractionalPriceIncrement());
						} 
						if (cs.editingSellPrice()) {
							cs.setSellPrice(cs.getEditStack(), cs.getSellPrice(cs.getEditStack()) + cs.getFractionalPriceIncrement());
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
			cs.refreshMenu();
		}		
	}
	
	

	
	private void handleOpen(ChestShopOpenEvent event) {
		event.cancel();
		ChestShop cs = event.getChestShop();
		if (cs == null) return;
		HyperPlayer opener = event.getOpener();
		if (!cs.hasViewers()) {//shop not opened by anyone
			cs.addViewer(opener);
			if (opener.equals(cs.getOwner())) {
				cs.setOpenedByOwner(true);//if the viewer is the owner, set the flag
				hc.getMC().openInventory(cs.getOwnerShopMenu(), opener, cs.getMenuName(opener));//shop opened by owner
			} else {
				hc.getMC().openInventory(cs.getShopMenu(), opener, cs.getMenuName(opener));//shop opened by shopper
			}
		} else { //shop is already open by someone
			if (opener.equals(cs.getOwner())) return; //shop already opened by a shopper, owner can't open
			if (cs.openedByOwner()) return; //shop already opened by owner, player can't shop	
			cs.addViewer(opener);//add the new viewer if not the owner
			hc.getMC().openInventory(cs.getShopMenu(), opener, cs.getMenuName(opener));//chest shop opened by second player
		}
	}
	

	private void handleClose(ChestShopCloseEvent event) {
		ChestShop cs = event.getChestShop();
		cs.removeViewer(event.getCloser());//remove closing viewer
		if (cs.openedByOwner() && event.getCloser().equals(cs.getOwner())) {
			cs.setOpenedByOwner(false);
			cs.setEditMode(false, false);
			cs.setSelectItemMode(false);
		} 
	}

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
		ChestShop returnShop = null;
		for (ChestShop cs:chestShops.values()) {
			if (cs.hasViewer(hp)) {
				returnShop = cs;
				break;
			}
		}
		if (returnShop != null) {
			if (!returnShop.isInitialized()) returnShop.initialize();
			if (!returnShop.isValid()) {
				deleteChestShop(returnShop);
				return null;
			}
		}
		return returnShop;
	}
	
	public ArrayList<ChestShop> getChestShops(HyperPlayer hp) {
		ArrayList<ChestShop> playerChestShops = new ArrayList<ChestShop>();
		if (!hc.enabled()) return playerChestShops;
		for (ChestShop cs:chestShops.values()) {
			if (cs.getOwner().equals(hp)) {
				playerChestShops.add(cs);
			}
		}
		return playerChestShops;
	}
	
	
	
	public void deleteChestShop(ChestShop cs) {
		cs.delete();				
		chestShops.remove(cs.getChestLocation());
	}

	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof DataLoadEvent) {
			DataLoadEvent dle = (DataLoadEvent) event;
			if (dle.loadType == DataLoadType.SHOP) {
				loadChestShops();
			}
		}
		if (!hc.loaded()) {
			if (event instanceof HPlayerInteractEvent) {
				HPlayerInteractEvent hpie = (HPlayerInteractEvent)event;
				HLocation l = hpie.getBlock().getLocation();
				if (hc.getMC().isPartOfChestShop(l) ) {
					event.cancel();
				}
			} else if (event instanceof HBlockBreakEvent) {
				HBlockBreakEvent hevent = (HBlockBreakEvent) event;
				if (hc.getMC().isPartOfChestShop(hevent.getBlock().getLocation())) {
					hevent.cancel();
				}
			} else if (event instanceof HEntityExplodeEvent) {
				HEntityExplodeEvent hevent = (HEntityExplodeEvent)event;
				for (HBlock b : hevent.getBrokenBlocks()) {
					if (hc.getMC().isPartOfChestShop(b.getLocation())) {
						hevent.cancel();
					}
				}
			} else if (event instanceof HBlockPistonExtendEvent) {
				HBlockPistonExtendEvent hevent = (HBlockPistonExtendEvent)event;
				for (HBlock b : hevent.getBlocks()) {
					if (hc.getMC().isPartOfChestShop(b.getLocation())) {
						hevent.cancel();
					}
				}
			} else if (event instanceof HBlockPistonRetractEvent) {
				HBlockPistonRetractEvent hevent = (HBlockPistonRetractEvent)event;
				if (hc.getMC().isPartOfChestShop(hevent.getRetractedBlock().getLocation())) {
					hevent.cancel();
				}
			} else if (event instanceof HBlockPlaceEvent) {
				HBlockPlaceEvent hevent = (HBlockPlaceEvent)event;
				HBlock block = hevent.getBlock();
				for (HBlock b : block.getSurroundingBlocks()) {
					if (hc.getMC().isPartOfChestShop(b.getLocation())) {
						hevent.cancel();
					}
				}
			} else if (event instanceof ChestShopOpenEvent) {
				event.cancel();
			}
			return;
		}
		
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
		}
	}
	


	
	
	private void handleBlockBreak(HBlockBreakEvent event) {
		HLocation loc = event.getBlock().getLocation();
		if (hc.getMC().isChestShopChest(loc)) {
			event.cancel();
		} else if (hc.getMC().isChestShopSign(loc)) {
			HLocation chestLoc = loc.down();
			boolean delete = false;
			if (event.getPlayer().hasPermission("hyperconomy.admin") && event.getPlayer().isSneaking()) delete = true;
			ChestShop cs = chestShops.get(chestLoc);
			if (cs != null && cs.getOwner().equals(event.getPlayer()) && event.getPlayer().isSneaking()) delete = true;
			if (cs == null) delete = true;
			if (delete) {
				chestShops.remove(chestLoc);
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
			String line2 = hc.getMC().removeColor(sign.getLine(1)).trim();
			if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
				sign.setLine(0, "&4Write");
				sign.setLine(1, "&4ChestShop");
				sign.setLine(2, "&4on the first");
				sign.setLine(3, "&4line.");
				sign.update();
				return;
			}
			String line1 = hc.getMC().removeColor(sign.getLine(0)).trim().replace(" ", "");
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
			
			if (limitChestShops && getChestShops(hp).size() >= maxChestShopsPerPlayer) {
				sign.setLine(0, "&4You have");
				sign.setLine(1, "&4too many");
				sign.setLine(2, "&4chest shops!");
				sign.setLine(3, "");
				sign.update();
				return;
			}

			ChestShop newShop = new ChestShop(hc, cLoc, (HyperAccount)hp, 100);
			newShop.initialize();
			if (!newShop.isValid()) return;
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
