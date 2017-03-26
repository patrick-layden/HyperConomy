package regalowl.hyperconomy.shop;


import regalowl.simpledatalib.CommonFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.concurrent.atomic.AtomicBoolean;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HInventoryType;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.tradeobject.TempTradeItem;
import regalowl.hyperconomy.tradeobject.TradeObject;


public class ChestShop {

	private transient HyperConomy hc;
	private transient ArrayList<HyperPlayer> viewers = new ArrayList<HyperPlayer>();
	//private transient HInventory shopMenuInventory;
	private transient boolean openedByOwner;
	private transient AtomicBoolean updateMenuLock = new AtomicBoolean();
	private transient HashMap<Integer, CustomPriceChestShopItem> customPriceItems = new HashMap<Integer, CustomPriceChestShopItem>();
	private transient int editSlot = -1;
	private transient boolean inEditItemMode = false;
	private transient boolean editAllItems = false;
	private transient boolean editingBuyPrice = true;
	private transient boolean editingSellPrice = true;
	private transient boolean inSelectItemMode = false;
	private transient boolean inPreviewMode = false;
	private transient boolean inDeleteMode = false;
	private transient boolean deleted = false;
	
	private HLocation location;
	private HyperAccount owner;
	
	private boolean isInitialized;
	private HSign sign;
	private HBlock attachedBlock;
	private HInventory inventory;
	private long priceIncrement = 100;


	private boolean isValidChestShop;

	public ChestShop(HyperConomy hc, HLocation location, HyperAccount owner, long priceIncrement) {
		this.hc = hc;
		this.location = location;
		this.owner = owner;
		this.priceIncrement = priceIncrement;
		
		this.isValidChestShop = false;
		this.openedByOwner = false;
		this.isInitialized = false;
	}
	
	
	
	
	public class CustomPriceChestShopItem {
		String chestId;
		int slot;
		String dataString;
		double buyPrice;
		double sellPrice;
		ChestShopType type;
		
		CustomPriceChestShopItem(String chestId, int slot, String dataString, double buyPrice, double sellPrice, ChestShopType type) {
			this.chestId = chestId;
			this.slot = slot;
			this.dataString = dataString;
			this.buyPrice = buyPrice;
			this.sellPrice = sellPrice;
			this.type = type;
		}
	}
	
	public CustomPriceChestShopItem setCustomPriceItem(String chestId, int slot, String dataString, double buyPrice, double sellPrice, ChestShopType type) {
		CustomPriceChestShopItem i = new CustomPriceChestShopItem(chestId, slot, dataString, buyPrice, sellPrice, type);
		customPriceItems.put(slot, i);
		return i;
	}
	
	
	public void saveCustomChestShopItem(int slot) {
		CustomPriceChestShopItem i = customPriceItems.get(slot);
		if (i == null) return;
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("CHEST_ID", i.chestId);
		conditions.put("SLOT", i.slot+"");
		hc.getSQLWrite().performDelete("hyperconomy_chest_shop_items", conditions);
		
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("CHEST_ID", i.chestId);
		values.put("SLOT", i.slot+"");
		values.put("DATA", i.dataString);
		values.put("BUY_PRICE", i.buyPrice+"");
		values.put("SELL_PRICE", i.sellPrice+"");
		values.put("TYPE", i.type.name());
		hc.getSQLWrite().performInsert("hyperconomy_chest_shop_items", values);
	}
	
	/*
	public ChestShop(HyperConomy hc, HBlock b) {
		this.hc = hc;
		this.location = b.getLocation();
		this.isInitialized = false;
		this.isValidChestShop = false;
		initialize();
	}
	
	public ChestShop(HyperConomy hc, HLocation location) {
		this.hc = hc;
		this.location = location;
		this.isInitialized = false;
		this.isValidChestShop = false;
		initialize();
	}
	*/
	public void initialize() {
		if (isInitialized) return;
		isInitialized = true;
		if (location == null || owner == null || hc == null) return;
		HLocation signLocation = new HLocation(location);
		signLocation.setY(location.getY() + 1);		
		this.sign = hc.getMC().getSign(signLocation);
		if (sign == null) return;
		attachedBlock = sign.getAttachedBlock();
		if (attachedBlock == null) return;
		inventory = hc.getMC().getChestInventory(location);
		if (inventory == null) return;
		isValidChestShop = true;
		fixSign();
	}
	
