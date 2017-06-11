package regalowl.hyperconomy.event;

import java.io.Serializable;

public class HyperEconomyDeletionEvent extends HyperEvent implements Serializable {
	


	private static final long serialVersionUID = 7293641863711872068L;
	private String economy;
	
	public HyperEconomyDeletionEvent(String economy) {
		this.economy = economy;
	}
	
	public String getHyperEconomyName() {
		return economy;
	}
	
	
}