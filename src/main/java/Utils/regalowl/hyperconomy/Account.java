package regalowl.hyperconomy;



import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
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
	private LanguageFile L;
	
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
		L = hc.getLanguageFile();
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
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
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
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function withdraws money from a player's account.
	 * 
	 */
	public void withdrawAccount(String name, double money){
		if (economy != null) {
			economy.withdrawPlayer(name, money);
		} else {
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
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
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function deposits money into a player's account.
	 * 
	 */
	public void depositAccount(String name, double money){		
		if (economy != null) {
			economy.depositPlayer(name, money);
		} else {
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
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
			String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
			economy.withdrawPlayer(globalaccount, money);
		} else {
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
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
			String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
			economy.depositPlayer(globalaccount, money);
		} else {
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
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
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
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
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
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
			
			String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
			boolean result = false;
			if ((economy.getBalance(globalaccount) - money) >= 0) {
				result = true;
			}
			return result;
			
		} else {
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
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
			String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
			if (!economy.hasAccount(globalaccount)) {
				setBalance(globalaccount, hc.getYaml().getConfig().getDouble("config.initialshopbalance"));
			}
			
		} else {
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function returns the current balance of an account.
	 * 
	 */
	public double getBalance(String account){		
		if (economy != null) {
			
			return economy.getBalance(account);
			
		} else {
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
	    	return 0;
		}
	}
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function creates a new account if it doesn't already exist.
	 * 
	 */
	public void createAccount(String account){		
		if (economy != null) {

			if (!economy.hasAccount(account)) {
				setBalance(account, 0);
			}
			
		} else {
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function deletes an account.
	 * 
	 */
	public void deleteAccount(String account){		
		if (economy != null) {

			if (economy.hasAccount(account)) {
				//TODO  Currently don't know how.
			}
			
		} else {
			Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
		}
	}
	
	
}
