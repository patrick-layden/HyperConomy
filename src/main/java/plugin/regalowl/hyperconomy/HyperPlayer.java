package regalowl.hyperconomy;

import static java.lang.System.out;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.SQLWrite;



public class HyperPlayer {

	private HyperConomy hc;
	private TransactionProcessor tp;
	private EconomyManager em;
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
		balance = hc.gYH().gFC("config").getDouble("config.starting-player-account-balance");
		economy = "default";
		boolean playerOnline = false;
		for (Player p:Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(player)) {
				name = p.getName();
				x = p.getLocation().getX();
				y = p.getLocation().getY();
				z = p.getLocation().getZ();
				world = p.getLocation().getWorld().getName();
				sw.addToQueue("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT)" + " VALUES ('" + name + "','" + economy + "','" + balance + "','" + x + "','" + y + "','" + z + "','" + world + "','','')");
				playerOnline = true;
				break;
			}
		}
		if (!playerOnline) {
			name = player;
			sw.addToQueue("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT)" + " VALUES ('" + name + "','" + economy + "','" + balance + "','" + 0 + "','" + 0 + "','" + 0 + "','" + "world" + "','','')");
		}
		createExternalAccount();
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
		createExternalAccount();
	}
	
	private void createExternalAccount() {
		if (!hc.useExternalEconomy()) {return;}
		if (!hc.getEconomy().hasAccount(name)) {
			hc.getEconomy().createPlayerAccount(name);
			setBalance(balance);
		}

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
		if (hc.useExternalEconomy()) {
			return hc.getEconomy().getBalance(name);
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
	
	public void delete() {
		hc.getEconomyManager().removeHyperPlayer(this);
		String statement = "DELETE FROM hyperconomy_players WHERE PLAYER = '" + this.name + "'";
		hc.getSQLWrite().addToQueue(statement);
	}
	
	public void setName(String name) {
		String statement = "UPDATE hyperconomy_players SET PLAYER='" + name + "' WHERE PLAYER = '" + this.name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.name = name;
	}
	public void setEconomy(String economy) {
		String statement = "UPDATE hyperconomy_players SET ECONOMY='" + economy + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.economy = economy;
	}
	public void setX(double x) {
		String statement = "UPDATE hyperconomy_players SET X='" + x + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.x = x;
	}
	public void setY(double y) {
		String statement = "UPDATE hyperconomy_players SET Y='" + y + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.y = y;
	}
	public void setZ(double z) {
		String statement = "UPDATE hyperconomy_players SET Z='" + z + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.z = z;
	}
	public void setWorld(String world) {
		String statement = "UPDATE hyperconomy_players SET WORLD='" + world + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.world = world;
	}
	public void setHash(String hash) {
		String statement = "UPDATE hyperconomy_players SET HASH='" + hash + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.hash = hash;
	}
	public void setSalt(String salt) {
		String statement = "UPDATE hyperconomy_players SET SALT='" + salt + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().addToQueue(statement);
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
		CommonFunctions cf = hc.gCF();
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
			salestax = cf.twoDecimals((salestaxpercent / 100) * price);
		}
		return salestax;
	}
	
	
	public TransactionResponse processTransaction(PlayerTransaction playerTransaction) {
		return tp.processTransaction(playerTransaction);
	}
	
	
	public boolean hasSellPermission(Shop s) {
		if (!hc.gYH().gQFC("config").gB("config.use-shop-permissions")) {
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
		if (!(hc.gYH().gQFC("config").gB("config.use-shop-permissions"))) {
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
		if (hc.useExternalEconomy()) {
			if (hc.getEconomy().hasAccount(name)) {
				hc.getEconomy().withdrawPlayer(name, hc.getEconomy().getBalance(name));
			} else {
				hc.getEconomy().createPlayerAccount(name);
			}
			hc.getEconomy().depositPlayer(name, balance);
			hc.getLog().writeAuditLog(name, "setbalance", balance, hc.getEconomy().getName());
		} else {
			this.balance = balance;
			String statement = "UPDATE hyperconomy_players SET BALANCE='" + balance + "' WHERE PLAYER = '" + name + "'";
			hc.getSQLWrite().addToQueue(statement);
			hc.getLog().writeAuditLog(name, "setbalance", balance, "HyperConomy");
		}
	}
	public void setInternalBalance(double balance) {
		this.balance = balance;
		String statement = "UPDATE hyperconomy_players SET BALANCE='" + balance + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().addToQueue(statement);
		hc.getLog().writeAuditLog(name, "setbalance", balance, "HyperConomy");
	}
	public void deposit(double amount) {
		if (hc.useExternalEconomy()) {
			hc.getEconomy().depositPlayer(name, amount);
			hc.getLog().writeAuditLog(name, "deposit", amount, hc.getEconomy().getName());
		} else {
			this.balance += amount;
			String statement = "UPDATE hyperconomy_players SET BALANCE='" + balance + "' WHERE PLAYER = '" + name + "'";
			hc.getSQLWrite().addToQueue(statement);
			hc.getLog().writeAuditLog(name, "deposit", amount, "HyperConomy");
		}
	}
	
	public void withdraw(double amount) {
		if (hc.useExternalEconomy()) {
			hc.getEconomy().withdrawPlayer(name, amount);
			hc.getLog().writeAuditLog(name, "withdrawal", amount, hc.getEconomy().getName());
		} else {
			this.balance -= amount;
			String statement = "UPDATE hyperconomy_players SET BALANCE='" + balance + "' WHERE PLAYER = '" + name + "'";
			hc.getSQLWrite().addToQueue(statement);
			hc.getLog().writeAuditLog(name, "withdrawal", amount, "HyperConomy");
		}
	}

	
}
