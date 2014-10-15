package regalowl.hyperconomy.command;



import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;




public class Hcdelete extends BaseCommand implements HyperCommand {
	
	public Hcdelete() {
		super(false);
	}


	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = getEconomy();
		if (args.length == 0) {
			data.addResponse(L.get("HCDELETE_INVALID"));
			return data;
		}
		if (args[0].equalsIgnoreCase("object")) {
			try {
				String name = args[1];
				if (he.objectTest(name)) {
					he.getHyperObject(name).delete();
					data.addResponse(L.get("HCDELETE_SUCCESS"));
				} else {
					data.addResponse(L.get("INVALID_NAME"));
				}
			} catch (Exception e) {
				data.addResponse(L.get("HCDELETE_OBJECT_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("account")) {
			try {
				String name = args[1];
				if (dm.hyperPlayerExists(name)) {
					HyperPlayer hp = dm.getHyperPlayer(name);
					hp.delete();
					data.addResponse(L.get("HCDELETE_SUCCESS"));
				} else {
					data.addResponse(L.get("ACCOUNT_NOT_FOUND"));
				}
			} catch (Exception e) {
				data.addResponse(L.get("HCDELETE_ACCOUNT_INVALID"));
			}
		} else {
			data.addResponse(L.get("HCDELETE_INVALID"));
		}
		return data;
	}
	

}
