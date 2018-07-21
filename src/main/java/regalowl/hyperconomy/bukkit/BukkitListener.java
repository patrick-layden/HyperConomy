package regalowl.hyperconomy.bukkit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.display.ItemDisplay;
import regalowl.hyperconomy.event.minecraft.ChestShopClickEvent;
import regalowl.hyperconomy.event.minecraft.ChestShopCloseEvent;
import regalowl.hyperconomy.event.minecraft.ChestShopOpenEvent;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonExtendEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonRetractEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPlaceEvent;
import regalowl.hyperconomy.event.minecraft.HEntityExplodeEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerDropItemEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerInteractEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerItemHeldEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerJoinEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerQuitEvent;
import regalowl.hyperconomy.event.minecraft.HSignChangeEvent;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HMob;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.shop.ChestShop;

public class BukkitListener implements Listener {

	private BukkitConnector bc;
	private HyperConomy hc;
	private boolean minimal;
	
	public BukkitListener(BukkitConnector bc) {
		this.bc = bc;
		this.hc = bc.getHC();
		this.minimal = true;
	}
	

	public void unregisterAllListeners() {
		HandlerList.unregisterAll((Plugin)bc);
	}

	public void registerListeners() {
		bc.getServer().getPluginManager().registerEvents(this, bc);
	}
	
	
	public boolean isMinimal() {
		return minimal;
	}
	public void setMinimal(boolean state) {
		this.minimal = state;
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent event) {
		if (minimal) return;
		if (event.isCancelled()) return;
		HyperPlayer hp = bc.getBukkitCommon().getPlayer(event.getPlayer());
		HLocation sl = bc.getBukkitCommon().getLocation(event.getBlock().getLocation());
		HSign sign = hc.getMC().getSign(sl);
		ArrayList<String> lines = new ArrayList<String>();
		for (String li:event.getLines()) {
			lines.add(li);
		}
		sign.setLines(lines);
		HSignChangeEvent se = new HSignChangeEvent(sign, hp);
		hc.getHyperEventHandler().fireEvent(se);
		if (se.isCancelled()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent pihevent) {
		if (minimal) return;
		if (pihevent.isCancelled()) return;
		HyperPlayer hp = bc.getBukkitCommon().getPlayer(pihevent.getPlayer());
		HPlayerItemHeldEvent hpih = new HPlayerItemHeldEvent(hp, pihevent.getPreviousSlot(), pihevent.getNewSlot());
		hc.getHyperEventHandler().fireEvent(hpih);
		if (hpih.isCancelled()) pihevent.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (minimal) return;
		if (hc.getHyperLock().loadLock()) return;
		HyperPlayer hp = hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getUniqueId());
		if (hp == null) hp = hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		hc.getHyperEventHandler().fireEvent(new HPlayerJoinEvent(hp));
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (minimal) return;
		if (hc.getHyperLock().loadLock()) return;
		HyperPlayer hp = hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getUniqueId());
		if (hp == null) hp = hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		hc.getHyperEventHandler().fireEvent(new HPlayerQuitEvent(hp));
	}
	

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent bbevent) {
		if (bbevent.isCancelled()) {return;}
		HyperPlayer hp = bc.getBukkitCommon().getPlayer(bbevent.getPlayer());
		HBlockBreakEvent event = new HBlockBreakEvent(bc.getBukkitCommon().getBlock(bbevent.getBlock()), hp);
		hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) bbevent.setCancelled(true);
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent eeevent) {
		if (eeevent.isCancelled()) {return;}
		ArrayList<HBlock> blocks = new ArrayList<HBlock>();
		for (Block b:eeevent.blockList()) {
			blocks.add(bc.getBukkitCommon().getBlock(b));
		}
		HEntityExplodeEvent event = new HEntityExplodeEvent(blocks);
		hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) eeevent.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent bpeevent) {
		if (bpeevent.isCancelled()) {return;}
		ArrayList<HBlock> blocks = new ArrayList<HBlock>();
		for (Block b:bpeevent.getBlocks()) {
			blocks.add(bc.getBukkitCommon().getBlock(b));
		}
		HBlockPistonExtendEvent event = new HBlockPistonExtendEvent(blocks);
		hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) bpeevent.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent bprevent) {
		if (bprevent.isCancelled()) {return;}
		HBlockPistonRetractEvent event = new HBlockPistonRetractEvent(bc.getBukkitCommon().getBlock(bprevent.getBlock()));
		hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) bprevent.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent bpevent) {
		if (bpevent.isCancelled()) {return;}
		HBlockPlaceEvent event = new HBlockPlaceEvent(bc.getBukkitCommon().getBlock(bpevent.getBlock()));
		hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) bpevent.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		if (!event.hasBlock()) return;
		HyperPlayer hp = null;
		if (!minimal) hp = bc.getBukkitCommon().getPlayer(event.getPlayer());
		HBlock block = bc.getBukkitCommon().getBlock(event.getClickedBlock());
		HPlayerInteractEvent hpie;
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			hpie = new HPlayerInteractEvent(hp, block, true);
		} else {
			hpie = new HPlayerInteractEvent(hp, block, false);
		}
		hc.getHyperEventHandler().fireEvent(hpie);
		if (minimal) if (bc.isTransactionSign(block.getLocation())) event.setCancelled(true);
		if (hpie.isCancelled()) event.setCancelled(true);
	}
	
	
	
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChestShopInventoryClickEvent(InventoryClickEvent icevent) {
		if (minimal) return;
		if (icevent.isCancelled()) {return;}
		HumanEntity he = icevent.getWhoClicked();
		if (!(he instanceof Player)) return;
		Player p = (Player)he;
		InventoryHolder ih = icevent.getView().getTopInventory().getHolder();
		if (!(ih instanceof Player)) return;
		HyperPlayer clicker = hc.getHyperPlayerManager().getHyperPlayer(p.getName());
		ChestShop openShop = hc.getDataManager().getChestShopHandler().getOpenShop(clicker);
		if (openShop == null) return;
		
		HItemStack cursorStack = bc.getBukkitCommon().getSerializableItemStack(icevent.getCursor());
		HItemStack clickedStack = bc.getBukkitCommon().getSerializableItemStack(icevent.getCurrentItem());
		ChestShopClickEvent event = new ChestShopClickEvent(clicker, openShop, icevent.getRawSlot(), icevent.getAction().toString().toUpperCase(), clickedStack.getAmount(), clickedStack, cursorStack.getAmount(), cursorStack);
		if (icevent.isShiftClick()) {
			event.setShiftClick();
		} 
		if (icevent.isLeftClick()) {
			event.setLeftClick();
		} else if (icevent.isRightClick()) {
			event.setRightClick();
		}
		hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) icevent.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChestShopInventoryDragEvent(InventoryDragEvent idEvent) {
		if (minimal) return;
		HumanEntity he = idEvent.getWhoClicked();
		if (!(he instanceof Player)) return;
		Player p = (Player)he;
		InventoryHolder ih = idEvent.getView().getTopInventory().getHolder();
		if (!(ih instanceof Player)) return;
		HyperPlayer clicker = hc.getHyperPlayerManager().getHyperPlayer(p.getName());
		ChestShop openShop = hc.getDataManager().getChestShopHandler().getOpenShop(clicker);
		if (openShop == null) return;
		idEvent.setCancelled(true);
		//TODO fire inventory click event with proper settings
	}
	
	
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChestShopInventoryOpenEvent(InventoryOpenEvent ioevent) {
		if (minimal) return;
		if (ioevent.isCancelled()) {return;}
		HumanEntity he = ioevent.getPlayer();
		if (!(he instanceof Player)) return;
		Player p = (Player)he;
		InventoryHolder ih = ioevent.getView().getTopInventory().getHolder();
		if (!(ih instanceof Chest)) return;
		Chest c = (Chest)ih;
		HLocation chestLoc = bc.getBukkitCommon().getLocation(c.getLocation());
		ChestShop cs = hc.getDataManager().getChestShopHandler().getChestShop(chestLoc);
		if (cs == null) return;
		//add old format chest shops
		/*
		if (cs == null) {
			BukkitCommon common = bc.getBukkitCommon();
			//if (!bc.getBukkitCommon().isChestShopChest(chestLoc)) return;
			Block b = common.getBlock(chestLoc);
			if (b == null) return;
			if (!(b.getState() instanceof Chest)) return;
			Chest chest = (Chest) b.getState();
			HLocation sl = common.getLocation(chest.getLocation());
			sl.setY(sl.getY() + 1);
			Block sb = common.getBlock(sl);
			if (sb == null) return;
			if (!(sb.getState() instanceof Sign)) return;
			Sign s = (Sign) sb.getState();
			
			String line1 = ChatColor.stripColor(s.getLine(0)).trim();
			if (!hc.enabled() && line1.equalsIgnoreCase("ChestShop")) {
				ioevent.setCancelled(true);
				return;
			}
			String line2 = ChatColor.stripColor(s.getLine(1)).trim();
			
			if (!line2.equalsIgnoreCase("[Trade]") && !line2.equalsIgnoreCase("[Buy]") && !line2.equalsIgnoreCase("[Sell]")) return;
			String line34 = hc.getMC().removeColor(s.getLine(2)).trim() + hc.getMC().removeColor(s.getLine(3)).trim();
			if (!hc.getDataManager().accountExists(line34)) return;
			HyperAccount owner = hc.getDataManager().getAccount(line34);
			s.setLine(1, "");
			s.setLine(2, "");
			s.setLine(3, "");
			s.update();
			cs = hc.getChestShopHandler().addNewChestShop(chestLoc, owner);
		}
		*/
		
		ChestShopOpenEvent event = new ChestShopOpenEvent(hc.getHyperPlayerManager().getHyperPlayer(p.getName()), cs);
		hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) ioevent.setCancelled(true);	
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChestShopInventoryCloseEvent(InventoryCloseEvent icevent) {
		if (minimal) return;
		HumanEntity he = icevent.getPlayer();
		if (!(he instanceof Player)) return;
		Player p = (Player)he;
		InventoryHolder ih = icevent.getView().getTopInventory().getHolder();
		if (!(ih instanceof Player)) return;
		HyperPlayer closer = hc.getHyperPlayerManager().getHyperPlayer(p.getName());
		ChestShop cs = hc.getDataManager().getChestShopHandler().getOpenShop(closer);
		if (cs == null || cs.updateMenuLock()) return;
		ChestShopCloseEvent event = new ChestShopCloseEvent(closer, cs);
		hc.getHyperEventHandler().fireEvent(event);
	}
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemDisplayLoad(ChunkLoadEvent event) {
		if (minimal) return;
		Chunk chunk = event.getChunk();
		if (chunk == null) return;
		for (ItemDisplay display : hc.getItemDisplay().getDisplays()) {
			if (display == null) continue;
			if (bc.getBukkitCommon().chunkContainsLocation(display.getLocation(), chunk)) {
				display.refresh();
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemDisplayUnload(ChunkUnloadEvent event) {
		if (minimal) return;
		Chunk chunk = event.getChunk();
		if (chunk == null) {return;}
		for (ItemDisplay display:hc.getItemDisplay().getDisplays()) {
			if (display == null) {continue;}
			if (bc.getBukkitCommon().chunkContainsLocation(display.getLocation(), chunk)) {
				display.removeItem();
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickupDisplayCreatureSpawn(CreatureSpawnEvent event) {
		if (minimal) return;
		HLocation l = bc.getBukkitCommon().getLocation(event.getLocation());
		HMob mob = new HMob(l, event.getEntity().getCanPickupItems());
		for (ItemDisplay display : hc.getItemDisplay().getDisplays()) {
			if (!display.isActive()) {continue;}
			if (display.blockEntityPickup(mob)) {
				event.getEntity().setCanPickupItems(false);
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickupItemDisplayEvent(EntityPickupItemEvent event) {
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
		if (minimal) return;
		for (ItemDisplay display : hc.getItemDisplay().getDisplays()) {
			if (display.getEntityId() == item.getEntityId() || item.equals(display.getItem())) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent devent) {
		if (minimal) return;
		if (devent.isCancelled()) return;
		HPlayerDropItemEvent event = new HPlayerDropItemEvent(bc.getBukkitCommon().getItem(devent.getItemDrop()), bc.getBukkitCommon().getPlayer(devent.getPlayer()));
		hc.getHyperEventHandler().fireEvent(event);
		if (event.isCancelled()) devent.setCancelled(true);
	}
	
	
	
}
