package regalowl.hyperconomy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;



/**
 * 
 *This class handles the items, config, and enchants yaml files.
 *
 */
public class YamlFile extends HyperConomy {

    FileConfiguration config;
    FileConfiguration items;
    FileConfiguration enchants;
    FileConfiguration shops;
    FileConfiguration log;
    File configFile;
    File itemsFile;      
    File enchantsFile;  
    File shopsFile;
    File logFile;
	
    
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
        try {
            config.load(configFile);
            items.load(itemsFile);
            enchants.load(enchantsFile);
            shops.load(shopsFile);
            log.load(logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * 
	 * This saves the yaml files.
	 * 
	 */
    public void saveYamls() {
        try {
            config.save(configFile); 
            items.save(itemsFile);
            enchants.save(enchantsFile);
            shops.save(shopsFile);
            log.save(logFile);
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
	

}
