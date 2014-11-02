package regalowl.hyperconomy.command;

import java.util.ArrayList;

import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;

public class CommandData {

	private Object sender;
	private String senderName;
	private boolean isPlayer;
	private String command;
	private String[] args;
	private boolean wasSuccessful;
	private ArrayList<String> response = new ArrayList<String>();
	
	public CommandData(Object sender, String senderName, boolean isPlayer, String command, String[] args) {
		this.sender = sender;
		this.senderName = senderName;
		this.isPlayer = isPlayer;
		this.command = command;
		this.args = args;
		this.wasSuccessful = false;
	}
	
	public Object getSender() {
		return sender;
	}
	public String getSenderName() {
		return senderName;
	}
	public boolean isPlayer() {
		return isPlayer;
	}
	public String getCommand() {
		return command;
	}
	public String[] getArgs() {
		return args;
	}
	 
	public HyperPlayer getHyperPlayer() {
		if (isPlayer) {
			return HC.hc.getHyperPlayerManager().getHyperPlayer(senderName);
		}
		return null;
	}
	
	public void setSuccessful() {
		this.wasSuccessful = true;
	}
	public boolean wasSuccessful() {
		return wasSuccessful;
	}
	
	public void addResponse(String response) {
		this.response.add(response);
	}
	public void addResponses(ArrayList<String> responses) {
		this.response.addAll(responses);
	}
	public ArrayList<String> getResponse() {
		return response;
	}
	
	
}
