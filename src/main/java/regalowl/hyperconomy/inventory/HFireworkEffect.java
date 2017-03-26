package regalowl.hyperconomy.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import regalowl.simpledatalib.CommonFunctions;


public class HFireworkEffect {

	private List<HColor> colors = new ArrayList<HColor>();
	private List<HColor> fadeColors = new ArrayList<HColor>();
	private String type;
	private boolean hasFlicker;
	private boolean hasTrail;
 
	public HFireworkEffect(ArrayList<HColor> colors, ArrayList<HColor> fadeColors, String type, boolean hasFlicker, boolean hasTrail) {
		this.colors = colors;
		this.fadeColors = fadeColors;
        this.type = type;
        this.hasFlicker = hasFlicker;
        this.hasTrail = hasTrail;
    }

	public HFireworkEffect(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		ArrayList<String> c = CommonFunctions.explode(data.get("colors"));
		for (String cString:c) {
			colors.add(new HColor(cString));
		}
		ArrayList<String> fc = CommonFunctions.explode(data.get("fadeColors"));
		for (String cString:fc) {
			fadeColors.add(new HColor(cString));
		}
		type = data.get("type");
		hasFlicker = Boolean.parseBoolean(data.get("hasFlicker"));
		hasTrail = Boolean.parseBoolean(data.get("hasTrail"));
    }
	
	public HFireworkEffect(HFireworkEffect fe) {
		for (HColor c:fe.colors) {
			this.colors.add(new HColor(c));
		}
		for (HColor c:fe.colors) {
			this.fadeColors.add(new HColor(c));
		}
		type = fe.type;
		hasFlicker = fe.hasFlicker;
		hasTrail = fe.hasTrail;
    }

	public String serialize() {
		ArrayList<String> c = new ArrayList<String>();
		for (HColor hc:colors) {
			c.add(hc.serialize());
		}
		ArrayList<String> fc = new ArrayList<String>();
		for (HColor hc:fadeColors) {
			fc.add(hc.serialize());
		}
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("colors", CommonFunctions.implode(c));
		data.put("fadeColors", CommonFunctions.implode(fc));
		data.put("type", type);
		data.put("hasFlicker", hasFlicker+"");
		data.put("hasTrail", hasTrail+"");
		return CommonFunctions.implodeMap(data);
	}
	

	public List<HColor> getColors() {
		return colors;
	}
	public List<HColor> getFadeColors() {
		return fadeColors;
	}
	public String getType() {
		return type;
	}
	public boolean hasFlicker() {
		return hasFlicker;
	}
	public boolean hasTrail() {
		return hasTrail;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((colors == null) ? 0 : colors.hashCode());
		result = prime * result + ((fadeColors == null) ? 0 : fadeColors.hashCode());
		result = prime * result + (hasFlicker ? 1231 : 1237);
		result = prime * result + (hasTrail ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		HFireworkEffect other = (HFireworkEffect) obj;
		if (colors == null) {
			if (other.colors != null)
				return false;
		} else if (!colors.equals(other.colors))
			return false;
		if (fadeColors == null) {
			if (other.fadeColors != null)
				return false;
		} else if (!fadeColors.equals(other.fadeColors))
			return false;
		if (hasFlicker != other.hasFlicker)
			return false;
		if (hasTrail != other.hasTrail)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
