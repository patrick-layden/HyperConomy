package regalowl.hyperconomy;

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
		DataFunctions df = hc.getDataFunctions();

		if (!df.inDatabase(event.getPlayer().getName())) {
			hc.getDataFunctions().addPlayer(event.getPlayer().getName());
		}

	}




	
}
