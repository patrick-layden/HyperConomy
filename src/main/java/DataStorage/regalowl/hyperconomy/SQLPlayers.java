package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;



public class SQLPlayers implements Listener {
	
	private HyperConomy hc;
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;
	
	public SQLPlayers(HyperConomy hyc){
		hc = hyc;
    	hc.getServer().getPluginManager().registerEvents(this, hc);
		FileConfiguration config = hc.getYaml().getConfig();
		username = config.getString("config.sql-connection.username");
		password = config.getString("config.sql-connection.password");
		port = config.getInt("config.sql-connection.port");
		host = config.getString("config.sql-connection.host");
		database = config.getString("config.sql-connection.database");
    }

    
	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
		if (!inDatabase(event.getPlayer())) {
			SQLWrite sw = hc.getSQLWrite();
			sw.writeData("Insert Into hyperplayers (PLAYER, ECONOMY)" + " Values ('" + event.getPlayer().getName().toLowerCase() + "','" + "default" + "')");
			hc.getSQLFunctions().addPlayerEconomy(event.getPlayer().getName().toLowerCase(), "default");
		}
	}



	private boolean inDatabase(Player p) {
		boolean indatabase = false;
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();	
			ResultSet result = state.executeQuery("SELECT PLAYER FROM hyperplayers WHERE PLAYER = " + "'" + p.getName() + "'");
			int c = 0;
			while (result.next()) {
				c++;
			}
			if (c > 0) {
				indatabase = true;
			} else {
				indatabase =  false;
			}
            result.close();
            state.close();
            connect.close();
            return indatabase;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "actionzones.admin");
			e.printStackTrace();
			return false;
		}
	}
	
}
