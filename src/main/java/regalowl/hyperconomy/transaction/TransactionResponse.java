package regalowl.hyperconomy.transaction;

import java.util.ArrayList;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.util.LanguageFile;

public class TransactionResponse {

	private transient HyperConomy hc;
	private boolean success;
	private HyperPlayer hp;
	
	private ArrayList<String> messages = new ArrayList<String>();
	private ArrayList<Double> prices = new ArrayList<Double>();
	private ArrayList<TradeObject> failedObjects = new ArrayList<TradeObject>();
	private ArrayList<TradeObject> successfulObjects = new ArrayList<TradeObject>();
	
	public TransactionResponse(HyperConomy hc, HyperPlayer hp) {
		this.hc = hc;
		this.success = false;
		this.hp = hp;
	}
	
	public void addFailed(String message, TradeObject ho) {
		messages.add(message);
		failedObjects.add(ho);
	}
	
	public void addSuccess(String message, Double money, TradeObject ho) {
		messages.add(message);
		this.prices.add(money);
		successfulObjects.add(ho);
	}
	
	public void setSuccessful() {
		this.success = true;
	}
	
	
	public void sendMessages() {
		LanguageFile L = hc.getLanguageFile();
		if (success) {
			hp.sendMessage(L.get("LINE_BREAK"));
		}
		for (String message:messages) {
			hp.sendMessage(message);
		}
		if (success) {
			hp.sendMessage(L.get("LINE_BREAK"));
		}
	}
	
	public String getMessage() {
		if (messages.size() > 0) return messages.get(0);
		return "";
	}
	
	public double getPrice() {
		if (prices.size() > 0) return prices.get(0);
		return 0.0;
	}
	
	public ArrayList<String> getMessages() {
		return messages;
	}
	
	public ArrayList<Double> getPrices() {
		return prices;
	}
	
	public double getTotalPrice() {
		double total = 0.0;
		for (double p:prices) {
			total += p;
		}
		return CommonFunctions.twoDecimals(total);
	}
	
	public ArrayList<TradeObject> getFailedObjects() {
		return failedObjects;
	}

	
	public ArrayList<TradeObject> getSuccessfulObjects() {
		return successfulObjects;
	}
	
	public boolean successful() {
		return success;
	}
	
	public void setFailed() {
		success = false;
	}
	
}
