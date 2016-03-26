package regalowl.hyperconomy.event.minecraft;

import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.minecraft.HBlock;

public class HBlockPistonRetractEvent extends HyperEvent {

	private HBlock block;
	
	public HBlockPistonRetractEvent(HBlock block) {
		this.block = block;
	}
	
	public HBlock getRetractedBlock() {
		return block;
	}
	
}
