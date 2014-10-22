package regalowl.hyperconomy.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.minecraft.FrameShopEvent;
import regalowl.hyperconomy.event.minecraft.HyperPlayerJoinEvent;
import regalowl.hyperconomy.event.minecraft.HyperPlayerQuitEvent;
import regalowl.hyperconomy.event.minecraft.HyperSignChangeEvent;
import regalowl.hyperconomy.shop.FrameShop;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.SimpleLocation;

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
		HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		Location l = event.getBlock().getLocation();
		SimpleLocation sl = new SimpleLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
		HyperConomy.hc.getHyperEventHandler().fireEvent(new HyperSignChangeEvent(event.getLines(), sl, hp));
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		HyperConomy.hc.getHyperEventHandler().fireEvent(new HyperPlayerJoinEvent(hp));
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer().getName());
		HyperConomy.hc.getHyperEventHandler().fireEvent(new HyperPlayerQuitEvent(hp));
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;
		Entity entity = event.getEntity();
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (entity instanceof ItemFrame) {
				SimpleLocation l = bc.getLocation(entity.getLocation());
				if (HyperConomy.hc.getFrameShopHandler().frameShopExists(l)) {
					event.setCancelled(true);
					FrameShop fs = HyperConomy.hc.getFrameShopHandler().getFrameShop(l);
					HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(p.getName());
					HyperConomy.hc.getHyperEventHandler().fireEvent(new FrameShopEvent(fs, hp, TransactionType.SELL));
				}
			}
		}
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		if (event.isCancelled()) return;
		Entity entity = event.getRightClicked();
		if (entity instanceof ItemFrame) {
			ItemFrame iFrame = (ItemFrame) entity;
			if (iFrame.getItem().getType().equals(Material.MAP)) {
				SimpleLocation l = bc.getLocation(entity.getLocation());
				if (HyperConomy.hc.getFrameShopHandler().frameShopExists(l)) {
					event.setCancelled(true);
					Player p = event.getPlayer();
					HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(p.getName());
					FrameShop fs = HyperConomy.hc.getFrameShopHandler().getFrameShop(l);
					HyperConomy.hc.getHyperEventHandler().fireEvent(new FrameShopEvent(fs, hp, TransactionType.BUY));
				}
			}
		}
	}

	
}
