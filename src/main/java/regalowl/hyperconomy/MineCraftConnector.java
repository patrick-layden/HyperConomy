package regalowl.hyperconomy;


import net.milkbowl.vault.economy.Economy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.util.SimpleLocation;

public interface MineCraftConnector {
	
	public boolean isEnabled();
	public void disablePlugin();
	
	public void registerCommand(String command, HyperCommand hCommand);
	
	public void unregisterAllListeners();

	
	
	public void runTask(Runnable r);
	public void runTaskLater(Runnable r, Long delayTicks);
	public long runRepeatingTask(Runnable r, Long delayTicks, Long intervalTicks);
	public void cancelTask(long id);
	public void cancelAllTasks();
	
	public void kickPlayer(String name, String message);
	public boolean hasPermission(String name, String permission);
	
	public boolean useExternalEconomy();
	public String getEconomyName();
	public void hookExternalEconomy();
	public void unhookExternalEconomy();
	public void setupExternalEconomy();
	
	//remove later
	public Economy getEconomy();  
	public BukkitConnector getConnector();
	
	public SimpleLocation getLocation(HyperPlayer hp);
	public SerializableInventory getInventory(HyperPlayer hp);
	public void setInventory(HyperPlayer hp, SerializableInventory inventory);

}
