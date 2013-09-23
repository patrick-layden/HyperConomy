package regalowl.hyperconomy;


import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import regalowl.databukkit.SQLWrite;



public class HyperPlayer {

	private HyperConomy hc;
	private TransactionProcessor tp;
	private EconomyManager em;
	private Economy econ;
	private LanguageFile L;
	
	private String name;
	private String economy;
	private double balance;
	private double x;
	private double y;
	private double z;
	private String world;
	private String hash;
	private String salt;
	
	
	HyperPlayer(String player) {
		hc = HyperConomy.hc;
		tp = new TransactionProcessor(this);
		em = hc.getEconomyManager();
		SQLWrite sw = hc.getSQLWrite();
		try {
			balance = hc.getConfig().getDouble("config.starting-player-account-balance");
		} catch (Exception e) {
			hc.gDB().writeError(e);
			balance = 0;
		}
		economy = "default";
		for (Player p:Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(player)) {
				name = p.getName();
				x = p.getLocation().getX();
				y = p.getLocation().getY();
				z = p.getLocation().getZ();
				world = p.getLocation().getWorld().getName();
				sw.executeSQL("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT)" + " VALUES ('" + name + "','" + economy + "','" + balance + "','" + x + "','" + y + "','" + z + "','" + world + "','','')");
				return;
			}
		}
		name = player;
		sw.executeSQL("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT)" + " VALUES ('" + name + "','" + economy + "','" + balance + "','" + 0 + "','" + 0 + "','" + 0 + "','" + "world" + "','','')");
		econ = hc.getEconomy();
		L = hc.getLanguageFile();
	}
	
	
	HyperPlayer(String name, String economy, double balance, double x, double y, double z, String world, String hash, String salt) {
		hc = HyperConomy.hc;
		tp = new TransactionProcessor(this);
		em = hc.getEconomyManager();
		this.name = name;
		this.economy = economy;
		this.balance = balance;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.hash = hash;
		this.salt = salt;
		econ = hc.getEconomy();
		L = hc.getLanguageFile();
	}
	
	public String getName() {
		return name;
	}
	public String getEconomy() {
		return economy;
	}
	public HyperEconomy getHyperEconomy() {
		return em.getEconomy(economy);
	}
	public double getBalance() {
		if (hc.s().gB("use-external-economy-plugin")) {
			if (econ != null) {
				return econ.getBalance(name);
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
		    	return 0.0;
			}
		} else {
			return balance;
		}
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public String getWorld() {
		return world;
	}
	public String getHash() {
		return hash;
	}
	public String getSalt() {
		return salt;
	}
	
	
	public void setName(String name) {
		String statement = "UPDATE hyperconomy_players SET PLAYER='" + name + "' WHERE PLAYER = '" + this.name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.name = name;
	}
	public void setEconomy(String economy) {
		em.getEconomy(this.economy).removeHyperPlayer(this);
		String statement = "UPDATE hyperconomy_players SET ECONOMY='" + economy + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.economy = economy;
		em.getEconomy(this.economy).addHyperPlayer(this);
	}
	public void setX(double x) {
		String statement = "UPDATE hyperconomy_players SET X='" + x + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.x = x;
	}
	public void setY(double y) {
		String statement = "UPDATE hyperconomy_players SET Y='" + y + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.y = y;
	}
	public void setZ(double z) {
		String statement = "UPDATE hyperconomy_players SET Z='" + z + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.z = z;
	}
	public void setWorld(String world) {
		String statement = "UPDATE hyperconomy_players SET WORLD='" + world + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.world = world;
	}
	public void setHash(String hash) {
		String statement = "UPDATE hyperconomy_players SET HASH='" + hash + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.hash = hash;
	}
	public void setSalt(String salt) {
		String statement = "UPDATE hyperconomy_players SET SALT='" + salt + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.salt = salt;
	}
	
	
	public Player getPlayer() {
		return Bukkit.getPlayer(name);
	}
	
	public Inventory getInventory() {
		Player p = Bukkit.getPlayer(name);
		if (p != null) {
			return p.getInventory();
		} else {
			return null;
		}
	}

	public boolean sendMessage(String message) {
		if (getPlayer() != null) {
			getPlayer().sendMessage(message);
			return true;
		}
		return false;
	}
	
	public double getSalesTax(Double price) {
		Calculation calc = hc.getCalculation();
		double salestax = 0;
		if (hc.gYH().gFC("config").getBoolean("config.dynamic-tax.use-dynamic-tax")) {
			double moneyfloor = hc.gYH().gFC("config").getDouble("config.dynamic-tax.money-floor");
			double moneycap = hc.gYH().gFC("config").getDouble("config.dynamic-tax.money-cap");
			double cbal = getBalance();
			double maxtaxrate = hc.gYH().gFC("config").getDouble("config.dynamic-tax.max-tax-percent") / 100.0;
			if (cbal >= moneycap) {
				salestax = price * maxtaxrate;
			} else if (cbal <= moneyfloor) {
				salestax = 0;
			} else {
				double taxrate = ((cbal - moneyfloor) / (moneycap - moneyfloor));
				if (taxrate > maxtaxrate) {
					taxrate = maxtaxrate;
				}
				salestax = price * taxrate;
			}
		} else {
			double salestaxpercent = hc.gYH().gFC("config").getDouble("config.sales-tax-percent");
			salestax = calc.twoDecimals((salestaxpercent / 100) * price);
		}
		return salestax;
	}
	
	public TransactionResponse processTransaction(PlayerTransaction playerTransaction) {
		return tp.processTransaction(playerTransaction);
	}
	
	
	public boolean hasSellPermission(Shop s) {
		if (!hc.s().gB("use-shop-permissions")) {
			return true;
		}
		boolean hasPermission = false;
		if (getPlayer().isPermissionSet("hyperconomy.shop")) {
			hasPermission = getPlayer().hasPermission("hyperconomy.shop");
		}
		if (getPlayer().isPermissionSet("hyperconomy.shop." + s.getName())) {
			hasPermission = getPlayer().hasPermission("hyperconomy.shop." + s.getName());
		}
		if (getPlayer().isPermissionSet("hyperconomy.shop." + s.getName() + ".sell")) {
			hasPermission = getPlayer().hasPermission("hyperconomy.shop." + s.getName() + ".sell");
		}
		return hasPermission;
	}
	
	public boolean hasBuyPermission(Shop s) {
		if (!hc.s().gB("use-shop-permissions")) {
			return true;
		}
		boolean hasPermission = false;
		if (getPlayer().isPermissionSet("hyperconomy.shop")) {
			hasPermission = getPlayer().hasPermission("hyperconomy.shop");
		}
		if (getPlayer().isPermissionSet("hyperconomy.shop." + s.getName())) {
			hasPermission = getPlayer().hasPermission("hyperconomy.shop." + s.getName());
		}
		if (getPlayer().isPermissionSet("hyperconomy.shop." + s.getName() + ".buy")) {
			hasPermission = getPlayer().hasPermission("hyperconomy.shop." + s.getName() + ".buy");
		}
		return hasPermission;
	}
	
	
	public boolean hasBalance(double amount) {
		if ((getBalance() - amount) >= 0) {
			return true;
		}
		return false;
	}
	public void setBalance(double balance) {
		if (hc.s().gB("use-external-economy-plugin")) {
			if (econ != null) {
				if (econ.hasAccount(name)) {
					econ.withdrawPlayer(name, econ.getBalance(name));
				} else {
					econ.createPlayerAccount(name);
				}
				econ.depositPlayer(name, balance);
				hc.getLog().writeAuditLog(name, "setbalance", balance, econ.getName());
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			this.balance = balance;
			String statement = "UPDATE hyperconomy_players SET BALANCE='" + balance + "' WHERE PLAYER = '" + name + "'";
			hc.getSQLWrite().executeSQL(statement);
			hc.getLog().writeAuditLog(name, "setbalance", balance, "HyperConomy");
		}
	}
	public void deposit(double amount) {
		if (hc.s().gB("use-external-economy-plugin")) {
			if (econ != null) {
				econ.depositPlayer(name, amount);
				hc.getLog().writeAuditLog(name, "deposit", amount, econ.getName());
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			this.balance += amount;
			String statement = "UPDATE hyperconomy_players SET BALANCE='" + balance + "' WHERE PLAYER = '" + name + "'";
			hc.getSQLWrite().executeSQL(statement);
			hc.getLog().writeAuditLog(name, "deposit", amount, "HyperConomy");
		}
	}
	
	public void withdraw(double amount) {
		if (hc.s().gB("use-external-economy-plugin")) {
			if (econ != null) {
				econ.withdrawPlayer(name, amount);
				hc.getLog().writeAuditLog(name, "withdrawal", amount, econ.getName());
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			this.balance -= amount;
			String statement = "UPDATE hyperconomy_players SET BALANCE='" + balance + "' WHERE PLAYER = '" + name + "'";
			hc.getSQLWrite().executeSQL(statement);
			hc.getLog().writeAuditLog(name, "withdrawal", amount, "HyperConomy");
		}
	}

	
}
