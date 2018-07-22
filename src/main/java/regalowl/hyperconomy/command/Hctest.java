package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HyperConomy;

public class Hctest extends BaseCommand implements HyperCommand {
	
	public Hctest(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		/*
		for (TradeObject to:hc.getDataManager().getTradeObjects()) {
			to.setCeiling(123456);
			to.setMaxStock(123456);
			to.setMedian(123456);
			to.setStatic(true);
			to.setMedian(123456);
			to.setStock(123456);
			to.setStaticPrice(123456);
			to.setFloor(123456);
			to.setValue(123456);
			to.setCeiling(123456);
			to.setMaxStock(123456);
			to.setMedian(123456);
			to.setStatic(true);
			to.setMedian(123456);
			to.setStock(123456);
			to.setStaticPrice(123456);
			to.setFloor(123456);
			to.setValue(123456);
		}
		*/
		return data;
	}

}
