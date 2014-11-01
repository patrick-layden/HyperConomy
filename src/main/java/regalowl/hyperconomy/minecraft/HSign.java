package regalowl.hyperconomy.minecraft;

import regalowl.hyperconomy.HC;

public class HSign {

	private HLocation location;
	private String[] lines;
	private boolean isWallSign;
	
	public HSign(HLocation location, String[] lines, boolean isWallSign) {
		this.location = location;
		this.lines = lines;
		this.isWallSign = isWallSign;
	}

	public HLocation getLocation() {
		return location;
	}

	public String[] getLines() {
		return lines;
	}

	public void setLocation(HLocation location) {
		this.location = location;
	}

	public void setLines(String[] lines) {
		this.lines = lines;
		HC.mc.setSign(this);
	}
	
	public boolean isWallSign() {
		return isWallSign;
	}
	
	public HBlock getAttachedBlock() {
		if (!isWallSign) return null;
		return HC.mc.getAttachedBlock(this);
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
			HC.mc.setSign(this);
		}
	}
	
	public void update() {
		HC.mc.updateSign(this);
	}
}
