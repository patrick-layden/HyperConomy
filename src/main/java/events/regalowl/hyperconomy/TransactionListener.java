package regalowl.hyperconomy;

public interface TransactionListener extends HyperListener {
	public void onTransaction(PlayerTransaction transaction, TransactionResponse response);
}
