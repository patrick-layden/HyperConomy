package regalowl.hyperconomy;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Renameshop {
	Renameshop(CommandSender sender, String[] args, _Command cmd) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Shop s = hc.getShop();
		try {
			boolean rename = cmd.getRename();
			String renameshopname = cmd.getRenameShopName();
			if (args.length >= 1) {
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
					cmd.setRenameShopName("");
					cmd.setRename(false);
					sender.sendMessage(ChatColor.GOLD + "Command has been reset!");
					return;
				}
				String teststring = hc.getYaml().getShops().getString(name);
				if (!rename && teststring != null) {
					cmd.setRenameShopName(name);
					cmd.setRename(true);
					sender.sendMessage(ChatColor.GOLD + "Shop to be renamed selected!");
					sender.sendMessage(ChatColor.GOLD + "Now type /renameshop [new name]");
					sender.sendMessage(ChatColor.GOLD + "To reset the command and start over type /renameshop reset");
				} else if (rename) {
					if (name.equalsIgnoreCase(renameshopname)) {
						sender.sendMessage(ChatColor.DARK_RED + "You can't give the shop its original name!");
						return;
					}
					s.setrenShop(renameshopname, name);
					s.renameShop();
					cmd.setRenameShopName("");
					cmd.setRename(false);
					sender.sendMessage(ChatColor.GOLD + "Shop renamed successfully!");
				} else {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameshop [name]");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameshop [name]");
		}
	}
}
