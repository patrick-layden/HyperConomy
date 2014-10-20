package regalowl.hyperconomy.command;


import org.bukkit.ChatColor;

import regalowl.databukkit.sql.SQLWrite;


public class Intervals extends BaseCommand implements HyperCommand {

	public Intervals() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 0) {
				SQLWrite sw = hc.getSQLWrite();
				data.addResponse(L.get("LINE_BREAK"));
				data.addResponse(ChatColor.GREEN + "" + dm.getHyperShopManager().getShopCheckInterval() + ChatColor.BLUE + " tick (" + ChatColor.GREEN + "" + dm.getHyperShopManager().getShopCheckInterval() / 20 + ChatColor.BLUE + " second) shop update interval.");
				data.addResponse(ChatColor.GREEN + "" + hc.gYH().getSaveInterval()/1000 + ChatColor.BLUE + " second save interval.");
				data.addResponse(ChatColor.GREEN + "" + sw.getBufferSize() + ChatColor.BLUE + " statements in the SQL write buffer.");
				data.addResponse(L.get("LINE_BREAK"));
			} else {
				data.addResponse(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
			}
		} catch (Exception e) {
			data.addResponse(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
		}
		return data;
	}
}
