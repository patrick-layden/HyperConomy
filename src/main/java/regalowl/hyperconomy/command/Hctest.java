package regalowl.hyperconomy.command;












public class Hctest implements HyperCommand {
	public CommandData onCommand(CommandData data) {
		data.setSuccessful();
		return data;
	}
}
