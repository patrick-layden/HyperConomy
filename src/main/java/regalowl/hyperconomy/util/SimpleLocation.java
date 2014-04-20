package regalowl.hyperconomy.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SimpleLocation {

	private double x;
	private double y;
	private double z;
	private String world;
	
	public SimpleLocation(Location l) {
		this.world = l.getWorld().getName();
		this.x = l.getX();
		this.y = l.getY();
		this.z = l.getZ();
	}
	
	public SimpleLocation(String world, double x, double y, double z) {
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
	
	
	public Location getLocation() {
		return new Location(Bukkit.getWorld(world),x,y,z);
	}
	
	
}
