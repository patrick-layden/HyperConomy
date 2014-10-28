package regalowl.hyperconomy.event.minecraft;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.util.HBlock;

public class HBlockPlaceEvent extends Event {

	private HBlock block;
	
	public HBlockPlaceEvent(HBlock block) {
		this.block = block;
	}
	
	public HBlock getBlock() {
		return block;
	}
	
}
