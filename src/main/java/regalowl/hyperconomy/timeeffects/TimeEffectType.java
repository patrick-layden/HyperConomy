package regalowl.hyperconomy.timeeffects;


public enum TimeEffectType {

	INCREASE_STOCK, DECREASE_STOCK, BALANCE_STOCK, INCREASE_BALANCE, DECREASE_BALANCE, BALANCE_BALANCE, RANDOM_STOCK, RANDOM_BALANCE, NONE;
	
	public static TimeEffectType fromString(String type) {
		if (type == null) {
			return TimeEffectType.NONE;
		}
		type = type.toUpperCase();
		if (type.equalsIgnoreCase("INCREASE_STOCK") || type.equalsIgnoreCase("is")) {
			return TimeEffectType.INCREASE_STOCK;
		} else if (type.equalsIgnoreCase("DECREASE_STOCK") || type.equalsIgnoreCase("ds")) {
			return TimeEffectType.DECREASE_STOCK;
		} else if (type.equalsIgnoreCase("BALANCE_STOCK") || type.equalsIgnoreCase("bs")) {
			return TimeEffectType.BALANCE_STOCK;
		} else if (type.equalsIgnoreCase("INCREASE_BALANCE") || type.equalsIgnoreCase("ib")) {
			return TimeEffectType.INCREASE_BALANCE;
		} else if (type.equalsIgnoreCase("DECREASE_BALANCE") || type.equalsIgnoreCase("db")) {
			return TimeEffectType.DECREASE_BALANCE;
		} else if (type.equalsIgnoreCase("BALANCE_BALANCE") || type.equalsIgnoreCase("bb")) {
			return TimeEffectType.BALANCE_BALANCE;
		} else if (type.equalsIgnoreCase("RANDOM_STOCK") || type.equalsIgnoreCase("rs")) {
			return TimeEffectType.RANDOM_STOCK;
		} else if (type.equalsIgnoreCase("RANDOM_BALANCE") || type.equalsIgnoreCase("rb")) {
			return TimeEffectType.RANDOM_BALANCE;
		} else {
			return TimeEffectType.NONE;
		}
	}
	
	public static boolean isTimeEffectType(String type) {
		TimeEffectType t = fromString(type);
		if (t == TimeEffectType.NONE) return false;
		if (type.toUpperCase().equalsIgnoreCase(t.toString())) return true;
		return false;
	}
	
	public static boolean isTradeObjectType(TimeEffectType type) {
		if (type == TimeEffectType.INCREASE_STOCK || type == TimeEffectType.DECREASE_STOCK || type == TimeEffectType.BALANCE_STOCK || type == TimeEffectType.RANDOM_STOCK) {
			return true;
		}
		return false;
	}
}
