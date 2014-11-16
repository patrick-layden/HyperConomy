package regalowl.hyperconomy.command;


import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.util.Backup;

public class Hceconomy extends BaseCommand implements HyperCommand {
	
	
	public Hceconomy(HyperConomy hc) {
		super(hc, false);
	}


	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		if (args.length == 0) {
			data.addResponse(L.get("HCECONOMY_INVALID"));
			return data;
		}
		
		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
			try {
				if (dm.economyExists(args[1])) {
					data.addResponse(L.get("ECONOMY_ALREADY_EXISTS"));
					return data;
				}
				String template = "default";
				if (args.length >= 3 && dm.economyExists(args[2])) {
					template = args[2];
				}
				boolean clone = false;
				if (args.length >= 4 && args[3].equalsIgnoreCase("clone")) {
					clone = true;
				}
				hc.getDataManager().createNewEconomy(args[1], template, clone);
				data.addResponse(L.get("NEW_ECONOMY_CREATED"));
			} catch (Exception e) {
				data.addResponse(L.get("HCECONOMY_CREATE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("delete")) {
			try {
				String economy = args[1];
				if (economy.equalsIgnoreCase("default")) {
					data.addResponse(L.get("CANT_DELETE_DEFAULT_ECONOMY"));
					return data;
				}
				if (!dm.economyExists(economy)) {
					data.addResponse(L.get("ECONOMY_DOESNT_EXIST"));
					return data;
				}
				if (hc.getConf().getBoolean("enable-feature.automatic-backups")) {
					new Backup(hc);
				}
				for (Shop shop:dm.getHyperShopManager().getShops()) {
					if (shop.getEconomy().equalsIgnoreCase(economy)) {
						shop.setEconomy("default");
					}
				}
				for (HyperPlayer hp:dm.getHyperPlayerManager().getHyperPlayers()) {
					if (hp.getEconomy().equalsIgnoreCase(economy)) {
						hp.setEconomy("default");
					}
				}
				dm.deleteEconomy(economy);
				data.addResponse(L.get("ECONOMY_DELETED"));
			} catch (Exception e) {
				data.addResponse(L.get("HCECONOMY_DELETE_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("account")) {
			try {
				String economy = args[1];
				if (!dm.economyExists(economy)) {
					data.addResponse(L.get("ECONOMY_DOESNT_EXIST"));
					return data;
				}
				String account = args[2];
				if (!dm.accountExists(account)) {
					data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
					return data;
				}
				dm.getEconomy(economy).setDefaultAccount(dm.getAccount(account));
				data.addResponse(L.get("HCECONOMY_ACCOUNT_SET"));
			} catch (Exception e) {
				data.addResponse(L.get("HCECONOMY_ACCOUNT_INVALID"));
			}
		} else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {
			ArrayList<String> economies = dm.getEconomyList();
			data.addResponse("&b" + economies.toString());
		} else {
			data.addResponse(L.get("HCECONOMY_INVALID"));
			return data;
		}
		return data;
	}
}
