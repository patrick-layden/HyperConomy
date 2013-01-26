package regalowl.hyperconomy;

public class SQLRetry {
	
	private String playerecon;
	private String econplayer;
	private HyperConomy hc;

	public void retrySetEconomy(HyperConomy hyc, String player, String economy) {
		hc = hyc;
		playerecon = economy;
		econplayer = player;
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			public void run() {
				hc.getDataFunctions().setPlayerEconomy(econplayer, playerecon);
			}
		}, 20L);
	}
	
}
