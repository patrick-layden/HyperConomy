package regalowl.hyperconomy.command;

import java.util.ArrayList;


import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.util.Backup;

public class Scalebypercent extends BaseCommand implements HyperCommand {


	public Scalebypercent() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			HyperEconomy he = super.getEconomy();
			ArrayList<String> names = he.getNames();
			boolean onlyItems = false;
			boolean onlyEnchants = false;
			if (args.length == 2 || args.length == 3) {
				if (args.length == 3) {
					if (args[2].contains("item")) {
						onlyItems = true;
					} else if (args[2].contains("enchantment")) {
						onlyEnchants = true;
					}
				} else {
					names = he.getNames();
				}
				String type = args[0];
				Double percent = Double.parseDouble(args[1]);
				percent = percent / 100;
				if (percent >= 0) {
					if (type.equalsIgnoreCase("value") || type.equalsIgnoreCase("staticprice") || type.equalsIgnoreCase("stock") || type.equalsIgnoreCase("median") || type.equalsIgnoreCase("startprice")) {
						if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {
							new Backup();
						}
						for (int c = 0; c < names.size(); c++) {
							String cname = names.get(c);
							HyperObject ho = he.getHyperObject(cname);
							if (!(ho.getType() == HyperObjectType.ITEM) && onlyItems) {continue;}
							if (!(ho.getType() == HyperObjectType.ENCHANTMENT) && onlyEnchants) {continue;}
							if (!ho.isCompositeObject()) {
								if (type.equalsIgnoreCase("value")) {
									ho.setValue(cf.twoDecimals(ho.getValue() * percent));
								} else if (type.equalsIgnoreCase("staticprice")) {
									ho.setStaticprice(cf.twoDecimals(ho.getStaticprice() * percent));
								} else if (type.equalsIgnoreCase("stock")) {
									ho.setStock(Math.floor(ho.getStock() * percent + .5));
								} else if (type.equalsIgnoreCase("median")) {
									ho.setMedian(cf.twoDecimals(ho.getMedian() * percent));
								} else if (type.equalsIgnoreCase("startprice")) {
									ho.setStartprice(cf.twoDecimals(ho.getStartprice() * percent));
								}
							}
						}
						data.addResponse(L.get("ADJUSTMENT_SUCCESSFUL"));
					} else {
						data.addResponse(L.get("SCALEBYPERCENT_TYPES"));
					}
				} else {
					data.addResponse(L.get("PERCENT_GREATER_THAN_0"));
				}
			} else {
				data.addResponse(L.get("SCALEBYPERCENT_INVALID"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("SCALEBYPERCENT_INVALID"));
		}
		return data;
	}
}
