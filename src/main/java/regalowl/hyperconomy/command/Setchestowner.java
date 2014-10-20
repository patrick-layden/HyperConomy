package regalowl.hyperconomy.command;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;

public class Setchestowner extends BaseCommand implements HyperCommand {


	public Setchestowner() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 1) {
				String name = args[0];
				String line3 = "";
				String line4 = "";
				if (name.length() > 12) {
					line3 = name.substring(0, 12);
					line4 = name.substring(12, name.length());
				} else {
					line3 = name;
					line4 = "";
				}
				@SuppressWarnings("deprecation")
				Block b = player.getTargetBlock(null, 500);
				if (b.getState() instanceof Chest) {
		    		Chest c = (Chest) b.getState();
					Block signblock = Bukkit.getWorld(c.getBlock().getWorld().getName()).getBlockAt(c.getX(), c.getY() + 1, c.getZ());
					if (signblock != null && signblock.getType().equals(Material.WALL_SIGN)) {
			    		Sign s = (Sign) signblock.getState();
						String line2 = s.getLine(1).trim();
				    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
			    			s.setLine(2, "\u00A7f" + line3);
			    			s.setLine(3, "\u00A7f" + line4);
				    		s.update();
				    		data.addResponse(L.get("CHEST_OWNER_UPDATED"));
				    	}
			    	}
		    	} else if (b != null && b.getType().equals(Material.WALL_SIGN)) {
		    	    Sign s = (Sign) b.getState();
					String line2 = s.getLine(1).trim();
			    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
						BlockState chestblock = Bukkit.getWorld(s.getBlock().getWorld().getName()).getBlockAt(s.getX(), s.getY() - 1, s.getZ()).getState();
				    	if (chestblock instanceof Chest) {
			    			s.setLine(2, "\u00A7f" + line3);
			    			s.setLine(3, "\u00A7f" + line4);
				    		s.update();
				    		data.addResponse(L.get("CHEST_OWNER_UPDATED"));
				    	}
			    	}
		    	} else {
		    		data.addResponse(L.get("LOOK_AT_VALID_CHESTSHOP"));
		    	}
			} else {
				data.addResponse(L.get("SETCHESTOWNER_INVALID"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("SETCHESTOWNER_INVALID"));
		}
	}
}
