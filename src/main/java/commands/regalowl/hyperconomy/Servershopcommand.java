package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;

public class Servershopcommand implements CommandExecutor {
	
	
	private HashMap<HyperPlayer, ServerShop> currentShop = new HashMap<HyperPlayer, ServerShop>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		CommonFunctions cf = hc.gCF();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);
			return true;
		}
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (player == null) {
			return true;
		}
		HyperPlayer hp = em.getHyperPlayer(player.getName());
		if (em.inAnyShop(player)) {
			Shop s = em.getShop(player);
			if (s instanceof ServerShop) {
				ServerShop ss = (ServerShop)s;
				if (player.hasPermission("hyperconomy.admin")) {
					currentShop.put(hp, ss);
				}
			}
		}
		ServerShop css = null;
		if (currentShop.containsKey(hp)) {
			css = currentShop.get(hp);
		}

		if (args.length == 0) {
			player.sendMessage(L.get("SERVERSHOP_INVALID"));
			if (css == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
			} else {
				player.sendMessage(L.f(L.get("MANAGESHOP_HELP2"), css.getDisplayName()));
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("s")) {
			if (!em.shopExists(args[1])) {
				player.sendMessage(L.get("SHOP_NOT_EXIST"));
				return true;
			}
			Shop s = em.getShop(args[1]);
			if (!(s instanceof ServerShop)) {
				player.sendMessage(L.get("ONLY_SERVER_SHOPS"));
				return true;
			}
			currentShop.put(hp, (ServerShop)s);
			player.sendMessage(L.get("SHOP_SELECTED"));
		} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
			if (css == null) {
				player.sendMessage(L.get("NO_SHOP_SELECTED"));
				return true;
			}
			player.sendMessage(L.f(L.get("MANAGESHOP_HELP2"), css.getDisplayName()));
			player.sendMessage(L.f(L.get("MANAGESHOP_HELP3"), css.getName()) + " " + ChatColor.AQUA + css.getOwner().getName());
			player.sendMessage(L.f(L.get("SERVERSHOP_ECONOMY_INFO"), css.getEconomy()));
		} else if (args[0].equalsIgnoreCase("p1")) {
			try {
				String name = args[1].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					em.getShop(name).setPoint1(player.getLocation());
				} else {
					Location l = player.getLocation();
					Shop shop = new ServerShop(name, hp.getEconomy(), hc.getEconomyManager().getGlobalShopAccount(), l, l);
					em.addShop(shop);
				}
				player.sendMessage(L.get("P1_SET"));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_P1_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("p2")) {
			try {
				String name = args[1].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					em.getShop(name).setPoint2(player.getLocation());
				} else {
					Location l = player.getLocation();
					Shop shop = new ServerShop(name, hp.getEconomy(), hc.getEconomyManager().getGlobalShopAccount(), l, l);
					em.addShop(shop);
				}
				player.sendMessage(L.get("P2_SET"));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_P2_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("list")) {
			String shoplist = HyperConomy.hyperAPI.listShops().toString().replace("_", " ").replace("[", "").replace("]", "");
			sender.sendMessage(ChatColor.AQUA + shoplist);
		} else if (args[0].equalsIgnoreCase("owner") || args[0].equalsIgnoreCase("o")) {
			try {
				HyperAccount owner = null;
				if (em.hasAccount(args[1])) {
					owner = em.getHyperPlayer(args[1]);
				} else {
					if (em.hasBank(args[1])) {
						owner = em.getHyperBank(args[1]);
					} else {
						player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
					}
				}
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				css.setOwner(owner);
				player.sendMessage(L.get("OWNER_SET"));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_OWNER_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("removeshop")) {
			try {
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				css.deleteShop();
				sender.sendMessage(L.f(L.get("HAS_BEEN_REMOVED"), css.getName()));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_REMOVE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("rename")) {
			try {
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				String newName = args[1].replace(".", "").replace(":", "");
				css.setName(newName);
				sender.sendMessage(L.get("SHOP_RENAMED"));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_RENAME_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("m")) {
			try {
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				css.setMessage(args[1]);
				sender.sendMessage(L.get("MESSAGE_SET"));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_MESSAGE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("a")) {
			try {
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				if (args[1].equalsIgnoreCase("all")) {
					css.unBanAllObjects();
					sender.sendMessage(ChatColor.GOLD + L.get("ALL_ITEMS_ADDED") + " " + css.getDisplayName());
					return true;
				}
				HyperObject ho = em.getEconomy(css.getEconomy()).getHyperObject(args[1]);
				if (ho == null) {
					sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
					return true;
				}
				if (!css.isBanned(ho)) {
					sender.sendMessage(L.get("SHOP_ALREADY_HAS"));
					return true;
				}
				ArrayList<HyperObject> add = new ArrayList<HyperObject>();
				add.add(ho);
				css.unBanObjects(add);
				sender.sendMessage(ChatColor.GOLD + ho.getDisplayName() + " " + L.get("ADDED_TO") + " " + css.getDisplayName());
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_ALLOW_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("b")) {
			try {
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				if (args[1].equalsIgnoreCase("all")) {
					css.banAllObjects();
					sender.sendMessage(L.f(L.get("ALL_REMOVED_FROM"), css.getDisplayName()));
					return true;
				}
				HyperObject ho = em.getEconomy(css.getEconomy()).getHyperObject(args[1]);
				if (ho == null) {
					sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
					return true;
				}
				if (css.isBanned(ho)) {
					sender.sendMessage(L.get("ALREADY_BEEN_REMOVED"));
					return true;
				}
				ArrayList<HyperObject> remove = new ArrayList<HyperObject>();
				remove.add(ho);
				css.banObjects(remove);
				sender.sendMessage(L.f(L.get("REMOVED_FROM"), ho.getDisplayName(), css.getDisplayName()));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_BAN_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("addcategory") || args[0].equalsIgnoreCase("acat")) {
			try {
				FileConfiguration category = hc.gYH().gFC("categories");
				String categoryString = category.getString(args[1]);
				if (categoryString == null) {
					sender.sendMessage(L.get("CATEGORY_NOT_EXIST"));
					return true;
				}
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				ArrayList<String> categoryNames = cf.explode(categoryString, ",");
				HyperEconomy he = css.getHyperEconomy();
				ArrayList<HyperObject> add = new ArrayList<HyperObject>();
				for (String name:categoryNames) {
					HyperObject ho = he.getHyperObject(name);
					if (ho != null) {
						add.add(ho);
					}
				}
				css.unBanObjects(add);
				sender.sendMessage(ChatColor.GOLD + args[1] + " " + L.get("ADDED_TO") + " " + css.getDisplayName());
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_ADDCATEGORY_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("removecategory") || args[0].equalsIgnoreCase("rcat")) {
			try {
				FileConfiguration category = hc.gYH().gFC("categories");
				String categoryString = category.getString(args[1]);
				if (categoryString == null) {
					sender.sendMessage(L.get("CATEGORY_NOT_EXIST"));
					return true;
				}
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				ArrayList<String> categoryNames = cf.explode(categoryString, ",");
				HyperEconomy he = css.getHyperEconomy();
				ArrayList<HyperObject> remove = new ArrayList<HyperObject>();
				for (String name:categoryNames) {
					HyperObject ho = he.getHyperObject(name);
					if (ho != null) {
						remove.add(ho);
					}
				}
				css.banObjects(remove);
				sender.sendMessage(L.f(L.get("REMOVED_FROM"), args[1], css.getDisplayName()));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_REMOVECATEGORY_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("economy") || args[0].equalsIgnoreCase("e")) {
			try {
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				String economy = args[1];
				if (hc.getEconomyManager().economyExists(economy)) {
					css.setEconomy(economy);
					sender.sendMessage(L.get("SHOP_ECONOMY_SET"));
				} else {
					sender.sendMessage(L.get("ECONOMY_DOESNT_EXIST"));
				}
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_ECONOMY_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("goto")) {
			try {
				if (css == null) {
					player.sendMessage(L.get("NO_SHOP_SELECTED"));
					return true;
				}
				player.teleport(css.getLocation1());
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_ECONOMY_INVALID"));
			}
		} else {
			player.sendMessage(L.get("SERVERSHOP_INVALID"));
			return true;
		}
		return true;
	}
}
