package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.bukkit.BukkitConnector;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.display.ItemDisplay;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.shop.ChestShop;
import regalowl.hyperconomy.util.HBlock;
import regalowl.hyperconomy.util.HItem;
import regalowl.hyperconomy.util.HSign;
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
	public void load(SimpleLocation l);
	public ArrayList<HyperPlayer> getOnlinePlayers();
	public void teleport(HyperPlayer hp, SimpleLocation sl);
	public void sendMessage(HyperPlayer hp, String message);
	public void kickPlayer(HyperPlayer hp, String message);
	public boolean isSneaking(HyperPlayer hp);
	public boolean hasPermission(HyperPlayer hp, String permission);
	public HyperPlayer getPlayer(UUID uuid);
	public boolean playerExists(UUID uuid);
	public int getLevel(HyperPlayer hp);
	
	
	
	public void logInfo(String message);
	public void logSevere(String message);
	public String applyColor(String text);
	public String removeColor(String text);
	
	public SerializableInventory getInventory(HyperPlayer hp);
	public SerializableInventory getChestInventory(SimpleLocation l);
	public void setInventory(SerializableInventory inventory);
	public SerializableItemStack getItem(HyperPlayer hp, int slot);
	public int getHeldItemSlot(HyperPlayer hp);
	public void setItem(HyperPlayer hp, SerializableItemStack item, int slot);
	
	public boolean conflictsWith(SerializableEnchantment e1, SerializableEnchantment e2);
	public boolean canEnchantItem(SerializableItemStack item);
	
	public boolean isTransactionSign(SimpleLocation l);
	public boolean isInfoSign(SimpleLocation l);
	public boolean isChestShopSign(SimpleLocation l);
	public boolean isChestShop(SimpleLocation l, boolean includeSign);
	
	public ChestShop getChestShop(SimpleLocation location);
	public HSign getSign(SimpleLocation location);
	public void setSign(HSign sign);
	public void updateSign(HSign sign);
	public HBlock getAttachedBlock(HSign sign);
	public boolean isChest(SimpleLocation l);
	public boolean canHoldChestShopSign(SimpleLocation l);
	
	public HItem dropItemDisplay(SimpleLocation location, SerializableItemStack item);
	public void removeItem(HItem item);
	public void clearNearbyNonDisplayItems(HItem item, double radius);
	public void zeroVelocity(HItem item);
	public HBlock getFirstNonAirBlockInColumn(SimpleLocation location);
	public boolean canFall(HBlock block);
}
