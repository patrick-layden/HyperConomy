package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setinterval {

	Setinterval(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		YamlFile yaml = hc.getYaml();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		try {
    		if (args.length == 2) {
    			if (args[0].equalsIgnoreCase("shop")) {
    				for (HyperEconomy he:em.getEconomies()) {
    	    			he.setShopCheckInterval(Long.parseLong(args[1]));
    	    			yaml.getConfig().set("config.shopcheckinterval", he.getShopCheckInterval());
    	    			he.stopShopCheck();
    	    			he.startShopCheck();
    				}
	    			sender.sendMessage(L.get("SHOP_INTERVAL_SET"));
    			} else if (args[0].equalsIgnoreCase("save")) {
    				long saveinterval = Long.parseLong(args[1]);
	    			yaml.getConfig().set("config.saveinterval", saveinterval);	
	    			hc.s().setSaveInterval(saveinterval);
	    			hc.s().stopSave();
	    			hc.s().startSave();	
	    			sender.sendMessage(L.get("SAVE_INTERVAL_SET"));
    			} else if (args[0].equalsIgnoreCase("sign")) {
    				
    				isign.setInterval(Long.parseLong(args[1]));
	    			yaml.getConfig().set("config.signupdateinterval", isign.getUpdateInterval());		    		
	    			sender.sendMessage(L.get("SIGN_INTERVAL_SET"));
    			} else {
    				sender.sendMessage(L.get("SETINTERVAL_INVALID"));
    			}
    		} else {
    			sender.sendMessage(L.get("SETINTERVAL_INVALID"));
    		}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETINTERVAL_INVALID"));
		}
	}
}
