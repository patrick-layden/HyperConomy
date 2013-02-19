package regalowl.hyperconomy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;



public class HyperPlayer {

	private HyperConomy hc;
	
	
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
	
	
	public void save() {
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH, SALT)" + " VALUES ('" + name + "','" + economy + "','" + balance + "','" + x + "','" + y + "','" + z + "','" + world + "','" + hash + "','" + salt + "')");
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
	
	
}
