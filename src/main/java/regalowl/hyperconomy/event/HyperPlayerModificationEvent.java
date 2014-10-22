package regalowl.hyperconomy.event;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.databukkit.event.Event;

public class HyperPlayerModificationEvent extends Event {
	private HyperPlayer hp;
	
	public HyperPlayerModificationEvent(HyperPlayer hp) {
		this.hp = hp;
	}
	
	public HyperPlayer getHyperPlayer() {
		return hp;
	}
}