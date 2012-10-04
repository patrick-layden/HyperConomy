package regalowl.hyperconomy;

import static regalowl.hyperconomy.Messages.*;

import org.bukkit.command.CommandSender;


public class Hc {
	Hc(CommandSender sender, String args[]) {
		try {
			if (args.length == 0) {
				sender.sendMessage(LINE_BREAK);
				sender.sendMessage(HC_BUY);
				sender.sendMessage(HC_SELL);
				sender.sendMessage(HC_INFO);
				sender.sendMessage(HC_PARAMS);
				sender.sendMessage(LINE_BREAK);
			} else if (args.length == 1) {
				String type = args[0];
				if (type.equalsIgnoreCase("sell")) {
					sender.sendMessage(HC_SELL_SELL);
					sender.sendMessage(HC_SELL_HS);
					sender.sendMessage(HC_SELL_ESELL);
					sender.sendMessage(HC_SELL_SELLALL);
					sender.sendMessage(HC_SELL_MORE);
					sender.sendMessage(LINE_BREAK);
				} else if (type.equalsIgnoreCase("buy")) {
					sender.sendMessage(LINE_BREAK);
					sender.sendMessage(HC_BUY_BUY);
					sender.sendMessage(HC_BUY_HB);
					sender.sendMessage(HC_BUY_BUYID);
					sender.sendMessage(HC_BUY_EBUY);
					sender.sendMessage(HC_BUY_MORE);
					sender.sendMessage(LINE_BREAK);
				} else if (type.equalsIgnoreCase("info")) {
					sender.sendMessage(LINE_BREAK);
					sender.sendMessage(HC_INFO_VALUE);
					sender.sendMessage(HC_INFO_HV);
					sender.sendMessage(HC_INFO_II);
					sender.sendMessage(HC_INFO_TOPITEMS);
					sender.sendMessage(HC_INFO_TOPENCHANTS);
					sender.sendMessage(HC_INFO_BROWSESHOP);
					sender.sendMessage(HC_INFO_XPINFO);
					sender.sendMessage(HC_INFO_EVALUE);
					sender.sendMessage(HC_INFO_MORE);
					sender.sendMessage(LINE_BREAK);
				} else if (type.equalsIgnoreCase("params")) {
					sender.sendMessage(LINE_BREAK);
					sender.sendMessage(HC_PARAMS_REQUIRED);
					sender.sendMessage(HC_PARAMS_OPTIONAL);
					sender.sendMessage(HC_PARAMS_ADDITIONAL);
					sender.sendMessage(HC_PARAMS_NAME);
					sender.sendMessage(HC_PARAMS_COMMAND);
					sender.sendMessage(LINE_BREAK);
				}
			} else if (args.length == 2) {
				String type = args[0];
				String subtype = args[1];
				if (type.equalsIgnoreCase("sell")) {
					if (subtype.equalsIgnoreCase("sell")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_SELL_SELL);
						sender.sendMessage(HC_SELL_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("hs")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_SELL_HS);
						sender.sendMessage(HC_HS_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("esell")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_SELL_ESELL);
						sender.sendMessage(HC_ESELL_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("sellall")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_SELL_SELLALL);
						sender.sendMessage(HC_SELLALL_DETAIL);
						sender.sendMessage(LINE_BREAK);
					}
				} else if (type.equalsIgnoreCase("buy")) {
					if (subtype.equalsIgnoreCase("buy")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_BUY_BUY);
						sender.sendMessage(HC_BUY_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("hb")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_BUY_HB);
						sender.sendMessage(HC_HB_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("buyid")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_BUY_BUYID);
						sender.sendMessage(HC_BUYID_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("ebuy")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_BUY_EBUY);
						sender.sendMessage(HC_EBUY_DETAIL);
						sender.sendMessage(LINE_BREAK);
					}
				} else if (type.equalsIgnoreCase("info")) {
					if (subtype.equalsIgnoreCase("value")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_INFO_VALUE);
						sender.sendMessage(HC_VALUE_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("hv")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_INFO_HV);
						sender.sendMessage(HC_HV_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("ii")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_INFO_II);
						sender.sendMessage(HC_II_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("topitems")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_INFO_TOPITEMS);
						sender.sendMessage(HC_TOPITEMS_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("topenchants")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_INFO_TOPENCHANTS);
						sender.sendMessage(HC_TOPENCHANTS_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("browseshop")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_INFO_BROWSESHOP);
						sender.sendMessage(HC_BROWSESHOP_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("evalue")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_INFO_EVALUE);
						sender.sendMessage(HC_EVALUE_DETAIL);
						sender.sendMessage(LINE_BREAK);
					} else if (subtype.equalsIgnoreCase("xpinfo")) {
						sender.sendMessage(LINE_BREAK);
						sender.sendMessage(HC_INFO_XPINFO);
						sender.sendMessage(HC_XPINFO_DETAIL);
						sender.sendMessage(LINE_BREAK);
					}
				}
			} else {
				// do nothing
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(HC_INVALID);
		}
	}
}
