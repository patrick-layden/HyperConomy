package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.SimpleLocation;
import regalowl.databukkit.event.Event;

public class HyperSignChangeEvent extends Event {

		private String[] lines;
		private SimpleLocation l;
		private HyperPlayer hp;
		
		public HyperSignChangeEvent(String[] lines, SimpleLocation l, HyperPlayer hp) {
			this.l = l;
			this.lines = lines;
			this.hp = hp;
		}

		public String[] getLines() {
			return lines;
		}
		
		public SimpleLocation getLocation() {
			return l;
		}
		
		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
}