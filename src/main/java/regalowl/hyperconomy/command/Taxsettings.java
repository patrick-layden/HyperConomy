package regalowl.hyperconomy.command;

import regalowl.databukkit.file.FileConfiguration;


public class Taxsettings extends BaseCommand implements HyperCommand {


	public Taxsettings() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			FileConfiguration conf = hc.getConf();
			Double purchasetaxpercent = conf.getDouble("tax.purchase");
			Double initialpurchasetaxpercent = conf.getDouble("tax.initial");
			Double statictaxpercent = conf.getDouble("tax.static");
			Double enchanttaxpercent = conf.getDouble("tax.enchant");
			Double salestaxpercent = conf.getDouble("tax.sales");
			data.addResponse(L.get("LINE_BREAK"));
			data.addResponse(L.f(L.get("PURCHASE_TAX_PERCENT"), purchasetaxpercent));
			data.addResponse(L.f(L.get("INITIAL_TAX_PERCENT"), initialpurchasetaxpercent));
			data.addResponse(L.f(L.get("STATIC_TAX_PERCENT"), statictaxpercent));
			data.addResponse(L.f(L.get("ENCHANTMENT_TAX_PERCENT"), enchanttaxpercent));
			data.addResponse(L.f(L.get("SALES_TAX_PERCENT"), salestaxpercent));
			data.addResponse(L.get("LINE_BREAK"));
		} catch (Exception e) {
			data.addResponse(L.get("TAXSETTINGS_INVALID"));
		}
		return data;
	}
}
