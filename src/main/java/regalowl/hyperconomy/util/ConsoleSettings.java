package regalowl.hyperconomy.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperPlayerManager;

public class ConsoleSettings {

	private HyperConomy hc;
	private HyperPlayerManager hpm;
	private String economy;
	
	public ConsoleSettings(String economy) {
		this.economy = economy;
		this.hc = HyperConomy.hc;
		hpm = hc.getHyperPlayerManager();
	}
	
	public String getConsoleEconomy() {
		return economy;
	}
	
	public void setConsoleEconomy(String economy) {
		this.economy = economy;
	}
	
	public String getEconomy(CommandSender sender) {
		String econ = economy;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			econ = hpm.getHyperPlayer(player).getEconomy();
		}
		return econ;
	}
	
	
}
