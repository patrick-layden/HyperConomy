package regalowl.hyperconomy.transaction;


import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.event.TransactionEvent;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HEnchantmentStorageMeta;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemMeta;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectStatus;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.Log;
import regalowl.hyperconomy.util.MessageBuilder;
import regalowl.simpledatalib.CommonFunctions;



public class TransactionProcessor {

	private HyperConomy hc;
	private HyperEventHandler heh;
	private LanguageFile L;
	private HyperPlayer trader;
	private DataManager dm;
	private Log log;
	
	private TransactionType transactionType;
	private HyperAccount tradePartner;
	private HyperAccount taxAccount;
	private TradeObject tradeObject;
	private int amount;
	private HInventory giveInventory;
	private HInventory receiveInventory;
	private double money;
	//private boolean chargeTax;
	private boolean setPrice;
	//private HItemStack giveItem;
	private TradeObjectStatus status;
	private boolean overMaxStock;
	private boolean obeyShops;
	
	
	private boolean shopUnlimitedMoney;
	
	private TransactionResponse response;
	
	
	public TransactionProcessor(HyperConomy hc, HyperPlayer hp) {
		this.hc = hc;
		this.trader = hp;
		L = hc.getLanguageFile();
		dm = hc.getDataManager();
		log = hc.getLog();
		heh = hc.getHyperEventHandler();
	}
	
	
	public TransactionResponse processTransaction(PlayerTransaction pt) {
		transactionType = pt.getTransactionType();
		tradeObject = pt.getHyperObject();
		amount = pt.getAmount();
		overMaxStock = false;
		if (tradeObject.isShopObject()) {
			status = tradeObject.getShopObjectStatus();
			int maxStock = tradeObject.getShopObjectMaxStock();
			int globalMaxStock = hc.getConf().getInt("shop.max-stock-per-item-in-playershops");
			if ((tradeObject.getStock() + amount) > maxStock || (tradeObject.getStock() + amount) > globalMaxStock) {
				overMaxStock = true;
			}
		} else {
			status = TradeObjectStatus.TRADE;
		}
		taxAccount = hc.getDataManager().getAccount(hc.getConf().getString("tax.account"));
		if (taxAccount == null) taxAccount = trader.getHyperEconomy().getDefaultAccount();
		tradePartner = pt.getTradePartner();
		if (tradePartner == null) tradePartner = trader.getHyperEconomy().getDefaultAccount();
		giveInventory = pt.getGiveInventory();
		if (giveInventory == null) {
			giveInventory = trader.getInventory();
		}
		receiveInventory = pt.getReceiveInventory();
		if (receiveInventory == null) {
			receiveInventory = trader.getInventory();
		}
		money = pt.getMoney();
		//chargeTax = pt.isChargeTax();
		setPrice = pt.isSetPrice();
		//giveItem = pt.getGiveItem();
		obeyShops = pt.obeyShops();
		
		shopUnlimitedMoney = hc.getConf().getBoolean("shop.server-shops-have-unlimited-money");
		
		response = new TransactionResponse(hc, trader);

		switch (this.transactionType) {
			case BUY:
				checkShopBuy();
				if (response.getFailedObjects().size() > 0) {break;}
				if (tradeObject.getType() == TradeObjectType.ITEM) {
					buy();
					break;
				} else if (tradeObject.getType() == TradeObjectType.EXPERIENCE) {
					buyXP();
					break;
				} else if (tradeObject.getType() == TradeObjectType.ENCHANTMENT) {
					buyEnchant();
					break;
				}
			case SELL:
				checkShopSell();
				if (response.getFailedObjects().size() > 0) {break;}
				if (tradeObject.getType() == TradeObjectType.ITEM) {
					sell();
					break;
				} else if (tradeObject.getType() == TradeObjectType.EXPERIENCE) {
					sellXP();
					break;
				} else if (tradeObject.getType() == TradeObjectType.ENCHANTMENT) {
					sellEnchant();
					break;
				}
			case SELL_CUSTOM:
				sellCustom();
				break;
			case BUY_CUSTOM:
				buyCustom();
				break;
			//case BUY_FROM_ITEM:
			//	buyEnchantFromItem();
			//	break;
		}
		heh.fireEvent(new TransactionEvent(pt, response));
		return response;
	}
	
	
	private void resetBalanceIfUnlimited() {
		if (shopUnlimitedMoney && tradePartner.equals(trader.getHyperEconomy().getDefaultAccount())) {
			tradePartner.setBalance(0);
		}
	}
	
