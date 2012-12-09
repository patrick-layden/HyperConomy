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
		DataFunctions df = hc.getSQLFunctions();
		
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
			if ((df.getPlayerBalance(player) - money) >= 0) {
				result = true;
			}
			return result;
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function determines if an account's balance is greater than the given amount of money.
	 * 
	 */
	public boolean checkFunds(double money, String name) {
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions df = hc.getSQLFunctions();
		
		if (useExternalEconomy) {
			if (economy != null) {
				boolean result = false;
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
			if ((df.getPlayerBalance(name) - money) >= 0) {
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
		DataFunctions df = hc.getSQLFunctions();
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
			df.setPlayerBalance(player, df.getPlayerBalance(player) - money);
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function withdraws money from a player's account.
	 * 
	 */
	public void withdrawAccount(double money, String name){
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions df = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				economy.withdrawPlayer(name, money);
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			df.setPlayerBalance(name, df.getPlayerBalance(name) - money);
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
		DataFunctions df = hc.getSQLFunctions();
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
			df.setPlayerBalance(player, df.getPlayerBalance(player) + money);
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function deposits money into a player's account.
	 * 
	 */
	public void depositAccount(double money, String name){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions df = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				economy.depositPlayer(name, money);
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			df.setPlayerBalance(name, df.getPlayerBalance(name) + money);
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
		DataFunctions df = hc.getSQLFunctions();
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
			df.setPlayerBalance(globalaccount, df.getPlayerBalance(globalaccount) - money);
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
		DataFunctions df = hc.getSQLFunctions();
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
			df.setPlayerBalance(globalaccount, df.getPlayerBalance(globalaccount) + money);
		}
	}
	
	
	
	
	/**
	 * 
	 * 
	 * This function sets an account's balance.
	 * 
	 */
	public void setBalance(double balance, String name){	
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions df = hc.getSQLFunctions();
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
			df.setPlayerBalance(name, balance);
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
		DataFunctions df = hc.getSQLFunctions();
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
			return df.hasAccount(name);
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
		DataFunctions df = hc.getSQLFunctions();
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
			if ((df.getPlayerBalance(globalaccount) - money) >= 0) {
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
		DataFunctions df = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
				if (!economy.hasAccount(globalaccount)) {
					setBalance(hc.getYaml().getConfig().getDouble("config.initialshopbalance"), globalaccount);
				}
				
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
			if (!df.hasAccount(globalaccount)) {
				df.createPlayerAccount(globalaccount);
				df.setPlayerBalance(globalaccount, hc.getYaml().getConfig().getDouble("config.initialshopbalance"));
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
		Calculation calc = hc.getCalculation();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions df = hc.getSQLFunctions();
		if (useExternalEconomy) {

			if (economy != null) {
				
				return calc.twoDecimals(economy.getBalance(account));
				
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
		    	return 0.0;
			}
		} else {
			return calc.twoDecimals(df.getPlayerBalance(account));
		}
	}
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function creates a new account if it doesn't already exist.
	 * 
	 */
	public boolean createAccount(String account){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		boolean useExternalEconomy = hc.useExternalEconomy();
		DataFunctions df = hc.getSQLFunctions();
		if (useExternalEconomy) {
			if (economy != null) {
				if (!economy.hasAccount(account)) {
					setBalance(0, account);
					return true;
				} else {
					return false;
				}
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
		    	return false;
			}
		} else {
			return df.createPlayerAccount(account);
		}
	}
	
	
}
