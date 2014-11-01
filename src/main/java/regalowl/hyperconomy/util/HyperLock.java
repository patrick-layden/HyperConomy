package regalowl.hyperconomy.util;


import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.command.CommandData;

public class HyperLock {

	private boolean loadLock;
	private boolean fullLock;
	private boolean playerLock;
	
	private LanguageFile L;
	private HC hc;
	
	public HyperLock(boolean loadLock, boolean fullLock, boolean playerLock) {
		hc = HC.hc;
		L = hc.getLanguageFile();
		this.loadLock = loadLock;
		this.fullLock = fullLock;
		this.playerLock = playerLock;
	}
	
	
	public void setLoadLock(boolean state) {
		loadLock = state;
	}
	public void setFullLock(boolean state) {
		fullLock = state;
		playerLock = state;
	}
	public void setPlayerLock(boolean state) {
		playerLock = state;
	}
	
	public boolean loadLock() {
		return loadLock;
	}
	public boolean fullLock() {
		return fullLock;
	}
	public boolean playerLock() {
		return playerLock;
	}
	
	public boolean isLocked(HyperPlayer hp) {
		if (fullLock || loadLock) return true;
		if (playerLock && hp != null && !hp.hasPermission("hyperconomy.admin")) return true;
		return false;
	}

	public boolean isLocked() {
		if (fullLock || loadLock) return true;
		return false;
	}
	
	public CommandData sendLockMessage(CommandData data) {
		if (loadLock) {
			data.addResponse(L.get("HYPERCONOMY_LOADING"));
			return data;
		}
		if (fullLock) {
			data.addResponse(L.get("GLOBAL_SHOP_LOCKED"));
			return data;
		}
		if (playerLock && data.isPlayer() && !data.getHyperPlayer().hasPermission("hyperconomy.admin")) {
			data.addResponse(L.get("GLOBAL_SHOP_LOCKED"));
			return data;
		}
		return data;
	}
	
	public void sendLockMessage(HyperPlayer hp) {
		if (loadLock) {
			hp.sendMessage(L.get("HYPERCONOMY_LOADING"));
		}
		if (fullLock) {
			hp.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
		}
		if (playerLock && !hp.hasPermission("hyperconomy.admin")) {
			hp.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
		}
	}

}