	private boolean tradePartnerHasBalance(double price) {
		if (!tradePartner.hasBalance(price)) {
			if (shopUnlimitedMoney && tradePartner.equals(trader.getHyperEconomy().getDefaultAccount())) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	
	private void checkShopBuy() {
		if (trader == null || tradeObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), tradeObject);
			return;
		}
		if (obeyShops) {
			if (!dm.getHyperShopManager().inAnyShop(trader)) {
				response.addFailed(L.get("MUST_BE_IN_SHOP"), tradeObject);
				return;
			} else {
				Shop shop = dm.getHyperShopManager().getShop(trader);
				if (!trader.hasBuyPermission(shop)) {
					response.addFailed(L.get("NO_TRADE_PERMISSION"), tradeObject);
					return;
				}
				if (shop.isBanned(tradeObject.getName())) {
					response.addFailed(L.get("CANT_BE_TRADED"), tradeObject);
					return;
				}
			}
		}
		if (status == TradeObjectStatus.NONE) {
			response.addFailed(L.f(L.get("NO_TRADE_ITEM"), tradeObject.getDisplayName()), tradeObject);
			return;
		} else if (status == TradeObjectStatus.SELL) {
			response.addFailed(L.f(L.get("SELL_ONLY_ITEM"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
		if (amount <= 0) {
			response.addFailed(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
		if (tradeObject.getStock() < amount) {
			response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
	}
	

	public void buy() {
		try {
			double price = CommonFunctions.twoDecimals(tradeObject.getBuyPrice(amount));
			double tax = CommonFunctions.twoDecimals(tradeObject.getPurchaseTax(price));
			if (trader.hasPermission("hyperconomy.taxexempt")) {
				tax = 0.00;
			}
			if (!trader.hasBalance(price + tax)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), tradeObject);
				return;
			}
			int space = receiveInventory.getAvailableSpace(tradeObject.getItem());
			if (space < amount) {
				response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, tradeObject.getDisplayName()), tradeObject);
				return;
			}
			receiveInventory.add(amount, tradeObject.getItem());
			if (!tradeObject.isStatic() || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || tradeObject.isShopObject()) {
				tradeObject.setStock(tradeObject.getStock() - amount);
			}
			trader.withdraw(price + tax);
			tradePartner.deposit(price);
			taxAccount.deposit(tax);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, price + tax, tradeObject.getDisplayName(), tax), price + tax, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(trader.getName(), "purchase", tradeObject.getDisplayName(), (double) amount, price, tax, tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction buy() passed values name='" + tradeObject.getDisplayName() + "', player='" + trader.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
			return;
		}
	}
	
	
	public void buyXP() {
		try {
			double price = CommonFunctions.twoDecimals(tradeObject.getBuyPrice(amount));
			double tax = CommonFunctions.twoDecimals(tradeObject.getPurchaseTax(price));
			if (trader.hasPermission("hyperconomy.taxexempt")) {
				tax = 0.00;
			}
			if (!trader.hasBalance(price + tax)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), tradeObject);
				return;
			}
			tradeObject.add(amount, trader);
			if (!tradeObject.isStatic() || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || tradeObject.isShopObject()) {
				tradeObject.setStock(tradeObject.getStock() - amount);
			}
			trader.withdraw(price + tax);
			tradePartner.deposit(price);
			taxAccount.deposit(tax);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, price + tax, tradeObject.getDisplayName(), tax), price + tax, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(trader.getName(), "purchase", trader.getName(), (double) amount, price, tax, tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction buyXP() passed values name='" + tradeObject.getDisplayName() + "', player='" + trader.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function handles the purchase of enchantments.
	 * 
	 */
	public void buyEnchant() {
		try {
			HInventory inv = trader.getInventory();
			HItemStack heldItem = inv.getHeldItem();
			HEnchantment ench = tradeObject.getEnchantment();
			double price = CommonFunctions.twoDecimals(tradeObject.getBuyPrice(EnchantmentClass.fromString(heldItem.getMaterial())));
			double tax = CommonFunctions.twoDecimals(tradeObject.getPurchaseTax(price));
			if (trader.hasPermission("hyperconomy.taxexempt")) {
				tax = 0.00;
			}
			if (heldItem.containsEnchantment(ench)) {
				response.addFailed(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"), tradeObject);
				return;
			}
			if (!heldItem.canAcceptEnchantment(ench)) {
				response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), tradeObject);
				return;
			}
			if (heldItem.getAmount() > 1) {
				response.addFailed(L.get("ENCHANTMENT_ONLY_SINGLE_ITEM"), tradeObject);
				return;
			}
			if (!trader.hasBalance(price + tax)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), tradeObject);
				return;
			}
			tradeObject.setStock(tradeObject.getStock() - amount);
			trader.withdraw(price + tax);
			tradePartner.deposit(price);
			taxAccount.deposit(tax);
			resetBalanceIfUnlimited();
			
			if (heldItem.getMaterial().equalsIgnoreCase("BOOK")) {
				HItemMeta cMeta = heldItem.getItemMeta();
				cMeta.addEnchantment(ench);
				heldItem.setMaterial("ENCHANTED_BOOK");
				heldItem.setHItemMeta(new HEnchantmentStorageMeta(cMeta));
			} else {
				heldItem.addEnchantment(ench);
			}
			inv.updateInventory();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("ENCHANTMENT_PURCHASE_MESSAGE"), 1, price + tax, tradeObject.getDisplayName(), tax), price + tax, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(trader.getName(), "purchase", tradeObject.getDisplayName(), 1.0, price, tax, tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "ETransaction buyEnchant() passed values name='" + tradeObject.getDisplayName() + "', player='" + trader.getName() + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void checkShopSell() {
		if (trader == null || tradeObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), tradeObject);
			return;
		}
		if (trader.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
			response.addFailed(L.get("CANT_SELL_CREATIVE"), tradeObject);
			return;
		}
		if (obeyShops) {
			if (!dm.getHyperShopManager().inAnyShop(trader)) {
				response.addFailed(L.get("MUST_BE_IN_SHOP"), tradeObject);
				return;
			} else {
				Shop shop = dm.getHyperShopManager().getShop(trader);
				if (!trader.hasSellPermission(shop)) {
					response.addFailed(L.get("NO_TRADE_PERMISSION"), tradeObject);
					return;
				}
				if (shop.isBanned(tradeObject.getName())) {
					response.addFailed(L.get("CANT_BE_TRADED"), tradeObject);
					return;
				}
			}
		}
		if (status == TradeObjectStatus.NONE) {
			response.addFailed(L.f(L.get("NO_TRADE_ITEM"), tradeObject.getDisplayName()), tradeObject);
			return;
		} else if (status == TradeObjectStatus.BUY) {
			response.addFailed(L.f(L.get("BUY_ONLY_ITEM"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
		if (amount <= 0) {
			response.addFailed(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
		if (overMaxStock) {
			response.addFailed(L.f(L.get("OVER_MAX_STOCK"), tradeObject.getDisplayName()), tradeObject);
			return;
		}
	}
	
	

	/**
	 * 
	 * 
	 * This function handles the sale of items.
	 * 
	 */
	
	public void sell() {
		try {
			String name = tradeObject.getDisplayName();
			if (tradeObject.getItem() == null) {
				response.addFailed(L.f(L.get("CANNOT_BE_SOLD_WITH"), name), tradeObject);
				return;
			}
			int totalitems = giveInventory.count(tradeObject.getItem());
			if (totalitems < amount) {
				boolean sellRemaining = hc.getConf().getBoolean("shop.sell-remaining-if-less-than-requested-amount");
				if (sellRemaining) {
					amount = totalitems;
				} else {
					response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), tradeObject);
					return;
				}
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), tradeObject);
				return;
			}
			double price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(amount, trader));
			double tax = CommonFunctions.twoDecimals(trader.getSalesTax(price));
			if (tradeObject.isShopObject()) tax = 0;
			double shopstock = tradeObject.getStock();
			if (!tradePartnerHasBalance(price)) {
				response.addFailed(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), tradePartner.getName()), tradeObject);
				return;
			}
			double amountRemoved = giveInventory.remove(amount, tradeObject.getItem());
			if (!tradeObject.isStatic() || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || tradeObject.isShopObject()) {
				tradeObject.setStock(shopstock + amountRemoved);
			}
			trader.deposit(price - tax);
			taxAccount.deposit(tax);
			tradePartner.withdraw(price);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, price, name, tax), price - tax, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(trader.getName(), "sale", name, (double)amount, price - tax, tax, tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction sell() passed values name='" + tradeObject.getDisplayName() + "', player='" + trader.getName() + ", amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}
	
	
	



	public void sellXP() {
		try {
			if (trader.getTotalXpPoints() < amount) {
				response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), tradeObject.getDisplayName()), tradeObject);
				return;
			}
			double price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(amount));
			double tax = CommonFunctions.twoDecimals(trader.getSalesTax(price));
			if (tradeObject.isShopObject()) tax = 0;
			if (!tradePartnerHasBalance(price)) {
				response.addFailed(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), tradePartner.getName()), tradeObject);
				return;
			}
			tradeObject.remove(amount, trader);
			if (!tradeObject.isStatic() || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || tradeObject.isShopObject()) {
				tradeObject.setStock(amount + tradeObject.getStock());
			}
			trader.deposit(price - tax);
			taxAccount.deposit(tax);
			tradePartner.withdraw(price);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, price, tradeObject.getDisplayName(), tax), price - tax, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(trader.getName(), "sale", tradeObject.getDisplayName(), (double) amount, price - tax, tax, tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction sellXP() passed values name='" + tradeObject.getDisplayName() + "', player='" + trader.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}


	
	


	
	
	
	/**
	 * 
	 * 
	 * This function handles the sale of enchantments.
	 * 
	 */
	public void sellEnchant() {
		try {
			HInventory inv = trader.getInventory();
			HItemStack heldItem = inv.getHeldItem();
			if (!(heldItem.containsEnchantment(tradeObject.getEnchantment()))) {
				response.addFailed(L.f(L.get("ITEM_DOESNT_HAVE_ENCHANTMENT"), tradeObject.getDisplayName()), tradeObject);
				return;
			}
			String mater = heldItem.getMaterial().toString();
			double price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(EnchantmentClass.fromString(mater), trader));
			double tax = CommonFunctions.twoDecimals(trader.getSalesTax(price));
			if (tradeObject.isShopObject()) tax = 0;
			if (!tradePartnerHasBalance(price)) {
				response.addFailed(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), tradePartner.getName()), tradeObject);
				return;
			}
			double shopstock = tradeObject.getStock();
			double amountRemoved = heldItem.removeEnchantment(tradeObject.getEnchantment());
			if (heldItem.getMaterial().equalsIgnoreCase("ENCHANTED_BOOK") && !heldItem.hasEnchantments()) {
				heldItem.setMaterial("BOOK");
				heldItem.setHItemMeta(new HItemMeta(heldItem.getItemMeta()));
			}
			inv.updateInventory();
			tradeObject.setStock(shopstock + amountRemoved);
			trader.deposit(price - tax);
			taxAccount.deposit(tax);
			tradePartner.withdraw(price);
			resetBalanceIfUnlimited();
			tradeObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("ENCHANTMENT_SELL_MESSAGE"), 1, price, tradeObject.getDisplayName(), tax), price - tax, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(trader.getName(), "sale", tradeObject.getDisplayName(), 1.0, price - tax, tax, tradePartner.getName(), tradeObject.getStatusString());
		} catch (Exception e) {
			String info = "ETransaction sellEnchant() passed values name='" + tradeObject.getDisplayName() + "', player='" + trader.getName() + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void buyCustom() {
		if (trader == null || tradePartner == null || tradeObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), tradeObject);
			return;
		}
		try {
			double price = 0.0;
			if (setPrice) {
				price = CommonFunctions.twoDecimals(money);
			} else {
				price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(amount));
			}
			if (!trader.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), tradeObject);
				return;
			}
			/*
			int space = trader.getInventory().getAvailableSpace(tradeObject.getItem());
			if (space < amount) {
				response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, tradeObject.getDisplayName()), tradeObject);
				return;
			}
			*/
			//trader.getInventory().add(amount, tradeObject.getItem());
			//giveInventory.remove(amount, tradeObject.getItem());
			trader.withdraw(price);
			tradePartner.deposit(price);
			response.addSuccess(L.f(L.get("PURCHASE_CHEST_MESSAGE"), amount, price, tradeObject.getDisplayName(), tradePartner.getName()), price, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(trader.getName(), "purchase", tradeObject.getDisplayName(), (double) amount, price, 0.0, tradePartner.getName(), "chestshop");
			
			MessageBuilder mb = new MessageBuilder(hc, "CUSTOM_BUY_NOTIFICATION");
			mb.setAmount(amount);
			mb.setObjectName(tradeObject.getDisplayName());
			mb.setPrice(price);
			mb.setPlayerName(trader.getName());
			tradePartner.sendMessage(mb.build());
		} catch (Exception e) {
			String info = "Transaction buyCustom() passed values name='" + tradeObject.getDisplayName() + "', player='" + trader.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}



	/**
	 * 
	 * 
	 * This function handles the sale of items from HyperChests.
	 * 
	 */
	public void sellCustom() {
		if (trader == null || tradePartner == null || tradeObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), tradeObject);
			return;
		}
		try {
			double price = 0.0;
			if (setPrice) {
				price = CommonFunctions.twoDecimals(money);
			} else {
				price = CommonFunctions.twoDecimals(tradeObject.getSellPrice(amount, trader));
			}
			if (!tradePartner.hasBalance(price)) {
				response.addFailed(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), tradePartner.getName()), tradeObject);
				return;
			}
			//trader.getInventory().remove(amount, tradeObject.getItem());
			//receiveInventory.add(amount, tradeObject.getItem());
			trader.deposit(price);
			tradePartner.withdraw(price);
			response.addSuccess(L.f(L.get("SELL_CHEST_MESSAGE"), amount, price, tradeObject.getDisplayName(), tradePartner.getName()), price, tradeObject);
			response.setSuccessful();
			log.writeSQLLog(trader.getName(), "sale", tradeObject.getDisplayName(), (double) amount, price, 0.0, tradePartner.getName(), "chestshop");
			
			MessageBuilder mb = new MessageBuilder(hc, "CUSTOM_SELL_NOTIFICATION");
			mb.setAmount(amount);
			mb.setObjectName(tradeObject.getDisplayName());
			mb.setPrice(price);
			mb.setPlayerName(trader.getName());
			tradePartner.sendMessage(mb.build());
		} catch (Exception e) {
			String info = "Transaction sellCustom() passed values name='" + tradeObject.getDisplayName() + "', player='" + trader.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gSDL().getErrorWriter().writeError(e, info);
		}
	}
	
	
	

	
	
	

	
	
	
	
}
