package regalowl.hyperconomy.event.minecraft;

import java.util.ArrayList;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.util.HBlock;

public class HBlockPistonExtendEvent extends Event {

	private ArrayList<HBlock> blocks;
	
	public HBlockPistonExtendEvent(ArrayList<HBlock> blocks) {
		this.blocks = blocks;
	}
	
	public ArrayList<HBlock> getBlocks() {
		return blocks;
	}
	
}
