package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.HyperLock;
import regalowl.hyperconomy.util.LanguageFile;



public class HcCommand extends BaseCommand implements HyperCommand {
	
	public HcCommand() {
		super(false);
	}
	
	public CommandData onCommand(CommandData data) {
		String args[] = data.getArgs();
		HC hc = HC.hc;
		HyperLock hl = hc.getHyperLock();
		LanguageFile L = hc.getLanguageFile();
		HyperPlayer hp = data.getHyperPlayer();
		if ((args.length == 0 && !hl.isLocked(hp)) || (args.length >= 1 && !args[0].equalsIgnoreCase("enable") && !args[0].equalsIgnoreCase("disable") && !hl.isLocked(hp))) {
			displayInfo(data);
		} else {
			if (!data.isPlayer() || hp.hasPermission("hyperconomy.admin")) {
				if (args.length == 1 && args[0].equalsIgnoreCase("enable") && hl.fullLock()) {
					hc.load();
					hc.enable();
					data.addResponse(L.get("HC_HYPERCONOMY_ENABLED"));
					data.addResponse(L.get("FILES_RELOADED"));
					data.addResponse(L.get("SHOP_UNLOCKED"));
				} else if (args.length == 1 && args[0].equalsIgnoreCase("disable") && !hl.fullLock()) {
					data.addResponse(L.get("HC_HYPERCONOMY_DISABLED"));
					data.addResponse(L.get("SHOP_LOCKED"));
					hl.setFullLock(true);
					hc.disable(true);
				}
			}
		}
		data.setSuccessful();
		return data;
	}
	
	
	private void displayInfo(CommandData data) {
		String args[] = data.getArgs();
		LanguageFile L = HC.hc.getLanguageFile();
		try {
			if (args.length == 0) {
				data.addResponse(L.get("LINE_BREAK"));
				data.addResponse(L.get("HC_BUY"));
				data.addResponse(L.get("HC_SELL"));
				data.addResponse(L.get("HC_SHOP"));
				data.addResponse(L.get("HC_INFO"));
				data.addResponse(L.get("HC_ECON"));
				data.addResponse(L.get("HC_PARAMS"));
				data.addResponse(L.get("LINE_BREAK"));
			} else if (args.length == 1) {
				String type = args[0];
				if (type.equalsIgnoreCase("sell")) {
					data.addResponse(L.get("HC_SELL_SELL"));
					data.addResponse(L.get("HC_SELL_HS"));
					data.addResponse(L.get("HC_SELL_SELLALL"));
					data.addResponse(L.get("HC_SELL_MORE"));
					data.addResponse(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("buy")) {
					data.addResponse(L.get("LINE_BREAK"));
					data.addResponse(L.get("HC_BUY_BUY"));
					data.addResponse(L.get("HC_BUY_HB"));
					data.addResponse(L.get("HC_BUY_MORE"));
					data.addResponse(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("shop")) {
					data.addResponse(L.get("LINE_BREAK"));
					data.addResponse(L.get("HC_SHOP_MANAGESHOP"));
					data.addResponse(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("info")) {
					data.addResponse(L.get("LINE_BREAK"));
					data.addResponse(L.get("HC_INFO_VALUE"));
					data.addResponse(L.get("HC_INFO_HV"));
					data.addResponse(L.get("HC_INFO_II"));
					data.addResponse(L.get("HC_INFO_TOPITEMS"));
					data.addResponse(L.get("HC_INFO_TOPENCHANTS"));
					data.addResponse(L.get("HC_INFO_BROWSESHOP"));
					data.addResponse(L.get("HC_INFO_XPINFO"));
					data.addResponse(L.get("HC_INFO_MORE"));
					data.addResponse(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("params")) {
					data.addResponse(L.get("LINE_BREAK"));
					data.addResponse(L.get("HC_PARAMS_REQUIRED"));
					data.addResponse(L.get("HC_PARAMS_OPTIONAL"));
					data.addResponse(L.get("HC_PARAMS_ADDITIONAL"));
					data.addResponse(L.get("HC_PARAMS_NAME"));
					data.addResponse(L.get("HC_PARAMS_COMMAND"));
					data.addResponse(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("econ")) {
					data.addResponse(L.get("LINE_BREAK"));
					data.addResponse(L.get("HC_ECON_HCB"));
					data.addResponse(L.get("HC_ECON_HCP"));
					data.addResponse(L.get("HC_ECON_HCT"));
					data.addResponse(L.get("HC_ECON_MORE"));
					data.addResponse(L.get("LINE_BREAK"));
				}
			} else if (args.length == 2) {
				String type = args[0];
				String subtype = args[1];
				if (type.equalsIgnoreCase("sell")) {
					if (subtype.equalsIgnoreCase("sell")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_SELL_SELL"));
						data.addResponse(L.get("HC_SELL_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hs")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_SELL_HS"));
						data.addResponse(L.get("HC_HS_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("sellall")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_SELL_SELLALL"));
						data.addResponse(L.get("HC_SELLALL_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					}
				} else if (type.equalsIgnoreCase("buy")) {
					if (subtype.equalsIgnoreCase("buy")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_BUY_BUY"));
						data.addResponse(L.get("HC_BUY_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hb")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_BUY_HB"));
						data.addResponse(L.get("HC_HB_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					}
				} else if (type.equalsIgnoreCase("shop")) {
					if (subtype.equalsIgnoreCase("manageshop")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_SHOP_MANAGESHOP"));
						data.addResponse(L.get("HC_MANAGESHOP_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					}
				} else if (type.equalsIgnoreCase("econ")) {
					if (subtype.equalsIgnoreCase("hcb")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_ECON_HCB"));
						data.addResponse(L.get("HC_HCB_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hcp")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_ECON_HCP"));
						data.addResponse(L.get("HC_HCP_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hct")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_ECON_HCT"));
						data.addResponse(L.get("HC_HCT_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					}
				} else if (type.equalsIgnoreCase("info")) {
					if (subtype.equalsIgnoreCase("value")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_INFO_VALUE"));
						data.addResponse(L.get("HC_VALUE_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hv")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_INFO_HV"));
						data.addResponse(L.get("HC_HV_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("ii")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_INFO_II"));
						data.addResponse(L.get("HC_II_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("topitems")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_INFO_TOPITEMS"));
						data.addResponse(L.get("HC_TOPITEMS_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("topenchants")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_INFO_TOPENCHANTS"));
						data.addResponse(L.get("HC_TOPENCHANTS_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("browseshop")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_INFO_BROWSESHOP"));
						data.addResponse(L.get("HC_BROWSESHOP_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("xpinfo")) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("HC_INFO_XPINFO"));
						data.addResponse(L.get("HC_XPINFO_DETAIL"));
						data.addResponse(L.get("LINE_BREAK"));
					}
				}
			}
		} catch (Exception e) {
			data.addResponse(L.get("HC_INVALID"));
		}
	}
}

