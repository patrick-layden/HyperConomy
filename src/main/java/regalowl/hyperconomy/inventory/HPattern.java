package regalowl.hyperconomy.inventory;

import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

public class HPattern {

	private String dyeColor;
	private String patternType;
	
	public HPattern(String dyeColor, String patternType) {
		this.dyeColor = dyeColor;
		this.patternType = patternType;
	}

	public String serialize() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("dyeColor", dyeColor);
		data.put("patternType", patternType);
		return CommonFunctions.implodeMap(data);
	}
	
	public HPattern(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.dyeColor = data.get("dyeColor");
		this.patternType = data.get("patternType");
    }
	
	public HPattern(HPattern hp) {
		this.dyeColor = hp.dyeColor;
		this.patternType = hp.patternType;
    }
	
	public String getDyeColor() {
		return dyeColor;
	}
	
	public String getPatternType() {
		return patternType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dyeColor == null) ? 0 : dyeColor.hashCode());
		result = prime * result + ((patternType == null) ? 0 : patternType.hashCode());
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
		HPattern other = (HPattern) obj;
		if (dyeColor == null) {
			if (other.dyeColor != null)
				return false;
		} else if (!dyeColor.equals(other.dyeColor))
			return false;
		if (patternType == null) {
			if (other.patternType != null)
				return false;
		} else if (!patternType.equals(other.patternType))
			return false;
		return true;
	}
	
	
}
