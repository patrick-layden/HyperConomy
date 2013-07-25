package regalowl.hyperconomy;

import java.util.List;




public interface DatabaseConnection {

	public void write(List<String> statements);
	public void syncWrite(List<String> statements);
	public QueryResult read(String statement);
	public List<String> closeConnection();
	public boolean inUse();
	
}
