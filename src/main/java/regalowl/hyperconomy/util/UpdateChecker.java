package regalowl.hyperconomy.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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
	
	private boolean upgradeAvailable = false;
	private boolean runningDevBuild = false;
	
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
			@Override
			public void run() {
				try {
					URL url = new URL("https://api.curseforge.com/servermods/files?projectids=38059");
					URLConnection conn = url.openConnection();
					conn.setReadTimeout(10000);
					conn.addRequestProperty("User-Agent", "HyperConomy/v"+hc.getMC().getVersion()+" (by RegalOwl)");
					conn.setDoOutput(true);
					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String response = reader.readLine();
					JSONArray array = (JSONArray) JSONValue.parse(response);
					if (array.size() == 0) return;
					boolean start = false;
					ArrayList<String> acceptableUpgrades = new ArrayList<String>();
					for (Object o:array) {
						JSONObject jObject = (JSONObject)o;
						String nameData = (String)jObject.get("name");
						nameData = nameData.trim();
						if (nameData.equals("v0.974.1 [Dev]")) start = true;
						if (!start) continue;
						String version = nameData.substring(1, nameData.indexOf(" "));
						latestVersion = version;
						String type = getType(nameData);
						int code = getVersionComparisonCode(currentVersion, version);
						//hc.getMC().logSevere("["+version+"]["+code+"]");
						if (code >= 0) continue;
						if (type.equalsIgnoreCase("BROKEN")) continue;
						if (type.equalsIgnoreCase("DEV") && !dev) continue;
						if (type.equalsIgnoreCase("BETA") && !beta) continue;
						if (type.equalsIgnoreCase("RB") && !rb) continue;
						acceptableUpgrades.add(nameData);
					}
					if (acceptableUpgrades.size() == 0) {
						int code = getVersionComparisonCode(currentVersion, latestVersion);
						if (code == 1) runningDevBuild = true;
					} else {
						latestVersion = acceptableUpgrades.get(acceptableUpgrades.size() - 1);
						type = getType(latestVersion);
						latestVersion = latestVersion.substring(1, latestVersion.indexOf(" "));
						upgradeAvailable = true;
					}
					hc.getMC().runTask(new Runnable() {
						@Override
						public void run() {
							if (upgradeAvailable) {
								if (notifyInGame) notifyAdmins();
								hc.getMC().logInfo("[HyperConomy]A new "+"["+type+"] build (" + latestVersion + ") is available for download.");
							} else if (runningDevBuild) {
								hc.getMC().logInfo("[HyperConomy]No updates available. You are running a development build.");
							} else {
								hc.getMC().logInfo("[HyperConomy]No updates available.");
							}
						}
					});
				} catch (SocketTimeoutException te) {
					hc.getMC().logInfo("[HyperConomy]Could not connect to server.");
				} catch (Exception e) {
					hc.gSDL().getErrorWriter().writeError(e);
				}
			}
		}).start();
	}
	
	private String getType(String data) {
		String type = "";
		if (data.contains("[") && data.contains("]")) {
			type = data.replace("[Lite]", "");
			type = type.substring(type.indexOf("[") + 1, type.length());
			type = type.substring(0, type.indexOf("]"));
		}
		return type;
	}
	
	private void notifyAdmins() {
		hc.getMC().runTaskLater(new Runnable() {
			@Override
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

	private int getVersionComparisonCode(String currentVersion, String latestVersion) {
		try {
			String[] values = currentVersion.split("\\.");
			String[] referenceValues = latestVersion.split("\\.");
			if (referenceValues.length < 3) return 1;
			if (Integer.parseInt(values[0]) > Integer.parseInt(referenceValues[0])) return 1;
			if (Integer.parseInt(values[0]) < Integer.parseInt(referenceValues[0])) return -1;
			if (Integer.parseInt(values[1]) > Integer.parseInt(referenceValues[1])) return 1;
			if (Integer.parseInt(values[1]) < Integer.parseInt(referenceValues[1])) return -1;
			if (Integer.parseInt(values[2]) > Integer.parseInt(referenceValues[2])) return 1;
			if (Integer.parseInt(values[2]) < Integer.parseInt(referenceValues[2])) return -1;
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	
	
}
