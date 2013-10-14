package regalowl.hyperconomy;

import regalowl.databukkit.YamlHandler;


public class Update {
	
	
	Update() {
		HyperConomy hc = HyperConomy.hc;
		YamlHandler yh = hc.getYamlHandler();
		if (yh.gFC("config").getBoolean("config.run-automatic-backups")) {
			new Backup();
		}
		String t = yh.gFC("config").getString("config.signupdateinterval");
		if (t == null) {
			yh.gFC("config").set("config.signupdateinterval", 13);
		}
		String t2 = yh.gFC("config").getString("config.daystosavehistory");
		if (t2 == null) {
			yh.gFC("config").set("config.daystosavehistory", 30);
		}
		String t3 = yh.gFC("config").getString("config.initialshopbalance");
		if (t3 == null) {
			yh.gFC("config").set("config.initialshopbalance", 20000000);
		}
		String t4 = yh.gFC("config").getString("config.shop-has-unlimited-money");
		if (t4 == null) {
			yh.gFC("config").set("config.shop-has-unlimited-money", false);
		}
		String t5 = yh.gFC("config").getString("config.use-shop-exit-message");
		if (t5 == null) {
			yh.gFC("config").set("config.use-shop-exit-message", true);
		}
		String t6 = yh.gFC("config").getString("config.use-notifications");
		if (t6 == null) {
			yh.gFC("config").set("config.use-notifications", true);
		}
		String t7 = yh.gFC("config").getString("config.notify-for");
		if (t7 == null) {
			yh.gFC("config").set("config.notify-for", "diamond,diamondblock,");
		}
		String t8 = yh.gFC("config").getString("config.use-info-signs");
		if (t8 == null) {
			yh.gFC("config").set("config.use-info-signs", true);
		}
		String t9 = yh.gFC("config").getString("config.store-price-history");
		if (t9 == null) {
			yh.gFC("config").set("config.store-price-history", true);
		}
		String t10 = yh.gFC("config").getString("version");
		if (t10 == null) {
			Double newversion = Double.parseDouble(hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion());
			yh.gFC("config").set("version", newversion);
		}
		String t11 = yh.gFC("config").getString("config.use-transaction-signs");
		if (t11 == null) {
			yh.gFC("config").set("config.use-transaction-signs", true);
		}
		String t12 = yh.gFC("config").getString("config.global-shop-account");
		if (t12 == null) {
			yh.gFC("config").set("config.global-shop-account", "hyperconomy");
		}
		String t13 = yh.gFC("config").getString("config.use-chest-shops");
		if (t13 == null) {
			yh.gFC("config").set("config.use-chest-shops", true);
		}
		String t14 = yh.gFC("config").getString("config.use-shop-permissions");
		if (t14 == null) {
			yh.gFC("config").set("config.use-shop-permissions", false);
		}
		String t15 = yh.gFC("config").getString("config.require-chest-shops-to-be-in-shop");
		if (t15 == null) {
			yh.gFC("config").set("config.require-chest-shops-to-be-in-shop", false);
		}
		String t17 = yh.gFC("config").getString("config.sql-connection.use-mysql");
		if (t17 == null) {
			yh.gFC("config").set("config.sql-connection.use-mysql", false);
		}
		String t18 = yh.gFC("config").getString("config.sql-connection.username");
		if (t18 == null) {
			yh.gFC("config").set("config.sql-connection.username", "default");
		}
		String t19 = yh.gFC("config").getString("config.sql-connection.port");
		if (t19 == null) {
			yh.gFC("config").set("config.sql-connection.port", 3306);
		}
		String t20 = yh.gFC("config").getString("config.sql-connection.password");
		if (t20 == null) {
			yh.gFC("config").set("config.sql-connection.password", "default");
		}
		String t21 = yh.gFC("config").getString("config.sql-connection.host");
		if (t21 == null) {
			yh.gFC("config").set("config.sql-connection.host", "localhost");
		}
		String t22 = yh.gFC("config").getString("config.sql-connection.database");
		if (t22 == null) {
			yh.gFC("config").set("config.sql-connection.database", "minecraft");
		}
		String t23 = yh.gFC("config").getString("config.sales-tax-percent");
		if (t23 == null) {
			yh.gFC("config").set("config.sales-tax-percent", 0);
		}
		String t24 = yh.gFC("config").getString("config.dynamic-tax.use-dynamic-tax");
		if (t24 == null) {
			yh.gFC("config").set("config.dynamic-tax.use-dynamic-tax", false);
		}
		String t25 = yh.gFC("config").getString("config.dynamic-tax.money-cap");
		if (t25 == null) {
			yh.gFC("config").set("config.dynamic-tax.money-cap", 1000000);
		}
		String t26 = yh.gFC("config").getString("config.dynamic-tax.max-tax-percent");
		if (t26 == null) {
			yh.gFC("config").set("config.dynamic-tax.max-tax-percent", 100);
		}
		String t27 = yh.gFC("config").getString("config.dynamic-tax.money-floor");
		if (t27 == null) {
			yh.gFC("config").set("config.dynamic-tax.money-floor", 0);
		}
		String t28 = yh.gFC("config").getString("config.web-page.use-web-page");
		if (t28 == null) {
			yh.gFC("config").set("config.web-page.use-web-page", false);
			yh.gFC("config").set("config.web-page.background-color", "8FA685");
			yh.gFC("config").set("config.web-page.font-color", "F2F2F2");
			yh.gFC("config").set("config.web-page.border-color", "091926");
			yh.gFC("config").set("config.web-page.increase-value-color", "C8D9B0");
			yh.gFC("config").set("config.web-page.decrease-value-color", "F2B2A8");
			yh.gFC("config").set("config.web-page.highlight-row-color", "8FA685");
			yh.gFC("config").set("config.web-page.header-color", "091926");
		}
		String t30 = yh.gFC("config").getString("config.web-page.port");
		if (t30 == null) {
			yh.gFC("config").set("config.web-page.port", 7777);
		}
		String t50 = yh.gFC("config").getString("config.web-page.table-data-color");
		if (t50 == null) {
			yh.gFC("config").set("config.web-page.table-data-color", "314A59");
		}
		String t51 = yh.gFC("config").getString("config.web-page.font-size");
		if (t51 == null) {
			yh.gFC("config").set("config.web-page.font-size", 12);
		}
		String t52 = yh.gFC("config").getString("config.web-page.font");
		if (t52 == null) {
			yh.gFC("config").set("config.web-page.font", "verdana");
		}
		String t31 = yh.gFC("config").getString("config.run-automatic-backups");
		if (t31 == null) {
			yh.gFC("config").set("config.run-automatic-backups", true);
		}
		String t34 = yh.gFC("config").getString("config.require-transaction-signs-to-be-in-shop");
		if (t34 == null) {
			yh.gFC("config").set("config.require-transaction-signs-to-be-in-shop", false);
		}
		String t35 = yh.gFC("config").getString("config.unlimited-stock-for-static-items");
		if (t35 == null) {
			yh.gFC("config").set("config.unlimited-stock-for-static-items", false);
		}
		String t36 = yh.gFC("config").getString("config.use-item-displays");
		if (t36 == null) {
			yh.gFC("config").set("config.use-item-displays", true);
		}
		String t37 = yh.gFC("config").getString("config.language");
		if (t37 == null) {
			yh.gFC("config").set("config.language", "english");
		}
		String t41 = yh.gFC("config").getString("config.use-external-economy-plugin");
		if (t41 == null) {
			yh.gFC("config").set("config.use-external-economy-plugin", false);
		}
		String t42 = yh.gFC("config").getString("config.limit-info-commands-to-shops");
		if (t42 == null) {
			yh.gFC("config").set("config.limit-info-commands-to-shops", false);
		}
		String t43 = yh.gFC("config").getString("config.sell-remaining-if-less-than-requested-amount");
		if (t43 == null) {
			yh.gFC("config").set("config.sell-remaining-if-less-than-requested-amount", true);
		}
		String t44 = yh.gFC("config").getString("config.enchantment.classvalue.book");
		if (t44 == null) {
			yh.gFC("config").set("config.enchantment.classvalue.book", 1);
		}
		String t45 = yh.gFC("config").getString("config.use-shops");
		if (t45 == null) {
			yh.gFC("config").set("config.use-shops", true);
		}
		String t46 = yh.gFC("config").getString("api-version");
		if (t46 == null) {
			yh.gFC("config").set("api-version", 1.0);
		}
		String t47 = yh.gFC("config").getString("config.sql-connection.use-sql");
		if (t47 != null) {
			yh.gFC("config").set("config.sql-connection.use-mysql", yh.gFC("config").getBoolean("config.sql-connection.use-sql"));
			yh.gFC("config").set("config.sql-connection.use-sql", null);
		}
		String t48 = yh.gFC("config").getString("config.allow-scrolling-transaction-signs");
		if (t48 == null) {
			yh.gFC("config").set("config.allow-scrolling-transaction-signs", false);
		}
		String t49 = yh.gFC("config").getString("config.block-selling-in-creative-mode");
		if (t49 == null) {
			yh.gFC("config").set("config.block-selling-in-creative-mode", false);
		}
		String t53 = yh.gFC("config").getString("config.show-currency-symbol-after-price");
		if (t53 == null) {
			yh.gFC("config").set("config.show-currency-symbol-after-price", false);
		}
		String t54 = yh.gFC("config").getString("config.starting-player-account-balance");
		if (t54 == null) {
			yh.gFC("config").set("config.starting-player-account-balance", 0.0);
		}
		String t55 = yh.gFC("config").getString("config.block-player-with-same-name-as-global-shop-account");
		if (t55 == null) {
			yh.gFC("config").set("config.block-player-with-same-name-as-global-shop-account", true);
		}
		String t56 = yh.gFC("config").getString("config.max-player-shop-volume");
		if (t56 == null) {
			yh.gFC("config").set("config.max-player-shop-volume", 1000);
		}
		String t57 = yh.gFC("config").getString("config.use-player-shops");
		if (t57 == null) {
			yh.gFC("config").set("config.use-player-shops", true);
		}
		String t58 = yh.gFC("config").getString("config.use-composite-items");
		if (t58 == null) {
			yh.gFC("config").set("config.use-composite-items", false);
		}

		if (!yh.gFC("config").isSet("config.max-shops-per-player")) {
			yh.gFC("config").set("config.max-shops-per-player", 2);
		}
		if (!yh.gFC("config").isSet("config.hook-internal-economy-into-vault")) {
			yh.gFC("config").set("config.hook-internal-economy-into-vault", true);
		}

		yh.gFC("config").set("config.sql-connection.max-sql-threads", null);
		yh.gFC("config").set("config.log-sqlwrite-errors", null);
	}
}
