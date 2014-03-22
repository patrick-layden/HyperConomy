package regalowl.hyperconomy.event;

import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;

public interface TransactionListener {
	public void onTransaction(PlayerTransaction transaction, TransactionResponse response);
}
