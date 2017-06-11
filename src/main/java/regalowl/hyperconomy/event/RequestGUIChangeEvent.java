package regalowl.hyperconomy.event;


public class RequestGUIChangeEvent extends HyperEvent {
	
	private GUIChangeType type;
	private String message;
	
	public RequestGUIChangeEvent(GUIChangeType type) {
		this.type = type;
	}
	public RequestGUIChangeEvent(GUIChangeType type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public GUIChangeType getType() {
		return type;
	}
	
	public String getMessage() {
		return message;
	}
}