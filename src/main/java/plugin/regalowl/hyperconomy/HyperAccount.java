package regalowl.hyperconomy;

public interface HyperAccount {
	
	public String getName();
	public double getBalance();
	public void deposit(double amount);
	public void withdraw(double amount);
	public void setBalance(double balance);
	public boolean hasBalance(double balance);

}
