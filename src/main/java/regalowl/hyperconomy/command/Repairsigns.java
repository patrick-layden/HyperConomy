package regalowl.hyperconomy.command;


import java.util.HashMap;





import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.display.SignType;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.util.HBlock;
import regalowl.hyperconomy.util.HSign;
import regalowl.hyperconomy.util.SimpleLocation;

public class Repairsigns extends BaseCommand implements HyperCommand {
	
	

	public Repairsigns() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
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
				data.addResponse(L.f(L.get("VOLUME_TOO_LARGE"), maxVolume));
				return data;
			}
			
			SimpleLocation pl = hp.getLocation();
			String w = pl.getWorld();
			
			int px = pl.getBlockX();
			int py = pl.getBlockY();
			int pz = pl.getBlockZ();
			
			int signsRepaired = 0;
			
			for (int i = (px - xrad); i <= (px + xrad); i++) {
				for (int j = (pz - zrad); j <= (pz + zrad); j++) {
					for (int k = (py - yrad); k <= (py + yrad); k++) {
						SimpleLocation loc = new SimpleLocation(w, i, k, j);
						if (loc.isLoaded()) {
							HBlock cb = loc.getBlock();
							if (cb != null && cb.isInfoSign()) {
								HSign s = HyperConomy.mc.getSign(loc);
								String objectName = HyperConomy.mc.removeColor(s.getLine(0)).trim() + HyperConomy.mc.removeColor(s.getLine(1)).trim();
								objectName = dm.getEconomy("default").fixName(objectName);
								if (dm.getEconomy("default").objectTest(objectName)) {
									String ttype = HyperConomy.mc.removeColor(s.getLine(2).trim().replace(" ", "").toLowerCase());
									if (ttype.contains("[")) {
										continue;
									}
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
										conditions.put("WORLD", loc.getWorld());
										conditions.put("X", loc.getX()+"");
										conditions.put("Y", loc.getY()+"");
										conditions.put("Z", loc.getZ()+"");
										hc.getSQLWrite().performDelete("hyperconomy_info_signs", conditions);
										HashMap<String,String> values = new HashMap<String,String>();
										values.put("WORLD", loc.getWorld());
										values.put("X", loc.getX()+"");
										values.put("Y", loc.getY()+"");
										values.put("Z", loc.getZ()+"");
										values.put("HYPEROBJECT", objectName);
										values.put("TYPE", type.toString());
										values.put("MULTIPLIER", "1");
										values.put("ECONOMY", hp.getEconomy());
										String eclass = "";
										if (dm.getEconomy("default").enchantTest(objectName)) {
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
				HyperConomy.mc.runTaskLater(new Runnable() {
					public void run() {
						HyperConomy.hc.getInfoSignHandler().updateSigns();
					}
				}, 60L);
				data.addResponse(L.f(L.get("X_SIGNS_REPAIRED"), signsRepaired));
			} else {
				data.addResponse(L.get("NO_SIGNS_FOUND"));
			}
			
		} else {
			data.addResponse(L.get("REPAIRSIGNS_INVALID"));
		}
		return data;
	}
	
	

}
