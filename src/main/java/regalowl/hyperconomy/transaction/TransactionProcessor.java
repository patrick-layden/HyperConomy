package regalowl.hyperconomy.transaction;


import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperItemStack;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectStatus;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.Log;



public class TransactionProcessor {

	private HyperConomy hc;
	private HyperEventHandler heh;
	private LanguageFile L;
	private HyperPlayer hp;

	
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
	private PlayerTransaction pt;
	private boolean overMaxStock;
	
	
	private boolean shopUnlimitedMoney;
	
	
	
	public TransactionProcessor(HyperPlayer hp) {
		hc = HyperConomy.hc;
		L = hc.getLanguageFile();
		this.hp = hp;
		heh = hc.getHyperEventHandler();
	}
	
	
	public TransactionResponse processTransaction(PlayerTransaction pt) {
		this.pt = pt;
		transactionType = pt.getTransactionType();
		hyperObject = pt.getHyperObject();
		amount = pt.getAmount();
		if (amount <= 0) {
			amount = 1;
		}
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
		receiveInventory = pt.getReceiveInventory();
		money = pt.getMoney();
		//chargeTax = pt.isChargeTax();
		setPrice = pt.isSetPrice();
		giveItem = pt.getGiveItem();
		
		
		shopUnlimitedMoney = hc.getConf().getBoolean("shop.server-shops-have-unlimited-money");

		switch (this.transactionType) {
			case BUY:
				if (hyperObject.getType() == HyperObjectType.ITEM) {
					return buy();
				} else if (hyperObject.getType() == HyperObjectType.EXPERIENCE) {
					return buyXP();
				} else if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
					return buyEnchant();
				} else {
					return null;
				}
			case SELL:
				if (hyperObject.getType() == HyperObjectType.ITEM) {
					return sell();
				} else if (hyperObject.getType() == HyperObjectType.EXPERIENCE) {
					return sellXP();
				} else if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
					return sellEnchant();
				} else {
					return null;
				}
			case SELL_TO_INVENTORY:
				return sellToInventory();
			case BUY_FROM_INVENTORY:
				return buyFromInventory();
			case BUY_FROM_ITEM:
				return buyEnchantFromItem();
			default:
				return new TransactionResponse(hp);
		}
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
	
	
	
	public TransactionResponse buy() {
		try {
			TransactionResponse response = new TransactionResponse(hp);
			if (hp == null || hyperObject == null) {
				response.setFailed();
				response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			CommonFunctions cf = hc.gCF();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			String name = hyperObject.getDisplayName();
			if (receiveInventory == null) {
				receiveInventory = hp.getPlayer().getInventory();
			}
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.SELL) {
				response.addFailed(L.f(L.get("SELL_ONLY_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), name), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			double shopstock = hyperObject.getStock();
			if (shopstock < amount) {
				response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), name), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (hyperObject.getItemStack().getType() == null) {
				response.addFailed(L.f(L.get("CANNOT_BE_PURCHASED_WITH"), name), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			double price = hyperObject.getBuyPrice(amount);
			double taxpaid = hyperObject.getPurchaseTax(price);
			price = cf.twoDecimals(price + taxpaid);
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			int space = hyperObject.getAvailableSpace(receiveInventory);
			if (space < amount) {
				response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, name), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			hyperObject.add(amount, receiveInventory);
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
				hyperObject.setStock(shopstock - amount);
			}
			hp.withdraw(price);
			tradePartner.deposit(price);
			resetBalanceIfUnlimited();
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, price, name, cf.twoDecimals(taxpaid)), price, hyperObject);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperObject.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(hp.getName(), "purchase", name, (double) amount, cf.twoDecimals(price - taxpaid), cf.twoDecimals(taxpaid), tradePartner.getName(), type);
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction buy() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}
	

	/**
	 * 
	 * 
	 * This function handles the sale of items.
	 * 
	 */
	
	public TransactionResponse sell() {
		try {
			TransactionResponse response = new TransactionResponse(hp);
			if (hp == null || hyperObject == null) {
				response.setFailed();
				response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			CommonFunctions cf = hc.gCF();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			String name = hyperObject.getDisplayName();
			if (giveInventory == null) {
				giveInventory = hp.getPlayer().getInventory();
			}
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.BUY) {
				response.addFailed(L.f(L.get("BUY_ONLY_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), name), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (hyperObject.getItemStack().getType() == null) {
				response.addFailed(L.f(L.get("CANNOT_BE_SOLD_WITH"), name), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			int totalitems = hyperObject.count(giveInventory);
			if (totalitems < amount) {
				boolean sellRemaining = hc.getConf().getBoolean("shop.sell-remaining-if-less-than-requested-amount");
				if (sellRemaining) {
					amount = totalitems;
				} else {
					response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), hyperObject);
					heh.fireTransactionEvent(pt, response);
					return response;
				}
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (overMaxStock) {
				response.addFailed(L.f(L.get("OVER_MAX_STOCK"), name), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
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
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
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
			heh.fireTransactionEvent(pt, response);
			return response;

		} catch (Exception e) {
			String info = "Transaction sell() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + ", amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}
	
	
	

	
	
	
	/**
	 * 
	 * 
	 * This function handles the purchase of experience.
	 * 
	 */
	public TransactionResponse buyXP() {
		TransactionResponse response = new TransactionResponse(hp);
		if (hp == null || hyperObject == null) {
			response.setFailed();
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			heh.fireTransactionEvent(pt, response);
			return response;
		}
		try {
			CommonFunctions cf = hc.gCF();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.SELL) {
				response.addFailed(L.f(L.get("SELL_ONLY_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (amount > 0) {
				int shopstock = 0;
				shopstock = (int) hyperObject.getStock();
				if (shopstock >= amount) {
					double price = hyperObject.getBuyPrice(amount);
					double taxpaid = hyperObject.getPurchaseTax(price);
					price = cf.twoDecimals(price + taxpaid);
					if (hp.hasBalance(price)) {
						int totalxp = hp.getTotalXpPoints();
						int newxp = totalxp + amount;
						int newlvl = hp.getLvlFromXP(newxp);
						newxp = newxp - hp.getLvlXpPoints(newlvl);
						float xpbarxp = (float) newxp / (float) hp.getXpForNextLvl(newlvl);
						hp.getPlayer().setLevel(newlvl);
						hp.getPlayer().setExp(xpbarxp);
						if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConf().getBoolean("shop.unlimited-stock-for-static-items") || hyperObject.isShopObject()) {
							hyperObject.setStock(shopstock - amount);
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
					} else {
						response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
					}
				} else {
					response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), hyperObject.getDisplayName()), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), hyperObject.getDisplayName()), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction buyXP() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}

	/**
	 * 
	 * 
	 * This function handles the sale of experience.
	 * 
	 */
	public TransactionResponse sellXP() {
		TransactionResponse response = new TransactionResponse(hp);
		if (hp == null || hyperObject == null) {
			response.setFailed();
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			heh.fireTransactionEvent(pt, response);
			return response;
		}
		try {
			CommonFunctions cf = hc.gCF();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.BUY) {
				response.addFailed(L.f(L.get("BUY_ONLY_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (amount > 0) {
				int totalxp = hp.getTotalXpPoints();
				if (totalxp >= amount) {
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
					if (hasBalance(price)) {
						if (overMaxStock) {
							response.addFailed(L.f(L.get("OVER_MAX_STOCK"), hyperObject.getDisplayName()), hyperObject);
							heh.fireTransactionEvent(pt, response);
							return response;
						}
						if (maxi == 0) {
							price = hyperObject.getSellPrice(amount);
						}
						int newxp = totalxp - amount;
						int newlvl = hp.getLvlFromXP(newxp);
						newxp = newxp - hp.getLvlXpPoints(newlvl);
						float xpbarxp = (float) newxp / (float) hp.getXpForNextLvl(newlvl);
						hp.getPlayer().setLevel(newlvl);
						hp.getPlayer().setExp(xpbarxp);
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
					} else {
						response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
					}
				} else {
					response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), hyperObject.getDisplayName()), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), hyperObject.getDisplayName()), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction sellXP() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}


	
	
	public TransactionResponse buyFromInventory() {
		TransactionResponse response = new TransactionResponse(hp);
		if (hp == null || tradePartner == null || hyperObject == null) {
			response.setFailed();
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			heh.fireTransactionEvent(pt, response);
			return response;
		}
		try {
			CommonFunctions cf = hc.gCF();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			double price = 0.0;
			if (setPrice) {
				price = money;
			} else {
				price = hyperObject.getSellPrice(amount);
			}
			if (hp.hasBalance(price)) {
				int space = hyperObject.getAvailableSpace(hp.getPlayer().getInventory());
				if (space >= amount) {
					hyperObject.add(amount, hp.getPlayer().getInventory());
					hyperObject.remove(amount, giveInventory);
					hp.withdraw(price);
					tradePartner.deposit(price);
					response.addSuccess(L.f(L.get("PURCHASE_CHEST_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getDisplayName(), tradePartner.getName()), cf.twoDecimals(price), hyperObject);
					response.setSuccessful();
					log.writeSQLLog(hp.getName(), "purchase", hyperObject.getDisplayName(), (double) amount, cf.twoDecimals(price), 0.0, tradePartner.getName(), "chestshop");
					tradePartner.sendMessage(L.f(L.get("CHEST_BUY_NOTIFICATION"), amount, cf.twoDecimals(price), hyperObject.getDisplayName(), hp.getPlayer()));
				} else {
					response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, hyperObject.getDisplayName()), hyperObject);
				}
			} else {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction buyChest() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}



	/**
	 * 
	 * 
	 * This function handles the sale of items from HyperChests.
	 * 
	 */
	public TransactionResponse sellToInventory() {
		TransactionResponse response = new TransactionResponse(hp);
		if (hp == null || tradePartner == null || hyperObject == null) {
			response.setFailed();
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			heh.fireTransactionEvent(pt, response);
			return response;
		}
		try {
			CommonFunctions cf = hc.gCF();
			Log log = hc.getLog();
			LanguageFile L = hc.getLanguageFile();
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
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction sellChest() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}

	
	
	
	/**
	 * 
	 * 
	 * This function handles the sale of enchantments.
	 * 
	 */
	public TransactionResponse sellEnchant() {
		TransactionResponse response = new TransactionResponse(hp);
		if (hp == null || hyperObject == null) {
			response.setFailed();
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			heh.fireTransactionEvent(pt, response);
			return response;
		}
		CommonFunctions cf = hc.gCF();
		Log log = hc.getLog();
		try {
			String nenchant = "";
			Player p = hp.getPlayer();
			nenchant = hyperObject.getEnchantmentName();
			Enchantment ench = Enchantment.getByName(nenchant);
			int lvl = hyperObject.getEnchantmentLevel();
			int truelvl = new HyperItemStack(p.getItemInHand()).getEnchantmentLevel(ench);
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.BUY) {
				response.addFailed(L.f(L.get("BUY_ONLY_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (overMaxStock) {
				response.addFailed(L.f(L.get("OVER_MAX_STOCK"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (new HyperItemStack(p.getItemInHand()).containsEnchantment(ench) && lvl == truelvl) {
				String mater = p.getItemInHand().getType().toString();
				double price = hyperObject.getSellPrice(EnchantmentClass.fromString(mater), hp);
				double fprice = price;
				if (hasBalance(fprice)) {
					double shopstock = hyperObject.getStock();
					double amountRemoved = hyperObject.removeEnchantment(p.getItemInHand());
					hyperObject.setStock(shopstock + amountRemoved);
					double salestax = hp.getSalesTax(fprice);
					hp.deposit(fprice - salestax);
					tradePartner.withdraw(fprice - salestax);
					resetBalanceIfUnlimited();
					fprice = cf.twoDecimals(fprice);
					response.addSuccess(L.f(L.get("ENCHANTMENT_SELL_MESSAGE"), 1, cf.twoDecimals(fprice), hyperObject.getDisplayName(), cf.twoDecimals(salestax)), cf.twoDecimals(fprice - salestax), hyperObject);
					response.setSuccessful();
					String type = "dynamic";
					if (Boolean.parseBoolean(hyperObject.getInitiation())) {
						type = "initial";
					} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
						type = "static";
					}
					log.writeSQLLog(p.getName(), "sale", hyperObject.getDisplayName(), 1.0, fprice - salestax, salestax, tradePartner.getName(), type);
				} else {
					response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("ITEM_DOESNT_HAVE_ENCHANTMENT"), hyperObject.getDisplayName()), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "ETransaction sellEnchant() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of enchantments.
	 * 
	 */
	public TransactionResponse buyEnchant() {
		TransactionResponse response = new TransactionResponse(hp);
		if (hp == null || hyperObject == null) {
			response.setFailed();
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			heh.fireTransactionEvent(pt, response);
			return response;
		}
		CommonFunctions cf = hc.gCF();
		Log log = hc.getLog();
		try {
			Player p = hp.getPlayer();
			String nenchant = hyperObject.getEnchantmentName();
			Enchantment ench = Enchantment.getByName(nenchant);
			double shopstock = hyperObject.getStock();
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.SELL) {
				response.addFailed(L.f(L.get("SELL_ONLY_ITEM"), hyperObject.getDisplayName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (shopstock >= 1.0) {
				String mater = p.getItemInHand().getType().toString();
				double price = hyperObject.getBuyPrice(EnchantmentClass.fromString(mater));
				price = price + hyperObject.getPurchaseTax(price);
				if (!new HyperItemStack(p.getItemInHand()).containsEnchantment(ench)) {
					if (hp.hasBalance(price)) {
						if (new HyperItemStack(p.getItemInHand()).canAcceptEnchantment(ench)) {
							hyperObject.setStock(shopstock - 1.0);
							hp.withdraw(price);
							tradePartner.deposit(price);
							resetBalanceIfUnlimited();
							int l = hyperObject.getName().length();
							String lev = hyperObject.getName().substring(l - 1, l);
							int level = Integer.parseInt(lev);
							new HyperItemStack(p.getItemInHand()).addEnchantment(ench, level);
							boolean stax;
							stax = Boolean.parseBoolean(hyperObject.getIsstatic());
							double taxrate;
							if (!stax) {
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
						} else {
							response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), hyperObject);
						}
					} else {
						response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
					}
				} else {
					response.addFailed(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), hyperObject.getDisplayName()), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "ETransaction buyEnchant() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of chestshop enchantments.
	 * 
	 */
	public TransactionResponse buyEnchantFromItem() {
		TransactionResponse response = new TransactionResponse(hp);
		if (hp == null || hyperObject == null || tradePartner == null || giveItem == null) {
			response.setFailed();
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			heh.fireTransactionEvent(pt, response);
			return response;
		}
		CommonFunctions cf = hc.gCF();
		Log log = hc.getLog();
		try {
			Player p = hp.getPlayer();
			String nenchant = "";
			nenchant = hyperObject.getEnchantmentName();
			Enchantment ench = Enchantment.getByName(nenchant);
			String mater = p.getItemInHand().getType().toString();
			double price;
			if (setPrice) {
				price = money;
			} else {
				price = hyperObject.getSellPrice(EnchantmentClass.fromString(mater), hp);
			}
			HyperItemStack his = new HyperItemStack(p.getItemInHand());
			if (!new HyperItemStack(p.getItemInHand()).containsEnchantment(ench)) {
				if (his.canAcceptEnchantment(ench) && p.getItemInHand().getAmount() == 1) {
					if (hp.hasBalance(price)) {
						hp.withdraw(price);
						tradePartner.deposit(price);
						int level = hyperObject.getEnchantmentLevel();
						his.addEnchantment(ench, level);
						new HyperItemStack(giveItem).removeEnchant(ench);
						price = cf.twoDecimals(price);
						response.addSuccess(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, cf.twoDecimals(price), hyperObject.getDisplayName(), tradePartner.getName()), cf.twoDecimals(price), hyperObject);
						response.setSuccessful();
						log.writeSQLLog(p.getName(), "purchase", hyperObject.getDisplayName(), 1.0, price, 0.0, tradePartner.getName(), "chestshop");
						tradePartner.sendMessage(L.f(L.get("CHEST_ENCHANTMENT_BUY_NOTIFICATION"), 1, cf.twoDecimals(price), hyperObject.getDisplayName(), p));
					} else {
						response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
					}
				} else {
					response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), hyperObject);
				}
			} else {
				response.addFailed(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "ETransaction buyChestEnchant() passed values name='" + hyperObject.getDisplayName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}


	
	
	

	
	
	

	
	
	
	
}
