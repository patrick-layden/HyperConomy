package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConsoleSettings {

	private HyperConomy hc;
	private EconomyManager em;
	private String economy;
	
	ConsoleSettings(String economy) {
		this.economy = economy;
		this.hc = HyperConomy.hc;
		em = hc.getEconomyManager();
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
