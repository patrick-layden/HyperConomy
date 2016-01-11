package regalowl.hyperconomy.event;

import java.io.Serializable;

import regalowl.hyperconomy.HyperEconomy;
import regalowl.simpledatalib.event.Event;

public class HyperEconomyCreationEvent extends Event implements Serializable {
	

	private static final long serialVersionUID = -6574521760650516023L;
	private HyperEconomy he;
	
	public HyperEconomyCreationEvent(HyperEconomy he) {
		this.he = he;
	}
	
	public HyperEconomy getHyperEconomy() {
		return he;
	}
	
	
}