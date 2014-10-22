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
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.util.Economy_HyperConomy;
import regalowl.hyperconomy.util.SimpleLocation;

public class BukkitConnector extends JavaPlugin implements MineCraftConnector, Listener {

	private HashMap<String, HyperCommand> commands = new HashMap<String, HyperCommand>();
	private ConcurrentHashMap<Long, BukkitTask> tasks = new ConcurrentHashMap<Long, BukkitTask>();
	private AtomicLong taskCounter = new AtomicLong();
	private HyperConomy hc;
	private BukkitInventory bi;
	private BukkitListener bl;
	
	private boolean vaultInstalled;
	private boolean useExternalEconomy;
	private Economy economy;
	
	public BukkitConnector() {
		new HyperConomy(this);
		this.bi = new BukkitInventory();
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
		Location loc = getLocation(l);
		return loc.getChunk().isLoaded();
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
		ItemStack s = bi.getItemStack(item);
		for (Enchantment enchant:Enchantment.values()) {
			if (enchant.canEnchantItem(s)) return true;
		}
		return false;
	}


	

	@Override
	public SerializableInventory getInventory(HyperPlayer hp) {
		return bi.getInventory(hp);
	}

	@Override
	public SerializableInventory getChestInventory(SimpleLocation l) {
		return bi.getChestInventory(l);
	}

	@Override
	public void setInventory(SerializableInventory inventory) {
		bi.setInventory(inventory);
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
		return bi.getSerializableItemStack(p.getInventory().getItem(slot));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setItem(HyperPlayer hp, SerializableItemStack item, int slot) {
		Player p = Bukkit.getPlayer(hp.getName());
		p.getInventory().setItem(slot, bi.getItemStack(item));
	}
	






	@Override
	public String applyColor(String message) {
		message = message.replace("&0", ChatColor.BLACK + "");
		message = message.replace("&1", ChatColor.DARK_BLUE + "");
		message = message.replace("&2", ChatColor.DARK_GREEN + "");
		message = message.replace("&3", ChatColor.DARK_AQUA + "");
		message = message.replace("&4", ChatColor.DARK_RED + "");
		message = message.replace("&5", ChatColor.DARK_PURPLE + "");
		message = message.replace("&6", ChatColor.GOLD + "");
		message = message.replace("&7", ChatColor.GRAY + "");
		message = message.replace("&8", ChatColor.DARK_GRAY + "");
		message = message.replace("&9", ChatColor.BLUE + "");
		message = message.replace("&a", ChatColor.GREEN + "");
		message = message.replace("&b", ChatColor.AQUA + "");
		message = message.replace("&c", ChatColor.RED + "");
		message = message.replace("&d", ChatColor.LIGHT_PURPLE + "");
		message = message.replace("&e", ChatColor.YELLOW + "");
		message = message.replace("&f", ChatColor.WHITE + "");
		message = message.replace("&k", ChatColor.MAGIC + "");
		message = message.replace("&l", ChatColor.BOLD + "");
		message = message.replace("&m", ChatColor.STRIKETHROUGH + "");
		message = message.replace("&n", ChatColor.UNDERLINE + "");
		message = message.replace("&o", ChatColor.ITALIC + "");
		message = message.replace("&r", ChatColor.RESET + "");
		return message;
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
	public boolean isInCreativeMode(HyperPlayer hp) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		
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



	protected Location getLocation(SimpleLocation l) {
		return new Location(Bukkit.getWorld(l.getWorld()), l.getX(), l.getY(), l.getZ());
	}

	protected SimpleLocation getLocation(Location l) {
		return new SimpleLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
	}



	@Override
	public boolean isSneaking(HyperPlayer hp) {
		// TODO Auto-generated method stub
		return false;
	}


	
}
