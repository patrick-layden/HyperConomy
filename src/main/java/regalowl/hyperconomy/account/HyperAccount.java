package regalowl.hyperconomy.account;

public interface HyperAccount {
	
	public String getName();
	public double getBalance();
	public void deposit(double amount);
	public void withdraw(double amount);
	public void setName(String newName);
	public void setBalance(double balance);
	public boolean hasBalance(double balance);
	public void sendMessage(String message);

}
