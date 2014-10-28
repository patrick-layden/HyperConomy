package regalowl.hyperconomy.event.minecraft;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.HBlock;

public class HBlockBreakEvent extends Event {

	private HBlock block;
	private HyperPlayer hp;
	
	
	public HBlockBreakEvent(HBlock block, HyperPlayer hp) {
		this.block = block;
		this.hp = hp;
	}


	public HBlock getBlock() {
		return block;
	}
	
	public HyperPlayer getPlayer() {
		return hp;
	}
	
	
	
}
