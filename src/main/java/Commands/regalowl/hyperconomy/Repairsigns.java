package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Repairsigns {
	
	
	private ArrayList<String> signtypes = new ArrayList<String>();
	
	Repairsigns(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		
		FileConfiguration sns = hc.getYaml().getSigns();
		
		ArrayList<String> names = hc.getNames();
		signtypes.add("buy");
		signtypes.add("sell");
		signtypes.add("stock");
		signtypes.add("value");
		signtypes.add("status");
		signtypes.add("static price");
		signtypes.add("start price");
		signtypes.add("median");
		signtypes.add("history");
		signtypes.add("tax");
		signtypes.add("sb");
		if (args.length == 3 || args.length == 1) {
			int xrad = Math.abs(Integer.parseInt(args[0]));
			int yrad = xrad;
			int zrad = xrad;
			
			if (args.length == 3) {
				xrad = Math.abs(Integer.parseInt(args[0]));
				yrad = Math.abs(Integer.parseInt(args[1]));
				zrad = Math.abs(Integer.parseInt(args[2]));
			}
			
			int maxVolume = 1000000;
			int volume = xrad * yrad * zrad * 8;
			if (volume > maxVolume) {
				player.sendMessage(L.f(L.get("VOLUME_TOO_LARGE"), maxVolume));
				return;
			}
			
			Location pl = player.getLocation();
			World w = player.getWorld();
			
			int px = pl.getBlockX();
			int py = pl.getBlockY();
			int pz = pl.getBlockZ();
			
			int signsRepaired = 0;
			
			for (int i = (px - xrad); i <= (px + xrad); i++) {
				for (int j = (pz - zrad); j <= (pz + zrad); j++) {
					for (int k = (py - yrad); k <= (py + yrad); k++) {
						if (w.getChunkAt(new Location(w, i, k, j)).isLoaded()) {
							Block cb = w.getBlockAt(i, k, j);
							
							if (cb != null && cb.getType().equals(Material.SIGN_POST) || cb != null && cb.getType().equals(Material.WALL_SIGN)) {
								Sign s = (Sign) cb.getState();
								String line12 = ChatColor.stripColor(s.getLine(0)).trim() + ChatColor.stripColor(s.getLine(1)).trim();
								line12 = hc.fixName(line12);
								if (names.contains(line12.toLowerCase())) {
									String ttype = ChatColor.stripColor(s.getLine(2)).toLowerCase();
									if (ttype.contains("S:") || ttype.contains("s:")) {
										ttype = "sb";
									}
									String type = getsignType(ttype.replace(":", ""));
									if (type != null) {
										String locat = s.getBlock().getWorld().getName() + "|" + s.getBlock().getX() + "|" + s.getBlock().getY() + "|" + s.getBlock().getZ();
											sns.set(locat + ".itemname", line12);
											sns.set(locat + ".type", type);
											if (hc.useSQL()) {
												sns.set(locat + ".economy", hc.getSQLFunctions().getPlayerEconomy(player.getName()));
											} else {
												sns.set(locat + ".economy", "default");
											}
											signsRepaired++;
									}
								}
							}
						}
					}
				}
			}
			if (signsRepaired > 0) {
				InfoSign is = hc.getInfoSign();
				is.resetAll();
				is.setrequestsignUpdate(true);
				is.checksignUpdate();
				player.sendMessage(L.f(L.get("X_SIGNS_REPAIRED"), signsRepaired));
			} else {
				player.sendMessage(L.get("NO_SIGNS_FOUND"));
			}
			
		} else {
			player.sendMessage(L.get("REPAIRSIGNS_INVALID"));
		}
	}
	
	
	
	private String getsignType(String line3) {
		String type = null;
		int counter = 0;
		while (counter < signtypes.size()) {
			if (line3.equalsIgnoreCase(signtypes.get(counter))) {
				type = signtypes.get(counter);
				break;
			}
			counter++;
		}
		return type;
	}

}
