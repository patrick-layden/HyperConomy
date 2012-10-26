package regalowl.hyperconomy;

import org.bukkit.ChatColor;


public final class Messages {
	
	//static FileConfiguration lang = HyperConomy.hc.getYaml().getLanguage();
	
	
	static LanguageFile lang = new LanguageFile();
	
	//Generic
	public static final String CURRENCY = formatMessage(HyperConomy.hc.getYaml().getConfig().getString("config.currency-symbol"));
	public static final String CC = "\u00A7";
	public static final String SHOP_NOT_EXIST = formatMessage(lang.getConstant("SHOP_NOT_EXIST"));
	public static final String ADDED_TO = formatMessage(lang.getConstant("ADDED_TO"));
	public static final String OBJECT_NOT_IN_DATABASE = formatMessage(lang.getConstant("OBJECT_NOT_IN_DATABASE"));
	public static final String INVALID_ITEM_NAME = formatMessage(lang.getConstant("INVALID_ITEM_NAME"));
	public static final String MUST_BE_IN_SHOP = formatMessage(lang.getConstant("MUST_BE_IN_SHOP"));
	public static final String NO_TRADE_PERMISSION = formatMessage(lang.getConstant("NO_TRADE_PERMISSION"));
	public static final String CANT_BE_TRADED = formatMessage(lang.getConstant("CANT_BE_TRADED"));
	public static final String OBJECT_NOT_AVAILABLE = formatMessage(lang.getConstant("OBJECT_NOT_AVAILABLE"));
	public static final String LINE_BREAK = formatMessage(lang.getConstant("LINE_BREAK"));
	public static final String INSUFFICIENT_FUNDS = formatMessage(lang.getConstant("INSUFFICIENT_FUNDS"));
	public static final String ECONOMY_NOT_EXIST = formatMessage(lang.getConstant("ECONOMY_NOT_EXIST"));
	
	//Transaction
	public static final String ONLY_ROOM_TO_BUY = formatMessage(lang.getConstant("ONLY_ROOM_TO_BUY"));
	public static final String CANNOT_BE_PURCHASED_WITH = formatMessage(lang.getConstant("CANNOT_BE_PURCHASED_WITH"));
	public static final String CANNOT_BE_SOLD_WITH = formatMessage(lang.getConstant("CANNOT_BE_SOLD_WITH"));
	public static final String THE_SHOP_DOESNT_HAVE_ENOUGH = formatMessage(lang.getConstant("THE_SHOP_DOESNT_HAVE_ENOUGH"));
	public static final String CANT_BUY_LESS_THAN_ONE = formatMessage(lang.getConstant("CANT_BUY_LESS_THAN_ONE"));
	public static final String LOG_BUY = formatMessage(lang.getConstant("LOG_BUY"));
	public static final String LOG_SELL = formatMessage(lang.getConstant("LOG_SELL"));
	public static final String LOG_BUY_CHEST = formatMessage(lang.getConstant("LOG_BUY_CHEST"));
	public static final String LOG_SELL_CHEST = formatMessage(lang.getConstant("LOG_SELL_CHEST"));
	public static final String STATIC_PRICE = formatMessage(lang.getConstant("STATIC_PRICE"));
	public static final String INITIAL_PRICE = formatMessage(lang.getConstant("INITIAL_PRICE"));
	public static final String SHOP_NOT_ENOUGH_MONEY = formatMessage(lang.getConstant("SHOP_NOT_ENOUGH_MONEY"));
	public static final String YOU_DONT_HAVE_ENOUGH = formatMessage(lang.getConstant("YOU_DONT_HAVE_ENOUGH"));
	public static final String CANT_SELL_LESS_THAN_ONE = formatMessage(lang.getConstant("CANT_SELL_LESS_THAN_ONE"));
	public static final String CURRENTLY_CANT_SELL_MORE_THAN = formatMessage(lang.getConstant("CURRENTLY_CANT_SELL_MORE_THAN"));
	public static final String PURCHASE_MESSAGE = formatMessage(lang.getConstant("PURCHASE_MESSAGE"));
	public static final String SELL_MESSAGE = formatMessage(lang.getConstant("SELL_MESSAGE"));
	public static final String PURCHASE_CHEST_MESSAGE = formatMessage(lang.getConstant("PURCHASE_CHEST_MESSAGE"));
	public static final String SELL_CHEST_MESSAGE = formatMessage(lang.getConstant("SELL_CHEST_MESSAGE"));
	public static final String CHEST_BUY_NOTIFICATION = formatMessage(lang.getConstant("CHEST_BUY_NOTIFICATION"));
	public static final String CHEST_SELL_NOTIFICATION = formatMessage(lang.getConstant("CHEST_SELL_NOTIFICATION"));
	
