package regalowl.hyperconomy;


import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;




public class Manageshop implements CommandExecutor {
	
	private HashMap<HyperPlayer, PlayerShop> currentShop = new HashMap<HyperPlayer, PlayerShop>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if (player == null) {
			return true;
		}
		HyperPlayer hp = em.getHyperPlayer(player.getName());
		HyperEconomy he = em.getEconomy(hp.getEconomy());
		if (he.inAnyShop(player)) {
			Shop s = he.getShop(player);
			if (s instanceof PlayerShop && (s.getOwner().equals(hp) || player.hasPermission("hyperconomy.admin"))) {
				currentShop.put(hp, (PlayerShop)s);
			}
		}
		PlayerShop cps = null;
		if (currentShop.containsKey(hp)) {
			cps = currentShop.get(hp);
		}
		if (args.length == 0) {
			player.sendMessage("Use /manageshop [select, create, set]");
			return true;
		}
		if (args[0].equalsIgnoreCase("select")) {
			if (args.length == 1) {
				player.sendMessage("Use /manageshop select [shop]");
				return true;
			}
			if (!he.shopExists(args[1])) {
				player.sendMessage(L.get("SHOP_NOT_EXIST"));
				return true;
			}
			Shop s = he.getShop(args[1]);
			if ((!(s instanceof PlayerShop) || !(s.getOwner().equals(hp))) && !player.hasPermission("hyperconomy.admin")) {
				player.sendMessage("You can only edit your own shops!");
				return true;
			}
			currentShop.put(hp, (PlayerShop)s);
		} else  if (args[0].equalsIgnoreCase("create")) {
			if (args.length == 1) {
				player.sendMessage("Use /manageshop create [shop name] (radius)");
				return true;
			}
			if (he.shopExists(args[1])){
				player.sendMessage("A shop by that name already exists.");
				return true;
			}
			String name = args[1];
			int radius = 2;
			if (args.length > 2) {
				try {
					radius = Integer.parseInt(args[2]);
				} catch (Exception e) {
					//continue
				}
			}
			PlayerShop newShop = new PlayerShop(name, hp);
			Location l = player.getLocation();
			newShop.setPoint1(player.getWorld().getName(), l.getBlockX() - radius, l.getBlockY() - radius, l.getBlockZ() - radius);
			newShop.setPoint2(player.getWorld().getName(), l.getBlockX() + radius, l.getBlockY() + radius, l.getBlockZ() + radius);
			he.addShop(newShop);
			player.sendMessage("Shop created.");
		} else if (args[0].equalsIgnoreCase("set1")) {
			if (cps == null) {
				player.sendMessage("You don't have a shop selected.");
				return true;
			}
			cps.setPoint1(player);
			player.sendMessage(L.get("P1_SET"));
		} else if (args[0].equalsIgnoreCase("set2")) {
			if (cps == null) {
				player.sendMessage("You don't have a shop selected.");
				return true;
			}
			cps.setPoint2(player);
			player.sendMessage(L.get("P2_SET"));
		} else if (args[0].equalsIgnoreCase("price")) {
			if (cps == null) {
				player.sendMessage("You don't have a shop selected.");
				return true;
			}
			if (args.length != 3) {
				player.sendMessage("Use /manageshop price [object] [price]");
				return true;
			}
			double price = 0.0;
			try {
				price = Double.parseDouble(args[2]);
			} catch (Exception e) {
				player.sendMessage("Use /manageshop price [object] [price]");
				return true;
			}
			if (!he.itemTest(args[1])) {
				player.sendMessage("The specified object does not exist.");
				return true;
			}
			HyperObject ho = he.getHyperObject(args[1], cps);
			if (ho instanceof PlayerShopObject) {
				((PlayerShopObject) ho).setPrice(price);
				player.sendMessage("Price set.");
				return true;
			} else {
				player.sendMessage("Setting the price has failed.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("status")) {
			if (cps == null) {
				player.sendMessage("You don't have a shop selected.");
				return true;
			}
			if (args.length != 3) {
				player.sendMessage("Use /manageshop status [object] ['trade', 'sell' or 'buy']");
				return true;
			}
			HyperObjectStatus status = HyperObjectStatus.fromString(args[2]);
			if (status == HyperObjectStatus.NONE) {
				player.sendMessage("That is not a valid status.");
				return true;
			}
			if (!he.itemTest(args[1])) {
				player.sendMessage("The specified object does not exist.");
				return true;
			}
			HyperObject ho = he.getHyperObject(args[1], cps);
			if (ho instanceof PlayerShopObject) {
				((PlayerShopObject) ho).setStatus(status);
				player.sendMessage("Status set.");
				return true;
			} else {
				player.sendMessage("Setting the status has failed.");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("owner") && player.hasPermission("hyperconomy.admin")) {
			if (cps == null) {
				player.sendMessage("You don't have a shop selected.");
				return true;
			}
			if (args.length != 2) {
				player.sendMessage("Use /manageshop owner [name]");
				return true;
			}
			if (!he.hasAccount(args[1])) {
				player.sendMessage("That account doesn't exist.");
				return true;
			}
			HyperPlayer newOwner = he.getHyperPlayer(args[1]);
			cps.setOwner(newOwner);
			player.sendMessage("Owner set.");
			return true;
		}

		
		
		return true;
	}
	
	

}
