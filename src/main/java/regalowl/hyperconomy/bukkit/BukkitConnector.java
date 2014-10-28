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
import org.bukkit.ChatColor;
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
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.MineCraftConnector;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.command.CommandData;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.display.SignType;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.shop.ChestShop;
import regalowl.hyperconomy.util.Economy_HyperConomy;
import regalowl.hyperconomy.util.HBlock;
import regalowl.hyperconomy.util.HSign;
import regalowl.hyperconomy.util.SimpleLocation;

public class BukkitConnector extends JavaPlugin implements MineCraftConnector, Listener {

	private HashMap<String, HyperCommand> commands = new HashMap<String, HyperCommand>();
	private ConcurrentHashMap<Long, BukkitTask> tasks = new ConcurrentHashMap<Long, BukkitTask>();
	private AtomicLong taskCounter = new AtomicLong();
	private HyperConomy hc;
	private BukkitListener bl;
	
	private boolean vaultInstalled;
	private boolean useExternalEconomy;
	private Economy economy;

	
	public BukkitConnector() {
		new HyperConomy(this);
		this.bl = new BukkitListener(this);
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
			CommandData data = hCommand.onCommand(new CommandData(sender, sender.getName(), isPlayer, cmd.getName(), args));
			for (String response: data.getResponse()) {
				sender.sendMessage(response);
			}
		} else {
			hc.getDebugMode().syncDebugConsoleMessage("Command not found: " + cmd.getName());
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void hookExternalEconomy() {
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");
		if (vault != null & vault instanceof Vault) {
			vaultInstalled = true;
		} else {
			vaultInstalled = false;
		}
		useExternalEconomy = HyperConomy.hc.gYH().getFileConfiguration("config").getBoolean("economy-plugin.use-external");
		if (!vaultInstalled) {
			useExternalEconomy = false;
		}
		if (vaultInstalled && HyperConomy.hc.gYH().gFC("config").getBoolean("economy-plugin.hook-internal-economy-into-vault")) {
			getServer().getServicesManager().register(Economy.class, new Economy_HyperConomy(), this, ServicePriority.Highest);
			this.getLogger().info("[HyperConomy]Internal economy hooked into Vault.");
		}
	}
	@Override
	public void unhookExternalEconomy() {
		if (!vaultInstalled) {
			return;
		}
	    RegisteredServiceProvider<Economy> eco = getServer().getServicesManager().getRegistration(Economy.class);
	    if (eco != null) {
	    	Economy registeredEconomy = eco.getProvider();
	    	if (registeredEconomy != null && registeredEconomy.getName().equalsIgnoreCase("HyperConomy")) {
		        getServer().getServicesManager().unregister(eco.getProvider());
		        this.getLogger().info("[HyperConomy]Internal economy unhooked from Vault.");
	    	}
	    }
	}
	@Override
	public void setupExternalEconomy() {
		if (!useExternalEconomy || !vaultInstalled) {return;}
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider == null) {
			useExternalEconomy = false;
			return;
		}
		economy = economyProvider.getProvider();
		if (economy == null) {
			useExternalEconomy = false;
			return;
		}
		if (economy.getName().equalsIgnoreCase("HyperConomy")) {
			useExternalEconomy = false;
			return;
		}
	}
	@Override
	public boolean useExternalEconomy() {
		return useExternalEconomy;
	}
	public Economy getEconomy() {
		if (economy == null) {
			setupExternalEconomy();
		}
		return economy;
	}

