package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.bukkit.BukkitConnector;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.util.SimpleLocation;

public interface MineCraftConnector {
	
	public boolean isEnabled();
	public void disablePlugin();
	
	public void registerCommand(String command, HyperCommand hCommand);
	
	public void unregisterAllListeners();
	public void registerListeners();
	
	
	public void runTask(Runnable r);
	public void runTaskLater(Runnable r, Long delayTicks);
	public long runRepeatingTask(Runnable r, Long delayTicks, Long intervalTicks);
	public void cancelTask(long id);
	public void cancelAllTasks();
	

	
	public boolean useExternalEconomy();
	public String getEconomyName();
	public void hookExternalEconomy();
	public void unhookExternalEconomy();
	public void setupExternalEconomy();
	

	public Economy getEconomy();  	//remove later
	public BukkitConnector getConnector();
	
	public boolean isInCreativeMode(HyperPlayer hp);
	public SimpleLocation getLocation(HyperPlayer hp);
	public SimpleLocation getTargetLocation(HyperPlayer hp);
	public SimpleLocation getLocationBeforeTargetLocation(HyperPlayer hp);
	public boolean isLoaded(SimpleLocation l);
	public ArrayList<HyperPlayer> getOnlinePlayers();
	public void teleport(HyperPlayer hp, SimpleLocation sl);
	public void sendMessage(HyperPlayer hp, String message);
	public void kickPlayer(HyperPlayer hp, String message);
	public boolean isSneaking(HyperPlayer hp);
	public boolean hasPermission(HyperPlayer hp, String permission);
	public HyperPlayer getPlayer(UUID uuid);
	public boolean playerExists(UUID uuid);
	
	
	
	public void logInfo(String message);
	public void logSevere(String message);
	public String applyColor(String text);
	
	public SerializableInventory getInventory(HyperPlayer hp);
	public SerializableInventory getChestInventory(SimpleLocation l);
	public void setInventory(SerializableInventory inventory);
	public SerializableItemStack getItem(HyperPlayer hp, int slot);
	public int getHeldItemSlot(HyperPlayer hp);
	public void setItem(HyperPlayer hp, SerializableItemStack item, int slot);
	
	public boolean conflictsWith(SerializableEnchantment e1, SerializableEnchantment e2);
	public boolean canEnchantItem(SerializableItemStack item);

}
