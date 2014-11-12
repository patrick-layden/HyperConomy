package regalowl.hyperconomy.shop;


import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.util.LanguageFile;

public class ChestShop {

	private HLocation location;
	private HSign sign;
	private HyperAccount owner;
	private HLocation signLocation;
	private HBlock attachedBlock;
	private ChestShopType type;
	private HInventory inventory;
	private boolean hasStaticPrice;
	private double staticPrice;
	private boolean isValidChestShop;

	
	public ChestShop(HBlock b) {
		this.location = b.getLocation();
		initialize();
	}
	
	public ChestShop(HLocation location) {
		this.location = location;
		initialize();
	}
	
	private void initialize() {
		isValidChestShop = false;
		if (location == null) return;
		if (!HC.mc.isChestShopChest(location)) return;
		signLocation = new HLocation(location);
		signLocation.setY(location.getY() + 1);
		this.sign = HC.mc.getSign(signLocation);
		if (sign == null) return;
		attachedBlock = sign.getAttachedBlock();
		if (attachedBlock == null) return;
		type = ChestShopType.fromString(HC.mc.removeColor(sign.getLine(1)).trim());
		if (type == null) return;
		inventory = HC.mc.getChestInventory(location);
		if (inventory == null) return;
		LanguageFile L = HC.hc.getLanguageFile();
		String line1 = HC.mc.removeColor(sign.getLine(0)).trim();
		hasStaticPrice = false;
		if (line1.startsWith(L.gC(false))) {
			try {
				String price = line1.substring(1, line1.length());
				staticPrice = CommonFunctions.twoDecimals(Double.parseDouble(price));
				hasStaticPrice = true;
			} catch (Exception e) {}
		} else if (line1.endsWith(L.gC(false))) {
			try {
				String price = line1.substring(0, line1.length() - 1);
				staticPrice = CommonFunctions.twoDecimals(Double.parseDouble(price));
				hasStaticPrice = true;
			} catch (Exception e) {}
		}
		String chestOwnerName = HC.mc.removeColor(sign.getLine(2)).trim() + HC.mc.removeColor(sign.getLine(3)).trim();
		this.owner = HC.hc.getHyperPlayerManager().getAccount(chestOwnerName);
		if (owner == null && !chestOwnerName.equals("")) {
			this.owner = HC.hc.getHyperPlayerManager().getHyperPlayer(chestOwnerName);
		} else {
			return;
		}
		isValidChestShop = true;
	}
	
	public HLocation getChestLocation() {
		return location;
	}
	public HSign getSign() {
		return sign;
	}
	public HyperAccount getOwner() {
		return owner;
	}
	public HLocation getSignLocation() {
		return signLocation;
	}
	public HInventory getInventory() {
		return inventory;
	}
	public ChestShopType getType() {
		return type;
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
		return HC.mc.canHoldChestShopSign(attachedBlock.getLocation());
	}
	public boolean isDoubleChest() {
		if (!HC.mc.isChest(location)) return false;
		HBlock cBlock = new HBlock(location);
		HBlock[] surrounding = cBlock.getNorthSouthEastWestBlocks();
		for (HBlock b:surrounding) {
			if (HC.mc.isChest(b.getLocation())) return true;
		}
		return false;
	}
	public boolean isBuyChest() {
		if (type == ChestShopType.TRADE || type == ChestShopType.BUY) return true;
		return false;
	}
	public boolean isSellChest() {
		if (type == ChestShopType.TRADE || type == ChestShopType.SELL) return true;
		return false;
	}
	
}
