package regalowl.hyperconomy.gui;



import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.api.ServerConnectionType;
import regalowl.hyperconomy.util.DefaultConnector;

public class GUIConnector extends DefaultConnector implements MineCraftConnector {
	

	
	public GUIConnector() {
		super();
	}
	
	@Override
	public void logInfo(String message) {
		System.out.println(message);
	}

	@Override
	public void logSevere(String message) {
		System.out.println(message);
	}
	
	@Override
	public ServerConnectionType getServerConnectionType() {
		return ServerConnectionType.GUI;
	}


}
