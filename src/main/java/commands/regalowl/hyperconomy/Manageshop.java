package regalowl.hyperconomy;


import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;




public class Manageshop implements CommandExecutor {
	
	private HashMap<HyperPlayer, PlayerShop> currentShop = new HashMap<HyperPlayer, PlayerShop>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);;
			return true;
		}
		if (!hc.gYH().gFC("config").getBoolean("config.use-player-shops")) {
			sender.sendMessage(L.get("PLAYERSHOPS_DISABLED"));
			return true;
		}
		int maxVolume = hc.gYH().gFC("config").getInt("config.max-player-shop-volume");
		EconomyManager em = hc.getEconomyManager();
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if (player == null) {
			return true;
		}
		HyperPlayer hp = em.getHyperPlayer(player.getName());
		HyperEconomy he = em.getEconomy(hp.getEconomy());
		if (em.inAnyShop(player)) {
			Shop s = em.getShop(player);
			if (s instanceof PlayerShop && (s.getOwner().equals(hp) || player.hasPermission("hyperconomy.admin"))) {
				currentShop.put(hp, (PlayerShop)s);
			}
		}
		PlayerShop cps = null;
		if (currentShop.containsKey(hp)) {
			cps = currentShop.get(hp);
		}
		if (args.length == 0) {
			player.sendMessage(L.get("MANAGESHOP_HELP"));
			if (cps != null) {
				player.sendMessage(L.f(L.get("MANAGESHOP_HELP2"), cps.getName()));
			} else {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("sel")) {
			if (args.length == 1) {
				player.sendMessage(L.get("MANAGESHOP_SELECT_HELP"));
				return true;
			}
			if (!em.shopExists(args[1])) {
				player.sendMessage(L.get("SHOP_NOT_EXIST"));
				return true;
			}
			Shop s = em.getShop(args[1]);
			if ((!(s instanceof PlayerShop) || !(s.getOwner().equals(hp))) && !player.hasPermission("hyperconomy.admin")) {
				player.sendMessage(L.get("ONLY_EDIT_OWN_SHOPS"));
				return true;
			}
			currentShop.put(hp, (PlayerShop)s);
			player.sendMessage(L.get("SHOP_SELECTED"));
		} else  if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
			if (args.length == 1) {
				player.sendMessage(L.get("MANAGESHOP_CREATE_HELP"));
				return true;
			}
			if (em.shopExists(args[1])){
				player.sendMessage(L.get("SHOP_ALREADY_EXISTS"));
				return true;
			}
			int maxShops = hc.gYH().gFC("config").getInt("config.max-shops-per-player");
			if (em.getShops(hp).size() > maxShops && !player.hasPermission("hyperconomy.admin")) {
				player.sendMessage(L.f(L.get("SHOP_LIMIT_REACHED"), maxShops));
				return true;
			}
			String name = args[1];
			int radius = 2;
			if (args.length > 2) {
				try {
					radius = Integer.parseInt(args[2]);
				} catch (Exception e) {
					//continue
				}
			}
			PlayerShop newShop = new PlayerShop(name, hp.getEconomy(), hp);
			Location l = player.getLocation();
			newShop.setPoint1(player.getWorld().getName(), l.getBlockX() - radius, l.getBlockY() - radius, l.getBlockZ() - radius);
			newShop.setPoint2(player.getWorld().getName(), l.getBlockX() + radius, l.getBlockY() + radius, l.getBlockZ() + radius);
			if (newShop.getVolume() > maxVolume) {
				player.sendMessage(L.f(L.get("CANT_MAKE_SHOP_LARGER_THAN"), maxVolume));
				newShop.deleteShop();
				return true;
			}
			for (HyperObject ho:he.getHyperObjects(newShop)) {
				if (ho instanceof PlayerShopObject) {
					((PlayerShopObject) ho).setStatus(HyperObjectStatus.NONE);
				}
			}
			em.addShop(newShop);
			player.sendMessage(L.get("SHOP_CREATED"));
		} else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("d")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (cps.isEmpty()) {
				cps.deleteShop();
				currentShop.remove(hp);
				player.sendMessage(L.f(L.get("HAS_BEEN_REMOVED"), cps.getName()));
				return true;
			} else {
				if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
					cps.deleteShop();
					currentShop.remove(hp);
					player.sendMessage(L.f(L.get("HAS_BEEN_REMOVED"), cps.getName()));
					return true;
				} else {
					player.sendMessage(L.get("MANAGESHOP_DELETE_CONFIRM"));
					return true;
				}
			}
			
			
		} else if (args[0].equalsIgnoreCase("set1") || args[0].equalsIgnoreCase("s1")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			Location priorLoc = cps.getLocation1();
			cps.setPoint1(player.getLocation());
			if (cps.getVolume() > maxVolume) {
				player.sendMessage(L.f(L.get("CANT_MAKE_SHOP_LARGER_THAN"), maxVolume));
				cps.setPoint1(priorLoc);
				return true;
			}
			player.sendMessage(L.get("P1_SET"));
		} else if (args[0].equalsIgnoreCase("set2") || args[0].equalsIgnoreCase("s2")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			Location priorLoc = cps.getLocation2();
			cps.setPoint2(player.getLocation());
			if (cps.getVolume() > maxVolume) {
				player.sendMessage(L.f(L.get("CANT_MAKE_SHOP_LARGER_THAN"), maxVolume));
				cps.setPoint2(priorLoc);
				return true;
			}
			player.sendMessage(L.get("P2_SET"));
		} else if (args[0].equalsIgnoreCase("price") || args[0].equalsIgnoreCase("p")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 3) {
				player.sendMessage(L.get("MANAGESHOP_PRICE_HELP"));
				return true;
			}
			double price = 0.0;
			try {
				price = Double.parseDouble(args[2]);
			} catch (Exception e) {
				player.sendMessage(L.get("MANAGESHOP_PRICE_HELP"));
				return true;
			}
			if (!he.itemTest(args[1])) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			HyperObject ho = he.getHyperObject(args[1], cps);
			if (ho instanceof PlayerShopObject) {
				((PlayerShopObject) ho).setPrice(price);
				player.sendMessage(L.get("PRICE_SET"));
				return true;
			} else {
				hc.getDataBukkit().writeError("Setting PlayerShopObject price failed.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("s")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 3) {
				player.sendMessage(L.get("MANAGESHOP_STATUS_HELP"));
				return true;
			}
			HyperObjectStatus status = HyperObjectStatus.fromString(args[2]);
			if (status == HyperObjectStatus.NONE && !args[2].equalsIgnoreCase("none")) {
				player.sendMessage(L.get("INVALID_STATUS"));
				return true;
			}
			if (!he.itemTest(args[1]) && !args[1].equalsIgnoreCase("all")) {
				player.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			if (args[1].equalsIgnoreCase("all")) {
				for (HyperObject ho:he.getHyperObjects(cps)) {
					if (ho instanceof PlayerShopObject) {
						((PlayerShopObject) ho).setStatus(status);
					}
				}
				player.sendMessage(L.get("ALL_STATUS_SET"));
				return true;
			} else {
				HyperObject ho = he.getHyperObject(args[1], cps);
				if (ho instanceof PlayerShopObject) {
					((PlayerShopObject) ho).setStatus(status);
					player.sendMessage(L.get("STATUS_SET"));
					return true;
				} else {
					hc.getDataBukkit().writeError("Setting PlayerShopObject status failed.");
					return true;
				}
			}
		} else if (args[0].equalsIgnoreCase("owner") && player.hasPermission("hyperconomy.admin")) {
			if (cps == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			if (args.length != 2) {
				player.sendMessage(L.get("MANAGESHOP_OWNER_HELP"));
				return true;
			}
			if (!em.hasAccount(args[1])) {
				player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				return true;
			}
			HyperPlayer newOwner = em.getHyperPlayer(args[1]);
			cps.setOwner(newOwner);
			player.sendMessage(L.get("OWNER_SET"));
			return true;
		} else {
			player.sendMessage(L.get("MANAGESHOP_HELP"));
			if (cps != null) {
				player.sendMessage(L.f(L.get("MANAGESHOP_HELP2"), cps.getName()));
			} else {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
			}
			return true;
		}

		
		
		return true;
	}
	
	

}
