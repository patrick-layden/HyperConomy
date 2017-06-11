package regalowl.hyperconomy.event.minecraft;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;

public class InventoryClickEvent extends HyperEvent {

	private HyperPlayer clicker;
	private int clickedSlot;
	private HItemStack clickedItem;
	private boolean isShiftClick;
	private boolean isLeftClick;
	private boolean isRightClick;
	
	public InventoryClickEvent(HyperPlayer clicker, HInventory inventory, int clickedSlot, HItemStack clickedItem) {
		this.clicker = clicker;
		this.clickedSlot = clickedSlot;
		this.clickedItem = clickedItem;
	}

	public HyperPlayer getClicker() {
		return clicker;
	}
	public int getClickedSlot() {
		return clickedSlot;
	}
	public HItemStack getClickedItem() {
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
