package regalowl.hyperconomy.event;

import java.io.Serializable;

import regalowl.hyperconomy.tradeobject.TradeObject;

public class TradeObjectModificationEvent extends HyperEvent implements Serializable {

	private static final long serialVersionUID = 7592641160642604842L;
	private TradeObject to;
	private TradeObjectModificationType tomt;
	
	public TradeObjectModificationEvent(TradeObject ho, TradeObjectModificationType tomt) {
		this.to = ho;
		this.tomt = tomt;
	}
	
	public TradeObject getTradeObject() {
		return to;
	}
	
	public TradeObjectModificationType getTradeObjectModificationType() {
		return tomt;
	}
}