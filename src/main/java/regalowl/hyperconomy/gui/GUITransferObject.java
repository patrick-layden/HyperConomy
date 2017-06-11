package regalowl.hyperconomy.gui;

import java.io.Serializable;
import java.util.ArrayList;

import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class GUITransferObject implements Serializable {

	private static final long serialVersionUID = 8005677661537696729L;
	
	private GUITransferType type;
	private String authKey;
	public GUITransferObject(GUITransferType type, String authKey) {
		this.type = type;
		this.authKey = authKey;
	}
	public GUITransferType getType() {
		return type;
	}
	public String getAuthKey() {
		return authKey;
	}
	
	
	public synchronized void clear() {
		deletedTradeObjects.clear();
		hyperEconomies.clear();
		deletedEconomies.clear();
		hyperPlayers.clear();
		hyperObjects.clear();
		banks.clear();
		shops.clear();
	}
	public boolean isEmpty() {
		if (!deletedTradeObjects.isEmpty()) return false;
		if (!hyperEconomies.isEmpty()) return false;
		if (!deletedEconomies.isEmpty()) return false;
		if (!hyperPlayers.isEmpty()) return false;
		if (!hyperObjects.isEmpty()) return false;
		if (!shops.isEmpty()) return false;
		if (!banks.isEmpty()) return false;
		return true;
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
	
	
	
	private ArrayList<HyperEconomy> hyperEconomies = new ArrayList<HyperEconomy>();
	public synchronized void addEconomy(HyperEconomy he) {
		hyperEconomies.add(he);
	}
	public ArrayList<HyperEconomy> getHyperEconomies() {
		return hyperEconomies;
	}
	
	
	
	private ArrayList<String> deletedEconomies = new ArrayList<String>();
	public synchronized void addDeletedEconomy(String he) {
		deletedEconomies.add(he);
	}
	public ArrayList<String> getDeletedEconomies() {
		return deletedEconomies;
	}
	
	
	
	private ArrayList<TradeObject> deletedTradeObjects = new ArrayList<TradeObject>();
	public synchronized void addDeletedTradeObject(TradeObject to) {
		deletedTradeObjects.add(to);
	}
	public ArrayList<TradeObject> getDeletedTradeObjects() {
		return deletedTradeObjects;
	}


}
