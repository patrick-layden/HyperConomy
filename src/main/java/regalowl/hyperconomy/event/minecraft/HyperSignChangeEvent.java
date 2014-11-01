package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.databukkit.event.Event;

public class HyperSignChangeEvent extends Event {

		private HSign sign;
		private HyperPlayer hp;
		
		public HyperSignChangeEvent(HSign sign, HyperPlayer hp) {
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