package regalowl.hyperconomy.transaction;





import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.event.TransactionEvent;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectStatus;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.Log;
import regalowl.hyperconomy.util.MessageBuilder;



public class TransactionProcessor {

	private HC hc;
	private HyperEventHandler heh;
	private LanguageFile L;
	private HyperPlayer hp;
	private DataManager dm;
	private Log log;
	
	private TransactionType transactionType;
	private HyperAccount tradePartner;
	private TradeObject hyperObject;
	private int amount;
	private HInventory giveInventory;
	private HInventory receiveInventory;
	private double money;
	//private boolean chargeTax;
	private boolean setPrice;
	private HItemStack giveItem;
	private TradeObjectStatus status;
	private boolean overMaxStock;
	private boolean obeyShops;
	
	
	private boolean shopUnlimitedMoney;
	
	private TransactionResponse response;
	
	
	public TransactionProcessor(HyperPlayer hp) {
		this.hp = hp;
		hc = HC.hc;
		L = hc.getLanguageFile();
		dm = HC.hc.getDataManager();
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
			status = TradeObjectStatus.TRADE;
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
				if (hyperObject.getType() == TradeObjectType.ITEM) {
					buy();
					break;
				} else if (hyperObject.getType() == TradeObjectType.EXPERIENCE) {
					buyXP();
					break;
				} else if (hyperObject.getType() == TradeObjectType.ENCHANTMENT) {
					buyEnchant();
					break;
				}
			case SELL:
				checkShopSell();
				if (response.getFailedObjects().size() > 0) {break;}
				if (hyperObject.getType() == TradeObjectType.ITEM) {
					sell();
					break;
				} else if (hyperObject.getType() == TradeObjectType.EXPERIENCE) {
					sellXP();
					break;
				} else if (hyperObject.getType() == TradeObjectType.ENCHANTMENT) {
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
		if (status == TradeObjectStatus.NONE) {
			response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
			return;
		} else if (status == TradeObjectStatus.SELL) {
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
			price = CommonFunctions.twoDecimals(price + taxpaid);
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
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, price, hyperObject.getDisplayName(), CommonFunctions.twoDecimals(taxpaid)), price, hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), (double) amount, CommonFunctions.twoDecimals(price - taxpaid), CommonFunctions.twoDecimals(taxpaid), tradePartner.getName(), hyperObject.getStatusString());
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
			price = CommonFunctions.twoDecimals(price + taxpaid);
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
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, CommonFunctions.twoDecimals(price), hyperObject.getDisplayName(), CommonFunctions.twoDecimals(taxpaid)), CommonFunctions.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hp.getName(), (double) amount, CommonFunctions.twoDecimals(price), CommonFunctions.twoDecimals(taxpaid), tradePartner.getName(), hyperObject.getStatusString());
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
			HInventory inv = hp.getInventory();
			HItemStack heldItem = inv.getHeldItem();
			HEnchantment ench = hyperObject.getEnchantment();
			double price = hyperObject.getBuyPrice(EnchantmentClass.fromString(heldItem.getMaterial()));
			double taxpaid = hyperObject.getPurchaseTax(price);
			price = CommonFunctions.twoDecimals(taxpaid + price);
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
			inv.updateInventory();
			hyperObject.checkInitiationStatus();
			response.addSuccess(L.f(L.get("ENCHANTMENT_PURCHASE_MESSAGE"), 1, price, hyperObject.getDisplayName(), CommonFunctions.twoDecimals(taxpaid)), CommonFunctions.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), 1.0, CommonFunctions.twoDecimals(price), CommonFunctions.twoDecimals(taxpaid), tradePartner.getName(), hyperObject.getStatusString());
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
		if (status == TradeObjectStatus.NONE) {
			response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
			return;
		} else if (status == TradeObjectStatus.BUY) {
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
			double price = CommonFunctions.twoDecimals(hyperObject.getSellPrice(amount, hp));
			double amountRemoved = hyperObject.remove(amount, giveInventory);
			double shopstock = hyperObject.getStock();
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
				hyperObject.setStock(shopstock + amountRemoved);
			}
			double salestax = CommonFunctions.twoDecimals(hp.getSalesTax(price));
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
			double price = CommonFunctions.twoDecimals(hyperObject.getSellPrice(amount));
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				return;
			}
			hyperObject.remove(amount, hp);
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
				hyperObject.setStock(amount + hyperObject.getStock());
			}
			double salestax = CommonFunctions.twoDecimals(hp.getSalesTax(price));
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
			HInventory inv = hp.getInventory();
			HItemStack heldItem = inv.getHeldItem();
			if (!(heldItem.containsEnchantment(hyperObject.getEnchantment()))) {
				response.addFailed(L.f(L.get("ITEM_DOESNT_HAVE_ENCHANTMENT"), hyperObject.getDisplayName()), hyperObject);
				return;
			}
			String mater = heldItem.getMaterial().toString();
			double price = CommonFunctions.twoDecimals(hyperObject.getSellPrice(EnchantmentClass.fromString(mater), hp));
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				return;
			}
			double shopstock = hyperObject.getStock();
			double amountRemoved = hyperObject.removeEnchantment(heldItem);
			inv.updateInventory();
			hyperObject.setStock(shopstock + amountRemoved);
			double salestax = CommonFunctions.twoDecimals(hp.getSalesTax(price));
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
				price = CommonFunctions.twoDecimals(hyperObject.getSellPrice(amount));
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
			
			MessageBuilder mb = new MessageBuilder("CHEST_BUY_NOTIFICATION");
			mb.setAmount(amount);
			mb.setObjectName(hyperObject.getDisplayName());
			mb.setPrice(price);
			mb.setPlayerName(hp.getName());
			tradePartner.sendMessage(mb.build());
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
				price = CommonFunctions.twoDecimals(hyperObject.getSellPrice(amount, hp));
			}
			hyperObject.remove(amount, hp.getInventory());
			hyperObject.add(amount, receiveInventory);
			hp.deposit(price);
			tradePartner.withdraw(price);
			response.addSuccess(L.f(L.get("SELL_CHEST_MESSAGE"), amount, price, hyperObject.getDisplayName(), tradePartner.getName()), price, hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", hyperObject.getDisplayName(), (double) amount, price, 0.0, tradePartner.getName(), "chestshop");
			
			MessageBuilder mb = new MessageBuilder("CHEST_SELL_NOTIFICATION");
			mb.setAmount(amount);
			mb.setObjectName(hyperObject.getDisplayName());
			mb.setPrice(price);
			mb.setPlayerName(hp.getName());
			tradePartner.sendMessage(mb.build());
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
				price = CommonFunctions.twoDecimals(hyperObject.getSellPrice(EnchantmentClass.fromString(hp.getItemInHand().getMaterial()), hp));
			}
			HInventory inv = hp.getInventory();
			HItemStack heldItem = inv.getHeldItem();
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
			inv.updateInventory();
			price = CommonFunctions.twoDecimals(price);
			response.addSuccess(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, price, hyperObject.getDisplayName(), tradePartner.getName()), CommonFunctions.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), 1.0, price, 0.0, tradePartner.getName(), "chestshop");
			MessageBuilder mb = new MessageBuilder("CHEST_ENCHANTMENT_BUY_NOTIFICATION");
			mb.setAmount(1);
			mb.setObjectName(hyperObject.getDisplayName());
			mb.setPrice(price);
			mb.setPlayerName(hp.getName());
			tradePartner.sendMessage(mb.build());
			//tradePartner.sendMessage(L.f(L.get("CHEST_ENCHANTMENT_BUY_NOTIFICATION"), 1, price, hyperObject.getDisplayName(), hp));
		} catch (Exception e) {
			String info = "ETransaction buyChestEnchant() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "'";
			hc.gDB().writeError(e, info);
		}
	}


	
	
	

	
	
	

	
	
	
	
}
