package regalowl.hyperconomy.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;

public class ConsoleSettings {

	private HyperConomy hc;
	private DataManager em;
	private String economy;
	
	public ConsoleSettings(String economy) {
		this.economy = economy;
		this.hc = HyperConomy.hc;
		em = hc.getDataManager();
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
			econ = em.getHyperPlayer(player).getEconomy();
		}
		return econ;
	}
	
	
}
