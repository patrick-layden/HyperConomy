package regalowl.hyperconomy;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;




public class Hcset implements CommandExecutor {
	
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);;
			return true;
		}
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		InfoSignHandler ih = hc.getInfoSignHandler();
		try {
			String economy = hc.getConsoleSettings().getEconomy(sender);
			Player p = null;
			if (sender instanceof Player) {
				p = (Player)sender;
			}
			HyperEconomy he = em.getEconomy(economy);
			if (args.length == 0) {
				sender.sendMessage(L.get("HCSET_INVALID"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("name")) {
				try {
					if (args.length == 3) {
						String name = args[1];
						String newName = args[2];
						if (he.objectTest(name)) {
							he.getHyperObject(name).setName(newName);
							sender.sendMessage(L.get("NAME_SET"));
							hc.restart();
						} else {
							sender.sendMessage(L.get("INVALID_NAME"));
						}
					} else {
						sender.sendMessage(L.get("HCSET_NAME_INVALID"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_NAME_INVALID"));
				}
			}
			if (args[0].equalsIgnoreCase("ceiling")) {
				try {
					if (args.length == 3) {
						String name = args[1];
						double ceiling = Double.parseDouble(args[2]);
						if (he.objectTest(name)) {
							he.getHyperObject(name).setCeiling(ceiling);
							sender.sendMessage(L.f(L.get("CEILING_SET"), name));
							ih.updateSigns();
						} else {
							sender.sendMessage(L.get("INVALID_NAME"));
						}
					} else {
						sender.sendMessage(L.get("HCSET_CEILING_INVALID"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_CEILING_INVALID"));
				}
			}
			
		} catch (Exception e) {
			sender.sendMessage(L.get("HCSET_INVALID"));
		}
		return true;
	}
	
	

}
