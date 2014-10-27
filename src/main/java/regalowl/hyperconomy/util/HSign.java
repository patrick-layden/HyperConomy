package regalowl.hyperconomy.util;

import regalowl.hyperconomy.HyperConomy;

public class HSign {

	private SimpleLocation location;
	private String[] lines;
	private boolean isWallSign;
	
	public HSign(SimpleLocation location, String[] lines, boolean isWallSign) {
		this.location = location;
		this.lines = lines;
		this.isWallSign = isWallSign;
	}

	public SimpleLocation getLocation() {
		return location;
	}

	public String[] getLines() {
		return lines;
	}

	public void setLocation(SimpleLocation location) {
		this.location = location;
	}

	public void setLines(String[] lines) {
		this.lines = lines;
		HyperConomy.mc.setSign(this);
	}
	
	public boolean isWallSign() {
		return isWallSign;
	}
	
	public HBlock getAttachedBlock() {
		if (!isWallSign) return null;
		return HyperConomy.mc.getAttachedBlock(this);
	}
	
	public String getLine(int line) {
		if (lines.length > line && line >= 0) {
			return lines[line];
		}
		return "";
	}
	
	public void setLine(int line, String text) {
		if (lines.length > line && line >= 0) {
			lines[line] = text;
			HyperConomy.mc.setSign(this);
		}
	}
}
