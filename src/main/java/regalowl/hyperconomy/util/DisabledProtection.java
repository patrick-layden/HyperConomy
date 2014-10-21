package regalowl.hyperconomy.util;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.shop.ChestShopHandler;

public class DisabledProtection implements Listener {

	private ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
	private ArrayList<BlockFace> allfaces = new ArrayList<BlockFace>();
	private ChestShopHandler cs;

	public DisabledProtection() {
		faces.add(BlockFace.EAST);
		faces.add(BlockFace.WEST);
		faces.add(BlockFace.NORTH);
		faces.add(BlockFace.SOUTH);
		allfaces.add(BlockFace.EAST);
		allfaces.add(BlockFace.WEST);
		allfaces.add(BlockFace.NORTH);
		allfaces.add(BlockFace.SOUTH);
		allfaces.add(BlockFace.DOWN);
		allfaces.add(BlockFace.UP);
		HyperConomy.hc.getMC().getConnector().getServer().getPluginManager().registerEvents(this, HyperConomy.hc.getMC().getConnector());
		cs = HyperConomy.hc.getChestShop();
	}

	public boolean isTransactionSign(Block b) {
		try {
			if (b != null && b.getType().equals(Material.SIGN_POST) || b != null && b.getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) b.getState();
				String line3 = ChatColor.stripColor(s.getLine(2)).trim();
				if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			HyperConomy.hc.gDB().writeError(e);
			return false;
		}
	}

	public boolean isInfoSign(Block b) {
		try {
			if (b != null && b.getType().equals(Material.SIGN_POST) || b != null && b.getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) b.getState();
				String type = ChatColor.stripColor(s.getLine(2)).trim().replace(":", "").replace(" ", "");
				if (type.equalsIgnoreCase("buy")) {
					return true;
				} else if (type.equalsIgnoreCase("sell")) {
					return true;
				} else if (type.equalsIgnoreCase("stock")) {
					return true;
				} else if (type.equalsIgnoreCase("value")) {
					return true;
				} else if (type.equalsIgnoreCase("status")) {
					return true;
				} else if (type.equalsIgnoreCase("staticprice")) {
					return true;
				} else if (type.equalsIgnoreCase("startprice")) {
					return true;
				} else if (type.equalsIgnoreCase("median")) {
					return true;
				} else if (type.equalsIgnoreCase("history")) {
					return true;
				} else if (type.equalsIgnoreCase("tax")) {
					return true;
				} else if (type.equalsIgnoreCase("s")) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			HyperConomy.hc.gDB().writeError(e);
			return false;
		}
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
