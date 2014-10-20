package regalowl.hyperconomy.command;



import regalowl.hyperconomy.util.HyperLock;

public class Lockshop extends BaseCommand implements HyperCommand {

	public Lockshop() {
		super(false);
	}


	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperLock hl = hc.getHyperLock();
		try {
			if (hl.fullLock()) {
				return data;
			}
			if (args.length == 0) {
				if (hl.playerLock()) {
					hl.setPlayerLock(false);
					data.addResponse(L.get("SHOP_UNLOCKED"));
					return data;
				} else if (!hl.playerLock()) {
					hl.setPlayerLock(true);
					data.addResponse(L.get("SHOP_LOCKED"));
					return data;
				} else {
					data.addResponse(L.get("FIX_YML_FILE"));
					return data;
				}
			} else {
				data.addResponse(L.get("LOCKSHOP_INVALID"));
				return data;
			}
		} catch (Exception e) {
			data.addResponse(L.get("LOCKSHOP_INVALID"));
			return data;
		}
	}
}
