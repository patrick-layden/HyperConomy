package regalowl.hyperconomy.inventory;

import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

public class HItemFlag {

	private String itemFlag;
	
	public HItemFlag(String itemFlag) {
		this.itemFlag = itemFlag;
	}
	
	public HItemFlag(HItemFlag hItemFlag) {
		this.itemFlag = hItemFlag.itemFlag;
	}

	public String serialize() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("itemFlag", itemFlag);
		return CommonFunctions.implodeMap(data);
	}
	
	
	public String getItemFlag() {
		return itemFlag;
	}


	public static HItemFlag deserialize(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		String itemFlag = data.get("itemFlag");
		return new HItemFlag(itemFlag);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemFlag == null) ? 0 : itemFlag.hashCode());
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
		HItemFlag other = (HItemFlag) obj;
		if (itemFlag == null) {
			if (other.itemFlag != null)
				return false;
		} else if (!itemFlag.equals(other.itemFlag))
			return false;
		return true;
	}
	
	
}
