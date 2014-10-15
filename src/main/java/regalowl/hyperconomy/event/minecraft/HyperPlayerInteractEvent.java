package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;

public class HyperPlayerInteractEvent extends HyperEvent {

		private HyperPlayer hp;
		
		public HyperPlayerInteractEvent(HyperPlayer hp) {
			this.hp = hp;
		}

		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
}