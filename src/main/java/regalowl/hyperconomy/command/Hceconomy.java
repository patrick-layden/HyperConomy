package regalowl.hyperconomy.command;


import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.util.Backup;
import regalowl.hyperconomy.util.LanguageFile;

public class Hceconomy implements CommandExecutor {
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(L.get("HCECONOMY_INVALID"));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
			try {
				if (em.economyExists(args[1])) {
					sender.sendMessage(L.get("ECONOMY_ALREADY_EXISTS"));
					return true;
				}
				String template = "default";
				if (args.length >= 3 && em.economyExists(args[2])) {
					template = args[2];
				}
				boolean clone = false;
				if (args.length >= 4 && args[3].equalsIgnoreCase("clone")) {
					clone = true;
				}
				hc.getDataManager().createNewEconomy(args[1], template, clone);
				sender.sendMessage(L.get("NEW_ECONOMY_CREATED"));
			} catch (Exception e) {
				sender.sendMessage(L.get("HCECONOMY_CREATE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("delete")) {
			try {
				String economy = args[1];
				if (economy.equalsIgnoreCase("default")) {
					sender.sendMessage(L.get("CANT_DELETE_DEFAULT_ECONOMY"));
					return true;
				}
				if (!em.economyExists(economy)) {
					sender.sendMessage(L.get("ECONOMY_DOESNT_EXIST"));
					return true;
				}
				if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {
					new Backup();
				}
				for (Shop shop:em.getHyperShopManager().getShops()) {
					if (shop.getEconomy().equalsIgnoreCase(economy)) {
						shop.setEconomy("default");
					}
				}
				for (HyperPlayer hp:em.getHyperPlayerManager().getHyperPlayers()) {
					if (hp.getEconomy().equalsIgnoreCase(economy)) {
						hp.setEconomy("default");
					}
				}
				em.deleteEconomy(economy);
				sender.sendMessage(L.get("ECONOMY_DELETED"));
			} catch (Exception e) {
				sender.sendMessage(L.get("HCECONOMY_DELETE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("account")) {
			try {
				String economy = args[1];
				if (!em.economyExists(economy)) {
					sender.sendMessage(L.get("ECONOMY_DOESNT_EXIST"));
					return true;
				}
				String account = args[2];
				if (!em.accountExists(account)) {
					sender.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
					return true;
				}
				em.getEconomy(economy).setDefaultAccount(em.getAccount(account));
				sender.sendMessage(L.get("HCECONOMY_ACCOUNT_SET"));
			} catch (Exception e) {
				sender.sendMessage(L.get("HCECONOMY_ACCOUNT_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {
			ArrayList<String> economies = em.getEconomyList();
			sender.sendMessage(ChatColor.AQUA + economies.toString());
		} else {
			sender.sendMessage(L.get("HCECONOMY_INVALID"));
			return true;
		}
		return true;
	}
}
