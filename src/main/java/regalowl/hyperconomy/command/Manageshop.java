package regalowl.hyperconomy.command;


import java.util.ArrayList;
import java.util.HashMap;








import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.HyperShopManager;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.ShopCreationEvent;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectStatus;
import regalowl.hyperconomy.tradeobject.TradeObjectType;




public class Manageshop extends BaseCommand implements HyperCommand {
	
	public Manageshop() {
		super(true);
	}

	private HashMap<HyperPlayer, PlayerShop> currentShop = new HashMap<HyperPlayer, PlayerShop>();
	

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		if (!hc.getConf().getBoolean("enable-feature.player-shops")) {
			data.addResponse(L.get("PLAYERSHOPS_DISABLED"));
			return data;
		}
		int maxVolume = hc.getConf().getInt("shop.max-player-shop-volume");
		DataManager em = HC.hc.getDataManager();
		HyperShopManager hsm = hc.getHyperShopManager();
		if (hp == null) {return data;}
		HyperEconomy he = em.getEconomy(hp.getEconomy());
		if (hsm.inAnyShop(hp)) {
			Shop s = hsm.getShop(hp);
			if (s instanceof PlayerShop) {
				PlayerShop ps = (PlayerShop)s;
				if (ps.getOwner().equals(hp) || ps.isAllowed(hp) || hp.hasPermission("hyperconomy.admin")) {
					currentShop.put(hp, ps);
				}
			}
		}
		PlayerShop cps = null;
		if (currentShop.containsKey(hp)) {
			cps = currentShop.get(hp);
			if (!(cps.getOwner() == hp) && !cps.isAllowed(hp) && !hp.hasPermission("hyperconomy.admin")) {
				currentShop.remove(hp);
				cps = null;
			}
		}
		if (args.length == 0) {
			data.addResponse(L.get("MANAGESHOP_HELP"));
			if (cps != null) {
				data.addResponse(L.f(L.get("MANAGESHOP_HELP2"), cps.getName()));
				data.addResponse(L.f(L.get("MANAGESHOP_HELP3"), cps.getName()) + " &b" + cps.getOwner().getName());
				data.addResponse(L.get("MANAGESHOP_HELP4") + " &b" +  CommonFunctions.implode(cps.getAllowed(), ","));
			} else {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
			}
			return data;
		}
		if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("sel")) {
			if (args.length == 1) {
				data.addResponse(L.get("MANAGESHOP_SELECT_HELP"));
				return data;
			}
			if (!hsm.shopExists(args[1])) {
				data.addResponse(L.get("SHOP_NOT_EXIST"));
				return data;
			}
			Shop s = hsm.getShop(args[1]);
			if (!(s instanceof PlayerShop)) {
				data.addResponse(L.get("ONLY_PLAYER_SHOPS"));
				return data;
			}
			PlayerShop ps = (PlayerShop)s;
			if ((!(ps.getOwner().equals(hp) || ps.isAllowed(hp))) && !hp.hasPermission("hyperconomy.admin")) {
				data.addResponse(L.get("ONLY_EDIT_OWN_SHOPS"));
				return data;
			}
			currentShop.put(hp, ps);
			data.addResponse(L.get("SHOP_SELECTED"));
		} else if (args[0].equalsIgnoreCase("setstock") && hp.hasPermission("hyperconomy.admin")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			TradeObject ho = null;
			double amount = 0.0;
			if (args.length == 3) {
				ho = hp.getHyperEconomy().getHyperObject(args[1]);
				try {
					amount = Double.parseDouble(args[2]);
				} catch (Exception e) {
					data.addResponse(L.get("MANAGESHOP_SETSHOP_HELP"));
					return data;
				}
			} else {
				data.addResponse(L.get("MANAGESHOP_SETSHOP_HELP"));
				return data;
			}
			if (ho == null) {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				return data;
			}
			TradeObject ho2 = he.getHyperObject(ho.getName(), cps);
			if (ho2.isShopObject()) {
				ho2.setStock(amount);
				data.addResponse(L.f(L.get("STOCK_SET"), ho2.getName()));
				return data;
			} else {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
			}
		} else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("a")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (!cps.inShop(hp)) {
				data.addResponse(L.get("MANAGESHOP_EDIT_INSIDE_ONLY"));
				return data;
			}
			int amount = 1;
			TradeObject ho = null;
			if (args.length == 1) {
				HItemStack selectedItem = hp.getItemInHand();
				ho = hp.getHyperEconomy().getHyperObject(selectedItem);
			} else if (args.length == 2) {
				try {
					amount = Integer.parseInt(args[1]);
				} catch (Exception e) {
					data.addResponse(L.get("MANAGESHOP_ADD_HELP"));
					return data;
				}
				HItemStack selectedItem = hp.getItemInHand();
				ho = hp.getHyperEconomy().getHyperObject(selectedItem);
			} else if (args.length == 3) {
				ho = hp.getHyperEconomy().getHyperObject(args[1]);
				try {
					amount = Integer.parseInt(args[2]);
				} catch (Exception e) {
					data.addResponse(L.get("MANAGESHOP_ADD_HELP"));
					return data;
				}
			} else {
				data.addResponse(L.get("MANAGESHOP_ADD_HELP"));
				return data;
			}

	

			if (ho == null) {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				return data;
			}
			
			TradeObject ho2 = he.getHyperObject(ho.getName(), cps);
			int globalMaxStock = hc.getConf().getInt("shop.max-stock-per-item-in-playershops");
			if (ho2.getStock() + amount > globalMaxStock) {
				data.addResponse(L.get("CANT_ADD_MORE_STOCK"));
				return data;
			}
			if (ho2.getType() == TradeObjectType.ITEM) {
				int count = ho2.count(hp.getInventory());
				if (amount > count) {
					amount = count;
				}
				if (amount <= 0) {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return data;
				}
				double amountRemoved = ho2.remove(amount, hp.getInventory());
				ho2.setStock(ho2.getStock() + amountRemoved);
				data.addResponse(L.get("STOCK_ADDED"));
				return data;
			} else if (ho2.getType() == TradeObjectType.ENCHANTMENT) {
				if (amount < 1) {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return data;
				}
				double removed = ho2.removeEnchantment(hp.getItemInHand());
				if (removed > 0) {
					ho2.setStock(ho2.getStock() + removed);
					data.addResponse(L.get("STOCK_ADDED"));
				} else {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
				}
				return data;
			} else if (ho.getType() == TradeObjectType.EXPERIENCE) {
				if (amount < 1) {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return data;
				}
				int count = hp.getTotalXpPoints();
				if (amount > count) {
					amount = count;
				}
				double rcount = ho2.remove(amount, hp);
				if (rcount > 0) {
					ho2.setStock(ho2.getStock() + amount);
					data.addResponse(L.get("STOCK_ADDED"));
				} else {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
				}
			} else {
				hc.getSimpleDataLib().getErrorWriter().writeError("Setting PlayerShopObject stock failed in /ms add.");
				return data;
			}	
		} else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("r")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (!cps.inShop(hp)) {
				data.addResponse(L.get("MANAGESHOP_EDIT_INSIDE_ONLY"));
				return data;
			}
			if (args.length < 2) {
				data.addResponse(L.get("MANAGESHOP_REMOVE_HELP"));
				return data;
			}
			int amount = 1;
			if (args.length == 3) {
				try {
					amount = Integer.parseInt(args[2]);
				} catch (Exception e) {}
			}

			
			TradeObject ho = he.getHyperObject(args[1], cps);
			if (ho == null) {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				return data;
			}
			if (ho.getType() == TradeObjectType.ITEM) {
				if (ho.getStock() < amount) {
					amount = (int) Math.floor(ho.getStock());
				}
				if (amount <= 0.0) {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return data;
				}
				int space = ho.getAvailableSpace(hp.getInventory());
				if (space < amount) {
					data.addResponse(L.get("NOT_ENOUGH_SPACE"));
					return data;
				}
				ho.add(amount, hp.getInventory());
				ho.setStock(ho.getStock() - amount);
				data.addResponse(L.get("STOCK_REMOVED"));
				return data;
			} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
				if (ho.getStock() < 1) {
					amount = (int) Math.floor(ho.getStock());
				}
				if (amount < 1) {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return data;
				}
				double amountAdded = ho.addEnchantment(hp.getItemInHand());
				if (amountAdded > 0) {
					ho.setStock(ho.getStock() - amountAdded);
				} else {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
				}
			} else if (ho.getType() == TradeObjectType.EXPERIENCE) {
				if (ho.getStock() < amount) {
					amount = (int) Math.floor(ho.getStock());
				}
				if (amount < 1) {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
					return data;
				}
				boolean success = hp.addXp(amount);
				if (success) {
					ho.setStock(ho.getStock() - amount);
				} else {
					data.addResponse(L.get("MUST_TRANSFER_MORE_THAN_ZERO"));
				}
			} else {
				hc.getSimpleDataLib().getErrorWriter().writeError("Setting PlayerShopObject stock failed in /ms remove.");
				return data;
			}
		} else if ((args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c"))) {
			if (!hp.hasPermission("hyperconomy.playershop.create")) {
				data.addResponse(L.get("YOU_DONT_HAVE_PERMISSION"));
				return data;
			}
			if (args.length == 1) {
				data.addResponse(L.get("MANAGESHOP_CREATE_HELP"));
				return data;
			}
			String name = args[1].replace(".", "").replace(":", "");
			if (hsm.shopExists(name)){
				data.addResponse(L.get("SHOP_ALREADY_EXISTS"));
				return data;
			}
			int maxShops = hc.getConf().getInt("shop.max-player-shops-per-player");
			if (hsm.getShops(hp).size() > maxShops && !hp.hasPermission("hyperconomy.admin")) {
				data.addResponse(L.f(L.get("SHOP_LIMIT_REACHED"), maxShops));
				return data;
			}
			int radius = 2;
			if (args.length > 2) {
				try {
					radius = Integer.parseInt(args[2]);
				} catch (Exception e) {
					//continue
				}
			}
			HLocation l = hp.getLocation();
			HLocation p1 = new HLocation(l.getWorld(), l.getBlockX() - radius, l.getBlockY() - radius, l.getBlockZ() - radius);
			HLocation p2 = new HLocation(l.getWorld(), l.getBlockX() + radius, l.getBlockY() + radius, l.getBlockZ() + radius);
			PlayerShop newShop = new PlayerShop(name, hp.getEconomy(), hp, p1, p2);
			if (newShop.getVolume() > maxVolume) {
				data.addResponse(L.f(L.get("CANT_MAKE_SHOP_LARGER_THAN"), maxVolume));
				newShop.deleteShop();
				return data;
			}
			for (Shop s:hsm.getShops()) {
				if (newShop.intersectsShop(s, 10000)) {
					data.addResponse(L.f(L.get("SHOP_INTERSECTS_SHOP"), s.getDisplayName()));
					newShop.deleteShop();
					return data;
				}
			}
			for (TradeObject ho:he.getHyperObjects(newShop)) {
				if (ho.isShopObject()) {
					ho.setStatus(TradeObjectStatus.NONE);
				}
			}
			hsm.addShop(newShop);
			hc.getHyperEventHandler().fireEvent(new ShopCreationEvent(newShop));
			data.addResponse(L.get("SHOP_CREATED"));
		} else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (cps.isEmpty()) {
				cps.deleteShop();
				currentShop.remove(hp);
				data.addResponse(L.f(L.get("HAS_BEEN_REMOVED"), cps.getName()));
				return data;
			} else {
				if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
					cps.deleteShop();
					currentShop.remove(hp);
					data.addResponse(L.f(L.get("HAS_BEEN_REMOVED"), cps.getName()));
					return data;
				} else {
					data.addResponse(L.get("MANAGESHOP_DELETE_CONFIRM"));
					return data;
				}
			}
			
			
		} else if (args[0].equalsIgnoreCase("set1") || args[0].equalsIgnoreCase("s1")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			HLocation priorLoc = cps.getLocation1();
			cps.setPoint1(hp.getLocation());
			if (cps.getVolume() > maxVolume) {
				data.addResponse(L.f(L.get("CANT_MAKE_SHOP_LARGER_THAN"), maxVolume));
				cps.setPoint1(priorLoc);
				return data;
			}
			for (Shop s:hsm.getShops()) {
				if (cps.intersectsShop(s, 10000)) {
					if (cps.equals(s)) {continue;}
					data.addResponse(L.f(L.get("SHOP_INTERSECTS_SHOP"), s.getDisplayName()));
					cps.setPoint1(priorLoc);
					return data;
				}
			}
			data.addResponse(L.get("P1_SET"));
		} else if (args[0].equalsIgnoreCase("set2") || args[0].equalsIgnoreCase("s2")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			HLocation priorLoc = cps.getLocation2();
			cps.setPoint2(hp.getLocation());
			if (cps.getVolume() > maxVolume) {
				data.addResponse(L.f(L.get("CANT_MAKE_SHOP_LARGER_THAN"), maxVolume));
				cps.setPoint2(priorLoc);
				return data;
			}
			for (Shop s:hsm.getShops()) {
				if (cps.intersectsShop(s, 10000)) {
					if (cps.equals(s)) {continue;}
					data.addResponse(L.f(L.get("SHOP_INTERSECTS_SHOP"), s.getDisplayName()));
					cps.setPoint2(priorLoc);
					return data;
				}
			}
			data.addResponse(L.get("P2_SET"));
		} else if (args[0].equalsIgnoreCase("price") || args[0].equalsIgnoreCase("p")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (args.length != 3) {
				data.addResponse(L.get("MANAGESHOP_PRICE_HELP"));
				return data;
			}
			double price = 0.0;
			try {
				price = Double.parseDouble(args[2]);
			} catch (Exception e) {
				data.addResponse(L.get("MANAGESHOP_PRICE_HELP"));
				return data;
			}
			if (!he.objectTest(args[1])) {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				return data;
			}
			TradeObject ho = he.getHyperObject(args[1], cps);
			if (ho.isShopObject()) {
				ho.setBuyPrice(price);
				ho.setSellPrice(price);
				data.addResponse(L.get("PRICE_SET"));
				return data;
			} else {
				hc.getSimpleDataLib().getErrorWriter().writeError("Setting PlayerShopObject price failed.");
				return data;
			}
		} else if (args[0].equalsIgnoreCase("buyprice") || args[0].equalsIgnoreCase("bp")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (args.length != 3) {
				data.addResponse(L.get("MANAGESHOP_PRICE_HELP"));
				return data;
			}
			double price = 0.0;
			try {
				price = Double.parseDouble(args[2]);
			} catch (Exception e) {
				data.addResponse(L.get("MANAGESHOP_PRICE_HELP"));
				return data;
			}
			if (!he.objectTest(args[1])) {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				return data;
			}
			TradeObject ho = he.getHyperObject(args[1], cps);
			if (ho.isShopObject()) {
				ho.setBuyPrice(price);
				data.addResponse(L.get("PRICE_SET"));
				return data;
			} else {
				hc.getSimpleDataLib().getErrorWriter().writeError("Setting PlayerShopObject buyprice failed.");
				return data;
			}
		} else if (args[0].equalsIgnoreCase("sellprice") || args[0].equalsIgnoreCase("sp")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (args.length != 3) {
				data.addResponse(L.get("MANAGESHOP_PRICE_HELP"));
				return data;
			}
			double price = 0.0;
			try {
				price = Double.parseDouble(args[2]);
			} catch (Exception e) {
				data.addResponse(L.get("MANAGESHOP_PRICE_HELP"));
				return data;
			}
			if (!he.objectTest(args[1])) {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				return data;
			}
			TradeObject ho = he.getHyperObject(args[1], cps);
			if (ho.isShopObject()) {
				ho.setSellPrice(price);
				data.addResponse(L.get("PRICE_SET"));
				return data;
			} else {
				hc.getSimpleDataLib().getErrorWriter().writeError("Setting PlayerShopObject sellprice failed.");
				return data;
			}
		} else if (args[0].equalsIgnoreCase("maxstock") || args[0].equalsIgnoreCase("ms")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (args.length != 3) {
				data.addResponse(L.get("MANAGESHOP_MAXSTOCK_HELP"));
				return data;
			}
			int maxStock = 1000000;
			try {
				maxStock = Integer.parseInt(args[2]);
			} catch (Exception e) {
				data.addResponse(L.get("MANAGESHOP_MAXSTOCK_HELP"));
				return data;
			}
			if (!he.objectTest(args[1])) {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				return data;
			}
			TradeObject ho = he.getHyperObject(args[1], cps);
			if (ho.isShopObject()) {
				ho.setMaxStock(maxStock);
				data.addResponse(L.get("MAXSTOCK_SET"));
				return data;
			} else {
				hc.getSimpleDataLib().getErrorWriter().writeError("Setting PlayerShopObject max stock failed.");
				return data;
			}
		} else if (args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("s")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (args.length != 3) {
				data.addResponse(L.get("MANAGESHOP_STATUS_HELP"));
				return data;
			}
			TradeObjectStatus status = TradeObjectStatus.fromString(args[2]);
			if (status == TradeObjectStatus.NONE && !args[2].equalsIgnoreCase("none")) {
				data.addResponse(L.get("INVALID_STATUS"));
				return data;
			}
			if (args[1].equalsIgnoreCase("all")) {
				for (TradeObject ho:he.getHyperObjects(cps)) {
					ho.setStatus(status);
				}
				data.addResponse(L.get("ALL_STATUS_SET"));
				return data;
			}
			if (args[1].equalsIgnoreCase("instock")) {
				for (TradeObject ho:he.getHyperObjects(cps)) {
					if (ho.getStock() > 0) {
						ho.setStatus(status);
					}
				}
				data.addResponse(L.get("INSTOCK_STATUS_SET"));
				return data;
			}
			if (he.objectTest(args[1])) {
				TradeObject ho = he.getHyperObject(args[1], cps);
				ho.setStatus(status);
				data.addResponse(L.get("STATUS_SET"));
				return data;
			}
			FileConfiguration category = hc.gYH().getFileConfiguration("categories");
			String categoryString = category.getString(args[1]);
			if (categoryString != null) {
				ArrayList<String> names = CommonFunctions.explode(categoryString, ",");
				for (String name:names) {
					TradeObject ho = he.getHyperObject(name, cps);
					if (ho != null) {
						ho.setStatus(status);
					}
				}
				data.addResponse(L.get("CATEGORY_STATUS_SET"));
				return data;
			}
			data.addResponse(L.get("MANAGESHOP_STATUS_NOT_FOUND"));
			return data;

		} else if (args[0].equalsIgnoreCase("allow")) {
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (args.length != 2) {
				data.addResponse(L.get("MANAGESHOP_ALLOW_HELP"));
				return data;
			}
			if (!em.accountExists(args[1])) {
				data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
				return data;
			}
			HyperAccount ap = em.getAccount(args[1]);
			if (cps.isAllowed(ap)) {
				cps.removeAllowed(ap);
				data.addResponse(L.get("DISALLOWED_TO_MANAGE_SHOP"));
			} else {
				cps.addAllowed(ap);
				data.addResponse(L.get("ALLOWED_TO_MANAGE_SHOP"));
			}
			return data;
		} else if (args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("m")) {
			try {
				if (cps == null) {
					data.addResponse(L.get("NO_SHOP_SELECTED"));
					return data;
				}
				cps.setMessage(args[1].replace("_", " "));
				data.addResponse(L.get("MESSAGE_SET"));
			} catch (Exception e) {
				data.addResponse(L.get("MANAGESHOP_MESSAGE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("owner")) {
			if (!hp.hasPermission("hyperconomy.admin")) {
				data.addResponse(L.get("YOU_DONT_HAVE_PERMISSION"));
				return data;
			}
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (args.length != 2) {
				data.addResponse(L.get("MANAGESHOP_OWNER_HELP"));
				return data;
			}
			if (!em.accountExists(args[1])) {
				data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
				return data;
			}
			HyperAccount newOwner = em.getAccount(args[1]);
			cps.setOwner(newOwner);
			data.addResponse(L.get("OWNER_SET"));
			return data;
		} else if (args[0].equalsIgnoreCase("goto")) {
			if (!hp.hasPermission("hyperconomy.admin")) {
				data.addResponse(L.get("YOU_DONT_HAVE_PERMISSION"));
				return data;
			}
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			hp.teleport(cps.getLocation1());
		} else if (args[0].equalsIgnoreCase("stockmode")) {
			if (!hp.hasPermission("hyperconomy.admin")) {
				data.addResponse(L.get("YOU_DONT_HAVE_PERMISSION"));
				return data;
			}
			if (cps == null) {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
				return data;
			}
			if (cps.getUseEconomyStock()) {
				cps.setUseEconomyStock(false);
				data.addResponse(L.get("STOCK_MODE_SET_SHOP"));
			} else {
				cps.setUseEconomyStock(true);
				data.addResponse(L.get("STOCK_MODE_SET_ECONOMY"));
			}
		} else if (args[0].equalsIgnoreCase("list")) {
			if (!hp.hasPermission("hyperconomy.admin")) {
				data.addResponse(L.get("YOU_DONT_HAVE_PERMISSION"));
				return data;
			}
			ArrayList<Shop> shops = hsm.getShops();
			String sList = "";
			for (Shop s:shops) {
				if (s instanceof PlayerShop) {
					sList += s.getDisplayName() + ",";
				}
			}
			if (sList.length() > 0) {
				sList = sList.substring(0, sList.length() - 1);
			}
			String shoplist = sList.replace("_", " ");
			data.addResponse("&b" + shoplist);
		} else {
			data.addResponse(L.get("MANAGESHOP_HELP"));
			if (cps != null) {
				data.addResponse(L.f(L.get("MANAGESHOP_HELP2"), cps.getName()));
				data.addResponse(L.f(L.get("MANAGESHOP_HELP3"), cps.getName()) + " &b" + cps.getOwner().getName());
				data.addResponse(L.get("MANAGESHOP_HELP4") + " &b" + CommonFunctions.implode(cps.getAllowed(), ","));
			} else {
				data.addResponse(L.get("NO_SHOP_SELECTED"));
			}
			return data;
		}

		
		
		return data;
	}
	
	

}
