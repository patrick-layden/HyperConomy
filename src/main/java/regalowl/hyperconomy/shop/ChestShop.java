package regalowl.hyperconomy.shop;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.util.HBlock;
import regalowl.hyperconomy.util.HSign;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.SimpleLocation;

public class ChestShop {

	private SimpleLocation location;
	private HSign sign;
	private HyperAccount owner;
	private SimpleLocation signLocation;
	private HBlock attachedBlock;
	private ChestShopType type;
	private SerializableInventory inventory;
	private boolean hasStaticPrice;
	private double staticPrice;
	private boolean isValidChestShop;

	
	public ChestShop(SimpleLocation location) {
		isValidChestShop = false;
		this.location = location;
		if (location == null) return;
		signLocation = new SimpleLocation(location);
		signLocation.setY(location.getY() + 1);
		this.sign = HyperConomy.mc.getSign(signLocation);
		if (sign == null) return;
		attachedBlock = sign.getAttachedBlock();
		if (attachedBlock == null) return;
		String chestOwnerName = HyperConomy.mc.removeColor(sign.getLine(2)).trim() + HyperConomy.mc.removeColor(sign.getLine(3)).trim();
		this.owner = HyperConomy.hc.getHyperPlayerManager().getAccount(chestOwnerName);
		if (owner == null) return;
		type = ChestShopType.fromString(HyperConomy.mc.removeColor(sign.getLine(1)).trim());
		if (type == null) return;
		inventory = HyperConomy.mc.getChestInventory(location);
		if (inventory == null) return;
		LanguageFile L = HyperConomy.hc.getLanguageFile();
		CommonFunctions cf = HyperConomy.hc.getCommonFunctions();
		String line1 = HyperConomy.mc.removeColor(sign.getLine(0)).trim();
		hasStaticPrice = false;
		if (line1.startsWith(L.gC(false))) {
			try {
				String price = line1.substring(1, line1.length());
				staticPrice = cf.twoDecimals(Double.parseDouble(price));
				hasStaticPrice = true;
			} catch (Exception e) {}
		} else if (line1.endsWith(L.gC(false))) {
			try {
				String price = line1.substring(0, line1.length() - 1);
				staticPrice = cf.twoDecimals(Double.parseDouble(price));
				hasStaticPrice = true;
			} catch (Exception e) {}
		}
		isValidChestShop = true;
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
	public boolean isValid() {
		return isValidChestShop;
	}
	public boolean isSignAttachedToValidBlock() {
		return HyperConomy.mc.canHoldChestShopSign(attachedBlock.getLocation());
	}
	public boolean isDoubleChest() {
		if (!HyperConomy.mc.isChest(location)) return false;
		SimpleLocation l1 = new SimpleLocation(location);
		l1.setX(l1.getX() + 1);
		if (HyperConomy.mc.isChest(l1)) return true;
		SimpleLocation l2 = new SimpleLocation(location);
		l2.setX(l2.getX() - 1);
		if (HyperConomy.mc.isChest(l2)) return true;
		SimpleLocation l3 = new SimpleLocation(location);
		l3.setZ(l3.getZ() + 1);
		if (HyperConomy.mc.isChest(l3)) return true;
		SimpleLocation l4 = new SimpleLocation(location);
		l4.setZ(l4.getZ() - 1);
		if (HyperConomy.mc.isChest(l4)) return true;
		return false;
	}
	
}
