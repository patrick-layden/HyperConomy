package regalowl.hyperconomy.event;


import java.util.concurrent.CopyOnWriteArrayList;

import regalowl.hyperconomy.HyperConomy;

public class HyperEventHandler {
	
	private HyperConomy hc;
	private CopyOnWriteArrayList<HyperListener> listeners = new CopyOnWriteArrayList<HyperListener>();
	
    public HyperEventHandler() {
    	hc = HyperConomy.hc;
    }
    
    public void clearListeners() {
    	listeners.clear();
    }
    
    public synchronized void registerListener(HyperListener listener) {
    	if (!listeners.contains(listener)) {
    		listeners.add(listener);
    	}
    }
    public synchronized void unRegisterListener(HyperListener listener) {
    	if (listeners.contains(listener)) {
    		listeners.remove(listener);
    	}
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
			for (HyperListener listener : listeners) {
				listener.onHyperEvent(event);
			}
		}
    }
	
    
	public HyperEvent fireEvent(HyperEvent event) {
		for (HyperListener listener : listeners) {
			listener.onHyperEvent(event);
		}
		return event;
	}
	
}