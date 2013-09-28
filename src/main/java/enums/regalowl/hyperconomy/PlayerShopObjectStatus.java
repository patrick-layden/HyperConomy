package regalowl.hyperconomy;

public enum PlayerShopObjectStatus {
TRADE, BUY, SELL, NONE;

public static PlayerShopObjectStatus fromString(String type) {
type = type.toLowerCase();
if (type.contains("trade")) {
return PlayerShopObjectStatus.TRADE;
} else if (type.contains("buy")) {
return PlayerShopObjectStatus.BUY;
} else if (type.contains("sell")) {
return PlayerShopObjectStatus.SELL;
} else {
return PlayerShopObjectStatus.NONE;
}

}
}