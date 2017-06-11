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
	private transient HashMap<HyperPlayer,String> viewerMenuTitles = new HashMap<HyperPlayer, String>();
	private transient boolean openedByOwner;
	private transient AtomicBoolean updateMenuLock = new AtomicBoolean();
	private transient HashMap<Integer, CustomPriceChestShopItem> customPriceItems = new HashMap<Integer, CustomPriceChestShopItem>();
	private transient HItemStack editStack;
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
		int dataId;
		String dataString;
		double buyPrice;
		double sellPrice;
		ChestShopType type;
		
		CustomPriceChestShopItem(String chestId, int dataId, String dataString, double buyPrice, double sellPrice, ChestShopType type) {
			this.chestId = chestId;
			this.dataId = dataId;
			this.dataString = dataString;
			this.buyPrice = buyPrice;
			this.sellPrice = sellPrice;
			this.type = type;
		}
	}
	
	public CustomPriceChestShopItem setCustomPriceItem(String chestId, int dataId, String dataString, double buyPrice, double sellPrice, ChestShopType type) {
		CustomPriceChestShopItem i = new CustomPriceChestShopItem(chestId, dataId, dataString, buyPrice, sellPrice, type);
		customPriceItems.put(dataId, i);
		return i;
	}
	
	public void saveCustomChestShopItem(HItemStack stack) {
		CustomPriceChestShopItem i = getCustomPriceItem(stack);
		if (i == null) return;
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("CHEST_ID", i.chestId);
		conditions.put("DATA_ID", i.dataId+"");
		hc.getSQLWrite().performDelete("hyperconomy_chest_shop_items", conditions);
		
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("CHEST_ID", i.chestId);
		values.put("DATA_ID", i.dataId+"");
		values.put("BUY_PRICE", i.buyPrice+"");
		values.put("SELL_PRICE", i.sellPrice+"");
		values.put("TYPE", i.type.name());
		hc.getSQLWrite().performInsert("hyperconomy_chest_shop_items", values);
	}
	
	public TradeObject getTradeObject(HItemStack stack) {
		if (stack == null || stack.isBlank()) return null;
		TradeObject to = null;
		if (owner instanceof HyperPlayer) {
			HyperPlayer hp = (HyperPlayer)owner;
			to = hp.getHyperEconomy().getTradeObject(stack);
		} else {
			to = hc.getDataManager().getDefaultEconomy().getTradeObject(stack);
		}
		if (to == null) to = TempTradeItem.generate(hc, stack);
		return to;
	}
	

	public CustomPriceChestShopItem getCustomPriceItem(HItemStack stack) {
		if (!isInitialized) return null;
		if (stack == null || stack.isBlank()) return null;
		Integer existingId = hc.getDataManager().getItemDataIdFromStack(stack);
		if (existingId != null) {
			CustomPriceChestShopItem i = customPriceItems.get(existingId);
			if (i != null) return i;
		} 
		TradeObject to = getTradeObject(stack);
		return setCustomPriceItem(getId(), to.getDataId(), stack.serialize(), to.getBuyPrice(1), to.getSellPrice(1), ChestShopType.TRADE);
	}

	
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
		if (sign != null) {
			sign.setLine(0, hc.getMC().applyColor("&cDeleted"));
			sign.update();
		}
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

	public ChestShopType getType(HItemStack stack) {
		CustomPriceChestShopItem i = getCustomPriceItem(stack);
		if (i == null) return ChestShopType.TRADE;
		return i.type;
	}

	public void setType(ChestShopType type, HItemStack stack) {
		if (!isInitialized) return;
		CustomPriceChestShopItem i = getCustomPriceItem(stack);
		i.type = type;
		saveCustomChestShopItem(stack);
	}

	public boolean isBuyStack(HItemStack stack) {
		ChestShopType type = getType(stack);
		if (type == ChestShopType.TRADE || type == ChestShopType.BUY) return true;
		return false;
	}

	public boolean isSellStack(HItemStack stack) {
		ChestShopType type = getType(stack);
		if (type == ChestShopType.TRADE || type == ChestShopType.SELL) return true;
		return false;
	}

	public void setBuyPrice(HItemStack stack, double price) {
		if (!isInitialized) return;
		if (price < 0) price = 0;
		CustomPriceChestShopItem i = getCustomPriceItem(stack);
		i.buyPrice = price;
		saveCustomChestShopItem(stack);
	}
	
	public void setBuyPriceAll(double price) {
		if (!isInitialized) return;
		if (price < 0) price = 0;
		for (int i = 0; i < inventory.getSize(); i++) {
			HItemStack stack = inventory.getItem(i);
			if (stack.isBlank()) continue;
			setBuyPrice(stack, price);
		}
	}

	public double getBuyPrice(HItemStack stack) {
		CustomPriceChestShopItem i = getCustomPriceItem(stack);
		if (i == null) {
			return getTradeObject(stack).getBuyPrice(1);
		} else {
			return i.buyPrice;
		}
	}

	public void setSellPrice(HItemStack stack, double price) {
		if (!isInitialized) return;
		if (price < 0) price = 0;
		CustomPriceChestShopItem i = getCustomPriceItem(stack);
		i.sellPrice = price;
		saveCustomChestShopItem(stack);
	}
	
	public void setSellPriceAll(double price) {
		if (!isInitialized) return;
		if (price < 0) price = 0;
		for (int i = 0; i < inventory.getSize(); i++) {
			HItemStack stack = inventory.getItem(i);
			if (stack.isBlank()) continue;
			setSellPrice(stack, price);
		}
	}

	public double getSellPrice(HItemStack stack) {
		CustomPriceChestShopItem i = getCustomPriceItem(stack);
		if (i == null) {
			return getTradeObject(stack).getSellPrice(1);
		} else {
			return i.sellPrice;
		}
	}

	public void toggleType(HItemStack stack) {
		ChestShopType type = getType(stack);
		if (type == ChestShopType.BUY) {
			setType(ChestShopType.TRADE, stack);
		} else if (type == ChestShopType.SELL) {
			setType(ChestShopType.BUY, stack);
		} else if (type == ChestShopType.TRADE) {
			setType(ChestShopType.SELL, stack);
		}
	}
	public void toggleTypeAll() {
		ChestShopType type = getType(getFirstItem());
		ChestShopType newType = ChestShopType.TRADE;
		if (type == ChestShopType.BUY) {
			newType = ChestShopType.TRADE;
		} else if (type == ChestShopType.SELL) {
			newType = ChestShopType.BUY;
		} else if (type == ChestShopType.TRADE) {
			newType = ChestShopType.SELL;
		}
		for (int i = 0; i < inventory.getSize(); i++) {
			HItemStack stack = inventory.getItem(i);
			if (stack.isBlank()) continue;
			setType(newType, stack);
		}
	}


	public boolean isValid() {
		return isValidChestShop;
	}
	public boolean isInitialized() {
		return isInitialized;
	}

	
	


	
	public HInventory getShopMenu() {
		HInventory shopMenuInventory = new HInventory(inventory);
		for (int i = 0; i < shopMenuInventory.getSize(); i++) {
			HItemStack menuStack = shopMenuInventory.getItem(i);
			HItemStack realStack = inventory.getItem(i);
			if (menuStack.isBlank()) continue;
			List<String> originalLore = menuStack.getItemMeta().getLore();
			menuStack.getItemMeta().setLore(new ArrayList<String>());
			if (isBuyStack(realStack)) {
				menuStack.getItemMeta().addLore(hc.getMC().applyColor("&9Buy 1 from chest: " + "&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getBuyPrice(realStack)))));
				menuStack.getItemMeta().addLore(hc.getMC().applyColor("&e(Any Click)"));
			}
			if (isSellStack(realStack)) {
				menuStack.getItemMeta().addLore(hc.getMC().applyColor("&9Sell 1 to chest: " + "&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getSellPrice(realStack)))));
				menuStack.getItemMeta().addLore(hc.getMC().applyColor("&e(Drag and Drop)"));
			}
			if (originalLore.size() > 0) {
				menuStack.getItemMeta().addLore(hc.getMC().applyColor("&5Item has lore: "));
				for (String entry:originalLore) {
					menuStack.getItemMeta().addLore(entry);
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
			for (int i = 0; i < shopMenuInventory.getSize(); i++) {
				HItemStack menuStack = shopMenuInventory.getItem(i);
				HItemStack realStack = inventory.getItem(i);
				if (realStack == null || realStack.isBlank() || !(realStack.equals(editStack)) || editAllItems) {
					shopMenuInventory.setItem(i, blockedSlot, false);
				} else {
					menuStack.getItemMeta().setLore(new ArrayList<String>());
					menuStack.getItemMeta().setDisplayName(hc.getMC().applyColor("&eEdit Mode"));
					menuStack.getItemMeta().addLore(hc.getMC().applyColor("&9Edit this item's settings "));
					menuStack.getItemMeta().addLore(hc.getMC().applyColor("&9using the bottom buttons."));
					menuStack.getItemMeta().addLore(hc.getMC().applyColor(" "));
					if (isBuyStack(realStack)) {
						menuStack.getItemMeta().addLore(hc.getMC().applyColor("&9Buy From Chest Price: "));
						menuStack.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getBuyPrice(realStack)))));
					} 
					if (isSellStack(realStack)){
						menuStack.getItemMeta().addLore(hc.getMC().applyColor("&9Sell To Chest Price: "));
						menuStack.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getSellPrice(realStack)))));
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
			if (isBuyStack(editStack) && isSellStack(editStack)) {
				tradeMode.getItemMeta().addLore(hc.getMC().applyColor("&bbought from the chest"));
				tradeMode.getItemMeta().addLore(hc.getMC().applyColor("&band sold to the chest"));
			} else if (isBuyStack(editStack)) {
				tradeMode.getItemMeta().addLore(hc.getMC().applyColor("&bbought from the chest"));
			} else if (isSellStack(editStack)) {
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
			if (isBuyStack(editStack)) {
				decreasePrice.getItemMeta().addLore(hc.getMC().applyColor("&9Current Buy From Chest Price: "));
				decreasePrice.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getBuyPrice(editStack)))));
			} 
			if (isSellStack(editStack)) {
				decreasePrice.getItemMeta().addLore(hc.getMC().applyColor("&9Current Sell To Chest Price: "));
				decreasePrice.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getSellPrice(editStack)))));
			}
			shopMenuInventory.setItem(30, decreasePrice, false);
			HItemStack increasePrice = hc.getDataManager().getDefaultEconomy().getTradeObject("lime_stained_glass_pane").getItem();
			increasePrice.getItemMeta().setDisplayName(hc.getMC().applyColor("&eIncrease Price"));
			if (isBuyStack(editStack)) {
				increasePrice.getItemMeta().addLore(hc.getMC().applyColor("&9Current Buy From Chest Price: "));
				increasePrice.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getBuyPrice(editStack)))));
			} 
			if (isSellStack(editStack)) {
				increasePrice.getItemMeta().addLore(hc.getMC().applyColor("&9Current Sell To Chest Price: "));
				increasePrice.getItemMeta().addLore(hc.getMC().applyColor("&a" + hc.getLanguageFile().fC(CommonFunctions.twoDecimals(getSellPrice(editStack)))));
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
	
	
	
	public String getMenuName(HyperPlayer viewer) {
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
			if (viewer != null) {
				String title = viewerMenuTitles.get(viewer);
				if (title != null && !title.equals("")) menuName = title;
			}
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
				hc.getMC().openInventory(getOwnerShopMenu(), viewer, getMenuName(viewer));
			} else {
				hc.getMC().openInventory(getShopMenu(), viewer, getMenuName(viewer));
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
		viewerMenuTitles.remove(hp);
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
	
	public void setViewerMenuTitle(HyperPlayer viewer, String title) {
		if (hasViewer(viewer)) {
			viewerMenuTitles.put(viewer, title);
		}
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
		if (getFirstItem() != null) {
			this.editAllItems = editAllItems;
		}
	}
	public boolean inEditItemMode() {
		return inEditItemMode;
	}
	public boolean editAllItems() {
		return editAllItems;
	}
	public void setEditStack(HItemStack stack) {
		this.editStack = stack;
	}
	public HItemStack getEditStack() {
		return editStack;
	}
	public HItemStack getFirstItem() {
		for (int i = 0; i < inventory.getSize(); i++) {
			HItemStack stack = inventory.getItem(i);
			if (stack.isBlank()) continue;
			return stack;
		}
		return null;
	}
	public int getSlotOfMatchingItem(HItemStack stack) {
		for (int i = 0; i < inventory.getSize(); i++) {
			HItemStack cItem = inventory.getItem(i);
			if (cItem.isBlank()) continue;
			if (cItem.equals(stack)) return i;
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
	
	public boolean isPartOfChestShop(HLocation l) {
		if (location == null || l == null) return false;
		if (location.equals(l)) return true;
		if (location.equals(l.down())) return true;
		return false;
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
