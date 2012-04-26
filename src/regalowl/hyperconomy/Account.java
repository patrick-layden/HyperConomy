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
	private HyperConomy hc;
	private Player player;
	private Economy economy;
	
	/**
	 * 
	 * 
	 * This function sets up an account for a player.
	 * 
	 */
	public void setAccount(HyperConomy hyperc, Player p, Economy e){		
		hc = hyperc;
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
	
	
	
	/**
	 * 
	 * 
	 * This function withdraws money from the shop's account.
	 * 
	 */
	public void withdrawShop(double money){
		if (economy != null) {
			economy.withdrawPlayer("hyperconomy", money);
		} else {
			Bukkit.broadcast(ChatColor.DARK_RED + "No economy plugin detected! No money can be gained or lost.", "hyperconomy.error");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("No economy plugin detected! No money can be gained or lost. Please read the installation guide here: http://dev.bukkit.org/server-mods/hyperconomy/pages/quick-installation-guide/");
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function deposits money into the shop's account.
	 * 
	 */
	public void depositShop(double money){		
		if (economy != null) {
			economy.depositPlayer("hyperconomy", money);
		} else {
			Bukkit.broadcast(ChatColor.DARK_RED + "No economy plugin detected! No money can be gained or lost.", "hyperconomy.error");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("No economy plugin detected! No money can be gained or lost. Please read the installation guide here: http://dev.bukkit.org/server-mods/hyperconomy/pages/quick-installation-guide/");
		}
	}
	
	
	
	
	/**
	 * 
	 * 
	 * This function sets an account's balance.
	 * 
	 */
	public void setBalance(String name, double balance){		
		if (economy != null) {
			if (economy.hasAccount(name)) {
				economy.withdrawPlayer(name, economy.getBalance(name));
				economy.depositPlayer(name, balance);
			} else {
				economy.createPlayerAccount(name);
				economy.depositPlayer(name, balance);
			}
			
		} else {
			Bukkit.broadcast(ChatColor.DARK_RED + "No economy plugin detected! No money can be gained or lost.", "hyperconomy.error");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("No economy plugin detected! No money can be gained or lost. Please read the installation guide here: http://dev.bukkit.org/server-mods/hyperconomy/pages/quick-installation-guide/");
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function tests if an account exists.
	 * 
	 */
	public boolean checkAccount(String name){
		boolean hasaccount = false;
		if (economy != null) {
			if (economy.hasAccount(name)) {
				hasaccount = true;
			}
			return hasaccount;
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
	 * This function checks if the global shop has enough money for a transaction.
	 * 
	 */
	public boolean checkshopBalance(double money){		
		if (economy != null) {
			
			boolean result = false;
			if ((economy.getBalance("hyperconomy") - money) >= 0) {
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
	 * This function creates the global shop account when the server starts if it doesn't already exist.
	 * 
	 */
	public void checkshopAccount(){		
		if (economy != null) {
			
			if (!economy.hasAccount("hyperconomy")) {
				setBalance("hyperconomy", hc.getYaml().getConfig().getDouble("config.initialshopbalance"));
			}
			
		} else {
			Bukkit.broadcast(ChatColor.DARK_RED + "No economy plugin detected! No money can be gained or lost.", "hyperconomy.error");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("No economy plugin detected! No money can be gained or lost. Please read the installation guide here: http://dev.bukkit.org/server-mods/hyperconomy/pages/quick-installation-guide/");
		}
	}
	
	
}
