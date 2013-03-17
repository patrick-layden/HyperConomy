package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class TransactionResponse {

	private boolean success;
	private Player player;
	
	private ArrayList<String> messages = new ArrayList<String>();
	private ArrayList<Double> prices = new ArrayList<Double>();
	private ArrayList<HyperObject> failedObjects = new ArrayList<HyperObject>();
	private ArrayList<HyperObject> successfulObjects = new ArrayList<HyperObject>();
	
	TransactionResponse(Player player) {
		this.success = false;
		this.player = player;
	}
	
	public void addFailed(String message, HyperObject ho) {
		messages.add(message);
		failedObjects.add(ho);
	}
	
	public void addSuccess(String message, Double money, HyperObject ho) {
		messages.add(message);
		this.prices.add(money);
		successfulObjects.add(ho);
	}
	
	public void setSuccessful() {
		this.success = true;
	}
	
	
	public void sendMessages() {
		LanguageFile L = HyperConomy.hc.getLanguageFile();
		if (success) {
			player.sendMessage(L.get("LINE_BREAK"));
		}
		for (String message:messages) {
			player.sendMessage(message);
		}
		if (success) {
			player.sendMessage(L.get("LINE_BREAK"));
		}
	}
	
	public String getMessage() {
		return messages.get(0);
	}
	
	public Double getPrice() {
		return prices.get(0);
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
		return total;
	}
	
	public ArrayList<HyperObject> getFailedObjects() {
		return failedObjects;
	}
	
	public ArrayList<HyperObject> getSuccessfulObjects() {
		return successfulObjects;
	}
	
	public boolean successful() {
		return success;
	}
	
}
