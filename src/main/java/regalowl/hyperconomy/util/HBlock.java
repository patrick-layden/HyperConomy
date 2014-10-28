package regalowl.hyperconomy.util;

import regalowl.hyperconomy.HyperConomy;



public class HBlock {

	private SimpleLocation location;
	
	
	public HBlock(SimpleLocation location) {
		this.location = location;
	}

	public SimpleLocation getLocation() {
		return location;
	}
	
	public boolean isChest() {
		return HyperConomy.mc.isChest(location);
	}
	
	public boolean canHoldChestShopSign() {
		return HyperConomy.mc.canHoldChestShopSign(location);
	}
	
	public HBlock[] getSurroundingBlocks() {
		HBlock[] blocks = new HBlock[6];
		SimpleLocation l1 = new SimpleLocation(location);
		l1.setY(l1.getY() + 1);
		blocks[0] = new HBlock(l1);
		SimpleLocation l2 = new SimpleLocation(location);
		l2.setY(l2.getY() - 1);
		blocks[1] = new HBlock(l2);
		HBlock[] nsew = getNorthSouthEastWestBlocks();
		blocks[2] = nsew[0];
		blocks[3] = nsew[1];
		blocks[4] = nsew[2];
		blocks[5] = nsew[3];
		return blocks;
	}
	
	public HBlock[] getNorthSouthEastWestBlocks() {
		HBlock[] blocks = new HBlock[4];
		SimpleLocation l1 = new SimpleLocation(location);
		l1.setX(l1.getX() + 1);
		blocks[0] = new HBlock(l1);
		SimpleLocation l2 = new SimpleLocation(location);
		l2.setX(l2.getX() - 1);
		blocks[1] = new HBlock(l2);
		SimpleLocation l3 = new SimpleLocation(location);
		l3.setZ(l3.getZ() + 1);
		blocks[2] = new HBlock(l3);
		SimpleLocation l4 = new SimpleLocation(location);
		l4.setZ(l4.getZ() - 1);
		blocks[3] = new HBlock(l4);
		return blocks;
	}
	
}
