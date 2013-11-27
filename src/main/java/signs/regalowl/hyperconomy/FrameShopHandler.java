package regalowl.hyperconomy;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Listener;

import regalowl.databukkit.QueryResult;

public class FrameShopHandler implements Listener {

	private HyperConomy hc;
	private EconomyManager em;
	private HashMap<Short, FrameShop> frameShops = new HashMap<Short, FrameShop>();
	
	public FrameShopHandler() {
		hc = HyperConomy.hc;
		em = hc.getEconomyManager();
		hc.getServer().getPluginManager().registerEvents(this, hc);
		load();
	}
	
	private void load() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				frameShops.clear();
				QueryResult result = hc.getSQLRead().aSyncSelect("SELECT * FROM hyperconomy_frame_shops");
				while (result.next()) {
					double x = result.getDouble("X");
					double y = result.getDouble("Y");
					double z = result.getDouble("Z");
					World w = Bukkit.getWorld(result.getString("WORLD"));
					Location l = new Location(w,x,y,z);
					Shop s = em.getShop(result.getString("SHOP"));
					String economy = em.getDefaultEconomy().getEconomy();
					if (s != null) {
						economy = s.getEconomy();
					}
					HyperObject ho = em.getEconomy(economy).getHyperObject(result.getString("HYPEROBJECT"));
					FrameShop fs = new FrameShop((short)(int)result.getInt("ID"), l, ho, s);
					frameShops.put(fs.getMapId(), fs);
					
				}
				result.close();
			}
		});
	}
	
	public FrameShop getFrameShop(short id) {
		if (frameShops.containsKey(id)) {
			return frameShops.get(id);
		}
		return null;
	}
	
	public void removeFrameShop(short id) {
		if (frameShops.containsKey(id)) {
			frameShops.remove(id);
		}
	}
	
	public void createFrameShop(Location l, HyperObject ho, Shop s) {
		FrameShop fs = new FrameShop(l, ho, s);
		frameShops.put(fs.getMapId(), fs);
	}
	
	public ItemFrame getFrame(Location loc) {
		for (Entity e : loc.getChunk().getEntities())
			if (e instanceof ItemFrame) {
				if (e.getLocation().getBlock().getLocation().distance(loc) == 0) {
					return (ItemFrame) e;
				}
			}
		return null;
	}
	
	
}
