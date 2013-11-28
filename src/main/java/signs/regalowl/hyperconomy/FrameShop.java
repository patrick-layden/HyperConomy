package regalowl.hyperconomy;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class FrameShop {

	private HyperConomy hc;
	private short mapId;
	private HyperObject ho;
	private FrameShopRenderer fsr;
	
	private int x;
	private int y;
	private int z;
	private String world;
	private Shop s;

	
	@SuppressWarnings("deprecation")
	FrameShop(Location l, HyperObject ho, Shop s) {
		hc = HyperConomy.hc;
		x = l.getBlockX();
		y = l.getBlockY();
		z = l.getBlockZ();
		world = l.getWorld().getName();
		this.ho = ho;
		this.s = s;
        MapView mapView = hc.getServer().createMap(l.getWorld());
        mapId = mapView.getId();
        String shop = "";
        if (s != null) {
        	shop = s.getName();
        }
        hc.getSQLWrite().addToQueue("INSERT INTO hyperconomy_frame_shops (ID, HYPEROBJECT, ECONOMY, SHOP, X, Y, Z, WORLD) VALUES ('" + mapId + "','" + ho.getName() + "','" + ho.getEconomy() + "','" + shop + "','" + x + "','" + y + "','" + z + "','" + world + "')");
        render();
	}
	
	
	FrameShop(short mapId, Location l, HyperObject ho, Shop s) {
		hc = HyperConomy.hc;
		this.mapId = mapId;
		x = l.getBlockX();
		y = l.getBlockY();
		z = l.getBlockZ();
		world = l.getWorld().getName();
		this.s = s;
		render();
	}
	
	public short getMapId() {
		return mapId;
	}
	public Shop getShop() {
		return s;
	}
	
	public void render() {
		Location l = new Location(Bukkit.getWorld(world), x, y, z);
		if (!l.getChunk().isLoaded()) {
			return;
		}
        ItemFrame frame = getFrame(l);
        if (frame == null) {
        	hc.getFrameShopHandler().removeFrameShop(mapId);
        	hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_frame_shops WHERE ID = '"+mapId+"'");
        	return;
        }
        @SuppressWarnings("deprecation")
		MapView mapView = hc.getServer().getMap(mapId);
        for (MapRenderer mr : mapView.getRenderers()) {
        	mapView.removeRenderer(mr);
        }
        fsr = new FrameShopRenderer(ho);
        mapView.addRenderer(fsr);
		ItemStack stack = new ItemStack(Material.MAP, 1);
		stack.setDurability(mapId);
		frame.setItem(stack);
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
