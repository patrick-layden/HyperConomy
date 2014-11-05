package regalowl.hyperconomy.command;



import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.shop.ChestShop;

public class Setchestowner extends BaseCommand implements HyperCommand {


	public Setchestowner() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length != 1) {
				data.addResponse(L.get("SETCHESTOWNER_INVALID"));
				return data;
			}
			String name = args[0];
			String line3 = "";
			String line4 = "";
			if (name.length() > 12) {
				line3 = name.substring(0, 12);
				line4 = name.substring(12, name.length());
			} else {
				line3 = name;
				line4 = "";
			}
			HyperPlayer hp = data.getHyperPlayer();
			HLocation l = hp.getTargetLocation();
			if (HC.mc.isChestShopChest(l)) {
				ChestShop cs = new ChestShop(l);
				HSign s = cs.getSign();
				s.setLine(2, "&f" + line3);
				s.setLine(3, "&f" + line4);
				s.update();
				data.addResponse(L.get("CHEST_OWNER_UPDATED"));
			} else if (HC.mc.isChestShopSign(l)) {
				HSign s = HC.mc.getSign(l);
				s.setLine(2, "&f" + line3);
				s.setLine(3, "&f" + line4);
				s.update();
				data.addResponse(L.get("CHEST_OWNER_UPDATED"));

			} else {
				data.addResponse(L.get("LOOK_AT_VALID_CHESTSHOP"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("SETCHESTOWNER_INVALID"));
		}
		return data;
	}
}
