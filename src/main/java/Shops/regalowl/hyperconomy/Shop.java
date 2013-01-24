package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


/**
 * 
 * 
 * This class handles the shop regions and checks if a player is in a shop.
 * 
 */
public class Shop {
	

	private HashMap<Player, ShopStatus> playerStatus;
	private ArrayList<String> shopdata = new ArrayList<String>();
	private ArrayList<String> shopworld = new ArrayList<String>();
	private ArrayList<String> shopecon = new ArrayList<String>();
	private ArrayList<String> shopmessage1 = new ArrayList<String>();
	private ArrayList<String> shopmessage2 = new ArrayList<String>();
	private int nshops;
	
	private ArrayList<Integer> p1x = new ArrayList<Integer>();
	private ArrayList<Integer> p1y = new ArrayList<Integer>();
	private ArrayList<Integer> p1z = new ArrayList<Integer>();
	private ArrayList<Integer> p2x = new ArrayList<Integer>();
	private ArrayList<Integer> p2y = new ArrayList<Integer>();
	private ArrayList<Integer> p2z = new ArrayList<Integer>();
	
	private boolean useshopexitmessage;
	
	private long shopinterval;
	private int shoptaskid;

	private HyperConomy hc;
	private LanguageFile L;
	
	private boolean useShops;

	
	public int getshopdataSize() {
		return shopdata.size();
	}
	
	public String getshopData(int index) {
		return shopdata.get(index);
	}
	
	public void setMessage1(int i, String message) {
		shopmessage1.set(i, message);
	}
	
	public void setMessage2(int i, String message) {
		shopmessage2.set(i, message);
	}
	
	public void clearAll() {
		playerStatus.clear();
    	shopdata.clear();
    	shopecon.clear();
    	shopworld.clear();
    	shopmessage1.clear();
    	shopmessage2.clear();
    	p1x.clear();
    	p1y.clear();
    	p1z.clear();
    	p2x.clear();
    	p2y.clear();
    	p2z.clear();
	}

	
	
	/**
	 * 
	 * 
	 * Constructor for a new shop on server start.  It stores the shop's location, radius, and creates a new ShopPlayer.
	 * 
	 */
	Shop(HyperConomy hyperc) {
		hc = hyperc;
		playerStatus = new HashMap<Player, ShopStatus>();	
		shopinterval = hc.getYaml().getConfig().getLong("config.shopcheckinterval");
		useShops = hc.getYaml().getConfig().getBoolean("config.use-shops");
		useshopexitmessage = hc.getYaml().getConfig().getBoolean("config.use-shop-exit-message");	
		L = hc.getLanguageFile();
		buildShopData();
	}
	
	
	private void buildShopData() {
		clearAll();
		Iterator<String> it = hc.getYaml().getShops().getKeys(false).iterator();
		int counter = 0;
		while (it.hasNext()) {   			
			Object element2 = it.next();
			String ele = element2.toString(); 
			shopdata.add(ele);
			counter++;
		}
		nshops = shopdata.size();
		counter = 0;
		FileConfiguration sh = hc.getYaml().getShops();
		while (counter < nshops) {
			String nameshop = shopdata.get(counter);
			shopworld.add(sh.getString(nameshop + ".world"));
			shopecon.add(sh.getString(nameshop + ".economy"));
			p1x.add(sh.getInt(nameshop + ".p1.x"));
			p1y.add(sh.getInt(nameshop + ".p1.y"));
			p1z.add(sh.getInt(nameshop + ".p1.z"));
			p2x.add(sh.getInt(nameshop + ".p2.x"));
			p2y.add(sh.getInt(nameshop + ".p2.y"));
			p2z.add(sh.getInt(nameshop + ".p2.z"));
			shopmessage1.add(sh.getString(nameshop + ".shopmessage1"));
			shopmessage2.add(sh.getString(nameshop + ".shopmessage2"));
			counter++;
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function is run every few seconds to check which players are in the shop.
	 * 
	 */
	public void shopThread() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			int snum = inShop(p);
			//Gets whether or not the player was in the shop the last time the function was run.
			ShopStatus status = ShopStatus.NOT_IN_SHOP;;
			if (playerStatus.containsKey(p)) {
				status = playerStatus.get(p);
			}

			if (status == ShopStatus.NOT_IN_SHOP) {
				if (snum <= -1) {
					//Player is not in a shop or shops are disabled.
					continue;
				} else {
					//Player is in a shop
					p.sendMessage(L.get("SHOP_LINE_BREAK"));
					p.sendMessage(shopmessage1.get(snum).replace("%n", shopdata.get(snum).replace("_", " ")).replace("&","\u00A7"));
					p.sendMessage(shopmessage2.get(snum).replace("%n", shopdata.get(snum).replace("_", " ")).replace("&","\u00A7"));
					p.sendMessage(L.get("SHOP_LINE_BREAK"));
					playerStatus.put(p, ShopStatus.IN_SHOP);
					
					if (hc.useSQL()) {
						//Sets the player to the shop's economy.
						String shopecon = hc.getYaml().getShops().getString(shopdata.get(snum) + ".economy");
						if (shopecon == null) {
							shopecon = "default";
						}
						if (hc.getSQLFunctions().testEconomy(shopecon)) {
							hc.getSQLFunctions().setPlayerEconomy(p.getName().toLowerCase(), shopecon);
						}
					}
				}
				
			//Deals with players that were in the shop previously.
			} else if (status == ShopStatus.IN_SHOP) {
				
				//If the player is no longer in the shop it sends the exit message.
				if (snum == -1) {
					
					//Sets the player to not being in the shop in the inshop.txt file.
					//sp.setData(p.getName(), "false");
					playerStatus.put(p, ShopStatus.NOT_IN_SHOP);
					if (useshopexitmessage) {
						p.sendMessage(L.get("SHOP_EXIT_MESSAGE"));
					}		
				}
			}
		}
	}


