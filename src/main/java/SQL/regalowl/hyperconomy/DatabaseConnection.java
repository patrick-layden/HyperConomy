package regalowl.hyperconomy;




abstract class DatabaseConnection {

	public abstract void write(String statement);
	public abstract QueryResult read(String statement);
	public abstract String closeConnection();
	
	protected abstract void openConnection();
	
	protected abstract void writeThread();
	protected abstract void scheduleRetry(long wait);
	
	public abstract boolean inUse();
	
}
