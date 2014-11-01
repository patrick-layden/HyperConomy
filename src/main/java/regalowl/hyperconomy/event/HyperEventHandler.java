package regalowl.hyperconomy.event;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.HC;

public class HyperEventHandler {
	
	private HC hc;
	
    public HyperEventHandler() {
    	hc = HC.hc;
    }

    public void registerListener(Object listener) {
    	hc.getDataBukkit().getEventPublisher().registerListener(listener);
    }
    public void unRegisterListener(Object listener) {
    	hc.getDataBukkit().getEventPublisher().unRegisterListener(listener);
    }
    
    public void clearListeners() {
    	hc.getDataBukkit().getEventPublisher().unRegisterAllListeners();
    }
	
	public void fireEventFromAsyncThread(Event event) {
		HC.mc.runTask(new EventFire(event));
	}
    private class EventFire implements Runnable {
    	private Event event;
    	public EventFire(Event event) {
    		this.event = event;
    	}
		public void run() {
			hc.getDataBukkit().getEventPublisher().fireEvent(event);
		}
    }
	
	public Event fireEvent(Event event) {
		return hc.getDataBukkit().getEventPublisher().fireEvent(event);
	}
	
}