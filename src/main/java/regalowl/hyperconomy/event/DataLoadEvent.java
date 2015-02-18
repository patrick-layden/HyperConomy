package regalowl.hyperconomy.event;

import regalowl.simpledatalib.event.Event;

public class DataLoadEvent extends Event {
	
	public final DataLoadType loadType;
	
	public DataLoadEvent(DataLoadType type) {
		this.loadType = type;
	}
	
	public enum DataLoadType {
		START,ECONOMY,COMPLETE,PLAYER,SHOP,BANK,DEFAULT_ACCOUNT;
	}
	
}