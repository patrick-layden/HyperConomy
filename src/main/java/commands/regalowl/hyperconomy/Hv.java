package regalowl.hyperconomy;

import java.util.Iterator;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hv {
	Hv(String args[], Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		InventoryManipulation im = hc.getInventoryManipulation();
		DataHandler dh = hc.getDataFunctions();
		int amount;
		try {
			HyperPlayer hp = dh.getHyperPlayer(player);
			boolean requireShop = hc.getConfig().getBoolean("config.limit-info-commands-to-shops");
			if ((requireShop && s.inAnyShop(player)) || !requireShop || player.hasPermission("hyperconomy.admin")) {
				ItemStack iinhand = player.getItemInHand();
					if (args.length == 0) {
						amount = 1;
					} else {
						amount = Integer.parseInt(args[0]);
						if (amount > 10000) {
							amount = 10000;
						}
					}
					if (!im.hasenchants(iinhand)) {
					int itd = player.getItemInHand().getTypeId();
					int da = calc.getDamageValue(player.getItemInHand());
					HyperObject ho = sf.getHyperObject(itd, da, hp.getEconomy());
					if (ho == null) {
						player.sendMessage(L.get("OBJECT_NOT_AVAILABLE"));
					} else {
						String nam = ho.getName();
						double val = ho.getValue(amount, hp);
						if (ho.isDurable() && amount > 1) {
							int numberofitem = im.countItems(itd, player.getItemInHand().getData().getData(), player.getInventory());
							if (amount - numberofitem > 0) {
								int addamount = amount - numberofitem;
								val = val + ho.getValue(addamount);
							}
						}
						double salestax = hp.getSalesTax(val);
						val = calc.twoDecimals(val - salestax);
						player.sendMessage(L.get("LINE_BREAK"));
						player.sendMessage(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, nam));
						double cost = ho.getCost(amount);
						double taxpaid = ho.getPurchaseTax(cost);
						cost = calc.twoDecimals(cost + taxpaid);
						if (cost > Math.pow(10, 10)) {
							cost = -1;
						}
						double stock = 0;
						stock = sf.getHyperObject(nam, playerecon).getStock();
						player.sendMessage(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, cost, nam));
						player.sendMessage(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), stock, nam));
						player.sendMessage(L.get("LINE_BREAK"));
					}
					} else {
					player.getItemInHand().getEnchantments().keySet().toArray();
					Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
					player.sendMessage(L.get("LINE_BREAK"));
					while (ite.hasNext()) {
						String rawstring = ite.next().toString();
						String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
						Enchantment en = null;
						en = Enchantment.getByName(enchname);
						int lvl = player.getItemInHand().getEnchantmentLevel(en);
						String enam = sf.getEnchantNameWithoutLevel(enchname);
						String fnam = enam + lvl;
						String mater = player.getItemInHand().getType().name();
						HyperObject ho = sf.getHyperObject(fnam, playerecon);
						double value = ho.getValue(EnchantmentClass.fromString(mater), hp);
						double cost = ho.getCost(EnchantmentClass.fromString(mater));
						cost = cost + ho.getPurchaseTax(cost);
						value = calc.twoDecimals(value);
						cost = calc.twoDecimals(cost);
						double salestax = 0;
						salestax = hp.getSalesTax(value);
						value = calc.twoDecimals(value - salestax);
						player.sendMessage(L.f(L.get("EVALUE_SALE"), value, fnam));
						player.sendMessage(L.f(L.get("EVALUE_PURCHASE"), cost, fnam));
						player.sendMessage(L.f(L.get("EVALUE_STOCK"), sf.getHyperObject(fnam, playerecon).getStock(), fnam));
					}
					player.sendMessage(L.get("LINE_BREAK"));
				}
			} else {
				player.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("HV_INVALID"));
		}
	}
}
