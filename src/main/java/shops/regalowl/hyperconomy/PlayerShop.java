package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import regalowl.databukkit.QueryResult;

public class PlayerShop implements Shop, Comparable<Shop> {

	private String name;
	private String world;
	private HyperEconomy he;
	private HyperPlayer owner;
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
	
	private CopyOnWriteArrayList<PlayerShopObject> shopContents = new CopyOnWriteArrayList<PlayerShopObject>();
	private ArrayList<String> inShop = new ArrayList<String>();
	
	PlayerShop(String shopName, HyperPlayer owner) {
		this.name = shopName;
		this.owner = owner;
		hc = HyperConomy.hc;
		em = hc.getEconomyManager();
		he = em.getEconomy(owner.getEconomy());
		ps = this;
		L = hc.getLanguageFile();
		shopFile = hc.gYH().getFileConfiguration("shops");
		shopFile.set(name + ".economy", owner.getEconomy());
		shopFile.set(name + ".owner", owner.getName());
		useshopexitmessage = hc.gYH().gFC("config").getBoolean("config.use-shop-exit-message");	
		
		
		
		
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				QueryResult result = hc.getSQLRead().aSyncSelect("SELECT * FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"'");
				while (result.next()) {
					double price = result.getDouble("PRICE");
					HyperObject ho = he.getHyperObject(result.getString("HYPEROBJECT"));
					double stock = result.getDouble("QUANTITY");
					HyperObjectStatus status = HyperObjectStatus.fromString(result.getString("STATUS"));
					PlayerShopObject pso = new PlayerShopObject(ps, ho, stock, price, status);
					shopContents.add(pso);
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
	
	
	public void setPoint1(Player player) {
		Location l = player.getLocation();
		setPoint1(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public void setPoint2(Player player) {
		Location l = player.getLocation();
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
		shopFile.set(name + ".economy", owner.getEconomy());
	}
	
	public void setEconomy(String economy) {
		owner.setEconomy(economy);
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
		return owner.getEconomy();
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
		item = em.getEconomy(owner.getEconomy()).fixNameTest(item);
		if (item == null) {
			return false;
		}
		SerializeArrayList sal = new SerializeArrayList();

		ArrayList<String> unavailable = sal.stringToArray(unavailableS);
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
		shopFile.set(name + ".unavailable", "all");
	}
	
	public void addObjects(ArrayList<String> objects) {
		HyperEconomy he = em.getEconomy(owner.getEconomy());
		FileConfiguration sh = hc.gYH().gFC("shops");
		SerializeArrayList sal = new SerializeArrayList();
		ArrayList<String> unavailable = sal.stringToArray(sh.getString(name + ".unavailable"));
		if (unavailable.size() == 1 && unavailable.get(0).equalsIgnoreCase("all")) {
			unavailable = he.getNames();
		}
		for (String object:objects) {
			if (unavailable.contains(he.fixName(object))) {
				unavailable.remove(object);
			}
		}
		sh.set(name + ".unavailable", sal.stringArrayToString(unavailable));
	}
	
	public void removeObjects(ArrayList<String> objects) {
		HyperEconomy he = em.getEconomy(owner.getEconomy());
		FileConfiguration sh = hc.gYH().gFC("shops");
		SerializeArrayList sal = new SerializeArrayList();
		ArrayList<String> unavailable = sal.stringToArray(sh.getString(name + ".unavailable"));
		if (unavailable.size() == 1 && unavailable.get(0).equalsIgnoreCase("all")) {
			return;
		}
		for (String object:objects) {
			if (!unavailable.contains(he.fixName(object))) {
				unavailable.add(object);
			}
		}
		sh.set(name + ".unavailable", sal.stringArrayToString(unavailable));
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
	
	public HyperPlayer getOwner() {
		return owner;
	}
	
	public void setOwner(HyperPlayer owner) {
		this.owner = owner;
	}

	public ArrayList<HyperObject> getAvailableObjects() {
		ArrayList<HyperObject> available = new ArrayList<HyperObject>();
		for (PlayerShopObject pso:shopContents) {
			available.add(pso.getHyperObject());
		}
		return available;
	}
	
	public void deleteShop() {
		hc.getSQLWrite().executeSQL("DELETE FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"'");
	}
	
	public void removePlayerShopObject(HyperObject hyperObject) {
		HyperObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			return;
		} else {
			shopContents.remove(pso);
			hc.getSQLWrite().executeSQL("DELETE FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"' AND HYPEROBJECT = '"+hyperObject.getName()+"'");
		}
	}
	public HyperObject getPlayerShopObject(HyperObject hyperObject) {
		for (PlayerShopObject pso:shopContents) {
			if (hyperObject.equals(pso.getHyperObject())) {
				return pso;
			}
		}
		PlayerShopObject pso = new PlayerShopObject(this, hyperObject, 0.0, 0.0, HyperObjectStatus.TRADE);
		shopContents.add(pso);
		hc.getSQLWrite().executeSQL("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, PRICE, STATUS) VALUES ('"+name+"', '"+hyperObject.getName()+"', '0.0', '0.0', 'trade')");
		return pso;
	}
	public boolean hasPlayerShopObject(HyperObject ho) {
		for (PlayerShopObject pso:shopContents) {
			if (ho.equals(pso.getHyperObject())) {
				return true;
			}
		}
		return false;
	}
	


	public void setGlobal() {
		// TODO Auto-generated method stub
		
	}



	public HyperEconomy getHyperEconomy() {
		return em.getEconomy(owner.getEconomy());
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
					hc.getEconomyManager().getHyperPlayer(p.getName()).setEconomy(owner.getEconomy());
				}
			}
		}
	}



	public int getVolume() {
		return Math.abs(p1x - p2x) * Math.abs(p1y - p2y) * Math.abs(p1z - p2z);
	}
}
