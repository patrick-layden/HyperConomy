package regalowl.hyperconomy;

public enum HyperObjectStatus {
TRADE, BUY, SELL, NONE;

public static HyperObjectStatus fromString(String type) {
type = type.toLowerCase();
if (type.contains("trade")) {
return HyperObjectStatus.TRADE;
} else if (type.contains("buy")) {
return HyperObjectStatus.BUY;
} else if (type.contains("sell")) {
return HyperObjectStatus.SELL;
} else {
return HyperObjectStatus.NONE;
}

}
}