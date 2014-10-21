package regalowl.hyperconomy.transaction;





import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.event.TransactionEvent;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectStatus;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.Log;



public class TransactionProcessor {

	private HyperConomy hc;
	private HyperEventHandler heh;
	private LanguageFile L;
	private HyperPlayer hp;
	private DataManager dm;
	private CommonFunctions cf;
	private Log log;
	
	private TransactionType transactionType;
	private HyperAccount tradePartner;
	private HyperObject hyperObject;
	private int amount;
	private SerializableInventory giveInventory;
	private SerializableInventory receiveInventory;
	private double money;
	//private boolean chargeTax;
	private boolean setPrice;
	private SerializableItemStack giveItem;
	private HyperObjectStatus status;
	private boolean overMaxStock;
	private boolean obeyShops;
	
	
	private boolean shopUnlimitedMoney;
	
	private TransactionResponse response;
	
	
	public TransactionProcessor(HyperPlayer hp) {
		this.hp = hp;
		hc = HyperConomy.hc;
		L = hc.getLanguageFile();
		dm = hc.getDataManager();
		cf = hc.getCommonFunctions();
		log = hc.getLog();
		heh = hc.getHyperEventHandler();
	}
	
	
	public TransactionResponse processTransaction(PlayerTransaction pt) {
		transactionType = pt.getTransactionType();
		hyperObject = pt.getHyperObject();
		amount = pt.getAmount();
		overMaxStock = false;
		if (hyperObject.isShopObject()) {
			status = hyperObject.getStatus();
			int maxStock = hyperObject.getMaxStock();
			int globalMaxStock = hc.getConf().getInt("shop.max-stock-per-item-in-playershops");
			if ((hyperObject.getStock() + amount) > maxStock || (hyperObject.getStock() + amount) > globalMaxStock) {
				overMaxStock = true;
			}
		} else {
			status = HyperObjectStatus.TRADE;
		}
		tradePartner = pt.getTradePartner();
		if (tradePartner == null) {
			tradePartner = hp.getHyperEconomy().getDefaultAccount();
		}
		giveInventory = pt.getGiveInventory();
		if (giveInventory == null) {
			giveInventory = hp.getInventory();
		}
		receiveInventory = pt.getReceiveInventory();
		if (receiveInventory == null) {
			receiveInventory = hp.getInventory();
		}
		money = pt.getMoney();
		//chargeTax = pt.isChargeTax();
		setPrice = pt.isSetPrice();
		giveItem = pt.getGiveItem();
		obeyShops = pt.obeyShops();
		
		shopUnlimitedMoney = hc.getConf().getBoolean("shop.server-shops-have-unlimited-money");
		
		response = new TransactionResponse(hp);

		switch (this.transactionType) {
			case BUY:
				checkShopBuy();
				if (response.getFailedObjects().size() > 0) {break;}
				if (hyperObject.getType() == HyperObjectType.ITEM) {
					buy();
					break;
				} else if (hyperObject.getType() == HyperObjectType.EXPERIENCE) {
					buyXP();
					break;
				} else if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
					buyEnchant();
					break;
				}
			case SELL:
				checkShopSell();
				if (response.getFailedObjects().size() > 0) {break;}
				if (hyperObject.getType() == HyperObjectType.ITEM) {
					sell();
					break;
				} else if (hyperObject.getType() == HyperObjectType.EXPERIENCE) {
					sellXP();
					break;
				} else if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
					sellEnchant();
					break;
				}
			case SELL_TO_INVENTORY:
				sellToInventory();
				break;
			case BUY_FROM_INVENTORY:
				buyFromInventory();
				break;
			case BUY_FROM_ITEM:
				buyEnchantFromItem();
				break;
		}
		heh.fireEvent(new TransactionEvent(pt, response));
		return response;
	}
	
	
	private void resetBalanceIfUnlimited() {
		if (shopUnlimitedMoney && tradePartner.equals(hp.getHyperEconomy().getDefaultAccount())) {
			tradePartner.setBalance(0);
		}
	}
	
	private boolean hasBalance(double price) {
		if (!tradePartner.hasBalance(price)) {
			if (shopUnlimitedMoney && tradePartner.equals(hp.getHyperEconomy().getDefaultAccount())) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	
	private void checkShopBuy() {
		if (hp == null || hyperObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			return;
		}
		if (obeyShops) {
			if (!dm.getHyperShopManager().inAnyShop(hp)) {
				response.addFailed(L.get("MUST_BE_IN_SHOP"), hyperObject);
				return;
			} else {
				Shop shop = dm.getHyperShopManager().getShop(hp);
				if (!hp.hasBuyPermission(shop)) {
					response.addFailed(L.get("NO_TRADE_PERMISSION"), hyperObject);
					return;
				}
				if (shop.isBanned(hyperObject)) {
					response.addFailed(L.get("CANT_BE_TRADED"), hyperObject);
					return;
				}
			}
		}
		if (status == HyperObjectStatus.NONE) {
			response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
			return;
		} else if (status == HyperObjectStatus.SELL) {
			response.addFailed(L.f(L.get("SELL_ONLY_ITEM"), hyperObject.getDisplayName()), hyperObject);
			return;
		}
		if (amount <= 0) {
			response.addFailed(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), hyperObject.getDisplayName()), hyperObject);
			return;
		}
		if (hyperObject.getStock() < amount) {
			response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), hyperObject.getDisplayName()), hyperObject);
			return;
		}
	}
	

	public void buy() {
		try {
			double price = hyperObject.getBuyPrice(amount);
			double taxpaid = hyperObject.getPurchaseTax(price);
			price = cf.twoDecimals(price + taxpaid);
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
				return;
			}
			int space = hyperObject.getAvailableSpace(receiveInventory);
			if (space < amount) {
				response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, hyperObject.getDisplayName()), hyperObject);
				return;
			}
			hyperObject.add(amount, receiveInventory);
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
				hyperObject.setStock(hyperObject.getStock() - amount);
			}
			hp.withdraw(price);
			tradePartner.deposit(price);
			resetBalanceIfUnlimited();
			hyperObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, price, hyperObject.getDisplayName(), cf.twoDecimals(taxpaid)), price, hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), (double) amount, cf.twoDecimals(price - taxpaid), cf.twoDecimals(taxpaid), tradePartner.getName(), hyperObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction buy() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			return;
		}
	}
	
	
	public void buyXP() {
		try {
			double price = hyperObject.getBuyPrice(amount);
			double taxpaid = hyperObject.getPurchaseTax(price);
			price = cf.twoDecimals(price + taxpaid);
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
				return;
			}
			hyperObject.add(amount, hp);
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
				hyperObject.setStock(hyperObject.getStock() - amount);
			}
			hp.withdraw(price);
			tradePartner.deposit(price);
			resetBalanceIfUnlimited();
			hyperObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getDisplayName(), cf.twoDecimals(taxpaid)), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hp.getName(), (double) amount, cf.twoDecimals(price), cf.twoDecimals(taxpaid), tradePartner.getName(), hyperObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction buyXP() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
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
			SerializableInventory inv = hp.getInventory();
			SerializableItemStack heldItem = inv.getHeldItem();
			SerializableEnchantment ench = hyperObject.getEnchantment();
			double price = hyperObject.getBuyPrice(EnchantmentClass.fromString(heldItem.getMaterial()));
			double taxpaid = hyperObject.getPurchaseTax(price);
			price = cf.twoDecimals(taxpaid + price);
			if (heldItem.containsEnchantment(ench)) {
				response.addFailed(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"), hyperObject);
				return;
			}
			if (!heldItem.canAcceptEnchantment(ench)) {
				response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), hyperObject);
				return;
			}
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
				return;
			}
			hyperObject.setStock(hyperObject.getStock() - amount);
			hp.withdraw(price);
			tradePartner.deposit(price);
			resetBalanceIfUnlimited();
			heldItem.addEnchantment(ench);
			hyperObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("ENCHANTMENT_PURCHASE_MESSAGE"), 1, price, hyperObject.getDisplayName(), cf.twoDecimals(taxpaid)), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), 1.0, cf.twoDecimals(price), cf.twoDecimals(taxpaid), tradePartner.getName(), hyperObject.getStatusString());
		} catch (Exception e) {
			String info = "ETransaction buyEnchant() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "'";
			hc.gDB().writeError(e, info);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void checkShopSell() {
		if (hp == null || hyperObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			return;
		}
		if (hp.isInCreativeMode() && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
			response.addFailed(L.get("CANT_SELL_CREATIVE"), hyperObject);
			return;
		}
		if (obeyShops) {
			if (!dm.getHyperShopManager().inAnyShop(hp)) {
				response.addFailed(L.get("MUST_BE_IN_SHOP"), hyperObject);
				return;
			} else {
				Shop shop = dm.getHyperShopManager().getShop(hp);
				if (!hp.hasSellPermission(shop)) {
					response.addFailed(L.get("NO_TRADE_PERMISSION"), hyperObject);
					return;
				}
				if (shop.isBanned(hyperObject)) {
					response.addFailed(L.get("CANT_BE_TRADED"), hyperObject);
					return;
				}
			}
		}
		if (status == HyperObjectStatus.NONE) {
			response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
			return;
		} else if (status == HyperObjectStatus.BUY) {
			response.addFailed(L.f(L.get("BUY_ONLY_ITEM"), hyperObject.getDisplayName()), hyperObject);
			return;
		}
		if (amount <= 0) {
			response.addFailed(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), hyperObject.getDisplayName()), hyperObject);
			return;
		}
		if (overMaxStock) {
			response.addFailed(L.f(L.get("OVER_MAX_STOCK"), hyperObject.getDisplayName()), hyperObject);
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
			String name = hyperObject.getDisplayName();
			if (hyperObject.getItem() == null) {
				response.addFailed(L.f(L.get("CANNOT_BE_SOLD_WITH"), name), hyperObject);
				return;
			}
			int totalitems = hyperObject.count(giveInventory);
			if (totalitems < amount) {
				boolean sellRemaining = hc.getConf().getBoolean("shop.sell-remaining-if-less-than-requested-amount");
				if (sellRemaining) {
					amount = totalitems;
				} else {
					response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), hyperObject);
					return;
				}
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), hyperObject);
				return;
			}
			double price = cf.twoDecimals(hyperObject.getSellPrice(amount, hp));
			double amountRemoved = hyperObject.remove(amount, giveInventory);
			double shopstock = hyperObject.getStock();
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
				hyperObject.setStock(shopstock + amountRemoved);
			}
			double salestax = cf.twoDecimals(hp.getSalesTax(price));
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();
			hyperObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, price, name, salestax), price - salestax, hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", name, (double)amount, price - salestax, salestax, tradePartner.getName(), hyperObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction sell() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + ", amount='" + amount + "'";
			hc.gDB().writeError(e, info);
		}
	}
	
	
	



	public void sellXP() {
		try {
			if (hp.getTotalXpPoints() < amount) {
				response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), hyperObject.getDisplayName()), hyperObject);
				return;
			}
			double price = cf.twoDecimals(hyperObject.getSellPrice(amount));
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				return;
			}
			hyperObject.remove(amount, hp);
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
				hyperObject.setStock(amount + hyperObject.getStock());
			}
			double salestax = cf.twoDecimals(hp.getSalesTax(price));
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();
			hyperObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, price, hyperObject.getDisplayName(), salestax), price, hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", hyperObject.getDisplayName(), (double) amount, price - salestax, salestax, tradePartner.getName(), hyperObject.getStatusString());
		} catch (Exception e) {
			String info = "Transaction sellXP() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
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
			SerializableInventory inv = hp.getInventory();
			SerializableItemStack heldItem = inv.getHeldItem();
			if (!(heldItem.containsEnchantment(hyperObject.getEnchantment()))) {
				response.addFailed(L.f(L.get("ITEM_DOESNT_HAVE_ENCHANTMENT"), hyperObject.getDisplayName()), hyperObject);
				return;
			}
			String mater = heldItem.getMaterial().toString();
			double price = cf.twoDecimals(hyperObject.getSellPrice(EnchantmentClass.fromString(mater), hp));
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				return;
			}
			double shopstock = hyperObject.getStock();
			double amountRemoved = hyperObject.removeEnchantment(heldItem);
			hyperObject.setStock(shopstock + amountRemoved);
			double salestax = cf.twoDecimals(hp.getSalesTax(price));
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();
			hyperObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("ENCHANTMENT_SELL_MESSAGE"), 1, price, hyperObject.getDisplayName(), salestax), price - salestax, hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", hyperObject.getDisplayName(), 1.0, price - salestax, salestax, tradePartner.getName(), hyperObject.getStatusString());
		} catch (Exception e) {
			String info = "ETransaction sellEnchant() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "'";
			hc.gDB().writeError(e, info);
		}
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void buyFromInventory() {
		if (hp == null || tradePartner == null || hyperObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			return;
		}
		try {
			double price = 0.0;
			if (setPrice) {
				price = money;
			} else {
				price = cf.twoDecimals(hyperObject.getSellPrice(amount));
			}
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
				return;
			}
			int space = hyperObject.getAvailableSpace(hp.getInventory());
			if (space < amount) {
				response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, hyperObject.getDisplayName()), hyperObject);
				return;
			}
			hyperObject.add(amount, hp.getInventory());
			hyperObject.remove(amount, giveInventory);
			hp.withdraw(price);
			tradePartner.deposit(price);
			response.addSuccess(L.f(L.get("PURCHASE_CHEST_MESSAGE"), amount, price, hyperObject.getDisplayName(), tradePartner.getName()), price, hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), (double) amount, price, 0.0, tradePartner.getName(), "chestshop");
			tradePartner.sendMessage(L.f(L.get("CHEST_BUY_NOTIFICATION"), amount, price, hyperObject.getDisplayName(), hp));
		} catch (Exception e) {
			String info = "Transaction buyChest() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
		}
	}



	/**
	 * 
	 * 
	 * This function handles the sale of items from HyperChests.
	 * 
	 */
	public void sellToInventory() {
		if (hp == null || tradePartner == null || hyperObject == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			return;
		}
		try {
			double price = 0.0;
			if (setPrice) {
				price = money;
			} else {
				price = cf.twoDecimals(hyperObject.getSellPrice(amount, hp));
			}
			hyperObject.remove(amount, hp.getInventory());
			hyperObject.add(amount, receiveInventory);
			hp.deposit(price);
			tradePartner.withdraw(price);
			response.addSuccess(L.f(L.get("SELL_CHEST_MESSAGE"), amount, price, hyperObject.getDisplayName(), tradePartner.getName()), price, hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", hyperObject.getDisplayName(), (double) amount, price, 0.0, tradePartner.getName(), "chestshop");
			tradePartner.sendMessage(L.f(L.get("CHEST_SELL_NOTIFICATION"), amount, price, hyperObject.getDisplayName(), hp));
		} catch (Exception e) {
			String info = "Transaction sellChest() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
		}
	}
	
	
	

	/**
	 * 
	 * 
	 * This function handles the purchase of chestshop enchantments.
	 * 
	 */
	public void buyEnchantFromItem() {
		if (hp == null || hyperObject == null || tradePartner == null || giveItem == null) {
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			return;
		}
		try {
			double price;
			if (setPrice) {
				price = money;
			} else {
				price = cf.twoDecimals(hyperObject.getSellPrice(EnchantmentClass.fromString(hp.getItemInHand().getMaterial()), hp));
			}
			SerializableItemStack heldItem = hp.getItemInHand();
			if (heldItem.containsEnchantment(hyperObject.getEnchantment())) {
				response.addFailed(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"), hyperObject);
				return;
			}
			if (!heldItem.canAcceptEnchantment(hyperObject.getEnchantment()) || hp.getItemInHand().getAmount() != 1) {
				response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), hyperObject);
				return;
			}
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
				return;
			}
			hp.withdraw(price);
			tradePartner.deposit(price);
			hyperObject.addEnchantment(heldItem);
			hyperObject.removeEnchantment(giveItem);
			price = cf.twoDecimals(price);
			response.addSuccess(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, price, hyperObject.getDisplayName(), tradePartner.getName()), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), 1.0, price, 0.0, tradePartner.getName(), "chestshop");
			tradePartner.sendMessage(L.f(L.get("CHEST_ENCHANTMENT_BUY_NOTIFICATION"), 1, price, hyperObject.getDisplayName(), hp));
		} catch (Exception e) {
			String info = "ETransaction buyChestEnchant() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "'";
			hc.gDB().writeError(e, info);
		}
	}


	
	
	

	
	
	

	
	
	
	
}
