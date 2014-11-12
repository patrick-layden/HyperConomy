package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.simpledatalib.event.Event;

public class HPlayerQuitEvent extends Event {

		private HyperPlayer hp;
		
		public HPlayerQuitEvent(HyperPlayer hp) {
			this.hp = hp;
		}

		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
}