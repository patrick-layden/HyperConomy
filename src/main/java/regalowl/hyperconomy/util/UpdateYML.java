package regalowl.hyperconomy.util;

import org.bukkit.configuration.file.FileConfiguration;

import regalowl.databukkit.YamlHandler;
import regalowl.hyperconomy.HyperConomy;


public class UpdateYML {
	
	
	public UpdateYML() {
		HyperConomy hc = HyperConomy.hc;
		YamlHandler yh = hc.getYamlHandler();
		FileConfiguration cfg = yh.gFC("config");
		
		if (!cfg.isSet("enable-feature.shops")) {
			cfg.set("enable-feature.shops", true);
		}
		if (!cfg.isSet("enable-feature.item-displays")) {
			cfg.set("enable-feature.item-displays", true);
		}
		if (!cfg.isSet("enable-feature.chest-shops")) {
			cfg.set("enable-feature.chest-shops", true);
		}
		if (!cfg.isSet("enable-feature.info-signs")) {
			cfg.set("enable-feature.info-signs", true);
		}
		if (!cfg.isSet("enable-feature.composite-items")) {
			cfg.set("enable-feature.composite-items", false);
		}
		if (!cfg.isSet("enable-feature.player-shops")) {
			cfg.set("enable-feature.player-shops", true);
		}
		if (!cfg.isSet("enable-feature.scrolling-transaction-signs")) {
			cfg.set("enable-feature.scrolling-transaction-signs", false);
		}
		if (!cfg.isSet("enable-feature.price-history-storage")) {
			cfg.set("enable-feature.price-history-storage", true);
		}
		if (!cfg.isSet("enable-feature.transaction-signs")) {
			cfg.set("enable-feature.transaction-signs", true);
		}
		if (!cfg.isSet("enable-feature.automatic-backups")) {
			cfg.set("enable-feature.automatic-backups", true);
		}
		if (!cfg.isSet("enable-feature.per-shop-permissions")) {
			cfg.set("enable-feature.per-shop-permissions", false);
		}
		if (!cfg.isSet("enable-feature.price-change-notifications")) {
			cfg.set("enable-feature.price-change-notifications", true);
		}
		
		
		
		if (!cfg.isSet("economy-plugin.use-external")) {
			cfg.set("economy-plugin.use-external", true);
		}
		if (!cfg.isSet("economy-plugin.hook-internal-economy-into-vault")) {
			cfg.set("economy-plugin.hook-internal-economy-into-vault", false);
		}
		if (!cfg.isSet("economy-plugin.starting-player-account-balance")) {
			cfg.set("economy-plugin.starting-player-account-balance", 0);
		}
		
		
		if (!cfg.isSet("sql.use-mysql")) {
			cfg.set("sql.use-mysql", false);
		}
		if (!cfg.isSet("sql.log-sql-statements")) {
			cfg.set("sql.log-sql-statements", false);
		}
		if (!cfg.isSet("sql.mysql-connection.username")) {
			cfg.set("sql.mysql-connection.username", "default_username");
		}
		if (!cfg.isSet("sql.mysql-connection.port")) {
			cfg.set("sql.mysql-connection.port", 3306);
		}
		if (!cfg.isSet("sql.mysql-connection.password")) {
			cfg.set("sql.mysql-connection.password", "default_password");
		}
		if (!cfg.isSet("sql.mysql-connection.host")) {
			cfg.set("sql.mysql-connection.host", "localhost");
		}
		if (!cfg.isSet("sql.mysql-connection.database")) {
			cfg.set("sql.mysql-connection.database", "minecraft");
		}
		
		if (!cfg.isSet("bank.max-ownerships-per-player")) {
			cfg.set("bank.max-ownerships-per-player", 3);
		}
		
		
		
		if (!cfg.isSet("language")) {
			cfg.set("language", "english");
		}

		
		if (!cfg.isSet("intervals.shop-check")) {
			cfg.set("intervals.shop-check", 6);
		}
		if (!cfg.isSet("intervals.save")) {
			cfg.set("intervals.save", 24000);
		}
		
		
		if (!cfg.isSet("tax.purchase")) {
			cfg.set("tax.purchase", 3);
		}
		if (!cfg.isSet("tax.initial")) {
			cfg.set("tax.initial", 100);
		}
		if (!cfg.isSet("tax.static")) {
			cfg.set("tax.static", 100);
		}
		if (!cfg.isSet("tax.enchant")) {
			cfg.set("tax.enchant", 100);
		}
		if (!cfg.isSet("tax.sales")) {
			cfg.set("tax.sales", 0);
		}
		if (!cfg.isSet("tax.dynamic.enable")) {
			cfg.set("tax.dynamic.enable", false);
		}
		if (!cfg.isSet("tax.dynamic.money-floor")) {
			cfg.set("tax.dynamic.money-floor", 0);
		}
		if (!cfg.isSet("tax.dynamic.money-cap")) {
			cfg.set("tax.dynamic.money-cap", 1000000);
		}
		if (!cfg.isSet("tax.dynamic.max-tax-percent")) {
			cfg.set("tax.dynamic.max-tax-percent", 100);
		}
		
		
		if (!cfg.isSet("shop.default-server-shop-account")) {
			cfg.set("shop.default-server-shop-account", "hyperconomy");
		}
		if (!cfg.isSet("shop.default-server-shop-account-initial-balance")) {
			cfg.set("shop.default-server-shop-account-initial-balance", 20000000);
		}
		if (!cfg.isSet("shop.display-shop-exit-message")) {
			cfg.set("shop.display-shop-exit-message", true);
		}
		if (!cfg.isSet("shop.max-stock-per-item-in-playershops")) {
			cfg.set("shop.max-stock-per-item-in-playershops", 100000);
		}
		if (!cfg.isSet("shop.max-player-shop-volume")) {
			cfg.set("shop.max-player-shop-volume", 1000);
		}
		if (!cfg.isSet("shop.max-player-shops-per-player")) {
			cfg.set("shop.max-player-shops-per-player", 1);
		}
		if (!cfg.isSet("shop.sell-remaining-if-less-than-requested-amount")) {
			cfg.set("shop.sell-remaining-if-less-than-requested-amount", true);
		}
		if (!cfg.isSet("shop.server-shops-have-unlimited-money")) {
			cfg.set("shop.server-shops-have-unlimited-money", false);
		}
		if (!cfg.isSet("shop.limit-info-commands-to-shops")) {
			cfg.set("shop.limit-info-commands-to-shops", false);
		}
		if (!cfg.isSet("shop.block-selling-in-creative-mode")) {
			cfg.set("shop.block-selling-in-creative-mode", false);
		}
		if (!cfg.isSet("shop.show-currency-symbol-after-price")) {
			cfg.set("shop.show-currency-symbol-after-price", false);
		}
		if (!cfg.isSet("shop.unlimited-stock-for-static-items")) {
			cfg.set("shop.unlimited-stock-for-static-items", false);
		}
		if (!cfg.isSet("shop.require-chest-shops-to-be-in-shop")) {
			cfg.set("shop.require-chest-shops-to-be-in-shop", false);
		}
		if (!cfg.isSet("shop.require-transaction-signs-to-be-in-shop")) {
			cfg.set("shop.require-transaction-signs-to-be-in-shop", false);
		}
		if (!cfg.isSet("shop.send-price-change-notifications-for")) {
			cfg.set("shop.send-price-change-notifications-for", "diamond,diamondblock,");
		}
		
		

		
		

		if (!cfg.isSet("history.days-to-save")) {
			cfg.set("history.days-to-save", 7);
		}
		
		
		
		if (!cfg.isSet("enchantment.classvalue.wood")) {
			cfg.set("enchantment.classvalue.wood", .1);
		}
		if (!cfg.isSet("enchantment.classvalue.leather")) {
			cfg.set("enchantment.classvalue.leather", .1);
		}
		if (!cfg.isSet("enchantment.classvalue.stone")) {
			cfg.set("enchantment.classvalue.stone", .15);
		}
		if (!cfg.isSet("enchantment.classvalue.chainmail")) {
			cfg.set("enchantment.classvalue.chainmail", .2);
		}
		if (!cfg.isSet("enchantment.classvalue.iron")) {
			cfg.set("enchantment.classvalue.iron", .25);
		}
		if (!cfg.isSet("enchantment.classvalue.gold")) {
			cfg.set("enchantment.classvalue.gold", .1);
		}
		if (!cfg.isSet("enchantment.classvalue.diamond")) {
			cfg.set("enchantment.classvalue.diamond", 1);
		}
		if (!cfg.isSet("enchantment.classvalue.bow")) {
			cfg.set("enchantment.classvalue.bow", .25);
		}
		if (!cfg.isSet("enchantment.classvalue.book")) {
			cfg.set("enchantment.classvalue.book", 1);
		}
		
		

		
		
		
		cfg.set("config", null);
		cfg.set("api-version", null);
		cfg.set("version", null);
	}
}
