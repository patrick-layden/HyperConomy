package regalowl.hyperconomy;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.command.CommandData;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.event.minecraft.HyperPlayerJoinEvent;
import regalowl.hyperconomy.event.minecraft.HyperSignChangeEvent;
import regalowl.hyperconomy.util.Economy_HyperConomy;
import regalowl.hyperconomy.util.SimpleLocation;

public class BukkitConnector extends JavaPlugin implements MineCraftConnector, Listener {

	private HashMap<String, HyperCommand> commands = new HashMap<String, HyperCommand>();
	private ConcurrentHashMap<Long, BukkitTask> tasks = new ConcurrentHashMap<Long, BukkitTask>();
	private AtomicLong taskCounter = new AtomicLong();
	private Plugin plugin;
	private HyperConomy hc;
	
	private boolean vaultInstalled;
	private boolean useExternalEconomy;
	private Economy economy;
	
	public BukkitConnector() {
		new HyperConomy(this);
		this.plugin = this;
	}
	
	
	
	//JavaPlugin Bukkit methods
	@Override
	public void onLoad() {
		if (hc == null) {
			hc = new HyperConomy(this);
		}
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
		HandlerList.unregisterAll(plugin);
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
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent event) {
		HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer());
		Location l = event.getBlock().getLocation();
		SimpleLocation sl = new SimpleLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
		HyperConomy.hc.getHyperEventHandler().fireEvent(new HyperSignChangeEvent(event.getLines(), sl, hp));
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		HyperPlayer hp = HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(event.getPlayer());
		HyperConomy.hc.getHyperEventHandler().fireEvent(new HyperPlayerJoinEvent(hp));
	}



	@Override
	public void kickPlayer(String name, String message) {
		Player p = Bukkit.getPlayer(name);
		if (p != null) {
			p.kickPlayer(message);
		}
	}



	@Override
	public boolean hasPermission(String name, String permission) {
		Player p = Bukkit.getPlayer(name);
		if (p != null) {
			return p.hasPermission(permission);
		}
		return false;
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




	
}
