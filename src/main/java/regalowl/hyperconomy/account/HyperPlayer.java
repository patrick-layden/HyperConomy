package regalowl.hyperconomy.account;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.sql.SQLWrite;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.event.HyperPlayerModificationEvent;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionProcessor;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.util.SimpleLocation;



public class HyperPlayer implements HyperAccount {


	private static final long serialVersionUID = -5665733095958448373L;
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
		HyperConomy hc = HyperConomy.hc;
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
		//checkExternalAccount();
	}
	
	
	public HyperPlayer(String name, String uuid, String economy, double balance, double x, double y, double z, String world, String hash, String salt) {
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
		HyperConomy.mc.runTask(new Runnable() {
			public void run() {
				checkUUID();
				//checkExternalAccount();
				//checkForNameChange();
			}
		});
	}

	
	@SuppressWarnings("deprecation")
	private void checkExternalAccount() {
		if (!HyperConomy.mc.useExternalEconomy()) {return;}
		if (name == null) {return;}
		if (!HyperConomy.mc.getEconomy().hasAccount(name)) {
			HyperConomy.mc.getEconomy().createPlayerAccount(name);
			setBalance(balance);
		}
		checkForNameChange();
	}
	
	public void checkUUID() {
		HyperConomy hc = HyperConomy.hc;
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
	
	private void checkForNameChange() {
		if (uuid == null || uuid == "") {return;}
		Player p = null;
		try {
			p = Bukkit.getPlayer(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return;
		}
		if (p == null) {return;}
		if (p.getName().equals(name)) {return;}
		if (HyperConomy.mc.useExternalEconomy()) {
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
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		return em.getEconomy(economy);
	}
	@SuppressWarnings("deprecation")
	public double getBalance() {
		checkExternalAccount();
		if (HyperConomy.mc.useExternalEconomy()) {
			return HyperConomy.mc.getEconomy().getBalance(name);
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
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
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
		HyperConomy hc = HyperConomy.hc;
		hc.getHyperPlayerManager().removeHyperPlayer(this);
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("NAME", name);
		hc.getSQLWrite().performDelete("hyperconomy_players", conditions);
	}
	
	public void setName(String name) {
		HyperConomy hc = HyperConomy.hc;
		hc.getHyperPlayerManager().removeHyperPlayer(this);
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", this.name);
		values.put("NAME", name);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.name = name;
		hc.getHyperPlayerManager().addHyperPlayer(this);
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setUUID(String uuid) {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", this.name);
		values.put("UUID", uuid);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.uuid = uuid;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	
	public void setEconomy(String economy) {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("ECONOMY", economy);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.economy = economy;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setX(double x) {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("X", x+"");
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.x = x;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setY(double y) {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("Y", y+"");
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.y = y;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setZ(double z) {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("Z", z+"");
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.z = z;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setWorld(String world) {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("WORLD", world);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.world = world;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setHash(String hash) {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("HASH", hash);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.hash = hash;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setSalt(String salt) {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("SALT", salt);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.salt = salt;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	
	
	
	@SuppressWarnings("deprecation")
	public Player getPlayer() {
		HyperConomy hc = HyperConomy.hc;
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
		HyperConomy hc = HyperConomy.hc;
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
	
	public SerializableInventory getInventory() {
		return HyperConomy.mc.getInventory(this);
	}

	public void sendMessage(String message) {
		HyperConomy.mc.sendMessage(this, HyperConomy.mc.applyColor(message));
	}
	
	public boolean hasPermission(String permission) {
		if (getPlayer() != null) {
			getPlayer().hasPermission(permission);
		}
		return false;
	}
	
	public double getSalesTax(Double price) {
		HyperConomy hc = HyperConomy.hc;
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
		TransactionProcessor tp = new TransactionProcessor(this);
		return tp.processTransaction(playerTransaction);
	}
	
	
	public boolean hasSellPermission(Shop s) {
		HyperConomy hc = HyperConomy.hc;
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
		HyperConomy hc = HyperConomy.hc;
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
		HyperConomy hc = HyperConomy.hc;
		checkExternalAccount();
		if (HyperConomy.mc.useExternalEconomy()) {
			if (HyperConomy.mc.getEconomy().hasAccount(name)) {
				HyperConomy.mc.getEconomy().withdrawPlayer(name, HyperConomy.mc.getEconomy().getBalance(name));
			} else {
				HyperConomy.mc.getEconomy().createPlayerAccount(name);
			}
			HyperConomy.mc.getEconomy().depositPlayer(name, balance);
			hc.getLog().writeAuditLog(name, "setbalance", balance, HyperConomy.mc.getEconomy().getName());
		} else {
			setInternalBalance(balance);
		}
	}
	public void setInternalBalance(double balance) {
		HyperConomy hc = HyperConomy.hc;
		this.balance = balance;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("BALANCE", balance+"");
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		hc.getLog().writeAuditLog(name, "setbalance", balance, "HyperConomy");
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	@SuppressWarnings("deprecation")
	public void deposit(double amount) {
		HyperConomy hc = HyperConomy.hc;
		checkExternalAccount();
		if (HyperConomy.mc.useExternalEconomy()) {
			HyperConomy.mc.getEconomy().depositPlayer(name, amount);
			hc.getLog().writeAuditLog(name, "deposit", amount, HyperConomy.mc.getEconomy().getName());
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
		HyperConomy hc = HyperConomy.hc;
		checkExternalAccount();
		if (HyperConomy.mc.useExternalEconomy()) {
			HyperConomy.mc.getEconomy().withdrawPlayer(name, amount);
			hc.getLog().writeAuditLog(name, "withdrawal", amount, HyperConomy.mc.getEconomy().getName());
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
	
	public SimpleLocation getTargetLocation() {
		return HyperConomy.mc.getTargetLocation(this);
	}
	
	public SimpleLocation getLocationBeforeTargetLocation() {
		return HyperConomy.mc.getLocationBeforeTargetLocation(this);
	}
	
	public SimpleLocation getLocation() {
		return HyperConomy.mc.getLocation(this);
	}

	public int getHeldItemSlot() {
		return HyperConomy.mc.getHeldItemSlot(this);
	}
	
	public SerializableItemStack getItemInHand() {
		return HyperConomy.mc.getItem(this, getHeldItemSlot());
	}
	
	public void teleport(SimpleLocation newLocation) {
		HyperConomy.mc.teleport(this, newLocation);
	}

	public boolean isInCreativeMode() {
		return HyperConomy.mc.isInCreativeMode(this);
	}
	
	public void kickPlayer(String message) {
		HyperConomy.mc.kickPlayer(this, message);
	}
	
	public boolean isSneaking() {
		return HyperConomy.mc.isSneaking(this);
	}
	
	public int getLevel() {
		return HyperConomy.mc.getLevel(this);
	}
	
}