	//Addcategory command
	public static final String ADD_CATEGORY_INVALID = formatMessage(lang.getConstant("ADD_CATEGORY_INVALID"));
	public static final String CATEGORY_NOT_EXIST = formatMessage(lang.getConstant("CATEGORY_NOT_EXIST"));
	
	
	//Additem command
	public static final String SHOP_ALREADY_HAS = formatMessage(lang.getConstant("SHOP_ALREADY_HAS"));
	public static final String ADD_ITEM_INVALID = formatMessage(lang.getConstant("ADD_ITEM_INVALID"));
	public static final String ALL_ITEMS_ADDED = formatMessage(lang.getConstant("ALL_ITEMS_ADDED"));
	
	//Browseshop command
	public static final String BROWSE_SHOP_INVALID = formatMessage(lang.getConstant("BROWSE_SHOP_INVALID"));
	public static final String REACHED_END = formatMessage(lang.getConstant("REACHED_END"));
	public static final String AVAILABLE = formatMessage(lang.getConstant("AVAILABLE"));
	public static final String EACH = formatMessage(lang.getConstant("EACH"));
	public static final String PAGE = formatMessage(lang.getConstant("PAGE"));
	
	//Buy command
	public static final String BUY_INVALID = formatMessage(lang.getConstant("BUY_INVALID"));
	

	//Buyid command
	public static final String BUYID_INVALID = formatMessage(lang.getConstant("BUYID_INVALID"));
	
	//Buyxp command
	public static final String BUYXP_INVALID = formatMessage(lang.getConstant("BUYXP_INVALID"));
	
	//Classvalues command
	public static final String BOW_VALUE = formatMessage(lang.getConstant("BOW_VALUE"));
	public static final String WOOD_VALUE = formatMessage(lang.getConstant("WOOD_VALUE"));
	public static final String LEATHER_VALUE = formatMessage(lang.getConstant("LEATHER_VALUE"));
	public static final String STONE_VALUE = formatMessage(lang.getConstant("STONE_VALUE"));
	public static final String CHAINMAIL_VALUE = formatMessage(lang.getConstant("CHAINMAIL_VALUE"));
	public static final String IRON_VALUE = formatMessage(lang.getConstant("IRON_VALUE"));
	public static final String GOLD_VALUE = formatMessage(lang.getConstant("GOLD_VALUE"));
	public static final String DIAMOND_VALUE = formatMessage(lang.getConstant("DIAMOND_VALUE"));
	public static final String CLASSVALUES_INVALID = formatMessage(lang.getConstant("CLASSVALUES_INVALID"));
	
	//Exporttoyml command
	public static final String ECONOMY_EXPORTED = formatMessage(lang.getConstant("ECONOMY_EXPORTED"));
	public static final String EXPORT_PROCEED = formatMessage(lang.getConstant("EXPORT_PROCEED"));
	public static final String EXPORTTOYML_INVALID = formatMessage(lang.getConstant("EXPORTTOYML_INVALID"));
	public static final String ONLY_AVAILABLE_SQL = formatMessage(lang.getConstant("ONLY_AVAILABLE_SQL"));
	
	//Importsql command
	public static final String IMPORTING_TABLES = formatMessage(lang.getConstant("IMPORTING_TABLES"));
	public static final String IMPORTSQL_WARNING = formatMessage(lang.getConstant("IMPORTSQL_WARNING"));
	public static final String IMPORTSQL_INVALID = formatMessage(lang.getConstant("IMPORTSQL_INVALID"));
	
	//Listcategories invalid
	public static final String LISTCATEGORIES_INVALID = formatMessage(lang.getConstant("LISTCATEGORIES_INVALID"));
	
	
	//Ymladditem command
	public static final String ALREADY_IN_DATABASE = formatMessage(lang.getConstant("ALREADY_IN_DATABASE"));
	public static final String ITEM_ADDED = formatMessage(lang.getConstant("ITEM_ADDED"));
	public static final String YMLADDITEM_INVALID = formatMessage(lang.getConstant("YMLADDITEM_INVALID"));
	
	//hcbackup command
	public static final String ALL_BACKED_UP = formatMessage(lang.getConstant("ALL_BACKED_UP"));
	public static final String HCBACKUP_INVALID = formatMessage(lang.getConstant("HCBACKUP_INVALID"));
	
