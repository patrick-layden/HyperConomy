package regalowl.hyperconomy;



import org.bukkit.command.CommandSender;

public class Hcweb {
	Hcweb(String[] args, CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args[0].equalsIgnoreCase("enable")) {
				hc.getYaml().getConfig().set("config.web-page.use-web-page", true);
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_ENABLED"));
			} else if (args[0].equalsIgnoreCase("disable")) {
				hc.getYaml().getConfig().set("config.web-page.use-web-page", false);
				hc.disableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_DISABLED"));
			} else if (args[0].equalsIgnoreCase("background")) {
				hc.getYaml().getConfig().set("config.web-page.background-color", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("tabledata")) {
				hc.getYaml().getConfig().set("config.web-page.table-data-color", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("fontsize")) {
				hc.getYaml().getConfig().set("config.web-page.font-size", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("font")) {
				hc.getYaml().getConfig().set("config.web-page.font", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("port")) {
				hc.getYaml().getConfig().set("config.web-page.port", Integer.parseInt(args[1]));
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("economy")) {
				hc.getYaml().getConfig().set("config.web-page.web-page-economy", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("font")) {
				hc.getYaml().getConfig().set("config.web-page.font-color", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("border")) {
				hc.getYaml().getConfig().set("config.web-page.border-color", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("increase")) {
				hc.getYaml().getConfig().set("config.web-page.increase-value-color", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("decrease")) {
				hc.getYaml().getConfig().set("config.web-page.decrease-value-color", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("highlight")) {
				hc.getYaml().getConfig().set("config.web-page.highlight-row-color", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("header")) {
				hc.getYaml().getConfig().set("config.web-page.header-color", args[1]);
				hc.disableWebPage();
				hc.enableWebPage();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("refresh")) {
				hc.getHyperWebStart().updatePages();
			} else {
				sender.sendMessage(L.get("HCWEB_INVALID"));
			}
			
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("HCWEB_INVALID"));
			return;
		}
	}
}
