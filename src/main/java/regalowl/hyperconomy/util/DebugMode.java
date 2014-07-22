package regalowl.hyperconomy.util;


import org.bukkit.scheduler.BukkitRunnable;

import regalowl.hyperconomy.HyperConomy;

public class DebugMode {

	public void debugWriteError(Exception e) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.gDB().writeError(e, "[Debug Mode Error]");
	}


	
	public void debugWriteMessage(String entry) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.gDB().writeError("[Debug Mode Message]" + entry);
	}
	
	
	public void syncDebugConsoleMessage(String message) {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.log().info("[HyperConomy Debug]" + message);
	}
	
	public void ayncDebugConsoleMessage(String message) {
		HyperConomy hc = HyperConomy.hc;
		AsyncConsoleDebug cd  = new AsyncConsoleDebug(message);
		cd.runTask(hc);
	}
	private class AsyncConsoleDebug extends BukkitRunnable {
		private String m;
		private HyperConomy hc;
		public AsyncConsoleDebug(String message) {
			this.m = message;
			this.hc = HyperConomy.hc;
		}
		@Override
		public void run() {
			if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
			hc.log().info("[HyperConomy Debug]" + m);
		}
	}
	
	
}


