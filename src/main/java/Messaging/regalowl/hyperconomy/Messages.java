package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;


public final class Messages {
	
	static FileConfiguration lang = HyperConomy.hc.getYaml().getLanguage();
	
	
	//Generic
	public static final String CURRENCY = formatMessage(HyperConomy.hc.getYaml().getConfig().getString("config.currency-symbol"));
	public static final String CC = "\u00A7";
	public static final String SHOP_NOT_EXIST = formatMessage(lang.getString("SHOP_NOT_EXIST"));
	public static final String ADDED_TO = formatMessage(lang.getString("ADDED_TO"));
	public static final String OBJECT_NOT_IN_DATABASE = formatMessage(lang.getString("OBJECT_NOT_IN_DATABASE"));
	public static final String INVALID_ITEM_NAME = formatMessage(lang.getString("INVALID_ITEM_NAME"));
	public static final String MUST_BE_IN_SHOP = formatMessage(lang.getString("MUST_BE_IN_SHOP"));
	public static final String NO_TRADE_PERMISSION = formatMessage(lang.getString("NO_TRADE_PERMISSION"));
	public static final String CANT_BE_TRADED = formatMessage(lang.getString("CANT_BE_TRADED"));
	public static final String OBJECT_NOT_AVAILABLE = formatMessage(lang.getString("OBJECT_NOT_AVAILABLE"));
	public static final String LINE_BREAK = formatMessage(lang.getString("LINE_BREAK"));
	public static final String INSUFFICIENT_FUNDS = formatMessage(lang.getString("INSUFFICIENT_FUNDS"));
	
	//Transaction
	public static final String ONLY_ROOM_TO_BUY = formatMessage(lang.getString("ONLY_ROOM_TO_BUY"));
	public static final String CANNOT_BE_PURCHASED_WITH = formatMessage(lang.getString("CANNOT_BE_PURCHASED_WITH"));
	public static final String CANNOT_BE_SOLD_WITH = formatMessage(lang.getString("CANNOT_BE_SOLD_WITH"));
	public static final String THE_SHOP_DOESNT_HAVE_ENOUGH = formatMessage(lang.getString("THE_SHOP_DOESNT_HAVE_ENOUGH"));
	public static final String CANT_BUY_LESS_THAN_ONE = formatMessage(lang.getString("CANT_BUY_LESS_THAN_ONE"));
	public static final String LOG_BUY = formatMessage(lang.getString("LOG_BUY"));
	public static final String LOG_SELL = formatMessage(lang.getString("LOG_SELL"));
	public static final String LOG_BUY_CHEST = formatMessage(lang.getString("LOG_BUY_CHEST"));
	public static final String LOG_SELL_CHEST = formatMessage(lang.getString("LOG_SELL_CHEST"));
	public static final String STATIC_PRICE = formatMessage(lang.getString("STATIC_PRICE"));
	public static final String INITIAL_PRICE = formatMessage(lang.getString("INITIAL_PRICE"));
	public static final String SHOP_NOT_ENOUGH_MONEY = formatMessage(lang.getString("SHOP_NOT_ENOUGH_MONEY"));
	public static final String YOU_DONT_HAVE_ENOUGH = formatMessage(lang.getString("YOU_DONT_HAVE_ENOUGH"));
	public static final String CANT_SELL_LESS_THAN_ONE = formatMessage(lang.getString("CANT_SELL_LESS_THAN_ONE"));
	public static final String CURRENTLY_CANT_SELL_MORE_THAN = formatMessage(lang.getString("CURRENTLY_CANT_SELL_MORE_THAN"));
	public static final String PURCHASE_MESSAGE = formatMessage(lang.getString("PURCHASE_MESSAGE"));
	public static final String SELL_MESSAGE = formatMessage(lang.getString("SELL_MESSAGE"));
	public static final String PURCHASE_CHEST_MESSAGE = formatMessage(lang.getString("PURCHASE_CHEST_MESSAGE"));
	public static final String SELL_CHEST_MESSAGE = formatMessage(lang.getString("SELL_CHEST_MESSAGE"));
	public static final String CHEST_BUY_NOTIFICATION = formatMessage(lang.getString("CHEST_BUY_NOTIFICATION"));
	public static final String CHEST_SELL_NOTIFICATION = formatMessage(lang.getString("CHEST_SELL_NOTIFICATION"));
	
