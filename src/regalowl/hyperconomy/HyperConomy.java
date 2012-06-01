package regalowl.hyperconomy;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
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
	private ETransaction ench;
	private Message m;
	private Log l;
	private Shop s;
	private Account acc;
	private InfoSign isign;
	private Cmd commandhandler;
	private History hist;
	private Notify not;
	private TransactionSign tsign;
	private ChestShop cs;
	private UpdateSign us;
	
	
	public static HyperObject hyperobject;
	
    //VARIABLES**********************************************************************
	
	
	
    //VAULT**********************************************************************
	
    private Logger log = Logger.getLogger("Minecraft");
    private Vault vault = null;
    private Economy economy;
	
    //VAULT**********************************************************************
    

    
    @Override
    public void onEnable() {
    	
    	lock = false;
    	brokenfile = false;
    	
    	//Stores the new YamlFile as yaml.
    	YamlFile yam = new YamlFile(this);	
    	yam.YamlEnable();    	
    	yaml = yam;
    	
    	


    	if (!brokenfile) {
    		
    		
    		saveinterval = yaml.getConfig().getLong("config.saveinterval");

        	////////////////////For compatibility with previous versions of HyperConomy./////////////
        	Compatibility cb = new Compatibility();
        	cb.checkCompatibility(this);
        	

        	
        	//Creates the shop from the config.
        	s = new Shop(this);
        	
        	//Loads command messages.
        	m = new Message();
        	
        	//Loads the log.
        	l = new Log(this);
        	
        	
        	//Reused Objects
        	tran = new Transaction();
        	calc = new Calculation();
        	ench = new ETransaction();
        	acc = new Account();
        	commandhandler = new Cmd();
        	not = new Notify();
        	isign = new InfoSign();
        	hist = new History();
        	tsign = new TransactionSign();
        	us = new UpdateSign();
        		
        	
        	
        	
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
    		Iterator<String> it = yaml.getItems().getKeys(false).iterator();
    		while (it.hasNext()) {   			
    			String elst = it.next().toString();    				
    			String ikey = yaml.getItems().getString(elst + ".information.id") + ":" + yaml.getItems().getString(elst + ".information.data");
    			namedata.put(ikey, elst);
    		}        
    		
    		Iterator<String> it2 = yaml.getEnchants().getKeys(false).iterator();
    		while (it2.hasNext()) {   			
    			String elst2 = it2.next().toString();    				
    			enchantdata.put(yaml.getEnchants().getString(elst2 + ".information.name"), elst2.substring(0, elst2.length() - 1));
    		}        
    		
    		//Creates the names arraylist storing all item and enchantment names.
    		Iterator<String> it3 = yaml.getItems().getKeys(false).iterator();
    		while (it3.hasNext()) {   			  				
    			names.add(it3.next().toString());
    		}  
    		Iterator<String> it4 = yaml.getEnchants().getKeys(false).iterator();
    		while (it4.hasNext()) {   			
    			names.add(it4.next().toString());
    		}  
    		
    		

    		s.startshopCheck();
    		startSave();

    		//startBuffer();
    		
    		acc.setAccount(this, null, economy);
    		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
    		    public void run() {
    				//Sets up the global shop account if it doesn't already exist.
    				acc.checkshopAccount();
    		    }
    		}, 300L);

    		
    		
    		
    		isign.setinfoSign(this, calc, ench, tran);
    		hist.setHistory(this, calc, ench, isign);
    		hist.starthistoryLog();
    		
    		tsign.setTransactionSign(this, tran, calc, ench, l, acc, isign, not, economy, us);
    		
    		
    		
    		hyperobject = new HyperObject(this, yam, tran, calc, ench, m, l, s, acc, isign, commandhandler, hist, not, tsign, economy);
    		
    		cs = new ChestShop();
    		log.info("HyperConomy has been successfully enabled!");
    		
    		
    	}
		
    }
    
    
    
    
    
    
    
    
    @Override
    public void onDisable() {
    	if (s != null) {
        	s.stopshopCheck();
        	stopSave();
        	l.stopBuffer();
        	hist.stophistoryLog();
        	l.saveBuffer();
    	}

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
    				if (lock && !brokenfile) {
    					lock = false;
		    			l.checkBuffer();
		    			isign.checksignUpdate();
		    			s.startshopCheck();
		    			hist.starthistoryLog();
		    			startSave();
		    			sender.sendMessage(ChatColor.GOLD + "The global shop has been unlocked!");
    					return true;
    				} else if (!lock) {
    					lock = true;
    					s.stopshopCheck();
		    			l.stopBuffer();
		    			hist.stophistoryLog();
		    			isign.stopsignUpdate();
		    			isign.resetAll();
		    			l.saveBuffer();
		    			stopSave();
		    			yaml.saveYamls();
		    			sender.sendMessage(ChatColor.GOLD + "The global shop has been locked!");
    					return true;
    				} else {
    					sender.sendMessage(ChatColor.DARK_RED + "You must first fix your bad yml file!");
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
					YamlFile yam = new YamlFile(this);	
			    	yam.YamlEnable();    	
			    	yaml = yam;
			    	
			    	s.setshopInterval(yaml.getConfig().getLong("config.shopcheckinterval"));
			    	l.setlogInterval(yaml.getConfig().getLong("config.logwriteinterval"));
			    	saveinterval = yaml.getConfig().getLong("config.saveinterval");
			    	isign.setsignupdateInterval(yaml.getConfig().getLong("config.signupdateinterval"));

			    	
			    	s.clearAll();
			    	s = new Shop(this);
			    	
			    	isign.setinfoSign(this, calc, ench, tran);

			    	
			    	namedata.clear();
			    	enchantdata.clear();
			    	names.clear();
			    	
			        //Map name data to materials for /hb, /hv, and /hs command lookups
					Iterator<String> it = yaml.getItems().getKeys(false).iterator();
					while (it.hasNext()) {   			
						String elst = it.next().toString();    				
						String ikey = yaml.getItems().getString(elst + ".information.id") + ":" + yaml.getItems().getString(elst + ".information.data");
						namedata.put(ikey, elst);
					}        		
					Iterator<String> it2 = yaml.getEnchants().getKeys(false).iterator();
					while (it2.hasNext()) {   			
						String elst2 = it2.next().toString();    				
						enchantdata.put(yaml.getEnchants().getString(elst2 + ".information.name"), elst2.substring(0, elst2.length() - 1));
					}   
					//Creates the names arraylist storing all item and enchantment names.
					Iterator<String> it3 = yaml.getItems().getKeys(false).iterator();
					while (it3.hasNext()) {   			  				
						names.add(it3.next().toString().toLowerCase());
					}  
					Iterator<String> it4 = yaml.getEnchants().getKeys(false).iterator();
					while (it4.hasNext()) {   			
						names.add(it4.next().toString().toLowerCase());
					} 
					
					hyperobject = new HyperObject(this, yam, tran, calc, ench, m, l, s, acc, isign, commandhandler, hist, not, tsign, economy);
					
					sender.sendMessage(ChatColor.GOLD + "All files have been reloaded.");
				
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "You must first lock the shop!");
				}
				
				
				return true;
			} catch (Exception e) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /reloadfiles");
				return true;
			}
    	}
    	
    	
    	
    	
    	
    	if (!lock) {
    	
	    	if (cmd.getName().equalsIgnoreCase("setinterval")) {
	    		try {
	    		
		    		if (args.length == 2) {
		    			
		    			if (args[0].equalsIgnoreCase("shop")) {
			    			s.setshopInterval(Long.parseLong(args[1]));
			    			yaml.getConfig().set("config.shopcheckinterval", s.getshopInterval());
			    			s.stopshopCheck();
			    			s.startshopCheck();
			    			sender.sendMessage(ChatColor.GOLD + "Shop check interval set!");
		    			} else if (args[0].equalsIgnoreCase("log")) {
		    				l.setlogInterval(Long.parseLong(args[1]));
			    			yaml.getConfig().set("config.logwriteinterval", l.getlogInterval());		    		
			    			l.stopBuffer();
			    			l.checkBuffer();	
			    			sender.sendMessage(ChatColor.GOLD + "Log write interval set!");
		    				
			    			
		    			} else if (args[0].equalsIgnoreCase("save")) {
		    				
		    				saveinterval = Long.parseLong(args[1]);
			    			yaml.getConfig().set("config.saveinterval", saveinterval);		    		
			    			stopSave();
			    			startSave();	
			    			sender.sendMessage(ChatColor.GOLD + "Save interval set!");
		    				
			    			
		    			} else if (args[0].equalsIgnoreCase("sign")) {
		    				
		    				isign.setsignupdateInterval(Long.parseLong(args[1]));
			    			yaml.getConfig().set("config.signupdateinterval", isign.getsignupdateInterval());		    		
			    			isign.stopsignUpdate();
			    			isign.checksignUpdate();	
			    			sender.sendMessage(ChatColor.GOLD + "Sign update interval set!");
		    			} else {
		    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setinterval ['shop'/'log'/'save'/'sign'] [interval]");
		    			}
		    			
		    		} else {
		    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setinterval ['shop'/'log'/'save'/'sign'] [interval]");
		    		}
		    		return true;
	    		} catch (Exception e) {
	    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Usage.  Use /setinterval ['shop'/'log'/'save'/'sign'] [interval]");
	    			return true;
	    		}
	    		
	    	}
	    	
	    	
	    	
	    	
	    	
	        commandhandler.setCmd(this, economy, m, tran, calc, ench, l, s, acc, isign, not);
	    	boolean result = commandhandler.handleCommand(sender, cmd, label, args);
	    	
	    	l.checkBuffer();
	
	        return result;
        
    	} else {
    		sender.sendMessage(ChatColor.RED + "The global shop is currently locked!");
    		return true;
    	}
    	
    }

    //COMMANDS**********************************************************************

    
    

    

    
    public void startSave() {
		savetaskid = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		    	if (!brokenfile) {
		    		yaml.saveYamls();
		    	}
		    }
		}, saveinterval, saveinterval);
    }
    
    public void stopSave() {
    	this.getServer().getScheduler().cancelTask(savetaskid);
    }
    
    //Getters and Setters
    


    public long getsaveInterval() {
    	return saveinterval;
    }
    public YamlFile getYaml() {
    	return yaml;
    }

    public String getnameData(String key) {
    	return namedata.get(key);
    }
    public String getenchantData(String key) {
    	return enchantdata.get(key);
    }

    
    


    
    
    public void ymlCheck(int failcount) {
    	if (failcount == 0) {
    		brokenfile = false;
    	} else {
        	brokenfile = true;
        	if (!lock) {
        		if (s == null) {
        			log.info("Bad YML files detected, disabling HyperConomy!");
        			Bukkit.getPluginManager().disablePlugin(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy"));
        			return;
        		}
				lock = true;
				s.stopshopCheck();
		    	l.stopBuffer();
		    	hist.stophistoryLog();
		    	isign.stopsignUpdate();
		    	isign.resetAll();
		    	l.saveBuffer();
		    	stopSave();	
    		}
    	}
    }
    
    
    
    
    
    
    
	public String fixName(String nam) {
		int c = 0;
		int l = names.size();
		while (c < l) {
			if (names.get(c).equalsIgnoreCase(nam)) {
				String newname = names.get(c);
				return newname;
			}
			c++;
		}
		return nam;
	}

	

	public String fixsName(String nam) {
		String name = nam;
		int c = 0;
		int l = getYaml().getShops().getKeys(false).size();
		Object names[] = getYaml().getShops().getKeys(false).toArray();
		while (c < l) {
			if (names[c].toString().equalsIgnoreCase(name)) {
				name = names[c].toString();
				return name;
			}
			c++;
		}
		
		return name;
	}
	
	public ArrayList<String> getNames() {
		return names;
	}

	public boolean itemTest(String name) {
        String teststring = yaml.getItems().getString(name + ".stock.stock");
        boolean item = false;
        if (teststring != null) {
        	item = true;
        }
        return item;
	}
    
	public boolean enchantTest(String name) {
		 String teststring = yaml.getEnchants().getString(name + ".stock.stock");
        boolean enchant = false;
        if (teststring != null) {
        	enchant = true;
        }
        return enchant;
	}
    
    public boolean isLocked() {
    	return lock;
    }
    
    
    
    
    //Fields
    
    

	
	private long saveinterval;
	private int savetaskid;
	
	private YamlFile yaml;
	private boolean lock;
	private boolean brokenfile;
	

	




	
	
	//private String name;
	
	
	
	
	//Stores all item and enchantment names for reverse lookups.
	private HashMap <String, String> namedata = new HashMap<String, String>();
	private HashMap <String, String> enchantdata = new HashMap<String, String>();
	
	//Stores an arraylist of all item and enchantment names for other plugins to use.
	private ArrayList<String> names = new ArrayList<String>();
	
}

//Latest error number: 34
