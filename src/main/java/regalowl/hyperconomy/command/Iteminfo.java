package regalowl.hyperconomy.command;

import java.io.File;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class Iteminfo extends BaseCommand implements HyperCommand{

	public Iteminfo(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = hp.getHyperEconomy();
		HItemStack stack = null;
		TradeObject ho = null;
		if (args.length == 0) {
			stack = hp.getItemInHand();
			ho = he.getTradeObject(stack);
		} else {
			if (args[0].equalsIgnoreCase("pd")) {//prints HC serialized version of ItemStack in hand
				stack = hp.getItemInHand();
				data.addResponse(stack.serialize());
				return data;
			} else if (args[0].equalsIgnoreCase("pdf")) {//prints HC serialized version of ItemStack in hand to file named serialized_item.txt
					stack = hp.getItemInHand();
					String filePath = hc.getSimpleDataLib().getStoragePath() + File.separator + "serialized_item.txt";
					hc.getSimpleDataLib().getFileTools().writeStringToFile(stack.serialize(), filePath);
					data.addResponse("Item saved to file.");
					return data;
			} else if (args[0].equalsIgnoreCase("pdr")) {//displays info
					HItemStack newStack = new HItemStack(hp.getItemInHand().serialize());
					data.addResponses(newStack.displayInfo(hp, "&9", "&b"));
					return data;
			} else if (args[0].equalsIgnoreCase("comp")) {//compares specified HyperObject ItemStack with held item and displays data
				if (args.length == 2) {
					TradeObject to = hc.getDataManager().getDefaultEconomy().getTradeObject(args[1]);
					stack = hp.getItemInHand();
					HItemStack sis2 = to.getItem();
					if (stack.equals(sis2)) {
						data.addResponse("Objects equal.");
						return data;
					} else {
						data.addResponse("Objects not equal.");
						data.addResponse("Held: "+stack.serialize());
						data.addResponse("TradeObject: "+sis2.serialize());
						return data;
					}
				}
			}
			ho = he.getTradeObject(args[0]);
			if (ho == null) {
				data.addResponse("&9" + "Object not found.");
				return data;
			}
			stack = ho.getItem();
		}
		data.addResponse(L.get("LINE_BREAK"));
		if (ho == null) {
			data.addResponse("&cItem not in database.");
		} else {
			data.addResponse("&9Identifier: " + "&b" + ho.getName());
			data.addResponse("&9HyperConomy Name: " + "&b" + ho.getDisplayName());
			data.addResponse("&9Aliases: " + "&b" + ho.getAliasesString());
		}
		data.addResponses(stack.displayInfo(hp, "&9", "&b"));
		data.addResponse("&9NBT: " + "&b" + stack.getNBTTags().toString());
		data.addResponse(L.get("LINE_BREAK"));
		return data;
	}
}
