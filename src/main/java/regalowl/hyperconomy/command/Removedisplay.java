package regalowl.hyperconomy.command;



import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.display.ItemDisplayHandler;
import regalowl.hyperconomy.minecraft.HLocation;

public class Removedisplay extends BaseCommand implements HyperCommand {


	public Removedisplay(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		if (!hc.getConf().getBoolean("enable-feature.item-displays")) {
			data.addResponse(L.get("ITEMDISPLAYS_MUST_BE_ENABLED"));
			return data;
		}
		ItemDisplayHandler itdi = hc.getItemDisplay();

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
			HLocation l = new HLocation(w, x, 0.0, z);
			l.convertToBlockLocation();
			boolean success = itdi.removeDisplayInColumn(l);
			if (success) {
				data.addResponse(L.get("DISPLAY_REMOVED"));
			} else {
				data.addResponse(L.get("NO_DISPLAY_DETECTED_HERE"));
			}
		}
		return data;
	}
}
