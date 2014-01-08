package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;

public class Browseshop {
	
	Browseshop(String args[], CommandSender sender, Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		EconomyManager em = hc.getEconomyManager();
		CommonFunctions cf = hc.gCF();
		LanguageFile L = hc.getLanguageFile();
		ArrayList<String> aargs = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			aargs.add(args[i]);
		}
		try {
			boolean requireShop = hc.gYH().gFC("config").getBoolean("config.limit-info-commands-to-shops");
    		if (player != null) {
    			if ((requireShop && !em.inAnyShop(player)) && !player.hasPermission("hyperconomy.admin")) {
    				sender.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
    				return;
    			}			
    		}
			if (aargs.size() > 3) {
				sender.sendMessage(L.get("BROWSE_SHOP_INVALID"));
				return;
			}
			boolean alphabetic = false;
			if (aargs.contains("-a") && aargs.size() >= 2) {
				alphabetic = true;
				aargs.remove("-a");
			}
			int page;
			if (aargs.size() <= 2) {
				try {
					page = Integer.parseInt(aargs.get(0));
					aargs.remove(0);
				} catch (Exception e) {
					try {
						page = Integer.parseInt(aargs.get(1));
						aargs.remove(1);
					} catch (Exception f) {
						page = 1;
					}
				}
			} else {
				sender.sendMessage(L.get("BROWSE_SHOP_INVALID"));
				return;
			}
			String input = "";
			if (aargs.size() == 1) {
				input = aargs.get(0);
			} else {
				sender.sendMessage(L.get("BROWSE_SHOP_INVALID"));
				return;
			}
    		Shop shop = null;
    		if (player != null) {
    			if (!em.inAnyShop(player)) {
    				shop = null;
    			} else {
    				shop = em.getShop(player);
    			}		
    		}
			ArrayList<String> names = he.getNames();
			ArrayList<String> rnames = new ArrayList<String>();
			int i = 0;
			while(i < names.size()) {
				String cname = names.get(i);
				HyperObject ho = he.getHyperObject(cname);
				String displayName = ho.getDisplayName();
				if (alphabetic) {
					if (ho.nameStartsWith(input)) {
						if (shop == null || !shop.isBanned(cname)) {
							if (shop instanceof PlayerShop) {
								PlayerShop ps = (PlayerShop)shop;
								PlayerShopObject pso = ps.getPlayerShopObject(ho);
								if (pso != null) {
									if (pso.getStatus() == HyperObjectStatus.NONE) {
										if (ps.isAllowed(em.getHyperPlayer(player))) {
											rnames.add(displayName);
										}
									} else {
										rnames.add(displayName);
									}
								}
							} else {
								rnames.add(displayName);
							}
						}
					}
				} else {
					if (ho.nameContains(input)) {
						if (shop == null || !shop.isBanned(cname)) {
							if (shop instanceof PlayerShop) {
								PlayerShop ps = (PlayerShop)shop;
								PlayerShopObject pso = ps.getPlayerShopObject(ho);
								if (pso != null) {
									if (pso.getStatus() == HyperObjectStatus.NONE) {
										if (ps.isAllowed(em.getHyperPlayer(player))) {
											rnames.add(displayName);
										}
									} else {
										rnames.add(displayName);
									}
								}
							} else {
								rnames.add(displayName);
							}
						}
					}
				}
				i++;
			}
			Collections.sort(rnames, String.CASE_INSENSITIVE_ORDER);
			int numberpage = page * 10;
			int count = 0;
			int rsize = rnames.size();
			double maxpages = rsize/10;
			maxpages = Math.ceil(maxpages);
			int maxpi = (int)maxpages + 1;
			sender.sendMessage(ChatColor.RED + L.get("PAGE") + " " + ChatColor.WHITE + "(" + ChatColor.RED + "" + page + ChatColor.WHITE + "/" + ChatColor.RED + "" + maxpi + ChatColor.WHITE + ")");
			while (count < numberpage) {
				if (count > ((page * 10) - 11)) {
					if (count < rsize) {
						String iname = rnames.get(count);
			            Double cost = 0.0;
			            double stock = 0;
			            HyperObject ho = he.getHyperObject(iname, em.getShop(player));
			            if (ho instanceof HyperItem) {
			            	HyperItem hi = (HyperItem)ho;
							cost = hi.getCost(1);
							double taxpaid = ho.getPurchaseTax(cost);
							cost = cf.twoDecimals(cost + taxpaid);
							stock = cf.twoDecimals(he.getHyperObject(iname, em.getShop(player)).getStock());
						} else if (ho instanceof HyperEnchant) {
							HyperEnchant hye = (HyperEnchant)ho;
							cost = hye.getCost(EnchantmentClass.DIAMOND);
							cost = cost + ho.getPurchaseTax(cost);
							stock = cf.twoDecimals(he.getHyperObject(iname, em.getShop(player)).getStock());
						} else if (ho instanceof BasicObject) {
							BasicObject hi = (BasicObject)ho;
							cost = hi.getCost(1);
							double taxpaid = ho.getPurchaseTax(cost);
							cost = cf.twoDecimals(cost + taxpaid);
							stock = cf.twoDecimals(he.getHyperObject(iname, em.getShop(player)).getStock());
						}
			            if (ho instanceof PlayerShopObject) {
			            	PlayerShopObject pso = (PlayerShopObject)ho;
			            	sender.sendMessage(L.applyColor("&b" + iname + " &9[&a" + stock + " &9" + L.get("AVAILABLE") + "; &a" + L.fC(cost) + " &9" + L.get("EACH") + "; (&e" + pso.getStatus().toString()+ "&9)]"));
			            } else {
			            	sender.sendMessage(L.applyColor("&b" + iname + " &9[&a" + stock + " &9" + L.get("AVAILABLE") + "; &a" + L.fC(cost) + " &9" + L.get("EACH") + "]"));
			            }			
					} else {
						sender.sendMessage(L.get("REACHED_END"));
						break;
					}			
				}
				count++;
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("BROWSE_SHOP_INVALID"));
		}
	}
}
