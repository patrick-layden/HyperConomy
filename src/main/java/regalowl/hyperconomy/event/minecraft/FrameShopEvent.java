package regalowl.hyperconomy.event.minecraft;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.bukkit.FrameShop;
import regalowl.hyperconomy.transaction.TransactionType;

public class FrameShopEvent extends Event {
	
	private FrameShop fs;
	private HyperPlayer hp;
	private TransactionType type;

	public FrameShopEvent(FrameShop fs, HyperPlayer hp, TransactionType type) {
		this.fs = fs;
		this.hp = hp;
		this.type = type;
	}

	public FrameShop getFs() {
		return fs;
	}

	public HyperPlayer getHp() {
		return hp;
	}

	public TransactionType getType() {
		return type;
	}
	
	
	
}
