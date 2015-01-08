package regalowl.hyperconomy.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.InternalEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.api.HEconomyProvider;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.command.CommandData;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.display.FrameShopHandler;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.shop.ChestShop;

public class BukkitConnector extends JavaPlugin implements MineCraftConnector, Listener {

	private HashMap<String, HyperCommand> commands = new HashMap<String, HyperCommand>();
	private ConcurrentHashMap<Long, BukkitTask> tasks = new ConcurrentHashMap<Long, BukkitTask>();
	private AtomicLong taskCounter = new AtomicLong();
	private HyperConomy hc;
	private BukkitListener bl;
	private BukkitCommon common;
	
	private boolean vaultInstalled;
	private boolean useExternalEconomy;
	private Economy vaultEconomy;
	private HEconomyProvider economyProvider;

	
	public BukkitConnector() {
		this.hc = new HyperConomy(this);
		this.bl = new BukkitListener(this);
		this.common = new BukkitCommon(hc);
		useExternalEconomy = false;	
	}
	
	public BukkitCommon getBukkitCommon() {
		return common;
	}
	


	
	
	//JavaPlugin Bukkit methods
	@Override
	public void onLoad() {
		if (hc == null) hc = new HyperConomy(this);
		hc.load();
	}
	@Override
	public void onEnable() {
		hc.enable();
	}
	@Override
	public void onDisable() {
		hc.disable(false);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (commands.containsKey(cmd.getName().toLowerCase())) {
			HyperCommand hCommand = commands.get(cmd.getName().toLowerCase());
			boolean isPlayer = false;
			if (sender instanceof Player) {
				isPlayer = true;
			}
			CommandData data = hCommand.onCommand(new CommandData(hc, sender, sender.getName(), isPlayer, cmd.getName(), args));
			for (String response: data.getResponse()) {
				sender.sendMessage(common.applyColor(response));
			}
		} else {
			hc.getDebugMode().syncDebugConsoleMessage("Command not found: " + cmd.getName());
		}
		return true;
	}
	
	
	
	
	
	
	@Override
	public HEconomyProvider getEconomyProvider() {
		return economyProvider;
	}
	
