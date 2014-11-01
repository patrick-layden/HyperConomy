package regalowl.hyperconomy.event;

import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.databukkit.event.Event;

public class HyperObjectModificationEvent extends Event {
	private TradeObject ho;
	
	public HyperObjectModificationEvent(TradeObject ho) {
		this.ho = ho;
	}
	
	public TradeObject getHyperObject() {
		return ho;
	}
}