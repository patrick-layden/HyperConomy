package regalowl.hyperconomy.event.minecraft;

import java.util.ArrayList;

import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.minecraft.HBlock;

public class HEntityExplodeEvent extends HyperEvent {

	private ArrayList<HBlock> blocks;
	
	public HEntityExplodeEvent(ArrayList<HBlock> blocks) {
		this.blocks = blocks;
	}
	
	public ArrayList<HBlock> getBrokenBlocks() {
		return blocks;
	}
	
}