	public void fixSign() {
		if (!isValidChestShop) return;
		sign.setLine(0, "&b" + hc.getMC().applyColor("ChestShop"));
		sign.setLine(1, hc.getMC().applyColor(sign.getLine(1)));
		sign.setLine(2, hc.getMC().applyColor(sign.getLine(2)));
		sign.setLine(3, hc.getMC().applyColor(sign.getLine(3)));
		sign.update();
	}
	
	public void save() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("ID", getId());
		values.put("WORLD", location.getWorld());
		values.put("X", location.getBlockX()+"");
		values.put("Y", location.getBlockY()+"");
		values.put("Z", location.getBlockZ()+"");
		values.put("OWNER", owner.getName());
		values.put("PRICE_INCREMENT", priceIncrement+"");
		hc.getSQLWrite().performInsert("hyperconomy_chest_shops", values);
	}
	
	public void delete() {
		deleted = true;
		HInventory emptyInv = generateEmptyInventory();
		Iterator<HyperPlayer> it = viewers.iterator();
		while (it.hasNext()) {
			HyperPlayer hp = it.next();
			hc.getMC().openInventory(emptyInv, hp, "Deleted");
			hc.getMC().closeActiveInventory(hp);
		}

		sign.setLine(0, hc.getMC().applyColor("&cDeleted"));
		sign.update();
		deleteFromDatabase();
	}
	

	
	public void deleteFromDatabase() {
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("ID", getId());
		hc.getSQLWrite().performDelete("hyperconomy_chest_shops", conditions);
		conditions = new HashMap<String,String>();
		conditions.put("CHEST_ID", getId());
		hc.getSQLWrite().performDelete("hyperconomy_chest_shop_items", conditions);
	}
	
	public String getId() {
		return location.toBlockString();
	}

	public HLocation getChestLocation() {
		return location;
	}
	public HSign getSign() {
		return sign;
	}
	public HyperAccount getOwner() {
		return owner;
	}
	public void setOwner(HyperAccount account) {
		this.owner = account;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("ID", getId());
		values.put("OWNER", owner.getName());
		hc.getSQLWrite().performUpdate("hyperconomy_chest_shops", values, conditions);
		sign.setLine(2, "&f" + owner.getName());
		sign.update();
	}
	public long getPriceIncrement() {
		return priceIncrement;
	}
	public double getFractionalPriceIncrement() {
		double p = (double)priceIncrement;
		return p/100.0;
	}
	public void setPriceIncrement(long priceIncrement) {
		if (priceIncrement < 1) priceIncrement = 1;
		if (priceIncrement > 1000000000000L) priceIncrement = 1000000000000L;
		this.priceIncrement = priceIncrement;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("ID", getId());
		values.put("PRICE_INCREMENT", this.priceIncrement+"");
		hc.getSQLWrite().performUpdate("hyperconomy_chest_shops", values, conditions);
	}
	public HInventory getInventory() {
		return inventory;
	}
	
	
	
	public ChestShopType getType(int slot) {
		CustomPriceChestShopItem i = customPriceItems.get(slot);
		if (i == null) return ChestShopType.TRADE;
		return i.type;
	}
	public void setType(ChestShopType type, int slot) {
		if (!isInitialized) return;
		CustomPriceChestShopItem i = initializeCustomPriceItem(slot);
		i.type = type;
		saveCustomChestShopItem(slot);
	}
	public boolean isBuySlot(int slot) {
		ChestShopType type = getType(slot);
		if (type == ChestShopType.TRADE || type == ChestShopType.BUY) return true;
		return false;
	}
	public boolean isSellSlot(int slot) {
		ChestShopType type = getType(slot);
		if (type == ChestShopType.TRADE || type == ChestShopType.SELL) return true;
		return false;
	}
	
	
	public void setBuyPrice(int slot, double price) {
		if (!isInitialized) return;
		CustomPriceChestShopItem i = initializeCustomPriceItem(slot);
		i.buyPrice = price;
		saveCustomChestShopItem(slot);
	}
	public void setBuyPriceAll(double price) {
		for (int i = 0; i < inventory.getSize(); i++) {
			if (inventory.getItem(i).isBlank()) continue;
			setBuyPrice(i, price);
		}
	}
	public double getBuyPrice(int slot) {
		CustomPriceChestShopItem i = customPriceItems.get(slot);
		if (i == null) {
			TradeObject to = getTradeObject(slot);
			if (to == null) to = TempTradeItem.generate(hc, inventory.getItem(slot));
			return to.getBuyPrice(1);
		} else {
			return i.buyPrice;
		}
	}
	public void setSellPrice(int slot, double price) {
		if (!isInitialized) return;
		CustomPriceChestShopItem i = initializeCustomPriceItem(slot);
		i.sellPrice = price;
		saveCustomChestShopItem(slot);
	}
	public void setSellPriceAll(double price) {
		for (int i = 0; i < inventory.getSize(); i++) {
			if (inventory.getItem(i).isBlank()) continue;
			setSellPrice(i, price);
		}
	}
	public double getSellPrice(int slot) {
		CustomPriceChestShopItem i = customPriceItems.get(slot);
		if (i == null) {
			TradeObject to = getTradeObject(slot);
			if (to == null) to = TempTradeItem.generate(hc, inventory.getItem(slot));
			return to.getSellPrice(1);
		} else {
			return i.sellPrice;
		}
	}
	
	public CustomPriceChestShopItem initializeCustomPriceItem(int slot) {
		if (!isInitialized) return null;
		CustomPriceChestShopItem i = customPriceItems.get(slot);
		if (i == null) {
			HItemStack stack = inventory.getItem(slot);
			if (stack == null || stack.isBlank()) return null;
			TradeObject to = getTradeObject(slot);
			if (to == null) to = TempTradeItem.generate(hc, stack);
			return setCustomPriceItem(getId(), slot, stack.serialize(), to.getBuyPrice(1), to.getSellPrice(1), ChestShopType.TRADE);
		} else {
			return i;
		}
	}
	
	
	public void toggleType(int slot) {
		ChestShopType type = getType(slot);
		if (type == ChestShopType.BUY) {
			setType(ChestShopType.TRADE, slot);
		} else if (type == ChestShopType.SELL) {
			setType(ChestShopType.BUY, slot);
		} else if (type == ChestShopType.TRADE) {
			setType(ChestShopType.SELL, slot);
		}
	}
	public void toggleTypeAll() {
		ChestShopType type = getType(getSlotOfFirstItem());
		ChestShopType newType = ChestShopType.TRADE;
		if (type == ChestShopType.BUY) {
			newType = ChestShopType.TRADE;
		} else if (type == ChestShopType.SELL) {
			newType = ChestShopType.BUY;
		} else if (type == ChestShopType.TRADE) {
			newType = ChestShopType.SELL;
		}
		for (int i = 0; i < inventory.getSize(); i++) {
			if (inventory.getItem(i).isBlank()) continue;
			setType(newType, i);
		}
	}


	public boolean isValid() {
		return isValidChestShop;
	}
	public boolean isInitialized() {
		return isInitialized;
	}

	
	

	
	
	
	/*
	public boolean isBuyChest() {
		if (type == ChestShopType.TRADE || type == ChestShopType.BUY) return true;
		return false;
	}
	public boolean isSellChest() {
		if (type == ChestShopType.TRADE || type == ChestShopType.SELL) return true;
		return false;
	}
	*/
	
	public TradeObject getTradeObject(int slot) {
		HItemStack i = inventory.getItem(slot);
		if (i == null || i.isBlank()) return null;
		TradeObject to = null;
		if (owner instanceof HyperPlayer) {
			HyperPlayer hp = (HyperPlayer)owner;
			to = hp.getHyperEconomy().getTradeObject(i);
		} else {
			to = hc.getDataManager().getDefaultEconomy().getTradeObject(inventory.getItem(slot));
		}
		if (to == null) to = TempTradeItem.generate(hc, i);
		return to;
	}
	
	
	public HInventory getShopMenu() {
		//Bukkit.broadcastMessage(isInitialized + "|" + isValidChestShop);
		HInventory shopMenuInventory = new HInventory(inventory);
		for (int i = 0; i < shopMenuInventory.getSize(); i++) {
			HItemStack his = shopMenuInventory.getItem(i);
			if (his.isBlank()) continue;
			List<String> originalLore = his.getItemMeta().getLore();
			his.getItemMeta().setLore(new ArrayList<String>());
			if (isBuySlot(i)) {
				his.getItemMeta().addLore(hc.getMC().applyColor("&9Buy 1 from chest: " + "&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getBuyPrice(i)))));
				his.getItemMeta().addLore(hc.getMC().applyColor("&e(Left Click)"));
			}
			if (isSellSlot(i)) {
				his.getItemMeta().addLore(hc.getMC().applyColor("&9Sell 1 to chest: " + "&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getSellPrice(i)))));
				his.getItemMeta().addLore(hc.getMC().applyColor("&e(Right Click)"));
			}
			if (originalLore.size() > 0) {
				his.getItemMeta().addLore(hc.getMC().applyColor("&5Item has lore: "));
				for (String entry:originalLore) {
					his.getItemMeta().addLore(entry);
				}
			}
			shopMenuInventory.updateLore(i);
			
		}
		return shopMenuInventory;
	}
	
	
	
	private HItemStack getBlockedSlot() {
		HItemStack blockedSlot = hc.getDataManager().getDefaultEconomy().getTradeObject("black_stained_glass_pane").getItem();
		blockedSlot.getItemMeta().setDisplayName(" ");
		return blockedSlot;
	}
	private HInventory generateBlockedInventory() {
		HInventory blankInv = new HInventory(hc, new ArrayList<HItemStack>(), HInventoryType.PLAYER);
		HItemStack blockedSlot = getBlockedSlot();
		for (int i = 0; i < 36; i++) {
			blankInv.addItem(blockedSlot);
		}
		return blankInv;
	}
	private HInventory generateEmptyInventory() {
		HInventory blankInv = new HInventory(hc, new ArrayList<HItemStack>(), HInventoryType.PLAYER);
		HItemStack emptySlot = new HItemStack(hc);
		for (int i = 0; i < 36; i++) {
			blankInv.addItem(emptySlot);
		}
		return blankInv;
	}
	
	public HInventory getOwnerShopMenu() {
		HInventory shopMenuInventory = null;
		HItemStack blockedSlot = getBlockedSlot();
		if (inDeleteMode) {
			shopMenuInventory = generateBlockedInventory();
			HItemStack bookStack = hc.getDataManager().getDefaultEconomy().getTradeObject("red_banner").getItem();
			bookStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eExit Delete Mode"));
			shopMenuInventory.setItem(35, bookStack, false);
			
			HItemStack deleteStack = hc.getDataManager().getDefaultEconomy().getTradeObject("barrier").getItem();
			deleteStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eConfirm Delete"));
			deleteStack.getItemMeta().addLore(hc.getMC().applyColor("&9All custom pricing will be lost."));
			deleteStack.getItemMeta().addLore(hc.getMC().applyColor("&9All of your items will remain."));
			deleteStack.getItemMeta().addLore(hc.getMC().applyColor("&9Click to confirm deletion."));
			shopMenuInventory.setItem(27, deleteStack, false);
		} else if (inPreviewMode) {
			shopMenuInventory = getShopMenu();
			for (int i = 0; i < 9; i++) {
				shopMenuInventory.addItem(blockedSlot);
			}
			HItemStack bookStack = hc.getDataManager().getDefaultEconomy().getTradeObject("red_banner").getItem();
			bookStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eExit Preview Mode"));
			shopMenuInventory.setItem(35, bookStack, false);
			
			HItemStack paperStack = hc.getDataManager().getDefaultEconomy().getTradeObject("paper").getItem();
			paperStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eNotice"));
			paperStack.getItemMeta().addLore(hc.getMC().applyColor("&9Other players will not see"));
			paperStack.getItemMeta().addLore(hc.getMC().applyColor("&9the bottom menu row with"));
			paperStack.getItemMeta().addLore(hc.getMC().applyColor("&9these control buttons."));
			shopMenuInventory.setItem(34, paperStack, false);
		} else if (inSelectItemMode) {
			shopMenuInventory = new HInventory(inventory);
			for (int i = 0; i < 9; i++) {
				shopMenuInventory.addItem(blockedSlot);
			}
			for (int i = 0; i < 27; i++) {
				HItemStack his = shopMenuInventory.getItem(i);
				if (his.isBlank()) continue;
				his.getItemMeta().setLore(new ArrayList<String>());
				his.getItemMeta().addLore(hc.getMC().applyColor("&eSelect Item"));
				his.getItemMeta().addLore(hc.getMC().applyColor("&9Click to select this item."));
			}
			HItemStack bookStack = hc.getDataManager().getDefaultEconomy().getTradeObject("red_banner").getItem();
			bookStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eExit"));
			bookStack.getItemMeta().addLore(hc.getMC().applyColor("&9Click on an item to select it,"));
			bookStack.getItemMeta().addLore(hc.getMC().applyColor("&9or click here to exit item"));
			bookStack.getItemMeta().addLore(hc.getMC().applyColor("&9selection."));
			shopMenuInventory.setItem(35, bookStack, false);
			HItemStack selectAllStack = hc.getDataManager().getDefaultEconomy().getTradeObject("redstoneblock").getItem();
			selectAllStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eSelect All"));
			selectAllStack.getItemMeta().addLore(hc.getMC().applyColor("&9Click here to select all"));
			selectAllStack.getItemMeta().addLore(hc.getMC().applyColor("&9items.  This allows you to"));
			selectAllStack.getItemMeta().addLore(hc.getMC().applyColor("&9change the settings of all items"));
			selectAllStack.getItemMeta().addLore(hc.getMC().applyColor("&9in the chest simultaneously."));
			shopMenuInventory.setItem(34, selectAllStack, false);
		} else if (inEditItemMode) {
			shopMenuInventory = new HInventory(inventory);
			for (int i = 0; i < 9; i++) {
				shopMenuInventory.addItem(blockedSlot);
			}
			if (editAllItems) editSlot = getSlotOfFirstItem();
			for (int i = 0; i < shopMenuInventory.getSize(); i++) {
				if (!(i == editSlot) || editAllItems) {
					shopMenuInventory.setItem(i, blockedSlot, false);
				} else {
					HItemStack his = shopMenuInventory.getItem(i);
					his.getItemMeta().setLore(new ArrayList<String>());
					his.getItemMeta().setDisplayName(hc.getMC().applyColor("&eEdit Mode"));
					his.getItemMeta().addLore(hc.getMC().applyColor("&9Edit this item's settings "));
					his.getItemMeta().addLore(hc.getMC().applyColor("&9using the bottom buttons."));
					his.getItemMeta().addLore(hc.getMC().applyColor(" "));
					if (isBuySlot(editSlot)) {
						his.getItemMeta().addLore(hc.getMC().applyColor("&9Buy From Chest Price: "));
						his.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getBuyPrice(editSlot)))));
					} 
					if (isSellSlot(editSlot)){
						his.getItemMeta().addLore(hc.getMC().applyColor("&9Sell To Chest Price: "));
						his.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getSellPrice(editSlot)))));
					}
				}
			} 
			
			
			
			HItemStack bookStack = hc.getDataManager().getDefaultEconomy().getTradeObject("red_banner").getItem();
			bookStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eExit Edit Mode"));
			shopMenuInventory.setItem(35, bookStack, false);
			
			HItemStack tradeMode = hc.getDataManager().getDefaultEconomy().getTradeObject("clock").getItem();
			tradeMode.getItemMeta().setDisplayName(hc.getMC().applyColor("&eToggle Trade Mode"));
			tradeMode.getItemMeta().addLore(hc.getMC().applyColor("&9Click here to toggle the trade mode."));
			tradeMode.getItemMeta().addLore(hc.getMC().applyColor("&9Currently this item can be:"));
			if (isBuySlot(editSlot) && isSellSlot(editSlot)) {
				tradeMode.getItemMeta().addLore(hc.getMC().applyColor("&bbought from the chest"));
				tradeMode.getItemMeta().addLore(hc.getMC().applyColor("&band sold to the chest"));
			} else if (isBuySlot(editSlot)) {
				tradeMode.getItemMeta().addLore(hc.getMC().applyColor("&bbought from the chest"));
			} else if (isSellSlot(editSlot)) {
				tradeMode.getItemMeta().addLore(hc.getMC().applyColor("&bsold to the chest"));
			}
			shopMenuInventory.setItem(34, tradeMode, false);
			
			
			HItemStack priceMode = hc.getDataManager().getDefaultEconomy().getTradeObject("compass").getItem();
			priceMode.getItemMeta().setDisplayName(hc.getMC().applyColor("&eToggle Price Edit Mode"));
			priceMode.getItemMeta().addLore(hc.getMC().applyColor("&9Click here to toggle the price edit mode."));
			priceMode.getItemMeta().addLore(hc.getMC().applyColor("&9You're curently editing the:"));
			if (editingBuyPrice) {
				priceMode.getItemMeta().addLore(hc.getMC().applyColor("&bbuy from chest price"));
			} 
			if (editingSellPrice) {
				priceMode.getItemMeta().addLore(hc.getMC().applyColor("&bsell to chest price"));
			}
			shopMenuInventory.setItem(33, priceMode, false);
			
			
			
			
			HItemStack decreaseIncrement = hc.getDataManager().getDefaultEconomy().getTradeObject("red_stained_glass_pane").getItem();
			decreaseIncrement.getItemMeta().setDisplayName(hc.getMC().applyColor("&eDecrease Price Change Increment"));
			decreaseIncrement.getItemMeta().addLore(hc.getMC().applyColor("&9Current Increment: " + "&a" + hc.getLanguageFile().fC(getFractionalPriceIncrement())));
			shopMenuInventory.setItem(32, decreaseIncrement, false);
			HItemStack increaseIncrement = hc.getDataManager().getDefaultEconomy().getTradeObject("lime_stained_glass_pane").getItem();
			increaseIncrement.getItemMeta().setDisplayName(hc.getMC().applyColor("&eIncrease Price Change Increment"));
			increaseIncrement.getItemMeta().addLore(hc.getMC().applyColor("&9Current Increment: " + "&a" + hc.getLanguageFile().fC(getFractionalPriceIncrement())));
			shopMenuInventory.setItem(31, increaseIncrement, false);
			
			
			
			
			HItemStack decreasePrice = hc.getDataManager().getDefaultEconomy().getTradeObject("red_stained_glass_pane").getItem();
			decreasePrice.getItemMeta().setDisplayName(hc.getMC().applyColor("&eDecrease Price"));
			if (isBuySlot(editSlot)) {
				decreasePrice.getItemMeta().addLore(hc.getMC().applyColor("&9Current Buy From Chest Price: "));
				decreasePrice.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getBuyPrice(editSlot)))));
			} 
			if (isSellSlot(editSlot)) {
				decreasePrice.getItemMeta().addLore(hc.getMC().applyColor("&9Current Sell To Chest Price: "));
				decreasePrice.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getSellPrice(editSlot)))));
			}
			shopMenuInventory.setItem(30, decreasePrice, false);
			HItemStack increasePrice = hc.getDataManager().getDefaultEconomy().getTradeObject("lime_stained_glass_pane").getItem();
			increasePrice.getItemMeta().setDisplayName(hc.getMC().applyColor("&eIncrease Price"));
			if (isBuySlot(editSlot)) {
				increasePrice.getItemMeta().addLore(hc.getMC().applyColor("&9Current Buy From Chest Price: "));
				increasePrice.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getBuyPrice(editSlot)))));
			} 
			if (isSellSlot(editSlot)) {
				increasePrice.getItemMeta().addLore(hc.getMC().applyColor("&9Current Sell To Chest Price: "));
				increasePrice.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getSellPrice(editSlot)))));
			}
			shopMenuInventory.setItem(29, increasePrice, false);
			
			
		
			

		} else { //default menu if not in a special mode
			shopMenuInventory = new HInventory(inventory);
			for (int i = 0; i < 9; i++) {
				shopMenuInventory.addItem(blockedSlot);
			}
			HItemStack bookStack = hc.getDataManager().getDefaultEconomy().getTradeObject("nether_star").getItem();
			bookStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eConfigure Items"));
			bookStack.getItemMeta().addLore(hc.getMC().applyColor("&9Click here and then click"));
			bookStack.getItemMeta().addLore(hc.getMC().applyColor("&9the item you wish to edit."));
			shopMenuInventory.setItem(35, bookStack, false);
			
			HItemStack previewStack = hc.getDataManager().getDefaultEconomy().getTradeObject("end_crystal").getItem();
			previewStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&ePreview Shop"));
			previewStack.getItemMeta().addLore(hc.getMC().applyColor("&9Click here to see what your shop"));
			previewStack.getItemMeta().addLore(hc.getMC().applyColor("&9will look like to other players."));
			shopMenuInventory.setItem(34, previewStack, false);
			
			HItemStack deleteStack = hc.getDataManager().getDefaultEconomy().getTradeObject("barrier").getItem();
			deleteStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eDelete Shop"));
			deleteStack.getItemMeta().addLore(hc.getMC().applyColor("&9Click here to convert your shop"));
			deleteStack.getItemMeta().addLore(hc.getMC().applyColor("&9back into a regular chest."));
			shopMenuInventory.setItem(27, deleteStack, false);
		}

		return shopMenuInventory;
	}
	
	
	
	public String getMenuName() {
		if (openedByOwner) {
			String menuName = "";
			if (inEditItemMode) {
				menuName = "Edit Item";
			} else if (inSelectItemMode) {
				menuName = "Select Item";
			} else if (inPreviewMode) {
				menuName = "Shop Preview";
			} else if (inDeleteMode) {
				menuName = "Delete Shop?";
			} else {
				menuName = "Add or Remove Items";
			}
			
			return menuName;
		} else {
			String menuName = owner.getName() + "'s Chest Shop";
			return menuName;
		}

	}

	
	public void refreshMenu() {
		if (deleted) return;
		setUpdateMenuLock(true);
		Iterator<HyperPlayer> it = viewers.iterator();
		while (it.hasNext()) {
			HyperPlayer viewer = it.next();
			if (viewer.equals(owner)) {
				hc.getMC().openInventory(getOwnerShopMenu(), viewer, getMenuName());
			} else {
				hc.getMC().openInventory(getShopMenu(), viewer, getMenuName());
			}
		}
		setUpdateMenuLock(false);
	}
	
	
	public void addViewer(HyperPlayer hp) {
		if (deleted) return;
		if (this.viewers == null) viewers = new ArrayList<HyperPlayer>();
		if (!viewers.contains(hp)) viewers.add(hp);
	}
	public void removeViewer(HyperPlayer hp) {
		if (deleted) return;
		if (this.viewers == null) viewers = new ArrayList<HyperPlayer>();
		viewers.remove(hp);
	}
	public boolean hasViewers() {
		if (deleted) return false;
		if (!viewers.isEmpty()) return true;
		return false;
	}
	public boolean hasViewer(HyperPlayer hp) {
		if (deleted) return false;
		Iterator<HyperPlayer> it = viewers.iterator();
		while (it.hasNext()) {
			if (it.next().equals(hp)) return true;
		}
		return false;
	}
	public void setOpenedByOwner(boolean status) {
		openedByOwner = status;
	}
	public boolean openedByOwner() {
		return openedByOwner;
	}
	public boolean updateMenuLock() {
		return updateMenuLock.get();
	}
	public void setUpdateMenuLock(boolean state) {
		updateMenuLock.set(state);
	}

	
	public void setEditMode(boolean editModeEnabled, boolean editAllItems) {
		this.inEditItemMode = editModeEnabled;
		if (getSlotOfFirstItem() != -1) {
			this.editAllItems = editAllItems;
		}
	}
	public boolean inEditItemMode() {
		return inEditItemMode;
	}
	public boolean editAllItems() {
		return editAllItems;
	}
	public void setEditSlot(int slot) {
		this.editSlot = slot;
	}
	public int getEditSlot() {
		return editSlot;
	}
	public int getSlotOfFirstItem() {
		for (int i = 0; i < inventory.getSize(); i++) {
			if (inventory.getItem(i).isBlank()) continue;
			return i;
		}
		return -1;
	}
	public boolean inSelectItemMode() {
		return inSelectItemMode;
	}
	public void setSelectItemMode(boolean state) {
		this.inSelectItemMode = state;
	}
	public boolean inPreviewMode() {
		return inPreviewMode;
	}
	public void setInPreviewMode(boolean state) {
		this.inPreviewMode = state;
	}
	public boolean inDeleteMode() {
		return inDeleteMode;
	}
	public void setInDeleteMode(boolean state) {
		this.inDeleteMode = state;
	}
	
	public void setEditBuyPrice(boolean state) {
		this.editingBuyPrice = state;
	}
	public boolean editingBuyPrice() {
		return editingBuyPrice;
	}
	
	public void setEditSellPrice(boolean state) {
		this.editingSellPrice = state;
	}
	public boolean editingSellPrice() {
		return editingSellPrice;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ChestShop other = (ChestShop) obj;
		if (location == null) {
			if (other.location != null) return false;
		} else if (!location.equals(other.location)) return false;
		return true;
	}
	
}
