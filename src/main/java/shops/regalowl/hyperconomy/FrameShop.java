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
	private int tradeAmount;
	private FrameShopRenderer fsr;

	private int x;
	private int y;
	private int z;
	private String world;
	private Shop s;

	@SuppressWarnings("deprecation")
	FrameShop(Location l, HyperObject ho, Shop s, int amount) {
		hc = HyperConomy.hc;
		x = l.getBlockX();
		y = l.getBlockY();
		z = l.getBlockZ();
		world = l.getWorld().getName();
		this.ho = ho;
		this.tradeAmount = amount;
		this.s = s;
		MapView mapView = hc.getServer().createMap(l.getWorld());
		mapId = mapView.getId();
		String shop = "";
		if (s != null) {
			shop = s.getName();
		}
		hc.getSQLWrite().addToQueue("INSERT INTO hyperconomy_frame_shops (ID, HYPEROBJECT, ECONOMY, SHOP, TRADE_AMOUNT, X, Y, Z, WORLD) VALUES ('" + mapId + "','" + ho.getName() + "','" + ho.getEconomy() + "','" + shop + "','" + tradeAmount + "','" + x + "','" + y + "','" + z + "','" + world + "')");
		render();
	}

	FrameShop(short mapId, Location l, HyperObject ho, Shop s, int amount) {
		hc = HyperConomy.hc;
		this.mapId = mapId;
		x = l.getBlockX();
		y = l.getBlockY();
		z = l.getBlockZ();
		world = l.getWorld().getName();
		this.ho = ho;
		this.tradeAmount = amount;
		this.s = s;
		render();
	}

	public short getMapId() {
		return mapId;
	}

	public Shop getShop() {
		return s;
	}

	public String getKey() {
		return x + "|" + y + "|" + z + "|" + world;
	}

	public int getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(int amount) {
		tradeAmount = amount;
		hc.getSQLWrite().addToQueue("UPDATE hyperconomy_frame_shops SET TRADE_AMOUNT = '" + tradeAmount + "' WHERE ID = '" + mapId + "'");
	}

	public void render() {
		Location l = new Location(Bukkit.getWorld(world), x, y, z);
		if (!l.getChunk().isLoaded()) {
			return;
		}
		ItemFrame frame = getFrame(l);
		if (frame == null) {
			hc.getFrameShopHandler().removeFrameShop(getKey());
			hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_frame_shops WHERE ID = '" + mapId + "'");
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

	public void buy(HyperPlayer hp) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setAmount(tradeAmount);
		pt.setHyperObject(ho);
		TransactionResponse response = hp.processTransaction(pt);
		response.sendMessages();
		render();
	}

	public void sell(HyperPlayer hp) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
		pt.setAmount(tradeAmount);
		pt.setHyperObject(ho);
		TransactionResponse response = hp.processTransaction(pt);
		response.sendMessages();
		render();
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