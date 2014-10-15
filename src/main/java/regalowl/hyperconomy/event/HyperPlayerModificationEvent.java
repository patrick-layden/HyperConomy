package regalowl.hyperconomy.event;

import regalowl.hyperconomy.account.HyperPlayer;

public class HyperPlayerModificationEvent extends HyperEvent {
	private HyperPlayer hp;
	
	public HyperPlayerModificationEvent(HyperPlayer hp) {
		this.hp = hp;
	}
	
	public HyperPlayer getHyperPlayer() {
		return hp;
	}
}