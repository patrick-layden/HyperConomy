package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Shop {
	
	private String name;
	private String world;
	private String economy;
	private String account;
	private String message1;
	private String message2;
	private int p1x;
	private int p1y;
	private int p1z;
	private int p2x;
	private int p2y;
	private int p2z;
	
	private HyperConomy hc;
	private LanguageFile L;
	private boolean useShops;
	private FileConfiguration shopFile;
	
	Shop(String name, String economy) {
		this.name = name;
		this.economy = economy;
		hc = HyperConomy.hc;
		L = hc.getLanguageFile();
		useShops = hc.getYaml().getConfig().getBoolean("config.use-shops");
		shopFile = hc.getYaml().getShops();
		if (shopFile.getString(name) == null) {
			shopFile.set(name + ".economy", economy);
		}
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
		this.world = l.getWorld().getName();
		p1x = l.getBlockX();
		p1y = l.getBlockY();
		p1z = l.getBlockZ();
		shopFile.set(name + ".world", world);
		shopFile.set(name + ".p1.x", p1x);
		shopFile.set(name + ".p1.y", p1y);
		shopFile.set(name + ".p1.z", p1z);
	}
	
	public void setPoint2(Player player) {
		Location l = player.getLocation();
		this.world = l.getWorld().getName();
		p2x = l.getBlockX();
		p2y = l.getBlockY();
		p2z = l.getBlockZ();
		shopFile.set(name + ".world", world);
		shopFile.set(name + ".p2.x", p2x);
		shopFile.set(name + ".p2.y", p2y);
		shopFile.set(name + ".p2.z", p2z);
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
		message1 = "&aWelcome to %n";
		message2 = "&9Type &b/hc &9for help.";
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
	
	
	public boolean has(String item) {
		if (!useShops) {
			return true;
		}
		item = hc.fixNameTest(item);
		if (item == null) {
			return false;
		}
		SerializeArrayList sal = new SerializeArrayList();
		FileConfiguration sh = hc.getYaml().getShops();
		ArrayList<String> unavailable = sal.stringToArray(sh.getString(name + ".unavailable"));
		if (unavailable != null) {
			for (String object : unavailable) {
				if (object.equalsIgnoreCase(item)) {
					return false;
				}
			}
		}
		return true;
	}
	
	
}
