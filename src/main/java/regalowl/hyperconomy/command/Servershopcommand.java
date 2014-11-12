package regalowl.hyperconomy.command;


import java.util.ArrayList;
import java.util.HashMap;






import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.HyperShopManager;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.ShopCreationEvent;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.GlobalShop;
import regalowl.hyperconomy.shop.ServerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class Servershopcommand extends BaseCommand implements HyperCommand {
	
	public Servershopcommand() {
		super(true);
	}

	private HashMap<HyperPlayer, Shop> currentShop = new HashMap<HyperPlayer, Shop>();
	

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperShopManager hsm = hc.getHyperShopManager();
		if (hsm.inAnyShop(hp)) {
			Shop s = hsm.getShop(hp);
			if (s instanceof ServerShop) {
				ServerShop ss = (ServerShop)s;
				if (hp.hasPermission("hyperconomy.admin")) {
					currentShop.put(hp, ss);
				}
			}
		}
		Shop css = null;
		if (currentShop.containsKey(hp)) {
			css = currentShop.get(hp);
		}

		if (args.length == 0) {
			data.addResponse(L.get("SERVERSHOP_INVALID"));
			if (css == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
			} else {
				data.addResponse(L.f(L.get("MANAGESHOP_HELP2"), css.getDisplayName()));
			}
			return data;
		}
		
		if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("s")) {
			try {
				if (!hsm.shopExists(args[1])) {
					data.addResponse(L.get("SHOP_NOT_EXIST"));
					return data;
				}
				Shop s = hsm.getShop(args[1]);
				if (!(s instanceof ServerShop || s instanceof GlobalShop)) {
					data.addResponse(L.get("ONLY_SERVER_SHOPS"));
					return data;
				}
				currentShop.put(hp, s);
				data.addResponse(L.get("SHOP_SELECTED"));
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_SELECT_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
			if (css == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			data.addResponse(L.f(L.get("MANAGESHOP_HELP2"), css.getDisplayName()));
			data.addResponse(L.f(L.get("MANAGESHOP_HELP3"), css.getName()) + " &b" + css.getOwner().getName());
			data.addResponse(L.f(L.get("SERVERSHOP_ECONOMY_INFO"), css.getEconomy()));
		} else if (args[0].equalsIgnoreCase("p1")) {
			try {
				String name = args[1].replace(".", "").replace(":", "");
				if (hsm.shopExists(name)) {
					hsm.getShop(name).setPoint1(hp.getLocation());
				} else {
					HLocation l = hp.getLocation();
					Shop shop = new ServerShop(name, hp.getEconomy(), hp.getHyperEconomy().getDefaultAccount(), l, l);
					hsm.addShop(shop);
					hc.getHyperEventHandler().fireEvent(new ShopCreationEvent(shop));
				}
				data.addResponse(L.get("P1_SET"));
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_P1_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("p2")) {
			try {
				String name = args[1].replace(".", "").replace(":", "");
				if (hsm.shopExists(name)) {
					hsm.getShop(name).setPoint2(hp.getLocation());
				} else {
					HLocation l = hp.getLocation();
					Shop shop = new ServerShop(name, hp.getEconomy(), hp.getHyperEconomy().getDefaultAccount(), l, l);
					hsm.addShop(shop);
					hc.getHyperEventHandler().fireEvent(new ShopCreationEvent(shop));
				}
				data.addResponse(L.get("P2_SET"));
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_P2_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("list")) {
			ArrayList<Shop> shops = hsm.getShops();
			String sList = "";
			for (Shop s:shops) {
				if (s instanceof ServerShop || s instanceof GlobalShop) {
					sList += s.getDisplayName() + ",";
				}
			}
			if (sList.length() > 0) {
				sList = sList.substring(0, sList.length() - 1);
			}
			String shoplist = sList.replace("_", " ");
			data.addResponse("&b" + shoplist);
		} else if (args[0].equalsIgnoreCase("owner") || args[0].equalsIgnoreCase("o")) {
			try {
				HyperAccount owner = null;
				if (dm.hyperPlayerExists(args[1])) {
					owner = dm.getHyperPlayer(args[1]);
				} else {
					if (dm.getHyperBankManager().hasBank(args[1])) {
						owner = dm.getHyperBankManager().getHyperBank(args[1]);
					} else {
						data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
					}
				}
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				css.setOwner(owner);
				data.addResponse(L.get("OWNER_SET"));
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_OWNER_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("removeshop")) {
			try {
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				css.deleteShop();
				data.addResponse(L.f(L.get("HAS_BEEN_REMOVED"), css.getName()));
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_REMOVE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("rename")) {
			try {
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				String newName = args[1].replace(".", "").replace(":", "");
				css.setName(newName);
				data.addResponse(L.get("SHOP_RENAMED"));
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_RENAME_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("m")) {
			try {
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				css.setMessage(args[1].replace("_", " "));
				data.addResponse(L.get("MESSAGE_SET"));
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_MESSAGE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("a")) {
			try {
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				if (args[1].equalsIgnoreCase("all")) {
					css.unBanAllObjects();
					data.addResponse("&6" + L.get("ALL_ITEMS_ADDED") + " " + css.getDisplayName());
					return data;
				}
				TradeObject ho = dm.getEconomy(css.getEconomy()).getHyperObject(args[1]);
				if (ho == null) {
					data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
					return data;
				}
				if (!css.isBanned(ho)) {
					data.addResponse(L.get("SHOP_ALREADY_HAS"));
					return data;
				}
				ArrayList<TradeObject> add = new ArrayList<TradeObject>();
				add.add(ho);
				css.unBanObjects(add);
				data.addResponse("&6" + ho.getDisplayName() + " " + L.get("ADDED_TO") + " " + css.getDisplayName());
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_ALLOW_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("b")) {
			try {
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				if (args[1].equalsIgnoreCase("all")) {
					css.banAllObjects();
					data.addResponse(L.f(L.get("ALL_REMOVED_FROM"), css.getDisplayName()));
					return data;
				}
				TradeObject ho = dm.getEconomy(css.getEconomy()).getHyperObject(args[1]);
				if (ho == null) {
					data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
					return data;
				}
				if (css.isBanned(ho)) {
					data.addResponse(L.get("ALREADY_BEEN_REMOVED"));
					return data;
				}
				ArrayList<TradeObject> remove = new ArrayList<TradeObject>();
				remove.add(ho);
				css.banObjects(remove);
				data.addResponse(L.f(L.get("REMOVED_FROM"), ho.getDisplayName(), css.getDisplayName()));
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_BAN_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("addcategory") || args[0].equalsIgnoreCase("acat")) {
			try {
				FileConfiguration category = hc.gYH().gFC("categories");
				String categoryString = category.getString(args[1]);
				if (categoryString == null) {
					data.addResponse(L.get("CATEGORY_NOT_EXIST"));
					return data;
				}
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				ArrayList<String> categoryNames = CommonFunctions.explode(categoryString, ",");
				HyperEconomy he = css.getHyperEconomy();
				ArrayList<TradeObject> add = new ArrayList<TradeObject>();
				for (String name:categoryNames) {
					TradeObject ho = he.getHyperObject(name);
					if (ho != null) {
						add.add(ho);
					}
				}
				css.unBanObjects(add);
				data.addResponse("&6" + args[1] + " " + L.get("ADDED_TO") + " " + css.getDisplayName());
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_ADDCATEGORY_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("removecategory") || args[0].equalsIgnoreCase("rcat")) {
			try {
				FileConfiguration category = hc.gYH().gFC("categories");
				String categoryString = category.getString(args[1]);
				if (categoryString == null) {
					data.addResponse(L.get("CATEGORY_NOT_EXIST"));
					return data;
				}
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				ArrayList<String> categoryNames = CommonFunctions.explode(categoryString, ",");
				HyperEconomy he = css.getHyperEconomy();
				ArrayList<TradeObject> remove = new ArrayList<TradeObject>();
				for (String name:categoryNames) {
					TradeObject ho = he.getHyperObject(name);
					if (ho != null) {
						remove.add(ho);
					}
				}
				css.banObjects(remove);
				data.addResponse(L.f(L.get("REMOVED_FROM"), args[1], css.getDisplayName()));
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_REMOVECATEGORY_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("economy") || args[0].equalsIgnoreCase("e")) {
			try {
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				String economy = args[1];
				if (HC.hc.getDataManager().economyExists(economy)) {
					css.setEconomy(economy);
					data.addResponse(L.get("SHOP_ECONOMY_SET"));
				} else {
					data.addResponse(L.get("ECONOMY_DOESNT_EXIST"));
				}
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_ECONOMY_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("goto")) {
			try {
				if (css == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				hp.teleport(css.getLocation1());
			} catch (Exception e) {
				hc.getDebugMode().debugWriteError(e);
				data.addResponse(L.get("SERVERSHOP_GOTO_INVALID"));
			}
		} else {
			data.addResponse(L.get("SERVERSHOP_INVALID"));
			return data;
		}
		return data;
	}
}
