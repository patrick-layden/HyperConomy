package regalowl.hyperconomy.util;

import regalowl.hyperconomy.HC;

public class MessageBuilder {
	
	LanguageFile L;
	private String message;
	
	private int amount;
	private double price;
	private String objectName;
	private String playerName;
	
	
	public MessageBuilder(String message) {
		L = HC.hc.getLanguageFile();
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
		this.message = message;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
}
