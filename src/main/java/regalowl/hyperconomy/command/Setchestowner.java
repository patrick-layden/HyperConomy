package regalowl.hyperconomy.command;



import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.ChestShop;

public class Setchestowner extends BaseCommand implements HyperCommand {


	public Setchestowner(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		if (args.length != 1) {
			data.addResponse(L.get("SETCHESTOWNER_INVALID"));
			return data;
		}
		String name = args[0];
		if (!hc.getDataManager().accountExists(name)) {
			data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
			return data;
		}
		HyperAccount account = hc.getDataManager().getAccount(name);
		HLocation l = hp.getTargetLocation();
		ChestShop cs = hc.getChestShop().getChestShop(l);
		if (cs == null) {
			data.addResponse(L.get("LOOK_AT_VALID_CHESTSHOP"));
			return data;
		}	
		cs.setOwner(account);
		data.addResponse(L.get("CHEST_OWNER_UPDATED"));
		return data;
	}
}
