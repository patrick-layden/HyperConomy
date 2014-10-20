package regalowl.hyperconomy.command;



import regalowl.hyperconomy.display.ItemDisplayFactory;

public class Removedisplay extends BaseCommand implements HyperCommand {


	public Removedisplay() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		if (!hc.getConf().getBoolean("enable-feature.item-displays")) {
			data.addResponse(L.get("ITEMDISPLAYS_MUST_BE_ENABLED"));
			return data;
		}
		ItemDisplayFactory itdi = hc.getItemDisplay();

		if (args.length == 0) {
			boolean success = itdi.removeDisplay(hp.getTargetLocation());
			if (success) {
				data.addResponse(L.get("DISPLAY_REMOVED"));
			} else {
				data.addResponse(L.get("NO_DISPLAY_DETECTED"));
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("u")) {
			double x = Math.floor(hp.getLocation().getX()) + .5;
			double z = Math.floor(hp.getLocation().getZ()) + .5;
			String w = hp.getLocation().getWorld();
			boolean success = itdi.removeDisplay(x, z, w);
			if (success) {
				data.addResponse(L.get("DISPLAY_REMOVED"));
			} else {
				data.addResponse(L.get("NO_DISPLAY_DETECTED_HERE"));
			}
		}
		return data;
	}
}
