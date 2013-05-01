package regalowl.hyperconomy;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
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
import org.bukkit.inventory.InventoryHolder;

public class DisabledProtection implements Listener {

	private ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
	private ArrayList<BlockFace> allfaces = new ArrayList<BlockFace>();

	DisabledProtection() {
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
		HyperConomy.hc.getServer().getPluginManager().registerEvents(this, HyperConomy.hc);
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
			new HyperError(e);
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
			new HyperError(e);
			return false;
		}
	}

	public boolean isChestShop(Block b) {
		try {
			if (b == null) {
				return false;
			}
			if (b.getState() instanceof Chest) {
				Chest chest = (Chest) b.getState();
				String world = chest.getBlock().getWorld().getName();
				BlockState signblock = Bukkit.getWorld(world).getBlockAt(chest.getX(), chest.getY() + 1, chest.getZ()).getState();
				if (signblock instanceof Sign) {
					Sign s = (Sign) signblock;
					String line2 = ChatColor.stripColor(s.getLine(1)).trim();
					if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
						return true;
					}
				}
			} else if (b.getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) b.getState();
				String line2 = s.getLine(1).trim();
				if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
					BlockState chestblock = Bukkit.getWorld(s.getBlock().getWorld().getName()).getBlockAt(s.getX(), s.getY() - 1, s.getZ()).getState();
					if (chestblock instanceof Chest) {
						s.update();
						return true;
					}
				}
			} else {
				for (BlockFace cface : faces) {
					Block relative = b.getRelative(cface);
					if (relative.getType().equals(Material.WALL_SIGN)) {
						Sign s = (Sign) relative.getState();
						String line2 = s.getLine(1).trim();
						if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
							org.bukkit.material.Sign sign = (org.bukkit.material.Sign) relative.getState().getData();
							BlockFace attachedface = sign.getFacing();
							if (attachedface == cface) {
								return true;
							}
						}
					}
				}
			}
			return false;
		} catch (Exception e) {
			new HyperError(e);
			return false;
		}
	}

	public boolean isChestShop(InventoryHolder ih) {
		try {
			if (ih instanceof Chest) {
				Chest chest = (Chest) ih;
				int x = chest.getX();
				int y = chest.getY() + 1;
				int z = chest.getZ();
				String world = chest.getBlock().getWorld().getName();
				BlockState signblock = Bukkit.getWorld(world).getBlockAt(x, y, z).getState();
				if (signblock instanceof Sign) {
					Sign s = (Sign) signblock;
					String line2 = ChatColor.stripColor(s.getLine(1)).trim();
					if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			new HyperError(e);
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
		if (isChestShop(icevent.getInventory().getHolder())) {
			icevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEventTekkit(BlockPlaceEvent bpevent) {
		Block block = bpevent.getBlock();
		for (BlockFace bf : allfaces) {
			if (isChestShop(block.getRelative(bf))) {
				bpevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent bbevent) {
		if (isChestShop(bbevent.getBlock())) {
			bbevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent eeevent) {
		for (Block b : eeevent.blockList()) {
			if (isChestShop(b)) {
				eeevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent bpeevent) {
		for (Block b : bpeevent.getBlocks()) {
			if (isChestShop(b)) {
				bpeevent.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent bprevent) {
		if (isChestShop(bprevent.getRetractLocation().getBlock())) {
			bprevent.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent bpevent) {
		if (isChestShop(bpevent.getBlock())) {
			bpevent.setCancelled(true);
		}
	}

}
