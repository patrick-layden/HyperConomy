package regalowl.hyperconomy.inventory;


import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

public class HPotionEffect {


	private String potionEffectType;
	private int amplifier;
	private int duration;
	private boolean isAmbient;
 
	
	public HPotionEffect(String potionEffectType, int amplifier, int duration, boolean isAmbient) {
		this.potionEffectType = potionEffectType;
		this.amplifier = amplifier;
		this.duration = duration;
		this.isAmbient = isAmbient;
    }

	public HPotionEffect(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.potionEffectType = data.get("potionEffectType");
		this.amplifier = Integer.parseInt(data.get("amplifier"));
		this.duration = Integer.parseInt(data.get("duration"));
		this.isAmbient = Boolean.parseBoolean(data.get("isAmbient"));
    }
	
	public HPotionEffect(HPotionEffect pe) {
		this.potionEffectType = pe.potionEffectType;
		this.amplifier = pe.amplifier;
		this.duration = pe.duration;
		this.isAmbient = pe.isAmbient;
    }

	public String serialize() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("potionEffectType", potionEffectType);
		data.put("amplifier", amplifier+"");
		data.put("duration", duration+"");
		data.put("isAmbient", isAmbient+"");
		return CommonFunctions.implodeMap(data);
	}

	
	public String getType() {
		return potionEffectType;
	}
	public int getAmplifier() {
		return amplifier;
	}
	public int getDuration() {
		return duration;
	}
	public boolean isAmbient() {
		return isAmbient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amplifier;
		result = prime * result + duration;
		result = prime * result + (isAmbient ? 1231 : 1237);
		result = prime * result + ((potionEffectType == null) ? 0 : potionEffectType.hashCode());
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
		HPotionEffect other = (HPotionEffect) obj;
		if (amplifier != other.amplifier)
			return false;
		if (duration != other.duration)
			return false;
		if (isAmbient != other.isAmbient)
			return false;
		if (potionEffectType == null) {
			if (other.potionEffectType != null)
				return false;
		} else if (!potionEffectType.equals(other.potionEffectType))
			return false;
		return true;
	}
	

	
}
