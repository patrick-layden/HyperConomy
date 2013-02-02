package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class APIBridge implements OldAPI
{

	@Deprecated
	public double getTheoreticalPurchasePrice(int id, int durability, int amount, String nameOfEconomy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getTheoreticalPurchasePrice(id, durability, amount, nameOfEconomy);
	}

	@Deprecated
	public double getTheoreticalSaleValue(int id, int durability, int amount, String nameOfEconomy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getTheoreticalSaleValue(id, durability, amount, nameOfEconomy);
	}

	@Deprecated
	public double getTruePurchasePrice(int id, int durability, int amount, String nameOfEconomy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getTruePurchasePrice(id, durability, amount, nameOfEconomy);
	}

	@Deprecated
	public double getTrueSaleValue(int id, int durability, int amount, Player player) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getTrueSaleValue(id, durability, amount, player);
	}

	@Deprecated
	public String getName(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getName(name, economy);
	}

	@Deprecated
	public String getEconomy(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getEconomy(name, economy);
	}

	@Deprecated
	public String getType(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getType(name, economy);
	}

	@Deprecated
	public String getCategory(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getCategory(name, economy);
	}

	@Deprecated
	public String getMaterial(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getMaterial(name, economy);
	}

	@Deprecated
	public int getId(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getId(name, economy);
	}

	@Deprecated
	public int getData(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getData(name, economy);
	}

	@Deprecated
	public int getDurability(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getDurability(name, economy);
	}

	@Deprecated
	public double getValue(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getValue(name, economy);
	}

	@Deprecated
	public String getStatic(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getStatic(name, economy);
	}

	@Deprecated
	public double getStaticPrice(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getStaticPrice(name, economy);
	}

	@Deprecated
	public double getStock(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getStock(name, economy);
	}

	@Deprecated
	public double getMedian(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getMedian(name, economy);
	}

	@Deprecated
	public String getInitiation(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getInitiation(name, economy);
	}

	@Deprecated
	public double getStartPrice(String name, String economy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getStartPrice(name, economy);
	}

	@Deprecated
	public void setName(String name, String economy, String newname) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setName(name, economy, newname);
	}

	@Deprecated
	public void setEconomy(String name, String economy, String neweconomy) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setEconomy(name, economy, neweconomy);
	}

	@Deprecated
	public void setType(String name, String economy, String newtype) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setType(name, economy, newtype);
	}

	@Deprecated
	public void setCategory(String name, String economy, String newcategory) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setCategory(name, economy, newcategory);
	}

	@Deprecated
	public void setMaterial(String name, String economy, String newmaterial) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setMaterial(name, economy, newmaterial);
	}

	@Deprecated
	public void setId(String name, String economy, int newid) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setId(name, economy, newid);
	}

	@Deprecated
	public void setData(String name, String economy, int newdata) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setData(name, economy, newdata);
	}

	@Deprecated
	public void setDurability(String name, String economy, int newdurability) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setDurability(name, economy, newdurability);
	}

	@Deprecated
	public void setValue(String name, String economy, double newvalue) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setValue(name, economy, newvalue);
	}

	@Deprecated
	public void setStatic(String name, String economy, String newstatic) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setStatic(name, economy, newstatic);
	}

	@Deprecated
	public void setStaticPrice(String name, String economy, double newstaticprice) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setStaticPrice(name, economy, newstaticprice);
	}

	@Deprecated
	public void setStock(String name, String economy, double newstock) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setStock(name, economy, newstock);
	}

	@Deprecated
	public void setMedian(String name, String economy, double newmedian) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setMedian(name, economy, newmedian);
	}

	@Deprecated
	public void setInitiation(String name, String economy, String newinitiation) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setInitiation(name, economy, newinitiation);
	}

	@Deprecated
	public void setStartPrice(String name, String economy, double newstartprice) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		hoi.setStartPrice(name, economy, newstartprice);
	}

	@Deprecated
	public double getItemPurchasePrice(int id, int data, int amount) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getTheoreticalPurchasePrice(id, data, amount, "default");
	}

	@Deprecated
	public double getItemSaleValue(int id, int data, int amount) {
		HyperObjectAPI hoi = new HyperObjectAPI();
		return hoi.getTheoreticalSaleValue(id, data, amount, "default");
	}

}