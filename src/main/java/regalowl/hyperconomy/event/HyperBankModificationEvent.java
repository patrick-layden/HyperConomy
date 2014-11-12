package regalowl.hyperconomy.event;

import regalowl.hyperconomy.account.HyperBank;
import regalowl.simpledatalib.event.Event;

public class HyperBankModificationEvent extends Event {
	private HyperBank hb;
	
	public HyperBankModificationEvent(HyperBank hb) {
		this.hb = hb;
	}
	
	public HyperBank getHyperBank() {
		return hb;
	}
}