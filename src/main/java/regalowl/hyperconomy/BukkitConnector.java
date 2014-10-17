package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.command.CommandData;
import regalowl.hyperconomy.command.HyperCommand;
import regalowl.hyperconomy.event.minecraft.HyperPlayerJoinEvent;
import regalowl.hyperconomy.event.minecraft.HyperSignChangeEvent;
import regalowl.hyperconomy.serializable.SerializableBookMeta;
import regalowl.hyperconomy.serializable.SerializableColor;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableEnchantmentStorageMeta;
import regalowl.hyperconomy.serializable.SerializableFireworkEffect;
import regalowl.hyperconomy.serializable.SerializableFireworkEffectMeta;
import regalowl.hyperconomy.serializable.SerializableFireworkMeta;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemMeta;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.serializable.SerializableLeatherArmorMeta;
import regalowl.hyperconomy.serializable.SerializableMapMeta;
import regalowl.hyperconomy.serializable.SerializablePotionEffect;
import regalowl.hyperconomy.serializable.SerializablePotionMeta;
import regalowl.hyperconomy.serializable.SerializableSkullMeta;
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



	@SuppressWarnings("deprecation")
	@Override
	public void kickPlayer(String name, String message) {
		Player p = Bukkit.getPlayer(name);
		if (p != null) {
			p.kickPlayer(message);
		}
	}



	@SuppressWarnings("deprecation")
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



	@Override
	@SuppressWarnings("deprecation")
	public SerializableInventory getInventory(HyperPlayer hp) {
		ArrayList<SerializableItemStack> items = new ArrayList<SerializableItemStack>();
		Player p = Bukkit.getPlayer(hp.getName());
		Inventory i = p.getInventory();
		int size = i.getSize();
		int heldSlot = p.getInventory().getHeldItemSlot();
		for (int c = 0; c < size; c++) {
	        items.add(getSerializableItemStack(i.getItem(c)));
		}
		SerializableInventory si = new SerializableInventory(items, heldSlot);
		return si;
	}



	@SuppressWarnings("deprecation")
	@Override
	public void setInventory(HyperPlayer hp, SerializableInventory inventory) {
		Player p = Bukkit.getPlayer(hp.getName());
		p.getInventory().setHeldItemSlot(inventory.getHeldSlot());
		ArrayList<SerializableItemStack> cInventory = getInventory(hp).getItems();
		ArrayList<SerializableItemStack> nInventory = inventory.getItems();
		if (cInventory.size() != nInventory.size()) return;
		Inventory inv = p.getInventory();
		for (int i = 0; i < nInventory.size(); i++) {
			inv.setItem(i, getItemStack(nInventory.get(i)));
		}
	}
	
	@SuppressWarnings("deprecation")
	private SerializableItemStack getSerializableItemStack(ItemStack s) {
		if (s == null) return null;
        String material = s.getType().toString();
        short durability = s.getDurability();
        byte data = s.getData().getData(); 
        if (s.hasItemMeta()) {
        	ItemMeta im = s.getItemMeta();
            String displayName = im.getDisplayName();
            List<String> lore = im.getLore();
            List<SerializableEnchantment> enchantments = new ArrayList<SerializableEnchantment>();
            Map<Enchantment, Integer> enchants = im.getEnchants();
    		Iterator<Enchantment> it = enchants.keySet().iterator();
    		while (it.hasNext()) {
    			Enchantment e = it.next();
    			int lvl = enchants.get(e);
    			enchantments.add(new SerializableEnchantment(e.getName(), lvl));
    		}
    		SerializableItemMeta itemMeta = null;
        	if (im instanceof EnchantmentStorageMeta) {
        		EnchantmentStorageMeta sItemMeta = (EnchantmentStorageMeta)im;
        		List<SerializableEnchantment> storedEnchantments = new ArrayList<SerializableEnchantment>();
    			Map<Enchantment, Integer> stored = sItemMeta.getStoredEnchants();
    			Iterator<Enchantment> iter = stored.keySet().iterator();
    			while (iter.hasNext()) {
    				Enchantment e = iter.next();
    				int lvl = enchants.get(e);
    				storedEnchantments.add(new SerializableEnchantment(e.getName(), lvl));
    			}
        		itemMeta = new SerializableEnchantmentStorageMeta(displayName, lore, enchantments, storedEnchantments);
        	} else if (im instanceof BookMeta) {
        		BookMeta sItemMeta = (BookMeta)im;
        		itemMeta = new SerializableBookMeta(displayName, lore, enchantments, sItemMeta.getAuthor(), sItemMeta.getPages(), sItemMeta.getTitle());
        	} else if (im instanceof FireworkEffectMeta) {
        		FireworkEffectMeta sItemMeta = (FireworkEffectMeta)im;
        		FireworkEffect fe = sItemMeta.getEffect();
        		ArrayList<SerializableColor> colors = new ArrayList<SerializableColor>();
        		for (Color color:fe.getColors()) {
        			colors.add(new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
        		}
        		ArrayList<SerializableColor> fadeColors = new ArrayList<SerializableColor>();
        		for (Color color:fe.getFadeColors()) {
        			fadeColors.add(new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
        		}
        		SerializableFireworkEffect sfe = new SerializableFireworkEffect(colors, fadeColors, fe.getType().toString(), fe.hasFlicker(), fe.hasTrail());
        		itemMeta = new SerializableFireworkEffectMeta(displayName, lore, enchantments, sfe);
        	} else if (im instanceof FireworkMeta) {
        		FireworkMeta sItemMeta = (FireworkMeta)im;
        		ArrayList<SerializableFireworkEffect> fireworkEffects = new ArrayList<SerializableFireworkEffect>();
    			for (FireworkEffect fe:sItemMeta.getEffects()) {
	        		ArrayList<SerializableColor> colors = new ArrayList<SerializableColor>();
	        		for (Color color:fe.getColors()) {
	        			colors.add(new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
	        		}
	        		ArrayList<SerializableColor> fadeColors = new ArrayList<SerializableColor>();
	        		for (Color color:fe.getFadeColors()) {
	        			fadeColors.add(new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
	        		}
	        		fireworkEffects.add(new SerializableFireworkEffect(colors, fadeColors, fe.getType().toString(), fe.hasFlicker(), fe.hasTrail()));
    			}
        		itemMeta = new SerializableFireworkMeta(displayName, lore, enchantments, fireworkEffects, sItemMeta.getPower());
        	} else if (im instanceof LeatherArmorMeta) {
        		LeatherArmorMeta sItemMeta = (LeatherArmorMeta)im;
        		Color color = sItemMeta.getColor();
        		itemMeta = new SerializableLeatherArmorMeta(displayName, lore, enchantments, new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
        	} else if (im instanceof PotionMeta) {
        		PotionMeta sItemMeta = (PotionMeta)im;
        		ArrayList<SerializablePotionEffect> potionEffects = new ArrayList<SerializablePotionEffect>();
        		for (PotionEffect pe:sItemMeta.getCustomEffects()) {
        			potionEffects.add(new SerializablePotionEffect(pe.getType().toString(), pe.getAmplifier(), pe.getDuration(), pe.isAmbient()));
        		}
        		itemMeta = new SerializablePotionMeta(displayName, lore, enchantments, potionEffects);
        	} else if (im instanceof SkullMeta) {
        		SkullMeta sItemMeta = (SkullMeta)im;
        		itemMeta = new SerializableSkullMeta(displayName, lore, enchantments, sItemMeta.getOwner());
        	} else if (im instanceof MapMeta) {
        		MapMeta sItemMeta = (MapMeta)im;
        		itemMeta = new SerializableMapMeta(displayName, lore, enchantments, sItemMeta.isScaling());
        	} else {
        		itemMeta = new SerializableItemMeta(displayName, lore, enchantments);
        	}
        	return new SerializableItemStack(itemMeta, material, durability, data);
        }
        return new SerializableItemStack(null, material, durability, data);
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack getItemStack(SerializableItemStack sis) {
		if (sis == null) return null;
        ItemStack item = new ItemStack(Material.matchMaterial(sis.getMaterial()));
        item.setAmount(1);
        item.setDurability(sis.getDurability());
        item.getData().setData(sis.getData());
        if (sis.getItemMeta() != null) {
        	SerializableItemMeta sim = sis.getItemMeta();
        	ItemMeta itemMeta = item.getItemMeta();
        	itemMeta.setDisplayName(sim.getDisplayName());
        	itemMeta.setLore(sim.getLore());
    		for (SerializableEnchantment se:sim.getEnchantments()) {
    			itemMeta.addEnchant(Enchantment.getByName(se.getEnchantmentName()), se.getLvl(), true);
    		}
        	if (sim instanceof SerializableEnchantmentStorageMeta) {
        		SerializableEnchantmentStorageMeta sItemMeta = (SerializableEnchantmentStorageMeta)sim;
        		EnchantmentStorageMeta esm = (EnchantmentStorageMeta)itemMeta;
        		for (SerializableEnchantment se:sItemMeta.getStoredEnchantments()) {
        			esm.addStoredEnchant(Enchantment.getByName(se.getEnchantmentName()), se.getLvl(), true);
        		}
        	} else if (sim instanceof SerializableBookMeta) {
        		SerializableBookMeta sItemMeta = (SerializableBookMeta)sim;
        		BookMeta bm = (BookMeta)itemMeta;
        		bm.setPages(sItemMeta.getPages());
        		bm.setAuthor(sItemMeta.getAuthor());
        		bm.setTitle(sItemMeta.getTitle());
        	} else if (sim instanceof SerializableFireworkEffectMeta) {
        		SerializableFireworkEffectMeta sItemMeta = (SerializableFireworkEffectMeta)sim;
        		FireworkEffectMeta fem = (FireworkEffectMeta)itemMeta;
        		SerializableFireworkEffect sfe = sItemMeta.getEffect();
    			Builder fb = FireworkEffect.builder();
    			for (SerializableColor c:sfe.getColors()) {
    				fb.withColor(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
    			}
    			for (SerializableColor c:sfe.getFadeColors()) {
    				fb.withFade(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
    			}
    			fb.with(FireworkEffect.Type.valueOf(sfe.getType()));
    			fb.flicker(sfe.hasFlicker());
    			fb.trail(sfe.hasTrail());
    			fem.setEffect(fb.build());
        	} else if (sim instanceof SerializableFireworkMeta) {
        		SerializableFireworkMeta sItemMeta = (SerializableFireworkMeta)sim;
        		FireworkMeta fm = (FireworkMeta)itemMeta;
        		for (SerializableFireworkEffect sfe:sItemMeta.getEffects()) {
        			Builder fb = FireworkEffect.builder();
        			for (SerializableColor c:sfe.getColors()) {
        				fb.withColor(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
        			}
        			for (SerializableColor c:sfe.getFadeColors()) {
        				fb.withFade(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
        			}
        			fb.with(FireworkEffect.Type.valueOf(sfe.getType()));
        			fb.flicker(sfe.hasFlicker());
        			fb.trail(sfe.hasTrail());
        			fm.addEffect(fb.build());
        		}
        		fm.setPower(sItemMeta.getPower());
        	} else if (sim instanceof SerializableLeatherArmorMeta) {
        		SerializableLeatherArmorMeta sItemMeta = (SerializableLeatherArmorMeta)sim;
        		LeatherArmorMeta lam = (LeatherArmorMeta)itemMeta;
        		SerializableColor sc = sItemMeta.getColor();
        		lam.setColor(Color.fromRGB(sc.getRed(), sc.getGreen(), sc.getBlue()));
        	} else if (sim instanceof SerializablePotionMeta) {
        		SerializablePotionMeta sItemMeta = (SerializablePotionMeta)sim;
        		PotionMeta pm = (PotionMeta)itemMeta;
        		for (SerializablePotionEffect spe:sItemMeta.getPotionEffects()) {
        			PotionEffect pe = new PotionEffect(PotionEffectType.getByName(spe.getType()), spe.getDuration(), spe.getAmplifier(), spe.isAmbient());
        			pm.addCustomEffect(pe, true);
        		}
        	} else if (sim instanceof SerializableSkullMeta) {
        		SerializableSkullMeta sItemMeta = (SerializableSkullMeta)sim;
        		SkullMeta sm = (SkullMeta)itemMeta;
        		sm.setOwner(sItemMeta.getOwner());
        	} else if (sim instanceof SerializableMapMeta) {
        		SerializableMapMeta sItemMeta = (SerializableMapMeta)sim;
        		MapMeta mm = (MapMeta)itemMeta;
        		mm.setScaling(sItemMeta.isScaling());
        	}
        	item.setItemMeta(itemMeta);
        }
        return item;
	}



	
}
