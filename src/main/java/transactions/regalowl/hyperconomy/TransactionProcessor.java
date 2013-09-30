package regalowl.hyperconomy;


import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;



public class TransactionProcessor {

	private HyperConomy hc;
	private EconomyManager em;
	private LanguageFile L;
	private HyperPlayer hp;
	private InventoryManipulation im;
	
	private TransactionType transactionType;
	private HyperPlayer tradePartner;
	private HyperObject hyperObject;
	private int amount;
	private Inventory giveInventory;
	private Inventory receiveInventory;
	private double money;
	//private boolean chargeTax;
	private boolean setPrice;
	private ItemStack giveItem;
	
	
	
	
	TransactionProcessor(HyperPlayer hp) {
		hc = HyperConomy.hc;
		em = hc.getEconomyManager();
		L = hc.getLanguageFile();
		this.hp = hp;
		im = hc.getInventoryManipulation();
	}
	
	
	public TransactionResponse processTransaction(PlayerTransaction pt) {
		
		transactionType = pt.getTransactionType();
		hyperObject = pt.getHyperObject();
		tradePartner = pt.getTradePartner();
		amount = pt.getAmount();
		giveInventory = pt.getGiveInventory();
		receiveInventory = pt.getReceiveInventory();
		money = pt.getMoney();
		//chargeTax = pt.isChargeTax();
		setPrice = pt.isSetPrice();
		giveItem = pt.getGiveItem();
		

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
	
	
	
	
	
	
	
	
	public TransactionResponse buy() {
		try {
			TransactionResponse response = new TransactionResponse(hp);
			if (hp == null || hyperObject == null) {
				response.setFailed();
				response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
				return response;
			}
			Calculation calc = hc.getCalculation();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = hp.getEconomy();
			String name = hyperObject.getName();
			int id = hyperObject.getId();
			int data = hyperObject.getData();
			if (receiveInventory == null) {
				receiveInventory = hp.getPlayer().getInventory();
			}
			if (amount > 0) {
				double shopstock = hyperObject.getStock();
				if (shopstock >= amount) {
					if (id >= 0) {
						double price = hyperObject.getCost(amount);
						double taxpaid = hyperObject.getPurchaseTax(price);
						price = calc.twoDecimals(price + taxpaid);
						if (hp.hasBalance(price)) {
							int space = im.getAvailableSpace(id, data, receiveInventory);
							if (space >= amount) {
								im.addItems(amount, id, data, receiveInventory);
								if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
									hyperObject.setStock(shopstock - amount);
								}
								hp.withdraw(price);
								em.getGlobalShopAccount().deposit(price);
								if (hc.gYH().gFC("config").getBoolean("config.shop-has-unlimited-money")) {
									em.getGlobalShopAccount().setBalance(0);
								}
								
								response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, price, name, calc.twoDecimals(taxpaid)), price, hyperObject);
								response.setSuccessful();
								String type = "dynamic";
								if (Boolean.parseBoolean(hyperObject.getInitiation())) {
									type = "initial";
								} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
									type = "static";
								}
								log.writeSQLLog(hp.getName(), "purchase", name, (double) amount, calc.twoDecimals(price - taxpaid), calc.twoDecimals(taxpaid), playerecon, type);

								isign.updateSigns();
								not.setNotify(name, null, playerecon);
								not.sendNotification();
								return response;
							} else {
								response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, name), hyperObject);
								return response;
							}
						} else {
							response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
							return response;
						}
					} else {
						response.addFailed(L.f(L.get("CANNOT_BE_PURCHASED_WITH"), name), hyperObject);
						return response;
					}
				} else {
					response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), name), hyperObject);
					return response;
				}
			} else {
				response.addFailed(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), name), hyperObject);
				return response;
			}
		} catch (Exception e) {
			String info = "Transaction buy() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', id='" + hyperObject.getId() + "', data='" + hyperObject.getData() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
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
				return response;
			}
			Calculation calc = hc.getCalculation();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = hp.getEconomy();
			int id = hyperObject.getId();
			int data = hyperObject.getData();
			String name = hyperObject.getName();
			if (giveInventory == null) {
				giveInventory = hp.getPlayer().getInventory();
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), name), hyperObject);
				return response;
			}
			if (id < 0) {
				response.addFailed(L.f(L.get("CANNOT_BE_SOLD_WITH"), name), hyperObject);
				return response;
			}
			int totalitems = im.countItems(id, data, giveInventory);
			if (totalitems < amount) {
				boolean sellRemaining = hc.gYH().gFC("config").getBoolean("config.sell-remaining-if-less-than-requested-amount");
				if (sellRemaining) {
					amount = totalitems;
				} else {
					response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), hyperObject);
					return response;
				}
			}
			if (amount <= 0) {
				response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name), hyperObject);
				return response;
			}
			double price = hyperObject.getValue(amount, hp);
			if (price == 3235624645000.7) {
				response.addFailed(L.f(L.get("CURRENTLY_CANT_SELL_MORE_THAN"), hyperObject.getStock(), name), hyperObject);
				return response;
			}
			int maxi = hyperObject.getMaxInitial();
			boolean isstatic = false;
			boolean isinitial = false;
			isinitial = Boolean.parseBoolean(hyperObject.getInitiation());
			isstatic = Boolean.parseBoolean(hyperObject.getIsstatic());
			if ((amount > maxi) && !isstatic && isinitial) {
				amount = maxi;
				price = hyperObject.getValue(amount, hp);
			}
			boolean sunlimited = hc.gYH().gFC("config").getBoolean("config.shop-has-unlimited-money");
			if (!em.getGlobalShopAccount().hasBalance(price) && !sunlimited) {
				response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				return response;
			}
			if (maxi == 0) {
				price = hyperObject.getValue(amount, hp);
			}
			double amountRemoved = im.removeItems(id, data, amount, giveInventory);
			double shopstock = 0;
			shopstock = hyperObject.getStock();
			if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
				hyperObject.setStock(shopstock + amountRemoved);
			}
			int maxi2 = hyperObject.getMaxInitial();
			if (maxi2 == 0) {
				hyperObject.setInitiation("false");
			}
			double salestax = hp.getSalesTax(price);
			hp.deposit(price - salestax);
			em.getGlobalShopAccount().withdraw(price - salestax);
			if (sunlimited) {
				em.getGlobalShopAccount().setBalance(0);
			}

			response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, calc.twoDecimals(price), name, calc.twoDecimals(salestax)), calc.twoDecimals(price - salestax), hyperObject);
			response.setSuccessful();
			String type = "dynamic";
			if (Boolean.parseBoolean(hyperObject.getInitiation())) {
				type = "initial";
			} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
				type = "static";
			}
			log.writeSQLLog(hp.getName(), "sale", name, (double) amount, calc.twoDecimals(price - salestax), calc.twoDecimals(salestax), playerecon, type);
			isign.updateSigns();
			not.setNotify(name, null, playerecon);
			not.sendNotification();
			return response;

		} catch (Exception e) {
			String info = "Transaction sell() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', id='" + hyperObject.getId() + "', data='" + hyperObject.getData() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
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
				return response;
			}
			response.setSuccessful();
			HyperEconomy econ = em.getEconomy(hp.getEconomy());
			Inventory invent = null;
			int id = 0;
			if (giveInventory == null) {
				invent = hp.getPlayer().getInventory();
			} else {
				invent = giveInventory;
			}
			for (int slot = 0; slot < invent.getSize(); slot++) {
				if (invent.getItem(slot) != null) {
					id = invent.getItem(slot).getTypeId();
					ItemStack stack = invent.getItem(slot);
					int da = im.getDamageValue(invent.getItem(slot));
					hyperObject = econ.getHyperObject(id, da, econ.getShop(hp.getPlayer()));
					if (im.hasenchants(stack) == false) {
						if (hyperObject != null) {
							if (econ.getShop(hp.getPlayer()).has(hyperObject.getName())) {
								amount = im.countItems(id, da, hp.getInventory());
								TransactionResponse sresponse = sell();
								if (sresponse.successful()) {
									response.addSuccess(sresponse.getMessage(), sresponse.getPrice(), hyperObject);
								} else {
									response.addFailed(sresponse.getMessage(), hyperObject, stack);
								}
							} else {
								response.addFailed(L.get("CANT_BE_TRADED"), hyperObject, stack);
							}
						} else {
							response.addFailed(L.get("CANT_BE_TRADED"), hyperObject, stack);
						}
					} else {
						response.addFailed(L.get("CANT_BUY_SELL_ENCHANTED_ITEMS"), hyperObject, stack);
					}
				} 
			}
			return response;
		} catch (Exception e) {
			hc.gDB().writeError(e);
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
			return response;
		}
		try {
			Calculation calc = hc.getCalculation();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = hp.getEconomy();
			if (amount > 0) {
				int shopstock = 0;
				shopstock = (int) hyperObject.getStock();
				if (shopstock >= amount) {
					double price = hyperObject.getCost(amount);
					double taxpaid = hyperObject.getPurchaseTax(price);
					price = calc.twoDecimals(price + taxpaid);
					if (hp.hasBalance(price)) {
						int totalxp = im.gettotalxpPoints(hp.getPlayer());
						int newxp = totalxp + amount;
						int newlvl = im.getlvlfromXP(newxp);
						newxp = newxp - im.getlvlxpPoints(newlvl);
						float xpbarxp = (float) newxp / (float) im.getxpfornextLvl(newlvl);
						hp.getPlayer().setLevel(newlvl);
						hp.getPlayer().setExp(xpbarxp);
						if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
							hyperObject.setStock(shopstock - amount);
						}
						hp.withdraw(price);
						em.getGlobalShopAccount().deposit(price);
						if (hc.gYH().gFC("config").getBoolean("config.shop-has-unlimited-money")) {
							em.getGlobalShopAccount().setBalance(0);
						}
						response.addSuccess(L.f(L.get("PURCHASE_MESSAGE"), amount, calc.twoDecimals(price), hyperObject.getName(), calc.twoDecimals(taxpaid)), calc.twoDecimals(price), hyperObject);
						response.setSuccessful();
						String type = "dynamic";
						if (Boolean.parseBoolean(hyperObject.getInitiation())) {
							type = "initial";
						} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
							type = "static";
						}
						log.writeSQLLog(hp.getName(), "purchase", hp.getName(), (double) amount, calc.twoDecimals(price), calc.twoDecimals(taxpaid), playerecon, type);
						isign.updateSigns();
						not.setNotify(hyperObject.getName(), null, playerecon);
						not.sendNotification();
					} else {
						response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
					}
				} else {
					response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), hyperObject.getName()), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), hyperObject.getName()), hyperObject);
			}
			return response;
		} catch (Exception e) {
			String info = "Transaction buyXP() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
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
			return response;
		}
		try {
			Calculation calc = hc.getCalculation();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = hp.getEconomy();
			if (amount > 0) {
				int totalxp = im.gettotalxpPoints(hp.getPlayer());
				if (totalxp >= amount) {
					double price = hyperObject.getValue(amount, hp);
					Boolean toomuch = false;
					if (price == 3235624645000.7) {
						toomuch = true;
					}
					if (!toomuch) {
						int maxi = hyperObject.getMaxInitial();
						boolean itax;
						boolean stax;
						itax = Boolean.parseBoolean(hyperObject.getInitiation());
						stax = Boolean.parseBoolean(hyperObject.getIsstatic());
						if (amount > (maxi) && !stax && itax) {
							amount = maxi;
							price = hyperObject.getValue(amount, hp);
						}
						boolean sunlimited = hc.gYH().gFC("config").getBoolean("config.shop-has-unlimited-money");
						if (em.getGlobalShopAccount().hasBalance(price) || sunlimited) {
							if (maxi == 0) {
								price = hyperObject.getValue(amount, hp);
							}
							int newxp = totalxp - amount;
							int newlvl = im.getlvlfromXP(newxp);
							newxp = newxp - im.getlvlxpPoints(newlvl);
							float xpbarxp = (float) newxp / (float) im.getxpfornextLvl(newlvl);
							hp.getPlayer().setLevel(newlvl);
							hp.getPlayer().setExp(xpbarxp);
							if (!Boolean.parseBoolean(hyperObject.getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
								hyperObject.setStock(amount + hyperObject.getStock());
							}
							int maxi2 = hyperObject.getMaxInitial();
							if (maxi2 == 0) {
								hyperObject.setInitiation("false");
							}
							double salestax = calc.twoDecimals(hp.getSalesTax(price));
							hp.deposit(price - salestax);
							em.getGlobalShopAccount().withdraw(price - salestax);
							if (sunlimited) {
								em.getGlobalShopAccount().setBalance(0);
							}
							response.addSuccess(L.f(L.get("SELL_MESSAGE"), amount, calc.twoDecimals(price), hyperObject.getName(), salestax), calc.twoDecimals(price), hyperObject);
							response.setSuccessful();
							String type = "dynamic";
							if (Boolean.parseBoolean(hyperObject.getInitiation())) {
								type = "initial";
							} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
								type = "static";
							}
							log.writeSQLLog(hp.getName(), "sale", hyperObject.getName(), (double) amount, calc.twoDecimals(price - salestax), calc.twoDecimals(salestax), playerecon, type);

							isign.updateSigns();
							not.setNotify(hyperObject.getName(), null, playerecon);
							not.sendNotification();
						} else {
							response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
						}
					} else {
						response.addFailed(L.f(L.get("CURRENTLY_CANT_SELL_MORE_THAN"), hyperObject.getStock(), hyperObject.getName()), hyperObject);
					}
				} else {
					response.addFailed(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), hyperObject.getName()), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), hyperObject.getName()), hyperObject);
			}
			return response;
		} catch (Exception e) {
			String info = "Transaction sellXP() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			return new TransactionResponse(hp);
		}
	}


	
	
	public TransactionResponse buyFromInventory() {
		TransactionResponse response = new TransactionResponse(hp);
		if (hp == null || tradePartner == null || hyperObject == null) {
			response.setFailed();
			response.addFailed(L.get("TRANSACTION_FAILED"), hyperObject);
			return response;
		}
		try {
			Calculation calc = hc.getCalculation();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			double price = 0.0;
			if (setPrice) {
				price = money;
			} else {
				price = hyperObject.getValue(amount);
			}
			if (hp.hasBalance(price)) {
				int space = im.getAvailableSpace(hyperObject.getId(), hyperObject.getData(), hp.getPlayer().getInventory());
				if (space >= amount) {
					im.addItems(amount, hyperObject.getId(), hyperObject.getData(), hp.getPlayer().getInventory());
					im.removeItems(hyperObject.getId(), hyperObject.getData(), amount, giveInventory);
					hp.withdraw(price);
					tradePartner.deposit(price);
					response.addSuccess(L.f(L.get("PURCHASE_CHEST_MESSAGE"), amount, calc.twoDecimals(price), hyperObject.getName(), tradePartner.getName()), calc.twoDecimals(price), hyperObject);
					response.setSuccessful();
					log.writeSQLLog(hp.getName(), "purchase", hyperObject.getName(), (double) amount, calc.twoDecimals(price), 0.0, tradePartner.getName(), "chestshop");
					tradePartner.sendMessage(L.f(L.get("CHEST_BUY_NOTIFICATION"), amount, calc.twoDecimals(price), hyperObject.getName(), hp.getPlayer()));
				} else {
					response.addFailed(L.f(L.get("ONLY_ROOM_TO_BUY"), space, hyperObject.getName()), hyperObject);
				}
			} else {
				response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
			}
			return response;
		} catch (Exception e) {
			String info = "Transaction buyChest() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
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
			return response;
		}
		try {
			Calculation calc = hc.getCalculation();
			Log log = hc.getLog();
			LanguageFile L = hc.getLanguageFile();
			double price = 0.0;
			if (setPrice) {
				price = money;
			} else {
				price = hyperObject.getValue(amount, hp);
			}
			Boolean toomuch = false;
			if (price == 3235624645000.7) {
				toomuch = true;
			}
			if (!toomuch) {
				im.removeItems(hyperObject.getId(), hyperObject.getData(), amount, hp.getPlayer().getInventory());
				im.addItems(amount, hyperObject.getId(), hyperObject.getData(), receiveInventory);
				hp.deposit(price);
				tradePartner.withdraw(price);
				response.addSuccess(L.f(L.get("SELL_CHEST_MESSAGE"), amount, calc.twoDecimals(price), hyperObject.getName(), tradePartner.getName()), calc.twoDecimals(price), hyperObject);
				response.setSuccessful();
				log.writeSQLLog(hp.getName(), "sale", hyperObject.getName(), (double) amount, calc.twoDecimals(price), 0.0, tradePartner.getName(), "chestshop");
				tradePartner.sendMessage(L.f(L.get("CHEST_SELL_NOTIFICATION"), amount, calc.twoDecimals(price), hyperObject.getName(), hp.getPlayer()));
			} else {
				response.addFailed(L.f(L.get("CURRENTLY_CANT_SELL_MORE_THAN"), hyperObject.getStock(), hyperObject.getName()), hyperObject);
			}
			return response;
		} catch (Exception e) {
			String info = "Transaction sellChest() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
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
			return response;
		}
		Calculation calc = hc.getCalculation();
		Log log = hc.getLog();
		Notification not = hc.getNotify();
		InfoSignHandler isign = hc.getInfoSignHandler();
		try {
			String nenchant = "";
			String playerecon = hp.getEconomy();
			Player p = hp.getPlayer();
			nenchant = hyperObject.getMaterial();
			Enchantment ench = Enchantment.getByName(nenchant);
			int lvl = Integer.parseInt(hyperObject.getName().substring(hyperObject.getName().length() - 1, hyperObject.getName().length()));
			int truelvl = im.getEnchantmentLevel(p.getItemInHand(), ench);
			if (im.containsEnchantment(p.getItemInHand(), ench) && lvl == truelvl) {
				double dura = p.getItemInHand().getDurability();
				double maxdura = p.getItemInHand().getType().getMaxDurability();
				double duramult = (1 - dura / maxdura);
				if (p.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
					duramult = 1;
				}
				String mater = p.getItemInHand().getType().toString();
				double price = hyperObject.getValue(EnchantmentClass.fromString(mater), hp);
				double fprice = price;
				boolean sunlimited = hc.gYH().gFC("config").getBoolean("config.shop-has-unlimited-money");
				if (em.getGlobalShopAccount().hasBalance(fprice) || sunlimited) {
					im.removeEnchantment(p.getItemInHand(), ench);
					double shopstock = hyperObject.getStock();
					hyperObject.setStock(shopstock + duramult);
					double salestax = hp.getSalesTax(fprice);
					hp.deposit(fprice - salestax);
					em.getGlobalShopAccount().withdraw(fprice - salestax);
					if (sunlimited) {
						em.getGlobalShopAccount().setBalance(0);
					}
					fprice = calc.twoDecimals(fprice);
					response.addSuccess(L.f(L.get("ENCHANTMENT_SELL_MESSAGE"), 1, calc.twoDecimals(fprice), hyperObject.getName(), calc.twoDecimals(salestax)), calc.twoDecimals(fprice - salestax), hyperObject);
					response.setSuccessful();
					String type = "dynamic";
					if (Boolean.parseBoolean(hyperObject.getInitiation())) {
						type = "initial";
					} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
						type = "static";
					}
					log.writeSQLLog(p.getName(), "sale", hyperObject.getName(), 1.0, fprice - salestax, salestax, playerecon, type);

					isign.updateSigns();
					not.setNotify(hyperObject.getName(), mater, playerecon);
					not.sendNotification();
				} else {
					response.addFailed(L.get("SHOP_NOT_ENOUGH_MONEY"), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("ITEM_DOESNT_HAVE_ENCHANTMENT"), hyperObject.getName()), hyperObject);
			}
			return response;
		} catch (Exception e) {
			String info = "ETransaction sellEnchant() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "'";
			hc.gDB().writeError(e, info);
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
			return response;
		}
		Calculation calc = hc.getCalculation();
		Log log = hc.getLog();
		Notification not = hc.getNotify();
		InfoSignHandler isign = hc.getInfoSignHandler();
		try {
			String playerecon = hp.getEconomy();
			Player p = hp.getPlayer();
			String nenchant = hyperObject.getMaterial();
			Enchantment ench = Enchantment.getByName(nenchant);
			int shopstock = 0;
			shopstock = (int) hyperObject.getStock();
			if (shopstock >= 1) {
				String mater = p.getItemInHand().getType().toString();
				double price = hyperObject.getCost(EnchantmentClass.fromString(mater));
				price = price + hyperObject.getPurchaseTax(price);
				if (price != 123456789) {
					if (!im.containsEnchantment(p.getItemInHand(), ench)) {
						if (hp.hasBalance(price)) {
							if (im.canAcceptEnchantment(p.getItemInHand(), ench) && p.getItemInHand().getAmount() == 1) {
								hyperObject.setStock(shopstock - 1);
								hp.withdraw(price);
								em.getGlobalShopAccount().deposit(price);
								if (hc.gYH().gFC("config").getBoolean("config.shop-has-unlimited-money")) {
									em.getGlobalShopAccount().setBalance(0);
								}
								int l = hyperObject.getName().length();
								String lev = hyperObject.getName().substring(l - 1, l);
								int level = Integer.parseInt(lev);
								im.addEnchantment(p.getItemInHand(), ench, level);
								boolean stax;
								stax = Boolean.parseBoolean(hyperObject.getIsstatic());
								double taxrate;
								if (!stax) {
									taxrate = hc.gYH().gFC("config").getDouble("config.enchanttaxpercent");
								} else {
									taxrate = hc.gYH().gFC("config").getDouble("config.statictaxpercent");
								}
								double taxpaid = price - (price / (1 + taxrate / 100));
								taxpaid = calc.twoDecimals(taxpaid);
								price = calc.twoDecimals(price);
								response.addSuccess(L.f(L.get("ENCHANTMENT_PURCHASE_MESSAGE"), 1, price, hyperObject.getName(), calc.twoDecimals(taxpaid)), calc.twoDecimals(price), hyperObject);
								response.setSuccessful();
								String type = "dynamic";
								if (Boolean.parseBoolean(hyperObject.getInitiation())) {
									type = "initial";
								} else if (Boolean.parseBoolean(hyperObject.getIsstatic())) {
									type = "static";
								}
								log.writeSQLLog(p.getName(), "purchase", hyperObject.getName(), 1.0, price, taxpaid, playerecon, type);

								isign.updateSigns();
								not.setNotify(hyperObject.getName(), mater, playerecon);
								not.sendNotification();
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
					response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), hyperObject);
				}
			} else {
				response.addFailed(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), hyperObject.getName()), hyperObject);
			}
			return response;
		} catch (Exception e) {
			String info = "ETransaction buyEnchant() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "'";
			hc.gDB().writeError(e, info);
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
			return response;
		}
		Calculation calc = hc.getCalculation();
		Log log = hc.getLog();
		try {
			Player p = hp.getPlayer();
			String nenchant = "";
			nenchant = hyperObject.getMaterial();
			Enchantment ench = Enchantment.getByName(nenchant);
			String mater = p.getItemInHand().getType().toString();
			double price;
			if (setPrice) {
				price = money;
			} else {
				price = hyperObject.getValue(EnchantmentClass.fromString(mater), hp);
			}
			if (price != 123456789) {
				if (!im.containsEnchantment(p.getItemInHand(), ench)) {
					if (im.canAcceptEnchantment(p.getItemInHand(), ench) && p.getItemInHand().getAmount() == 1) {
						if (hp.hasBalance(price)) {
							hp.withdraw(price);
							tradePartner.deposit(price);
							int l = hyperObject.getName().length();
							String lev = hyperObject.getName().substring(l - 1, l);
							int level = Integer.parseInt(lev);
							im.addEnchantment(p.getItemInHand(), ench, level);
							im.removeEnchantment(giveItem, ench);
							price = calc.twoDecimals(price);
							response.addSuccess(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, calc.twoDecimals(price), hyperObject.getName(), tradePartner.getName()), calc.twoDecimals(price), hyperObject);
							response.setSuccessful();
							log.writeSQLLog(p.getName(), "purchase", hyperObject.getName(), 1.0, price, 0.0, tradePartner.getName(), "chestshop");
							tradePartner.sendMessage(L.f(L.get("CHEST_ENCHANTMENT_BUY_NOTIFICATION"), 1, calc.twoDecimals(price), hyperObject.getName(), p));
						} else {
							response.addFailed(L.get("INSUFFICIENT_FUNDS"), hyperObject);
						}
					} else {
						response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), hyperObject);
					}
				} else {
					response.addFailed(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"), hyperObject);
				}
			} else {
				response.addFailed(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"), hyperObject);
			}
			return response;
		} catch (Exception e) {
			String info = "ETransaction buyChestEnchant() passed values name='" + hyperObject.getName() + "', player='" + hp.getName() + "', owner='" + tradePartner.getName() + "'";
			hc.gDB().writeError(e, info);
			return new TransactionResponse(hp);
		}
	}


	
	
	

	
	
	

	
	
	
	
}