	//hc command
	public static final String HC_BUY = formatMessage(lang.getConstant("HC_BUY"));
	public static final String HC_SELL = formatMessage(lang.getConstant("HC_SELL"));
	public static final String HC_INFO = formatMessage(lang.getConstant("HC_INFO"));
	public static final String HC_PARAMS = formatMessage(lang.getConstant("HC_PARAMS"));
	public static final String HC_SELL_SELL = formatMessage(lang.getConstant("HC_SELL_SELL"));
	public static final String HC_SELL_HS = formatMessage(lang.getConstant("HC_SELL_HS"));
	public static final String HC_SELL_ESELL = formatMessage(lang.getConstant("HC_SELL_ESELL"));
	public static final String HC_SELL_SELLALL = formatMessage(lang.getConstant("HC_SELL_SELLALL"));
	public static final String HC_SELL_MORE = formatMessage(lang.getConstant("HC_SELL_MORE"));
	public static final String HC_BUY_BUY = formatMessage(lang.getConstant("HC_BUY_BUY"));
	public static final String HC_BUY_HB = formatMessage(lang.getConstant("HC_BUY_HB"));
	public static final String HC_BUY_BUYID = formatMessage(lang.getConstant("HC_BUY_BUYID"));
	public static final String HC_BUY_EBUY = formatMessage(lang.getConstant("HC_BUY_EBUY"));
	public static final String HC_BUY_MORE = formatMessage(lang.getConstant("HC_BUY_MORE"));
	public static final String HC_INFO_VALUE = formatMessage(lang.getConstant("HC_INFO_VALUE"));
	public static final String HC_INFO_HV = formatMessage(lang.getConstant("HC_INFO_HV"));
	public static final String HC_INFO_II = formatMessage(lang.getConstant("HC_INFO_II"));
	public static final String HC_INFO_TOPITEMS = formatMessage(lang.getConstant("HC_INFO_TOPITEMS"));
	public static final String HC_INFO_TOPENCHANTS = formatMessage(lang.getConstant("HC_INFO_TOPENCHANTS"));
	public static final String HC_INFO_BROWSESHOP = formatMessage(lang.getConstant("HC_INFO_BROWSESHOP"));
	public static final String HC_INFO_XPINFO = formatMessage(lang.getConstant("HC_INFO_XPINFO"));
	public static final String HC_INFO_EVALUE = formatMessage(lang.getConstant("HC_INFO_EVALUE"));
	public static final String HC_INFO_MORE = formatMessage(lang.getConstant("HC_INFO_MORE"));
	public static final String HC_PARAMS_REQUIRED = formatMessage(lang.getConstant("HC_PARAMS_REQUIRED"));
	public static final String HC_PARAMS_OPTIONAL = formatMessage(lang.getConstant("HC_PARAMS_OPTIONAL"));
	public static final String HC_PARAMS_ADDITIONAL = formatMessage(lang.getConstant("HC_PARAMS_ADDITIONAL"));
	public static final String HC_PARAMS_NAME = formatMessage(lang.getConstant("HC_PARAMS_NAME"));
	public static final String HC_PARAMS_COMMAND = formatMessage(lang.getConstant("HC_PARAMS_COMMAND"));
	public static final String HC_SELL_DETAIL = formatMessage(lang.getConstant("HC_SELL_DETAIL"));
	public static final String HC_HS_DETAIL = formatMessage(lang.getConstant("HC_HS_DETAIL"));
	public static final String HC_ESELL_DETAIL = formatMessage(lang.getConstant("HC_ESELL_DETAIL"));
	public static final String HC_SELLALL_DETAIL = formatMessage(lang.getConstant("HC_SELLALL_DETAIL"));
	public static final String HC_BUY_DETAIL = formatMessage(lang.getConstant("HC_BUY_DETAIL"));
	public static final String HC_HB_DETAIL = formatMessage(lang.getConstant("HC_HB_DETAIL"));
	public static final String HC_BUYID_DETAIL = formatMessage(lang.getConstant("HC_BUYID_DETAIL"));
	public static final String HC_EBUY_DETAIL = formatMessage(lang.getConstant("HC_EBUY_DETAIL"));
	public static final String HC_VALUE_DETAIL = formatMessage(lang.getConstant("HC_VALUE_DETAIL"));
	public static final String HC_HV_DETAIL = formatMessage(lang.getConstant("HC_HV_DETAIL"));
	public static final String HC_II_DETAIL = formatMessage(lang.getConstant("HC_II_DETAIL"));
	public static final String HC_TOPITEMS_DETAIL = formatMessage(lang.getConstant("HC_TOPITEMS_DETAIL"));
	public static final String HC_TOPENCHANTS_DETAIL = formatMessage(lang.getConstant("HC_TOPENCHANTS_DETAIL"));
	public static final String HC_BROWSESHOP_DETAIL = formatMessage(lang.getConstant("HC_BROWSESHOP_DETAIL"));
	public static final String HC_EVALUE_DETAIL = formatMessage(lang.getConstant("HC_EVALUE_DETAIL"));
	public static final String HC_XPINFO_DETAIL = formatMessage(lang.getConstant("HC_XPINFO_DETAIL"));
	public static final String HC_INVALID = formatMessage(lang.getConstant("HC_INVALID"));
	
