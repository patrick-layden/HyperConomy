package regalowl.hyperconomy.util;


import regalowl.hyperconomy.HC;

public class DebugMode {

	public DebugMode() {
		HC hc = HC.hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.getDataBukkit().setDebug(true);
	}
	
	public void debugWriteError(Exception e) {
		HC hc = HC.hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.gDB().writeError(e, "[Debug Mode Error]");
	}


	
	public void debugWriteMessage(String entry) {
		HC hc = HC.hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		hc.gDB().writeError("[Debug Mode Message]" + entry);
	}
	
	
	public void syncDebugConsoleMessage(String message) {
		HC hc = HC.hc;
		if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
		HC.mc.logInfo("[HyperConomy Debug]" + message);
	}
	
	public void ayncDebugConsoleMessage(String message) {
		AsyncConsoleDebug cd  = new AsyncConsoleDebug(message);
		new Thread(cd).start();
	}
	private class AsyncConsoleDebug implements Runnable {
		private String m;
		private HC hc;
		public AsyncConsoleDebug(String message) {
			this.m = message;
			this.hc = HC.hc;
		}
		@Override
		public void run() {
			if (!hc.getConf().getBoolean("enable-feature.debug-mode")) {return;}
			HC.mc.logInfo("[HyperConomy Debug]" + m);
		}
	}
	
	
}


