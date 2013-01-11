package regalowl.hyperconomy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ThreadSafeMessage {

	private String mess;
	private String perm;
	private Player play;
	private String splay;
	
	ThreadSafeMessage(String message, String permission, boolean broadcast) {
		mess = message;
		perm = permission;
		HyperConomy hc = HyperConomy.hc;
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			public void run() {
				Bukkit.broadcast(mess, perm);
			}
		}, 0L);
	}
	
	ThreadSafeMessage(String message, Player player) {
		mess = message;
		play = player;
		HyperConomy hc = HyperConomy.hc;
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			public void run() {
				play.sendMessage(mess);
			}
		}, 0L);
	}
	
	ThreadSafeMessage(String message, String player) {
		mess = message;
		splay = player;
		HyperConomy hc = HyperConomy.hc;
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			public void run() {
				Bukkit.getPlayer(splay).sendMessage(mess);
			}
		}, 0L);
	}
	
}
