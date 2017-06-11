package regalowl.hyperconomy.event.minecraft;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.ChestShop;

public class ChestShopClickEvent extends HyperEvent {

	private HyperPlayer clicker;
	private ChestShop chestShop;
	private int clickedSlot;
	private String action;
	private boolean isShiftClick;
	private boolean isLeftClick;
	private boolean isRightClick;
	private int invQuantity;
	private HItemStack invItem;
	private int cursorQuantity;
	private HItemStack cursorItem;
	
	public ChestShopClickEvent(HyperPlayer clicker, ChestShop chestShop, int clickedSlot, String action, int invQuantity, HItemStack invItem, int cursorQuantity, HItemStack cursorItem) {
		this.clicker = clicker;
		this.chestShop = chestShop;
		this.clickedSlot = clickedSlot;
		this.action = action;
		this.invQuantity = invQuantity;
		this.invItem = invItem;
		this.cursorQuantity = invQuantity;
		this.cursorItem = cursorItem;
	}

	public HyperPlayer getClicker() {
		return clicker;
	}
	public ChestShop getChestShop() {
		return chestShop;
	}
	public int getClickedSlot() {
		return clickedSlot;
	}
 	public boolean isShiftClick() {
		return isShiftClick;
	}
	public boolean isLeftClick() {
		return isLeftClick;
	}
	public boolean isRightClick() {
		return isRightClick;
	}

	public void setShiftClick() {
		this.isShiftClick = true;
	}

	public void setLeftClick() {
		this.isLeftClick = true;
	}

	public void setRightClick() {
		this.isRightClick = true;
	}
	
	public String getAction() {
		return action;
	}
	
	public int getInvQuantity() {
		return invQuantity;
	}
	
	public HItemStack getInvItem() {
		return invItem;
	}
	
	public int getCursorQuantity() {
		return cursorQuantity;
	}
	
	public HItemStack getCursorItem() {
		return cursorItem;
	}

	
}
