package regalowl.hyperconomy.bukkit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.display.ItemDisplay;
import regalowl.hyperconomy.event.minecraft.ChestShopClickEvent;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonExtendEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonRetractEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPlaceEvent;
import regalowl.hyperconomy.event.minecraft.HEntityExplodeEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerDropItemEvent;
import regalowl.hyperconomy.event.minecraft.HyperPlayerInteractEvent;
import regalowl.hyperconomy.event.minecraft.HyperPlayerItemHeldEvent;
import regalowl.hyperconomy.event.minecraft.HyperPlayerJoinEvent;
import regalowl.hyperconomy.event.minecraft.HyperPlayerQuitEvent;
import regalowl.hyperconomy.event.minecraft.HyperSignChangeEvent;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HMob;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.shop.ChestShop;

public class BukkitListener implements Listener {

	private BukkitConnector bc;
	
	public BukkitListener(BukkitConnector bc) {
		this.bc = bc;
	}
	

	public void unregisterAllListeners() {
		HandlerList.unregisterAll((Plugin)bc);
	}

	public void registerListeners() {
		bc.getServer().getPluginManager().registerEvents(this, bc);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent event) {
		if (event.isCancelled()) return;
		HyperPlayer hp = HC.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		Location l = event.getBlock().getLocation();
		HLocation sl = new HLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
		HSign sign = HC.mc.getSign(sl);
		HyperSignChangeEvent se = new HyperSignChangeEvent(sign, hp);
		HC.hc.getHyperEventHandler().fireEvent(se);
		if (se.isCancelled()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent pihevent) {
		if (pihevent.isCancelled()) return;
		HyperPlayer hp = BukkitCommon.getPlayer(pihevent.getPlayer());
		HyperPlayerItemHeldEvent hpih = new HyperPlayerItemHeldEvent(hp, pihevent.getPreviousSlot(), pihevent.getNewSlot());
		HC.hc.getHyperEventHandler().fireEvent(hpih);
		if (hpih.isCancelled()) pihevent.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		HyperPlayer hp = HC.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		HC.hc.getHyperEventHandler().fireEvent(new HyperPlayerJoinEvent(hp));
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		HyperPlayer hp = HC.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		HC.hc.getHyperEventHandler().fireEvent(new HyperPlayerQuitEvent(hp));
	}
	

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent bbevent) {
		if (bbevent.isCancelled()) {return;}
		HyperPlayer hp = BukkitCommon.getPlayer(bbevent.getPlayer());
		HBlockBreakEvent event = new HBlockBreakEvent(BukkitCommon.getBlock(bbevent.getBlock()), hp);
		HC.hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) bbevent.setCancelled(true);
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent eeevent) {
		if (eeevent.isCancelled()) {return;}
		ArrayList<HBlock> blocks = new ArrayList<HBlock>();
		for (Block b:eeevent.blockList()) {
			blocks.add(BukkitCommon.getBlock(b));
		}
		HEntityExplodeEvent event = new HEntityExplodeEvent(blocks);
		HC.hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) eeevent.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent bpeevent) {
		if (bpeevent.isCancelled()) {return;}
		ArrayList<HBlock> blocks = new ArrayList<HBlock>();
		for (Block b:bpeevent.getBlocks()) {
			blocks.add(BukkitCommon.getBlock(b));
		}
		HBlockPistonExtendEvent event = new HBlockPistonExtendEvent(blocks);
		HC.hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) bpeevent.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent bprevent) {
		if (bprevent.isCancelled()) {return;}
		HBlockPistonRetractEvent event = new HBlockPistonRetractEvent(BukkitCommon.getBlock(bprevent.getBlock()));
		HC.hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) bprevent.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent bpevent) {
		if (bpevent.isCancelled()) {return;}
		HBlockPlaceEvent event = new HBlockPlaceEvent(BukkitCommon.getBlock(bpevent.getBlock()));
		HC.hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) bpevent.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		if (!event.hasBlock()) return;
		HyperPlayer hp = BukkitCommon.getPlayer(event.getPlayer());
		HBlock block = BukkitCommon.getBlock(event.getClickedBlock());
		HyperPlayerInteractEvent hpie;
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			hpie = new HyperPlayerInteractEvent(hp, block, true);
		} else {
			hpie = new HyperPlayerInteractEvent(hp, block, false);
		}
		HC.hc.getHyperEventHandler().fireEvent(hpie);
		if (hpie.isCancelled()) event.setCancelled(true);
	}
	
	
	
	
	
	/**
	 * Fires events for chest shop inventory clicks.
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChestShopInventoryClickEvent(InventoryClickEvent icevent) {
		if (icevent.isCancelled()) {return;}
		HumanEntity he = icevent.getWhoClicked();
		if (!(he instanceof Player)) return;
		Player p = (Player)he;
		InventoryHolder ih = icevent.getView().getTopInventory().getHolder();
		if (!(ih instanceof Chest)) return;
		Chest c = (Chest)ih;
		if (!BukkitCommon.isChestShopChest(BukkitCommon.getLocation(c.getLocation()))) return;
		ChestShop cs = new ChestShop(BukkitCommon.getLocation(c.getBlock().getLocation()));
		HyperPlayer clicker = HC.hc.getHyperPlayerManager().getHyperPlayer(p.getName());
		int slot = icevent.getRawSlot();
		HItemStack clickedStack = BukkitCommon.getSerializableItemStack(icevent.getCurrentItem());
		ChestShopClickEvent event = new ChestShopClickEvent(clicker, cs, slot, clickedStack);
		if (icevent.isLeftClick()) {
			event.setLeftClick();
		} else if (icevent.isRightClick()) {
			event.setRightClick();
		} else if (icevent.isShiftClick()) {
			event.setShiftClick();
		}
		HC.hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) icevent.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemDisplayLoad(ChunkLoadEvent event) {
		Chunk chunk = event.getChunk();
		if (chunk == null) return;
		for (ItemDisplay display : HC.hc.getItemDisplay().getDisplays()) {
			if (display == null) continue;
			if (BukkitCommon.chunkContainsLocation(display.getLocation(), chunk)) {
				display.refresh();
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemDisplayUnload(ChunkUnloadEvent event) {
		Chunk chunk = event.getChunk();
		if (chunk == null) {return;}
		for (ItemDisplay display:HC.hc.getItemDisplay().getDisplays()) {
			if (display == null) {continue;}
			if (BukkitCommon.chunkContainsLocation(display.getLocation(), chunk)) {
				display.removeItem();
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickupDisplayCreatureSpawn(CreatureSpawnEvent event) {
		HLocation l = BukkitCommon.getLocation(event.getLocation());
		HMob mob = new HMob(l, event.getEntity().getCanPickupItems());
		for (ItemDisplay display : HC.hc.getItemDisplay().getDisplays()) {
			if (!display.isActive()) {continue;}
			if (display.blockEntityPickup(mob)) {
				event.getEntity().setCanPickupItems(false);
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickupItemDisplayEvent(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		if (!event.isCancelled()) {
			List<MetadataValue> meta = item.getMetadata("HyperConomy");
			for (MetadataValue cmeta : meta) {
				if (cmeta.asString().equalsIgnoreCase("item_display")) {
					event.setCancelled(true);
					return;
				}
			}
		}
		for (ItemDisplay display : HC.hc.getItemDisplay().getDisplays()) {
			if (display.getEntityId() == item.getEntityId() || item.equals(display.getItem())) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent devent) {
		if (devent.isCancelled()) return;
		HPlayerDropItemEvent event = new HPlayerDropItemEvent(BukkitCommon.getItem(devent.getItemDrop()), BukkitCommon.getPlayer(devent.getPlayer()));
		HC.hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) devent.setCancelled(true);
	}
	
	
	
}
