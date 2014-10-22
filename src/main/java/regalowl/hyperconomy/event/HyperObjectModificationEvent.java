package regalowl.hyperconomy.event;

import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.databukkit.event.Event;

public class HyperObjectModificationEvent extends Event {
	private HyperObject ho;
	
	public HyperObjectModificationEvent(HyperObject ho) {
		this.ho = ho;
	}
	
	public HyperObject getHyperObject() {
		return ho;
	}
}