	/**
	 * 
	 * 
	 * This function checks if the player is in the shop.
	 * 
	 */
	public int inShop(Player p) {
		if (!useShops) {
			return -2;
		}
		int inshop = -1;
		int counter = 0;
		while (counter < nshops) {
			
			if (p.getWorld().getName().equalsIgnoreCase(shopworld.get(counter))) {
				int locx = p.getLocation().getBlockX();
				int rangex = Math.abs(p1x.get(counter) - p2x.get(counter));
				if (Math.abs(locx - p1x.get(counter)) <= rangex && Math.abs(locx - p2x.get(counter)) <= rangex) {
		
	
					int locz = p.getLocation().getBlockZ();
					int rangez = Math.abs(p1z.get(counter) - p2z.get(counter));
					if (Math.abs(locz - p1z.get(counter)) <= rangez && Math.abs(locz - p2z.get(counter)) <= rangez) {
						
	
						int locy = p.getLocation().getBlockY();
						int rangey = Math.abs(p1y.get(counter) - p2y.get(counter));
						if (Math.abs(locy - p1y.get(counter)) <= rangey && Math.abs(locy - p2y.get(counter)) <= rangey) {
							inshop = counter;
						}
					}			
				}
			}
			counter++;
		}
		
		return inshop;
	}


	
	/**
	 * 
	 * 
	 * This function sets the shop's location, changing the static coordinates of the shop and saving them to the config file.
	 * 
	 */
	public void setPoint1(String name, Player p){
		int x = p.getLocation().getBlockX();
		int y = p.getLocation().getBlockY();
		int z = p.getLocation().getBlockZ();
		String w = p.getWorld().getName();
		FileConfiguration shop = hc.getYaml().getShops();
		shop.set(name + ".world", w);
		shop.set(name + ".p1.x", x);
		shop.set(name + ".p1.y", y);
		shop.set(name + ".p1.z", z);
		
		if (shopdata.indexOf(name) == -1) {
			shopdata.add(name);
		}
		
		nshops = shopdata.size();
		int shopnumber = shopdata.indexOf(name);

			if (p1x.size() < nshops) {
				shopworld.add(shopnumber, w);
				shopecon.add(shopnumber, "default");
				p1x.add(shopnumber, x);
				p1y.add(shopnumber, y);
				p1z.add(shopnumber, z);
				p2x.add(shopnumber, x);
				p2y.add(shopnumber, y);
				p2z.add(shopnumber, z);
				shop.set(name + ".economy", "default");
				shop.set(name + ".p2.x", x);
				shop.set(name + ".p2.y", y);
				shop.set(name + ".p2.z", z);
			} else {
				shopworld.set(shopnumber, w);
				p1x.set(shopnumber, x);
				p1y.set(shopnumber, y);
				p1z.set(shopnumber, z);
			}
			
			
			if (shopmessage1.size() < nshops) {
				shopmessage1.add(shopnumber, "&aWelcome to %n");
				shop.set(name + ".shopmessage1", "&aWelcome to %n");
			}
			if (shopmessage2.size() < nshops) {
				shopmessage2.add(shopnumber, "&9Type &b/hc &9for help.");
				shop.set(name + ".shopmessage2", "&9Type &b/hc &9for help.");
			}

	}
	
	

