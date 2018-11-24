package regalowl.hyperconomy.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.display.FrameShop;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
@SuppressWarnings("deprecation")
public class BukkitFrameShop implements FrameShop, HyperEventListener {

	private transient HyperConomy hc;
	
	private int mapId;
	private TradeObject to;
	private int tradeAmount;
	private BukkitFrameShopRenderer fsr;
	private HLocation l;

	private Shop s;
	
	private BukkitConnector bc;

	private BukkitFrameShop(HyperConomy hc, HLocation l, TradeObject ho, Shop s, int amount) {
		this.hc = hc;
		hc.getHyperEventHandler().registerListener(this);
		if (ho == null) {
			delete();
			return;
		}
		this.l = l;
		this.to = ho;
		this.tradeAmount = amount;
		this.s = s;
		this.bc = (BukkitConnector)hc.getMC();
		MapView mapView = Bukkit.getServer().createMap(bc.getBukkitCommon().getLocation(l).getWorld());
		mapId = mapView.getId();
		String shop = "";
		if (s != null) {
			shop = s.getName();
		}
		hc.getSQLWrite().addToQueue("INSERT INTO hyperconomy_frame_shops (ID, HYPEROBJECT, ECONOMY, SHOP, TRADE_AMOUNT, X, Y, Z, WORLD) VALUES "
				+ "('" + mapId + "','" + ho.getName() + "','" + ho.getEconomy() + "','" + shop + "','" + tradeAmount + "','" + l.getX() + "','" + 
				l.getY() + "','" + l.getZ() + "','" + l.getWorld() + "')");
		render();
	}

	private BukkitFrameShop(HyperConomy hc, short mapId, HLocation l, TradeObject ho, Shop s, int amount) {
		this.hc = hc;
		this.bc = (BukkitConnector)hc.getMC();
		hc.getHyperEventHandler().registerListener(this);
		if (ho == null) {
			delete();
			return;
		}
		if (l == null || l.getWorld() == null) {
			delete();
			return;
		}
		this.mapId = mapId;
		this.l = l;
		this.to = ho;
		this.tradeAmount = amount;
		this.s = s;
		render();
	}
	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof TradeObjectModificationEvent) {
			TradeObjectModificationEvent tevent = (TradeObjectModificationEvent)event;
			if (tevent.getTradeObject().equals(to)) {
				render();
			}
		}
		
	}

	public int getMapId() {
		return mapId;
	}

	public Shop getShop() {
		return s;
	}
	
	public TradeObject getTradeObject() {
		return to;
	}

	public int getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(int amount) {
		tradeAmount = amount;
		hc.getSQLWrite().addToQueue("UPDATE hyperconomy_frame_shops SET TRADE_AMOUNT = '" + tradeAmount + "' WHERE ID = '" + mapId + "'");
	}

	private void render() {
		Location loc = bc.getBukkitCommon().getLocation(l);
		if (!loc.getChunk().isLoaded()) {
			return;
		}
		ItemFrame frame = getFrame(loc);
		if (frame == null) {
			delete();
			return;
		}

		MapView mapView = Bukkit.getServer().getMap((short)mapId);
		for (MapRenderer mr : mapView.getRenderers()) {
			mapView.removeRenderer(mr);
		}
		fsr = new BukkitFrameShopRenderer(hc, to);
		mapView.addRenderer(fsr);
		ItemStack stack = new ItemStack(Material.FILLED_MAP, 1);



		//stack.setDurability(mapId);
		frame.setItem(stack);
	}

	public void buy(HyperPlayer hp) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setAmount(tradeAmount);
		pt.setHyperObject(to);
		TransactionResponse response = hp.processTransaction(pt);
		response.sendMessages();
		//render();
	}

	public void sell(HyperPlayer hp) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
		pt.setAmount(tradeAmount);
		pt.setHyperObject(to);
		TransactionResponse response = hp.processTransaction(pt);
		response.sendMessages();
		//render();
	}

	private ItemFrame getFrame(Location loc) {
		for (Entity e : loc.getChunk().getEntities())
			if (e instanceof ItemFrame) {
				if (e.getLocation().getBlock().getLocation().distance(loc) == 0) {
					return (ItemFrame) e;
				}
			}
		return null;
	}
	
	public HLocation getLocation() {
		return l;
	}
	
	private Block getAttachedBlock() {
		Location loc = bc.getBukkitCommon().getLocation(l);
		if (l == null) {return null;}
		ItemFrame frame = getFrame(loc);
		if (frame == null) {return null;}

		return loc.getBlock().getRelative(frame.getAttachedFace());
	}
	
	public void delete() {
		hc.getHyperEventHandler().unRegisterListener(this);
		hc.getFrameShopHandler().removeFrameShop(l);
		hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_frame_shops WHERE ID = '" + mapId + "'");
	}



}