	//ebuy command
	public static final String ENCHANTMENT_NOT_IN_DATABASE = formatMessage(lang.getConstant("ENCHANTMENT_NOT_IN_DATABASE"));
	public static final String EBUY_INVALID = formatMessage(lang.getConstant("EBUY_INVALID"));
	
	//esell command
	public static final String HAS_NO_ENCHANTMENTS = formatMessage(lang.getConstant("HAS_NO_ENCHANTMENTS"));
	public static final String ESELL_INVALID = formatMessage(lang.getConstant("ESELL_INVALID"));
	
	//HyperConomy class
	public static final String LOG_BREAK = formatMessage(lang.getConstant("LOG_BREAK"));
	public static final String DATABASE_CONNECTION_ERROR = formatMessage(lang.getConstant("DATABASE_CONNECTION_ERROR"));
	public static final String VAULT_NOT_FOUND = formatMessage(lang.getConstant("VAULT_NOT_FOUND"));
	public static final String HYPERCONOMY_ENABLED = formatMessage(lang.getConstant("HYPERCONOMY_ENABLED"));
	public static final String HYPERCONOMY_DISABLED = formatMessage(lang.getConstant("HYPERCONOMY_DISABLED"));
	public static final String BAD_YMLFILE_DETECTED = formatMessage(lang.getConstant("BAD_YMLFILE_DETECTED"));
	
	//ETransaction class
	public static final String ITEM_ALREADY_HAS_ENCHANTMENT = formatMessage(lang.getConstant("ITEM_ALREADY_HAS_ENCHANTMENT"));
	public static final String ITEM_CANT_ACCEPT_ENCHANTMENT = formatMessage(lang.getConstant("ITEM_CANT_ACCEPT_ENCHANTMENT"));
	public static final String ITEM_DOESNT_HAVE_ENCHANTMENT = formatMessage(lang.getConstant("ITEM_DOESNT_HAVE_ENCHANTMENT"));
	public static final String ENCHANTMENT_SELL_MESSAGE = formatMessage(lang.getConstant("ENCHANTMENT_SELL_MESSAGE"));
	public static final String ENCHANTMENT_PURCHASE_MESSAGE = formatMessage(lang.getConstant("ENCHANTMENT_PURCHASE_MESSAGE"));
	public static final String CHEST_ENCHANTMENT_BUY_NOTIFICATION = formatMessage(lang.getConstant("CHEST_ENCHANTMENT_BUY_NOTIFICATION"));
	public static final String PURCHASE_ENCHANTMENT_CHEST_MESSAGE = formatMessage(lang.getConstant("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"));
	public static final String LOG_BUY_ENCHANTMENT = formatMessage(lang.getConstant("LOG_BUY_ENCHANTMENT"));
	public static final String LOG_SELL_ENCHANTMENT = formatMessage(lang.getConstant("LOG_SELL_ENCHANTMENT"));
	public static final String LOG_BUY_CHEST_ENCHANTMENT = formatMessage(lang.getConstant("LOG_BUY_CHEST_ENCHANTMENT"));
	
	//sell command
	public static final String SELL_INVALID = formatMessage(lang.getConstant("SELL_INVALID"));
	
	//sellall command
	public static final String ONE_OR_MORE_CANT_BE_TRADED = formatMessage(lang.getConstant("ONE_OR_MORE_CANT_BE_TRADED"));
	public static final String CANT_BUY_SELL_ENCHANTED_ITEMS = formatMessage(lang.getConstant("CANT_BUY_SELL_ENCHANTED_ITEMS"));
	public static final String SELLALL_INVALID = formatMessage(lang.getConstant("SELLALL_INVALID"));
	
	//hb command
	public static final String HB_INVALID = formatMessage(lang.getConstant("HB_INVALID"));
	
	//hs command
	public static final String HS_INVALID = formatMessage(lang.getConstant("HS_INVALID"));
	
	//value command
	public static final String GLOBAL_SHOP_CURRENTLY_HAS = formatMessage(lang.getConstant("GLOBAL_SHOP_CURRENTLY_HAS"));
	public static final String CAN_BE_PURCHASED_FOR = formatMessage(lang.getConstant("CAN_BE_PURCHASED_FOR"));
	public static final String CAN_BE_SOLD_FOR = formatMessage(lang.getConstant("CAN_BE_SOLD_FOR"));
	public static final String VALUE_INVALID = formatMessage(lang.getConstant("VALUE_INVALID"));
	
	//hv command
	public static final String HV_INVALID = formatMessage(lang.getConstant("HV_INVALID"));
	
	//Notification class
	public static final String SQL_NOTIFICATION = formatMessage(lang.getConstant("SQL_NOTIFICATION"));
	public static final String NOTIFICATION = formatMessage(lang.getConstant("NOTIFICATION"));
	
	
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
