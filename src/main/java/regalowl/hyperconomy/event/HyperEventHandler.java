package regalowl.hyperconomy.event;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.HC;

public class HyperEventHandler {
	
    public void registerListener(Object listener) {
    	HC.hc.getDataBukkit().getEventPublisher().registerListener(listener);
    }
    public void unRegisterListener(Object listener) {
    	HC.hc.getDataBukkit().getEventPublisher().unRegisterListener(listener);
    }
    
    public void clearListeners() {
    	HC.hc.getDataBukkit().getEventPublisher().unRegisterAllListeners();
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
			HC.hc.getDataBukkit().getEventPublisher().fireEvent(event);
		}
    }
	
	public Event fireEvent(Event event) {
		return HC.hc.getDataBukkit().getEventPublisher().fireEvent(event);
	}
	
}