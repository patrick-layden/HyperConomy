package regalowl.hyperconomy.event.minecraft;


import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.databukkit.event.Event;

public class HyperPlayerInteractEvent extends Event {

		private HyperPlayer hp;
		private HBlock block;
		private boolean isLeftClick;
		
		public HyperPlayerInteractEvent(HyperPlayer hp, HBlock block, boolean isLeftClick) {
			this.hp = hp;
			this.block = block;
			this.isLeftClick = isLeftClick;
		}

		public HyperPlayer getHyperPlayer() {
			return hp;
		}
	
		public HBlock getBlock() {
			return block;
		}
		
		public boolean isLeftClick() {
			return isLeftClick;
		}
}