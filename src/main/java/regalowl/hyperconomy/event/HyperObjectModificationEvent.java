package regalowl.hyperconomy.event;

import regalowl.hyperconomy.hyperobject.HyperObject;

public class HyperObjectModificationEvent extends HyperEvent {
	private HyperObject ho;
	
	public HyperObjectModificationEvent(HyperObject ho) {
		this.ho = ho;
	}
	
	public HyperObject getHyperObject() {
		return ho;
	}
}