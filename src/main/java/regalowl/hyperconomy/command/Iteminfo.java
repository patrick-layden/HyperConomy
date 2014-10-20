package regalowl.hyperconomy.command;

import org.bukkit.ChatColor;

import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableItemStack;

public class Iteminfo extends BaseCommand implements HyperCommand{

	public Iteminfo() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = hp.getHyperEconomy();
		SerializableItemStack sis = null;
		HyperObject ho = null;
		if (args.length == 0) {
			ho = he.getHyperObject(hp.getItemInHand());
			sis = hp.getItemInHand();
		} else {
			ho = he.getHyperObject(args[0]);
			if (ho == null) {
				data.addResponse(ChatColor.BLUE + "Object not found.");
				return data;
			}
			sis = ho.getItem();
		}
		data.addResponse(L.get("LINE_BREAK"));
		if (ho == null) {
			data.addResponse(ChatColor.RED + "Item not in database.");
		} else {
			data.addResponse(ChatColor.BLUE + "Identifier: " + ChatColor.AQUA + "" + ho.getName());
			data.addResponse(ChatColor.BLUE + "HyperConomy Name: " + ChatColor.AQUA + "" + ho.getDisplayName());
			data.addResponse(ChatColor.BLUE + "Aliases: " + ChatColor.AQUA + "" + ho.getAliasesString());
		}
		sis.displayInfo(hp, "&9", "&b");
		data.addResponse(L.get("LINE_BREAK"));
		return data;
	}
}
