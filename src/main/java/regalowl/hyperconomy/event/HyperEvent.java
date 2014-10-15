package regalowl.hyperconomy.event;

public class HyperEvent {
	private boolean cancelled;
	
	public HyperEvent() {
		cancelled = false;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void cancel() {
		this.cancelled = true;
	}
}
