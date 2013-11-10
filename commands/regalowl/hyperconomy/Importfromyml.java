package regalowl.hyperconomy;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import regalowl.databukkit.SQLWrite;

public class Importfromyml {
	
	public Importfromyml() {}
	public Importfromyml(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (args.length == 1 || args.length == 2) {
				String economy = args[0];
				if (em.economyExists(economy)) {
					if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
						if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
							new Backup();
						}
						SQLWrite sw = hc.getSQLWrite();
						sw.addToQueue("DELETE FROM hyperconomy_objects WHERE ECONOMY = '" + economy + "'");
						FileConfiguration temp = hc.getYamlHandler().getFileConfiguration("temp");
						temp.set("importfromyml.sender", sender.getName());
						temp.set("importfromyml.economy", economy);
						sw.afterWrite(this, "runCreate");
					} else {
						sender.sendMessage(L.get("IMPORT_PROCEED"));
					}
				} else {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("IMPORTFROMYML_INVALID"));
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTFROMYML_INVALID"));
			return;
		}
	}
	
	public void runCreate() {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		FileConfiguration temp = hc.getYamlHandler().getFileConfiguration("temp");
		em.createEconomyFromYml(temp.getString("importfromyml.economy"), true);
		Player p = Bukkit.getPlayer(temp.getString("importfromyml.sender"));
		if (p != null) {
			p.sendMessage(L.get("ECONOMY_IMPORTED"));	
		}
		temp.set("importfromyml.sender", null);
		temp.set("importfromyml.economy", null);
	}
}
