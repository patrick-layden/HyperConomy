package regalowl.hyperconomy.event;

import regalowl.hyperconomy.transaction.PlayerTransaction;

import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.simpledatalib.event.Event;

public class TransactionEvent extends Event {
	private PlayerTransaction transaction;
	private TransactionResponse response;
	
	public TransactionEvent(PlayerTransaction transaction, TransactionResponse response) {
		this.transaction = transaction;
		this.response = response;
	}

	public PlayerTransaction getTransaction() {
		return transaction;
	}
	
	public TransactionResponse getTransactionResponse() {
		return response;
	}
}
