package regalowl.hyperconomy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;



public class HyperPlayers implements Listener {
	
	private HyperConomy hc;
	
	public HyperPlayers(HyperConomy hyc){
		hc = hyc;
    	hc.getServer().getPluginManager().registerEvents(this, hc);
    }

    
	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
		DataFunctions df = hc.getSQLFunctions();
		if (hc.useSQL()) {
			if (!df.inDatabase(event.getPlayer().getName())) {
				hc.getSQLFunctions().addPlayer(event.getPlayer().getName());
			}
		} else {
			String player = event.getPlayer().getName();
			FileConfiguration players = hc.getYaml().getPlayers();
			String test = players.getString(player + ".balance");
			if (test == null) {
				players.set(player + ".balance", 0);
				hc.getSQLFunctions().addPlayer(player);
			}
		}
	}




	
}
