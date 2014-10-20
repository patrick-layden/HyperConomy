package regalowl.hyperconomy.command;



public class Seteconomy extends BaseCommand implements HyperCommand {

	public Seteconomy() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 1) {
				String economy = args[0];
				if (dm.economyExists(economy)) {
					if (hp != null) {
						hp.setEconomy(economy);
						data.addResponse(L.get("ECONOMY_SET"));
					} else {
						cs.setConsoleEconomy(economy);
						hc.getConsoleSettings().setConsoleEconomy(economy);
						data.addResponse(L.get("ECONOMY_SET"));
					}
				} else {
					data.addResponse(L.get("ECONOMY_NOT_EXIST"));
				}

			} else {
				data.addResponse(L.get("SETECONOMY_INVALID"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("SETECONOMY_INVALID"));
		}
		return data;
	}
}
