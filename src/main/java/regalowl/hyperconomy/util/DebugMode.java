package regalowl.hyperconomy.util;


import regalowl.hyperconomy.HyperConomy;

public class DebugMode {

	private transient HyperConomy hc;
	
	public DebugMode(HyperConomy hc) {
		this.hc = hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.getSimpleDataLib().setDebug(true);
	}
	
	public void debugWriteError(Exception e) {
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.gSDL().getErrorWriter().writeError(e, "[Debug Mode Error]");
	}


	
	public void debugWriteMessage(String entry) {
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.gSDL().getErrorWriter().writeError("[Debug Mode Message]" + entry);
	}
	
	
	public void syncDebugConsoleMessage(String message) {
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.getMC().logInfo("[HyperConomy Debug]" + message);
	}
	
	public void ayncDebugConsoleMessage(String message) {
		AsyncConsoleDebug cd  = new AsyncConsoleDebug(message);
		new Thread(cd).start();
	}
	private class AsyncConsoleDebug implements Runnable {
		private String m;
		public AsyncConsoleDebug(String message) {
			this.m = message;
		}
		@Override
		public void run() {
			if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
			hc.getMC().logInfo("[HyperConomy Debug]" + m);
		}
	}
	
	
}


