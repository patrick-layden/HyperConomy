package regalowl.hyperconomy.event.minecraft;

import regalowl.simpledatalib.event.Event;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.minecraft.HBlock;

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
