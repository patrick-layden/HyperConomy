package regalowl.hyperconomy;



public class HyperPlayer {

	private HyperConomy hc;
	
	private String name;
	private String economy;
	private double balance;
	
	
	HyperPlayer() {
		hc = HyperConomy.hc;
	}
	
	HyperPlayer(String player) {
		hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		sw.writeData("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE)" + " VALUES ('" + player + "','" + "default" + "','" + 0.0 + "')");
	}
	
	public String getName() {
		return name;
	}
	public String getEconomy() {
		return economy;
	}
	public double getBalance() {
		return balance;
	}
	
	
	public void setName(String name) {
		String statement = "UPDATE hyperconomy_players SET PLAYER='" + name + "' WHERE PLAYER = '" + this.name + "'";
		hc.getSQLWrite().writeData(statement);
		this.name = name;
	}
	public void setEconomy(String economy) {
		String statement = "UPDATE hyperconomy_players SET ECONOMY='" + economy + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().writeData(statement);
		this.economy = economy;
	}
	public void setBalance(double balance) {
		String statement = "UPDATE hyperconomy_players SET BALANCE='" + balance + "' WHERE PLAYER = '" + name + "'";
		hc.getSQLWrite().writeData(statement);
		this.balance = balance;
	}

	
	
}
