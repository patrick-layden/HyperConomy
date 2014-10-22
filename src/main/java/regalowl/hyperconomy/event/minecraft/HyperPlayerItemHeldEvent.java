package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.databukkit.event.Event;

public class HyperPlayerItemHeldEvent extends Event {

		private HyperPlayer hp;
		
		public HyperPlayerItemHeldEvent(HyperPlayer hp) {
			this.hp = hp;
		}

		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
}