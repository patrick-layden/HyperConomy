package regalowl.hyperconomy.account;

import java.io.Serializable;

public interface HyperAccount extends Serializable {
	
	public String getName();
	public double getBalance();
	public void deposit(double amount);
	public void withdraw(double amount);
	public void setName(String newName);
	public void setBalance(double balance);
	public boolean hasBalance(double balance);
	public void sendMessage(String message);


}
