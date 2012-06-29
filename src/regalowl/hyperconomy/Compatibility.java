package regalowl.hyperconomy;

public class Compatibility {
	
	
	public boolean checkCompatibility(HyperConomy hc) {
		boolean uptodate = true;
		YamlFile yaml = hc.getYaml();
		
		String version = hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion();
		String configversion = yaml.getConfig().getString("version");
		
		if (configversion == null || !configversion.equalsIgnoreCase(version)) {
	    	String t = yaml.getConfig().getString("config.signupdateinterval");
	    	if (t == null) {
	    		//isign.setsignupdateInterval(13L);
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
