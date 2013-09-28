package regalowl.hyperconomy;

public class PlayerShopObject {

private HyperObject hyperObject;
private double quantity;
private double price;
private boolean customPrice;
private PlayerShopObjectStatus status;




PlayerShopObject(HyperObject hyperObject, double quantity, PlayerShopObjectStatus status, double price) {
this.hyperObject = hyperObject;
this.quantity = quantity;
this.price = price;
this.customPrice = true;
this.status = status;
}

PlayerShopObject(HyperObject hyperObject, double quantity, PlayerShopObjectStatus status) {
this.hyperObject = hyperObject;
this.quantity = quantity;
this.customPrice = false;
this.status = status;
}

public HyperObject getHyperObject() {
return hyperObject;
}
public double getQuantity() {
return quantity;
}
public double getPrice() {
return price;
}
public PlayerShopObjectStatus getStatus() {
return status;
}
public boolean useCustomPrice() {
return customPrice;
}
public void setHyperObject(HyperObject hyperObject) {
this.hyperObject = hyperObject;
}
public void setQuantity(double quantity) {
this.quantity = quantity;
}
public void setPrice(double price) {
this.price = price;
}
public void setUseCustomPrice(boolean customPrice) {
this.customPrice = customPrice;
}
public void setStatus(PlayerShopObjectStatus status) {
this.status = status;
}

}