package regalowl.hyperconomy.inventory;

import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

public class HItemFlag {

	private String itemFlag;
	
	public HItemFlag(String itemFlag) {
		this.itemFlag = itemFlag;
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
	
	
}
