package regalowl.hyperconomy.transaction;



import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperItemStack;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectStatus;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
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
	private Inventory giveInventory;
	private Inventory receiveInventory;
	private double money;
	//private boolean chargeTax;
	private boolean setPrice;
	private ItemStack giveItem;
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
			giveInventory = hp.getPlayer().getInventory();
		}
		receiveInventory = pt.getReceiveInventory();
		if (receiveInventory == null) {
			receiveInventory = hp.getPlayer().getInventory();
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
		heh.fireTransactionEvent(pt, response);
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
			if (!dm.inAnyShop(hp.getPlayer())) {
				response.addFailed(L.get("MUST_BE_IN_SHOP"), hyperObject);
				return;
			} else {
				Shop shop = dm.getShop(hp.getPlayer());
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
			//if (hyperObject.getItemStack().getType() == null) {
			//	response.addFailed(L.f(L.get("CANNOT_BE_PURCHASED_WITH"), name), hyperObject);
			//	return;
			//}
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
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, price, hyperObject.getDisplayName(), cf.twoDecimals(taxpaid)), price, hyperObject);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperObject.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), (double) amount, cf.twoDecimals(price - taxpaid), cf.twoDecimals(taxpaid), tradePartner.getName(), type);
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
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getDisplayName(), cf.twoDecimals(taxpaid)), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperObject.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(hp.getName(), "purchase", hp.getName(), (double) amount, cf.twoDecimals(price), cf.twoDecimals(taxpaid), tradePartner.getName(), type);
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
			Player p = hp.getPlayer();
			Enchantment ench = hyperObject.getEnchantment();
			double price = hyperObject.getBuyPrice(EnchantmentClass.fromString(p.getItemInHand().getType().toString()));
			price = price + hyperObject.getPurchaseTax(price);
			if (new HyperItemStack(p.getItemInHand()).containsEnchantment(ench)) {
				response.addFailed(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"), hyperObject);
				return;
			}
			if (!new HyperItemStack(p.getItemInHand()).canAcceptEnchantment(ench)) {
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
			String levelString = hyperObject.getName().substring(hyperObject.getName().length() - 1, hyperObject.getName().length());
			new HyperItemStack(p.getItemInHand()).addEnchantment(ench, Integer.parseInt(levelString));
			double taxrate;
			if (!Boolean.parseBoolean(hyperObject.getIsstatic())) {
				taxrate = hc.getConf().getDouble("tax.enchant");
			} else {
				taxrate = hc.getConf().getDouble("tax.static");
			}
			double taxpaid = price - (price / (1 + taxrate / 100));
			taxpaid = cf.twoDecimals(taxpaid);
			price = cf.twoDecimals(price);
			response.addSuccess(L.f(L.get("ENCHANTMENT_PURCHASE_MESSAGE"), 1, price, hyperObject.getDisplayName(), cf.twoDecimals(taxpaid)), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperObject.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(p.getName(), "purchase", hyperObject.getDisplayName(), 1.0, price, taxpaid, tradePartner.getName(), type);
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
		if (hp.getPlayer().getGameMode() == GameMode.CREATIVE && hc.getConf().getBoolean("shop.block-selling-in-creative-mode")) {
			response.addFailed(L.get("CANT_SELL_CREATIVE"), hyperObject);
			return;
		}
		if (obeyShops) {
			if (!dm.inAnyShop(hp.getPlayer())) {
				response.addFailed(L.get("MUST_BE_IN_SHOP"), hyperObject);
				return;
			} else {
				Shop shop = dm.getShop(hp.getPlayer());
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

			if (hyperObject.getItemStack().getType() == null) {
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
			double price = hyperObject.getSellPrice(amount, hp);
			int maxi = hyperObject.getMaxInitial();
			boolean isstatic = false;
			boolean isinitial = false;
			isinitial = Boolean.parseBoolean(hyperObject.getInitiation());
			isstatic = Boolean.parseBoolean(hyperObject.getIsstatic());
			if ((amount > maxi) && !isstatic && isinitial) {
				amount = maxi;
				price = hyperObject.getSellPrice(amount, hp);
			}
			if (maxi == 0) {
				price = hyperObject.getSellPrice(amount, hp);
			}
			double amountRemoved = hyperObject.remove(amount, giveInventory);
			double shopstock = hyperObject.getStock();
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
				hyperObject.setStock(shopstock + amountRemoved);
			}
			int maxi2 = hyperObject.getMaxInitial();
			if (maxi2 == 0) {
				hyperObject.setInitiation("false");
			}
			double salestax = hp.getSalesTax(price);
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();
			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, cf.twoDecimals(price), name, cf.twoDecimals(salestax)), cf.twoDecimals(price - salestax), hyperObject);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperObject.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(hp.getName(), "sale", name, (double) amount, cf.twoDecimals(price - salestax), cf.twoDecimals(salestax), tradePartner.getName(), type);
			return;

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
			double price = hyperObject.getSellPrice(amount);
			int maxi = hyperObject.getMaxInitial();
			boolean itax;
			boolean stax;
			itax = Boolean.parseBoolean(hyperObject.getInitiation());
			stax = Boolean.parseBoolean(hyperObject.getIsstatic());
			if (amount > (maxi) && !stax && itax) {
				amount = maxi;
				price = hyperObject.getSellPrice(amount);
			}
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				return;
			}
			if (maxi == 0) {
				price = hyperObject.getSellPrice(amount);
			}
			hyperObject.remove(amount, hp);
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
				hyperObject.setStock(amount + hyperObject.getStock());
			}
			int maxi2 = hyperObject.getMaxInitial();
			if (maxi2 == 0) {
				hyperObject.setInitiation("false");
			}
			double salestax = cf.twoDecimals(hp.getSalesTax(price));
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();
			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getDisplayName(), salestax), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperObject.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(hp.getName(), "sale", hyperObject.getDisplayName(), (double) amount, cf.twoDecimals(price - salestax), cf.twoDecimals(salestax), tradePartner.getName(), type);
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
			Player p = hp.getPlayer();
			if (!(new HyperItemStack(p.getItemInHand()).containsEnchantment(hyperObject.getEnchantment()))) {
				response.addFailed(L.f(L.get("ITEM_DOESNT_HAVE_ENCHANTMENT"), hyperObject.getDisplayName()), hyperObject);
				return;
			}
			String mater = p.getItemInHand().getType().toString();
			double price = hyperObject.getSellPrice(EnchantmentClass.fromString(mater), hp);
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				return;
			}
			double shopstock = hyperObject.getStock();
			double amountRemoved = hyperObject.removeEnchantment(p.getItemInHand());
			hyperObject.setStock(shopstock + amountRemoved);
			double salestax = hp.getSalesTax(price);
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();
			price = cf.twoDecimals(price);
			response.addSuccess(L.f(L.get("ENCHANTMENT_SELL_MESSAGE"), 1, cf.twoDecimals(price), hyperObject.getDisplayName(), cf.twoDecimals(salestax)), cf.twoDecimals(price - salestax), hyperObject);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperObject.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(p.getName(), "sale", hyperObject.getDisplayName(), 1.0, price - salestax, salestax, tradePartner.getName(), type);
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
				price = hyperObject.getSellPrice(amount);
			}
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
				return;
			}
			int space = hyperObject.getAvailableSpace(hp.getPlayer().getInventory());
			if (space < amount) {
				response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, hyperObject.getDisplayName()), hyperObject);
				return;
			}
			hyperObject.add(amount, hp.getPlayer().getInventory());
			hyperObject.remove(amount, giveInventory);
			hp.withdraw(price);
			tradePartner.deposit(price);
			response.addSuccess(L.f(L.get("PURCHASE_CHEST_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getDisplayName(), tradePartner.getName()), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), (double) amount, cf.twoDecimals(price), 0.0, tradePartner.getName(), "chestshop");
			tradePartner.sendMessage(L.f(L.get("CHEST_BUY_NOTIFICATION"), amount, cf.twoDecimals(price), hyperObject.getDisplayName(), hp.getPlayer()));
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
				price = hyperObject.getSellPrice(amount, hp);
			}
			hyperObject.remove(amount, hp.getPlayer().getInventory());
			hyperObject.add(amount, receiveInventory);
			hp.deposit(price);
			tradePartner.withdraw(price);
			response.addSuccess(L.f(L.get("SELL_CHEST_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getDisplayName(), tradePartner.getName()), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", hyperObject.getDisplayName(), (double) amount, cf.twoDecimals(price), 0.0, tradePartner.getName(), "chestshop");
			tradePartner.sendMessage(L.f(L.get("CHEST_SELL_NOTIFICATION"), amount, cf.twoDecimals(price), hyperObject.getDisplayName(), hp.getPlayer()));
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
			Player p = hp.getPlayer();
			Enchantment ench = Enchantment.getByName(hyperObject.getEnchantmentName());
			double price;
			if (setPrice) {
				price = money;
			} else {
				price = hyperObject.getSellPrice(EnchantmentClass.fromString(p.getItemInHand().getType().toString()), hp);
			}
			HyperItemStack his = new HyperItemStack(p.getItemInHand());
			if (his.containsEnchantment(ench)) {
				response.addFailed(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"), hyperObject);
				return;
			}
			if (!his.canAcceptEnchantment(ench) || p.getItemInHand().getAmount() != 1) {
				response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), hyperObject);
				return;
			}
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
				return;
			}
			hp.withdraw(price);
			tradePartner.deposit(price);
			hyperObject.addEnchantment(p.getItemInHand());
			hyperObject.removeEnchantment(giveItem);
			price = cf.twoDecimals(price);
			response.addSuccess(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, cf.twoDecimals(price), hyperObject.getDisplayName(), tradePartner.getName()), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(p.getName(), "purchase", hyperObject.getDisplayName(), 1.0, price, 0.0, tradePartner.getName(), "chestshop");
			tradePartner.sendMessage(L.f(L.get("CHEST_ENCHANTMENT_BUY_NOTIFICATION"), 1, cf.twoDecimals(price), hyperObject.getDisplayName(), p));
		} catch (Exception e) {
			String info = "ETransaction buyChestEnchant() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "'";
			hc.gDB().writeError(e, info);
		}
	}


	
	
	

	
	
	

	
	
	
	
}
