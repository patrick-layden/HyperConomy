package regalowl.hyperconomy;

public interface TransactionListener {
	public void onTransaction(PlayerTransaction transaction, TransactionResponse response);
}
