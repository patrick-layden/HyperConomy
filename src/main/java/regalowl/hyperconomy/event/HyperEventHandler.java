package regalowl.hyperconomy.event;

import regalowl.simpledatalib.event.Event;
import regalowl.hyperconomy.HC;

public class HyperEventHandler {
	
    public void registerListener(Object listener) {
    	HC.hc.getSimpleDataLib().getEventPublisher().registerListener(listener);
    }
    public void unRegisterListener(Object listener) {
    	HC.hc.getSimpleDataLib().getEventPublisher().unRegisterListener(listener);
    }
    
    public void clearListeners() {
    	HC.hc.getSimpleDataLib().getEventPublisher().unRegisterAllListeners();
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
			HC.hc.getSimpleDataLib().getEventPublisher().fireEvent(event);
		}
    }
	
	public Event fireEvent(Event event) {
		return HC.hc.getSimpleDataLib().getEventPublisher().fireEvent(event);
	}
	
}