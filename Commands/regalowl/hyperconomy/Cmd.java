package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class Cmd {

	//Command fields
	private Player player;
	private String name;
	private Message m;
	
	
	
	//Reused objects
	private HyperConomy hc;
	private Calculation calc;
	private ETransaction ench;
	private Log l;
	private Shop s;
	private Account acc;
	private InfoSign isign;
	private SQLFunctions sf;
	
	private int renameshopint;
	private String renameshopname;
	private String playerecon;
	
	private String nonPlayerEconomy;
	

	
	//Constructor for server start.
	Cmd() {
		renameshopint = 0;
		renameshopname = "";
		nonPlayerEconomy = "default";
	}


	public boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {
		hc = HyperConomy.hc;
		m = hc.getMessage();
		calc = hc.getCalculation();
		ench = hc.getETransaction();
		l = hc.getLog();
		s = hc.getShop();
		acc = hc.getAccount();
		isign = hc.getInfoSign();
		sf = hc.getSQLFunctions();
		
		player = null;
		if (sender instanceof Player) {
    		player = (Player) sender;
    	} else {
    		playerecon = nonPlayerEconomy;
    	}
		if (player != null) {
			playerecon = sf.getPlayerEconomy(player.getName());
		}
		

    	if (cmd.getName().equalsIgnoreCase("buy") && (player != null)){
    		new Buy(args, player, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("sell") && (player != null)) {
    		new Sell(args, player, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("sellall") && (player != null)) {
    		new Sellall(args, player);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("value")) {
    		new Value(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("hb") && (player != null)) {
    		new Hb(args, player, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("buyxp") && (player != null)) {
    		new Buyxp(args, player);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("sellxp") && (player != null)) {
    		new Sellxp(args, player);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("xpinfo") && (player != null)) {
    		new Xpinfo(args, player);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("buyid") && (player != null)) {
    		new Buyid(args, player);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("hs") && (player != null)) {
    		new Hs(args, player);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("hv") && (player != null)) {
    		new Hv(args, player, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("settax")) {
    		new Settax(args, sender);
    		return true;		
    	} else if (cmd.getName().equalsIgnoreCase("setclassvalue")) {
    		new Setclassvalue(args, sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setinterval")) {
	    	new Setinterval(args, sender);
	    	return true;
    	} else if (cmd.getName().equalsIgnoreCase("classvalues")) {
    		new Classvalues(sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setvalue")) {
    		new Setvalue(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setstock")) {
    		new Setstock(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setstockall")) {
    		new Setstockall(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setmedian")) {
    		new Setmedian(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setstatic")) {
    		new Setstatic(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setinitiation")) {
    		new Setinitiation(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setinitiationall")) {
    		new Setinitiationall(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setstaticprice")) {
    		new Setstaticprice(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("setstartprice")) {
    		new Setstartprice(args, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("writeitems")) {
    		new Writeitems(args, sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("topitems")) {
    		new Topitems(args, player, sender, playerecon);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("topenchants")) {
    		new Topenchants(args, player, sender, playerecon);
    		return true;	
    	} else if (cmd.getName().equalsIgnoreCase("browseshop")) {
    		new Browseshop(args, sender, player, playerecon);
    		return true;	
    	} else if (cmd.getName().equalsIgnoreCase("iteminfo") && (player != null)) {
    		new Iteminfo(args, player);
    		return true;	
    	} else if (cmd.getName().equalsIgnoreCase("itemsettings")) {
    		new Itemsettings(args, sender, player, playerecon);
    		return true;	
    	} else if (cmd.getName().equalsIgnoreCase("enchantsettings")) {
    		new Enchantsettings(args, sender, playerecon);
    		return true;	
    	} else if (cmd.getName().equalsIgnoreCase("taxsettings")) {
    		new Taxsettings(sender);
    		return true;	
    	} else if (cmd.getName().equalsIgnoreCase("createeconomy")) {
    		new Createeconomy(args, sender);
    		return true;	
    	} else if (cmd.getName().equalsIgnoreCase("seteconomy")) {
    		new Seteconomy(this, args, sender, player);
    		return true;	
    	} else if (cmd.getName().equalsIgnoreCase("economyinfo")) {
    		new Economyinfo(this, args, sender, player);
    		return true;	
    	} else if (cmd.getName().equalsIgnoreCase("setshopeconomy")) {
    		new Setshopeconomy(args, sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("deleteeconomy")) {
    		new Deleteeconomy(args, sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("listeconomies")) {
    		new Listeconomies(args, sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("loaditems")) {
    		new Loaditems(args, sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("exporttoyml")) {
    		try {
    			if (hc.useSQL()) {
        			if (args.length == 1 || args.length == 2) {
        				String economy = args[0];
        				if (hc.getSQLFunctions().testEconomy(economy)) {
        					if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
            					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
            						Backup back = new Backup();
            						back.BackupData();
            					}
            					SQLEconomy sqe = hc.getSQLEconomy();
            					sqe.exportToYml(economy);
                				sender.sendMessage(ChatColor.GOLD + "Economy exported to yml!");
        					} else {
        						sender.sendMessage(ChatColor.RED + "The will erase all data in your items.yml and enchants.yml, type /exporttoyml [economy] ['confirm'] to proceed!");
        					}
        				} else {
        					sender.sendMessage(ChatColor.RED + "That economy doesn't exist!");
        				}
        			} else {
        				sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /exporttoyml [economy]");
        			}
    			} else {
    				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /exporttoyml [economy] ['confirm']");
    			return true;
    		}
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("importsql")) {
    		try {
    			if (hc.useSQL()) {
        			if (args.length == 1 || args.length == 0) {
    					if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
        					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
        						Backup back = new Backup();
        						back.BackupData();
        					}
        					RestoreSQL rs = new RestoreSQL();
        					rs.restore(sender);
            				sender.sendMessage(ChatColor.BLUE + "Importing tables...");
    					} else {
    						sender.sendMessage(ChatColor.RED + "The will erase all of your HyperConomy SQL tables, type /importsql ['confirm'] to proceed!");
    					}
        			} else {
        				sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /importsql");
        			}
    			} else {
    				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /importsql");
    			return true;
    		}
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("listcategories")) {
    		try {
    			Iterator<String> it = hc.getYaml().getCategories().getKeys(false).iterator();
    			ArrayList<String> categories = new ArrayList<String>();
        		while (it.hasNext()) {   			
        			categories.add(it.next().toString());		
        		}    
        		sender.sendMessage(ChatColor.AQUA + "" + categories.toString());
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /listcategories");
    			return true;
    		}
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("ymladditem") && (player != null)) {
    		try {
    			String name = args[0];
    			double value = Double.parseDouble(args[1]);
    			int median = Integer.parseInt(args[2]);
    			double startprice = Double.parseDouble(args[3]);
				int itd = player.getItemInHand().getTypeId();
				int da = calc.getpotionDV(player.getItemInHand());
				int newdat = calc.newData(itd, da);
				String ke = itd + ":" + newdat;
				String nam = hc.getnameData(ke);
				if (nam != null) {
					player.sendMessage(ChatColor.DARK_RED + "That item is already in the item database.");
					return true;
				}		
    			FileConfiguration items = hc.getYaml().getItems();
				items.set(name + ".information.type", "item");
				items.set(name + ".information.category", "unknown");
				items.set(name + ".information.material", player.getItemInHand().getType().toString());
				items.set(name + ".information.id", itd);
				items.set(name + ".information.data", da);
				items.set(name + ".value", value);
				items.set(name + ".price.static", false);
				items.set(name + ".price.staticprice", startprice);
				items.set(name + ".stock.stock", 0);
				items.set(name + ".stock.median", median);
				items.set(name + ".initiation.initiation", true);
				items.set(name + ".initiation.startprice", startprice);
				player.sendMessage(ChatColor.GOLD + "Item added to items.yml.  Restart your server for it to become active.");
    			return true;
    		} catch (Exception e) {
    			player.sendMessage(ChatColor.RED + "Hold the item you'd like to add and type /ymladditem [name] [value] [median] [start price]");
    			return true;
    		}
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("hcbackup")) {
    		try {
    			Backup back = new Backup();
    			back.BackupData();
    			sender.sendMessage(ChatColor.GOLD + "All files have been backed up!");
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /hcbackup");
    			return true;
    		}
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("hc")) {
    		try {
    			if (args.length == 0) {
    				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    				m.send(sender, 53);
    				m.send(sender, 54);
    				m.send(sender, 55);
    				m.send(sender, 56);
					sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    			} else if (args.length == 1) {
    				
    				String type = args[0];
    				
    				if (type.equalsIgnoreCase("sell")) {
    					sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					m.send(sender, 57);
    					m.send(sender, 58);
    					m.send(sender, 59);
    					m.send(sender, 60);
    					m.send(sender, 100);
    					m.send(sender, 61);
    					sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    				} else if (type.equalsIgnoreCase("buy")) {
    					sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					m.send(sender, 62);
    					m.send(sender, 63);
    					m.send(sender, 64);
    					m.send(sender, 65);
    					m.send(sender, 99);
    					m.send(sender, 66);
    					sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					
    				} else if (type.equalsIgnoreCase("info")) {
    					
    					m.send(sender, 67);
    					m.send(sender, 68);
    					m.send(sender, 69);
    					m.send(sender, 70);
    					m.send(sender, 71);
    					m.send(sender, 96);
    					m.send(sender, 72);
    					m.send(sender, 73);
    					m.send(sender, 98);
    					m.send(sender, 74);
    					
    					
    				} else if (type.equalsIgnoreCase("params")) {
    					sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					m.send(sender, 75);
    					m.send(sender, 76);
    					m.send(sender, 77);
    					m.send(sender, 78);
    					m.send(sender, 79);
    					sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    				}
    				
    			} else if (args.length == 2) {
    				String type = args[0];
    				String subtype = args[1];

    				
    				if (type.equalsIgnoreCase("sell")) {
    					
    					if (subtype.equalsIgnoreCase("sell")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 57);
    						m.send(sender, 80);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("hs")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 58);
    						m.send(sender, 81);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("esell")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 59);
    						m.send(sender, 82);				
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("sellall")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 60);
    						m.send(sender, 83);	
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("sellxp")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 100);
    						m.send(sender, 102);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					}						
    				} else if (type.equalsIgnoreCase("buy")) {					
    					if (subtype.equalsIgnoreCase("buy")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 62);
    						m.send(sender, 84);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("hb")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 63);
    						m.send(sender, 85);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("buyid")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 64);
    						m.send(sender, 86);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("ebuy")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 65);
    						m.send(sender, 87);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("buyxp")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 99);
    						m.send(sender, 101);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					}			
    				} else if (type.equalsIgnoreCase("info")) {
    					if (subtype.equalsIgnoreCase("value")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 67);
    						m.send(sender, 88);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("hv")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 68);
    						m.send(sender, 89);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("iteminfo")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 69);
    						m.send(sender, 90);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("ii")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 70);
    						m.send(sender, 90);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("topitems")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 71);
    						m.send(sender, 91);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("topenchants")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 72);
    						m.send(sender, 92);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("browseshop")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 96);
    						m.send(sender, 97);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("evalue")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 73);
    						m.send(sender, 93);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					} else if(subtype.equalsIgnoreCase("xpinfo")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    						m.send(sender, 98);
    						m.send(sender, 103);
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    					}
    				}		
    			} else {   
    				//do nothing
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 46);
    		}

    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("ebuy") && player != null) {
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || 
    						player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".buy")) {

    				String name = args[0];

    				String teststring = hc.testeString(name);

    				if (teststring != null) {

        				if (s.has(s.getShop(player), name)) {	
            				ench.buyEnchant(name, player);
    					} else {
    						player.sendMessage(ChatColor.BLUE + "Sorry, that item or enchantment cannot be traded at this shop.");
    					}
    				} else {
    					m.send(player, 43);
    				}

    				
    			} else {
    				player.sendMessage(ChatColor.BLUE + "Sorry, you don't have permission to trade here.");
    			}
    				
    				
    			} else {
    				m.send(player, 49);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(player, 47);
    		}
    		
    		
    		
    			
    			
    			
    			
    			
    	} else if (cmd.getName().equalsIgnoreCase("esell") && player != null) {
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || 
    						player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".sell")) {
    					
    				
    				
    				String name = args[0];
    				

    				
    				if (args[0].equalsIgnoreCase("max")) {
    					
    					//need to add check for items without enchantments
    					if (!ench.hasenchants(player.getItemInHand())) {
    						m.send(player, 48);
    					}
    					
    					Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
        				while (ite.hasNext()) {
        					String rawstring = ite.next().toString();
        					String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
        					Enchantment en = null;
        					en = Enchantment.getByName(enchname);
        					int lvl = player.getItemInHand().getEnchantmentLevel(en);
        					String nam = hc.getenchantData(enchname);
        					String fnam = nam + lvl;
        					
        					
            				if (s.has(s.getShop(player), fnam)) {	
            					ench.sellEnchant(fnam, player);
        					} else {
        						player.sendMessage(ChatColor.BLUE + "Sorry, that item or enchantment cannot be traded at this shop.");
        					}
        					
        				}
    				} else {
        				String teststring = hc.testeString(name);
        				if (teststring != null) {

        					
            				if (s.has(s.getShop(player), name)) {	
            					ench.sellEnchant(name, player);
        					} else {
        						player.sendMessage(ChatColor.BLUE + "Sorry, that item or enchantment cannot be traded at this shop.");
        					}
        					
        					
        				} else {
        					m.send(player, 43);
        				}
    				}

    			} else {
    				player.sendMessage(ChatColor.BLUE + "Sorry, you don't have permission to trade here.");
    			}
    				
    			} else {
    				m.send(player, 49);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(player, 50);;
    		}
    		
    		
    			
    			
    			
    			
    	} else if (cmd.getName().equalsIgnoreCase("evalue")) {
    		try {
    		
    			if (args.length == 2) {

    				String nam = args[0];
    				String teststring = hc.testeString(nam);
    				if (teststring != null) {
    				

    					
    					String type = args[1];
    					
    					
    					
    					
    					if (type.equalsIgnoreCase("s")) {
    						
    					
    					
	    		        String[] classtype = new String[8];
	    		        classtype[0] = "leather";
	    		        classtype[1] = "wood";
	    		        classtype[2] = "iron";
	    		        classtype[3] = "chainmail";
	    		        classtype[4] = "stone";
	    		        classtype[5] = "gold";
	    		        classtype[6] = "diamond";
	    		        classtype[7] = "bow";
	    				int n = 0;
	    				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");		
	    				while (n < 8) {
	    				double value = calc.getEnchantValue(nam, classtype[n], playerecon);    	
	    				
	    				
						double salestax = 0;
						if (hc.getYaml().getConfig().getBoolean("config.dynamic-tax.use-dynamic-tax")) {
							double moneycap = hc.getYaml().getConfig().getDouble("config.dynamic-tax.money-cap");
							double cbal = acc.getBalance(player.getName());
							if (cbal >= moneycap) {
								salestax = value * (hc.getYaml().getConfig().getDouble("config.dynamic-tax.max-tax-percent")/100);
							} else {
								salestax = value * (cbal/moneycap);
							}
						} else {
							double salestaxpercent = hc.getYaml().getConfig().getDouble("config.sales-tax-percent");
							salestax = (salestaxpercent/100) * value;
						}
	    				
						value = calc.twoDecimals(value - salestax);
	    				
	    				
	    				sender.sendMessage(ChatColor.AQUA + "" + nam + ChatColor.BLUE + " on a " + ChatColor.AQUA + "" + classtype[n] + ChatColor.BLUE + " item can be sold for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + value);
	    				n++;
	    				}
	    				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	
    					} else if (type.equalsIgnoreCase("b")) {
    						
    	    		        String[] classtype = new String[8];
    	    		        classtype[0] = "leather";
    	    		        classtype[1] = "wood";
    	    		        classtype[2] = "iron";
    	    		        classtype[3] = "chainmail";
    	    		        classtype[4] = "stone";
    	    		        classtype[5] = "gold";
    	    		        classtype[6] = "diamond";
    	    		        classtype[7] = "bow";
    	    				int n = 0;
    	    				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    	    				while (n < 8) {
    	    				double cost = calc.getEnchantCost(nam, classtype[n], playerecon);    
    	    				cost = cost + calc.getEnchantTax(nam, playerecon, cost);
    	    				sender.sendMessage(ChatColor.AQUA + "" + nam + ChatColor.BLUE + " on a " + ChatColor.AQUA + "" + classtype[n] + ChatColor.BLUE + " item can be bought for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + cost);
    	    				n++;
    	    				}
    	    				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	
    	    				
    					} else if (type.equalsIgnoreCase("a")) {
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	
    						sender.sendMessage(ChatColor.BLUE + "The global shop has " + ChatColor.GREEN + "" + sf.getStock(name, playerecon) + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " available.");
    						sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	
    					} else {
    						m.send(sender, 51);
    					}
    				

        			} else {
        				sender.sendMessage(ChatColor.BLUE + "Sorry, that enchantment is not in the enchantment database.");
        			}

    				
    			} else if (args.length == 0 && player != null){
    				if (ench.hasenchants(player.getItemInHand())) {
    				player.getItemInHand().getEnchantments().keySet().toArray();
    				
    				Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
    				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	
    				double duramult = ench.getDuramult(player);
    				while (ite.hasNext()) {
    					String rawstring = ite.next().toString();
    					String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
    					Enchantment en = null;
    					en = Enchantment.getByName(enchname);
    					int lvl = player.getItemInHand().getEnchantmentLevel(en);
    					String nam = hc.getenchantData(enchname);
    					String fnam = nam + lvl;
    					String mater = player.getItemInHand().getType().name();
    					double value = calc.getEnchantValue(fnam, mater, playerecon) * duramult;
    					double cost = calc.getEnchantCost(fnam, mater, playerecon);
    					cost = cost + calc.getEnchantTax(fnam, playerecon, cost);
						value = calc.twoDecimals(value);
						cost = calc.twoDecimals(cost);
						
						
						double salestax = 0;
						if (hc.getYaml().getConfig().getBoolean("config.dynamic-tax.use-dynamic-tax")) {
							double moneycap = hc.getYaml().getConfig().getDouble("config.dynamic-tax.money-cap");
							double cbal = acc.getBalance(player.getName());
							if (cbal >= moneycap) {
								salestax = value * (hc.getYaml().getConfig().getDouble("config.dynamic-tax.max-tax-percent")/100);
							} else {
								salestax = value * (cbal/moneycap);
							}
						} else {
							double salestaxpercent = hc.getYaml().getConfig().getDouble("config.sales-tax-percent");
							salestax = (salestaxpercent/100) * value;
						}
	    				
						value = calc.twoDecimals(value - salestax);
						
    					player.sendMessage(ChatColor.AQUA + "" + fnam + ChatColor.BLUE + " can be sold for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + value);
    					player.sendMessage(ChatColor.AQUA + "" + fnam + ChatColor.BLUE + " can be purchased for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + cost);
    					player.sendMessage(ChatColor.BLUE + "The global shop currently has" + ChatColor.GREEN + " " + sf.getStock(fnam, playerecon) + ChatColor.AQUA + " " + fnam + ChatColor.BLUE + " available.");
    				}
    				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	
    			} else {
    				m.send(player, 48);
    			}
    			} else {
    				m.send(sender, 51);
    			}

    			return true;
    		} catch (Exception e) {
    			m.send(sender, 51);
    		}	
    			
    			
    			
    			
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("removeshop")) {
    		try {

    			if (args.length > 0) {
    				
    				int counter = 0;
    				String name = "";
    				while (counter < args.length) {
    					if (counter == 0) {
    						name = args[0];
    					} else {
    						name = name + "_" + args[counter];
    					}
    					counter++;
    				}
    				String teststring = hc.getYaml().getShops().getString(name);
    				if (teststring == null) {
    					name = hc.fixsName(name);
    				}
    				//sender.sendMessage("name: " + name);
        			s.setrShop(name);
        			s.removeShop();
        			sender.sendMessage(ChatColor.GOLD + name.replace("_", " ") + " has been removed!");	
    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /removeshop [name]");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist.");
    		}
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("listshops")) {
    		try {

    			if (args.length == 0) {
    				
    				String shoplist = s.listShops().toString().replace("_", " ").replace("[", "").replace("]", "");
    				sender.sendMessage(ChatColor.AQUA + shoplist);
    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /listshops");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /listshops");
    		}
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("intervals")) {
    		try {

    			if (args.length == 0) {
    				SQLWrite sw = hc.getSQLWrite();
    				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    				sender.sendMessage(ChatColor.BLUE + "Shop Check Interval: " + ChatColor.GREEN + "" + s.getshopInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + s.getshopInterval()/20 + ChatColor.BLUE + " Seconds");
    				sender.sendMessage(ChatColor.BLUE + "Save Interval: " + ChatColor.GREEN + "" + hc.getsaveInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + hc.getsaveInterval()/20 + ChatColor.BLUE + " Seconds");
    				if (!hc.useSQL()) {
    					sender.sendMessage(ChatColor.BLUE + "Log Write Interval: " + ChatColor.GREEN + "" + l.getlogInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + l.getlogInterval()/20 + ChatColor.BLUE + " Seconds");
        				sender.sendMessage(ChatColor.BLUE + "The log buffer currently holds " + ChatColor.GREEN + "" + l.getbufferSize() + ChatColor.BLUE + " entries.");
        				sender.sendMessage(ChatColor.BLUE + "The log has " + ChatColor.GREEN + "" + l.getlogSize() + ChatColor.BLUE + " entries.");	
    				}

    				sender.sendMessage(ChatColor.BLUE + "Sign Update Interval: " + ChatColor.GREEN + "" + isign.getsignupdateInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + isign.getsignupdateInterval()/20 + ChatColor.BLUE + " Seconds");
    				sender.sendMessage(ChatColor.BLUE + "There are " + ChatColor.GREEN + "" + isign.getremainingSigns() + ChatColor.BLUE + " signs waiting to update.");
    				if (hc.useSQL()) {
    					sender.sendMessage(ChatColor.BLUE + "The log has " + ChatColor.GREEN + "" + sf.countTableEntries("hyperlog") + ChatColor.BLUE + " entries.");	
        				sender.sendMessage(ChatColor.BLUE + "The SQL buffer contains " + ChatColor.GREEN + "" + sw.getBufferSize() + ChatColor.BLUE + " statements.");
        				sender.sendMessage(ChatColor.BLUE + "There are currently " + ChatColor.GREEN + "" + sw.getActiveThreads() + ChatColor.BLUE + " active SQL threads.");
    				}

    				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
    		}
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setbalance")) {
    		try {

    			if (args.length == 2) {
    				String accountname = args[0];
    				if (acc.checkAccount(accountname)) {
    					Double balance = Double.parseDouble(args[1]);
    					acc.setBalance(accountname, balance);
    					sender.sendMessage(ChatColor.GOLD + "Balance set!");
    				} else {
    					sender.sendMessage(ChatColor.DARK_RED + "That account doesn't exist!");
    				}
    				
    				
    				
    				

    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setbalance [account] [balance]");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setbalance [account] [balance]");
    		}
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("additem")) {
    		new Additem(args, sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("addcategory")) {
    		new Addcategory(args, sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("removeitem")) {
    		new Removeitem(args, sender);
    		return true;
    	} else if (cmd.getName().equalsIgnoreCase("removecategory")) {
    		new Removecategory(args, sender);
    		return true;
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("notify")) {
    		try {

    			String itemname = hc.fixName(args[0]);

    			if (args.length == 1) {
    				
    				if (hc.getYaml().getConfig().getBoolean("config.use-notifications")) {
    					
    				
    				
	    				if (hc.itemTest(itemname) || hc.enchantTest(itemname) || itemname.equalsIgnoreCase("all")) {
		    		
		    				
	    					if (!itemname.equalsIgnoreCase("all")) {
			    				boolean note = false;
			    				String notify = hc.getYaml().getConfig().getString("config.notify-for");
			    				if (notify != null) {		
			    					if (notify.contains("," + itemname + ",")) {
			    						note = true;
			    					}
			    					if (notify.length() >= itemname.length() && itemname.equalsIgnoreCase(notify.substring(0, itemname.length()))) {
			    						note = true;
			    					}
			    				}
			    				
			    				//Toggles the notification.
			    				if (note) {
			    					notify = notify.replace("," + itemname + ",", ",");
			    					if (itemname.equalsIgnoreCase(notify.substring(0, itemname.length()))) {
			    						notify = notify.substring(itemname.length() + 1, notify.length());
			    					}
			    					hc.getYaml().getConfig().set("config.notify-for", notify);
			    					sender.sendMessage(ChatColor.GOLD + "You will no longer receive notifications for " + itemname);
			    				} else {
			    					notify = notify + itemname + ",";
			    					hc.getYaml().getConfig().set("config.notify-for", notify);
			    					sender.sendMessage(ChatColor.GOLD + "You will now receive notifications for " + itemname);
			    				}
	    					} else {
	    						
	    						ArrayList<String> items = hc.getNames();
	    						String namelist = "";
	    						int i = 0;
	    						while (i < items.size()) {
	    							namelist = namelist + items.get(i) + ",";
	    							i++;
	    						}
	    						
	    						String notify = hc.getYaml().getConfig().getString("config.notify-for");
	    						if (notify.equalsIgnoreCase(namelist)) {
			    					hc.getYaml().getConfig().set("config.notify-for", "");
			    					sender.sendMessage(ChatColor.GOLD + "You will no longer receive notifications for any item or enchantment.");
	    						} else {
			    					hc.getYaml().getConfig().set("config.notify-for", namelist);
			    					sender.sendMessage(ChatColor.GOLD + "You will now receive notifications for all items and enchantments.");
	    						}
	    					}
	
			  
	
		    				
		    			} else {
		    				sender.sendMessage(ChatColor.DARK_RED + "That item or enchantment is not in the database!");
		    			}
    				
	    			} else {
	    				sender.sendMessage(ChatColor.DARK_RED + "Notifications are currently disabled!");
	    			}
    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /notify [name/'all']");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /notify [name/'all']");
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
     	} else if (cmd.getName().equalsIgnoreCase("setstockmedianall")) {
    		try {
    			
    			if (args.length == 0){
    				sender.sendMessage(ChatColor.RED + "Are you sure you wish to do this?");
    				sender.sendMessage(ChatColor.RED + "All item and enchantment stocks will be set to their median.");
    				sender.sendMessage(ChatColor.RED + "All item and enchantments will have initial pricing disabled.");
    				sender.sendMessage(ChatColor.RED + "Type /setstockmedianall confirm to proceed.");

    			} else if (args[0].equalsIgnoreCase("confirm")){
    					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
    						Backup back = new Backup();
    						back.BackupData();
    					}
    					ArrayList<String> names = hc.getNames();
    					for (int c = 0; c < names.size(); c++) {
    						sf.setStock(names.get(c), playerecon, sf.getMedian(names.get(c), playerecon));
    						sf.setInitiation(names.get(c), playerecon, "false");
    					}

	    			sender.sendMessage(ChatColor.GOLD + "Shop stocks of all items/enchantments have been set to their medians and initial pricing has been disabled.");
	    			
					//Updates all information signs.
	    			isign.setrequestsignUpdate(true);
	    			isign.checksignUpdate();
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setstockmedianall");
    			}
    			
    			
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setstockmedianall");
    		}	

    		
    		return true;
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("scalebypercent")) {
    		try {
    			
    			if (args.length == 2){
    				
    				String type = args[0];
    				Double percent = Double.parseDouble(args[1]);
    				percent = percent/100;
    				
    				if (percent >= 0) {
    					if (type.equalsIgnoreCase("value") || type.equalsIgnoreCase("staticprice") || type.equalsIgnoreCase("stock") || type.equalsIgnoreCase("median") || type.equalsIgnoreCase("startprice")) {
        					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
        						Backup back = new Backup();
        						back.BackupData();
        					}
    		   					ArrayList<String> names = hc.getNames();
    	    					for (int c = 0; c < names.size(); c++) {
    	    						String cname = names.get(c);
    	    						if (type.equalsIgnoreCase("value")) {
    	    							sf.setValue(cname, playerecon, calc.twoDecimals(sf.getValue(cname, playerecon) * percent));
    	    						} else if (type.equalsIgnoreCase("staticprice")) {
    	    							sf.setStaticPrice(cname, playerecon, calc.twoDecimals(sf.getStaticPrice(cname, playerecon) * percent));
    	    						} else if (type.equalsIgnoreCase("stock")) {
    	    							sf.setStock(cname, playerecon, Math.floor(sf.getStock(cname, playerecon) * percent + .5));
    	    						} else if (type.equalsIgnoreCase("median")) {
    	    							sf.setMedian(cname, playerecon, calc.twoDecimals(sf.getMedian(cname, playerecon) * percent));
    	    						} else if (type.equalsIgnoreCase("startprice")) {
    	    							sf.setStartPrice(cname, playerecon, calc.twoDecimals(sf.getStartPrice(cname, playerecon) * percent));
    	    						}
    	    					}
    						
        	    			sender.sendMessage(ChatColor.GOLD + "Adjustment successful!");
        	    			
        					//Updates all information signs.
        	    			isign.setrequestsignUpdate(true);
        	    			isign.checksignUpdate();
        				} else {
            				sender.sendMessage(ChatColor.DARK_RED + "The setting must be either value, staticprice, stock, median, or startprice!");
            			}
    						
    					} else {
    						sender.sendMessage(ChatColor.DARK_RED + "Percent must be greater than 0!");
    					}
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /scalebypercent [setting] [percent]");
    			}  			  			
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /scalebypercent [setting] [percent]");
    		}	

    		
    		return true;
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("resetshop")) {
    		try {
    			
    			if (args.length == 0){
    				sender.sendMessage(ChatColor.RED + "Are you sure you wish to do this?");
    				sender.sendMessage(ChatColor.RED + "All item and enchantment stocks will be set to 0.");
    				sender.sendMessage(ChatColor.RED + "All items and enchantments will return to initial pricing.");
    				sender.sendMessage(ChatColor.RED + "Static pricing will be disabled for all items and enchantments.");
    				sender.sendMessage(ChatColor.RED + "Type /resetshop confirm to proceed.");

    			} else if (args[0].equalsIgnoreCase("confirm")){
					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
						Backup back = new Backup();
						back.BackupData();
					}
	   					ArrayList<String> names = hc.getNames();
    					for (int c = 0; c < names.size(); c++) {
    						String cname = names.get(c);
    						sf.setStock(cname, playerecon, 0);
    						sf.setStatic(cname, playerecon, "false");
    						sf.setInitiation(cname, playerecon, "true");
    					}
	    			sender.sendMessage(ChatColor.GOLD + "Shop stock, initiation, and static pricing have been reset!");
	    			
					//Updates all information signs.
	    			isign.setrequestsignUpdate(true);
	    			isign.checksignUpdate();
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /resetshop");
    			}
    			
    			
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /resetshop");
    		}	

    		
    		return true;
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("renameeconomyaccount")) {
    		try {
    			
    			if (args.length == 1) {
    				
    				//Gets the account names.
    				String newaccount = args[0];
    				String oldaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
    				
    				//Creates the new account.
    				acc.createAccount(newaccount);
    				acc.setBalance(newaccount, acc.getBalance(oldaccount));
    				
    				//Deletes the old account.
    				acc.setBalance(oldaccount, 0);
    				
    				hc.getYaml().getConfig().set("config.global-shop-account", newaccount);
    				
    				sender.sendMessage(ChatColor.GOLD + "The global shop account has been successfully renamed!");
    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameeconomyaccount [new name]");
    			}
    			
    			
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameeconomyaccount [new name]");
    		}	

    		
    		return true;
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("renameshop")) {
    		try {
    			
    			if (args.length >= 1){
    				int counter = 0;
    				String name = "";
    				while (counter < args.length) {
    					if (counter == 0) {
    						name = args[0];
    					} else {
    						name = name + "_" + args[counter];
    					}
    					counter++;
    				}
				    name = hc.fixsName(name);
				    
				    if (name.equalsIgnoreCase("reset")) {
    					renameshopname = "";
    					renameshopint = 0;
				    	sender.sendMessage(ChatColor.GOLD + "Command has been reset!");
				    	return true;
				    }
				    
				    
					String teststring = hc.getYaml().getShops().getString(name);
    				
    				
    				
    				if (renameshopint == 0 && teststring != null) {
    					renameshopname = name;
    					renameshopint = 1;
    					sender.sendMessage(ChatColor.GOLD + "Shop to be renamed selected!");
    					sender.sendMessage(ChatColor.GOLD + "Now type /renameshop [new name]");
    					sender.sendMessage(ChatColor.GOLD + "To reset the command and start over type /renameshop reset");
    				} else if (renameshopint == 1) {
    					
    					if (name.equalsIgnoreCase(renameshopname)) {
    						sender.sendMessage(ChatColor.DARK_RED + "You can't give the shop its original name!");
    						return true;
    					}
    					s.setrenShop(renameshopname, name);
    					s.renameShop();
    					renameshopname = "";
    					renameshopint = 0;
    					sender.sendMessage(ChatColor.GOLD + "Shop renamed successfully!");
    				} else {
    					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
    				}
    				

    			
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameshop [name]");
    			}
    			
    			
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameshop [name]");
    		}	

    		
    		return true;
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setmessage")) {
    		try {
	    		
	    		if (args.length >= 3) {
	    			
	    			if (args[0].equalsIgnoreCase("1")) {

	    				String message = args[1];

	    				message = message.replace("%s", " ");
	    				
	    				int counter = 2;
	    				String name = "";
	    				while (counter < args.length) {
	    					if (counter == 2) {
	    						name = args[2];
	    					} else {
	    						name = name + "_" + args[counter];
	    					}
	    					counter++;
	    				}
	    				String teststring = hc.getYaml().getShops().getString(name);
	    				if (teststring == null) {
	    					name = hc.fixsName(name);
	    				}
	    				
	    				
	    				int i = 0;
	    				
	    				while (i < s.getshopdataSize()) {
	    					//sender.sendMessage("False: " + Shop.shopdata.get(i));
	    				if (name.equalsIgnoreCase(s.getshopData(i))) {
	    					s.setMessage1(i, message);
	    					hc.getYaml().getShops().set(s.getshopData(i) + ".shopmessage1", message);
	    					sender.sendMessage(ChatColor.GOLD + "Message 1 set!");
	    					//sender.sendMessage("True: " + Shop.shopdata.get(i) + "," + Shop.shopmessage1.get(i));
	    					return true;
	    				}
	    					i++;
	    				}
	    				sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");

		    			
	    			} else if (args[0].equalsIgnoreCase("2")) {
	    				
	    				String message = args[1];

	    				message = message.replace("%s", " ");
	    				
	    				int counter = 2;
	    				String name = "";
	    				while (counter < args.length) {
	    					if (counter == 2) {
	    						name = args[2];
	    					} else {
	    						name = name + "_" + args[counter];
	    					}
	    					counter++;
	    				}
	    				String teststring = hc.getYaml().getShops().getString(name);
	    				if (teststring == null) {
	    					name = hc.fixsName(name);
	    				}
	    				
	    				int i = 0;
	    				while (i < s.getshopdataSize()) {
	    				if (name.equalsIgnoreCase(s.getshopData(i))) {
	    					s.setMessage2(i, message);
	    					hc.getYaml().getShops().set(s.getshopData(i) + ".shopmessage2", message);
	    					sender.sendMessage(ChatColor.GOLD + "Message 2 set!");
	    					return true;
	    				}
	    				i++;
	    				}
	    				
	    				
	    				sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
		    			
	    			} 
	    			
	    		} else {
	    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setmessage ['1'/'2'] [message] [shop]");
	    		}
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setmessage ['1'/'2'] [message] [shop]");
    		}

    		
    		return true;
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setshop") && player != null) {
    		if (args.length >= 2) {
    			
    			if (args[0].equalsIgnoreCase("p1")) {
    				int counter = 1;
    				String name = "";
    				while (counter < args.length) {
    					if (counter == 1) {
    						name = args[1];
    					} else {
    						name = name + "_" + args[counter];
    					}
    					counter++;
    				}
    				String teststring = hc.getYaml().getShops().getString(name);
    				if (teststring == null) {
    					name = hc.fixsName(name);
    				}
    				name = name.replace(".", "").replace(":", "");
        			s.setsShop(name, player);
        			s.setShop1();
        			player.sendMessage(ChatColor.GOLD + "Shop location p1 has been set!");	
    			} else if (args[0].equalsIgnoreCase("p2")) {
    				int counter = 1;
    				String name = "";
    				while (counter < args.length) {
    					if (counter == 1) {
    						name = args[1];
    					} else {
    						name = name + "_" + args[counter];
    					}
    					counter++;
    				}
    				
    				name = name.replace(".", "").replace(":", "");
    				String teststring = hc.getYaml().getShops().getString(name);
    				if (teststring == null) {
    					name = hc.fixsName(name);
    				}
    				s.setsShop(name, player);
        			s.setShop2();
        			player.sendMessage(ChatColor.GOLD + "Shop location p2 has been set!");	
    			}
    		} else {
    			m.send(player, 52);
    		}
    		return true;	
    	}
    	
    	
    	
    	
    	
    	
    	

    	
    	
    	
    	return false;
	}
	
	
	public void setNonPlayerEconomy(String economy) {
		nonPlayerEconomy = economy;
	}
	public String getNonPlayerEconomy() {
		return nonPlayerEconomy;
	}
	

	}
	
	
	
	
	
	
	

