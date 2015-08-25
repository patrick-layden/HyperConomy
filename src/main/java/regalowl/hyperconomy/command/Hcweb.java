

package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;


public class Hcweb extends BaseCommand implements HyperCommand {

	public Hcweb(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = hp.getHyperEconomy();
		
		
		HyperConomy hc = hcw.getHC();
		LanguageFile L = hc.getLanguageFile();
		WebHandler wh = hcw.getWebHandler();
		try {
			if (args.length == 0) {
				data.addResponse(L.get("HCWEB_INVALID"));
				return true;
			}
			if (args[0].equalsIgnoreCase("enable")) {
				hcw.gYH().gFC("config").set("config.web-page.use-web-page", true);
				data.addResponse(L.get("WEB_PAGE_ENABLED"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("disable")) {
				hcw.gYH().gFC("config").set("config.web-page.use-web-page", false);
				data.addResponse(L.get("WEB_PAGE_DISABLED"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("background")) {
				hcw.gYH().gFC("config").set("config.web-page.background-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("tabledata")) {
				hcw.gYH().gFC("config").set("config.web-page.table-data-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("fontsize")) {
				hcw.gYH().gFC("config").set("config.web-page.font-size", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("font")) {
				hcw.gYH().gFC("config").set("config.web-page.font", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("port")) {
				hcw.gYH().gFC("config").set("config.web-page.port", Integer.parseInt(args[1]));
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("fontcolor")) {
				hcw.gYH().gFC("config").set("config.web-page.font-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("border")) {
				hcw.gYH().gFC("config").set("config.web-page.border-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("increase")) {
				hcw.gYH().gFC("config").set("config.web-page.increase-value-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("decrease")) {
				hcw.gYH().gFC("config").set("config.web-page.decrease-value-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("highlight")) {
				hcw.gYH().gFC("config").set("config.web-page.highlight-row-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("header")) {
				hcw.gYH().gFC("config").set("config.web-page.header-color", args[1]);
				data.addResponse(L.get("WEB_PAGE_SET"));
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("refresh")) {
				hcw.restart();
			} else if (args[0].equalsIgnoreCase("setdefault")) {
				hcw.gYH().gFC("config").set("config.web-page.background-color", "8FA685");
				hcw.gYH().gFC("config").set("config.web-page.font-color", "F2F2F2");
				hcw.gYH().gFC("config").set("config.web-page.border-color", "091926");
	    		hcw.gYH().gFC("config").set("config.web-page.increase-value-color", "C8D9B0");
	    		hcw.gYH().gFC("config").set("config.web-page.decrease-value-color", "F2B2A8");
	    		hcw.gYH().gFC("config").set("config.web-page.highlight-row-color", "8FA685");
	    		hcw.gYH().gFC("config").set("config.web-page.header-color", "091926");
	    		hcw.gYH().gFC("config").set("config.web-page.table-data-color", "314A59");
	    		hcw.gYH().gFC("config").set("config.web-page.font-size", 12);
	    		hcw.gYH().gFC("config").set("config.web-page.font", "verdana");
	    		hcw.gYH().gFC("config").set("config.web-page.port", 7777);
				hcw.restart();
	    		data.addResponse(L.get("WEB_PAGE_SET"));
			} else if (args[0].equalsIgnoreCase("status")) {
				if (wh == null || wh.getServer() == null) {
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
			return true;
		} catch (Exception e) {
			hcw.getSimpleDataLib().getErrorWriter().writeError(e);
			data.addResponse(L.get("HCWEB_INVALID"));
			return true;
		}
		
		
		
		
		
		try {
			if (args.length == 0) {
				data.addResponse(L.get("BUY_INVALID"));
				return data;
			}
			Shop s = hc.getHyperShopManager().getShop(hp);
			TradeObject ho = he.getTradeObject(args[0], hc.getHyperShopManager().getShop(hp));
			if (ho == null) {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				return data;
			}
			int amount = 1;
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("max")) {
					if (ho.getType() == TradeObjectType.ITEM) {
						amount = hp.getInventory().getAvailableSpace(ho.getItem());
						if (amount > ho.getStock()) amount = (int)Math.floor(ho.getStock());
					} else if (ho.getType() == TradeObjectType.EXPERIENCE) {
						amount = (int) ho.getStock();
					} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
						amount = 1;
					}
				} else {
					try {
						amount = Integer.parseInt(args[1]);
						if (amount > 10000) amount = 10000;
					} catch (Exception e) {
						data.addResponse(L.get("BUY_INVALID"));
						return data;
					}
				}
			}
			PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
			pt.setObeyShops(true);
			pt.setHyperObject(ho);
			pt.setAmount(amount);
			if (s != null) pt.setTradePartner(s.getOwner());
			TransactionResponse response = hp.processTransaction(pt);
			response.sendMessages();
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
}

