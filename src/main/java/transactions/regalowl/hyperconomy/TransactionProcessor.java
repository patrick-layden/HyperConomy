package regalowl.hyperconomy;


import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;



public class TransactionProcessor {

	private HyperConomy hc;
	private HyperEventHandler heh;
	private EconomyManager em;
	private LanguageFile L;
	private HyperPlayer hp;

	
	private TransactionType transactionType;
	private HyperPlayer tradePartner;
	private HyperObject hyperObject;
	private HyperItem hyperItem;
	private HyperEnchant hyperEnchant;
	private HyperXP xp;
	private int amount;
	private Inventory giveInventory;
	private Inventory receiveInventory;
	private double money;
	//private boolean chargeTax;
	private boolean setPrice;
	private ItemStack giveItem;
	private HyperObjectStatus status;
	private PlayerTransaction pt;
	
	
	
	private boolean shopUnlimitedMoney;
	
	
	
	TransactionProcessor(HyperPlayer hp) {
		hc = HyperConomy.hc;
		em = hc.getEconomyManager();
		L = hc.getLanguageFile();
		this.hp = hp;
		heh = hc.getHyperEventHandler();
	}
	
	
	public TransactionResponse processTransaction(PlayerTransaction pt) {
		this.pt = pt;
		transactionType = pt.getTransactionType();
		hyperObject = pt.getHyperObject();
		if (hyperObject instanceof PlayerShopObject) {
			status = ((PlayerShopObject) hyperObject).getStatus();
		} else {
			status = HyperObjectStatus.TRADE;
		}
		if (hyperObject instanceof HyperItem) {
			hyperItem = (HyperItem)hyperObject;
		} else if (hyperObject instanceof HyperEnchant) {
			hyperEnchant = (HyperEnchant)hyperObject;
		} else if (hyperObject instanceof HyperXP) {
			xp = (HyperXP)hyperObject;
		}
		tradePartner = pt.getTradePartner();
		if (tradePartner == null) {
			tradePartner = em.getGlobalShopAccount();
		}
		amount = pt.getAmount();
		giveInventory = pt.getGiveInventory();
		receiveInventory = pt.getReceiveInventory();
		money = pt.getMoney();
		//chargeTax = pt.isChargeTax();
		setPrice = pt.isSetPrice();
		giveItem = pt.getGiveItem();
		
		
		shopUnlimitedMoney = hc.gYH().gFC("config").getBoolean("config.shop-has-unlimited-money");

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
			case SELL_ALL:
				return sellAll();
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
		if (shopUnlimitedMoney && tradePartner.equals(em.getGlobalShopAccount())) {
			tradePartner.setBalance(0);
		}
	}
	
