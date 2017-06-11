package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.display.InfoSignHandler;

public class Settax extends BaseCommand implements HyperCommand {



	public Settax(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		InfoSignHandler isign = hc.getInfoSignHandler();
		double taxrate = 0.0;
		try {
			if (args.length != 2) {
				data.addResponse(L.get("SETTAX_INVALID"));
				return data;
			} else {
				String type = args[0];
				if (type.equalsIgnoreCase("purchase")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getConf().set("tax.purchase", taxrate);
					data.addResponse(L.get("PURCHASE_TAX_SET"));
				} else if (type.equalsIgnoreCase("sales")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getConf().set("tax.sales", taxrate);
					data.addResponse(L.get("SALES_TAX_SET"));
				} else if (type.equalsIgnoreCase("static")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getConf().set("tax.static", taxrate);
					data.addResponse(L.get("STATIC_TAX_SET"));
				} else if (type.equalsIgnoreCase("initial")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getConf().set("tax.initial", taxrate);
					data.addResponse(L.get("INITIAL_TAX_SET"));
				} else if (type.equalsIgnoreCase("enchant")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getConf().set("tax.enchant", taxrate);
					data.addResponse(L.get("ENCHANT_TAX_SET"));
				} else {
					data.addResponse(L.get("SETTAX_INVALID"));
					return data;
				}
				isign.updateSigns();
			}
		} catch (Exception e) {
			data.addResponse(L.get("SETTAX_INVALID"));
		}
		return data;
	}
	
	
}
