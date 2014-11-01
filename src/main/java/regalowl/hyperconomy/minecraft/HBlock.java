package regalowl.hyperconomy.minecraft;

import regalowl.hyperconomy.HC;



public class HBlock {

	private HLocation location;
	
	
	public HBlock(HLocation location) {
		this.location = location;
	}

	public HLocation getLocation() {
		return location;
	}
	
	public boolean isChest() {
		return HC.mc.isChest(location);
	}
	
	public boolean isInfoSign() {
		return HC.mc.isInfoSign(location);
	}
	
	public boolean isTransactionSign() {
		return HC.mc.isTransactionSign(location);
	}
	
	public boolean canHoldChestShopSign() {
		return HC.mc.canHoldChestShopSign(location);
	}
	
	public HBlock getFirstNonAirBlockBelow() {
		return HC.mc.getFirstNonAirBlockInColumn(location);
	}
	
	public boolean canFall() {
		return HC.mc.canFall(this);
	}
	
	public HBlock[] getSurroundingBlocks() {
		HBlock[] blocks = new HBlock[6];
		HLocation l1 = new HLocation(location);
		l1.setY(l1.getY() + 1);
		blocks[0] = new HBlock(l1);
		HLocation l2 = new HLocation(location);
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
		HLocation l1 = new HLocation(location);
		l1.setX(l1.getX() + 1);
		blocks[0] = new HBlock(l1);
		HLocation l2 = new HLocation(location);
		l2.setX(l2.getX() - 1);
		blocks[1] = new HBlock(l2);
		HLocation l3 = new HLocation(location);
		l3.setZ(l3.getZ() + 1);
		blocks[2] = new HBlock(l3);
		HLocation l4 = new HLocation(location);
		l4.setZ(l4.getZ() - 1);
		blocks[3] = new HBlock(l4);
		return blocks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HBlock other = (HBlock) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}
	
}
