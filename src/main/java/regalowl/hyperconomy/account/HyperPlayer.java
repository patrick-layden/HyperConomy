package regalowl.hyperconomy.account;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.sql.SQLWrite;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionProcessor;
import regalowl.hyperconomy.transaction.TransactionResponse;



public class HyperPlayer implements HyperAccount {

	private HyperConomy hc;
	private TransactionProcessor tp;
	private DataManager em;
	private String name;
	private String uuid;
	private String economy;
	private double balance;
	private double x;
	private double y;
	private double z;
	private String world;
	private String hash;
	private String salt;
	private boolean validUUID;
	
	
	public HyperPlayer(String player) {
		hc = HyperConomy.hc;
		tp = new TransactionProcessor(this);
		em = hc.getDataManager();
		SQLWrite sw = hc.getSQLWrite();
		balance = hc.getConf().getDouble("economy-plugin.starting-player-account-balance");
		economy = "default";
		Player p = getPlayer();
		if (p != null) {
			name = p.getName();
			if (hc.getHyperPlayerManager().uuidSupport()) {
				uuid = p.getUniqueId().toString();
			}
			x = p.getLocation().getX();
			y = p.getLocation().getY();
			z = p.getLocation().getZ();
			world = p.getLocation().getWorld().getName();
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("NAME", name);
			values.put("UUID", uuid);
			values.put("ECONOMY", economy);
			values.put("BALANCE", balance+"");
			values.put("X", x+"");
			values.put("Y", y+"");
			values.put("Z", z+"");
			values.put("WORLD", world);
			values.put("HASH", "");
			values.put("SALT", "");
			sw.performInsert("hyperconomy_players", values);
		} else {
			name = player;
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("NAME", name);
			values.put("ECONOMY", economy);
			values.put("BALANCE", balance+"");
			values.put("X", "0");
			values.put("Y", "0");
			values.put("Z", "0");
			values.put("WORLD", "world");
			values.put("HASH", "");
			values.put("SALT", "");
			sw.performInsert("hyperconomy_players", values);
		}
		checkUUID();
		checkExternalAccount();
	}
	
	
	public HyperPlayer(String name, String uuid, String economy, double balance, double x, double y, double z, String world, String hash, String salt) {
		hc = HyperConomy.hc;
		tp = new TransactionProcessor(this);
		em = hc.getDataManager();
		this.name = name;
		this.uuid = uuid;
		this.economy = economy;
		this.balance = balance;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.hash = hash;
		this.salt = salt;
		hc.getServer().getScheduler().runTask(hc, new Runnable() {
			public void run() {
				checkUUID();
				checkExternalAccount();
				checkForNameChange();
			}
		});
	}

	
	@SuppressWarnings("deprecation")
	private void checkExternalAccount() {
		if (!hc.useExternalEconomy()) {return;}
			if (name == null) {return;}
			if (!hc.getEconomy().hasAccount(name)) {
				hc.getEconomy().createPlayerAccount(name);
				setBalance(balance);
			}
	}
	
	public void checkUUID() {
		this.validUUID = false;
		if (!hc.getHyperPlayerManager().uuidSupport()) {return;}
		if (name == null) {return;}
		if (uuid == null || uuid == "") {
			@SuppressWarnings("deprecation")
			Player p = Bukkit.getPlayer(name);
			if (p == null) {return;}
			setUUID(p.getUniqueId().toString());
			if (uuid == null || uuid == "") {return;}
		}
		this.validUUID = true;
	}
	
