package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Resetshop {
	
	Resetshop(CommandSender sender, String[] args, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		try {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Are you sure you wish to do this?");
				sender.sendMessage(ChatColor.RED + "All item and enchantment stocks will be set to 0.");
				sender.sendMessage(ChatColor.RED + "All items and enchantments will return to initial pricing.");
				sender.sendMessage(ChatColor.RED + "Static pricing will be disabled for all items and enchantments.");
				sender.sendMessage(ChatColor.RED + "Type /resetshop confirm to proceed.");
			} else if (args[0].equalsIgnoreCase("confirm")) {
				if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
					new Backup();
				}
				ArrayList<String> names = hc.getNames();
				for (int c = 0; c < names.size(); c++) {
					String cname = names.get(c);
					sf.setStock(cname, playerecon, 0);
					sf.setStatic(cname, playerecon, "false");
					sf.setInitiation(cname, playerecon, "true");
				}
				sender.sendMessage(ChatColor.GOLD + "Shop stock, initiation, and static pricing have been reset!");
				isign.setrequestsignUpdate(true);
				isign.checksignUpdate();
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /resetshop");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /resetshop");
		}
	}
}
