package regalowl.hyperconomy.account;

import java.io.Serializable;

/**
 * This interface defines all necessary functions of a {@link HyperAccount}
 * 
 * @author Pingger
 * @author RegalOwl
 */
public interface HyperAccount extends Serializable {
	/**
	 * Deposits the given <b>amount</b> into the {@link HyperAccount}
	 * 
	 * @param amount the amount to deposit
	 */
	public void deposit(double amount);

	/**
	 * @return the Balance of the {@link HyperAccount}
	 */
	public double getBalance();

	/**
	 * @return the Name of the {@link HyperAccount}
	 */
	public String getName();

	/**
	 * Sends a message to this {@link HyperAccount}. Actual effect is implementation
	 * dependent
	 * 
	 * @param message
	 */
	public void sendMessage(String message);

	/**
	 * Sets this {@link HyperAccount}'s Balance to the given value
	 * 
	 * @param balance the balance to set
	 */
	public void setBalance(double balance);

	/**
	 * Changes the Name of this {@link HyperAccount}
	 * 
	 * @param newName the new Name to assign
	 */
	public void setName(String newName);

	/**
	 * Withdraws the <b>amount</b> from the {@link HyperAccount}
	 * 
	 * @param amount the amount to withdraw
	 */
	public void withdraw(double amount);

	/**
	 * Checks if this {@link HyperAccount} has the given Balance or more.
	 * 
	 * @param balance the balance to at least have
	 * @return true, if {@link HyperAccount#getBalance()}&gt;=<b>balance</b>
	 */
	default boolean hasBalance(double balance) {
		return getBalance() >= balance;
	}

	/**
	 * Updates this {@link HyperAccount}'s Balance. Positive Value deposits,
	 * negative Value withdraws. Zero/NaN does nothing.
	 * 
	 * @param amount the amount to deposit/withdraw from this {@link HyperAccount}
	 */
	default void updateBalance(double amount) {
		if (amount > 0) {
			deposit(amount);
		} else if (amount < 0) {
			withdraw(amount);
		}
		// on 0 do nothing, because no change
	}
}
