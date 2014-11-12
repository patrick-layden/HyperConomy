package regalowl.hyperconomy.event.minecraft;

import regalowl.simpledatalib.event.Event;
import regalowl.hyperconomy.minecraft.HBlock;

public class HBlockPistonRetractEvent extends Event {

	private HBlock block;
	
	public HBlockPistonRetractEvent(HBlock block) {
		this.block = block;
	}
	
	public HBlock getRetractedBlock() {
		return block;
	}
	
}
