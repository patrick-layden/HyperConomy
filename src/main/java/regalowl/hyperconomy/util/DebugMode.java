package regalowl.hyperconomy.util;

import regalowl.hyperconomy.HyperConomy;

public class DebugMode {

	public void handleError(Exception e) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.gDB().writeError(e, "[Debug Mode Error]");
	}
	
}
