package regalowl.hyperconomy.bukkit;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import regalowl.hyperconomy.util.SimpleLocation;

public class BukkitProtection {

	private ArrayList<SimpleLocation> protectedLocations = new ArrayList<SimpleLocation>();
	
	public BukkitProtection() {
		
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEvent(PlayerInteractEvent ievent) {
		if (isTransactionSign(ievent.getClickedBlock()) || isInfoSign(ievent.getClickedBlock())) {
			ievent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClickEvent(InventoryClickEvent icevent) {
		if (cs.isChestShop(icevent.getInventory().getHolder())) {
			icevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent bbevent) {
		if (cs.isChestShop(bbevent.getBlock(), true)) {
			bbevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent eeevent) {
		for (Block b : eeevent.blockList()) {
			if (cs.isChestShop(b, true)) {
				eeevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent bpeevent) {
		for (Block b : bpeevent.getBlocks()) {
			if (cs.isChestShop(b, true)) {
				bpeevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent bprevent) {
		if (cs.isChestShop(bprevent.getRetractLocation().getBlock(), true)) {
			bprevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent bpevent) {
		Block block = bpevent.getBlock();
		for (BlockFace bf : allfaces) {
			if (cs.isChestShop(block.getRelative(bf), false)) {
				bpevent.setCancelled(true);
			}
		}
	}
	
	
}
