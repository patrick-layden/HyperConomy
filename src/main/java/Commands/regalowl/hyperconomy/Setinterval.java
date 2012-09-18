package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setinterval {

	Setinterval(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		YamlFile yaml = hc.getYaml();
		Log l = hc.getLog();
		InfoSign isign = hc.getInfoSign();
		try {
    		if (args.length == 2) {
    			if (args[0].equalsIgnoreCase("shop")) {
	    			s.setshopInterval(Long.parseLong(args[1]));
	    			yaml.getConfig().set("config.shopcheckinterval", s.getshopInterval());
	    			s.stopshopCheck();
	    			s.startshopCheck();
	    			sender.sendMessage(ChatColor.GOLD + "Shop check interval set!");
    			} else if (args[0].equalsIgnoreCase("log")) {
    				l.setlogInterval(Long.parseLong(args[1]));
	    			yaml.getConfig().set("config.logwriteinterval", l.getlogInterval());		    		
	    			l.stopBuffer();
	    			l.checkBuffer();	
	    			sender.sendMessage(ChatColor.GOLD + "Log write interval set!");
    			} else if (args[0].equalsIgnoreCase("save")) {
    				long saveinterval = Long.parseLong(args[1]);
	    			yaml.getConfig().set("config.saveinterval", saveinterval);	
	    			hc.setSaveInterval(saveinterval);
	    			hc.stopSave();
	    			hc.startSave();	
	    			sender.sendMessage(ChatColor.GOLD + "Save interval set!");
    			} else if (args[0].equalsIgnoreCase("sign")) {
    				
    				isign.setsignupdateInterval(Long.parseLong(args[1]));
	    			yaml.getConfig().set("config.signupdateinterval", isign.getsignupdateInterval());		    		
	    			isign.stopsignUpdate();
	    			isign.checksignUpdate();	
	    			sender.sendMessage(ChatColor.GOLD + "Sign update interval set!");
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setinterval ['shop'/'log'/'save'/'sign'] [interval]");
    			}
    		} else {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setinterval ['shop'/'log'/'save'/'sign'] [interval]");
    		}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Usage.  Use /setinterval ['shop'/'log'/'save'/'sign'] [interval]");
		}
	}
}
