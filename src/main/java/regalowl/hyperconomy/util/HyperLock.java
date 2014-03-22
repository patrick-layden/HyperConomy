package regalowl.hyperconomy.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperConomy;

public class HyperLock {

	private boolean loadLock;
	private boolean fullLock;
	private boolean playerLock;
	
	private LanguageFile L;
	private HyperConomy hc;
	
	public HyperLock(boolean loadLock, boolean fullLock, boolean playerLock) {
		hc = HyperConomy.hc;
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
	
	public boolean isLocked(CommandSender sender) {
		if (playerLock && !sender.hasPermission("hyperconomy.admin")) {
			return true;
		}
		if (fullLock || loadLock) {
			return true;
		}
		return false;
	}
	public boolean isLocked(Player player) {
		if (playerLock && !player.hasPermission("hyperconomy.admin")) {
			return true;
		}
		if (fullLock || loadLock) {
			return true;
		}
		return false;
	}
	
	public void sendLockMessage(CommandSender sender) {
		if (playerLock && !sender.hasPermission("hyperconomy.admin")) {
			sender.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
		}
		if (loadLock) {
			sender.sendMessage(L.get("HYPERCONOMY_LOADING"));
		}
		if (fullLock) {
			sender.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
		}
	}
	public void sendLockMessage(Player player) {
		if (playerLock && !player.hasPermission("hyperconomy.admin")) {
			player.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
		}
		if (loadLock) {
			player.sendMessage(L.get("HYPERCONOMY_LOADING"));
		}
		if (fullLock) {
			player.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
		}
	}
}
