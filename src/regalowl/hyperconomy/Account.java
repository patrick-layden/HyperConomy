package regalowl.hyperconomy;



import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;



/**
 * 
 * 
 * This class deals with player accounts.
 * 
 */
public class Account {	
	private Player player;
	private Economy economy;
	
	/**
	 * 
	 * 
	 * This constructor function sets up an account for a player.
	 * 
	 */
	public void setAccount(Player p, Economy e){		
		player = p;
		economy = e;
	}
	
	/**
	 * 
	 * 
	 * This function determines if a players balance is greater than the given amount of money.
	 * 
	 */
	public boolean checkFunds(double money) {
		if (economy != null) {
			boolean result = false;
			String name = player.getName();
			if ((economy.getBalance(name) - money) >= 0) {
				result = true;
			}
			return result;
		} else {
			Bukkit.broadcast(ChatColor.DARK_RED + "No economy plugin detected! No money can be gained or lost.", "hyperconomy.error");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("No economy plugin detected! No money can be gained or lost. Please read the installation guide here: http://dev.bukkit.org/server-mods/hyperconomy/pages/quick-installation-guide/");
			return false;
		}
	}
	
	/**
	 * 
	 * 
	 * This function withdraws money from a player's account.
	 * 
	 */
	public void withdraw(double money){
		if (economy != null) {
			String name = player.getName();
			economy.withdrawPlayer(name, money);
		} else {
			Bukkit.broadcast(ChatColor.DARK_RED + "No economy plugin detected! No money can be gained or lost.", "hyperconomy.error");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("No economy plugin detected! No money can be gained or lost. Please read the installation guide here: http://dev.bukkit.org/server-mods/hyperconomy/pages/quick-installation-guide/");
		}
	}
	
	/**
	 * 
	 * 
	 * This function deposits money into a player's account.
	 * 
	 */
	public void deposit(double money){		
		if (economy != null) {
			String name = player.getName();
			economy.depositPlayer(name, money);
		} else {
			Bukkit.broadcast(ChatColor.DARK_RED + "No economy plugin detected! No money can be gained or lost.", "hyperconomy.error");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("No economy plugin detected! No money can be gained or lost. Please read the installation guide here: http://dev.bukkit.org/server-mods/hyperconomy/pages/quick-installation-guide/");
		}
	}
}
