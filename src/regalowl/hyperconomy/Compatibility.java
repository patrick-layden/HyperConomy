package regalowl.hyperconomy;

public class Compatibility {
	
	
	public boolean checkCompatibility(HyperConomy hc, InfoSign isign) {
		boolean uptodate = true;
		YamlFile yaml = hc.getYaml();
		
		String version = hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion();
		String configversion = yaml.getConfig().getString("version");
		
		if (configversion == null || !configversion.equalsIgnoreCase(version)) {
	    	String t = yaml.getConfig().getString("config.signupdateinterval");
	    	if (t == null) {
	    		isign.setsignupdateInterval(13L);
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
	    	
	    	if (configversion == null || Double.parseDouble(configversion) < .912) {
	    		hc.getYaml().getItems().set("xp.information.material", "N/A");
	    		hc.getYaml().getItems().set("xp.information.id", -1);
	    		hc.getYaml().getItems().set("xp.information.data", -1);
	    		hc.getYaml().getItems().set("xp.value", 3);
	    		hc.getYaml().getItems().set("xp.price.static", false);
	    		hc.getYaml().getItems().set("xp.price.staticprice", 3);
	    		hc.getYaml().getItems().set("xp.stock.stock", 0);
	    		hc.getYaml().getItems().set("xp.stock.median", 100000);
	    		hc.getYaml().getItems().set("xp.initiation.initiation", true);
	    		hc.getYaml().getItems().set("xp.initiation.startprice", 6);
	    	}
		}
		Double newversion = Double.parseDouble(hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion());
		yaml.getConfig().set("version", newversion);
		
    	return uptodate;
	}
	

}
