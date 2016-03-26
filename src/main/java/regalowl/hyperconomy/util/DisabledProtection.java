package regalowl.hyperconomy.util;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.minecraft.ChestShopClickEvent;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonExtendEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonRetractEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPlaceEvent;
import regalowl.hyperconomy.event.minecraft.HEntityExplodeEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerInteractEvent;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.shop.ChestShop;

public class DisabledProtection implements HyperEventListener {

	private transient HyperConomy hc;
	
	public DisabledProtection(HyperConomy hc) {
		this.hc = hc;
		hc.getHyperEventHandler().registerListener(this);
	}


	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof HPlayerInteractEvent) {
			HPlayerInteractEvent hevent = (HPlayerInteractEvent)event;
			if (hc.getMC().isTransactionSign(hevent.getBlock().getLocation())) {
				hevent.cancel();
			}
		} else if (event instanceof ChestShopClickEvent) {
			ChestShopClickEvent hevent = (ChestShopClickEvent)event;
			hevent.cancel();
		} else if (event instanceof HBlockBreakEvent) {
			HBlockBreakEvent hevent = (HBlockBreakEvent) event;
			if (new ChestShop(hc, hevent.getBlock().getLocation()).isValid()) {
				hevent.cancel();
			}
		} else if (event instanceof HEntityExplodeEvent) {
			HEntityExplodeEvent hevent = (HEntityExplodeEvent)event;
			for (HBlock b : hevent.getBrokenBlocks()) {
				if (new ChestShop(hc, b.getLocation()).isValid()) {
					hevent.cancel();
				}
			}
		} else if (event instanceof HBlockPistonExtendEvent) {
			HBlockPistonExtendEvent hevent = (HBlockPistonExtendEvent)event;
			for (HBlock b : hevent.getBlocks()) {
				if (new ChestShop(hc, b.getLocation()).isValid()) {
					hevent.cancel();
				}
			}
		} else if (event instanceof HBlockPistonRetractEvent) {
			HBlockPistonRetractEvent hevent = (HBlockPistonRetractEvent)event;
			if (new ChestShop(hc, hevent.getRetractedBlock().getLocation()).isValid()) {
				hevent.cancel();
			}
		} else if (event instanceof HBlockPlaceEvent) {
			HBlockPlaceEvent hevent = (HBlockPlaceEvent)event;
			HBlock block = hevent.getBlock();
			for (HBlock b : block.getSurroundingBlocks()) {
				if (new ChestShop(hc, b.getLocation()).isValid()) {
					hevent.cancel();
				}
			}
		}
	}

}
