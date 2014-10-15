package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;

public class HyperPlayerItemHeldEvent extends HyperEvent {

		private HyperPlayer hp;
		
		public HyperPlayerItemHeldEvent(HyperPlayer hp) {
			this.hp = hp;
		}

		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
}