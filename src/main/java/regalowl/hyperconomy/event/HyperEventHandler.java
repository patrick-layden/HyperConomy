package regalowl.hyperconomy.event;

import regalowl.simpledatalib.event.Event;
import regalowl.hyperconomy.HyperConomy;

public class HyperEventHandler {
	
	private HyperConomy hc;
	
	public HyperEventHandler(HyperConomy hc) {
		this.hc = hc;
	}
	
    public void registerListener(Object listener) {
    	hc.getSimpleDataLib().getEventPublisher().registerListener(listener);
    }
    public void unRegisterListener(Object listener) {
    	hc.getSimpleDataLib().getEventPublisher().unRegisterListener(listener);
    }
    
    public void clearListeners() {
    	hc.getSimpleDataLib().getEventPublisher().unRegisterAllListeners();
    }
	
	public void fireEventFromAsyncThread(Event event) {
		hc.getMC().runTask(new EventFire(event));
	}
    private class EventFire implements Runnable {
    	private Event event;
    	public EventFire(Event event) {
    		this.event = event;
    	}
		public void run() {
			hc.getSimpleDataLib().getEventPublisher().fireEvent(event);
		}
    }
	
	public Event fireEvent(Event event) {
		return hc.getSimpleDataLib().getEventPublisher().fireEvent(event);
	}
	
}