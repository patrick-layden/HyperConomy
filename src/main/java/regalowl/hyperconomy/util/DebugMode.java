package regalowl.hyperconomy.util;


import regalowl.hyperconomy.HyperConomy;

public class DebugMode {

	public DebugMode() {
		HyperConomy hc = HyperConomy.hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.getDataBukkit().setDebug(true);
	}
	
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
		AsyncConsoleDebug cd  = new AsyncConsoleDebug(message);
		new Thread(cd).start();
	}
	private class AsyncConsoleDebug implements Runnable {
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


