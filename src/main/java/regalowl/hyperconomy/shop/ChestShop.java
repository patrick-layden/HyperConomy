package regalowl.hyperconomy.shop;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.util.HSign;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.SimpleLocation;

public class ChestShop {

	private SimpleLocation location;
	private HSign sign;
	private HyperAccount owner;
	private SimpleLocation signLocation;
	private ChestShopType type;
	private SerializableInventory inventory;
	private boolean hasStaticPrice;
	private double staticPrice;

	
	public ChestShop(SimpleLocation location) {
		this.location = location;
		this.sign = HyperConomy.mc.getSign(getSignLocation());
		if (sign == null) return;
		String chestOwnerName = HyperConomy.mc.removeColor(sign.getLine(2)).trim() + HyperConomy.mc.removeColor(sign.getLine(3)).trim();
		this.owner = HyperConomy.hc.getHyperPlayerManager().getAccount(chestOwnerName);
		signLocation = new SimpleLocation(location);
		signLocation.setY(location.getY() + 1);
		type = ChestShopType.fromString(HyperConomy.mc.removeColor(sign.getLine(1)).trim());
		inventory = HyperConomy.mc.getChestInventory(location);
		LanguageFile L = HyperConomy.hc.getLanguageFile();
		CommonFunctions cf = HyperConomy.hc.getCommonFunctions();
		String line1 = HyperConomy.mc.removeColor(sign.getLine(0)).trim();
		if (line1.startsWith(L.gC(false))) {
			try {
				String price = line1.substring(1, line1.length());
				staticPrice = cf.twoDecimals(Double.parseDouble(price));
				hasStaticPrice = true;
			} catch (Exception e) {
				hasStaticPrice = false;
			}
		} else if (line1.endsWith(L.gC(false))) {
			try {
				String price = line1.substring(0, line1.length() - 1);
				staticPrice = cf.twoDecimals(Double.parseDouble(price));
				hasStaticPrice = true;
			} catch (Exception e) {
				hasStaticPrice = false;
			}
		}
	}
	
	public SimpleLocation getChestLocation() {
		return location;
	}
	public HSign getSign() {
		return sign;
	}
	public HyperAccount getOwner() {
		return owner;
	}
	public SimpleLocation getSignLocation() {
		return signLocation;
	}
	public SerializableInventory getInventory() {
		return inventory;
	}
	public ChestShopType getType() {
		return type;
	}
	public SimpleLocation[] getProtectedLocations() {
		//TODO
		return null;
	}
	public boolean hasStaticPrice() {
		return hasStaticPrice;
	}
	public double getStaticPrice() {
		return staticPrice;
	}
	
}
