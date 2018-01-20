package regalowl.hyperconomy.api;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.display.FrameShopHandler;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;

public interface MineCraftConnector {
	
	HyperConomy getHC();
	boolean isEnabled();
	void disablePlugin();
	String getVersion();
	ServerConnectionType getServerConnectionType();
	
	void registerCommand(String command, HyperCommand hCommand);
	
	
	void unregisterAllListeners();
	void registerListeners();
	void setListenerState(boolean minimal);
	
	
	void runTask(Runnable r);
	void runTaskLater(Runnable r, Long delayTicks);
	long runRepeatingTask(Runnable r, Long delayTicks, Long intervalTicks);
	void cancelTask(long id);
	void cancelAllTasks();
	

	boolean useExternalEconomy();
	String getEconomyName();
	void checkExternalEconomyRegistration();
	void unRegisterAsExternalEconomy();
	void setupHEconomyProvider();
	HEconomyProvider getEconomyProvider();
	
	
	boolean isInCreativeMode(HyperPlayer hp);
	HLocation getLocation(HyperPlayer hp);
	HLocation getTargetLocation(HyperPlayer hp);
	HLocation getLocationBeforeTargetLocation(HyperPlayer hp);
	boolean isOnline(HyperPlayer hp);
	UUID getUUID(HyperPlayer hp);
	void teleport(HyperPlayer hp, HLocation sl);
	void sendMessage(HyperPlayer hp, String message);
	void kickPlayer(HyperPlayer hp, String message);
	boolean isSneaking(HyperPlayer hp);
	boolean hasPermission(HyperPlayer hp, String permission);
	boolean isPermissionSet(HyperPlayer hp, String permission);
	HyperPlayer getPlayer(UUID uuid);
	boolean playerExists(UUID uuid);
	int getLevel(HyperPlayer hp);
	float getExp(HyperPlayer hp);
	void setLevel(HyperPlayer hp, int level);
	void setExp(HyperPlayer hp, float exp);
	String getName(HyperPlayer hp);
	void checkForNameChange(HyperPlayer hp);
	HItemStack getItem(HyperPlayer hp, int slot);
	int getHeldItemSlot(HyperPlayer hp);
	HInventory getInventory(HyperPlayer hp);
	void setItem(HyperPlayer player, HItemStack item, int slot);
	void setItem(HLocation location, HItemStack item, int slot);
	void setItemQuantity(HLocation location, int amount, int slot);
	void setItemQuantity(HyperPlayer hp, int amount, int slot);
	void setItemLore(HInventory inventory, List<String> lore, int slot);
	void setItemOnCursor(HyperPlayer p, HItemStack stack);
	String getMinecraftItemName(HItemStack stack);
	
	HInventory getChestInventory(HLocation l);
	void setInventory(HInventory inventory);
	void openInventory(HInventory inventory, HyperPlayer player, String name);
	void closeActiveInventory(HyperPlayer p);
	boolean conflictsWith(HEnchantment e1, HEnchantment e2);
	boolean canEnchantItem(HItemStack item);
	
	
	ArrayList<HyperPlayer> getOnlinePlayers();
	ArrayList<String> getOnlinePlayerNames();
	boolean worldExists(String world);
	
	
	boolean isLoaded(HLocation l);
	void load(HLocation l);

	
	void logInfo(String message);
	void logSevere(String message);
	String applyColor(String text);
	String removeColor(String text);
	
	
	boolean isTransactionSign(HLocation l);
	boolean isInfoSign(HLocation l);
	boolean isChestShopSign(HLocation l);
	boolean isChestShopSignBlock(HLocation l);
	boolean isChestShopChest(HLocation l);
	boolean isPartOfChestShop(HLocation l);
	//public ChestShop getChestShop(HLocation location);
	HSign getSign(HLocation location);
	void setSign(HSign sign);
	HBlock getAttachedBlock(HSign sign);
	boolean isChest(HLocation l);
	boolean canHoldChestShopSign(HLocation l);
	
	
	HItem dropItemDisplay(HLocation location, HItemStack item);
	void removeItem(HItem item);
	void clearNearbyNonDisplayItems(HItem item, double radius);
	void zeroVelocity(HItem item);
	HBlock getFirstNonAirBlockInColumn(HLocation location);
	boolean canFall(HBlock block);
	
	
	FrameShopHandler getFrameShopHandler();
	
}
