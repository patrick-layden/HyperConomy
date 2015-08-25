package regalowl.hyperconomy.util;


import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.hyperconomy.HyperConomy;


public class UpdateYML {
	
	
	public UpdateYML(HyperConomy hc) {
		FileConfiguration cfg = hc.getConf();

		//remove when unnecessary for upgrades
		cfg.setDefault("shop.default-server-shop-account", cfg.getString("config.global-shop-account"));
		

		cfg.setDefault("enable-feature.shops", true);
		cfg.setDefault("enable-feature.item-displays", true);
		cfg.setDefault("enable-feature.chest-shops", true);
		cfg.setDefault("enable-feature.info-signs", true);
		cfg.setDefault("enable-feature.composite-items", false);
		cfg.setDefault("enable-feature.player-shops", true);
		cfg.setDefault("enable-feature.scrolling-transaction-signs", false);
		cfg.setDefault("enable-feature.price-history-storage", true);
		cfg.setDefault("enable-feature.transaction-signs", true);
		cfg.setDefault("enable-feature.automatic-backups", true);
		cfg.setDefault("enable-feature.per-shop-permissions", false);
		cfg.setDefault("enable-feature.price-change-notifications", true);
		cfg.setDefault("enable-feature.treat-damaged-items-as-equals-to-undamaged-ones", true);
		cfg.setDefault("enable-feature.debug-mode", false);
		cfg.setDefault("enable-feature.uuid-support", true);
		
		
		cfg.setDefault("economy-plugin.use-external", true);
		cfg.setDefault("economy-plugin.hook-internal-economy-into-vault", false);
		cfg.setDefault("economy-plugin.starting-player-account-balance", 0);
		

		cfg.setDefault("sql.use-mysql", false);
		cfg.setDefault("sql.log-sql-statements", false);
		cfg.setDefault("sql.mysql-connection.username", "default_username");
		cfg.setDefault("sql.mysql-connection.port", 3306);
		cfg.setDefault("sql.mysql-connection.password", "default_password");		
		cfg.setDefault("sql.mysql-connection.host", "localhost");		
		cfg.setDefault("sql.mysql-connection.database", "minecraft");				
		cfg.setDefault("bank.max-ownerships-per-player", 3);

		
		cfg.setDefault("language", "english");
		cfg.setDefault("intervals.shop-check", 6);		
		cfg.setDefault("intervals.save", 24000);


		cfg.setDefault("tax.account", "hyperconomy");
		cfg.setDefault("tax.purchase", 3);
		cfg.setDefault("tax.initial", 100);
		cfg.setDefault("tax.static", 100);
		cfg.setDefault("tax.enchant", 100);
		cfg.setDefault("tax.sales", 0);
		cfg.setDefault("tax.dynamic.enable", false);
		cfg.setDefault("tax.dynamic.money-floor", 0);
		cfg.setDefault("tax.dynamic.money-cap", 1000000);
		cfg.setDefault("tax.dynamic.max-tax-percent", 100);
		
		
		cfg.setDefault("shop.default-server-shop-account", "hyperconomy");
		cfg.setDefault("shop.default-server-shop-account-initial-balance", 20000000);
		cfg.setDefault("shop.display-shop-exit-message", true);
		cfg.setDefault("shop.max-stock-per-item-in-playershops", 100000);
		cfg.setDefault("shop.max-player-shop-volume", 1000);
		cfg.setDefault("shop.max-player-shops-per-player", 1);
		cfg.setDefault("shop.sell-remaining-if-less-than-requested-amount", true);
		cfg.setDefault("shop.server-shops-have-unlimited-money", false);
		cfg.setDefault("shop.limit-info-commands-to-shops", false);
		cfg.setDefault("shop.block-selling-in-creative-mode", false);
		cfg.setDefault("shop.show-currency-symbol-after-price", false);
		cfg.setDefault("shop.unlimited-stock-for-static-items", false);
		cfg.setDefault("shop.require-chest-shops-to-be-in-shop", false);
		cfg.setDefault("shop.require-transaction-signs-to-be-in-shop", false);
		cfg.setDefault("shop.send-price-change-notifications-for", "diamond,diamondblock,");

		
		cfg.setDefault("history.days-to-save", 7);

		
		cfg.setDefault("enchantment.classvalue.wood", .1);
		cfg.setDefault("enchantment.classvalue.leather", .1);		
		cfg.setDefault("enchantment.classvalue.stone", .15);
		cfg.setDefault("enchantment.classvalue.chainmail", .2);
		cfg.setDefault("enchantment.classvalue.iron", .25);
		cfg.setDefault("enchantment.classvalue.gold", .1);
		cfg.setDefault("enchantment.classvalue.diamond", 1);
		cfg.setDefault("enchantment.classvalue.bow", .25);
		cfg.setDefault("enchantment.classvalue.book", 1);
		

		cfg.setDefault("multi-server.enable", false);
		cfg.setDefault("multi-server.remote-server-ip-addresses", "192.168.1.1,3313;192.168.1.1,3314;192.168.1.2,3313;");
		cfg.setDefault("multi-server.port", 3313);
		cfg.setDefault("multi-server.update-interval", 500);	
		cfg.setDefault("multi-server.connection-timeout-ms", 2000);
		cfg.setDefault("multi-server.sync-shops", true);
		cfg.setDefault("multi-server.sync-trade-objects", true);
		cfg.setDefault("multi-server.sync-accounts", true);
		
		
		cfg.setDefault("updater.enabled", true);
		cfg.setDefault("updater.notify-in-game", true);
		cfg.setDefault("updater.notify-for.dev-builds", true);
		cfg.setDefault("updater.notify-for.beta-builds", true);
		cfg.setDefault("updater.notify-for.recommended-builds", true);
		
		
		cfg.setDefault("web-page.enable", false);
		cfg.setDefault("web-page.port", 7777);
		cfg.setDefault("web-page.background-color", "8FA685");
		cfg.setDefault("web-page.font-color", "F2F2F2");
		cfg.setDefault("web-page.border-color", "091926");
		cfg.setDefault("web-page.increase-value-color", "C8D9B0");
		cfg.setDefault("web-page.decrease-value-color", "F2B2A8");
		cfg.setDefault("web-page.highlight-row-color", "8FA685");
		cfg.setDefault("web-page.header-color", "091926");
		cfg.setDefault("web-page.table-data-color", "314A59");
		cfg.setDefault("web-page.font-size", 12);
		cfg.setDefault("web-page.font", "verdana");
		cfg.setDefault("web-page.enable-web-api", false);
		cfg.setDefault("web-page.web-api-path", "API");
	}
}
