package regalowl.hyperconomy.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;

public class UpdateChecker {
	
	private HyperConomy hc;
	private String currentVersion;
	private String latestVersion;
	private String type = "";
	private boolean dev;
	private boolean beta;
	private boolean rb;
	private boolean notifyInGame;
	
	public UpdateChecker(HyperConomy hc) {
		this.hc = hc;
		this.currentVersion = hc.getMC().getVersion();
	}
	
	public void runCheck() {
		if (!hc.getConf().getBoolean("updater.enabled")) return;
		dev = hc.getConf().getBoolean("updater.notify-for.dev-builds");
		beta = hc.getConf().getBoolean("updater.notify-for.beta-builds");
		rb = hc.getConf().getBoolean("updater.notify-for.recommended-builds");
		notifyInGame = hc.getConf().getBoolean("updater.notify-in-game");
		
		hc.getMC().logInfo("[HyperConomy]Checking for updates...");
		new Thread(new Runnable() {
			public void run() {
				try {
					URL url = new URL("https://api.curseforge.com/servermods/files?projectids=38059");
					URLConnection conn = url.openConnection();
					conn.setReadTimeout(10000);
					conn.addRequestProperty("User-Agent", "HyperConomy/v"+hc.getMC().getVersion()+" (by RegalOwl)");
					conn.setDoOutput(true);
					final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					final String response = reader.readLine();
					final JSONArray array = (JSONArray) JSONValue.parse(response);
					if (array.size() == 0) return;
					JSONObject lastObject = (JSONObject) array.get(array.size() - 1);
					String nameData = (String) lastObject.get("name");
					latestVersion = nameData.substring(1, nameData.indexOf(" "));
					if (nameData.contains("[") && nameData.contains("]")) {
						type = nameData.replace("[Lite]", "");
						type = type.substring(type.indexOf("[") + 1, type.length());
						type = type.substring(0, type.indexOf("]"));
					}
					if (type.equalsIgnoreCase("DEV") && !dev) return;
					if (type.equalsIgnoreCase("BETA") && !beta) return;
					if (type.equalsIgnoreCase("RB") && !rb) return;
					hc.getMC().runTask(new Runnable() {
						public void run() {
							int code = getVersionComparisonCode(currentVersion, latestVersion);
							if (code == -1) {
								if (notifyInGame) {
									hc.getMC().runTaskLater(new Runnable() {
										public void run() {
											MessageBuilder mb = new MessageBuilder(hc, "NEW_VERSION_AVAILABLE");
											mb.setValue(latestVersion);
											mb.setType(" ["+type+"]");
											for (HyperPlayer hp:hc.getMC().getOnlinePlayers()) {
												if (hp.hasPermission("hyperconomy.admin")) {
													hp.sendMessage(mb.build());
												}
											}
										}
									}, 600L);
								}
								hc.getMC().logInfo("[HyperConomy]A new "+"["+type+"] build (" + latestVersion + ") is available for download.");
							} else if (code == 0) {
								hc.getMC().logInfo("[HyperConomy]No updates available.");
							} else if (code == 1) {
								hc.getMC().logInfo("[HyperConomy]No updates available. You are running a development build.");
							}
						}
					});
				} catch (Exception e) {
					hc.gSDL().getErrorWriter().writeError(e);
				}
			}
		}).start();
	}

	private int getVersionComparisonCode(String value, String referenceValue) {
		String[] values = value.split("\\.");
		String[] referenceValues = referenceValue.split("\\.");
		if (Integer.parseInt(values[0]) > Integer.parseInt(referenceValues[0])) return 1;
		if (Integer.parseInt(values[0]) < Integer.parseInt(referenceValues[0])) return -1;
		if (Integer.parseInt(values[1]) > Integer.parseInt(referenceValues[1])) return 1;
		if (Integer.parseInt(values[1]) < Integer.parseInt(referenceValues[1])) return -1;
		if (Integer.parseInt(values[2]) > Integer.parseInt(referenceValues[2])) return 1;
		if (Integer.parseInt(values[2]) < Integer.parseInt(referenceValues[2])) return -1;
		return 0;
	}
	
	
	
	
}
