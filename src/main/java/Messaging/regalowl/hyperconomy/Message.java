package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


/**
 * 
 * 
 * This class sends basic messages to players.
 * 
 */
public class Message {
	
	//Stores the messages to a String array and sets up values for color codes.
	String message[] = new String[104];
	String darkred = "§4";
	String blue = "§9";
	String gold = "§6";
	String black = "§0";
	String aqua = "§b";
	String green = "§a";
	String red = "§c";
	String yellow = "§e";
	String pink = "§d";
	String perror = "Invalid Parameters. ";

	
	
	/**
	 * 
	 * 
	 * This constructor creates the messages array.  This is intended to be called when the plugin enables.
	 * 
	 */
	Message() {
		//buy
		message[0] = darkred + perror + "Use /buy [name] (amount or 'max')";
		message[1] = darkred + "Invalid item name.";
		message[2] = darkred + "You must be in a shop to buy or sell!";
		//sell
		message[3] = darkred + perror + "Use /sell [name] (amount or 'max')";
		message[4] = blue + "You cannot buy or sell enchanted items!";
		//sellall
		message[5] = darkred + perror + "Use /sellall";
		message[6] = black + "-----------------------------------------------------";
		//value
		message[7] = darkred + perror + "Use /value [item name] (amount)";
		//hb
		message[8] = darkred + perror + "Use /hb (amount or 'max')";
		message[9] = blue + "Sorry, that item is not currently available.";
		//buyid
		message[10] = darkred + perror + "Use /buyid [amount] [id] (damage value)";
		//hs
		message[11] = darkred + perror + "Use /hs (amount or 'max')";
		message[12] = blue + "Sorry, you cannot sell that item.";
		//hv
		message[13] = darkred + perror + "Use /hv (amount)";
		//settax
		message[14] = darkred + perror + "Use /settax [percent]";
		message[15] = gold + "The purchase tax rate has been set!";
		//setininitialtax
		message[16] = darkred + perror + "Use /setinitialtax [percent]";
		message[17] = gold + "The initial purchase tax rate has been set!";
		//setstatictax
		message[18] = darkred + perror + "Use /setstatictax [percent]";
		message[19] = gold + "The static tax rate has been set!";
		//setenchanttax
		message[20] = darkred + perror + "Use /setenchanttax [percent]";
		message[21] = gold + "The enchantment tax rate has been set!";
		//setclassvalue
		message[22] = darkred + perror + "Use /setclassvalue [item class] [value]";
		message[23] = darkred + "Invalid item class.";
		//classvalues
		message[24] = darkred + perror + "Use /classvalues";
		//setradius
		message[25] = darkred + perror + "Use /setradius [radius]";
		message[26] = gold + "The shop radius has been set!";
		//setvalue
		message[27] = darkred + "Invalid Enchantment Name";
		message[28] = darkred + perror + "Use /setvalue [item/enchantment name] [value] ('e')";
		//setstock
		message[29] = darkred + perror + "Use /setstock [item/enchantment name] [stock] ('e')";
		//setmedian
		message[30] = darkred + perror + "Use /setmedian [item/enchantment name] [median] ('e')";
		//setstatic
		message[31] = darkred + perror + "Use /setstatic [item/enchantment name] ('e')";
		//setinitiation
		message[32] = darkred + perror + "Use /setinitiation [item/enchantment name] ('e')";
		//setstaticprice
		message[33] = darkred + perror + "Use /setstaticprice [item/enchantment name] [staticprice] ('e')";
		//setstartprice
		message[34] = darkred + perror + "Use /setstartprice [item/enchantment name] [startprice] ('e')";
		//writeitems
		message[35] = gold + "Item names written to ItemNames.txt!";
		message[36] = gold + "Enchantment names written to EnchantmentNames.txt!";
		message[37] = darkred + perror + "Use /writeitems ['row'/'column'] ('e')";
		//topitems
		message[38] = "You have reached the end.";
		message[39] = darkred + perror + "Use /topitems (page)";
		//topenchants
		message[40] = darkred + perror + "Use /topenchants (page)";
		//ii
		message[41] = darkred + "Invalid item or parameters.  Hold an item and use /iteminfo (id) (damage value)";
		//is
		message[42] = blue + "Sorry, that item or enchantment is not in the database.";
		message[43] = blue + "Sorry, that enchantment is not in the enchantment database.";
		message[44] = darkred + perror + "Hold an item and use /itemsettings (id) (damage value)";
		//taxsettings
		message[45] = darkred + perror + "Use /taxsettings";
		//hc
		message[46] = darkred + "Invalid Usage.  Use /hc";
		//ebuy
		message[47] = darkred + perror + "Use /ebuy [name]";
		//esell
		message[48] = darkred + "The item you're holding has no enchantments!";
		message[49] = darkred + "You must be in a shop to buy or sell enchantments!";
		message[50] = darkred + perror + "Use /esell [name/'max']";
		//evalue
		message[51] = darkred + perror + "Use /evalue (name) ('b'/'s'/'a')";
		//setshop
		message[52] = darkred + perror + "Use /setshop ['p1'/'p2'] [name]";
		//hc
		message[53] = aqua + "Type '" + green + "/hc buy" + aqua + "' for buy commands.";
		message[54] = aqua + "Type '" + green + "/hc sell" + aqua + "' for sell commands.";
		message[55] = aqua + "Type '" + green + "/hc info" + aqua + "' for informational commands.";
		message[56] = aqua + "Type '" + green + "/hc params" + aqua + "' for parameter help.";
		//hc sell
		message[57] = green + "/sell" + red + " [name] " + yellow + "(amount/'max')";
	    message[58] = green + "/hs" + yellow + " (amount/'max')";
		message[59] = green + "/esell" + red + " [name/'max']";
		message[60] = green + "/sellall";
		message[100] = green + "/sellxp" + yellow + " (amount/'max')";
		message[61] = aqua + "For more help type /hc sell " + red + "[command]";
		//hc buy
		message[62] = green + "/buy" + red + " [name]" + yellow + " (amount/'max')";
	    message[63] = green + "/hb" + yellow + " (amount/'max')";
		message[64] = green + "/buyid" + red + " [amount] [id]" + yellow + " (damage value)";
		message[65] = green + "/ebuy" + red + " [name]";
		message[99] = green + "/buyxp" + yellow + " (amount)";
		message[66] = aqua + "For more help type /hc buy " + green + "[command]";
		
		//hc info
		message[67] = green + "/value" + red + " [name]" + yellow + " (amount)";
	    message[68] = green + "/hv" + yellow + " (amount)";
		message[69] = green + "/iteminfo" + yellow + " (id) (damage value)";
		message[70] = green + "/ii" + yellow + " (id) (damage value)";
		message[71] = green + "/topitems" + yellow + " (page)";
	    message[72] = green+ "/topenchants" + yellow + " (page)";
	    message[96] = green+ "/browseshop" + red + " [name]" + yellow + " (page)";
	    message[98] = green+ "/xpinfo";
		message[73] = green + "/evalue" + pink + " {enchantment name} {'b'/'s'/'a'}";
		message[74] = aqua + "For more help type /hc info " + green + "[command]";
		//hc params
		message[75] = red + "[something] = required parameter";
	    message[76] = yellow + "(something) = optional parameter";
		message[77] = pink + "{something} = optional but requires an additional parameter if used";
		message[78] = aqua + "[name] = item name";
		message[79] = green + "[command] = commmand name such as 'buy' or 'topitems'";
		//hc sell sell
		message[80] = ChatColor.AQUA + "Sells 1 of the specified item if no parameters are given, an amount of the item, or all of the item in your inventory if the given parameter is 'max'.";
		//hc sell hs
		message[81] = ChatColor.AQUA + "Sells 1 of the item you're holding if no parameters are given, an amount of the item, or all of the item in your inventory if the given parameter is 'max'.";
		//hc sell esell
		message[82] = ChatColor.AQUA + "Sells the specified enchantment on the item you're holding or all of the item's enchantments if the given parameter is 'max'.";
		//hc sell sellall
		message[83] = ChatColor.AQUA + "Be careful, this sells all the items in your inventory. This does not include armor slots, however.";
		//hc buy buy
		message[84] = ChatColor.AQUA + "Buys 1 of the specified item if no parameters are given, an amount of the item, or as many as your inventory can hold if the given parameter is 'max'.";
		//hc buy hb
		message[85] = ChatColor.AQUA + "Buys 1 of the item you're holding if no parameters are given, an amount of the item, or as many as your inventory can hold if the given parameter is 'max'.";
		//hc buy buyid
		message[86] = ChatColor.AQUA + "Buys an amount of the item with the given id and damage value. (Useful if you don't know the item name.)";
		//hc buy ebuy
		message[87] = ChatColor.AQUA + "Buys the given enchantment and puts it on the item you're holding.";
		//hc info value
		message[88] = ChatColor.AQUA + "Displays the value of the given item(s) name.  This value is theoretical and does not factor in item damage.";
		//hc info hv
		message[89] = ChatColor.AQUA + "Displays the value of the item(s) you're holding. This factors in damage.  However, if you specify more of the item than you have in your inventory, the additional items will use a theoretical value.";
		//hc info iteminfo
		//hc info ii
		message[90] = ChatColor.AQUA + "Displays information about the item you're holding.  If an id and damage value is specified it returns the name of the item, even if you're not holding it.";
		//hc info topitems
		message[91] = ChatColor.AQUA + "Displays all of the items available in the global shop is not in a shop, or in the shop that the player is currently in. Items with the highest stock are shown first.  If no page is specified it shows the first page.";
		//hc info topenchants
		message[92] = ChatColor.AQUA + "Displays all of the enchantments available in all shops, if the player isn't in a shop, or in the shop that the player is currently in. Enchantments with the highest stock are shown first.  If no page is specified it shows the first page.";
		//hc info browseshop
		message[97] = ChatColor.AQUA + "Searches for items or enchantments that begin with the specified name.  It will display the items/enchantments in alphabetical order with their stock and purchase price.  If the player is in a shop it will only display items available in that shop.";
		//hc info evalue
		message[93] = ChatColor.AQUA + "If no parameters are given, displays the sell value, purchase price, and shopstock of all the enchantments on the item you're holding. If given an enchantment name, you must also specify 'b' for buy, 's' for sell, or 'a' for amount. If 'b' for instance, it will display the purchase price for all the enchantment on all item classes. (gold, diamond, iron, etc.)";
		//enchantsettings/es
		message[94] = darkred + perror + "Use /enchantsettings [enchantment] or /es [enchantment]";
		message[95] = blue + "Sorry, that item or enchantment cannot be traded at this shop.";
		//buyxp
		message[101] = aqua + "Buys the specified amount of experience points.";
		//sellxp
		message[102] = aqua + "Sells the specified amount of experience points, or all of the experience points that you have if you type 'max' as the amount.";
		//xpinfo
		message[103] = aqua + "Displays information about how much experience you have, how much experience is needed to level up, and how much experience is needed to reach level 50.";
		//Next message: 97
	}
			
	/**
	 * 
	 * 
	 * This function sends the selected message to the player.
	 * 
	 */
	public void send(CommandSender s, int mnum) {
		s.sendMessage(message[mnum]);	
	}
	


}
