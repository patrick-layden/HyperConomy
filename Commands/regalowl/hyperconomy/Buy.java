package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;




public class Buy {
	
	
	private SQLFunctions sf;
	private HyperConomy hc;
	private Transaction tran;
	private Calculation calc;
	private Shop s;
	
	private String name;
	private String playerecon;
	private Player player;
	private String[] args;
	private String message;
	private boolean complete;
	private int completetaskid;

	public void buy(HyperConomy hyc, String[] argsi, Player p, String nam) {
		
		hc = hyc;
		calc = hc.getCalculation();
		tran = hc.getTransaction();
		sf = hc.getSQLFunctions();
		s = hc.getShop();
		name = nam;
		player = p;
		args = argsi;
		complete = false;
		
		if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".buy")) {
		
			hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
				public void run() {
					
					buyThread();

				}
			});
		
		} else {
			message = "§9Sorry, you don't have permission to trade here.";
		}
		
		completetaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				if (complete) {
					hc.getServer().getScheduler().cancelTask(completetaskid);
					player.sendMessage(message);
				}
			}
		}, 1L, 1L);
	}
	
	
	
	private void buyThread() {
		playerecon = sf.getPlayerEconomy(player.getName());

		int amount = 0;
		String teststring = hc.testiString(name);
		
		int id = sf.getId(name, playerecon);
		int data = sf.getData(name, playerecon);

		if (teststring != null) {
		if (args.length == 1) {
			amount = 1;
		} else {
			
			try {
				amount = Integer.parseInt(args[1]);
			} catch (Exception e) {
				String max = args[1];
				if (max.equalsIgnoreCase("max")) {
					MaterialData damagemd = new MaterialData(id, (byte) data);
					ItemStack damagestack = damagemd.toItemStack();
					tran.setSpace(player, calc);
					int space = 0;
					if (id >= 0) {
						space = tran.getavailableSpace(id, calc.getdamageValue(damagestack));
					}

					amount = space;
					int shopstock = (int) sf.getStock(name, playerecon);

					//Buys the most possible from the shop if the amount is more than that for max.
					if (amount > shopstock) {
						amount = shopstock;
					}			
				} else {
					message = "§4Invalid Parameters.  Use /buy [name] (amount or 'max')";
					complete = true;
					return;
				}			
			}
		}	

		}

	if (teststring != null) {
		if (s.has(s.getShop(player), name)) {	
			tran.setAll(hc, id, data, amount, name, player, hc.getEconomy(), calc, hc.getETransaction(), hc.getLog(), hc.getAccount(), hc.getNotify(), hc.getInfoSign());
			tran.buy();
		} else {
			message ="§9Sorry, that item or enchantment cannot be traded at this shop.";
		}

	} else {
		message = "§4Invalid item name.";
	}
	complete = true;
	}
	
}
