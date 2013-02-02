package regalowl.hyperconomy;


abstract class DatabaseConnection {

	public abstract void write(String statement);
	public abstract String closeConnection();
	
	protected abstract void openConnection();
	protected abstract void retryConnection(long wait);
	protected abstract void refreshConnection();
	
	protected abstract void writeThread();
	protected abstract void scheduleRetry(long wait);
	protected abstract void returnConnection();
	
}
