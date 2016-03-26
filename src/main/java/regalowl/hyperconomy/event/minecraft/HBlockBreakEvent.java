package regalowl.hyperconomy.event.minecraft;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.minecraft.HBlock;

public class HBlockBreakEvent extends HyperEvent {

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
