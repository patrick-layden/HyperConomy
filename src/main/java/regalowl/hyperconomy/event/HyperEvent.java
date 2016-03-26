package regalowl.hyperconomy.event;

public class HyperEvent {
	private boolean cancelled;
	private boolean firedSuccessfully;
	
	public HyperEvent() {
		cancelled = false;
		firedSuccessfully = false;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void cancel() {
		this.cancelled = true;
	}
	
	public boolean firedSuccessfully() {
		return firedSuccessfully;
	}
	
	public void setFiredSuccessfully() {
		firedSuccessfully = true;
	}
}
