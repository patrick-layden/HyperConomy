package regalowl.hyperconomy.event.minecraft;

import java.util.ArrayList;

import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.minecraft.HBlock;

public class HBlockPistonExtendEvent extends HyperEvent {

	private ArrayList<HBlock> blocks;
	
	public HBlockPistonExtendEvent(ArrayList<HBlock> blocks) {
		this.blocks = blocks;
	}
	
	public ArrayList<HBlock> getBlocks() {
		return blocks;
	}
	
}
