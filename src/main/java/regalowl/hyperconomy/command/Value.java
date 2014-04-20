package regalowl.hyperconomy.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.util.LanguageFile;

public class Value implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);;
			return true;
		}
		HyperEconomy he = hc.getDataManager().getEconomy("default");
		CommonFunctions cf = hc.gCF();
		LanguageFile L = hc.getLanguageFile();
		Player player = null;
		DataManager em = hc.getDataManager();
		if (sender instanceof Player) {
			player = (Player) sender;
			HyperPlayer hp = em.getHyperPlayer(player);
			he = hp.getHyperEconomy();
		}
		try {
			boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
			if (player != null && requireShop && !em.inAnyShop(player) && !player.hasPermission("hyperconomy.admin")) {
				sender.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
				return true;
			}
			String name = he.fixName(args[0]);
			HyperObject ho = he.getHyperObject(name, em.getShop(player));
			if (ho == null) {
				sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				return true;
			}
			int amount = 1;
			if (ho.getType() != HyperObjectType.ENCHANTMENT && args.length > 1) {
				amount = Integer.parseInt(args[1]);
				if (amount > 10000) {
					amount = 10000;
				}
			}
			EnchantmentClass eClass = EnchantmentClass.DIAMOND;
			if (ho.getType() == HyperObjectType.ENCHANTMENT && args.length > 1) {
				eClass = EnchantmentClass.fromString(args[1]);
				if (eClass == EnchantmentClass.NONE) {
					eClass = EnchantmentClass.DIAMOND;
				}
			}
			double val = 0;
			double cost = 0;
			if (player != null) {
				HyperPlayer hp = em.getHyperPlayer(player);
				if (ho.getType() == HyperObjectType.ITEM) {
					val = ho.getSellPriceWithTax(amount, hp);
					cost = ho.getBuyPriceWithTax(amount);
				} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
					val = ho.getSellPrice(eClass, hp);
					val -= hp.getSalesTax(val);
					cost = ho.getBuyPrice(eClass);
					cost += ho.getPurchaseTax(cost);
				} else if (ho.getType() == HyperObjectType.EXPERIENCE) {
					val = ho.getSellPrice(amount);
					val -= hp.getSalesTax(val);
					cost = ho.getBuyPriceWithTax(amount);
				}
			} else {
				if (ho.getType() == HyperObjectType.ITEM) {
					val = ho.getSellPrice(amount);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPriceWithTax(amount);
				} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
					val = ho.getSellPrice(eClass);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPrice(eClass);
					cost += ho.getPurchaseTax(cost);
				} else if (ho.getType() == HyperObjectType.EXPERIENCE) {
					val = ho.getSellPrice(amount);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPriceWithTax(amount);
				}
			}


			sender.sendMessage(L.get("LINE_BREAK"));
			sender.sendMessage(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, ho.getDisplayName()));
			sender.sendMessage(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, cost, ho.getDisplayName()));
			sender.sendMessage(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), cf.twoDecimals(ho.getStock()), ho.getDisplayName()));
			sender.sendMessage(L.get("LINE_BREAK"));

		} catch (Exception e) {
			sender.sendMessage(L.get("VALUE_INVALID"));
			return true;
		}
		return true;
	}
}
