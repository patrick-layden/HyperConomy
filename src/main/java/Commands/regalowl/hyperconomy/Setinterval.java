package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setinterval {

	Setinterval(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		YamlFile yaml = hc.getYaml();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		try {
    		if (args.length == 2) {
    			if (args[0].equalsIgnoreCase("shop")) {
	    			s.setshopInterval(Long.parseLong(args[1]));
	    			yaml.getConfig().set("config.shopcheckinterval", s.getshopInterval());
	    			s.stopshopCheck();
	    			s.startshopCheck();
	    			sender.sendMessage(L.get("SHOP_INTERVAL_SET"));
    			} else if (args[0].equalsIgnoreCase("save")) {
    				long saveinterval = Long.parseLong(args[1]);
	    			yaml.getConfig().set("config.saveinterval", saveinterval);	
	    			hc.setSaveInterval(saveinterval);
	    			hc.stopSave();
	    			hc.startSave();	
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
