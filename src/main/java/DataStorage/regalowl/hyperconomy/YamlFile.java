package regalowl.hyperconomy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;



/**
 * 
 *This class handles the items, config, and enchants yaml files.
 *
 */
public class YamlFile {

    FileConfiguration config;
    FileConfiguration items;
    FileConfiguration enchants;
    FileConfiguration shops;
    FileConfiguration log;
    FileConfiguration signs;
    FileConfiguration history;
    FileConfiguration categories;
    FileConfiguration language;
    FileConfiguration displays;
    File configFile;
    File itemsFile;      
    File enchantsFile;  
    File shopsFile;
    File logFile;
    File signsFile;
    File historyFile;
    File categoryFile;
    File languageFile;
    File displaysFile;
    
    private boolean brokenfile;
    
    
    HyperConomy hc;
    
    YamlFile(HyperConomy hyperc) {
    	brokenfile = false;
    	hc = hyperc;
    }
	
    
	/**
	 * 
	 * This is run when the plugin is enabled, it calls the firstRun() method to create the yamls if they don't exist and then loads the yaml files with the loadYamls() method.
	 * 
	 */
	public void YamlEnable() {
		

        configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "config.yml");  
        itemsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "items.yml");   
        enchantsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "enchants.yml");
        shopsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "shops.yml");
        logFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "log.yml");
        signsFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "signs.yml");
        historyFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "history.yml");
        categoryFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "categories.yml");
        languageFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "language.yml");
        displaysFile = new File(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder(), "displays.yml");
        
        try {
            firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        config = new YamlConfiguration();
        items = new YamlConfiguration();
        enchants = new YamlConfiguration();
        shops = new YamlConfiguration();
        log = new YamlConfiguration();
        signs = new YamlConfiguration();
        history = new YamlConfiguration();
        categories = new YamlConfiguration();
        language = new YamlConfiguration();
        displays = new YamlConfiguration();
        loadYamls();
		
	}
	
	
	/**
	 * 
	 * Checks if the yamls exist, and if they don't, it copies the default files to the plugin's folder.
	 * 
	 */
    private void firstRun() throws Exception {
        if(!configFile.exists()){              
            configFile.getParentFile().mkdirs();         
            copy(this.getClass().getResourceAsStream("/config.yml"), configFile);
        }
        if(!itemsFile.exists()){
            itemsFile.getParentFile().mkdirs();
            copy(this.getClass().getResourceAsStream("/items.yml"), itemsFile);
        }
        if(!enchantsFile.exists()){
            enchantsFile.getParentFile().mkdirs();
            copy(this.getClass().getResourceAsStream("/enchants.yml"), enchantsFile);
        }
        if(!shopsFile.exists()){
            shopsFile.getParentFile().mkdirs();
            copy(this.getClass().getResourceAsStream("/shops.yml"), shopsFile);
        }
        if(!logFile.exists()){
            logFile.getParentFile().mkdirs();
            copy(this.getClass().getResourceAsStream("/log.yml"), logFile);
        }
        if(!signsFile.exists()){
            signsFile.getParentFile().mkdirs();
            copy(this.getClass().getResourceAsStream("/signs.yml"), signsFile);
        }
        if(!historyFile.exists()){
            historyFile.getParentFile().mkdirs();
            copy(this.getClass().getResourceAsStream("/history.yml"), historyFile);
        }
        if(!categoryFile.exists()){
            categoryFile.getParentFile().mkdirs();
            copy(this.getClass().getResourceAsStream("/categories.yml"), categoryFile);
        }
        if(!languageFile.exists()){
            languageFile.getParentFile().mkdirs();
            copy(this.getClass().getResourceAsStream("/language.yml"), languageFile);
        }
        if(!displaysFile.exists()){
            displaysFile.getParentFile().mkdirs();
            copy(this.getClass().getResourceAsStream("/displays.yml"), displaysFile);
        }
    }


	/**
	 * 
	 * This actually copies the files.
	 * 
	 */
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * 
	 * This loads the yaml files.
	 * 
	 */
    public void loadYamls() {
    	int failcount = 0;
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #24 Bad config.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #24.  Bad config.yml file.", "hyperconomy.error");
			failcount++;
        }
        try {
        	items.load(itemsFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #25 Bad items.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #25.  Bad items.yml file.", "hyperconomy.error");
			failcount++;
        }
        try {
        	enchants.load(enchantsFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #26 Bad enchants.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #26.  Bad enchants.yml file.", "hyperconomy.error");
			failcount++;
        }
        try {
        	shops.load(shopsFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #27 Bad shops.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #27.  Bad shops.yml file.", "hyperconomy.error");
			failcount++;
        }
        try {
        	log.load(logFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #28 Bad log.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #28.  Bad log.yml file.", "hyperconomy.error");
			failcount++;
        }
        try {
        	signs.load(signsFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #29 Bad signs.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #29.  Bad signs.yml file.", "hyperconomy.error");
			failcount++;
        }
        try {
        	history.load(historyFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #30 Bad history.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #30.  Bad history.yml file.", "hyperconomy.error");
			failcount++;
        }
        try {
        	categories.load(categoryFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #56 Bad categories.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #56.  Bad categories.yml file.", "hyperconomy.error");
			failcount++;
        }
        try {
        	language.load(languageFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #56 Bad language.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #57.  Bad language.yml file.", "hyperconomy.error");
			failcount++;
        }
        try {
        	displays.load(displaysFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #56 Bad displays.yml file.");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #57.  Bad displays.yml file.", "hyperconomy.error");
			failcount++;
        }
        if (failcount != 0) {
        	brokenfile = true;
        }
        hc.ymlCheck(failcount);

    }

	/**
	 * 
	 * This saves the yaml files.
	 * 
	 */
    public void saveYamls() {
        try {
        	if (!brokenfile) {
            	//Logger logger = Logger.getLogger("Minecraft");
            	//logger.info("---------------HyperConomy Files Saved----------------");
                config.save(configFile); 
                items.save(itemsFile);
                enchants.save(enchantsFile);
                shops.save(shopsFile);
                log.save(logFile);
                signs.save(signsFile);
                history.save(historyFile);
                categories.save(categoryFile);
                language.save(languageFile);
                displays.save(displaysFile);
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
	/**
	 * 
	 * This saves just the log file.
	 * 
	 */
    public void saveLog() {
        try {
            log.save(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
	
	
	/**
	 * 
	 * This gets the FileConfiguration items.
	 * 
	 */
    public FileConfiguration getItems(){
		return items;
	}
	
	/**
	 * 
	 * This gets the config FileConfiguraiton.
	 * 
	 */
	public FileConfiguration getConfig(){
		return config;
	}
	
	/**
	 * 
	 * This gets the enchants FileConfiguration.
	 * 
	 */
	public FileConfiguration getEnchants(){
		return enchants;
	}
	
	/**
	 * 
	 * This gets the shops FileConfiguration.
	 * 
	 */
	public FileConfiguration getShops(){
		return shops;
	}
	
	
	/**
	 * 
	 * This gets the log FileConfiguration.
	 * 
	 */
	public FileConfiguration getLog(){
		return log;
	}
	
	
	/**
	 * 
	 * This gets the signs FileConfiguration.
	 * 
	 */
	public FileConfiguration getSigns(){
		return signs;
	}
	
	
	/**
	 * 
	 * This gets the history FileConfiguration.
	 * 
	 */
	public FileConfiguration getHistory(){
		return history;
	}
	
	
	/**
	 * 
	 * This gets the categories FileConfiguration.
	 * 
	 */
	public FileConfiguration getCategories(){
		return categories;
	}
	
	/**
	 * 
	 * This gets the language FileConfiguration.
	 * 
	 */
	public FileConfiguration getLanguage(){
		return language;
	}
	
	/**
	 * 
	 * This gets the displays FileConfiguration.
	 * 
	 */
	public FileConfiguration getDisplays(){
		return displays;
	}

}
