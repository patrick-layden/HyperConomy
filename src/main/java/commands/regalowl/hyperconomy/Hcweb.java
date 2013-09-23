package regalowl.hyperconomy;



import org.bukkit.command.CommandSender;

public class Hcweb {
	Hcweb(String[] args, CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		WebHandler wh = hc.getWebHandler();
		try {
			if (args[0].equalsIgnoreCase("enable")) {
				hc.gYH().gFC("config").set("config.web-page.use-web-page", true);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_ENABLED"));
			} else if (args[0].equalsIgnoreCase("disable")) {
				hc.gYH().gFC("config").set("config.web-page.use-web-page", false);
				hc.s().loadData();
				wh.endServer();
				sender.sendMessage(L.get("WEB_PAGE_DISABLED"));
			} else if (args[0].equalsIgnoreCase("background")) {
				hc.gYH().gFC("config").set("config.web-page.background-color", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("tabledata")) {
				hc.gYH().gFC("config").set("config.web-page.table-data-color", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("fontsize")) {
				hc.gYH().gFC("config").set("config.web-page.font-size", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("font")) {
				hc.gYH().gFC("config").set("config.web-page.font", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("port")) {
				hc.gYH().gFC("config").set("config.web-page.port", Integer.parseInt(args[1]));
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("economy")) {
				hc.gYH().gFC("config").set("config.web-page.web-page-economy", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("font")) {
				hc.gYH().gFC("config").set("config.web-page.font-color", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("border")) {
				hc.gYH().gFC("config").set("config.web-page.border-color", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("increase")) {
				hc.gYH().gFC("config").set("config.web-page.increase-value-color", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("decrease")) {
				hc.gYH().gFC("config").set("config.web-page.decrease-value-color", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("highlight")) {
				hc.gYH().gFC("config").set("config.web-page.highlight-row-color", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("header")) {
				hc.gYH().gFC("config").set("config.web-page.header-color", args[1]);
				hc.s().loadData();
				wh.endServer();
				wh.startServer();
				sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("refresh")) {
				hc.s().loadData();
				wh.updatePages();
			} else if (args[0].equalsIgnoreCase("setdefault")) {
	    		hc.gYH().gFC("config").set("config.web-page.background-color", "8FA685");
	    		hc.gYH().gFC("config").set("config.web-page.font-color", "F2F2F2");
	    		hc.gYH().gFC("config").set("config.web-page.border-color", "091926");
	    		hc.gYH().gFC("config").set("config.web-page.increase-value-color", "C8D9B0");
	    		hc.gYH().gFC("config").set("config.web-page.decrease-value-color", "F2B2A8");
	    		hc.gYH().gFC("config").set("config.web-page.highlight-row-color", "8FA685");
	    		hc.gYH().gFC("config").set("config.web-page.header-color", "091926");
	    		hc.gYH().gFC("config").set("config.web-page.table-data-color", "314A59");
	    		hc.gYH().gFC("config").set("config.web-page.font-size", 12);
	    		hc.gYH().gFC("config").set("config.web-page.font", "verdana");
	    		hc.gYH().gFC("config").set("config.web-page.port", 7777);
	    		hc.s().loadData();
				wh.endServer();
				wh.startServer();
	    		sender.sendMessage(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("status")) {
				if (wh.getServer() == null) {
					sender.sendMessage(L.get("SERVER_NULL"));
				} else if (wh.getServer().isStopping()) {
					sender.sendMessage(L.get("SERVER_STOPPING"));
				} else  if (wh.getServer().isStarting()) {
					sender.sendMessage(L.get("SERVER_STARTING"));
				} else  if (wh.getServer().isFailed()) {
					sender.sendMessage(L.get("SERVER_FAILED"));
				} else  if (wh.getServer().isStopped()) {
					sender.sendMessage(L.get("SERVER_STOPPPED"));
				} else  if (wh.getServer().isRunning()) {
					sender.sendMessage(L.get("SERVER_RUNNING"));
				}
			} else {
				sender.sendMessage(L.get("HCWEB_INVALID"));
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
			sender.sendMessage(L.get("HCWEB_INVALID"));
			return;
		}
	}
}
