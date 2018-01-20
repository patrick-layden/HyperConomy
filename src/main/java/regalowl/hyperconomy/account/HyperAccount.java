package regalowl.hyperconomy.account;

import java.io.Serializable;

public interface HyperAccount extends Serializable {
	
	String getName();
	double getBalance();
	void deposit(double amount);
	void withdraw(double amount);
	void setName(String newName);
	void setBalance(double balance);
	boolean hasBalance(double balance);
	void sendMessage(String message);


}
