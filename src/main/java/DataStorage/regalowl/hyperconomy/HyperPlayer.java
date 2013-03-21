package regalowl.hyperconomy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;



public class HyperPlayer {

	private HyperConomy hc;
	private TransactionProcessor tp;
	
	private String name;
	private String economy;
	private double balance;
	private double x;
	private double y;
	private double z;
	private String world;
	private String hash;
	private String salt;
	
	
	HyperPlayer() {
		hc = HyperConomy.hc;
		tp = new TransactionProcessor(this);
	}
	
	
	HyperPlayer(String player) {
		hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		for (Player p:Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(player)) {
				name = p.getName();
				economy = "default";
				balance = 0.0;
				x = p.getLocation().getX();
				y = p.getLocation().getY();
				z = p.getLocation().getZ();
				world = p.getLocation().getWorld().getName();
				sw.executeSQL("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT)" + " VALUES ('" + name + "','" + economy + "','" + balance + "','" + x + "','" + y + "','" + z + "','" + world + "','','')");
				return;
			}
		}
		name = player;
		economy = "default";
		balance = 0.0;
		sw.executeSQL("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT)" + " VALUES ('" + name + "','" + economy + "','" + balance + "','" + 0 + "','" + 0 + "','" + 0 + "','" + "world" + "','','')");
		
	}
	
	
	public void create() {
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT) VALUES ('" + name + "','" + economy + "','" + balance + "','" + x + "','" + y + "','" + z + "','" + world + "','" + hash + "','" + salt + "')");
	}
	
	public void save() {
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL("UPDATE hyperconomy_players SET PLAYER='" + this.name + "', ECONOMY='" + this.economy + 
				"', BALANCE='" + this.balance + "', X='" + this.x + "', Y='" + this.y + "', Z='" + this.z + 
				"', WORLD='" + this.world + "', SALT='" + this.salt + "', HASH='" + this.hash + "' WHERE PLAYER = '" + this.name + "'");
		
	}
	
	
	public String getName() {
		return name;
	}
	public String getEconomy() {
		return economy;
	}
	public double getBalance() {
		return balance;
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
		String statement = "UPDATE hyperconomy_players SET ECONOMY='" + economy + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.economy = economy;
	}
	public void setBalance(double balance) {
		String statement = "UPDATE hyperconomy_players SET BALANCE='" + balance + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().executeSQL(statement);
		this.balance = balance;
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
		Account acc = hc.getAccount();
		double salestax = 0;
		if (hc.getYaml().getConfig().getBoolean("config.dynamic-tax.use-dynamic-tax")) {
			double moneyfloor = hc.getYaml().getConfig().getDouble("config.dynamic-tax.money-floor");
			double moneycap = hc.getYaml().getConfig().getDouble("config.dynamic-tax.money-cap");
			double cbal = acc.getBalance(name);
			double maxtaxrate = hc.getYaml().getConfig().getDouble("config.dynamic-tax.max-tax-percent") / 100.0;
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
			double salestaxpercent = hc.getYaml().getConfig().getDouble("config.sales-tax-percent");
			salestax = (salestaxpercent / 100) * price;
		}
		return salestax;
	}
	
	public TransactionResponse processTransaction(PlayerTransaction playerTransaction) {
		return tp.processTransaction(playerTransaction);
	}
	
}
