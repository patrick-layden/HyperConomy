package regalowl.hyperconomy.inventory;


import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

public class HColor {

	private int red;
	private int green;
	private int blue;
 
	public HColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public String serialize() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("red", red+"");
		data.put("green", green+"");
		data.put("blue", blue+"");
		return CommonFunctions.implodeMap(data);
	}
	
	public HColor(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.red = Integer.parseInt(data.get("red"));
		this.green = Integer.parseInt(data.get("green"));
		this.blue = Integer.parseInt(data.get("blue"));
    }
	

	public int getRed() {
		return red;
	}
	public int getGreen() {
		return green;
	}
	public int getBlue() {
		return blue;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + blue;
		result = prime * result + green;
		result = prime * result + red;
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
		HColor other = (HColor) obj;
		if (blue != other.blue)
			return false;
		if (green != other.green)
			return false;
		if (red != other.red)
			return false;
		return true;
	}


}
