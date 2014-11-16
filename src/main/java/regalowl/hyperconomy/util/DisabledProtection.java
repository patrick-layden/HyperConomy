package regalowl.hyperconomy.util;


import regalowl.simpledatalib.event.EventHandler;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.event.minecraft.ChestShopClickEvent;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonExtendEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonRetractEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPlaceEvent;
import regalowl.hyperconomy.event.minecraft.HEntityExplodeEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerInteractEvent;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.shop.ChestShop;

public class DisabledProtection {

	private transient HyperConomy hc;
	
	public DisabledProtection(HyperConomy hc) {
		this.hc = hc;
		hc.getHyperEventHandler().registerListener(this);
	}


	@EventHandler
	public void onPlayerInteractEvent(HPlayerInteractEvent ievent) {
		if (hc.getMC().isTransactionSign(ievent.getBlock().getLocation())) {
			ievent.cancel();
		}
	}

	@EventHandler
	public void onInventoryClickEvent(ChestShopClickEvent event) {
		event.cancel();
	}

	@EventHandler
	public void onBlockBreakEvent(HBlockBreakEvent bbevent) {
		if (new ChestShop(hc, bbevent.getBlock().getLocation()).isValid()) {
			bbevent.cancel();
		}
	}

	@EventHandler
	public void onEntityExplodeEvent(HEntityExplodeEvent eeevent) {
		for (HBlock b : eeevent.getBrokenBlocks()) {
			if (new ChestShop(hc, b.getLocation()).isValid()) {
				eeevent.cancel();
			}
		}
	}

	@EventHandler
	public void onBlockPistonExtendEvent(HBlockPistonExtendEvent bpeevent) {
		for (HBlock b : bpeevent.getBlocks()) {
			if (new ChestShop(hc, b.getLocation()).isValid()) {
				bpeevent.cancel();
			}
		}
	}

	@EventHandler
	public void onBlockPistonRetractEvent(HBlockPistonRetractEvent bprevent) {
		if (new ChestShop(hc, bprevent.getRetractedBlock().getLocation()).isValid()) {
			bprevent.cancel();
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(HBlockPlaceEvent bpevent) {
		HBlock block = bpevent.getBlock();
		for (HBlock b : block.getSurroundingBlocks()) {
			if (new ChestShop(hc, b.getLocation()).isValid()) {
				bpevent.cancel();
			}
		}
	}

}