	/**
	 * 
	 * 
	 * This function sets the shop's location, changing the static coordinates of the shop and saving them to the config file.
	 * 
	 */
	public void setPoint2(String name, Player p){
		int x = p.getLocation().getBlockX();
		int y = p.getLocation().getBlockY();
		int z = p.getLocation().getBlockZ();
		String w = p.getWorld().getName();
		FileConfiguration shop = hc.getYaml().getShops();
		shop.set(name + ".world", w);
		shop.set(name + ".p2.x", x);
		shop.set(name + ".p2.y", y);
		shop.set(name + ".p2.z", z);
		if (shopdata.indexOf(name) == -1) {
			shopdata.add(name);
		}
		
		nshops = shopdata.size();
		int shopnumber = shopdata.indexOf(name);

		
		if (p2x.size() < nshops) {
			shopworld.add(shopnumber, w);
			shopecon.add(shopnumber, "default");
			p2x.add(shopnumber, x);
			p2y.add(shopnumber, y);
			p2z.add(shopnumber, z);
			p1x.add(shopnumber, x);
			p1y.add(shopnumber, y);
			p1z.add(shopnumber, z);
			shop.set(name + ".economy", "default");
			shop.set(name + ".p1.x", x);
			shop.set(name + ".p1.y", y);
			shop.set(name + ".p1.z", z);
		} else {
			shopworld.set(shopnumber, w);
			p2x.set(shopnumber, x);
			p2y.set(shopnumber, y);
			p2z.set(shopnumber, z);
		}
		
		if (shopmessage1.size() < nshops) {
			shopmessage1.add(shopnumber, "&aWelcome to %n");
			shop.set(name + ".shopmessage1", "&aWelcome to %n");
		}
		if (shopmessage2.size() < nshops) {
			shopmessage2.add(shopnumber, "&9Type &b/hc &9for help.");
			shop.set(name + ".shopmessage2", "&9Type &b/hc &9for help.");
		}

	}
	
	public void removeShop(String name) {
		
		hc.getYaml().getShops().set(name, null);
		int shopnumber = shopdata.indexOf(name);
		shopdata.remove(shopnumber);
			p1x.remove(shopnumber);
			p1y.remove(shopnumber);
			p1z.remove(shopnumber);
			p2x.remove(shopnumber);
			p2y.remove(shopnumber);
			p2z.remove(shopnumber);
			shopworld.remove(shopnumber);
			shopecon.remove(shopnumber);
			
			shopmessage1.remove(shopnumber);
			shopmessage2.remove(shopnumber);

		nshops = nshops - 1;
	}
	
	
	public void renameShop(String name, String newname) {
		FileConfiguration shopsfile = hc.getYaml().getShops();
		shopsfile.set(newname + ".world", shopsfile.get(name + ".world"));
		shopsfile.set(newname + ".p1.x", shopsfile.get(name + ".p1.x"));
		shopsfile.set(newname + ".p1.y", shopsfile.get(name + ".p1.y"));
		shopsfile.set(newname + ".p1.z", shopsfile.get(name + ".p1.z"));
		shopsfile.set(newname + ".p2.x", shopsfile.get(name + ".p2.x"));
		shopsfile.set(newname + ".p2.y", shopsfile.get(name + ".p2.y"));
		shopsfile.set(newname + ".p2.z", shopsfile.get(name + ".p2.z"));
		shopsfile.set(newname + ".shopmessage1", shopsfile.get(name + ".shopmessage1"));
		shopsfile.set(newname + ".shopmessage2", shopsfile.get(name + ".shopmessage2"));
		shopsfile.set(newname + ".unavailable", shopsfile.get(name + ".unavailable"));
		shopsfile.set(newname + ".economy", shopsfile.get(name + ".economy"));
		shopsfile.set(name, null);

		buildShopData();

	}
	
	public ArrayList<String> listShops() {
		return shopdata;
	}
	
	public boolean has(String nameshop, String item) {
		if (nameshop.equalsIgnoreCase("shops_disabled")) {
			return true;
		}
		item = hc.fixNameTest(item);
		if (item == null) {
			return false;
		}
		SerializeArrayList sal = new SerializeArrayList();
		FileConfiguration sh = hc.getYaml().getShops();
		ArrayList<String> unavailable = sal.stringToArray(sh.getString(nameshop + ".unavailable"));
		if (unavailable != null) {
			for (int i = 0; i < unavailable.size(); i++) {
				if (unavailable.get(i).equalsIgnoreCase(item)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public String getShop(Player player) {
		int shopnumber = inShop(player);
		if (shopnumber == -2) {
			return "shops_disabled";
		}
		String shopname = shopdata.get(shopnumber);
		return shopname;
	}
	
	
	
	
	
	
	
	
	
    //Threading related functions.
    public void startshopCheck() {
		shoptaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
		    public void run() {
		    	shopThread();
		    }
		}, shopinterval, shopinterval);
    }
    
    
    public void stopshopCheck() {
    	hc.getServer().getScheduler().cancelTask(shoptaskid);
    }
    
    
    public long getshopInterval() {
    	return shopinterval;
    }
	
    public void setshopInterval(long interval) {
    	shopinterval = interval;
    }
	

	
}
