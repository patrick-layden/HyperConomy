package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ServerShop implements Shop, Comparable<Shop>{
	
	private String name;
	private String economy;
	private HyperPlayer owner;
	private String world;
	private String message1;
	private String message2;
	private int p1x;
	private int p1y;
	private int p1z;
	private int p2x;
	private int p2y;
	private int p2z;
	
	private ArrayList<String> inShop = new ArrayList<String>();
	private boolean useshopexitmessage;
	
	private boolean loaded;
	
	private HyperConomy hc;
	private EconomyManager em;
	private LanguageFile L;
	private FileConfiguration shopFile;
	
	private boolean globalShop;
	private ArrayList<HyperObject> availableObjects = new ArrayList<HyperObject>();
	
	
	ServerShop(String name, String econ, HyperPlayer owner) {
		loaded = false;
		hc = HyperConomy.hc;
		this.name = name;
		this.economy = econ;
		this.owner = owner;
		if (owner == null) {
			this.owner = hc.getEconomyManager().getGlobalShopAccount();
		}
		em = hc.getEconomyManager();
		L = hc.getLanguageFile();
		globalShop = false;
		shopFile = hc.gYH().gFC("shops");
		shopFile.set(name + ".owner", this.owner.getName());
		shopFile.set(name + ".economy", econ);
		useshopexitmessage = hc.gYH().gFC("config").getBoolean("config.use-shop-exit-message");	
		loadAvailable();
	}
	
	public boolean isLoaded() {
		return loaded;
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
	
	public void setGlobal() {
		globalShop = true;
		setMessage1("");
		setMessage1("");
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
		if (globalShop) {
			return true;
		}
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
		if (globalShop) {
			return;
		}
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
	
	public HyperEconomy getHyperEconomy() {
		return em.getEconomy(economy);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return name.replace("_", " ");
	}
	
	

	public void loadAvailable() {
		HyperEconomy he = getHyperEconomy();
		availableObjects.clear();
		for (HyperObject ho:he.getHyperObjects()) {
			availableObjects.add(ho);
		}
		ArrayList<String> unavailable = hc.gCF().explode(shopFile.getString(name + ".unavailable"),",");
		for (String objectName : unavailable) {
			HyperObject ho = he.getHyperObject(objectName);
			availableObjects.remove(ho);
		}
		loaded = true;
	}
	public void saveAvailable() {
		HyperEconomy he = getHyperEconomy();
		ArrayList<String> unavailable = new ArrayList<String>();
		ArrayList<HyperObject> allObjects = he.getHyperObjects();
		for (HyperObject ho:allObjects) {
			if (!availableObjects.contains(ho)) {
				unavailable.add(ho.getName());
			}
		}
		if (unavailable.isEmpty()) {
			shopFile.set(name + ".unavailable", null);
		} else {
			shopFile.set(name + ".unavailable", hc.gCF().implode(unavailable,","));
		}
	}
	
	public boolean isStocked(HyperObject ho) {
		if (ho != null && ho.getStock() > 0) {
			return true;
		}
		return false;
	}
	public boolean isBanned(HyperObject ho) {
		if (availableObjects.contains(ho)) {
			return false;
		}
		return true;
	}
	public boolean isBanned(String name) {
		return isBanned(getHyperEconomy().getHyperObject(name));
	}
	public boolean isTradeable(HyperObject ho) {
		if (!isBanned(ho)) {
			return true;
		}
		return false;
	}
	public boolean isStocked(String item) {
		return isStocked(getHyperEconomy().getHyperObject(item));
	}
	public boolean isAvailable(HyperObject ho) {
		if (isTradeable(ho) && isStocked(ho)) {
			return true;
		}
		return false;
	}
	public ArrayList<HyperObject> getTradeableObjects() {
		return availableObjects;
	}
	
	public void unBanAllObjects() {
		availableObjects.clear();
		for (HyperObject ho:getHyperEconomy().getHyperObjects()) {
			availableObjects.add(ho);
		}
		saveAvailable();
	}
	public void banAllObjects() {
		availableObjects.clear();
		saveAvailable();
	}
	public void unBanObjects(ArrayList<HyperObject> objects) {
		for (HyperObject ho:objects) {
			if (!availableObjects.contains(ho)) {
				availableObjects.add(ho);
			}
		}
		saveAvailable();
	}
	public void banObjects(ArrayList<HyperObject> objects) {
		for (HyperObject ho:objects) {
			if (availableObjects.contains(ho)) {
				availableObjects.remove(ho);
			}
		}
		saveAvailable();
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
	
	public void deleteShop() {
		shopFile.set(name, null);
		em.removeShop(name);
	}

	public void setOwner(HyperPlayer owner) {
		this.owner = owner;
		shopFile.set(name + ".owner", owner.getName());
	}


}
