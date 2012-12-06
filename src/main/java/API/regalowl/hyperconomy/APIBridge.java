package regalowl.hyperconomy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class APIBridge implements HyperAPI {
	public double getTheoreticalPurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + durability;
		String name = hc.getnameData(ke);
		Double price = calc.getCost(name, amount, economy);
		price = calc.twoDecimals(price);
		return price;
	}

	public double getTheoreticalSaleValue(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + durability;
		String name = hc.getnameData(ke);
		Double value = calc.getTvalue(name, amount, economy);
		value = calc.twoDecimals(value);
		return value;
	}

	public double getTruePurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		double durabilityPercent = 1.0;
		if (calc.isDurable(id)) {
			ItemStack item = new ItemStack(id, durability);
			durabilityPercent = 1.0 - ((double) durability / item.getType().getMaxDurability());
			durability = 0;
		}
		String ke = id + ":" + durability;
		String name = hc.getnameData(ke);
		Double price = calc.getCost(name, amount, economy) * durabilityPercent;
		double tax = calc.getPurchaseTax(name, economy, price);
		price = tax + price;
		price = calc.twoDecimals(price);
		return price;
	}

	public double getTrueSaleValue(int id, int durability, int amount, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		double durabilityPercent = 1.0;
		if (calc.isDurable(id)) {
			ItemStack item = new ItemStack(id, durability);
			durabilityPercent = 1.0 - ((double) durability / item.getType().getMaxDurability());
			durability = 0;
		}
		String ke = id + ":" + durability;
		String name = hc.getnameData(ke);
		Double value = calc.getValue(name, amount, player) * durabilityPercent;
		double salestax = calc.getSalesTax(player, value);
		value = value - salestax;
		value = calc.twoDecimals(value);
		return value;
	}

	public String getName(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getName(name, economy);
	}

	public String getEconomy(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getEconomy(name, economy);
	}

	public String getType(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getType(name, economy);
	}

	public String getCategory(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getCategory(name, economy);
	}

	public String getMaterial(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getMaterial(name, economy);
	}

	public int getId(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getId(name, economy);
	}

	public int getData(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getData(name, economy);
	}

	public int getDurability(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getDurability(name, economy);
	}

	public double getValue(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getValue(name, economy);
	}

	public String getStatic(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getStatic(name, economy);
	}

	public double getStaticPrice(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getStaticPrice(name, economy);
	}

	public double getStock(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getStock(name, economy);
	}

	public double getMedian(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getMedian(name, economy);
	}

	public String getInitiation(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getInitiation(name, economy);
	}

	public double getStartPrice(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		return sf.getStartPrice(name, economy);
	}

	public void setName(String name, String economy, String newname) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setName(name, economy, newname);
	}

	public void setEconomy(String name, String economy, String neweconomy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setEconomy(name, economy, neweconomy);
	}

	public void setType(String name, String economy, String newtype) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setType(name, economy, newtype);
	}

	public void setCategory(String name, String economy, String newcategory) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setCategory(name, economy, newcategory);
	}

	public void setMaterial(String name, String economy, String newmaterial) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setMaterial(name, economy, newmaterial);
	}

	public void setId(String name, String economy, int newid) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setId(name, economy, newid);
	}

	public void setData(String name, String economy, int newdata) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setData(name, economy, newdata);
	}

	public void setDurability(String name, String economy, int newdurability) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setDurability(name, economy, newdurability);
	}

	public void setValue(String name, String economy, double newvalue) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setValue(name, economy, newvalue);
	}

	public void setStatic(String name, String economy, String newstatic) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setStatic(name, economy, newstatic);
	}

	public void setStaticPrice(String name, String economy, double newstaticprice) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setStaticPrice(name, economy, newstaticprice);
	}

	public void setStock(String name, String economy, double newstock) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setStock(name, economy, newstock);
	}

	public void setMedian(String name, String economy, double newmedian) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setMedian(name, economy, newmedian);
	}

	public void setInitiation(String name, String economy, String newinitiation) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setInitiation(name, economy, newinitiation);
	}

	public void setStartPrice(String name, String economy, double newstartprice) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		sf.setStartPrice(name, economy, newstartprice);
	}

	public boolean checkFunds(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		return acc.checkFunds(money, player);
	}

	public void withdraw(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.withdraw(money, player);
	}

	public void withdrawAccount(String name, double money) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.withdrawAccount(name, money);
	}

	public void deposit(double money, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.deposit(money, player);
	}

	public void depositAccount(String name, double money) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.depositAccount(name, money);
	}

	public void withdrawShop(double money) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.withdrawShop(money);
	}

	public void depositShop(double money) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.depositShop(money);
	}

	public void setBalance(String name, double balance) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.setBalance(name, balance);
	}

	public boolean checkAccount(String name) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		return acc.checkAccount(name);
	}

	public boolean checkshopBalance(double money) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		return acc.checkshopBalance(money);
	}

	public void checkshopAccount() {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.checkshopAccount();
	}

	public double getBalance(String account) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		return acc.getBalance(account);
	}

	public void createAccount(String account) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		acc.createAccount(account);
	}

	public double getItemPurchasePrice(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);
		Double price = calc.getCost(name, amount, "default");
		price = calc.twoDecimals(price);
		return price;
	}

	public double getItemSaleValue(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);
		Double value = calc.getTvalue(name, amount, "default");
		value = calc.twoDecimals(value);
		return value;
	}
}
