

package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.webpage.HyperConomy_Web;
import regalowl.hyperconomy.webpage.WebHandler;


public class Hcweb extends BaseCommand implements HyperCommand {

	public Hcweb(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperConomy_Web hcw = hc.getHyperConomyWeb();
		WebHandler wh = hcw.getWebHandler();
		try {
			if (args.length == 0) {
				data.addResponse(L.get("HCWEB_INVALID"));
				return data;
			}
			if (args[0].equalsIgnoreCase("enable")) {
				hc.getConf().set("web-page.enable", true);
				data.addResponse(L.get("WEB_PAGE_ENABLED"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("disable")) {
				hc.getConf().set("web-page.enable", false);
				data.addResponse(L.get("WEB_PAGE_DISABLED"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("background")) {
				hc.getConf().set("web-page.background-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("tabledata")) {
				hc.getConf().set("web-page.table-data-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("fontsize")) {
				hc.getConf().set("web-page.font-size", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("font")) {
				hc.getConf().set("web-page.font", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("port")) {
				hc.getConf().set("web-page.port", Integer.parseInt(args[1]));
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("fontcolor")) {
				hc.getConf().set("web-page.font-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("border")) {
				hc.getConf().set("web-page.border-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("increase")) {
				hc.getConf().set("web-page.increase-value-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("decrease")) {
				hc.getConf().set("web-page.decrease-value-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("highlight")) {
				hc.getConf().set("web-page.highlight-row-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("header")) {
				hc.getConf().set("web-page.header-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("refresh")) {
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("setdefault")) {
				hc.getConf().set("web-page.background-color", "8FA685");
				hc.getConf().set("web-page.font-color", "F2F2F2");
				hc.getConf().set("web-page.border-color", "091926");
	    		hc.getConf().set("web-page.increase-value-color", "C8D9B0");
	    		hc.getConf().set("web-page.decrease-value-color", "F2B2A8");
	    		hc.getConf().set("web-page.highlight-row-color", "8FA685");
	    		hc.getConf().set("web-page.header-color", "091926");
	    		hc.getConf().set("web-page.table-data-color", "314A59");
	    		hc.getConf().set("web-page.font-size", 12);
	    		hc.getConf().set("web-page.font", "verdana");
	    		hc.getConf().set("web-page.port", 7777);
				hcw.restart();
	    		data.addResponse(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("status")) {
				if (!hcw.enabled()) {
					data.addResponse(L.get("SERVER_DISABLED"));
				} else if (wh == null || wh.getServer() == null) {
					data.addResponse(L.get("SERVER_NULL"));
				} else if (wh.getServer().isStopping()) {
					data.addResponse(L.get("SERVER_STOPPING"));
				} else  if (wh.getServer().isStarting()) {
					data.addResponse(L.get("SERVER_STARTING"));
				} else  if (wh.getServer().isFailed()) {
					data.addResponse(L.get("SERVER_FAILED"));
				} else  if (wh.getServer().isStopped()) {
					data.addResponse(L.get("SERVER_STOPPPED"));
				} else  if (wh.getServer().isRunning()) {
					data.addResponse(L.get("SERVER_RUNNING"));
				}
			} else {
				data.addResponse(L.get("HCWEB_INVALID"));
			}
			return data;
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
			data.addResponse(L.get("HCWEB_INVALID"));
			return data;
		}
		

	}
}