	@Override
	public String getEconomyName() {
		if (economy != null && useExternalEconomy) {
			return economy.getName();
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
		tasks.put(taskCounter.getAndIncrement(), t);
		return taskCounter.get();
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



	@SuppressWarnings("deprecation")
	@Override
	public void kickPlayer(HyperPlayer hp, String message) {
		Player p = Bukkit.getPlayer(hp.getName());
		if (p != null) {
			p.kickPlayer(message);
		}
	}



	@SuppressWarnings("deprecation")
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
	public SimpleLocation getTargetLocation(HyperPlayer hp) {
		try {
			Player p = Bukkit.getPlayer(hp.getName());
			if (p == null) return null;
			Location l = p.getTargetBlock(null, 500).getLocation();
			SimpleLocation sl = new SimpleLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
			return sl;
		} catch (Exception e) {
			return null;
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	public SimpleLocation getLocationBeforeTargetLocation(HyperPlayer hp) {
		try {
			Player p = Bukkit.getPlayer(hp.getName());
			if (p == null) return null;
			List<Block> ltb = p.getLastTwoTargetBlocks(null, 500);
			Block b = ltb.get(0);
			Location l = b.getLocation();
			SimpleLocation sl = new SimpleLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
			return sl;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean isLoaded(SimpleLocation l) {
		Location loc = BukkitCommon.getLocation(l);
		return loc.getChunk().isLoaded();
	}
		
	@Override
	public void load(SimpleLocation l) {
		Location loc = BukkitCommon.getLocation(l);
		loc.getChunk().load();
	}
	


	@Override
	public BukkitConnector getConnector() {
		return this;
	}



	@Override
	public SimpleLocation getLocation(HyperPlayer hp) {
		@SuppressWarnings("deprecation")
		Location l = Bukkit.getPlayer(hp.getName()).getLocation();
		return new SimpleLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
	}
	
	
	
	@Override
	public boolean conflictsWith(SerializableEnchantment e1, SerializableEnchantment e2) {
		Enchantment ench1 = Enchantment.getByName(e1.getEnchantmentName());
		Enchantment ench2 = Enchantment.getByName(e2.getEnchantmentName());
		return ench1.conflictsWith(ench2);
	}
	
	@Override
	public boolean canEnchantItem(SerializableItemStack item) {
		ItemStack s = BukkitCommon.getItemStack(item);
		for (Enchantment enchant:Enchantment.values()) {
			if (enchant.canEnchantItem(s)) return true;
		}
		return false;
	}


	

	@Override
	public SerializableInventory getInventory(HyperPlayer hp) {
		return BukkitCommon.getInventory(hp);
	}

	@Override
	public SerializableInventory getChestInventory(SimpleLocation l) {
		return BukkitCommon.getChestInventory(l);
	}

	@Override
	public void setInventory(SerializableInventory inventory) {
		BukkitCommon.setInventory(inventory);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int getHeldItemSlot(HyperPlayer hp) {
		Player p = Bukkit.getPlayer(hp.getName());
		return p.getInventory().getHeldItemSlot();
	}

	@SuppressWarnings("deprecation")
	@Override
	public SerializableItemStack getItem(HyperPlayer hp, int slot) {
		Player p = Bukkit.getPlayer(hp.getName());
		return BukkitCommon.getSerializableItemStack(p.getInventory().getItem(slot));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setItem(HyperPlayer hp, SerializableItemStack item, int slot) {
		Player p = Bukkit.getPlayer(hp.getName());
		p.getInventory().setItem(slot, BukkitCommon.getItemStack(item));
	}
	






	@Override
	public String applyColor(String message) {
		return BukkitCommon.applyColor(message);
	}

	@Override
	public void logInfo(String message) {
		Logger log = Logger.getLogger("Minecraft");
		log.info(message);
	}


	@Override
	public void logSevere(String message) {
		Logger log = Logger.getLogger("Minecraft");
		log.severe(message);
	}







	
	
	
	
	
	
	
	
	

	@Override
	public boolean isSneaking(HyperPlayer hp) {
		Player p = BukkitCommon.getPlayer(hp);
		return p.isSneaking();
	}

	@Override
	public boolean isInCreativeMode(HyperPlayer hp) {
		Player p = BukkitCommon.getPlayer(hp);
		return (p.getGameMode() == GameMode.CREATIVE) ? true:false;
	}



	@Override
	public ArrayList<HyperPlayer> getOnlinePlayers() {
		ArrayList<HyperPlayer> onlinePlayers = new ArrayList<HyperPlayer>();
		for (World world : Bukkit.getWorlds()) {
			for (Player p:world.getPlayers()) {
				onlinePlayers.add(HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(p.getName()));
			}
		}
		return onlinePlayers;
	}



	@Override
	public HyperPlayer getPlayer(UUID uuid) {
		OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
		if (!playerExists(uuid)) return null;
		HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(op.getName());
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
	public void teleport(HyperPlayer hp, SimpleLocation sl) {
		Player p = BukkitCommon.getPlayer(hp);
		Location l = BukkitCommon.getLocation(sl);
		p.teleport(l);
	}



	@SuppressWarnings("deprecation")
	@Override
	public void sendMessage(HyperPlayer hp, String message) {
		Player p = Bukkit.getPlayer(hp.getName());
		runTask(new Messager(p,message));
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
			p.sendMessage(m);
		}
	}











	@Override
	public ChestShop getChestShop(SimpleLocation location) {
		if (!BukkitCommon.isChestShop(location, true)) return null;
		return new ChestShop(location);
	}



	@Override
	public String removeColor(String text) {
		return ChatColor.stripColor(text);
	}



	@Override
	public HSign getSign(SimpleLocation location) {
		if (location == null) return null;
		Block b = BukkitCommon.getLocation(location).getBlock();
		if (b != null && (b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.WALL_SIGN))) {
			Sign s = (Sign) b.getState();
			boolean isWallSign = (b.getType().equals(Material.WALL_SIGN)) ? true:false;
			HSign sign = new HSign(location, s.getLines(), isWallSign);
			return sign;
		}
		return null;
	}
	
	@Override
	public void setSign(HSign sign) {
		Sign s = BukkitCommon.getSign(sign.getLocation());
		s.setLine(0, applyColor(sign.getLine(0)));
		s.setLine(1, applyColor(sign.getLine(1)));
		s.setLine(2, applyColor(sign.getLine(2)));
		s.setLine(3, applyColor(sign.getLine(3)));
	}

	@Override
	public HBlock getAttachedBlock(HSign sign) {
		Block b = BukkitCommon.getBlock(sign.getLocation());
		org.bukkit.material.Sign msign = (org.bukkit.material.Sign) b.getState().getData();
		BlockFace attachedface = msign.getAttachedFace();
		Block attachedblock = b.getRelative(attachedface);
		return BukkitCommon.getBlock(attachedblock);
	}



	@Override
	public boolean isChest(SimpleLocation l) {
		BlockState b = BukkitCommon.getBlock(l).getState();
		return (b instanceof Chest) ? true:false;
	}



	@Override
	public boolean canHoldChestShopSign(SimpleLocation l) {
		Block b = BukkitCommon.getBlock(l);
		Material m = b.getType();
		if (m == Material.ICE || m == Material.LEAVES || m == Material.SAND || m == Material.GRAVEL || m == Material.SIGN || m == Material.SIGN_POST || m == Material.TNT) {
			return false;
		}
		return true;
	}



	@Override
	public void updateSign(HSign sign) {
		Sign s = BukkitCommon.getSign(sign.getLocation());
		if (s != null) s.update();
	}



	@Override
	public boolean isTransactionSign(SimpleLocation l) {
		return BukkitCommon.isTransactionSign(l);
	}

	@Override
	public boolean isInfoSign(SimpleLocation l) {
		return BukkitCommon.isInfoSign(l);
	}

	@Override
	public boolean isChestShopSign(SimpleLocation l) {
		return BukkitCommon.isChestShopSign(l);
	}
	
	@Override
	public boolean isChestShop(SimpleLocation l, boolean includeSign) {
		return BukkitCommon.isChestShop(l, includeSign);
	}






	
}
