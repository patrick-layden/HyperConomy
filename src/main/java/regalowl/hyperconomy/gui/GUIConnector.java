package regalowl.hyperconomy.gui;


import javax.swing.JTextArea;

import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.util.DefaultConnector;

public class GUIConnector extends DefaultConnector implements MineCraftConnector {
	
	private JTextArea notificationText;
	
	public GUIConnector(JTextArea notificationText) {
		super();
		this.notificationText = notificationText;
	}
	
	
	@Override
	public void logInfo(String message) {
		notificationText.append("\n" + message);
	}

	@Override
	public void logSevere(String message) {
		notificationText.append("\n" + message);
	}


}
