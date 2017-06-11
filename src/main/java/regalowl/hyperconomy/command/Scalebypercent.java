package regalowl.hyperconomy.command;

import java.util.ArrayList;





import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.util.Backup;

public class Scalebypercent extends BaseCommand implements HyperCommand {


	public Scalebypercent(HyperConomy hc) {
		super(hc, false);
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
							new Backup(hc);
						}
						for (int c = 0; c < names.size(); c++) {
							String cname = names.get(c);
							TradeObject ho = he.getTradeObject(cname);
							if (!(ho.getType() == TradeObjectType.ITEM) && onlyItems) {continue;}
							if (!(ho.getType() == TradeObjectType.ENCHANTMENT) && onlyEnchants) {continue;}
							if (!ho.isCompositeObject()) {
								if (type.equalsIgnoreCase("value")) {
									ho.setValue(CommonFunctions.twoDecimals(ho.getValue() * percent));
								} else if (type.equalsIgnoreCase("staticprice")) {
									ho.setStaticPrice(CommonFunctions.twoDecimals(ho.getStaticPrice() * percent));
								} else if (type.equalsIgnoreCase("stock")) {
									ho.setStock(Math.floor(ho.getStock() * percent + .5));
								} else if (type.equalsIgnoreCase("median")) {
									ho.setMedian(CommonFunctions.twoDecimals(ho.getMedian() * percent));
								} else if (type.equalsIgnoreCase("startprice")) {
									ho.setStartPrice(CommonFunctions.twoDecimals(ho.getStartPrice() * percent));
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