	public void checkForNameChange() {
		if (uuid == null || uuid == "") {return;}
		Player p = null;
		try {
			p = Bukkit.getPlayer(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return;
		}
		if (p == null) {return;}
		if (p.getName().equals(name)) {return;}
		if (hc.useExternalEconomy()) {
			double oldBalance = getBalance();
			setBalance(0.0);
			setName(p.getName());
			setBalance(oldBalance);
		} else {
			setName(p.getName());
		}
	}
	
	public boolean validUUID() {
		return validUUID;
	}
	
	public String getName() {
		if (name == null || name == "") {
			return uuid;
		}
		return name;
	}
	public UUID getUUID() {
		if (uuid == null || uuid == "") {return null;}
		try {
			return UUID.fromString(uuid);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	public String getUUIDString() {
		return uuid;
	}
	public String getEconomy() {
		return economy;
	}
	public HyperEconomy getHyperEconomy() {
		return em.getEconomy(economy);
	}
	@SuppressWarnings("deprecation")
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
	
	public boolean safeToDelete() {
		if (balance > 0) {return false;}
		if (getPlayer() != null) {return false;}
		if (em.getHyperShopManager().getShops(this).size() > 0) {return false;}
		for (HyperEconomy he:em.getEconomies()) {
			if (he.getDefaultAccount().equals(this)) {
				return false;
			}
		}
		for (HyperBank hb:em.getHyperBankManager().getHyperBanks()) {
			if (hb.isOwner(this) || hb.isMember(this)) {
				return false;
			}
		}
		return true;
	}
	
	public void delete() {
		hc.getHyperPlayerManager().removeHyperPlayer(this);
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("NAME", name);
		hc.getSQLWrite().performDelete("hyperconomy_players", conditions);
	}
	
	public void setName(String name) {
		hc.getHyperPlayerManager().removeHyperPlayer(this);
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", this.name);
		values.put("NAME", name);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.name = name;
		hc.getHyperPlayerManager().addHyperPlayer(this);
	}
	public void setUUID(String uuid) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", this.name);
		values.put("UUID", uuid);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.uuid = uuid;
	}
	
	public void setEconomy(String economy) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("ECONOMY", economy);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.economy = economy;
	}
	public void setX(double x) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("X", x+"");
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.x = x;
	}
	public void setY(double y) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("Y", y+"");
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.y = y;
	}
	public void setZ(double z) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("Z", z+"");
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.z = z;
	}
	public void setWorld(String world) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("WORLD", world);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.world = world;
	}
	public void setHash(String hash) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("HASH", hash);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.hash = hash;
	}
	public void setSalt(String salt) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("SALT", salt);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.salt = salt;
	}
	
	

	@SuppressWarnings("deprecation")
	public Player getPlayer() {
		UUID id = getUUID();
		Player p = null;
		if (id != null && hc.getHyperPlayerManager().uuidSupport()) {
			p = Bukkit.getPlayer(id);
		}
		if (p == null && name != null) {
			p = Bukkit.getPlayer(name);
		}
		return p;
	}
	
	@SuppressWarnings("deprecation")
	public OfflinePlayer getOfflinePlayer() {
		UUID id = getUUID();
		OfflinePlayer op = null;
		if (id != null && hc.getHyperPlayerManager().uuidSupport()) {
			op = Bukkit.getOfflinePlayer(id);
			//if (op == null) {
			//	hc.log().severe("Null OfflinePlayer UUID [" + name + "]");
			//}
		}
		if (op == null && name != null) {
			op = Bukkit.getOfflinePlayer(name);
			//if (op == null) {
			//	hc.log().severe("Null OfflinePlayer name [" + name + "]");
			//}
		}
		return op;
	}
	
	public Inventory getInventory() {
		Player p = getPlayer();
		if (p != null) {
			return p.getInventory();
		} else {
			return null;
		}
	}

	public void sendMessage(String message) {
		if (getPlayer() != null) {
			getPlayer().sendMessage(message);
			return;
		}
	}
	
	public double getSalesTax(Double price) {
		CommonFunctions cf = hc.gCF();
		double salestax = 0;
		if (hc.getConf().getBoolean("tax.dynamic.enable")) {
			double moneyfloor = hc.getConf().getDouble("tax.dynamic.money-floor");
			double moneycap = hc.getConf().getDouble("tax.dynamic.money-cap");
			double cbal = getBalance();
			double maxtaxrate = hc.getConf().getDouble("tax.dynamic.max-tax-percent") / 100.0;
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
			double salestaxpercent = hc.getConf().getDouble("tax.sales");
			salestax = cf.twoDecimals((salestaxpercent / 100) * price);
		}
		return salestax;
	}
	
	
	public TransactionResponse processTransaction(PlayerTransaction playerTransaction) {
		return tp.processTransaction(playerTransaction);
	}
	
	
	public boolean hasSellPermission(Shop s) {
		if (!hc.getConf().getBoolean("enable-feature.per-shop-permissions")) {
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
		if (!(hc.getConf().getBoolean("enable-feature.per-shop-permissions"))) {
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

	@SuppressWarnings("deprecation")
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
			setInternalBalance(balance);
		}
	}
	public void setInternalBalance(double balance) {
		this.balance = balance;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("BALANCE", balance+"");
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		hc.getLog().writeAuditLog(name, "setbalance", balance, "HyperConomy");
	}
	@SuppressWarnings("deprecation")
	public void deposit(double amount) {
		if (hc.useExternalEconomy()) {
			hc.getEconomy().depositPlayer(name, amount);
			hc.getLog().writeAuditLog(name, "deposit", amount, hc.getEconomy().getName());
		} else {
			this.balance += amount;
			HashMap<String,String> conditions = new HashMap<String,String>();
			HashMap<String,String> values = new HashMap<String,String>();
			conditions.put("NAME", name);
			values.put("BALANCE", balance+"");
			hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
			hc.getLog().writeAuditLog(name, "deposit", amount, "HyperConomy");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void withdraw(double amount) {
		if (hc.useExternalEconomy()) {
			hc.getEconomy().withdrawPlayer(name, amount);
			hc.getLog().writeAuditLog(name, "withdrawal", amount, hc.getEconomy().getName());
		} else {
			this.balance -= amount;
			HashMap<String,String> conditions = new HashMap<String,String>();
			HashMap<String,String> values = new HashMap<String,String>();
			conditions.put("NAME", name);
			values.put("BALANCE", balance+"");
			hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
			hc.getLog().writeAuditLog(name, "withdrawal", amount, "HyperConomy");
		}
	}
	
	
	public int getBarXpPoints() {
		int lvl = getPlayer().getLevel();
		int exppoints = (int) Math.floor(((3.5 * lvl) + 6.7) * getPlayer().getExp() + .5);
		return exppoints;
	}

	public int getXpForNextLvl(int lvl) {
		int exppoints = (int) Math.floor(((3.5 * lvl) + 6.7) + .5);
		return exppoints;
	}

	public int getLvlXpPoints(int lvl) {
		int exppoints = (int) Math.floor((1.75 * Math.pow(lvl, 2)) + (5 * lvl) + .5);
		return exppoints;
	}

	public int getTotalXpPoints() {
		int lvl = getPlayer().getLevel();
		int lvlxp = getLvlXpPoints(lvl);
		int barxp = getBarXpPoints();
		int totalxp = lvlxp + barxp;
		return totalxp;
	}

	public int getLvlFromXP(int exp) {
		double lvlraw = (Math.sqrt((exp * 7.0) + 25.0) - 5.0) * (2.0 / 7.0);
		int lvl = (int) Math.floor(lvlraw);
		if ((double) lvl > lvlraw) {
			lvl = lvl - 1;
		}
		return lvl;
	}
	
	public boolean addXp(int amount) {
		if (getPlayer() == null || amount < 0) {return false;}
		int totalxp = getTotalXpPoints();
		int newxp = totalxp + amount;
		int newlvl = getLvlFromXP(newxp);
		newxp = newxp - getLvlXpPoints(newlvl);
		float xpbarxp = (float) newxp / (float) getXpForNextLvl(newlvl);
		getPlayer().setLevel(newlvl);
		getPlayer().setExp(xpbarxp);
		return true;
	}
	
	public boolean removeXp(Player p, int amount) {
		if (p == null || amount < 0) {return false;}
		int totalxp = getTotalXpPoints();
		int newxp = totalxp - amount;
		if (newxp < 0) {return false;}
		int newlvl = getLvlFromXP(newxp);
		newxp = newxp - getLvlXpPoints(newlvl);
		float xpbarxp = (float) newxp / (float) getXpForNextLvl(newlvl);
		p.setLevel(newlvl);
		p.setExp(xpbarxp);
		return true;
	}

	
}
