package regalowl.hyperconomy;


public class HyperObject {
	
	private HyperConomy hc;
	private String name;
	private String economy;
	private String type;
	private String category;
	private String material;
	private int id;
	private int data;
	private int durability;
	private double value;
	private String isstatic;
	private double staticprice;
	private double stock;
	private double median;
	private String initiation;
	private double startprice;
	private double ceiling;
	private double floor;
	private double maxstock;
	
	
	HyperObject() {
		hc = HyperConomy.hc;
	}
	
	public HyperObject(String name, String economy, String type, String category, String material, int id, int data, int durability, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock) {
		this.name = name;
		this.economy = economy;
		this.type = type;
		this.category = category;
		this.material = material;
		this.id = id;
		this.data = data;
		this.durability = durability;
		this.value = value;
		this.isstatic = isstatic;
		this.staticprice = staticprice;
		this.stock = stock;
		this.median = median;
		this.initiation = initiation;
		this.startprice = startprice;
		this.ceiling = ceiling;
		this.floor = floor;
		this.maxstock = maxstock;
	}
	
	
	public String getName() {
		return name;
	}
	public String getEconomy() {
		return economy;
	}
	public String getType() {
		return type;
	}
	public String getCategory() {
		return category;
	}
	public String getMaterial() {
		return material;
	}
	public int getId() {
		return id;
	}
	public int getData() {
		return data;
	}
	public int getDurability() {
		return durability;
	}
	public double getValue() {
		return value;
	}
	public String getIsstatic() {
		return isstatic;
	}
	public double getStaticprice() {
		return staticprice;
	}
	public double getStock() {
		return stock;
	}
	public double getMedian() {
		return median;
	}
	public String getInitiation() {
		return initiation;
	}
	public double getStartprice() {
		return startprice;
	}
	public double getCeiling() {
		return ceiling;
	}
	public double getFloor() {
		return floor;
	}
	public double getMaxstock() {
		return maxstock;
	}
	
	
	
	
	public void setName(String name) {
		String statement = "UPDATE hyperconomy_objects SET NAME='" + name + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.name = name;

	}
	public void setEconomy(String economy) {
		String statement = "UPDATE hyperconomy_objects SET ECONOMY='" + economy + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.economy = economy;
	}
	public void setType(String type) {
		String statement = "UPDATE hyperconomy_objects SET TYPE='" + type + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.type = type;
	}
	public void setCategory(String category) {
		String statement = "UPDATE hyperconomy_objects SET CATEGORY='" + category + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.category = category;
	}
	public void setMaterial(String material) {
		String statement = "UPDATE hyperconomy_objects SET MATERIAL='" + material + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.material = material;
	}
	public void setId(int id) {
		String statement = "UPDATE hyperconomy_objects SET ID='" + id + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.id = id;
	}
	public void setData(int data) {
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.data = data;
	}
	public void setDurability(int durability) {
		String statement = "UPDATE hyperconomy_objects SET DURABILITY='" + durability + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.durability = durability;
	}
	public void setValue(double value) {
		String statement = "UPDATE hyperconomy_objects SET VALUE='" + value + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.value = value;
	}
	public void setIsstatic(String isstatic) {
		String statement = "UPDATE hyperconomy_objects SET STATIC='" + isstatic + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.isstatic = isstatic;
	}
	public void setStaticprice(double staticprice) {
		String statement = "UPDATE hyperconomy_objects SET STATICPRICE='" + staticprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.staticprice = staticprice;
	}
	public void setStock(double stock) {
		Calculation calc = hc.getCalculation();
		stock = calc.round(stock, 2);
		String statement = "UPDATE hyperconomy_objects SET STOCK='" + stock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.stock = stock;
	}
	public void setMedian(double median) {
		String statement = "UPDATE hyperconomy_objects SET MEDIAN='" + median + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.median = median;
	}
	public void setInitiation(String initiation) {
		String statement = "UPDATE hyperconomy_objects SET INITIATION='" + initiation + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.initiation = initiation;
	}
	public void setStartprice(double startprice) {
		String statement = "UPDATE hyperconomy_objects SET STARTPRICE='" + startprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.startprice = startprice;
	}
	public void setCeiling(double ceiling) {
		String statement = "UPDATE hyperconomy_objects SET CEILING='" + ceiling + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.ceiling = ceiling;
	}
	public void setFloor(double floor) {
		String statement = "UPDATE hyperconomy_objects SET FLOOR='" + floor + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.floor = floor;
	}
	public void setMaxstock(double maxstock) {
		String statement = "UPDATE hyperconomy_objects SET MAXSTOCK='" + maxstock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().writeData(statement);
		this.maxstock = maxstock;
	}


	
}
