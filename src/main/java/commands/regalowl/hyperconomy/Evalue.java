package regalowl.hyperconomy;

import java.util.Iterator;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class Evalue {
	Evalue(String args[], Player player, CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		InventoryManipulation im = hc.getInventoryManipulation();
		try {
			HyperPlayer hp = em.getHyperPlayer(player.getName());
			HyperEconomy he = hp.getHyperEconomy();
			boolean requireShop = hc.getConfig().getBoolean("config.limit-info-commands-to-shops");
			if ((requireShop && he.inAnyShop(player)) || !requireShop || player.hasPermission("hyperconomy.admin")) {
				if (args.length == 2) {
					String nam = args[0];
					if (he.enchantTest(nam)) {
						HyperObject ho = he.getHyperObject(nam);
						String type = args[1];
						if (type.equalsIgnoreCase("s")) {
							String[] classtype = new String[9];
							classtype[0] = "leather";
							classtype[1] = "wood";
							classtype[2] = "iron";
							classtype[3] = "chainmail";
							classtype[4] = "stone";
							classtype[5] = "gold";
							classtype[6] = "diamond";
							classtype[7] = "bow";
							classtype[8] = "book";
							int n = 0;
							sender.sendMessage(L.get("LINE_BREAK"));
							while (n < 9) {
								double value = ho.getValue(EnchantmentClass.fromString(classtype[n]));
								double salestax = hp.getSalesTax(value);
								value = calc.twoDecimals(value - salestax);
								sender.sendMessage(L.f(L.get("EVALUE_CLASS_SALE"), 1, value, nam, classtype[n]));
								n++;
							}
							sender.sendMessage(L.get("LINE_BREAK"));
						} else if (type.equalsIgnoreCase("b")) {
							String[] classtype = new String[9];
							classtype[0] = "leather";
							classtype[1] = "wood";
							classtype[2] = "iron";
							classtype[3] = "chainmail";
							classtype[4] = "stone";
							classtype[5] = "gold";
							classtype[6] = "diamond";
							classtype[7] = "bow";
							classtype[8] = "book";
							int n = 0;
							sender.sendMessage(L.get("LINE_BREAK"));
							while (n < 9) {
								double cost = ho.getCost(EnchantmentClass.fromString(classtype[n]));
								cost = cost + ho.getPurchaseTax(cost);
								sender.sendMessage(L.f(L.get("EVALUE_CLASS_PURCHASE"), 1, cost, nam, classtype[n]));
								n++;
							}
							sender.sendMessage(L.get("LINE_BREAK"));
						} else if (type.equalsIgnoreCase("a")) {
							sender.sendMessage(L.get("LINE_BREAK"));
							sender.sendMessage(L.f(L.get("EVALUE_STOCK"), he.getHyperObject(nam).getStock(), nam));
							sender.sendMessage(L.get("LINE_BREAK"));
						} else {
							sender.sendMessage(L.get("EVALUE_INVALID"));
						}
					} else {
						sender.sendMessage(L.get("ENCHANTMENT_NOT_IN_DATABASE"));
					}
				} else if (args.length == 0 && player != null) {
					if (im.hasenchants(player.getItemInHand())) {
						Iterator<Enchantment> ite = im.getEnchantmentMap(player.getItemInHand()).keySet().iterator();
						player.sendMessage(L.get("LINE_BREAK"));

						while (ite.hasNext()) {
							String rawstring = ite.next().toString();
							String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
							Enchantment en = null;
							en = Enchantment.getByName(enchname);
							int lvl = im.getEnchantmentLevel(player.getItemInHand(), en);
							String nam = he.getEnchantNameWithoutLevel(enchname);
							String fnam = nam + lvl;
							HyperObject ho = he.getHyperObject(fnam);
							String mater = player.getItemInHand().getType().name();
							double value = ho.getValue(EnchantmentClass.fromString(mater), hp);
							double cost = ho.getCost(EnchantmentClass.fromString(mater));
							cost = cost + ho.getPurchaseTax(cost);
							value = calc.twoDecimals(value);
							cost = calc.twoDecimals(cost);
							double salestax = 0;
							if (hc.gYH().gFC("config").getBoolean("config.dynamic-tax.use-dynamic-tax")) {
								double moneycap = hc.gYH().gFC("config").getDouble("config.dynamic-tax.money-cap");
								double cbal = he.getHyperPlayer(player.getName()).getBalance();
								if (cbal >= moneycap) {
									salestax = value * (hc.gYH().gFC("config").getDouble("config.dynamic-tax.max-tax-percent") / 100);
								} else {
									salestax = value * (cbal / moneycap);
								}
							} else {
								double salestaxpercent = hc.gYH().gFC("config").getDouble("config.sales-tax-percent");
								salestax = (salestaxpercent / 100) * value;
							}
							value = calc.twoDecimals(value - salestax);
							sender.sendMessage(L.f(L.get("EVALUE_SALE"), value, fnam));
							sender.sendMessage(L.f(L.get("EVALUE_PURCHASE"), cost, fnam));
							sender.sendMessage(L.f(L.get("EVALUE_STOCK"), he.getHyperObject(fnam).getStock(), fnam));
						}
						player.sendMessage(L.get("LINE_BREAK"));
					} else {
						sender.sendMessage(L.get("HAS_NO_ENCHANTMENTS"));
					}
				} else {
					sender.sendMessage(L.get("EVALUE_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("EVALUE_INVALID"));
		}
	}
}
