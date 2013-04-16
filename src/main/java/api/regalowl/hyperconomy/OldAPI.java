package regalowl.hyperconomy;

import org.bukkit.entity.Player;

//DELETE THIS LATER

public interface OldAPI
{
@Deprecated
double getTheoreticalPurchasePrice(int id, int durability, int amount, String nameOfEconomy);
@Deprecated
double getTheoreticalSaleValue(int id, int durability, int amount, String nameOfEconomy);
@Deprecated
double getTruePurchasePrice(int id, int durability, int amount, String nameOfEconomy);
@Deprecated
double getTrueSaleValue(int id, int durability, int amount, Player player);
@Deprecated
public String getName(String name, String economy);
@Deprecated
public String getEconomy(String name, String economy);
@Deprecated
public String getType(String name, String economy);
@Deprecated
public String getCategory(String name, String economy);
@Deprecated
public String getMaterial(String name, String economy);
@Deprecated
public int getId(String name, String economy);
@Deprecated
public int getData(String name, String economy);
@Deprecated
public int getDurability(String name, String economy);
@Deprecated
public double getValue(String name, String economy);
@Deprecated
public String getStatic(String name, String economy);
@Deprecated
public double getStaticPrice(String name, String economy);
@Deprecated
public double getStock(String name, String economy);
@Deprecated
public double getMedian(String name, String economy);
@Deprecated
public String getInitiation(String name, String economy);
@Deprecated
public double getStartPrice(String name, String economy);
@Deprecated
public void setName(String name, String economy, String newname);
@Deprecated
public void setEconomy(String name, String economy, String neweconomy);
@Deprecated
public void setType(String name, String economy, String newtype);
@Deprecated
public void setCategory(String name, String economy, String newcategory);
@Deprecated
public void setMaterial(String name, String economy, String newmaterial);
@Deprecated
public void setId(String name, String economy, int newid);
@Deprecated
public void setData(String name, String economy, int newdata);
@Deprecated
public void setDurability(String name, String economy, int newdurability);
@Deprecated
public void setValue(String name, String economy, double newvalue);
@Deprecated
public void setStatic(String name, String economy, String newstatic);
@Deprecated
public void setStaticPrice(String name, String economy, double newstaticprice);
@Deprecated
public void setStock(String name, String economy, double newstock);
@Deprecated
public void setMedian(String name, String economy, double newmedian);
@Deprecated
public void setInitiation(String name, String economy, String newinitiation);
@Deprecated
public void setStartPrice(String name, String economy, double newstartprice);
@Deprecated
double getItemPurchasePrice(int id, int data, int amount);
@Deprecated
double getItemSaleValue(int id, int data, int amount);
}