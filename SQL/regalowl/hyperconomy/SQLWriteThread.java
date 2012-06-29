package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;


public class SQLWriteThread {


	private HyperConomy hc;
	private SQLWriteThreadManager sf;
	private int savecompletetaskid;
	private int counter;
	private ArrayList<String> statements;
	private boolean savecomplete;
	private int tid;
		
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;
		
	public void writeThread(HyperConomy hyc, SQLWriteThreadManager sqf, ArrayList<String> states, int threadid) {
		hc = hyc;
		sf = sqf;
		statements = states;
		tid = threadid;
			
		savecomplete = false;
		counter = 0;
			
		FileConfiguration config = hc.getYaml().getConfig();
		username = config.getString("config.sql-connection.username");
		password = config.getString("config.sql-connection.password");
		port = config.getInt("config.sql-connection.port");
		host = config.getString("config.sql-connection.host");
		database = config.getString("config.sql-connection.database");
			
			
		hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
			public void run() {
				try {
					Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
					Statement state = connection.createStatement();
					counter = 0;
					while (counter < statements.size()) {
						state.execute(statements.get(counter));
						counter++;
					}
						
				    state.close();
				    connection.close();
				    savecomplete = true;
				} catch (SQLException e) {
					e.printStackTrace();
					sf.abortSave();
				}
			}
		}, 0L);
		

			
		savecompletetaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				sf.setProcessed(tid, counter);
				if (savecomplete) {
					saveComplete();
					//p.sendMessage(ChatColor.GREEN + "Save complete!" + " (Thread: " + tid + ")");
				} else {
					//p.sendMessage(ChatColor.GREEN + "Saving Zone: " + counter + " items processed." + " (Thread: " + tid + ")");
				}
			}
		}, 0L, 200L);
			
		
		
	}
		
		
		public void saveComplete() {
			hc.getServer().getScheduler().cancelTask(savecompletetaskid);
		}


	
	
	
	
}
