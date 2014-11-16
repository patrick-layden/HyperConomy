package regalowl.hyperconomy.minecraft;

import regalowl.hyperconomy.HyperConomy;





public class HLocation {


	
	private double x;
	private double y;
	private double z;
	private String world;

	
	public HLocation(HLocation l) {
		this.world = l.getWorld();
		this.x = l.getX();
		this.y = l.getY();
		this.z = l.getZ();
	}
	
	public HLocation(String world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public String getWorld() {
		return world;
	}
	
	public int getBlockX() {
		return (int)x;
	}
	public int getBlockY() {
		return (int)y;
	}
	public int getBlockZ() {
		return (int)z;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
	public void setZ(double z) {
		this.z = z;
	}
	public void setWorld(String world) {
		this.world = world;
	}
	
	
	public boolean isLoaded(HyperConomy hc) {
		return hc.getMC().isLoaded(this);
	}
	public void load(HyperConomy hc) {
		hc.getMC().load(this);
	}
	public HBlock getBlock(HyperConomy hc) {
		return new HBlock(hc, this);
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		HLocation other = (HLocation) obj;
		if (world == null) {
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
	
	
}
