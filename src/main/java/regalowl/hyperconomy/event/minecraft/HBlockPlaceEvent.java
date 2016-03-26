package regalowl.hyperconomy.event.minecraft;

import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.minecraft.HBlock;

public class HBlockPlaceEvent extends HyperEvent {

	private HBlock block;
	
	public HBlockPlaceEvent(HBlock block) {
		this.block = block;
	}
	
	public HBlock getBlock() {
		return block;
	}
	
}