	private boolean hasBalance(double price) {
		if (!tradePartner.hasBalance(price)) {
			if (shopUnlimitedMoney && tradePartner.equals(em.getGlobalShopAccount())) {
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
			if (hp == null || hyperItem == null) {
				response.setFailed();
				response.addFailed(L.get("TRANSACTION_FAILED"), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			CommonFunctions cf = hc.gCF();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String name = hyperItem.getName();
			int id = hyperItem.getId();
			if (receiveInventory == null) {
				receiveInventory = hp.getPlayer().getInventory();
			}
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperItem.getName()), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.SELL) {
				response.addFailed(L.f(L.get("SELL_ONLY_ITEM"), hyperItem.getName()), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (amount == 0) {
				response.addFailed(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), name), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			double shopstock = hyperItem.getStock();
			if (shopstock < amount) {
				response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), name), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (id < 0) {
				response.addFailed(L.f(L.get("CANNOT_BE_PURCHASED_WITH"), name), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			double price = hyperItem.getCost(amount);
			double taxpaid = hyperItem.getPurchaseTax(price);
			price = cf.twoDecimals(price + taxpaid);
			if (!hp.hasBalance(price)) {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			int space = hyperItem.getAvailableSpace(receiveInventory);
			if (space < amount) {
				response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, name), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			hyperItem.add(amount, receiveInventory);
			if (!Boolean.parseBoolean(hyperItem.getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
				hyperItem.setStock(shopstock - amount);
			}
			hp.withdraw(price);
			tradePartner.deposit(price);
			resetBalanceIfUnlimited();
			response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, price, name, cf.twoDecimals(taxpaid)), price, hyperItem);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperItem.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperItem.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(hp.getName(), "purchase", name, (double) amount, cf.twoDecimals(price - taxpaid), cf.twoDecimals(taxpaid), tradePartner.getName(), type);
			isign.updateSigns();
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction buy() passed values name='" + hyperItem.getName() + "', player='" + hp.getName() + "', id='" + hyperItem.getId() + "', data='" + hyperItem.getData() + "', amount='" + amount + "'";
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
			if (hp == null || hyperItem == null) {
				response.setFailed();
				response.addFailed(L.get("TRANSACTION_FAILED"), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			CommonFunctions cf = hc.gCF();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			InfoSignHandler isign = hc.getInfoSignHandler();
			int id = hyperItem.getId();
			String name = hyperItem.getName();
			if (giveInventory == null) {
				giveInventory = hp.getPlayer().getInventory();
			}
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperItem.getName()), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.BUY) {
				response.addFailed(L.f(L.get("BUY_ONLY_ITEM"), hyperItem.getName()), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), name), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (id < 0) {
				response.addFailed(L.f(L.get("CANNOT_BE_SOLD_WITH"), name), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			int totalitems = hyperItem.count(giveInventory);
			if (totalitems < amount) {
				boolean sellRemaining = hc.gYH().gFC("config").getBoolean("config.sell-remaining-if-less-than-requested-amount");
				if (sellRemaining) {
					amount = totalitems;
				} else {
					response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), hyperItem);
					heh.fireTransactionEvent(pt, response);
					return response;
				}
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			double price = hyperItem.getValue(amount, hp);
			int maxi = hyperItem.getMaxInitial();
			boolean isstatic = false;
			boolean isinitial = false;
			isinitial = Boolean.parseBoolean(hyperItem.getInitiation());
			isstatic = Boolean.parseBoolean(hyperItem.getIsstatic());
			if ((amount > maxi) && !isstatic && isinitial) {
				amount = maxi;
				price = hyperItem.getValue(amount, hp);
			}
			if (!hasBalance(price)) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperItem);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (maxi == 0) {
				price = hyperItem.getValue(amount, hp);
			}
			double amountRemoved = hyperItem.remove(amount, giveInventory);
			double shopstock = hyperItem.getStock();
			if (!Boolean.parseBoolean(hyperItem.getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
				hyperItem.setStock(shopstock + amountRemoved);
			}
			int maxi2 = hyperItem.getMaxInitial();
			if (maxi2 == 0) {
				hyperItem.setInitiation("false");
			}
			double salestax = hp.getSalesTax(price);
			hp.deposit(price - salestax);
			tradePartner.withdraw(price - salestax);
			resetBalanceIfUnlimited();

			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, cf.twoDecimals(price), name, cf.twoDecimals(salestax)), cf.twoDecimals(price - salestax), hyperItem);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperItem.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperItem.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(hp.getName(), "sale", name, (double) amount, cf.twoDecimals(price - salestax), cf.twoDecimals(salestax), tradePartner.getName(), type);
			isign.updateSigns();
			heh.fireTransactionEvent(pt, response);
			return response;

		} catch (Exception e) {
			String info = "Transaction sell() passed values name='" + hyperItem.getName() + "', player='" + hp.getName() + "', id='" + hyperItem.getId() + "', data='" + hyperItem.getData() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}
	
	
	
	public TransactionResponse sellAll() {
		try {
			LanguageFile L = hc.getLanguageFile();
			TransactionResponse response = new TransactionResponse(hp);
			if (hp == null) {
				response.setFailed();
				response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			response.setSuccessful();
			HyperEconomy econ = em.getEconomy(hp.getEconomy());
			Inventory invent = null;
			if (giveInventory == null) {
				invent = hp.getPlayer().getInventory();
			} else {
				invent = giveInventory;
			}
			for (int slot = 0; slot < invent.getSize(); slot++) {
				if (invent.getItem(slot) != null) {
					ItemStack stack = invent.getItem(slot);
					hyperItem = econ.getHyperItem(stack, em.getShop(hp.getPlayer()));
					if (new HyperItemStack(stack).hasenchants() == false) {
						if (hyperItem != null) {
							if (em.getShop(hp.getPlayer()).has(hyperItem.getName())) {
								amount = hyperItem.count(hp.getInventory());
								pt.setHyperObject(hyperItem);
								TransactionResponse sresponse = sell();
								if (sresponse.successful()) {
									response.addSuccess(sresponse.getMessage(), sresponse.getPrice(), hyperItem);
								} else {
									response.addFailed(sresponse.getMessage(), hyperItem, stack);
								}
							} else {
								response.addFailed(L.get("CANT_BE_TRADED"), hyperItem, stack);
							}
						} else {
							response.addFailed(L.get("CANT_BE_TRADED"), hyperItem, stack);
						}
					} else {
						response.addFailed(L.get("CANT_BUY_SELL_ENCHANTED_ITEMS"), hyperItem, stack);
					}
				} 
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			hc.gDB().writeError(e);
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
			InfoSignHandler isign = hc.getInfoSignHandler();
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.SELL) {
				response.addFailed(L.f(L.get("SELL_ONLY_ITEM"), hyperObject.getName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (amount > 0) {
				int shopstock = 0;
				shopstock = (int) hyperObject.getStock();
				if (shopstock >= amount) {
					double price = xp.getCost(amount);
					double taxpaid = hyperObject.getPurchaseTax(price);
					price = cf.twoDecimals(price + taxpaid);
					if (hp.hasBalance(price)) {
						int totalxp = xp.getTotalXpPoints(hp.getPlayer());
						int newxp = totalxp + amount;
						int newlvl = xp.getLvlFromXP(newxp);
						newxp = newxp - xp.getLvlXpPoints(newlvl);
						float xpbarxp = (float) newxp / (float) xp.getXpForNextLvl(newlvl);
						hp.getPlayer().setLevel(newlvl);
						hp.getPlayer().setExp(xpbarxp);
						if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
							hyperObject.setStock(shopstock - amount);
						}
						hp.withdraw(price);
						tradePartner.deposit(price);
						resetBalanceIfUnlimited();
						response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getName(), cf.twoDecimals(taxpaid)), cf.twoDecimals(price), hyperObject);
						response.setSuccessful();
						String type = "dynamic";
						if (Boolean.parseBoolean(hyperObject.getInitiation())) {
							type = "initial";
						} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
							type = "static";
						}
						log.writeSQLLog(hp.getName(), "purchase", hp.getName(), (double) amount, cf.twoDecimals(price), cf.twoDecimals(taxpaid), tradePartner.getName(), type);
						isign.updateSigns();
					} else {
						response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
					}
				} else {
					response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), hyperObject.getName()), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), hyperObject.getName()), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction buyXP() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
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
			InfoSignHandler isign = hc.getInfoSignHandler();
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.BUY) {
				response.addFailed(L.f(L.get("BUY_ONLY_ITEM"), hyperObject.getName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (amount > 0) {
				int totalxp = xp.getTotalXpPoints(hp.getPlayer());
				if (totalxp >= amount) {
					double price = xp.getValue(amount);
					int maxi = hyperObject.getMaxInitial();
					boolean itax;
					boolean stax;
					itax = Boolean.parseBoolean(hyperObject.getInitiation());
					stax = Boolean.parseBoolean(hyperObject.getIsstatic());
					if (amount > (maxi) && !stax && itax) {
						amount = maxi;
						price = xp.getValue(amount);
					}
					if (hasBalance(price)) {
						if (maxi == 0) {
							price = xp.getValue(amount);
						}
						int newxp = totalxp - amount;
						int newlvl = xp.getLvlFromXP(newxp);
						newxp = newxp - xp.getLvlXpPoints(newlvl);
						float xpbarxp = (float) newxp / (float) xp.getXpForNextLvl(newlvl);
						hp.getPlayer().setLevel(newlvl);
						hp.getPlayer().setExp(xpbarxp);
						if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
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
						response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getName(), salestax), cf.twoDecimals(price), hyperObject);
						response.setSuccessful();
						String type = "dynamic";
						if (Boolean.parseBoolean(hyperObject.getInitiation())) {
							type = "initial";
						} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
							type = "static";
						}
						log.writeSQLLog(hp.getName(), "sale", hyperObject.getName(), (double) amount, cf.twoDecimals(price - salestax), cf.twoDecimals(salestax), tradePartner.getName(), type);

						isign.updateSigns();
					} else {
						response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
					}
				} else {
					response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), hyperObject.getName()), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), hyperObject.getName()), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction sellXP() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
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
				price = hyperItem.getValue(amount);
			}
			if (hp.hasBalance(price)) {
				int space = hyperItem.getAvailableSpace(hp.getPlayer().getInventory());
				if (space >= amount) {
					hyperItem.add(amount, hp.getPlayer().getInventory());
					hyperItem.remove(amount, giveInventory);
					hp.withdraw(price);
					tradePartner.deposit(price);
					response.addSuccess(L.f(L.get("PURCHASE_CHEST_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getName(), tradePartner.getName()), cf.twoDecimals(price), hyperObject);
					response.setSuccessful();
					log.writeSQLLog(hp.getName(), "purchase", hyperObject.getName(), (double) amount, cf.twoDecimals(price), 0.0, tradePartner.getName(), "chestshop");
					tradePartner.sendMessage(L.f(L.get("CHEST_BUY_NOTIFICATION"), amount, cf.twoDecimals(price), hyperObject.getName(), hp.getPlayer()));
				} else {
					response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, hyperObject.getName()), hyperObject);
				}
			} else {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction buyChest() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
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
				price = hyperItem.getValue(amount, hp);
			}
			hyperItem.remove(amount, hp.getPlayer().getInventory());
			hyperItem.add(amount, receiveInventory);
			hp.deposit(price);
			tradePartner.withdraw(price);
			response.addSuccess(L.f(L.get("SELL_CHEST_MESSAGE"), amount, cf.twoDecimals(price), hyperObject.getName(), tradePartner.getName()), cf.twoDecimals(price), hyperObject);
			response.setSuccessful();
			log.writeSQLLog(hp.getName(), "sale", hyperObject.getName(), (double) amount, cf.twoDecimals(price), 0.0, tradePartner.getName(), "chestshop");
			tradePartner.sendMessage(L.f(L.get("CHEST_SELL_NOTIFICATION"), amount, cf.twoDecimals(price), hyperObject.getName(), hp.getPlayer()));
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "Transaction sellChest() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
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
		InfoSignHandler isign = hc.getInfoSignHandler();
		try {
			String nenchant = "";
			Player p = hp.getPlayer();
			nenchant = hyperEnchant.getEnchantmentName();
			Enchantment ench = Enchantment.getByName(nenchant);
			int lvl = Integer.parseInt(hyperObject.getName().substring(hyperObject.getName().length() - 1, hyperObject.getName().length()));
			int truelvl = new HyperItemStack(p.getItemInHand()).getEnchantmentLevel(ench);
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.BUY) {
				response.addFailed(L.f(L.get("BUY_ONLY_ITEM"), hyperObject.getName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (new HyperItemStack(p.getItemInHand()).containsEnchantment(ench) && lvl == truelvl) {
				double dura = p.getItemInHand().getDurability();
				double maxdura = p.getItemInHand().getType().getMaxDurability();
				double duramult = (1 - dura / maxdura);
				if (p.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
					duramult = 1;
				}
				String mater = p.getItemInHand().getType().toString();
				double price = hyperEnchant.getValue(EnchantmentClass.fromString(mater), hp);
				double fprice = price;
				if (hasBalance(fprice)) {
					new HyperItemStack(p.getItemInHand()).removeEnchant(ench);
					double shopstock = hyperObject.getStock();
					hyperObject.setStock(shopstock + duramult);
					double salestax = hp.getSalesTax(fprice);
					hp.deposit(fprice - salestax);
					tradePartner.withdraw(fprice - salestax);
					resetBalanceIfUnlimited();
					fprice = cf.twoDecimals(fprice);
					response.addSuccess(L.f(L.get("ENCHANTMENT_SELL_MESSAGE"), 1, cf.twoDecimals(fprice), hyperObject.getName(), cf.twoDecimals(salestax)), cf.twoDecimals(fprice - salestax), hyperObject);
					response.setSuccessful();
					String type = "dynamic";
					if (Boolean.parseBoolean(hyperObject.getInitiation())) {
						type = "initial";
					} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
						type = "static";
					}
					log.writeSQLLog(p.getName(), "sale", hyperObject.getName(), 1.0, fprice - salestax, salestax, tradePartner.getName(), type);

					isign.updateSigns();
				} else {
					response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("ITEM_DOESNT_HAVE_ENCHANTMENT"), hyperObject.getName()), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "ETransaction sellEnchant() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "'";
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
		InfoSignHandler isign = hc.getInfoSignHandler();
		try {
			Player p = hp.getPlayer();
			String nenchant = hyperEnchant.getEnchantmentName();
			Enchantment ench = Enchantment.getByName(nenchant);
			int shopstock = 0;
			shopstock = (int) hyperObject.getStock();
			if (status == HyperObjectStatus.NONE) {
				response.addFailed(L.f(L.get("NO_TRADE_ITEM"), hyperObject.getName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			} else if (status == HyperObjectStatus.SELL) {
				response.addFailed(L.f(L.get("SELL_ONLY_ITEM"), hyperObject.getName()), hyperObject);
				heh.fireTransactionEvent(pt, response);
				return response;
			}
			if (shopstock >= 1) {
				String mater = p.getItemInHand().getType().toString();
				double price = hyperEnchant.getCost(EnchantmentClass.fromString(mater));
				price = price + hyperObject.getPurchaseTax(price);
				if (!new HyperItemStack(p.getItemInHand()).containsEnchantment(ench)) {
					if (hp.hasBalance(price)) {
						if (new HyperItemStack(p.getItemInHand()).canAcceptEnchantment(ench)) {
							hyperObject.setStock(shopstock - 1);
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
								taxrate = hc.gYH().gFC("config").getDouble("config.enchanttaxpercent");
							} else {
								taxrate = hc.gYH().gFC("config").getDouble("config.statictaxpercent");
							}
							double taxpaid = price - (price / (1 + taxrate / 100));
							taxpaid = cf.twoDecimals(taxpaid);
							price = cf.twoDecimals(price);
							response.addSuccess(L.f(L.get("ENCHANTMENT_PURCHASE_MESSAGE"), 1, price, hyperObject.getName(), cf.twoDecimals(taxpaid)), cf.twoDecimals(price), hyperObject);
							response.setSuccessful();
							String type = "dynamic";
							if (Boolean.parseBoolean(hyperObject.getInitiation())) {
								type = "initial";
							} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
								type = "static";
							}
							log.writeSQLLog(p.getName(), "purchase", hyperObject.getName(), 1.0, price, taxpaid, tradePartner.getName(), type);

							isign.updateSigns();
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
				response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), hyperObject.getName()), hyperObject);
			}
			heh.fireTransactionEvent(pt, response);
			return response;
		} catch (Exception e) {
			String info = "ETransaction buyEnchant() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "'";
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
			nenchant = hyperEnchant.getEnchantmentName();
			Enchantment ench = Enchantment.getByName(nenchant);
			String mater = p.getItemInHand().getType().toString();
			double price;
			if (setPrice) {
				price = money;
			} else {
				price = hyperEnchant.getValue(EnchantmentClass.fromString(mater), hp);
			}
			HyperItemStack his = new HyperItemStack(p.getItemInHand());
			if (!new HyperItemStack(p.getItemInHand()).containsEnchantment(ench)) {
				if (his.canAcceptEnchantment(ench) && p.getItemInHand().getAmount() == 1) {
					if (hp.hasBalance(price)) {
						hp.withdraw(price);
						tradePartner.deposit(price);
						int l = hyperObject.getName().length();
						String lev = hyperObject.getName().substring(l - 1, l);
						int level = Integer.parseInt(lev);
						his.addEnchantment(ench, level);
						new HyperItemStack(giveItem).removeEnchant(ench);
						price = cf.twoDecimals(price);
						response.addSuccess(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, cf.twoDecimals(price), hyperObject.getName(), tradePartner.getName()), cf.twoDecimals(price), hyperObject);
						response.setSuccessful();
						log.writeSQLLog(p.getName(), "purchase", hyperObject.getName(), 1.0, price, 0.0, tradePartner.getName(), "chestshop");
						tradePartner.sendMessage(L.f(L.get("CHEST_ENCHANTMENT_BUY_NOTIFICATION"), 1, cf.twoDecimals(price), hyperObject.getName(), p));
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
			String info = "ETransaction buyChestEnchant() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "'";
			hc.gDB().writeError(e, info);
			heh.fireTransactionEvent(pt, new TransactionResponse(hp));
			return new TransactionResponse(hp);
		}
	}


	
	
	

	
	
	

	
	
	
	
}
