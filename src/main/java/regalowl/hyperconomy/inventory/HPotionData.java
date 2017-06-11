package regalowl.hyperconomy.inventory;


import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

 

public class HPotionData {
	private String potionType;
    private boolean isExtended;
    private boolean isUpgraded;
 
	public HPotionData(String potionType, boolean isExtended, boolean isUpgraded) {
        this.potionType = potionType;
        this.isExtended = isExtended;
        this.isUpgraded = isUpgraded;
	}
	
	public String serialize() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("potionType", potionType);
		data.put("isExtended", isExtended+"");
		data.put("isUpgraded", isUpgraded+"");
		return CommonFunctions.implodeMap(data);
	}
	
	public HPotionData(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.potionType = data.get("potionType");
		this.isExtended = Boolean.parseBoolean(data.get("isExtended"));
		this.isUpgraded = Boolean.parseBoolean(data.get("isUpgraded"));
    }
	
	public HPotionData(HPotionData hpd) {
		this.potionType = hpd.potionType;
		this.isExtended = hpd.isExtended;
		this.isUpgraded = hpd.isUpgraded;
    }


	public String getPotionType() {
		return potionType;
	}

	public boolean isExtended() {
		return isExtended;
	}
	
	public boolean isUpgraded() {
		return isUpgraded;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isExtended ? 1231 : 1237);
		result = prime * result + (isUpgraded ? 1231 : 1237);
		result = prime * result + ((potionType == null) ? 0 : potionType.hashCode());
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
		HPotionData other = (HPotionData) obj;
		if (isExtended != other.isExtended)
			return false;
		if (isUpgraded != other.isUpgraded)
			return false;
		if (potionType == null) {
			if (other.potionType != null)
				return false;
		} else if (!potionType.equals(other.potionType))
			return false;
		return true;
	}


}