package regalowl.hyperconomy.account;

import java.util.HashMap;
import java.util.UUID;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.sql.SQLWrite;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.event.HyperPlayerModificationEvent;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionProcessor;
import regalowl.hyperconomy.transaction.TransactionResponse;



public class HyperPlayer implements HyperAccount {


	private static final long serialVersionUID = -5665733095958448373L;
	private String name;
	private String uuid;
	private String economy;
	private double balance;
	private HLocation location;
	private String hash;
	private String salt;
	private boolean validUUID;
	
	
	public HyperPlayer(String player) {
		HC hc = HC.hc;
		name = player;
		name = HC.mc.getName(this);
		SQLWrite sw = hc.getSQLWrite();
		balance = hc.getConf().getDouble("economy-plugin.starting-player-account-balance");
		economy = "default";
		if (HC.mc.isOnline(this)) {
			if (hc.getHyperPlayerManager().uuidSupport()) {
				uuid = HC.mc.getUUID(this).toString();
			}
			this.location = HC.mc.getLocation(this);
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("NAME", name);
			values.put("UUID", uuid);
			values.put("ECONOMY", economy);
			values.put("BALANCE", balance+"");
			values.put("X", location.getX()+"");
			values.put("Y", location.getY()+"");
			values.put("Z", location.getZ()+"");
			values.put("WORLD", location.getWorld());
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
	
	
	public HyperPlayer(String name, String uuid, String economy, double balance, HLocation location, String hash, String salt) {
		this.name = name;
		this.uuid = uuid;
		this.economy = economy;
		this.balance = balance;
		this.location = location;
		this.hash = hash;
		this.salt = salt;
		HC.mc.runTask(new Runnable() {
			public void run() {
				checkUUID();
				//checkExternalAccount();
				//checkForNameChange();
			}
		});
	}

	
	@SuppressWarnings("deprecation")
	private void checkExternalAccount() {
		if (!HC.mc.useExternalEconomy()) {return;}
		if (name == null) {return;}
		if (!HC.mc.getEconomy().hasAccount(name)) {
			HC.mc.getEconomy().createPlayerAccount(name);
			setBalance(balance);
		}
		checkForNameChange();
	}
	
	public void checkUUID() {
		HC hc = HC.hc;
		this.validUUID = false;
		if (!hc.getHyperPlayerManager().uuidSupport()) {return;}
		if (name == null) {return;}
		if (uuid == null || uuid == "") {
			if (!HC.mc.isOnline(this)) {return;}
			setUUID(HC.mc.getUUID(this).toString());
			if (uuid == null || uuid == "") {return;}
		}
		this.validUUID = true;
	}
	
	private void checkForNameChange() {
		HC.mc.checkForNameChange(this);
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
		DataManager em = HC.hc.getDataManager();
		return em.getEconomy(economy);
	}
	@SuppressWarnings("deprecation")
	public double getBalance() {
		checkExternalAccount();
		if (HC.mc.useExternalEconomy()) {
			return HC.mc.getEconomy().getBalance(name);
		} else {
			return balance;
		}
	}
	public double getX() {
		return location.getX();
	}
	public double getY() {
		return location.getY();
	}
	public double getZ() {
		return location.getZ();
	}
	public String getWorld() {
		return location.getWorld();
	}
	public String getHash() {
		return hash;
	}
	public String getSalt() {
		return salt;
	}
	
	public boolean safeToDelete() {
		DataManager em = HC.hc.getDataManager();
		if (balance > 0) {return false;}
		if (HC.mc.isOnline(this)) {return false;}
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
		HC hc = HC.hc;
		hc.getHyperPlayerManager().removeHyperPlayer(this);
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("NAME", name);
		hc.getSQLWrite().performDelete("hyperconomy_players", conditions);
	}
	
	public void setName(String name) {
		HC hc = HC.hc;
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
		HC hc = HC.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", this.name);
		values.put("UUID", uuid);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.uuid = uuid;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	
	public void setEconomy(String economy) {
		HC hc = HC.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("ECONOMY", economy);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.economy = economy;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setLocation(HLocation loc) {
		HC hc = HC.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("X", loc.getX()+"");
		values.put("Y", loc.getY()+"");
		values.put("Z", loc.getZ()+"");
		values.put("WORLD", loc.getWorld()+"");
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		location = loc;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setHash(String hash) {
		HC hc = HC.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("HASH", hash);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.hash = hash;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	public void setSalt(String salt) {
		HC hc = HC.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("SALT", salt);
		hc.getSQLWrite().performUpdate("hyperconomy_players", values, conditions);
		this.salt = salt;
		hc.getHyperEventHandler().fireEvent(new HyperPlayerModificationEvent(this));
	}
	
	
	/*
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
	*/
	public HInventory getInventory() {
		return HC.mc.getInventory(this);
	}

	public void sendMessage(String message) {
		HC.mc.sendMessage(this, message);
	}
	
	public boolean hasPermission(String permission) {
		if (HC.mc.isOnline(this)) {
			return HC.mc.hasPermission(this, permission);
		}
		return false;
	}
	
	public double getSalesTax(Double price) {
		HC hc = HC.hc;
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
			salestax = CommonFunctions.twoDecimals((salestaxpercent / 100) * price);
		}
		return salestax;
	}
	
	
	public TransactionResponse processTransaction(PlayerTransaction playerTransaction) {
		TransactionProcessor tp = new TransactionProcessor(this);
		return tp.processTransaction(playerTransaction);
	}
	
	
	public boolean hasSellPermission(Shop s) {
		HC hc = HC.hc;
		if (!hc.getConf().getBoolean("enable-feature.per-shop-permissions")) {
			return true;
		}
		boolean hasPermission = false;
		if (HC.mc.isPermissionSet(this, "hyperconomy.shop")) {
			hasPermission = HC.mc.hasPermission(this, "hyperconomy.shop");
		}
		if (HC.mc.isPermissionSet(this, "hyperconomy.shop." + s.getName())) {
			hasPermission = HC.mc.hasPermission(this, "hyperconomy.shop." + s.getName());
		}
		if (HC.mc.isPermissionSet(this, "hyperconomy.shop." + s.getName() + ".sell")) {
			hasPermission = HC.mc.hasPermission(this, "hyperconomy.shop." + s.getName() + ".sell");
		}
		return hasPermission;
	}
	
	public boolean hasBuyPermission(Shop s) {
		HC hc = HC.hc;
		if (!(hc.getConf().getBoolean("enable-feature.per-shop-permissions"))) {
			return true;
		}
		boolean hasPermission = false;
		if (HC.mc.isPermissionSet(this, "hyperconomy.shop")) {
			hasPermission = HC.mc.hasPermission(this, "hyperconomy.shop");
		}
		if (HC.mc.isPermissionSet(this, "hyperconomy.shop." + s.getName())) {
			hasPermission = HC.mc.hasPermission(this, "hyperconomy.shop." + s.getName());
		}
		if (HC.mc.isPermissionSet(this, "hyperconomy.shop." + s.getName() + ".buy")) {
			hasPermission = HC.mc.hasPermission(this, "hyperconomy.shop." + s.getName() + ".buy");
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
		HC hc = HC.hc;
		checkExternalAccount();
		if (HC.mc.useExternalEconomy()) {
			if (HC.mc.getEconomy().hasAccount(name)) {
				HC.mc.getEconomy().withdrawPlayer(name, HC.mc.getEconomy().getBalance(name));
			} else {
				HC.mc.getEconomy().createPlayerAccount(name);
			}
			HC.mc.getEconomy().depositPlayer(name, balance);
			hc.getLog().writeAuditLog(name, "setbalance", balance, HC.mc.getEconomy().getName());
		} else {
			setInternalBalance(balance);
		}
	}
	public void setInternalBalance(double balance) {
		HC hc = HC.hc;
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
		HC hc = HC.hc;
		checkExternalAccount();
		if (HC.mc.useExternalEconomy()) {
			HC.mc.getEconomy().depositPlayer(name, amount);
			hc.getLog().writeAuditLog(name, "deposit", amount, HC.mc.getEconomy().getName());
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
		HC hc = HC.hc;
		checkExternalAccount();
		if (HC.mc.useExternalEconomy()) {
			HC.mc.getEconomy().withdrawPlayer(name, amount);
			hc.getLog().writeAuditLog(name, "withdrawal", amount, HC.mc.getEconomy().getName());
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
		int lvl = HC.mc.getLevel(this);
		int exppoints = (int) Math.floor(((3.5 * lvl) + 6.7) * HC.mc.getExp(this) + .5);
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
		int lvl = HC.mc.getLevel(this);
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
		if (!HC.mc.isOnline(this) || amount < 0) {return false;}
		int totalxp = getTotalXpPoints();
		int newxp = totalxp + amount;
		int newlvl = getLvlFromXP(newxp);
		newxp = newxp - getLvlXpPoints(newlvl);
		float xpbarxp = (float) newxp / (float) getXpForNextLvl(newlvl);
		HC.mc.setLevel(this, newlvl);
		HC.mc.setExp(this, xpbarxp);
		return true;
	}
	
	public boolean removeXp(int amount) {
		if (!HC.mc.isOnline(this) || amount < 0) {return false;}
		int totalxp = getTotalXpPoints();
		int newxp = totalxp - amount;
		if (newxp < 0) {return false;}
		int newlvl = getLvlFromXP(newxp);
		newxp = newxp - getLvlXpPoints(newlvl);
		float xpbarxp = (float) newxp / (float) getXpForNextLvl(newlvl);
		HC.mc.setLevel(this, newlvl);
		HC.mc.setExp(this, xpbarxp);
		return true;
	}
	
	public HLocation getTargetLocation() {
		return HC.mc.getTargetLocation(this);
	}
	
	public HLocation getLocationBeforeTargetLocation() {
		return HC.mc.getLocationBeforeTargetLocation(this);
	}
	
	public HLocation getLocation() {
		return HC.mc.getLocation(this);
	}

	public int getHeldItemSlot() {
		return HC.mc.getHeldItemSlot(this);
	}
	
	public HItemStack getItemInHand() {
		return HC.mc.getItem(this, getHeldItemSlot());
	}
	
	public void teleport(HLocation newLocation) {
		HC.mc.teleport(this, newLocation);
	}

	public boolean isInCreativeMode() {
		return HC.mc.isInCreativeMode(this);
	}
	
	public void kickPlayer(String message) {
		HC.mc.kickPlayer(this, message);
	}
	
	public boolean isSneaking() {
		return HC.mc.isSneaking(this);
	}
	
	public int getLevel() {
		return HC.mc.getLevel(this);
	}
	
	public void setLevel(int level) {
		HC.mc.setLevel(this, level);
	}
	
	public void setExp(float exp) {
		HC.mc.setExp(this, exp);
	}
	
}
