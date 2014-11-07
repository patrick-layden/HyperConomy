package regalowl.hyperconomy.api;


import java.util.ArrayList;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.bukkit.BukkitConnector;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.shop.ChestShop;

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
	public HLocation getLocation(HyperPlayer hp);
	public HLocation getTargetLocation(HyperPlayer hp);
	public HLocation getLocationBeforeTargetLocation(HyperPlayer hp);
	public boolean isLoaded(HLocation l);
	public void load(HLocation l);
	public ArrayList<HyperPlayer> getOnlinePlayers();
	public boolean worldExists(String world);
	
	
	public boolean isOnline(HyperPlayer hp);
	public UUID getUUID(HyperPlayer hp);
	public void teleport(HyperPlayer hp, HLocation sl);
	public void sendMessage(HyperPlayer hp, String message);
	public void kickPlayer(HyperPlayer hp, String message);
	public boolean isSneaking(HyperPlayer hp);
	public boolean hasPermission(HyperPlayer hp, String permission);
	public boolean isPermissionSet(HyperPlayer hp, String permission);
	public HyperPlayer getPlayer(UUID uuid);
	public boolean playerExists(UUID uuid);
	public int getLevel(HyperPlayer hp);
	public float getExp(HyperPlayer hp);
	public void setLevel(HyperPlayer hp, int level);
	public void setExp(HyperPlayer hp, float exp);
	public String getName(HyperPlayer hp);
	public void checkForNameChange(HyperPlayer hp);
	
	
	
	public void logInfo(String message);
	public void logSevere(String message);
	public String applyColor(String text);
	public String removeColor(String text);
	
	public HInventory getInventory(HyperPlayer hp);
	public HInventory getChestInventory(HLocation l);
	public void setInventory(HInventory inventory);
	public HItemStack getItem(HyperPlayer hp, int slot);
	public int getHeldItemSlot(HyperPlayer hp);
	public void setItem(HyperPlayer hp, HItemStack item, int slot);
	
	public boolean conflictsWith(HEnchantment e1, HEnchantment e2);
	public boolean canEnchantItem(HItemStack item);
	
	public boolean isTransactionSign(HLocation l);
	public boolean isInfoSign(HLocation l);
	public boolean isChestShopSign(HLocation l);
	public boolean isChestShopSignBlock(HLocation l);
	public boolean isChestShopChest(HLocation l);
	public boolean isPartOfChestShop(HLocation l);
	
	public ChestShop getChestShop(HLocation location);
	public HSign getSign(HLocation location);
	public void setSign(HSign sign);
	public HBlock getAttachedBlock(HSign sign);
	public boolean isChest(HLocation l);
	public boolean canHoldChestShopSign(HLocation l);
	
	public HItem dropItemDisplay(HLocation location, HItemStack item);
	public void removeItem(HItem item);
	public void clearNearbyNonDisplayItems(HItem item, double radius);
	public void zeroVelocity(HItem item);
	public HBlock getFirstNonAirBlockInColumn(HLocation location);
	public boolean canFall(HBlock block);
}
