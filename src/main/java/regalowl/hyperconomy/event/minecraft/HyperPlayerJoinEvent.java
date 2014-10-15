package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;

public class HyperPlayerJoinEvent extends HyperEvent {

		private HyperPlayer hp;
		
		public HyperPlayerJoinEvent(HyperPlayer hp) {
			this.hp = hp;
		}

		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
}