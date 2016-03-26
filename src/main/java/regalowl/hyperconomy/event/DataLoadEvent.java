package regalowl.hyperconomy.event;


public class DataLoadEvent extends HyperEvent {
	
	public final DataLoadType loadType;
	
	public DataLoadEvent(DataLoadType type) {
		this.loadType = type;
	}
	
	public enum DataLoadType {
		START,ECONOMY,COMPLETE,PLAYER,SHOP,BANK,DEFAULT_ACCOUNT;
	}
	
}