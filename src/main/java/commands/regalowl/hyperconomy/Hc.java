package regalowl.hyperconomy;


import org.bukkit.command.CommandSender;


public class Hc {
	Hc(CommandSender sender, String args[]) {
		LanguageFile L = HyperConomy.hc.getLanguageFile();
		try {
			if (args.length == 0) {
				sender.sendMessage(L.get("LINE_BREAK"));
				sender.sendMessage(L.get("HC_BUY"));
				sender.sendMessage(L.get("HC_SELL"));
				sender.sendMessage(L.get("HC_SHOP"));
				sender.sendMessage(L.get("HC_INFO"));
				sender.sendMessage(L.get("HC_ECON"));
				sender.sendMessage(L.get("HC_PARAMS"));
				sender.sendMessage(L.get("LINE_BREAK"));
			} else if (args.length == 1) {
				String type = args[0];
				if (type.equalsIgnoreCase("sell")) {
					sender.sendMessage(L.get("HC_SELL_SELL"));
					sender.sendMessage(L.get("HC_SELL_HS"));
					sender.sendMessage(L.get("HC_SELL_ESELL"));
					sender.sendMessage(L.get("HC_SELL_SELLALL"));
					sender.sendMessage(L.get("HC_SELL_MORE"));
					sender.sendMessage(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("buy")) {
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.get("HC_BUY_BUY"));
					sender.sendMessage(L.get("HC_BUY_HB"));
					sender.sendMessage(L.get("HC_BUY_BUYID"));
					sender.sendMessage(L.get("HC_BUY_EBUY"));
					sender.sendMessage(L.get("HC_BUY_MORE"));
					sender.sendMessage(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("shop")) {
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.get("HC_SHOP_MANAGESHOP"));
					sender.sendMessage(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("info")) {
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.get("HC_INFO_VALUE"));
					sender.sendMessage(L.get("HC_INFO_HV"));
					sender.sendMessage(L.get("HC_INFO_II"));
					sender.sendMessage(L.get("HC_INFO_TOPITEMS"));
					sender.sendMessage(L.get("HC_INFO_TOPENCHANTS"));
					sender.sendMessage(L.get("HC_INFO_BROWSESHOP"));
					sender.sendMessage(L.get("HC_INFO_XPINFO"));
					sender.sendMessage(L.get("HC_INFO_EVALUE"));
					sender.sendMessage(L.get("HC_INFO_MORE"));
					sender.sendMessage(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("params")) {
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.get("HC_PARAMS_REQUIRED"));
					sender.sendMessage(L.get("HC_PARAMS_OPTIONAL"));
					sender.sendMessage(L.get("HC_PARAMS_ADDITIONAL"));
					sender.sendMessage(L.get("HC_PARAMS_NAME"));
					sender.sendMessage(L.get("HC_PARAMS_COMMAND"));
					sender.sendMessage(L.get("LINE_BREAK"));
				} else if (type.equalsIgnoreCase("econ")) {
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.get("HC_ECON_HCB"));
					sender.sendMessage(L.get("HC_ECON_HCP"));
					sender.sendMessage(L.get("HC_ECON_HCT"));
					sender.sendMessage(L.get("HC_ECON_MORE"));
					sender.sendMessage(L.get("LINE_BREAK"));
				}
			} else if (args.length == 2) {
				String type = args[0];
				String subtype = args[1];
				if (type.equalsIgnoreCase("sell")) {
					if (subtype.equalsIgnoreCase("sell")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_SELL_SELL"));
						sender.sendMessage(L.get("HC_SELL_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hs")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_SELL_HS"));
						sender.sendMessage(L.get("HC_HS_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("esell")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_SELL_ESELL"));
						sender.sendMessage(L.get("HC_ESELL_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("sellall")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_SELL_SELLALL"));
						sender.sendMessage(L.get("HC_SELLALL_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					}
				} else if (type.equalsIgnoreCase("buy")) {
					if (subtype.equalsIgnoreCase("buy")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_BUY_BUY"));
						sender.sendMessage(L.get("HC_BUY_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hb")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_BUY_HB"));
						sender.sendMessage(L.get("HC_HB_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("buyid")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_BUY_BUYID"));
						sender.sendMessage(L.get("HC_BUYID_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("ebuy")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_BUY_EBUY"));
						sender.sendMessage(L.get("HC_EBUY_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					}
				} else if (type.equalsIgnoreCase("shop")) {
					if (subtype.equalsIgnoreCase("manageshop")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_SHOP_MANAGESHOP"));
						sender.sendMessage(L.get("HC_MANAGESHOP_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					}
				} else if (type.equalsIgnoreCase("econ")) {
					if (subtype.equalsIgnoreCase("hcb")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_ECON_HCB"));
						sender.sendMessage(L.get("HC_HCB_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hcp")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_ECON_HCP"));
						sender.sendMessage(L.get("HC_HCP_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hct")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_ECON_HCT"));
						sender.sendMessage(L.get("HC_HCT_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					}
				} else if (type.equalsIgnoreCase("info")) {
					if (subtype.equalsIgnoreCase("value")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_INFO_VALUE"));
						sender.sendMessage(L.get("HC_VALUE_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("hv")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_INFO_HV"));
						sender.sendMessage(L.get("HC_HV_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("ii")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_INFO_II"));
						sender.sendMessage(L.get("HC_II_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("topitems")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_INFO_TOPITEMS"));
						sender.sendMessage(L.get("HC_TOPITEMS_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("topenchants")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_INFO_TOPENCHANTS"));
						sender.sendMessage(L.get("HC_TOPENCHANTS_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("browseshop")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_INFO_BROWSESHOP"));
						sender.sendMessage(L.get("HC_BROWSESHOP_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("evalue")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_INFO_EVALUE"));
						sender.sendMessage(L.get("HC_EVALUE_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					} else if (subtype.equalsIgnoreCase("xpinfo")) {
						sender.sendMessage(L.get("LINE_BREAK"));
						sender.sendMessage(L.get("HC_INFO_XPINFO"));
						sender.sendMessage(L.get("HC_XPINFO_DETAIL"));
						sender.sendMessage(L.get("LINE_BREAK"));
					}
				}
			} else {
				// do nothing
			}
			/*
			if (args[0].equalsIgnoreCase("test") && sender.hasPermission("hyperconomy.admin")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					new Test(p, args);
				}
			}
			*/
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("HC_INVALID"));
		}
	}
}
