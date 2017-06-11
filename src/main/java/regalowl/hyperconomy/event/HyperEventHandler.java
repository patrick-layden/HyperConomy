package regalowl.hyperconomy.event;

import java.util.concurrent.CopyOnWriteArrayList;

import regalowl.hyperconomy.HyperConomy;

public class HyperEventHandler {
	
	private HyperConomy hc;
	private CopyOnWriteArrayList<HyperEventListener> listeners = new CopyOnWriteArrayList<HyperEventListener>();
	
	public HyperEventHandler(HyperConomy hc) {
		this.hc = hc;
	}
	
    public void registerListener(HyperEventListener listener) {
    	if (!listeners.contains(listener)) {
    		listeners.add(listener);
    	}
    }
    
    public void unRegisterListener(HyperEventListener listener) {
    	if (listeners.contains(listener)) {
    		listeners.remove(listener);
    	}
    }
    
    public void clearListeners() {
    	listeners.clear();
    }
	
	public void fireEventFromAsyncThread(HyperEvent event) {
		hc.getMC().runTask(new EventFire(event));
	}
	
	
    private class EventFire implements Runnable {
    	private HyperEvent event;
    	public EventFire(HyperEvent event) {
    		this.event = event;
    	}
		public void run() {
			fireEvent(event);
		}
    }
	
	public HyperEvent fireEvent(HyperEvent event) {
		for (HyperEventListener listener:listeners) {
			listener.handleHyperEvent(event);
			event.setFiredSuccessfully();
		}
		return event;
	}
	
}