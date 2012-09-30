package regalowl.hyperconomy;

import org.bukkit.configuration.file.FileConfiguration;

public final class Messages {
	
	static FileConfiguration lang = HyperConomy.hc.getYaml().getLanguage();
	
	//Generic
	public static final String SHOP_NOT_EXIST = formatMessage(lang.getString("SHOP_NOT_EXIST"));
	public static final String ADDED_TO = formatMessage(lang.getString("ADDED_TO"));
	public static final String OBJECT_NOT_IN_DATABASE = formatMessage(lang.getString("OBJECT_NOT_IN_DATABASE"));
	public static final String INVALID_ITEM_NAME = formatMessage(lang.getString("INVALID_ITEM_NAME"));
	public static final String MUST_BE_IN_SHOP = formatMessage(lang.getString("MUST_BE_IN_SHOP"));
	
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
	public static final String CANT_BE_TRADED = formatMessage(lang.getString("CANT_BE_TRADED"));
	public static final String NO_TRADE_PERMISSION = formatMessage(lang.getString("NO_TRADE_PERMISSION"));
	

	
	
	private static String formatMessage(String message) {
		//message = message.replace("&","§");
		message = message.replace("&","\u00A7");
		message = message.replace("_"," ");
		return message;
	}

	
}
