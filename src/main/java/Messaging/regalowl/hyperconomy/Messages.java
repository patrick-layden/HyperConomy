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
	public static final String ECONOMY_NOT_EXIST = formatMessage(lang.getString("ECONOMY_NOT_EXIST"));
	
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
	
	//Exporttoyml command
	public static final String ECONOMY_EXPORTED = formatMessage(lang.getString("ECONOMY_EXPORTED"));
	public static final String EXPORT_PROCEED = formatMessage(lang.getString("EXPORT_PROCEED"));
	public static final String EXPORTTOYML_INVALID = formatMessage(lang.getString("EXPORTTOYML_INVALID"));
	public static final String ONLY_AVAILABLE_SQL = formatMessage(lang.getString("ONLY_AVAILABLE_SQL"));
	
	//Importsql command
	public static final String IMPORTING_TABLES = formatMessage(lang.getString("IMPORTING_TABLES"));
	public static final String IMPORTSQL_WARNING = formatMessage(lang.getString("IMPORTSQL_WARNING"));
	public static final String IMPORTSQL_INVALID = formatMessage(lang.getString("IMPORTSQL_INVALID"));
	
	//Listcategories invalid
	public static final String LISTCATEGORIES_INVALID = formatMessage(lang.getString("LISTCATEGORIES_INVALID"));
	
	
	//Ymladditem command
	public static final String ALREADY_IN_DATABASE = formatMessage(lang.getString("ALREADY_IN_DATABASE"));
	public static final String ITEM_ADDED = formatMessage(lang.getString("ITEM_ADDED"));
	public static final String YMLADDITEM_INVALID = formatMessage(lang.getString("YMLADDITEM_INVALID"));
	
	//hcbackup command
	public static final String ALL_BACKED_UP = formatMessage(lang.getString("ALL_BACKED_UP"));
	public static final String HCBACKUP_INVALID = formatMessage(lang.getString("HCBACKUP_INVALID"));
	
	//hc command
	public static final String HC_BUY = formatMessage(lang.getString("HC_BUY"));
	public static final String HC_SELL = formatMessage(lang.getString("HC_SELL"));
	public static final String HC_INFO = formatMessage(lang.getString("HC_INFO"));
	public static final String HC_PARAMS = formatMessage(lang.getString("HC_PARAMS"));
	public static final String HC_SELL_SELL = formatMessage(lang.getString("HC_SELL_SELL"));
	public static final String HC_SELL_HS = formatMessage(lang.getString("HC_SELL_HS"));
	public static final String HC_SELL_ESELL = formatMessage(lang.getString("HC_SELL_ESELL"));
	public static final String HC_SELL_SELLALL = formatMessage(lang.getString("HC_SELL_SELLALL"));
	public static final String HC_SELL_MORE = formatMessage(lang.getString("HC_SELL_MORE"));
	public static final String HC_BUY_BUY = formatMessage(lang.getString("HC_BUY_BUY"));
	public static final String HC_BUY_HB = formatMessage(lang.getString("HC_BUY_HB"));
	public static final String HC_BUY_BUYID = formatMessage(lang.getString("HC_BUY_BUYID"));
	public static final String HC_BUY_EBUY = formatMessage(lang.getString("HC_BUY_EBUY"));
	public static final String HC_BUY_MORE = formatMessage(lang.getString("HC_BUY_MORE"));
	public static final String HC_INFO_VALUE = formatMessage(lang.getString("HC_INFO_VALUE"));
	public static final String HC_INFO_HV = formatMessage(lang.getString("HC_INFO_HV"));
	public static final String HC_INFO_II = formatMessage(lang.getString("HC_INFO_II"));
	public static final String HC_INFO_TOPITEMS = formatMessage(lang.getString("HC_INFO_TOPITEMS"));
	public static final String HC_INFO_TOPENCHANTS = formatMessage(lang.getString("HC_INFO_TOPENCHANTS"));
	public static final String HC_INFO_BROWSESHOP = formatMessage(lang.getString("HC_INFO_BROWSESHOP"));
	public static final String HC_INFO_XPINFO = formatMessage(lang.getString("HC_INFO_XPINFO"));
	public static final String HC_INFO_EVALUE = formatMessage(lang.getString("HC_INFO_EVALUE"));
	public static final String HC_INFO_MORE = formatMessage(lang.getString("HC_INFO_MORE"));
	public static final String HC_PARAMS_REQUIRED = formatMessage(lang.getString("HC_PARAMS_REQUIRED"));
	public static final String HC_PARAMS_OPTIONAL = formatMessage(lang.getString("HC_PARAMS_OPTIONAL"));
	public static final String HC_PARAMS_ADDITIONAL = formatMessage(lang.getString("HC_PARAMS_ADDITIONAL"));
	public static final String HC_PARAMS_NAME = formatMessage(lang.getString("HC_PARAMS_NAME"));
	public static final String HC_PARAMS_COMMAND = formatMessage(lang.getString("HC_PARAMS_COMMAND"));
	public static final String HC_SELL_DETAIL = formatMessage(lang.getString("HC_SELL_DETAIL"));
	public static final String HC_HS_DETAIL = formatMessage(lang.getString("HC_HS_DETAIL"));
	public static final String HC_ESELL_DETAIL = formatMessage(lang.getString("HC_ESELL_DETAIL"));
	public static final String HC_SELLALL_DETAIL = formatMessage(lang.getString("HC_SELLALL_DETAIL"));
	public static final String HC_BUY_DETAIL = formatMessage(lang.getString("HC_BUY_DETAIL"));
	public static final String HC_HB_DETAIL = formatMessage(lang.getString("HC_HB_DETAIL"));
	public static final String HC_BUYID_DETAIL = formatMessage(lang.getString("HC_BUYID_DETAIL"));
	public static final String HC_EBUY_DETAIL = formatMessage(lang.getString("HC_EBUY_DETAIL"));
	public static final String HC_VALUE_DETAIL = formatMessage(lang.getString("HC_VALUE_DETAIL"));
	public static final String HC_HV_DETAIL = formatMessage(lang.getString("HC_HV_DETAIL"));
	public static final String HC_II_DETAIL = formatMessage(lang.getString("HC_II_DETAIL"));
	public static final String HC_TOPITEMS_DETAIL = formatMessage(lang.getString("HC_TOPITEMS_DETAIL"));
	public static final String HC_TOPENCHANTS_DETAIL = formatMessage(lang.getString("HC_TOPENCHANTS_DETAIL"));
	public static final String HC_BROWSESHOP_DETAIL = formatMessage(lang.getString("HC_BROWSESHOP_DETAIL"));
	public static final String HC_EVALUE_DETAIL = formatMessage(lang.getString("HC_EVALUE_DETAIL"));
	public static final String HC_XPINFO_DETAIL = formatMessage(lang.getString("HC_XPINFO_DETAIL"));
	public static final String HC_INVALID = formatMessage(lang.getString("HC_INVALID"));
	
	//ebuy command
	public static final String ENCHANTMENT_NOT_IN_DATABASE = formatMessage(lang.getString("ENCHANTMENT_NOT_IN_DATABASE"));
	public static final String EBUY_INVALID = formatMessage(lang.getString("EBUY_INVALID"));
	
	//esell command
	public static final String HAS_NO_ENCHANTMENTS = formatMessage(lang.getString("HAS_NO_ENCHANTMENTS"));
	public static final String ESELL_INVALID = formatMessage(lang.getString("ESELL_INVALID"));
	
	//HyperConomy class
	public static final String LOG_BREAK = formatMessage(lang.getString("LOG_BREAK"));
	public static final String DATABASE_CONNECTION_ERROR = formatMessage(lang.getString("DATABASE_CONNECTION_ERROR"));
	public static final String VAULT_NOT_FOUND = formatMessage(lang.getString("VAULT_NOT_FOUND"));
	public static final String HYPERCONOMY_ENABLED = formatMessage(lang.getString("HYPERCONOMY_ENABLED"));
	public static final String HYPERCONOMY_DISABLED = formatMessage(lang.getString("HYPERCONOMY_DISABLED"));
	public static final String BAD_YMLFILE_DETECTED = formatMessage(lang.getString("BAD_YMLFILE_DETECTED"));
	
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
