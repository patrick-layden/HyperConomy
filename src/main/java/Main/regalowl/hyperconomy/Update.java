package regalowl.hyperconomy;


public class Update {
	
	
	Update() {
		HyperConomy hc = HyperConomy.hc;
		YamlFile yaml = hc.getYaml();
		
		String version = hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion();
		String configversion = yaml.getConfig().getString("version");
		
		if (configversion == null || !configversion.equalsIgnoreCase(version)) {
			LanguageFile L = hc.getLanguageFile();
			L.updateBackup();
			if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
				new Backup();
			}
	    	String t = yaml.getConfig().getString("config.signupdateinterval");
	    	if (t == null) {
	    		yaml.getConfig().set("config.signupdateinterval", 13);
	    	}
	    	String t2 = yaml.getConfig().getString("config.daystosavehistory");
	    	if (t2 == null) {
	    		yaml.getConfig().set("config.daystosavehistory", 30);
	    	}
	    	String t3 = yaml.getConfig().getString("config.initialshopbalance");
	    	if (t3 == null) {
	    		yaml.getConfig().set("config.initialshopbalance", 20000000);
	    	}
	    	String t4 = yaml.getConfig().getString("config.shop-has-unlimited-money");
	    	if (t4 == null) {
	    		yaml.getConfig().set("config.shop-has-unlimited-money", false);
	    	}
	    	String t5 = yaml.getConfig().getString("config.use-shop-exit-message");
	    	if (t5 == null) {
	    		yaml.getConfig().set("config.use-shop-exit-message", true);
	    	}
	    	String t6 = yaml.getConfig().getString("config.use-notifications");
	    	if (t6 == null) {
	    		yaml.getConfig().set("config.use-notifications", true);
	    	}
	    	String t7 = yaml.getConfig().getString("config.notify-for");
	    	if (t7 == null) {
	    		yaml.getConfig().set("config.notify-for", "diamond,diamondblock,");
	    	}
	    	String t8 = yaml.getConfig().getString("config.use-info-signs");
	    	if (t8 == null) {
	    		yaml.getConfig().set("config.use-info-signs", true);
	    	}
	    	String t9 = yaml.getConfig().getString("config.store-price-history");
	    	if (t9 == null) {
	    		yaml.getConfig().set("config.store-price-history", true);
	    	}
	    	String t10 = yaml.getConfig().getString("version");
	    	if (t10 == null) {
	    		Double newversion = Double.parseDouble(hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion());
	    		yaml.getConfig().set("version", newversion);
	    	}
	    	String t11 = yaml.getConfig().getString("config.use-transaction-signs");
	    	if (t11 == null) {
	    		yaml.getConfig().set("config.use-transaction-signs", true);
	    	}
	    	String t12 = yaml.getConfig().getString("config.global-shop-account");
	    	if (t12 == null) {
	    		yaml.getConfig().set("config.global-shop-account", "hyperconomy");
	    	}
	    	String t13 = yaml.getConfig().getString("config.use-chest-shops");
	    	if (t13 == null) {
	    		yaml.getConfig().set("config.use-chest-shops", true);
	    	}
	    	String t14 = yaml.getConfig().getString("config.use-shop-permissions");
	    	if (t14 == null) {
	    		yaml.getConfig().set("config.use-shop-permissions", false);
	    	}
	    	String t15 = yaml.getConfig().getString("config.require-chest-shops-to-be-in-shop");
	    	if (t15 == null) {
	    		yaml.getConfig().set("config.require-chest-shops-to-be-in-shop", false);
	    	}
	    	String t16 = yaml.getConfig().getString("config.currency-symbol");
	    	if (t16 == null) {
	    		yaml.getConfig().set("config.currency-symbol", "$");
	    	}
	    	String t17 = yaml.getConfig().getString("config.sql-connection.use-sql");
	    	if (t17 == null) {
	    		yaml.getConfig().set("config.sql-connection.use-sql", false);
	    	}
	    	String t18 = yaml.getConfig().getString("config.sql-connection.username");
	    	if (t18 == null) {
	    		yaml.getConfig().set("config.sql-connection.username", "default");
	    	}
	    	String t19 = yaml.getConfig().getString("config.sql-connection.port");
	    	if (t19 == null) {
	    		yaml.getConfig().set("config.sql-connection.port", 3306);
	    	}
	    	String t20 = yaml.getConfig().getString("config.sql-connection.password");
	    	if (t20 == null) {
	    		yaml.getConfig().set("config.sql-connection.password", "default");
	    	}
	    	String t21 = yaml.getConfig().getString("config.sql-connection.host");
	    	if (t21 == null) {
	    		yaml.getConfig().set("config.sql-connection.host", "localhost");
	    	}
	    	String t22 = yaml.getConfig().getString("config.sql-connection.database");
	    	if (t22 == null) {
	    		yaml.getConfig().set("config.sql-connection.database", "minecraft");
	    	}
	    	String t23 = yaml.getConfig().getString("config.sales-tax-percent");
	    	if (t23 == null) {
	    		yaml.getConfig().set("config.sales-tax-percent", 0);
	    	}
	    	String t24 = yaml.getConfig().getString("config.dynamic-tax.use-dynamic-tax");
	    	if (t24 == null) {
	    		yaml.getConfig().set("config.dynamic-tax.use-dynamic-tax", false);
	    	}
	    	String t25 = yaml.getConfig().getString("config.dynamic-tax.money-cap");
	    	if (t25 == null) {
	    		yaml.getConfig().set("config.dynamic-tax.money-cap", 1000000);
	    	}
	    	String t26 = yaml.getConfig().getString("config.dynamic-tax.max-tax-percent");
	    	if (t26 == null) {
	    		yaml.getConfig().set("config.dynamic-tax.max-tax-percent", 100);
	    	}
	    	String t27 = yaml.getConfig().getString("config.dynamic-tax.money-floor");
	    	if (t27 == null) {
	    		yaml.getConfig().set("config.dynamic-tax.money-floor", 0);
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
	    	}
	    	String t29 = yaml.getConfig().getString("config.sql-connection.max-sql-threads");
	    	if (t29 == null) {
	    		yaml.getConfig().set("config.sql-connection.max-sql-threads", 20);
	    	}
	    	String t30 = yaml.getConfig().getString("config.web-page.port");
	    	if (t30 == null) {
	    		yaml.getConfig().set("config.web-page.port", 8765);
	    	}
	    	String t31 = yaml.getConfig().getString("config.run-automatic-backups");
	    	if (t31 == null) {
	    		yaml.getConfig().set("config.run-automatic-backups", true);
	    	}
	    	String t32 = yaml.getConfig().getString("config.error-count");
	    	if (t32 == null) {
	    		yaml.getConfig().set("config.error-count", 0);
	    	}
	    	String t33 = yaml.getConfig().getString("config.web-page.web-page-economy");
	    	if (t33 == null) {
	    		yaml.getConfig().set("config.web-page.web-page-economy", "default");
	    	}
	    	String t34 = yaml.getConfig().getString("config.require-transaction-signs-to-be-in-shop");
	    	if (t34 == null) {
	    		yaml.getConfig().set("config.require-transaction-signs-to-be-in-shop", false);
	    	}
	    	String t35 = yaml.getConfig().getString("config.unlimited-stock-for-static-items");
	    	if (t35 == null) {
	    		yaml.getConfig().set("config.unlimited-stock-for-static-items", false);
	    	}
	    	String t36 = yaml.getConfig().getString("config.use-item-displays");
	    	if (t36 == null) {
	    		yaml.getConfig().set("config.use-item-displays", true);
	    	}
	    	String t37 = yaml.getConfig().getString("config.language");
	    	if (t37 == null) {
	    		yaml.getConfig().set("config.language", "english");
	    	}
	    	String t38 = yaml.getConfig().getString("config.log-errors");
	    	if (t38 == null) {
	    		yaml.getConfig().set("config.log-errors", false);
	    	}
	    	String t39 = yaml.getConfig().getString("config.log-sqlwrite-errors");
	    	if (t39 == null) {
	    		yaml.getConfig().set("config.log-sqlwrite-errors", false);
	    	}
	    	String t40 = yaml.getConfig().getString("config.tekkit");
	    	if (t40 == null) {
	    		yaml.getConfig().set("config.tekkit", false);
	    	}
	    	String t41 = yaml.getConfig().getString("config.use-external-economy-plugin");
	    	if (t41 == null) {
	    		yaml.getConfig().set("config.use-external-economy-plugin", true);
	    	}
	    	String t42 = yaml.getConfig().getString("config.limit-info-commands-to-shops");
	    	if (t42 == null) {
	    		yaml.getConfig().set("config.limit-info-commands-to-shops", false);
	    	}
	    	String t43 = yaml.getConfig().getString("config.sell-remaining-if-less-than-requested-amount");
	    	if (t43 == null) {
	    		yaml.getConfig().set("config.sell-remaining-if-less-than-requested-amount", true);
	    	}
	    	String t44 = yaml.getConfig().getString("config.enchantment.classvalue.book");
	    	if (t44 == null) {
	    		yaml.getConfig().set("config.enchantment.classvalue.book", 1);
	    	}
	    	String t45 = yaml.getConfig().getString("config.use-shops");
	    	if (t45 == null) {
	    		yaml.getConfig().set("config.use-shops", true);
	    	}	    	
	    	
	    	
	    	double dversion = Double.parseDouble(configversion);
	    	if (dversion < .952) {
	    		yaml.getConfig().set("config.log-errors", false);
	    	}
	    	if (dversion < .957) {
	    		yaml.getConfig().set("config.sql-connection.max-sql-threads", 5);
	    	}
		}
		Double newversion = Double.parseDouble(hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion());
		yaml.getConfig().set("version", newversion);
	}
	

}
