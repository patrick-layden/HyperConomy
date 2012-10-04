package regalowl.hyperconomy;

import static regalowl.hyperconomy.Messages.CANT_BE_TRADED;
import static regalowl.hyperconomy.Messages.ENCHANTMENT_NOT_IN_DATABASE;
import static regalowl.hyperconomy.Messages.ESELL_INVALID;
import static regalowl.hyperconomy.Messages.HAS_NO_ENCHANTMENTS;
import static regalowl.hyperconomy.Messages.MUST_BE_IN_SHOP;
import static regalowl.hyperconomy.Messages.NO_TRADE_PERMISSION;

import java.util.Iterator;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class Esell {
	Esell(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		ETransaction ench = hc.getETransaction();
		Shop s = hc.getShop();
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".sell")) {
					String name = args[0];
					if (args[0].equalsIgnoreCase("max")) {
						if (!ench.hasenchants(player.getItemInHand())) {
							player.sendMessage(HAS_NO_ENCHANTMENTS);
						}
						Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
						while (ite.hasNext()) {
							String rawstring = ite.next().toString();
							String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
							Enchantment en = null;
							en = Enchantment.getByName(enchname);
							int lvl = player.getItemInHand().getEnchantmentLevel(en);
							String nam = hc.getenchantData(enchname);
							String fnam = nam + lvl;
							if (s.has(s.getShop(player), fnam)) {
								ench.sellEnchant(fnam, player);
							} else {
								player.sendMessage(CANT_BE_TRADED);
							}
						}
					} else {
						String teststring = hc.testeString(name);
						if (teststring != null) {
							if (s.has(s.getShop(player), name)) {
								ench.sellEnchant(name, player);
							} else {
								player.sendMessage(CANT_BE_TRADED);
							}
						} else {
							player.sendMessage(ENCHANTMENT_NOT_IN_DATABASE);
						}
					}
				} else {
					player.sendMessage(NO_TRADE_PERMISSION);
				}
			} else {
				player.sendMessage(MUST_BE_IN_SHOP);
			}
		} catch (Exception e) {
			player.sendMessage(ESELL_INVALID);
		}
	}
}