	@Override
	public void checkExternalEconomyRegistration() {
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");
		vaultInstalled = (vault != null & vault instanceof Vault) ? true:false;
		useExternalEconomy = hc.gYH().getFileConfiguration("config").getBoolean("economy-plugin.use-external");
		if (!vaultInstalled) useExternalEconomy = false;
		if (!useExternalEconomy && vaultInstalled && hc.gYH().gFC("config").getBoolean("economy-plugin.hook-internal-economy-into-vault")) {
			getServer().getServicesManager().register(Economy.class, new Economy_HyperConomy(hc), this, ServicePriority.Highest);
			this.getLogger().info("[HyperConomy]Internal economy registered with Vault.");
		}
	}
	@Override
	public void unRegisterAsExternalEconomy() {
		if (!vaultInstalled) return;
	    RegisteredServiceProvider<Economy> eco = getServer().getServicesManager().getRegistration(Economy.class);
	    if (eco == null) return;
    	Economy registeredEconomy = eco.getProvider();
    	if (registeredEconomy != null && registeredEconomy.getName().equalsIgnoreCase("HyperConomy")) {
	        getServer().getServicesManager().unregister(eco.getProvider());
	        this.getLogger().info("[HyperConomy]Internal economy unregistered from Vault.");
    	}
	}
	@Override
	public void setupHEconomyProvider() {
		if (useExternalEconomy && vaultInstalled) {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null) vaultEconomy = economyProvider.getProvider();
			if (vaultEconomy == null) useExternalEconomy = false;
			if (vaultEconomy != null && vaultEconomy.getName().equalsIgnoreCase("HyperConomy")) useExternalEconomy = false;
		}
		if (useExternalEconomy) {
			this.economyProvider = new BukkitEconomy(vaultEconomy);
			logInfo("[HyperConomy]Using external economy plugin ("+getEconomyName()+") via Vault.");
		} else {
			this.economyProvider = new InternalEconomy(hc);
			logInfo("[HyperConomy]Using internal economy plugin.");
		}
	}
	@Override
	public boolean useExternalEconomy() {
		return useExternalEconomy;
	}
	


	@Override
	public String getEconomyName() {
		if (vaultEconomy != null && useExternalEconomy) {
			return vaultEconomy.getName();
		}
		return "N/A";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Bukkit Listeners
	
	
	
	
	
	//MineCraftConnector overrides
	@Override
	public void unregisterAllListeners() {
		bl.unregisterAllListeners();
	}
	@Override
	public void registerListeners() {
		bl.registerListeners();
	}

	@Override
	public void registerCommand(String command, HyperCommand hCommand) {
		commands.put(command.toLowerCase(), hCommand);
	}
	@Override
	public void disablePlugin() {
		Bukkit.getPluginManager().disablePlugin(this);
	}

	@Override
	public void runTask(Runnable r) {
		getServer().getScheduler().runTask(this, r);
	}
	@Override
	public void runTaskLater(Runnable r, Long delay) {
		getServer().getScheduler().runTaskLater(this, r, delay);
	}
	@Override
	public long runRepeatingTask(Runnable r, Long delayTicks, Long intervalTicks) {
		BukkitTask t = getServer().getScheduler().runTaskTimer(this, r, delayTicks, intervalTicks);
		long taskCount = taskCounter.getAndIncrement();
		tasks.put(taskCount, t);
		return taskCount;
	}
	@Override
	public void cancelTask(long id) {
		if (tasks.containsKey(id)) {
			tasks.get(id).cancel();
			tasks.remove(id);
		}
	}
	@Override
	public void cancelAllTasks() {
		getServer().getScheduler().cancelTasks(this);
	}



	@Override
	public void kickPlayer(HyperPlayer hp, String message) {
		Player p = Bukkit.getPlayer(hp.getName());
		if (p != null) {
			p.kickPlayer(message);
		}
	}



	@Override
	public boolean hasPermission(HyperPlayer hp, String permission) {
		Player p = Bukkit.getPlayer(hp.getName());
		if (p != null) {
			return p.hasPermission(permission);
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public HLocation getTargetLocation(HyperPlayer hp) {
		try {
			Player p = Bukkit.getPlayer(hp.getName());
			if (p == null) return null;
			Location l = p.getTargetBlock(null, 500).getLocation();
			HLocation sl = new HLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
			return sl;
		} catch (Exception e) {
			return null;
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	public HLocation getLocationBeforeTargetLocation(HyperPlayer hp) {
		try {
			Player p = Bukkit.getPlayer(hp.getName());
			if (p == null) return null;
			List<Block> ltb = p.getLastTwoTargetBlocks(null, 500);
			Block b = ltb.get(0);
			Location l = b.getLocation();
			HLocation sl = new HLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
			return sl;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean isLoaded(HLocation l) {
		Location loc = common.getLocation(l);
		return loc.getChunk().isLoaded();
	}
		
	@Override
	public void load(HLocation l) {
		Location loc = common.getLocation(l);
		loc.getChunk().load();
	}
	

	@Override
	public HLocation getLocation(HyperPlayer hp) {
		if (hp == null) return null;
		Player p = Bukkit.getPlayer(hp.getName());
		if (p == null) return null;
		Location l = p.getLocation();
		return new HLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
	}
	
	
	
	@Override
	public boolean conflictsWith(HEnchantment e1, HEnchantment e2) {
		Enchantment ench1 = Enchantment.getByName(e1.getEnchantmentName());
		Enchantment ench2 = Enchantment.getByName(e2.getEnchantmentName());
		return ench1.conflictsWith(ench2);
	}
	
	@Override
	public boolean canEnchantItem(HItemStack item) {
		ItemStack s = common.getItemStack(item);
		for (Enchantment enchant:Enchantment.values()) {
			if (enchant.canEnchantItem(s)) return true;
		}
		return false;
	}


	

	@Override
	public HInventory getInventory(HyperPlayer hp) {
		return common.getInventory(hp);
	}

	@Override
	public HInventory getChestInventory(HLocation l) {
		return common.getChestInventory(l);
	}

	@Override
	public void setInventory(HInventory inventory) {
		common.setInventory(inventory);
	}
	

	@Override
	public int getHeldItemSlot(HyperPlayer hp) {
		Player p = Bukkit.getPlayer(hp.getName());
		return p.getInventory().getHeldItemSlot();
	}


	@Override
	public HItemStack getItem(HyperPlayer hp, int slot) {
		Player p = Bukkit.getPlayer(hp.getName());
		return common.getHItemStack(p.getInventory().getItem(slot));
	}


	@Override
	public void setItem(HyperPlayer hp, HItemStack item, int slot) {
		Player p = Bukkit.getPlayer(hp.getName());
		p.getInventory().setItem(slot, common.getItemStack(item));
	}
	






	@Override
	public String applyColor(String message) {
		return common.applyColor(message);
	}

	@Override
	public void logInfo(String message) {
		Logger log = Logger.getLogger("Minecraft");
		log.info(applyColor(message));
	}


	@Override
	public void logSevere(String message) {
		Logger log = Logger.getLogger("Minecraft");
		log.severe(applyColor(message));
	}







	
	
	
	
	
	
	
	
	

	@Override
	public boolean isSneaking(HyperPlayer hp) {
		Player p = common.getPlayer(hp);
		return p.isSneaking();
	}

	@Override
	public boolean isInCreativeMode(HyperPlayer hp) {
		Player p = common.getPlayer(hp);
		return (p.getGameMode() == GameMode.CREATIVE) ? true:false;
	}



	@Override
	public ArrayList<HyperPlayer> getOnlinePlayers() {
		ArrayList<HyperPlayer> onlinePlayers = new ArrayList<HyperPlayer>();
		for (World world : Bukkit.getWorlds()) {
			for (Player p:world.getPlayers()) {
				onlinePlayers.add(hc.getHyperPlayerManager().getHyperPlayer(p.getName()));
			}
		}
		return onlinePlayers;
	}



	@Override
	public HyperPlayer getPlayer(UUID uuid) {
		OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
		if (!playerExists(uuid)) return null;
		HyperPlayer hp = hc.getHyperPlayerManager().getHyperPlayer(op.getName());
		hp.setUUID(uuid.toString());
		return hp;
	}



	@Override
	public boolean playerExists(UUID uuid) {
		OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
		if (op.getName() == null || op.getName() == "") return false;
		return true;
	}



	@Override
	public void teleport(HyperPlayer hp, HLocation sl) {
		Player p = common.getPlayer(hp);
		Location l = common.getLocation(sl);
		Chunk c = l.getChunk();
		if (c == null) return;
		if (!c.isLoaded()) c.load(true);
		p.teleport(l);
	}



	@Override
	public void sendMessage(HyperPlayer hp, String message) {
		Player p = Bukkit.getPlayer(hp.getName());
		runTask(new Messager(p,applyColor(message)));
	}
	private class Messager implements Runnable {
		private Player p;
		private String m;
		public Messager(Player p, String message) {
			this.p = p;
			this.m = message;
		}
		@Override
		public void run() {
			if (p == null || m == null || m == "") return;
			p.sendMessage(m);
		}
	}
	
	
	
	@Override
	public boolean isOnline(HyperPlayer hp) {
		Player p = common.getPlayer(hp);
		if (p == null) return false;
		return true;
	}



	@Override
	public UUID getUUID(HyperPlayer hp) {
		Player p = common.getPlayer(hp);
		if (p == null) return null;
		return p.getUniqueId();
	}



	@Override
	public boolean isPermissionSet(HyperPlayer hp, String permission) {
		Player p = common.getPlayer(hp);
		if (p == null) return false;
		return p.isPermissionSet(permission);
	}










	@Override
	public boolean isInfoSign(HLocation l) {
		return common.isInfoSign(l);
	}


	@Override
	public boolean isChestShopChest(HLocation l) {
		return common.isChestShopChest(l);
	}
	@Override
	public boolean isChestShopSign(HLocation l) {
		return common.isChestShopSign(l);
	}
	@Override
	public boolean isChestShopSignBlock(HLocation l) {
		return common.isChestShopSignBlock(l);
	}
	@Override
	public boolean isPartOfChestShop(HLocation l) {
		return common.isPartOfChestShop(l);
	}
	@Override
	public ChestShop getChestShop(HLocation location) {
		return common.getChestShop(location);
	}


	@Override
	public boolean canHoldChestShopSign(HLocation l) {
		Block b = common.getBlock(l);
		Material m = b.getType();
		if (m == Material.ICE || m == Material.LEAVES || m == Material.SAND || m == Material.GRAVEL || m == Material.SIGN || m == Material.SIGN_POST || m == Material.TNT) {
			return false;
		}
		return true;
	}







	@Override
	public String removeColor(String text) {
		return common.removeColor(text);
	}



	@Override
	public HSign getSign(HLocation location) {
		if (location == null) return null;
		Block b = common.getLocation(location).getBlock();
		if (b != null && (b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.WALL_SIGN))) {
			Sign s = (Sign) b.getState();
			boolean isWallSign = (b.getType().equals(Material.WALL_SIGN)) ? true:false;
			ArrayList<String> lines = new ArrayList<String>();
			for (String l:s.getLines()) {
				lines.add(l);
			}
			HSign sign = new HSign(hc, new HLocation(location), lines, isWallSign);
			return sign;
		}
		return null;
	}
	
	@Override
	public void setSign(HSign sign) {
		Sign s = common.getSign(sign.getLocation());
		if (s == null) return;
		s.setLine(0, applyColor(sign.getLine(0)));
		s.setLine(1, applyColor(sign.getLine(1)));
		s.setLine(2, applyColor(sign.getLine(2)));
		s.setLine(3, applyColor(sign.getLine(3)));
		s.update();
	}




	@Override
	public HBlock getAttachedBlock(HSign sign) {
		Block b = common.getBlock(sign.getLocation());
		org.bukkit.material.Sign msign = (org.bukkit.material.Sign) b.getState().getData();
		BlockFace attachedface = msign.getAttachedFace();
		Block attachedblock = b.getRelative(attachedface);
		return common.getBlock(attachedblock);
	}



	@Override
	public boolean isChest(HLocation l) {
		BlockState b = common.getBlock(l).getState();
		return (b instanceof Chest) ? true:false;
	}



	@Override
	public boolean isTransactionSign(HLocation l) {
		return common.isTransactionSign(l);
	}

	@Override
	public HItem dropItemDisplay(HLocation location, HItemStack item) {
		World w = Bukkit.getWorld(location.getWorld());
		if (w == null) return null;
		Item i = w.dropItem(common.getLocation(location), common.getItemStack(item));
		i.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
		i.setMetadata("HyperConomy", new FixedMetadataValue(this, "item_display"));
		return new HItem(hc, location, i.getEntityId(), item);
	}



	@Override
	public void removeItem(HItem item) {
		HLocation l = item.getLocation();
		Location loc = common.getLocation(l);
		for (Entity ent:loc.getChunk().getEntities()) {
			if (ent instanceof Item) {
				Item i = (Item)ent;
				if (i.getEntityId() == item.getId()) {
					i.remove();
				}
			}
		}
	}



	@Override
	public void zeroVelocity(HItem item) {
		Item i = common.getItem(item);
		if (i != null) {
			i.setVelocity(new Vector(0,0,0));
		}
	}



	@Override
	public HBlock getFirstNonAirBlockInColumn(HLocation location) {
		Location loc = common.getLocation(location);
		Block dblock = loc.getBlock();
		while (dblock.getType().equals(Material.AIR)) {
			dblock = dblock.getRelative(BlockFace.DOWN);
		}
		return common.getBlock(dblock);
	}



	@Override
	public void clearNearbyNonDisplayItems(HItem item, double radius) {
		Item i = common.getItem(item);
		if (i == null) return;
		ItemStack tStack = new ItemStack(Material.ROTTEN_FLESH);
		Location l = common.getLocation(item.getLocation());
		Item tempItem = l.getWorld().dropItem(l, tStack);
		entityLoop : for (Entity entity : tempItem.getNearbyEntities(radius, radius, radius)) {
			if (!(entity instanceof Item)) {continue;}
			Item nearbyItem = (Item) entity;
			for (MetadataValue cmeta: nearbyItem.getMetadata("HyperConomy")) {
				if (cmeta.asString().equalsIgnoreCase("item_display")) {
					continue entityLoop;
				}
			}
			if (nearbyItem.equals(tempItem)) continue;
			if (nearbyItem.equals(i)) continue;
			if (!common.getHItemStack(tStack).equals(common.getHItemStack(nearbyItem.getItemStack()))) continue;
			if (nearbyItem.getItemStack().getType() != tempItem.getItemStack().getType()) continue;
			entity.remove();
		}
		tempItem.remove();
	}



	@Override
	public boolean canFall(HBlock block) {
		Block b = common.getBlock(block.getLocation());
		if (b.getType().equals(Material.GRAVEL) || b.getType().equals(Material.SAND)) return true;
		return false;
	}



	@Override
	public int getLevel(HyperPlayer hp) {
		Player p = common.getPlayer(hp);
		if (p == null) return 0;
		return p.getLevel();
	}

	@Override
	public float getExp(HyperPlayer hp) {
		Player p = common.getPlayer(hp);
		if (p == null) return 0;
		return p.getExp();
	}



	@Override
	public String getName(HyperPlayer hp) {
		Player p = common.getPlayer(hp);
		if (p == null) return null;
		return p.getName();
	}



	@Override
	public void setLevel(HyperPlayer hp, int level) {
		Player p = common.getPlayer(hp);
		if (p == null) return;
		p.setLevel(level);
	}



	@Override
	public void setExp(HyperPlayer hp, float exp) {
		Player p = common.getPlayer(hp);
		if (p == null) return;
		p.setExp(exp);
	}

	@Override
	public void checkForNameChange(HyperPlayer hp) {
		if (hp.getUUID() == null || hp.getUUIDString() == "") {return;}
		Player p = null;
		try {
			p = Bukkit.getPlayer(hp.getUUID());
		} catch (IllegalArgumentException e) {
			return;
		}
		if (p == null) {return;}
		if (p.getName().equals(hp.getName())) {return;}
		if (hc.getMC().useExternalEconomy()) {
			double oldBalance = hp.getBalance();
			hp.setBalance(0.0);
			hp.setName(p.getName());
			hp.setBalance(oldBalance);
		} else {
			hp.setName(p.getName());
		}
	}



	@Override
	public boolean worldExists(String world) {
		if (Bukkit.getWorld(world) == null) return false;
		return true;
	}



	@Override
	public HyperConomy getHC() {
		return hc;
	}

	@Override
	public FrameShopHandler getFrameShopHandler() {
		return new BukkitFrameShopHandler(this);
	}






	
}
