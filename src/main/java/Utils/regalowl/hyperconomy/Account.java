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
	
	/**
	 * 
	 * 
	 * This function determines if a players balance is greater than the given amount of money.
	 * 
	 */
	public boolean checkFunds(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		
		if (useExternalEconomy) {
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
		} else {
			boolean result = false;
			if ((sf.getPlayerBalance(player) - money) >= 0) {
				result = true;
			}
			return result;
		}
	}
	
	/**
	 * 
	 * 
	 * This function withdraws money from a player's account.
	 * 
	 */
	public void withdraw(double money, Player player){
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				String name = player.getName();
				economy.withdrawPlayer(name, money);
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			sf.setPlayerBalance(player, sf.getPlayerBalance(player) - money);
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function withdraws money from a player's account.
	 * 
	 */
	public void withdrawAccount(String name, double money){
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				economy.withdrawPlayer(name, money);
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			sf.setPlayerBalance(name, sf.getPlayerBalance(name) - money);
		}
	}
	
	/**
	 * 
	 * 
	 * This function deposits money into a player's account.
	 * 
	 */
	public void deposit(double money, Player player){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				String name = player.getName();
				economy.depositPlayer(name, money);
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			sf.setPlayerBalance(player, sf.getPlayerBalance(player) + money);
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function deposits money into a player's account.
	 * 
	 */
	public void depositAccount(String name, double money){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				economy.depositPlayer(name, money);
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			sf.setPlayerBalance(name, sf.getPlayerBalance(name) + money);
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function withdraws money from the shop's account.
	 * 
	 */
	public void withdrawShop(double money){
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
				economy.withdrawPlayer(globalaccount, money);
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
			sf.setPlayerBalance(globalaccount, sf.getPlayerBalance(globalaccount) - money);
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function deposits money into the shop's account.
	 * 
	 */
	public void depositShop(double money){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
				economy.depositPlayer(globalaccount, money);
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
			sf.setPlayerBalance(globalaccount, sf.getPlayerBalance(globalaccount) + money);
		}
	}
	
	
	
	
	/**
	 * 
	 * 
	 * This function sets an account's balance.
	 * 
	 */
	public void setBalance(String name, double balance){	
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
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
		} else {
			sf.setPlayerBalance(name, balance);
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function tests if an account exists.
	 * 
	 */
	public boolean checkAccount(String name){
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
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
		} else {
			return sf.hasAccount(name);
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function checks if the global shop has enough money for a transaction.
	 * 
	 */
	public boolean checkshopBalance(double money){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
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
		} else {
			String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
			boolean result = false;
			if ((sf.getPlayerBalance(globalaccount) - money) >= 0) {
				result = true;
			}
			return result;
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function creates the global shop account when the server starts if it doesn't already exist.
	 * 
	 */
	public void checkshopAccount(){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
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
		} else {
			String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
			if (!sf.hasAccount(globalaccount)) {
				sf.createPlayerAccount(globalaccount);
			}
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function returns the current balance of an account.
	 * 
	 */
	public double getBalance(String account){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {

			if (economy != null) {
				
				return economy.getBalance(account);
				
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
		    	return 0;
			}
		} else {
			return sf.getPlayerBalance(account);
		}
	}
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function creates a new account if it doesn't already exist.
	 * 
	 */
	public void createAccount(String account){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions sf = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
	
				if (!economy.hasAccount(account)) {
					setBalance(account, 0);
				}
				
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			sf.createPlayerAccount(account);
		}
	}
	
	
}
