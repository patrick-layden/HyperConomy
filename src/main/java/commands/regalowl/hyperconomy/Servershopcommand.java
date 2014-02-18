package regalowl.hyperconomy;


import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;

public class Servershopcommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
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
		EconomyManager em = hc.getEconomyManager();
		if (args.length == 0) {
			player.sendMessage(L.get("SERVERSHOP_INVALID"));
			return true;
		}
		if (args[0].equalsIgnoreCase("p1")) {
			String name = args[1].replace(".", "").replace(":", "");
			if (em.shopExists(name)) {
				em.getShop(name).setPoint1(player.getLocation());
			} else {
				HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player);
				Shop shop = new ServerShop(name, hp.getEconomy(), hc.getEconomyManager().getGlobalShopAccount());
				shop.setPoint1(player.getLocation());
				shop.setPoint2(player.getLocation());
				shop.setDefaultMessages();
				em.addShop(shop);
			}
			player.sendMessage(L.get("P1_SET"));
		} else if (args[0].equalsIgnoreCase("p2")) {
			String name = args[1].replace(".", "").replace(":", "");
			if (em.shopExists(name)) {
				em.getShop(name).setPoint2(player.getLocation());
			} else {
				HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player);
				Shop shop = new ServerShop(name, hp.getEconomy(), hc.getEconomyManager().getGlobalShopAccount());
				shop.setPoint1(player.getLocation());
				shop.setPoint2(player.getLocation());
				shop.setDefaultMessages();
				em.addShop(shop);
			}
			player.sendMessage(L.get("P2_SET"));
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
				String name = args[2].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					em.getShop(name).setOwner(owner);
					player.sendMessage(L.get("OWNER_SET"));
				} else {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
					return true;
				}
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_OWNER_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			try {
				String name = args[1].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					Shop shop = em.getShop(name);
					shop.deleteShop();
					sender.sendMessage(L.f(L.get("HAS_BEEN_REMOVED"), name));
				} else {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_REMOVE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("rename")) {
			try {
				String name = args[1].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					String newName = args[2].replace(".", "").replace(":", "");
					em.getShop(name).setName(newName);
					sender.sendMessage(L.get("SHOP_RENAMED"));
				} else {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_RENAME_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("message1")) {
			try {
				String name = args[2].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					hc.getEconomyManager().getShop(name).setMessage1(args[1].replace("_", " "));
					sender.sendMessage(L.get("MESSAGE1_SET"));
					hc.restart();
				} else {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_MESSAGE1_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("message2")) {
			try {
				String name = args[2].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					hc.getEconomyManager().getShop(name).setMessage2(args[1].replace("_", " "));
					sender.sendMessage(L.get("MESSAGE2_SET"));
					hc.restart();
				} else {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_MESSAGE2_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("allow")) {
			try {
				String shopName = args[2];
				if (!em.shopExists(shopName)) {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
					return true;
				}
				Shop s = em.getShop(shopName);
				if (args[1].equalsIgnoreCase("all")) {
					s.unBanAllObjects();
					sender.sendMessage(ChatColor.GOLD + L.get("ALL_ITEMS_ADDED") + " " + shopName.replace("_", " "));
					return true;
				}
				HyperObject ho = em.getEconomy(s.getEconomy()).getHyperObject(args[1]);
				if (ho == null) {
					sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
					return true;
				}
				if (!s.isBanned(ho)) {
					sender.sendMessage(L.get("SHOP_ALREADY_HAS"));
					return true;
				}
				ArrayList<HyperObject> add = new ArrayList<HyperObject>();
				add.add(ho);
				s.unBanObjects(add);
				sender.sendMessage(ChatColor.GOLD + ho.getDisplayName() + " " + L.get("ADDED_TO") + " " + shopName.replace("_", " "));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_ALLOW_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("ban")) {
			try {
				String shopName = args[2];
				if (!em.shopExists(shopName)) {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
					return true;
				}
				Shop s = em.getShop(shopName);
				if (args[1].equalsIgnoreCase("all")) {
					s.banAllObjects();
					sender.sendMessage(L.f(L.get("ALL_REMOVED_FROM"), shopName.replace("_", " ")));
					return true;
				}
				HyperObject ho = em.getEconomy(s.getEconomy()).getHyperObject(args[1]);
				if (ho == null) {
					sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
					return true;
				}
				if (s.isBanned(ho)) {
					sender.sendMessage(L.get("ALREADY_BEEN_REMOVED"));
					return true;
				}
				ArrayList<HyperObject> remove = new ArrayList<HyperObject>();
				remove.add(ho);
				s.banObjects(remove);
				sender.sendMessage(L.f(L.get("REMOVED_FROM"), ho.getDisplayName(), shopName.replace("_", " ")));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_BAN_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("addcategory")) {
			try {
				FileConfiguration category = hc.gYH().gFC("categories");
				String categoryString = category.getString(args[1]);
				if (categoryString == null) {
					sender.sendMessage(L.get("CATEGORY_NOT_EXIST"));
					return true;
				}
				String shopName = args[2];
				if (!em.shopExists(shopName)) {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
					return true;
				}
				Shop s = em.getShop(shopName);
				ArrayList<String> categoryNames = cf.explode(categoryString, ",");
				HyperEconomy he = s.getHyperEconomy();
				ArrayList<HyperObject> add = new ArrayList<HyperObject>();
				for (String name:categoryNames) {
					HyperObject ho = he.getHyperObject(name);
					if (ho != null) {
						add.add(ho);
					}
				}
				s.unBanObjects(add);
				sender.sendMessage(ChatColor.GOLD + args[1] + " " + L.get("ADDED_TO") + " " + shopName.replace("_", " "));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_ADDCATEGORY_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("removecategory")) {
			try {
				FileConfiguration category = hc.gYH().gFC("categories");
				String categoryString = category.getString(args[1]);
				if (categoryString == null) {
					sender.sendMessage(L.get("CATEGORY_NOT_EXIST"));
					return true;
				}
				String shopName = args[2];
				if (!em.shopExists(shopName)) {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
					return true;
				}
				Shop s = em.getShop(shopName);
				ArrayList<String> categoryNames = cf.explode(categoryString, ",");
				HyperEconomy he = s.getHyperEconomy();
				ArrayList<HyperObject> remove = new ArrayList<HyperObject>();
				for (String name:categoryNames) {
					HyperObject ho = he.getHyperObject(name);
					if (ho != null) {
						remove.add(ho);
					}
				}
				s.banObjects(remove);
				sender.sendMessage(L.f(L.get("REMOVED_FROM"), args[1], shopName.replace("_", " ")));
			} catch (Exception e) {
				player.sendMessage(L.get("SERVERSHOP_REMOVECATEGORY_INVALID"));
			}
		} else {
			player.sendMessage(L.get("SERVERSHOP_INVALID"));
			return true;
		}
		return true;
	}
}