	//Addcategory command
	public static final String ADD_CATEGORY_INVALID = formatMessage(lang.getString("ADD_CATEGORY_INVALID"));
	public static final String CATEGORY_NOT_EXIST = formatMessage(lang.getString("CATEGORY_NOT_EXIST"));
	
	
	//Additem command
	public static final String SHOP_ALREADY_HAS = formatMessage(lang.getString("SHOP_ALREADY_HAS"));
	public static final String ADD_ITEM_INVALID = formatMessage(lang.getString("ADD_ITEM_INVALID"));
	public static final String ALL_ITEMS_ADDED = formatMessage(lang.getString("ALL_ITEMS_ADDED"));
	
	//Browseshop command
	public static final String BROWSE_SHOP_INVALID = formatMessage(lang.getString("BROWSE_SHOP_INVALID"));
	public static final String REACHED_END = formatMessage(lang.getString("REACHED_END"));
	public static final String AVAILABLE = formatMessage(lang.getString("AVAILABLE"));
	public static final String EACH = formatMessage(lang.getString("EACH"));
	public static final String PAGE = formatMessage(lang.getString("PAGE"));
	
	//Buy command
	public static final String BUY_INVALID = formatMessage(lang.getString("BUY_INVALID"));
	

	//Buyid command
	public static final String BUYID_INVALID = formatMessage(lang.getString("BUYID_INVALID"));
	
	//Buyxp command
	public static final String BUYXP_INVALID = formatMessage(lang.getString("BUYXP_INVALID"));
	
	//Classvalues command
	public static final String BOW_VALUE = formatMessage(lang.getString("BOW_VALUE"));
	public static final String WOOD_VALUE = formatMessage(lang.getString("WOOD_VALUE"));
	public static final String LEATHER_VALUE = formatMessage(lang.getString("LEATHER_VALUE"));
	public static final String STONE_VALUE = formatMessage(lang.getString("STONE_VALUE"));
	public static final String CHAINMAIL_VALUE = formatMessage(lang.getString("CHAINMAIL_VALUE"));
	public static final String IRON_VALUE = formatMessage(lang.getString("IRON_VALUE"));
	public static final String GOLD_VALUE = formatMessage(lang.getString("GOLD_VALUE"));
	public static final String DIAMOND_VALUE = formatMessage(lang.getString("DIAMOND_VALUE"));
	public static final String CLASSVALUES_INVALID = formatMessage(lang.getString("CLASSVALUES_INVALID"));
	
	private static String formatMessage(String message) {
		//message = message.replace("&","\u00A7");
		//message = message.replace("_"," ");
		message = message.replace("&0", ChatColor.BLACK+"");
		message = message.replace("&1", ChatColor.DARK_BLUE+"");
		message = message.replace("&2", ChatColor.DARK_GREEN+"");
		message = message.replace("&3", ChatColor.DARK_AQUA+"");
		message = message.replace("&4", ChatColor.DARK_RED+"");
		message = message.replace("&5", ChatColor.DARK_PURPLE+"");
		message = message.replace("&6", ChatColor.GOLD+"");
		message = message.replace("&7", ChatColor.GRAY+"");
		message = message.replace("&8", ChatColor.DARK_GRAY+"");
		message = message.replace("&9", ChatColor.BLUE+"");
		message = message.replace("&a", ChatColor.GREEN+"");
		message = message.replace("&b", ChatColor.AQUA+"");
		message = message.replace("&c", ChatColor.RED+"");
		message = message.replace("&d", ChatColor.LIGHT_PURPLE+"");
		message = message.replace("&e", ChatColor.YELLOW+"");
		message = message.replace("&f", ChatColor.WHITE+"");
		message = message.replace("&k", ChatColor.MAGIC+"");
		message = message.replace("&l", ChatColor.BOLD+"");
		message = message.replace("&m", ChatColor.STRIKETHROUGH+"");
		message = message.replace("&n", ChatColor.UNDERLINE+"");
		message = message.replace("&o", ChatColor.ITALIC+"");
		message = message.replace("&r", ChatColor.RESET+"");
		return message;
	}

	
}
