package regalowl.hyperconomy.command;


import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.tradeobject.CompositeTradeItem;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.util.Backup;




public class Hcset extends BaseCommand implements HyperCommand {
	
	public Hcset(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			HyperEconomy he = getEconomy();
			if (args.length == 0) {
				data.addResponse(L.get("HCSET_INVALID"));
				return data;
			}
			
			if (args[0].equalsIgnoreCase("name")) {
				try {
					String name = args[1];
					String newName = args[2];
					if (he.objectTest(name)) {
						he.getTradeObject(name).setName(newName);
						data.addResponse(L.get("NAME_SET"));
						hc.restart();
					} else {
						data.addResponse(L.get("INVALID_NAME"));
					}
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_NAME_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("ceiling") || args[0].equalsIgnoreCase("c")) {
				try {
					String name = args[1];
					double ceiling = Double.parseDouble(args[2]);
					if (he.objectTest(name)) {
						he.getTradeObject(name).setCeiling(ceiling);
						data.addResponse(L.f(L.get("CEILING_SET"), name));
					} else {
						data.addResponse(L.get("INVALID_NAME"));
					}
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_CEILING_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("displayname") || args[0].equalsIgnoreCase("dn")) {
				try {
					String name = args[1];
					String newName = args[2].replace("_", " ");
					if (he.objectTest(name)) {
						TradeObject ho = he.getTradeObject(name);
						TradeObject to = he.getTradeObject(newName);
						if (to != null && !(ho.equals(to))) {
							data.addResponse(L.get("NAME_IN_USE"));
							return data;
						}
						ho.setDisplayName(newName);
						data.addResponse(L.f(L.get("DISPLAYNAME_SET"), newName));
					} else {
						data.addResponse(L.get("INVALID_NAME"));
					}
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_DISPLAYNAME_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("static") || args[0].equalsIgnoreCase("stat")) {
				try {
					String name = args[1];
					if (name.equalsIgnoreCase("all:copy") || name.equalsIgnoreCase("all:true") || name.equalsIgnoreCase("all:false")) {
						if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
						boolean state = false;
						boolean copy = false;
						String message = "";
						if (name.equalsIgnoreCase("all:copy")) {
							copy = true;
							state = true;
							message = "true + dynamic prices copied";
						} else if (name.equalsIgnoreCase("all:false")) {
							state = false;
							message = "false";
						} else if (name.equalsIgnoreCase("all:true")) {
							state = true;
							message = "true";
						}
						ArrayList<TradeObject> hyperObjects = he.getTradeObjects();
						for (TradeObject ho:hyperObjects) {
							if (ho instanceof CompositeTradeItem) {continue;}
							if (copy) {
								ho.setStaticPrice(ho.getStartPrice());
							}
							ho.setStatic(state);
						}
						data.addResponse(L.f(L.get("ALL_OBJECTS_SET_TO_STATIC"), message));
						return data;
					}
					TradeObject ho = he.getTradeObject(name);
					if (ho == null) {
						data.addResponse(L.get("INVALID_NAME"));
						return data;
					}
					boolean isStatic = ho.isStatic();
					if (isStatic) {
						ho.setStatic(false);
						data.addResponse(L.get("USE_DYNAMIC_PRICE"));
					} else {
						ho.setStatic(true);
						data.addResponse(L.get("USE_STATIC_PRICE"));
					}
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_STATIC_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("stock") || args[0].equalsIgnoreCase("s")) {
				try {
					String name = args[1];
					Double stock = 0.0;
					if (!name.equalsIgnoreCase("all:median")) {
						stock = Double.parseDouble(args[2]);
					}
					if (name.equalsIgnoreCase("all")) {
						if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
						ArrayList<TradeObject> hyperObjects = he.getTradeObjects();
						for (TradeObject ho:hyperObjects) {
							if (ho instanceof CompositeTradeItem) {continue;}
							ho.setStock(stock);
						}
						data.addResponse(L.get("ALL_STOCKS_SET"));
						return data;
					} else if (name.equalsIgnoreCase("all:median")) {
						if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
						for (TradeObject ho:he.getTradeObjects()) {
							if ((ho instanceof CompositeTradeItem)) {continue;}
							ho.setStock(ho.getMedian());
							ho.setUseInitialPricing(false);
						}
						data.addResponse(L.get("SETSTOCKMEDIANALL_SUCCESS"));
						return data;
					}
					TradeObject ho = he.getTradeObject(name);
					if (ho == null) {
						data.addResponse(L.get("INVALID_NAME"));
						return data;
					}
					ho.setStock(stock);
					data.addResponse(L.f(L.get("STOCK_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_STOCK_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("startprice") || args[0].equalsIgnoreCase("starp")) {
				try {
					String name = args[1];
					TradeObject ho = he.getTradeObject(name);
					if (ho == null) {
						data.addResponse(L.get("INVALID_NAME"));
						return data;
					}
					Double price = Double.parseDouble(args[2]);
					ho.setStartPrice(price);
					data.addResponse(L.f(L.get("START_PRICE_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_STARTPRICE_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("staticprice") || args[0].equalsIgnoreCase("statp")) {
				try {
					String name = args[1];
					TradeObject ho = he.getTradeObject(name);
					if (ho == null) {
						data.addResponse(L.get("INVALID_NAME"));
						return data;
					}
					Double price = Double.parseDouble(args[2]);
					ho.setStaticPrice(price);
					data.addResponse(L.f(L.get("STATIC_PRICE_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_STATICPRICE_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("value") || args[0].equalsIgnoreCase("v")) {
				try {
					String name = args[1];
					TradeObject ho = he.getTradeObject(name);
					if (ho == null) {
						data.addResponse(L.get("INVALID_NAME"));
						return data;
					}
					Double value = Double.parseDouble(args[2]);
					ho.setValue(value);
					data.addResponse(L.f(L.get("VALUE_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_VALUE_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("median") || args[0].equalsIgnoreCase("m")) {
				try {
					String name = args[1];
					TradeObject ho = he.getTradeObject(name);
					if (ho == null) {
						data.addResponse(L.get("INVALID_NAME"));
						return data;
					}
					Double median = Double.parseDouble(args[2]);
					ho.setMedian(median);
					data.addResponse(L.f(L.get("MEDIAN_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_MEDIAN_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("floor") || args[0].equalsIgnoreCase("f")) {
				try {
					String name = args[1];
					TradeObject ho = he.getTradeObject(name);
					if (ho == null) {
						data.addResponse(L.get("INVALID_NAME"));
						return data;
					}
					Double floor = Double.parseDouble(args[2]);
					ho.setFloor(floor);
					data.addResponse(L.f(L.get("FLOOR_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_FLOOR_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("category") || args[0].equalsIgnoreCase("cat")) {
				try {
					String name = args[1];
					TradeObject ho = he.getTradeObject(name);
					if (ho == null) {
						data.addResponse(L.get("INVALID_NAME"));
						return data;
					}
					String category = args[2];
					if (ho.inCategory(category)) {
						ho.removeCategory(category);
						data.addResponse(L.get("CATEGORY_REMOVED"));
					} else {
						ho.addCategory(category);
						data.addResponse(L.get("CATEGORY_ADDED"));
					}
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_CATEGORY_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("b")) {
				try {
					String accountName = args[1];
					if (dm.accountExists(accountName)) {
						Double balance = Double.parseDouble(args[2]);
						dm.getAccount(accountName).setBalance(balance);
						data.addResponse(L.get("BALANCE_SET"));
					} else {
						data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
					}
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_BALANCE_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("initiation") || args[0].equalsIgnoreCase("init")) {
				try {
					String name = args[1];
					if (name.equalsIgnoreCase("all:true") || name.equalsIgnoreCase("all:false")) {
						if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {new Backup(hc);}
						boolean state = false;
						String message = "";
						if (name.equalsIgnoreCase("all:false")) {
							state = false;
							message = "false";
						} else if (name.equalsIgnoreCase("all:true")) {
							state = true;
							message = "true";
						}
						ArrayList<TradeObject> hyperObjects = he.getTradeObjects();
						for (TradeObject ho:hyperObjects) {
							if (ho instanceof CompositeTradeItem) {continue;}
							ho.setUseInitialPricing(state);
						}
						data.addResponse(L.f(L.get("ALL_OBJECTS_SET_TO"), message));
						return data;
					}
					TradeObject ho = he.getTradeObject(name);
					if (ho == null) {
						data.addResponse(L.get("INVALID_NAME"));
						return data;
					}
					boolean isInitial= ho.useInitialPricing();
					if (isInitial) {
						ho.setUseInitialPricing(false);
						data.addResponse(L.f(L.get("INITIATION_FALSE"), ho.getDisplayName()));
					} else {
						ho.setUseInitialPricing(true);
						data.addResponse(L.f(L.get("INITIATION_TRUE"), ho.getDisplayName()));
					}
				} catch (Exception e) {
					data.addResponse(L.get("HCSET_INITIATION_INVALID"));
				}
			} else {
				data.addResponse(L.get("HCSET_INVALID"));
				return data;
			}
			
			
		} catch (Exception e) {
			data.addResponse(L.get("HCSET_INVALID"));
		}
		return data;
	}
	
	

}
