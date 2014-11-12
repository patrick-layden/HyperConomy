package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.simpledatalib.event.Event;

public class HPlayerJoinEvent extends Event {

		private HyperPlayer hp;
		
		public HPlayerJoinEvent(HyperPlayer hp) {
			this.hp = hp;
		}

		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
}