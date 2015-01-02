package regalowl.hyperconomy.minecraft;

import java.io.Serializable;
import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;

public class HSign implements Serializable {

	private static final long serialVersionUID = 7389491795296647203L;

	private transient HyperConomy hc;
	
	private HLocation location;
	private ArrayList<String> lines = new ArrayList<String>();
	private boolean isWallSign;
	
	public HSign(HyperConomy hc, HLocation location, ArrayList<String> lines, boolean isWallSign) {
		this.hc = hc;
		this.location = location;
		this.lines.addAll(lines);
		this.isWallSign = isWallSign;
	}

	public HLocation getLocation() {
		return location;
	}

	public String[] getLines() {
		String[] aLines = new String[4];
		aLines[0] = lines.get(0);
		aLines[1] = lines.get(1);
		aLines[2] = lines.get(2);
		aLines[3] = lines.get(3);
		return aLines;
	}

	public void setLocation(HLocation location) {
		this.location = location;
	}

	public void setLines(ArrayList<String> lines) {
		this.lines.clear();
		this.lines.addAll(lines);
		hc.getMC().setSign(this);
	}
	
	public boolean isWallSign() {
		return isWallSign;
	}
	
	public HBlock getAttachedBlock() {
		if (!isWallSign) return null;
		return hc.getMC().getAttachedBlock(this);
	}
	
	public String getLine(int line) {
		if (lines.size() > line && line >= 0) {
			return lines.get(line);
		}
		return "";
	}
	public void setLine(int line, String text) {
		if (lines.size() > line && line >= 0) {
			lines.set(line, text);
		}
	}
	
	public void update() {
		new SignUpdater(this);
	}
	
	private class SignUpdater {
		private HSign s;
		public SignUpdater(HSign sign) {
			this.s = sign;
			hc.getMC().runTask(new Runnable() {
				public void run() {
					hc.getMC().setSign(s);
				}
			});
		}
	}
	
}
