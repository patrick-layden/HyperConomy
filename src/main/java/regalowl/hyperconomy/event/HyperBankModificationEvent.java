package regalowl.hyperconomy.event;

import regalowl.hyperconomy.account.HyperBank;

public class HyperBankModificationEvent extends HyperEvent {
	private HyperBank hb;
	
	public HyperBankModificationEvent(HyperBank hb) {
		this.hb = hb;
	}
	
	public HyperBank getHyperBank() {
		return hb;
	}
}