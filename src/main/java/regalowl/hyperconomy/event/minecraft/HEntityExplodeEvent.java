package regalowl.hyperconomy.event.minecraft;

import java.util.ArrayList;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.minecraft.HBlock;

public class HEntityExplodeEvent extends Event {

	private ArrayList<HBlock> blocks;
	
	public HEntityExplodeEvent(ArrayList<HBlock> blocks) {
		this.blocks = blocks;
	}
	
	public ArrayList<HBlock> getBrokenBlocks() {
		return blocks;
	}
	
}
