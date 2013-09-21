package regalowl.hyperconomy;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Repairsigns {
	
	

	
	Repairsigns(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		FileConfiguration sns = hc.getYaml().getSigns();
		
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
								String objectName = ChatColor.stripColor(s.getLine(0)).trim() + ChatColor.stripColor(s.getLine(1)).trim();
								objectName = em.getEconomy("default").fixName(objectName);
								if (em.getEconomy("default").objectTest(objectName)) {
									String ttype = ChatColor.stripColor(s.getLine(2).trim().replace(" ", "").toLowerCase());
									if (ttype.startsWith("s:")) {
										ttype = "SB";
									}
									SignType stype = SignType.fromString(ttype.replace(":", ""));
									String type = null;
									if (stype != null) {
										type = stype.toString();
									}
									if (type != null) {
										String signKey = s.getBlock().getWorld().getName() + "|" + s.getBlock().getX() + "|" + s.getBlock().getY() + "|" + s.getBlock().getZ();
											sns.set(signKey + ".itemname", objectName);
											if (em.getEconomy("default").enchantTest(objectName)) {
												sns.set(signKey + ".enchantclass", EnchantmentClass.DIAMOND.toString());
											} else {
												sns.set(signKey + ".enchantclass", EnchantmentClass.NONE.toString());
											}
											sns.set(signKey + ".multiplier", 1.0);
											sns.set(signKey + ".type", type);
											sns.set(signKey + ".economy", em.getHyperPlayer(player.getName()).getEconomy());
											signsRepaired++;
									}
								}
							}
						}
					}
				}
			}
			if (signsRepaired > 0) {
				InfoSignHandler is = hc.getInfoSignHandler();
				is.reloadSigns();
				is.updateSigns();
				player.sendMessage(L.f(L.get("X_SIGNS_REPAIRED"), signsRepaired));
			} else {
				player.sendMessage(L.get("NO_SIGNS_FOUND"));
			}
			
		} else {
			player.sendMessage(L.get("REPAIRSIGNS_INVALID"));
		}
	}
	
	

}
