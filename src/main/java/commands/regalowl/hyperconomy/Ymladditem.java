package regalowl.hyperconomy;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Ymladditem {
	Ymladditem(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			String name = args[0];
			double value = Double.parseDouble(args[1]);
			int median = Integer.parseInt(args[2]);
			double startprice = Double.parseDouble(args[3]);
			int itd = player.getItemInHand().getTypeId();
			int da = hc.getInventoryManipulation().getDamageValue(player.getItemInHand());
			HyperEconomy econ = em.getEconomy(em.getHyperPlayer(player.getName()).getEconomy());
			HyperObject ho =  econ.getHyperObject(itd, da);
			if (ho != null) {
				player.sendMessage(L.get("ALREADY_IN_DATABASE"));
				return;
			}
			FileConfiguration items = hc.getYaml().getItems();
			items.set(name + ".information.type", "item");
			items.set(name + ".information.category", "unknown");
			items.set(name + ".information.material", player.getItemInHand().getType().toString());
			items.set(name + ".information.id", itd);
			items.set(name + ".information.data", da);
			items.set(name + ".value", value);
			items.set(name + ".price.static", false);
			items.set(name + ".price.staticprice", startprice);
			items.set(name + ".stock.stock", 0);
			items.set(name + ".stock.median", median);
			items.set(name + ".initiation.initiation", true);
			items.set(name + ".initiation.startprice", startprice);
			player.sendMessage(L.get("ITEM_ADDED"));
			return;
		} catch (Exception e) {
			player.sendMessage(L.get("YMLADDITEM_INVALID"));
			return;
		}
	}
}
