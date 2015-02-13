package regalowl.hyperconomy.util;

import java.util.ArrayList;
import java.util.UUID;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.api.HEconomyProvider;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.display.FrameShopHandler;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HInventoryType;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.shop.ChestShop;

public class DefaultConnector implements MineCraftConnector {

	protected HyperConomy hc;
	//private ConcurrentHashMap<Long, BukkitTask> tasks = new ConcurrentHashMap<Long, BukkitTask>();
	//private AtomicLong taskCounter = new AtomicLong();
	
	public DefaultConnector() {
		this.hc = new HyperConomy(this);
	}
	
	
	@Override
	public HyperConomy getHC() {
		return hc;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void disablePlugin() {}
	@Override
	public void registerCommand(String command, HyperCommand hCommand) {}
	@Override
	public void unregisterAllListeners() {}
	@Override
	public void registerListeners() {}

	@Override
	public void runTask(Runnable r) {
		r.run();
	}
	@Override
	public void runTaskLater(Runnable r, Long delayTicks) {
		// TODO Auto-generated method stub
	}

	@Override
	public long runRepeatingTask(Runnable r, Long delayTicks, Long intervalTicks) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void cancelTask(long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelAllTasks() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void logInfo(String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void logSevere(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean useExternalEconomy() {
		return false;
	}

	@Override
	public String getEconomyName() {
		return "None";
	}

	@Override
	public void checkExternalEconomyRegistration() {}

	@Override
	public void unRegisterAsExternalEconomy() {}

	@Override
	public void setupHEconomyProvider() {}

	@Override
	public HEconomyProvider getEconomyProvider() {
		return null;
	}


	@Override
	public boolean isInCreativeMode(HyperPlayer hp) {
		return false;
	}

	@Override
	public HLocation getLocation(HyperPlayer hp) {
		return new HLocation("",0,0,0);
	}

	@Override
	public HLocation getTargetLocation(HyperPlayer hp) {
		return new HLocation("",0,0,0);
	}

	@Override
	public HLocation getLocationBeforeTargetLocation(HyperPlayer hp) {
		return new HLocation("",0,0,0);
	}

	@Override
	public boolean isLoaded(HLocation l) {
		return true;
	}

	@Override
	public void load(HLocation l) {}

	@Override
	public ArrayList<HyperPlayer> getOnlinePlayers() {
		return new ArrayList<HyperPlayer>();
	}
	@Override
	public ArrayList<String> getOnlinePlayerNames() {
		return new ArrayList<String>();
	}

	@Override
	public boolean worldExists(String world) {
		return true;
	}

	@Override
	public boolean isOnline(HyperPlayer hp) {
		return false;
	}

	@Override
	public UUID getUUID(HyperPlayer hp) {
		return null;
	}

	@Override
	public void teleport(HyperPlayer hp, HLocation sl) {}

	@Override
	public void sendMessage(HyperPlayer hp, String message) {}

	@Override
	public void kickPlayer(HyperPlayer hp, String message) {}

	@Override
	public boolean isSneaking(HyperPlayer hp) {
		return false;
	}

	@Override
	public boolean hasPermission(HyperPlayer hp, String permission) {
		return false;
	}

	@Override
	public boolean isPermissionSet(HyperPlayer hp, String permission) {
		return false;
	}

	@Override
	public HyperPlayer getPlayer(UUID uuid) {
		return null;
	}

	@Override
	public boolean playerExists(UUID uuid) {
		return false;
	}

	@Override
	public int getLevel(HyperPlayer hp) {
		return 0;
	}

	@Override
	public float getExp(HyperPlayer hp) {
		return 0;
	}

	@Override
	public void setLevel(HyperPlayer hp, int level) {}

	@Override
	public void setExp(HyperPlayer hp, float exp) {}

	@Override
	public String getName(HyperPlayer hp) {
		return "name";
	}

	@Override
	public void checkForNameChange(HyperPlayer hp) {}

	@Override
	public String applyColor(String text) {
		return text;
	}
	@Override
	public String removeColor(String text) {
		return text;
	}

	@Override
	public HInventory getInventory(HyperPlayer hp) {
		return new HInventory(hc, new ArrayList<HItemStack>(), HInventoryType.PLAYER);
	}

	@Override
	public HInventory getChestInventory(HLocation l) {
		return new HInventory(hc, new ArrayList<HItemStack>(), HInventoryType.CHEST);
	}

	@Override
	public void setInventory(HInventory inventory) {}

	@Override
	public HItemStack getItem(HyperPlayer hp, int slot) {
		return new HItemStack(hc);
	}

	@Override
	public int getHeldItemSlot(HyperPlayer hp) {
		return 0;
	}

	@Override
	public void setItem(HyperPlayer hp, HItemStack item, int slot) {}

	@Override
	public boolean conflictsWith(HEnchantment e1, HEnchantment e2) {
		return false;
	}

	@Override
	public boolean canEnchantItem(HItemStack item) {
		return false;
	}

	@Override
	public boolean isTransactionSign(HLocation l) {
		return false;
	}

	@Override
	public boolean isInfoSign(HLocation l) {
		return false;
	}

	@Override
	public boolean isChestShopSign(HLocation l) {
		return false;
	}

	@Override
	public boolean isChestShopSignBlock(HLocation l) {
		return false;
	}

	@Override
	public boolean isChestShopChest(HLocation l) {
		return false;
	}

	@Override
	public boolean isPartOfChestShop(HLocation l) {
		return false;
	}

	@Override
	public ChestShop getChestShop(HLocation location) {
		return null;
	}

	@Override
	public HSign getSign(HLocation location) {
		return null;
	}

	@Override
	public void setSign(HSign sign) {}

	@Override
	public HBlock getAttachedBlock(HSign sign) {
		return null;
	}

	@Override
	public boolean isChest(HLocation l) {
		return false;
	}

	@Override
	public boolean canHoldChestShopSign(HLocation l) {
		return false;
	}

	@Override
	public HItem dropItemDisplay(HLocation location, HItemStack item) {
		return null;
	}

	@Override
	public void removeItem(HItem item) {}

	@Override
	public void clearNearbyNonDisplayItems(HItem item, double radius) {}

	@Override
	public void zeroVelocity(HItem item) {}

	@Override
	public HBlock getFirstNonAirBlockInColumn(HLocation location) {
		return null;
	}
	@Override
	public boolean canFall(HBlock block) {
		return false;
	}


	@Override
	public FrameShopHandler getFrameShopHandler() {
		return null;
	}




}
