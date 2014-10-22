package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.databukkit.event.Event;

public class HyperPlayerQuitEvent extends Event {

		private HyperPlayer hp;
		
		public HyperPlayerQuitEvent(HyperPlayer hp) {
			this.hp = hp;
		}

		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
}