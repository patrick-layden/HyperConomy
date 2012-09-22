package regalowl.hyperconomy;

import java.util.Iterator;

import org.bukkit.configuration.file.FileConfiguration;

public class Update {
	
	
	public boolean checkCompatibility(HyperConomy hc) {
		boolean uptodate = true;
		YamlFile yaml = hc.getYaml();
		
		String version = hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion();
		String configversion = yaml.getConfig().getString("version");
		
		if (configversion == null || !configversion.equalsIgnoreCase(version)) {
			
			if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
				//Removed for the sql table alteration.  When finished, reorder so that SQLFunctions is initialized first.
				//Backup back = new Backup();
				//back.BackupData();
			}
			
	    	String t = yaml.getConfig().getString("config.signupdateinterval");
	    	if (t == null) {
	    		yaml.getConfig().set("config.signupdateinterval", 13);
	    		uptodate = false;
	    	}
	    	String t2 = yaml.getConfig().getString("config.daystosavehistory");
	    	if (t2 == null) {
	    		yaml.getConfig().set("config.daystosavehistory", 30);
	    		uptodate = false;
	    	}
	    	String t3 = yaml.getConfig().getString("config.initialshopbalance");
	    	if (t3 == null) {
	    		yaml.getConfig().set("config.initialshopbalance", 20000000);
	    		uptodate = false;
	    	}
	    	String t4 = yaml.getConfig().getString("config.shop-has-unlimited-money");
	    	if (t4 == null) {
	    		yaml.getConfig().set("config.shop-has-unlimited-money", false);
	    		uptodate = false;
	    	}
	    	String t5 = yaml.getConfig().getString("config.use-shop-exit-message");
	    	if (t5 == null) {
	    		yaml.getConfig().set("config.use-shop-exit-message", true);
	    		uptodate = false;
	    	}
	    	String t6 = yaml.getConfig().getString("config.use-notifications");
	    	if (t6 == null) {
	    		yaml.getConfig().set("config.use-notifications", true);
	    		uptodate = false;
	    	}
	    	String t7 = yaml.getConfig().getString("config.notify-for");
	    	if (t7 == null) {
	    		yaml.getConfig().set("config.notify-for", "diamond,diamondblock,");
	    		uptodate = false;
	    	}
	    	String t8 = yaml.getConfig().getString("config.use-info-signs");
	    	if (t8 == null) {
	    		yaml.getConfig().set("config.use-info-signs", true);
	    		uptodate = false;
	    	}
	    	String t9 = yaml.getConfig().getString("config.store-price-history");
	    	if (t9 == null) {
	    		yaml.getConfig().set("config.store-price-history", true);
	    		uptodate = false;
	    	}
	    	String t10 = yaml.getConfig().getString("version");
	    	if (t10 == null) {
	    		Double newversion = Double.parseDouble(hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion());
	    		yaml.getConfig().set("version", newversion);
	    		uptodate = false;
	    	}
	    	String t11 = yaml.getConfig().getString("config.use-transaction-signs");
	    	if (t11 == null) {
	    		yaml.getConfig().set("config.use-transaction-signs", true);
	    		uptodate = false;
	    	}
	    	String t12 = yaml.getConfig().getString("config.global-shop-account");
	    	if (t12 == null) {
	    		yaml.getConfig().set("config.global-shop-account", "hyperconomy");
	    		uptodate = false;
	    	}
	    	String t13 = yaml.getConfig().getString("config.use-chest-shops");
	    	if (t13 == null) {
	    		yaml.getConfig().set("config.use-chest-shops", true);
	    		uptodate = false;
	    	}
	    	String t14 = yaml.getConfig().getString("config.use-shop-permissions");
	    	if (t14 == null) {
	    		yaml.getConfig().set("config.use-shop-permissions", false);
	    		uptodate = false;
	    	}
	    	String t15 = yaml.getConfig().getString("config.require-chest-shops-to-be-in-shop");
	    	if (t15 == null) {
	    		yaml.getConfig().set("config.require-chest-shops-to-be-in-shop", false);
	    		uptodate = false;
	    	}
	    	String t16 = yaml.getConfig().getString("config.currency-symbol");
	    	if (t16 == null) {
	    		yaml.getConfig().set("config.currency-symbol", "$");
	    		uptodate = false;
	    	}
	    	String t17 = yaml.getConfig().getString("config.sql-connection.use-sql");
	    	if (t17 == null) {
	    		yaml.getConfig().set("config.sql-connection.use-sql", false);
	    		uptodate = false;
	    	}
	    	String t18 = yaml.getConfig().getString("config.sql-connection.username");
	    	if (t18 == null) {
	    		yaml.getConfig().set("config.sql-connection.username", "default");
	    		uptodate = false;
	    	}
	    	String t19 = yaml.getConfig().getString("config.sql-connection.port");
	    	if (t19 == null) {
	    		yaml.getConfig().set("config.sql-connection.port", 3306);
	    		uptodate = false;
	    	}
	    	String t20 = yaml.getConfig().getString("config.sql-connection.password");
	    	if (t20 == null) {
	    		yaml.getConfig().set("config.sql-connection.password", "default");
	    		uptodate = false;
	    	}
	    	String t21 = yaml.getConfig().getString("config.sql-connection.host");
	    	if (t21 == null) {
	    		yaml.getConfig().set("config.sql-connection.host", "localhost");
	    		uptodate = false;
	    	}
	    	String t22 = yaml.getConfig().getString("config.sql-connection.database");
	    	if (t22 == null) {
	    		yaml.getConfig().set("config.sql-connection.database", "minecraft");
	    		uptodate = false;
	    	}
	    	String t23 = yaml.getConfig().getString("config.sales-tax-percent");
	    	if (t23 == null) {
	    		yaml.getConfig().set("config.sales-tax-percent", 0);
	    		uptodate = false;
	    	}
	    	String t24 = yaml.getConfig().getString("config.dynamic-tax.use-dynamic-tax");
	    	if (t24 == null) {
	    		yaml.getConfig().set("config.dynamic-tax.use-dynamic-tax", false);
	    		uptodate = false;
	    	}
	    	String t25 = yaml.getConfig().getString("config.dynamic-tax.money-cap");
	    	if (t25 == null) {
	    		yaml.getConfig().set("config.dynamic-tax.money-cap", 1000000);
	    		uptodate = false;
	    	}
	    	String t26 = yaml.getConfig().getString("config.dynamic-tax.max-tax-percent");
	    	if (t26 == null) {
	    		yaml.getConfig().set("config.dynamic-tax.max-tax-percent", 100);
	    		uptodate = false;
	    	}
	    	String t27 = yaml.getConfig().getString("config.dynamic-tax.money-floor");
	    	if (t27 == null) {
	    		yaml.getConfig().set("config.dynamic-tax.money-floor", 0);
	    		uptodate = false;
	    	}
	    	String t28 = yaml.getConfig().getString("config.web-page.use-web-page");
	    	if (t28 == null) {
	    		yaml.getConfig().set("config.web-page.use-web-page", false);
	    		yaml.getConfig().set("config.web-page.background-color", "EEEEF6");
	    		yaml.getConfig().set("config.web-page.font-color", "333333");
	    		yaml.getConfig().set("config.web-page.border-color", "D8DFE4");
	    		yaml.getConfig().set("config.web-page.increase-value-color", "A0FFB0");
	    		yaml.getConfig().set("config.web-page.decrease-value-color", "FFA0A0");
	    		yaml.getConfig().set("config.web-page.highlight-row-color", "B4FAF9");
	    		yaml.getConfig().set("config.web-page.header-color", "B8B4FF");
	    		uptodate = false;
	    	}
	    	String t29 = yaml.getConfig().getString("config.sql-connection.max-sql-threads");
	    	if (t29 == null) {
	    		yaml.getConfig().set("config.sql-connection.max-sql-threads", 20);
	    		uptodate = false;
	    	}
	    	String t30 = yaml.getConfig().getString("config.web-page.port");
	    	if (t30 == null) {
	    		yaml.getConfig().set("config.web-page.port", 8765);
	    		uptodate = false;
	    	}
	    	String t31 = yaml.getConfig().getString("config.run-automatic-backups");
	    	if (t31 == null) {
	    		yaml.getConfig().set("config.run-automatic-backups", true);
	    		uptodate = false;
	    	}
	    	String t32 = yaml.getConfig().getString("config.error-count");
	    	if (t32 == null) {
	    		yaml.getConfig().set("config.error-count", 0);
	    		uptodate = false;
	    	}
	    	String t33 = yaml.getConfig().getString("config.web-page.web-page-economy");
	    	if (t33 == null) {
	    		yaml.getConfig().set("config.web-page.web-page-economy", "default");
	    		uptodate = false;
	    	}

	    	
	    	double dversion = Double.parseDouble(configversion);

	    	if (dversion < .946) {
		    	if (hc.useSQL()) {
		    		SQLUtils su = new SQLUtils();
		    		FileConfiguration config = hc.getYaml().getConfig();
		    		boolean exists = su.tableExists(config.getString("config.sql-connection.host"), config.getInt("config.sql-connection.port"), config.getString("config.sql-connection.database"), config.getString("config.sql-connection.username"), config.getString("config.sql-connection.password"), "hyperobjects", "ceiling");
		    		if (!exists) {
		    			String statement = "ALTER TABLE hyperobjects ADD CEILING DOUBLE AFTER STARTPRICE";
		    			su.executeSQL(config.getString("config.sql-connection.host"), config.getInt("config.sql-connection.port"), config.getString("config.sql-connection.database"), config.getString("config.sql-connection.username"), config.getString("config.sql-connection.password"), statement);
		    			statement = "ALTER TABLE hyperobjects ADD FLOOR DOUBLE AFTER CEILING";
		    			su.executeSQL(config.getString("config.sql-connection.host"), config.getInt("config.sql-connection.port"), config.getString("config.sql-connection.database"), config.getString("config.sql-connection.username"), config.getString("config.sql-connection.password"), statement);
		    		}
		    	}
	    		Iterator<String> it = yaml.getItems().getKeys(false).iterator();
	    		while (it.hasNext()) {   			
	    			String elst = it.next().toString();    				
	    			String ctest1 = yaml.getItems().getString(elst + ".price.ceiling");
	    			String ctest2 = yaml.getItems().getString(elst + ".price.floor");
	    			if (ctest1 == null && ctest2 == null) {
	    				yaml.getItems().set(elst + ".price.ceiling", -1.0);
	    				yaml.getItems().set(elst + ".price.floor", -1.0);
	    			}
	    		}   
	    		Iterator<String> it2 = yaml.getEnchants().getKeys(false).iterator();
	    		while (it2.hasNext()) {   			
	    			String elst = it2.next().toString();    				
	    			String ctest1 = yaml.getEnchants().getString(elst + ".price.ceiling");
	    			String ctest2 = yaml.getEnchants().getString(elst + ".price.floor");
	    			if (ctest1 == null && ctest2 == null) {
	    				yaml.getEnchants().set(elst + ".price.ceiling", -1.0);
	    				yaml.getEnchants().set(elst + ".price.floor", -1.0);
	    			}
	    		}
	    	}
	    	
		}
		Double newversion = Double.parseDouble(hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion());
		yaml.getConfig().set("version", newversion);
		
    	return uptodate;
	}
	

}
