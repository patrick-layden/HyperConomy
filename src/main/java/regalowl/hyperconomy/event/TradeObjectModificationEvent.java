package regalowl.hyperconomy.event;

import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.simpledatalib.event.Event;

public class TradeObjectModificationEvent extends Event {
	private TradeObject to;
	
	public TradeObjectModificationEvent(TradeObject ho) {
		this.to = ho;
	}
	
	public TradeObject getTradeObject() {
		return to;
	}
}