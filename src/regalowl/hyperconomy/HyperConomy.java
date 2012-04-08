package regalowl.hyperconomy;



import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HyperConomy extends JavaPlugin{
	

    //VARIABLES**********************************************************************
	

	//Reused Objects
	private Transaction tran;
	private Calculation calc;
	private Enchant ench;
	private Message m;
	private Log l;
	private Shop s;
	private Account acc;
	
    //VARIABLES**********************************************************************
	
	
	
    //VAULT**********************************************************************
	
    private Logger log = Logger.getLogger("Minecraft");
    private Vault vault = null;
    private Economy economy;
	
    //VAULT**********************************************************************
    

    
    @Override
    public void onEnable() {
    	
    	//Stores the new YamlFile as yaml.
    	YamlFile yam = new YamlFile();	
    	yam.YamlEnable();    	
    	yaml = yam;
    	
    	
    	requestbuffer = false;
    	bufferactive = false;
    	logsize = yaml.getLog().getKeys(true).size();
    	
    	lock = false;
    	
    	
    	shopinterval = yaml.getConfig().getLong("config.shopcheckinterval");
    	loginterval = yaml.getConfig().getLong("config.logwriteinterval");
    	saveinterval = yaml.getConfig().getLong("config.saveinterval");
    	
    	//shopmessage1 = yaml.getConfig().getString("config.shopmessage1");
    	//shopmessage2 = yaml.getConfig().getString("config.shopmessage2");
    	
    	//Creates the shop from the config.
    	s = new Shop(this);
    	
    	//Loads command messages.
    	m = new Message();
    	
    	//Loads the log.
    	l = new Log(this, logsize);
    	
    	
    	//Reused Objects
    	tran = new Transaction();
    	calc = new Calculation();
    	ench = new Enchant();
    	acc = new Account();
    		
    	
    	
    	
        //VAULT**********************************************************************
    	Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
        if(x != null & x instanceof Vault) {
        	
        	this.setupEconomy();
            vault = (Vault) x;
            log.info(String.format("[%s] Hooked %s %s", getDescription().getName(), vault.getDescription().getName(), vault.getDescription().getVersion()));
        } else {
            log.warning(String.format("[%s] Vault was _NOT_ found! Disabling plugin.", getDescription().getName()));
            getPluginLoader().disablePlugin(this);
            return;
        }
        
        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
    	
        //VAULT**********************************************************************
    	
    	
    	
        //Map name data to materials for /hb, /hv, and /hs command lookups
		Iterator<String> it = yaml.getItems().getKeys(true).iterator();
		while (it.hasNext()) {   			
			Object element = it.next();
			String elst = element.toString();    				
			if (elst.indexOf(".") == -1) {
				String i = yaml.getItems().getString(elst + ".information.id");
				String d = yaml.getItems().getString(elst + ".information.data");
				String ikey = i + ":" + d;
				namedata.put(ikey, elst);
			}
		}        
		
		Iterator<String> it2 = yaml.getEnchants().getKeys(true).iterator();
		while (it2.hasNext()) {   			
			Object element2 = it2.next();
			String elst2 = element2.toString();    				
			if (elst2.indexOf(".") == -1) {
				String n = yaml.getEnchants().getString(elst2 + ".information.name");
				enchantdata.put(n, elst2.substring(0, elst2.length() - 1));
			}
		}        
		
		
		
		startshopCheck();
		startSave();
		//startBuffer();
		

		
		
		log.info("HyperConomy has been successfully enabled!");
		
    }
    
    
    
    
    
    
    
    
    @Override
    public void onDisable() {
    	stopshopCheck();
    	stopSave();
    	stopBuffer();
    	l.saveBuffer();
	
    	//Saves config and items files.
        yaml.saveYamls();
        log.info("HyperConomy has been disabled!");
    }
    
    
    
    
  //VAULT**********************************************************************
    
    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    
    
  //VAULT**********************************************************************



    
    
    
    
    //COMMANDS**********************************************************************

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	

    	
    	if (cmd.getName().equalsIgnoreCase("lockshop")) {
    		try {
    			if (args.length == 0) {	
    				if (lock) {
    					
    					lock = false;
    					//loginterval = logintervalold;
		    			//stopBuffer();
		    			checkBuffer();
		    			startshopCheck();
		    			startSave();
		    			sender.sendMessage(ChatColor.GOLD + "The global shop has been unlocked!");
    					return true;
    				} else if (!lock) {
    					
    					lock = true;
    					//loginterval = 1;
    					
    					stopshopCheck();
		    			stopBuffer();
		    			l.saveBuffer();
		    			stopSave();
		    			yaml.saveYamls();
		    			sender.sendMessage(ChatColor.GOLD + "The global shop has been locked!");
		    			//checkBuffer();
    					return true;
    				}
    			} else {
    				sender.sendMessage(ChatColor.RED + "Invalid parameters.  Use /lockshop");
    				return true;
    			}
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.RED + "Invalid Usage.  Use /lockshop");
    			return true;
    		}
    	}  else if (cmd.getName().equalsIgnoreCase("reloadfiles")) {
			try {
				
				if (lock) {
					YamlFile yam = new YamlFile();	
			    	yam.YamlEnable();    	
			    	yaml = yam;
			    	
			    	shopinterval = yaml.getConfig().getLong("config.shopcheckinterval");
			    	loginterval = yaml.getConfig().getLong("config.logwriteinterval");
			    	saveinterval = yaml.getConfig().getLong("config.saveinterval");

			    	
			    	s.clearAll();
			    	s = new Shop(this);
			    	
			    	
			    	
			    	
			    	
			    	namedata.clear();
			    	enchantdata.clear();
			    	
			        //Map name data to materials for /hb, /hv, and /hs command lookups
					Iterator<String> it = yaml.getItems().getKeys(true).iterator();
					while (it.hasNext()) {   			
						Object element = it.next();
						String elst = element.toString();    				
						if (elst.indexOf(".") == -1) {
							String i = yaml.getItems().getString(elst + ".information.id");
							String d = yaml.getItems().getString(elst + ".information.data");
							String ikey = i + ":" + d;
							namedata.put(ikey, elst);
						}
					}        		
					Iterator<String> it2 = yaml.getEnchants().getKeys(true).iterator();
					while (it2.hasNext()) {   			
						Object element2 = it2.next();
						String elst2 = element2.toString();    				
						if (elst2.indexOf(".") == -1) {
							String n = yaml.getEnchants().getString(elst2 + ".information.name");
							enchantdata.put(n, elst2.substring(0, elst2.length() - 1));
						}
					}   
					
					sender.sendMessage(ChatColor.GOLD + "All files have been reloaded.");
				
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "You must first lock the shop!");
				}
				
				
				return true;
			} catch (Exception e) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /reloadymls");
				return true;
			}
    	}
    	
    	
    	
    	
    	
    	if (!lock) {
    	
	    	if (cmd.getName().equalsIgnoreCase("setinterval")) {
	    		try {
	    		
		    		if (args.length == 2) {
		    			
		    			if (args[0].equalsIgnoreCase("shop")) {
			    			long inter = Long.parseLong(args[1]);
			    			shopinterval = inter;
			    			yaml.getConfig().set("config.shopcheckinterval", shopinterval);
			    			//yaml.saveYamls();
			    			stopshopCheck();
			    			startshopCheck();
			    			sender.sendMessage(ChatColor.GOLD + "Shop check interval set!");
		    			} else if (args[0].equalsIgnoreCase("log")) {
		    				loginterval = Long.parseLong(args[1]);
			    			yaml.getConfig().set("config.logwriteinterval", loginterval);		    		
			    			stopBuffer();
			    			checkBuffer();	
			    			sender.sendMessage(ChatColor.GOLD + "Log write interval set!");
		    				
			    			
		    			} else if (args[0].equalsIgnoreCase("save")) {
		    				
		    				saveinterval = Long.parseLong(args[1]);
			    			yaml.getConfig().set("config.saveinterval", saveinterval);		    		
			    			stopSave();
			    			startSave();	
			    			sender.sendMessage(ChatColor.GOLD + "Save interval set!");
		    				
		    			} else {
		    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setinterval ['shop'/'log'/'save'] [interval]");
		    			}
		    			
		    		} else {
		    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setinterval ['shop'/'log'/'save'] [interval]");
		    		}
		    		return true;
	    		} catch (Exception e) {
	    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Usage.  Use /setinterval ['shop'/'log'/'save'] [interval]");
	    			return true;
	    		}
	    		
	    	}
	    	
	    	
	    	
	    	
	    	
	        Cmd commandhandler = new Cmd(this, economy, m, tran, calc, ench, l, s, acc);
	    	boolean result = commandhandler.handleCommand(sender, cmd, label, args);
	    	
	    	checkBuffer();
	
	        return result;
        
    	} else {
    		sender.sendMessage(ChatColor.RED + "The global shop is currently locked!");
    		return true;
    	}
    	
    }

    //COMMANDS**********************************************************************

    
    
    //Threading related functions.
    public void startshopCheck() {
		shoptaskid = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		    	s.shopThread();
		    }
		}, shopinterval, shopinterval);
    }
    
    
    public void stopshopCheck() {
    	this.getServer().getScheduler().cancelTask(shoptaskid);
    }
    
    
    public void startBuffer() {
    	bufferactive = true;
		buffertaskid = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		    	//Bukkit.broadcastMessage("requestbuffer=" + requestbuffer + " bufferactive=" + bufferactive);
		    	if (!requestbuffer) {
		    		stopBuffer();
		    	} else {
		    		l.writelogThread();
		    		//Bukkit.broadcastMessage("Buffer thread");
		    	}
		    }
		}, loginterval, loginterval);
    }
    
    public void stopBuffer() {
    	this.getServer().getScheduler().cancelTask(buffertaskid);
    	bufferactive = false;
    }
    
    
    public void checkBuffer() {
    	if (requestbuffer && !bufferactive) {
    		startBuffer();
    	}
    }
    
    public void startSave() {
		savetaskid = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		    	yaml.saveYamls();
		    }
		}, saveinterval, saveinterval);
    }
    
    public void stopSave() {
    	this.getServer().getScheduler().cancelTask(savetaskid);
    }
    
    //Getters and Setters
    
    public long getshopInterval() {
    	return shopinterval;
    }
    public long getlogInterval() {
    	return loginterval;
    }
    public long getsaveInterval() {
    	return saveinterval;
    }
    public YamlFile getYaml() {
    	return yaml;
    }
    public int getlogSize() {
    	return logsize;
    }
    public String getnameData(String key) {
    	return namedata.get(key);
    }
    public String getenchantData(String key) {
    	return enchantdata.get(key);
    }

    
    
    public void setrequestBuffer(boolean bufferstate) {
    	requestbuffer = bufferstate;
    }
    public void setlogSize(int size) {
    	logsize = size;
    }
    
    
    
    
    
    
    
    //Fields
    
    
	private long shopinterval;
	private long loginterval;	
	private long saveinterval;
	
	private YamlFile yaml;
	private boolean lock;
	
	private boolean requestbuffer;
	private boolean bufferactive;
	private int logsize;
	
	private int shoptaskid;
	private int buffertaskid;
	private int savetaskid;
	
	
	//private String name;
	
	
	
	
	//Stores all item and enchantment names for reverse lookups.
	private HashMap <String, String> namedata = new HashMap<String, String>();
	private HashMap <String, String> enchantdata = new HashMap<String, String>();
	
}


