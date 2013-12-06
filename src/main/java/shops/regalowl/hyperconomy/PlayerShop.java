package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.QueryResult;

public class PlayerShop implements Shop, Comparable<Shop> {

	private String name;
	private String world;
	private HyperPlayer owner;
	private ArrayList<String> allowed = new ArrayList<String>();
	private String economy;
	private String message1;
	private String message2;
	private int p1x;
	private int p1y;
	private int p1z;
	private int p2x;
	private int p2y;
	private int p2z;
	
	private boolean useshopexitmessage;
	
	private HyperConomy hc;
	private LanguageFile L;
	private FileConfiguration shopFile;
	private EconomyManager em;
	private PlayerShop ps;
	private CommonFunctions cf;
	
	private ConcurrentHashMap<HyperObject,PlayerShopObject> shopContents = new ConcurrentHashMap<HyperObject,PlayerShopObject>();
	private ArrayList<String> inShop = new ArrayList<String>();
	
	PlayerShop(String shopName, String econ, HyperPlayer owner) {
		this.name = shopName;
		this.economy = econ;
		this.owner = owner;
		hc = HyperConomy.hc;
		cf = hc.getDataBukkit().getCommonFunctions();
		em = hc.getEconomyManager();
		ps = this;
		L = hc.getLanguageFile();
		shopFile = hc.gYH().getFileConfiguration("shops");
		shopFile.set(name + ".economy", economy);
		shopFile.set(name + ".owner", owner.getName());
		useshopexitmessage = hc.gYH().gFC("config").getBoolean("config.use-shop-exit-message");	
		allowed = cf.explode(shopFile.getString(name + ".allowed"), ",");
		
		
		
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				HyperEconomy he = em.getEconomy(economy);
				QueryResult result = hc.getSQLRead().aSyncSelect("SELECT * FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"'");
				while (result.next()) {
					double price = result.getDouble("PRICE");
					HyperObject ho = he.getHyperObject(result.getString("HYPEROBJECT"));
					double stock = result.getDouble("QUANTITY");
					HyperObjectStatus status = HyperObjectStatus.fromString(result.getString("STATUS"));
					if (ho instanceof ComponentItem) {
						ComponentShopItem pso = new ComponentShopItem(ps, (ComponentItem) ho, stock, price, status);
						shopContents.put(ho, pso);
					} else if (ho instanceof CompositeItem) {
						CompositeShopItem pso = new CompositeShopItem(ps, (CompositeItem)ho, stock, price, status);
						shopContents.put(ho, pso);
					} else if (ho instanceof Xp) {
						ShopXp pso = new ShopXp(ps, (BasicObject) ho, stock, price, status);
						shopContents.put(ho, pso);
					} else if (ho instanceof HyperEnchant) {
						HyperEnchant hye = (HyperEnchant)ho;
						ShopEnchant pso = new ShopEnchant(ps, hye, stock, price, status);
						shopContents.put(ho, pso);
					} else if (ho instanceof BasicObject) {
						BasicShopObject pso = new BasicShopObject(ps, (BasicObject) ho, stock, price, status);
						shopContents.put(ho, pso);
					}

				}
				result.close();
			}
		});		
	}

	
	public int compareTo(Shop s) {
		return name.compareTo(s.getName());
	}
	
	public void setPoint1(String world, int x, int y, int z) {
		this.world = world;
		p1x = x;
		p1y = y;
		p1z = z;
		shopFile.set(name + ".world", world);
		shopFile.set(name + ".p1.x", x);
		shopFile.set(name + ".p1.y", y);
		shopFile.set(name + ".p1.z", z);
	}
	public void setPoint2(String world, int x, int y, int z) {
		this.world = world;
		p2x = x;
		p2y = y;
		p2z = z;
		shopFile.set(name + ".world", world);
		shopFile.set(name + ".p2.x", x);
		shopFile.set(name + ".p2.y", y);
		shopFile.set(name + ".p2.z", z);
	}
	public void setPoint1(Location l) {
		setPoint1(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	public void setPoint2(Location l) {
		setPoint2(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	
	
	public void setMessage1(String message) {
		message1 = message;
		shopFile.set(name + ".shopmessage1", message1);
		
	}
	
	public void setMessage2(String message) {
		message2 = message;
		shopFile.set(name + ".shopmessage2", message2);
	}
	
	public void setDefaultMessages() {
		setMessage1("&aWelcome to %n");
		setMessage2("&9Type &b/hc &9for help.");
	}
	
	public void setWorld(String world) {
		this.world = world;
		shopFile.set(name + ".world", world);
	}
	
	public void setName(String name) {
		shopFile.set(this.name, null);
		this.name = name;
		shopFile.set(this.name, this.name);
		shopFile.set(name + ".world", world);
		shopFile.set(name + ".p1.x", p1x);
		shopFile.set(name + ".p1.y", p1y);
		shopFile.set(name + ".p1.z", p1z);
		shopFile.set(name + ".p2.x", p2x);
		shopFile.set(name + ".p2.y", p2y);
		shopFile.set(name + ".p2.z", p2z);
		shopFile.set(name + ".shopmessage1", message1);
		shopFile.set(name + ".shopmessage2", message2);
		shopFile.set(name + ".economy", economy);
	}
	
	public void setEconomy(String economy) {
		this.economy = economy;
		shopFile.set(name + ".economy", economy);
	}
	
	
	public boolean inShop(int x, int y, int z, String world) {
		if (world.equalsIgnoreCase(this.world)) {
			int rangex = Math.abs(p1x - p2x);
			if (Math.abs(x - p1x) <= rangex && Math.abs(x - p2x) <= rangex) {
				int rangez = Math.abs(p1z - p2z);
				if (Math.abs(z - p1z) <= rangez && Math.abs(z - p2z) <= rangez) {
					int rangey = Math.abs(p1y - p2y);
					if (Math.abs(y - p1y) <= rangey && Math.abs(y - p2y) <= rangey) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	public boolean inShop(Player player) {
		Location l = player.getLocation();
		return inShop(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());
	}
	
	public void sendEntryMessage(Player player) {
		if (message1 == null || message2 == null) {
			message1 = "&aWelcome to %n";
			message2 = "&9Type &b/hc &9for help.";
		}
		player.sendMessage(L.get("SHOP_LINE_BREAK"));
		player.sendMessage(message1.replace("%n", name).replace("_", " ").replace("&","\u00A7"));
		player.sendMessage(message2.replace("%n", name).replace("_", " ").replace("&","\u00A7"));
		player.sendMessage(L.get("SHOP_LINE_BREAK"));
	}
	
	public String getEconomy() {
		return economy;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return name.replace("_", " ");
	}
	
	
	public boolean has(String item) {
		FileConfiguration sh = hc.gYH().gFC("shops");
		String unavailableS = sh.getString(name + ".unavailable");
		if (unavailableS == null || unavailableS.equalsIgnoreCase("")) {
			return true;
		}
		if (unavailableS.equalsIgnoreCase("all")) {
			return false;
		}
		item = em.getEconomy(economy).fixNameTest(item);
		if (item == null) {
			return false;
		}
		CommonFunctions cf = hc.gCF();

		ArrayList<String> unavailable = cf.explode(unavailableS,",");
		for (String object : unavailable) {
			if (object.equalsIgnoreCase(item)) {
				return false;
			}
		}
		return true;
	}
	
	
	public void addAllObjects() {
		shopFile.set(name + ".unavailable", null);
	}
	public void removeAllObjects() {
		CommonFunctions cf = hc.gCF();
		HyperEconomy he = em.getEconomy(economy);
		ArrayList<String> unavailable = new ArrayList<String>();
		for (HyperObject ho:he.getHyperObjects()) {
			unavailable.add(ho.getName());
		}
		shopFile.set(name + ".unavailable", cf.implode(unavailable, ","));
	}
	public void addObjects(ArrayList<HyperObject> objects) {
		FileConfiguration sh = hc.gYH().gFC("shops");
		CommonFunctions cf = hc.gCF();
		ArrayList<String> unavailable = cf.explode(sh.getString(name + ".unavailable"),",");
		for (HyperObject ho:objects) {
			if (unavailable.contains(ho.getName())) {
				unavailable.remove(ho.getName());
			}
		}
		sh.set(name + ".unavailable", cf.implode(unavailable,","));
	}
	public void removeObjects(ArrayList<HyperObject> objects) {
		FileConfiguration sh = hc.gYH().gFC("shops");
		CommonFunctions cf = hc.gCF();
		ArrayList<String> unavailable = cf.explode(sh.getString(name + ".unavailable"),",");
		for (HyperObject ho:objects) {
			if (!unavailable.contains(ho.getName())) {
				unavailable.add(ho.getName());
			}
		}
		sh.set(name + ".unavailable", cf.implode(unavailable,","));
	}
	
	
	public int getP1x() {
		return p1x;
	}

	public int getP1y() {
		return p1y;
	}

	public int getP1z() {
		return p1z;
	}

	public int getP2x() {
		return p2x;
	}

	public int getP2y() {
		return p2y;
	}

	public int getP2z() {
		return p2z;
	}
	
	public Location getLocation1() {
		return new Location(Bukkit.getWorld(world), p1x, p1y, p1z);
	}
	
	public Location getLocation2() {
		return new Location(Bukkit.getWorld(world), p2x, p2y, p2z);
	}
	
	public HyperPlayer getOwner() {
		return owner;
	}
	
	public void setOwner(HyperPlayer owner) {
		this.owner = owner;
		shopFile.set(name + ".owner", owner.getName());
	}

	public ArrayList<HyperObject> getAvailableObjects() {
		ArrayList<HyperObject> available = new ArrayList<HyperObject>();
		for (PlayerShopObject pso:shopContents.values()) {
			if (pso.getStatus() != HyperObjectStatus.NONE) {
				if (has(pso)) {
					available.add(pso);
				}
			}
		}
		return available;
	}
	
	public boolean isEmpty() {
		for (PlayerShopObject pso:shopContents.values()) {
			if (pso.getStock() > 0) {
				return false;
			}
		}
		return true;
	}
	
	public void deleteShop() {
		hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"'");
		shopContents.clear();
		shopFile.set(name, null);
		em.removeShop(name);
	}
	
	public void removePlayerShopObject(HyperObject hyperObject) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			return;
		} else {
			shopContents.remove(pso);
			hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"' AND HYPEROBJECT = '"+hyperObject.getName()+"'");
		}
	}
	public PlayerShopObject getPlayerShopObject(HyperObject hyperObject) {
		if (shopContents.containsKey(hyperObject)) {
			return shopContents.get(hyperObject);
		}
		if (hyperObject instanceof ComponentItem) {
			ComponentShopItem pso = new ComponentShopItem(this, (ComponentItem)hyperObject, 0.0, 0.0, HyperObjectStatus.NONE);
			shopContents.put(hyperObject, pso);
			hc.getSQLWrite().addToQueue("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, PRICE, STATUS) VALUES ('"+name+"', '"+hyperObject.getName()+"', '0.0', '0.0', 'none')");
			return pso;
		} else if (hyperObject instanceof CompositeItem) {
			CompositeShopItem pso = new CompositeShopItem(this, (CompositeItem)hyperObject, 0.0, 0.0, HyperObjectStatus.NONE);
			shopContents.put(hyperObject, pso);
			hc.getSQLWrite().addToQueue("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, PRICE, STATUS) VALUES ('"+name+"', '"+hyperObject.getName()+"', '0.0', '0.0', 'none')");
			return pso;
		} else if (hyperObject instanceof Xp) {
			ShopXp pso = new ShopXp(this, (Xp)hyperObject, 0.0, 0.0, HyperObjectStatus.NONE);
			shopContents.put(hyperObject, pso);
			hc.getSQLWrite().addToQueue("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, PRICE, STATUS) VALUES ('"+name+"', '"+hyperObject.getName()+"', '0.0', '0.0', 'none')");
			return pso;
		} else if (hyperObject instanceof HyperEnchant) {
			ShopEnchant pso = new ShopEnchant(ps, (HyperEnchant)hyperObject, 0.0, 0.0, HyperObjectStatus.NONE);
			shopContents.put(hyperObject, pso);
			hc.getSQLWrite().addToQueue("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, PRICE, STATUS) VALUES ('"+name+"', '"+hyperObject.getName()+"', '0.0', '0.0', 'none')");
			return pso;
		} else if (hyperObject instanceof BasicObject) {
			BasicShopObject pso = new BasicShopObject(this, (BasicObject)hyperObject, 0.0, 0.0, HyperObjectStatus.NONE);
			shopContents.put(hyperObject, pso);
			hc.getSQLWrite().addToQueue("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, PRICE, STATUS) VALUES ('"+name+"', '"+hyperObject.getName()+"', '0.0', '0.0', 'none')");
			return pso;
		}
		return null;
	}
	
	public PlayerShopItem getPlayerShopItem(HyperObject hyperObject) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso != null && pso instanceof PlayerShopItem) {
			return (PlayerShopItem)pso;
		}
		return null;
	}
	
	public PlayerShopEnchant getPlayerShopEnchant(HyperObject hyperObject) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso != null && pso instanceof PlayerShopEnchant) {
			return (PlayerShopEnchant)pso;
		}
		return null;
	}
	
	public BasicShopObject getBasicShopObject(HyperObject hyperObject) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso != null && pso instanceof BasicShopObject) {
			return (BasicShopObject)pso;
		}
		return null;
	}
	
	public ShopXp getShopXp(HyperObject hyperObject) {
		PlayerShopObject pso = getPlayerShopObject(hyperObject);
		if (pso != null && pso instanceof ShopXp) {
			return (ShopXp)pso;
		}
		return null;
	}
	
	public boolean hasPlayerShopObject(HyperObject ho) {
		return shopContents.containsKey(ho);
	}
	


	public void setGlobal() {
		//do nothing
	}



	public HyperEconomy getHyperEconomy() {
		return em.getEconomy(economy);
	}



	public boolean has(HyperObject ho) {
		return has(ho.getName());
	}



	public void updatePlayerStatus() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (inShop.contains(p.getName())) {
				if (!inShop(p)) {
					inShop.remove(p.getName());
					if (useshopexitmessage) {
						p.sendMessage(L.get("SHOP_EXIT_MESSAGE"));
					}
				}
			} else {
				if (inShop(p)) {
					inShop.add(p.getName());
					sendEntryMessage(p);
					hc.getEconomyManager().getHyperPlayer(p.getName()).setEconomy(economy);
				}
			}
		}
	}



	public int getVolume() {
		return Math.abs(p1x - p2x) * Math.abs(p1y - p2y) * Math.abs(p1z - p2z);
	}
	
	public ArrayList<String> getAllowed() {
		return allowed;
	}
	public void addAllowed(HyperPlayer hp) {
		if (!allowed.contains(hp.getName())) {
			allowed.add(hp.getName());
		}
		saveAllowed();
	}
	public void removeAllowed(HyperPlayer hp) {
		if (allowed.contains(hp.getName())) {
			allowed.remove(hp.getName());
		}
		saveAllowed();
	}
	public boolean isAllowed(HyperPlayer hp) {
		if (allowed.contains(hp.getName())) {
			return true;
		}
		if (hp.getName().equalsIgnoreCase(owner.getName())) {
			return true;
		}
		if (hp.getPlayer() != null && hp.getPlayer().hasPermission("hyperconomy.admin")) {
			return true;
		}
		return false;
	}
	public void saveAllowed() {
		shopFile.set(name + ".allowed", cf.implode(allowed, ","));
	}
}
