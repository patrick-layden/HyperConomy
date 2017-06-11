package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.minecraft.HSign;

public class HSignChangeEvent extends HyperEvent {

		private HSign sign;
		private HyperPlayer hp;
		
		public HSignChangeEvent(HSign sign, HyperPlayer hp) {
			this.sign = sign;
			this.hp = hp;
		}

		public HSign getSign() {
			return sign;
		}
		
		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
}