package regalowl.hyperconomy.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.simpledatalib.CommonFunctions;

public class HBannerMeta extends HItemMeta {
	
	private String baseColor;
	private ArrayList<HPattern> patterns = new ArrayList<HPattern>();

	public HBannerMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HItemFlag> itemFlags, String baseColor, ArrayList<HPattern> patterns) {
		super(displayName, lore, enchantments, itemFlags);
		this.patterns = patterns;
		this.baseColor = baseColor;
	}
	
	public HBannerMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		ArrayList<String> stringPatterns = CommonFunctions.explode(data.get("patterns"));
		for (String hp:stringPatterns) {
			patterns.add(new HPattern(hp));
		}
		this.baseColor = data.get("baseColor");
    }
	
	@Override
	public String serialize() {
		HashMap<String,String> data = super.getMap();
		data.put("baseColor", baseColor);
		ArrayList<String> stringPatterns = new ArrayList<String>();
		for (HPattern hp:patterns) {
			stringPatterns.add(hp.serialize());
		}
		data.put("patterns", CommonFunctions.implode(stringPatterns));
		return CommonFunctions.implodeMap(data);
	}
	
	@Override
	public ArrayList<String> displayInfo(HyperPlayer p, String color1, String color2) {
		ArrayList<String> info = super.displayInfo(p, color1, color2);
		info.add(color1 + "Base Color: " + color2 + displayName);
		String patternString = "";
		if (patterns != null && patterns.size() > 0) {
			for(HPattern pat:patterns) {
				patternString += pat.getDyeColor() + "," + pat.getPatternType() + ";";
			}
			patternString = patternString.substring(0, patternString.length() - 1);
		}
		info.add(color1 + "Patterns: " + color2 + patternString);
		return info;
	}
	
	@Override
	public HItemMetaType getType() {
		return HItemMetaType.BANNER;
	}
	

	public ArrayList<HPattern> getPatterns() {
		return patterns;
	}
	
	public String getBaseColor() {
		return baseColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((baseColor == null) ? 0 : baseColor.hashCode());
		result = prime * result + ((patterns == null) ? 0 : patterns.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HBannerMeta other = (HBannerMeta) obj;
		if (baseColor == null) {
			if (other.baseColor != null)
				return false;
		} else if (!baseColor.equals(other.baseColor))
			return false;
		if (patterns == null) {
			if (other.patterns != null)
				return false;
		} else if (!patterns.equals(other.patterns))
			return false;
		return true;
	}


}
