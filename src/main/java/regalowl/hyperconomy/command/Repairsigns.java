package regalowl.hyperconomy.command;


import java.util.HashMap;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.display.SignType;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.util.LanguageFile;

public class Repairsigns {
	
	

	
	Repairsigns(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataManager em = hc.getDataManager();
		
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
										HashMap<String,String> conditions = new HashMap<String,String>();
										conditions.put("WORLD", s.getBlock().getWorld().getName());
										conditions.put("X", s.getBlock().getX()+"");
										conditions.put("Y", s.getBlock().getY()+"");
										conditions.put("Z", s.getBlock().getZ()+"");
										hc.getSQLWrite().performDelete("hyperconomy_info_signs", conditions);
										HashMap<String,String> values = new HashMap<String,String>();
										values.put("WORLD", s.getBlock().getWorld().getName());
										values.put("X", s.getBlock().getX()+"");
										values.put("Y", s.getBlock().getY()+"");
										values.put("Z", s.getBlock().getZ()+"");
										values.put("HYPEROBJECT", objectName);
										values.put("TYPE", type.toString());
										values.put("MULTIPLIER", "1");
										values.put("ECONOMY", em.getHyperPlayer(player).getEconomy());
										String eclass = "";
										if (em.getEconomy("default").enchantTest(objectName)) {
											eclass = EnchantmentClass.DIAMOND.toString();
										} else {
											eclass = EnchantmentClass.NONE.toString();
										}
										values.put("ECLASS", eclass);
										hc.getSQLWrite().performInsert("hyperconomy_info_signs", values);
										signsRepaired++;
									}
								}
							}
						}
					}
				}
			}
			if (signsRepaired > 0) {
				hc.getInfoSignHandler().reloadSigns();
				hc.getServer().getScheduler().runTaskLater(hc, new Runnable() {
					public void run() {
						HyperConomy.hc.getInfoSignHandler().updateSigns();
					}
				}, 60L);
				player.sendMessage(L.f(L.get("X_SIGNS_REPAIRED"), signsRepaired));
			} else {
				player.sendMessage(L.get("NO_SIGNS_FOUND"));
			}
			
		} else {
			player.sendMessage(L.get("REPAIRSIGNS_INVALID"));
		}
	}
	
	

}
