package regalowl.hyperconomy.util;

import regalowl.hyperconomy.HyperConomy;

public class MessageBuilder {
	
	private transient LanguageFile L;
	
	private String message = "";
	private double amount = 0.0;
	private double price = 0.0;
	private String objectName = "";
	private String playerName = "";
	
	
	public MessageBuilder(HyperConomy hc, String message) {
		L = hc.getLanguageFile();
		this.message = L.get(message);
	}
	
	public String build() {
		String newMessage = message;
		newMessage = newMessage.replace("{amount}",amount+"");
		newMessage = newMessage.replace("{playerName}",playerName);
		newMessage = newMessage.replace("{objectName}",objectName);
		newMessage = newMessage.replace("{price}",price+"");
		newMessage = newMessage.replace("{currencySymbol}",L.get("CURRENCY"));
		return newMessage;
	}

	public void setMessage(String message) {
		if (message == null) return;
		this.message = message;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setObjectName(String objectName) {
		if (objectName == null) return;
		this.objectName = objectName;
	}

	public void setPlayerName(String playerName) {
		if (playerName == null) return;
		this.playerName = playerName;
	}
	
}
