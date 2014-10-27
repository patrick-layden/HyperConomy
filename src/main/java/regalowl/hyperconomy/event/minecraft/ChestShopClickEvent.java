package regalowl.hyperconomy.event.minecraft;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.shop.ChestShop;

public class ChestShopClickEvent extends Event {

	private HyperPlayer clicker;
	private ChestShop chestShop;
	private int clickedSlot;
	private SerializableItemStack clickedItem;
	private boolean isShiftClick;
	private boolean isLeftClick;
	private boolean isRightClick;
	
	public ChestShopClickEvent(HyperPlayer clicker, ChestShop chestShop, int clickedSlot, SerializableItemStack clickedItem) {
		this.clicker = clicker;
		this.chestShop = chestShop;
		this.clickedSlot = clickedSlot;
		this.clickedItem = clickedItem;
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
	public SerializableItemStack getClickedItem() {
		return clickedItem;
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
	
}
