package regalowl.hyperconomy;

import java.util.ArrayList;
import org.bukkit.entity.Player;

public class Esell {
	Esell(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		ETransaction ench = hc.getETransaction();
		LanguageFile L = hc.getLanguageFile();
		Shop s = hc.getShop();
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".sell")) {
					String name = args[0];
					if (args[0].equalsIgnoreCase("max")) {
						if (!ench.hasenchants(player.getItemInHand())) {
							player.sendMessage(L.get("HAS_NO_ENCHANTMENTS"));
						}
						ArrayList<String> enchants = ench.getEnchantments(player.getItemInHand());
						for (String e:enchants) {
							if (s.has(s.getShop(player), e)) {
								ench.sellEnchant(e, player);
							} else {
								player.sendMessage(L.get("CANT_BE_TRADED"));
							}
						}

					} else {
						if (hc.enchantTest(name)) {
							if (s.has(s.getShop(player), name)) {
								ench.sellEnchant(name, player);
							} else {
								player.sendMessage(L.get("CANT_BE_TRADED"));
							}
						} else {
							player.sendMessage(L.get("ENCHANTMENT_NOT_IN_DATABASE"));
						}
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("ESELL_INVALID"));
		}
	}
}
