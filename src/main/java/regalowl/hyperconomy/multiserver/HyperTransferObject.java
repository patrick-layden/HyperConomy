package regalowl.hyperconomy.multiserver;

import java.io.Serializable;
import java.util.ArrayList;

import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class HyperTransferObject implements Serializable {

	private static final long serialVersionUID = 8005677661537696729L;
	
	public synchronized void clear() {
		hyperPlayers.clear();
		hyperObjects.clear();
		banks.clear();
		shops.clear();
	}
	public boolean isEmpty() {
		if (!hyperPlayers.isEmpty()) return false;
		if (!hyperObjects.isEmpty()) return false;
		if (!shops.isEmpty()) return false;
		if (!banks.isEmpty()) return false;
		return true;
	}
	
	private HyperTransferType type;
	public HyperTransferObject(HyperTransferType type) {
		this.type = type;
	}
	public HyperTransferType getType() {
		return type;
	}
	
	private ArrayList<TradeObject> hyperObjects = new ArrayList<TradeObject>();
	public synchronized void addHyperObject(TradeObject ho) {
		if (!hyperObjects.contains(ho)) {
			hyperObjects.add(ho);
		}
	}
	public ArrayList<TradeObject> getHyperObjects() {
		return hyperObjects;
	}
	
	
	private ArrayList<HyperPlayer> hyperPlayers = new ArrayList<HyperPlayer>();
	public synchronized void addHyperPlayer(HyperPlayer hp) {
		if (!hyperPlayers.contains(hp)) {
			hyperPlayers.add(hp);
		}
	}
	public ArrayList<HyperPlayer> getHyperPlayers() {
		return hyperPlayers;
	}
	
	private ArrayList<Shop> shops = new ArrayList<Shop>();
	public synchronized void addShop(Shop s) {
		if (!shops.contains(s)) {
			shops.add(s);
		}
	}
	public ArrayList<Shop> getShops() {
		return shops;
	}
	
	private ArrayList<HyperBank> banks = new ArrayList<HyperBank>();
	public synchronized void addBank(HyperBank hb) {
		if (!banks.contains(hb)) {
			banks.add(hb);
		}
	}
	public ArrayList<HyperBank> getBanks() {
		return banks;
	}
	


}
