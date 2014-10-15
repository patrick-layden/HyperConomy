package regalowl.hyperconomy.command;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;

import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.HyperShopManager;
import regalowl.hyperconomy.hyperobject.BasicObject;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectStatus;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;

public class Browseshop extends BaseCommand implements HyperCommand {
	

	public Browseshop() {
		super(false);
	}
	
	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = getEconomy();
		HyperShopManager hsm = hc.getHyperShopManager();
		ArrayList<String> aargs = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			aargs.add(args[i]);
		}
		try {
			boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
    		if (hp != null) {
    			if ((requireShop && !hsm.inAnyShop(hp)) && !hp.hasPermission("hyperconomy.admin")) {
    				data.addResponse(L.get("REQUIRE_SHOP_FOR_INFO"));
    				return data;
    			}			
    		}
			if (aargs.size() > 3) {
				data.addResponse(L.get("BROWSE_SHOP_INVALID"));
				return data;
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
				data.addResponse(L.get("BROWSE_SHOP_INVALID"));
				return data;
			}
			String input = "";
			if (aargs.size() == 1) {
				input = aargs.get(0);
			} else {
				data.addResponse(L.get("BROWSE_SHOP_INVALID"));
				return data;
			}
    		Shop shop = null;
    		if (hp != null) {
    			if (!hsm.inAnyShop(hp)) {
    				shop = null;
    			} else {
    				shop = hsm.getShop(hp);
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
								HyperObject pso = ps.getPlayerShopObject(ho);
								if (pso != null) {
									if (pso.getStatus() == HyperObjectStatus.NONE) {
										if (ps.isAllowed(hp)) {
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
								HyperObject pso = ps.getPlayerShopObject(ho);
								if (pso != null) {
									if (pso.getStatus() == HyperObjectStatus.NONE) {
										if (ps.isAllowed(hp)) {
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
			data.addResponse(ChatColor.RED + L.get("PAGE") + " " + ChatColor.WHITE + "(" + ChatColor.RED + "" + page + ChatColor.WHITE + "/" + ChatColor.RED + "" + maxpi + ChatColor.WHITE + ")");
			while (count < numberpage) {
				if (count > ((page * 10) - 11)) {
					if (count < rsize) {
						String iname = rnames.get(count);
			            Double cost = 0.0;
			            double stock = 0;
			            HyperObject ho = he.getHyperObject(iname, hsm.getShop(hp));
			            if (ho.getType() == HyperObjectType.ITEM) {
							cost = ho.getBuyPrice(1);
							double taxpaid = ho.getPurchaseTax(cost);
							cost = cf.twoDecimals(cost + taxpaid);
							stock = cf.twoDecimals(he.getHyperObject(iname, hsm.getShop(hp)).getStock());
						} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
							cost = ho.getBuyPrice(EnchantmentClass.DIAMOND);
							cost = cost + ho.getPurchaseTax(cost);
							stock = cf.twoDecimals(he.getHyperObject(iname, hsm.getShop(hp)).getStock());
						} else {
							BasicObject hi = (BasicObject)ho;
							cost = hi.getBuyPrice(1);
							double taxpaid = ho.getPurchaseTax(cost);
							cost = cf.twoDecimals(cost + taxpaid);
							stock = cf.twoDecimals(he.getHyperObject(iname, hsm.getShop(hp)).getStock());
						}
			            if (ho.isShopObject()) {
			            	data.addResponse(L.applyColor("&b" + iname + " &9[&a" + stock + " &9" + L.get("AVAILABLE") + "; &a" + L.fC(cost) + " &9" + L.get("EACH") + "; (&e" + ho.getStatus().toString()+ "&9)]"));
			            } else {
			            	data.addResponse(L.applyColor("&b" + iname + " &9[&a" + stock + " &9" + L.get("AVAILABLE") + "; &a" + L.fC(cost) + " &9" + L.get("EACH") + "]"));
			            }			
					} else {
						data.addResponse(L.get("REACHED_END"));
						return data;
					}			
				}
				count++;
			}
		} catch (Exception e) {
			data.addResponse(L.get("BROWSE_SHOP_INVALID"));
		}
		return data;
	}



}
