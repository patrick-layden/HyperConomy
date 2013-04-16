package regalowl.hyperconomy;




public interface DatabaseConnection {

	public void write(String statement);
	public QueryResult read(String statement);
	public String closeConnection();
	public boolean inUse();
	
}
