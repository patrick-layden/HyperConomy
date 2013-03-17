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
		Transaction tran = hc.getTransaction();
		ETransaction ench = hc.getETransaction();
		ShopFactory s = hc.getShopFactory();
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
					}
					int itd = player.getItemInHand().getTypeId();
					int da = calc.getDamageValue(player.getItemInHand());
					HyperObject ho = sf.getHyperObject(itd, da, hp.getEconomy());
					if (ho == null) {
						player.sendMessage(L.get("OBJECT_NOT_AVAILABLE"));
					} else {
						String nam = ho.getName();
						double val = calc.getValue(nam, amount, player);
						if (calc.isDurable(itd) && amount > 1) {
							int numberofitem = tran.countInvitems(itd, player.getItemInHand().getData().getData(), player.getInventory());
							if (amount - numberofitem > 0) {
								int addamount = amount - numberofitem;
								val = val + calc.getTvalue(nam, addamount, playerecon);
							}
						}
						double salestax = calc.getSalesTax(player, val);
						val = calc.twoDecimals(val - salestax);
						player.sendMessage(L.get("LINE_BREAK"));
						player.sendMessage(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, nam));
						double cost = calc.getCost(nam, amount, playerecon);
						double taxpaid = calc.getPurchaseTax(nam, playerecon, cost);
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
				if (ench.hasenchants(iinhand)) {
					Account acc = hc.getAccount();
					player.getItemInHand().getEnchantments().keySet().toArray();
					Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
					player.sendMessage(L.get("LINE_BREAK"));
					double duramult = ench.getDuramult(player);
					while (ite.hasNext()) {
						String rawstring = ite.next().toString();
						String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
						Enchantment en = null;
						en = Enchantment.getByName(enchname);
						int lvl = player.getItemInHand().getEnchantmentLevel(en);
						String enam = sf.getEnchantNameWithoutLevel(enchname);
						String fnam = enam + lvl;
						String mater = player.getItemInHand().getType().name();
						double value = calc.getEnchantValue(fnam, EnchantmentClass.fromString(mater), playerecon) * duramult;
						double cost = calc.getEnchantCost(fnam, EnchantmentClass.fromString(mater), playerecon);
						cost = cost + calc.getEnchantTax(fnam, playerecon, cost);
						value = calc.twoDecimals(value);
						cost = calc.twoDecimals(cost);
						double salestax = 0;
						if (hc.getYaml().getConfig().getBoolean("config.dynamic-tax.use-dynamic-tax")) {
							double moneycap = hc.getYaml().getConfig().getDouble("config.dynamic-tax.money-cap");
							double cbal = acc.getBalance(player.getName());
							if (cbal >= moneycap) {
								salestax = value * (hc.getYaml().getConfig().getDouble("config.dynamic-tax.max-tax-percent") / 100);
							} else {
								salestax = value * (cbal / moneycap);
							}
						} else {
							double salestaxpercent = hc.getYaml().getConfig().getDouble("config.sales-tax-percent");
							salestax = (salestaxpercent / 100) * value;
						}
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
