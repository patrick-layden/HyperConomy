package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;

import regalowl.databukkit.CommonFunctions;

public class CompositeItem extends BasicObject implements HyperItem {


	private String material;
	private Material materialEnum;
	private int data;
	private int durability;

	
	private FileConfiguration composites;
	private CommonFunctions cf;
	
	private ConcurrentHashMap<HyperItem,Double> components = new ConcurrentHashMap<HyperItem,Double>();
	
	
	public CompositeItem(String name, String economy) {
		super(name,economy,"","","",0,"",0,0,0,"",0,0,0,0);
		hc = HyperConomy.hc;
		cf = hc.gCF();
		composites = hc.gYH().gFC("composites");
		this.displayName = composites.getString(this.name + ".name.display");
		String sAliases = composites.getString(this.name + ".name.aliases");
		ArrayList<String> tAliases = hc.gCF().explode(sAliases, ",");
		for (String cAlias:tAliases) {
			this.aliases.add(cAlias);
		}
		this.type = HyperObjectType.fromString(composites.getString(this.name + ".information.type"));
		this.material = composites.getString(this.name + ".information.material");
		this.materialEnum = Material.matchMaterial(this.material);
		this.data = composites.getInt(this.name + ".information.data");
		this.durability = composites.getInt(this.name + ".information.data");
		
		HashMap<String,String> tempComponents = cf.explodeMap(composites.getString(this.name + ".components"));
		for (Map.Entry<String,String> entry : tempComponents.entrySet()) {
		    String oname = entry.getKey();
		    String amountString = entry.getValue();
		    double amount = 0.0;
		    if (amountString.contains("/")) {
				int top = Integer.parseInt(amountString.substring(0, amountString.indexOf("/")));
				int bottom = Integer.parseInt(amountString.substring(amountString.indexOf("/") + 1, amountString.length()));
				amount = ((double)top/(double)bottom);
		    } else {
		    	int number = Integer.parseInt(amountString);
		    	amount = (double)number;
		    }
		    HyperItem ho = hc.getEconomyManager().getEconomy(economy).getHyperItem(oname);
		    this.components.put(ho, amount);
		}
	}

	public String getMaterial() {
		return material;
	}
	public Material getMaterialEnum() {
		return materialEnum;
	}
	public int getData() {
		return data;
	}
	public int getDurability() {
		return durability;
	}
	
	@Override
	public double getValue() {
		double value = 0;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getValue() * qty);
		}
		return value;
	}
	@Override
	public String getIsstatic() {
		String isstatic = "true";
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    if (!Boolean.parseBoolean(ho.getIsstatic())) {
		    	isstatic = "false";
		    }
		}
		return isstatic;
	}
	@Override
	public double getStaticprice() {
		double staticprice = 0;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    staticprice += (ho.getStaticprice() * qty);
		}
		return staticprice;
	}
	@Override
	public double getStock() {
		double stock = 999999999.99;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    double cs = (ho.getStock() / qty);
		    if (cs < stock) {
		    	stock = cs;
		    }
		}
		return stock;
	}
	@Override
	public double getTotalStock() {
		double stock = 999999999.99;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    double cs = (ho.getTotalStock() / qty);
		    if (cs < stock) {
		    	stock = cs;
		    }
		}
		return stock;
	}
	@Override
	public double getMedian() {
		double median = 999999999;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    if (ho.getMedian() < median) {
		    	median = ho.getMedian();
		    }
		}
		return median;
	}
	@Override
	public String getInitiation() {
		String initial = "false";
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    if (Boolean.parseBoolean(ho.getInitiation())) {
		    	initial = "true";
		    }
		}
		return initial;
	}
	@Override
	public double getStartprice() {
		double startprice = 0;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    startprice += (ho.getStartprice() * qty);
		}
		return startprice;
	}
	@Override
	public double getCeiling() {
		double ceiling = 9999999999999.99;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    double cc = ho.getCeiling();
		    if (cc < ceiling) {
		    	ceiling = cc;
		    }
		}
		if (ceiling <= 0) {
			return 9999999999999.99;
		}
		return ceiling;
	}
	@Override
	public double getFloor() {
		double floor = 0;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    double cf = ho.getFloor();
		    if (cf > floor) {
		    	floor = cf;
		    }
		}
		if (floor < 0) {
			return 0.0;
		}
		return floor;
	}
	@Override
	public double getMaxstock() {
		double maxstock = 999999999;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    double cm = ho.getMaxstock();
		    if (cm < maxstock) {
		    	maxstock = cm;
		    }
		}
		return maxstock;
	}
	

	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public void setEconomy(String economy) {
		this.economy = economy;
	}
	@Override
	public void setType(String type) {
		this.type = HyperObjectType.fromString(type);
		composites.set(this.name + ".information.type", this.type.toString());
	}
	public void setMaterial(String material) {
		this.material = material;
		this.materialEnum = Material.matchMaterial(material);
		composites.set(this.name + ".information.material", this.material);
	}
	public void setMaterial(Material material) {
		String materialS = material.toString();
		this.material = materialS;
		this.materialEnum = material;
		composites.set(this.name + ".information.material", materialS);
	}
	public void setData(int data) {
		this.data = data;
		composites.set(this.name + ".information.data", this.data);
	}
	public void setDurability(int durability) {
		this.durability = durability;
		composites.set(this.name + ".information.durability", this.durability);
	}
	@Override
	public void setValue(double value) {}
	@Override
	public void setIsstatic(String isstatic) {}
	@Override
	public void setStaticprice(double staticprice) {}
	@Override
	public void setStock(double stock) {
		if (stock < 0.0) {stock = 0.0;}
		double difference = stock - getStock();
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    double newStock = ho.getStock() + (difference * qty);
		    ho.setStock(newStock);
		}
	}
	@Override
	public void setMedian(double median) {}
	@Override
	public void setInitiation(String initiation) {}
	@Override
	public void setStartprice(double startprice) {}
	@Override
	public void setCeiling(double ceiling) {}
	@Override
	public void setFloor(double floor) {}
	@Override
	public void setMaxstock(double maxstock) {}
	
	
	
	
	
	
	@Override
	public int getMaxInitial() {
		int maxInitial = 999999999;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    int ci = (int) Math.floor(ho.getMaxInitial() / qty);
		    if (ci < maxInitial) {
		    	maxInitial = ci;
		    }
		}
		return maxInitial;
	}

	
	@Override
	public double getCost(int amount) {
		double cost = 0;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
			HyperItem ho = entry.getKey();
		    Double qty = entry.getValue();
		    cost += (ho.getCost(amount) * qty);
		}
		return cost;
	}
	
	@Override
	public double getValue(int amount) {
		double value = 0;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
			HyperItem ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getValue(amount) * qty);
		}
		return value;
	}
	public double getValue(int amount, HyperPlayer hp) {
		double value = 0;
		for (Map.Entry<HyperItem,Double> entry : components.entrySet()) {
			HyperItem ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getValue(amount, hp) * qty);
		}
		return value;
	}


	
	public boolean isDurable() {
		if (materialEnum != null && materialEnum.getMaxDurability() > 0) {
			return true;
		}
		return false;
	}
	

	
	public ConcurrentHashMap<HyperItem,Double> getComponents() {
		return components;
	}

	public int count(Inventory inventory) {
		int totalitems = 0;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			HyperItemStack his = new HyperItemStack(stack);
			if (stack != null && !his.hasenchants()) {
				if (stack.getType() == materialEnum && his.getDamageValue() == data) {
					totalitems += stack.getAmount();
				}
			}
		}
		return totalitems;
	}
	
	public int getAvailableSpace(Inventory inventory) {
		try {
			ItemStack stack = getItemStack();
			int maxstack = stack.getMaxStackSize();
			int availablespace = 0;
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				ItemStack citem = inventory.getItem(slot);
				HyperItemStack his = new HyperItemStack(citem);
				if (citem == null) {
					availablespace += maxstack;
				} else if (citem.getType() == materialEnum && his.getDamageValue() == data) {
					availablespace += (maxstack - citem.getAmount());
				}
			}
			return availablespace;
		} catch (Exception e) {
			String info = "Transaction getAvailableSpace() passed values inventory='" + inventory.getName() + "', data='" + data + "'";
			hc.gDB().writeError(e, info);
			int availablespace = 0;
			return availablespace;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void add(int amount, Inventory inventory) {
		try {
			ItemStack stack = getItemStack();
			int maxstack = stack.getMaxStackSize();
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				int pamount = 0;
				ItemStack citem = inventory.getItem(slot);
				HyperItemStack his = new HyperItemStack(citem);
				if (citem != null && citem.getType() == materialEnum && data == his.getDamageValue()) {
					int currentamount = citem.getAmount();
					if ((maxstack - currentamount) >= amount) {
						pamount = amount;
						citem.setAmount(pamount + currentamount);
					} else {
						pamount = maxstack - currentamount;
						citem.setAmount(maxstack);
					}
				} else if (inventory.getItem(slot) == null) {
					if (amount > maxstack) {
						pamount = maxstack;
					} else {
						pamount = amount;
					}
					stack.setAmount(pamount);
					inventory.setItem(slot, stack);
				}
				amount -= pamount;
				if (amount <= 0) {
					break;
				}
			}
			if (amount != 0) {
				String info = "Error adding items to inventory; + '" + amount + "' remaining. Transaction addBoughtItems() passed values inventory='" + inventory.getName() + "', data='" + data + "', amount='" + amount + "'";
				hc.gDB().writeError(info);
			}
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				p.updateInventory();
			}
		} catch (Exception e) {
			String info = "Transaction addItems() passed values inventory='" + inventory.getName() + "', data='" + data + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
		}
	}
	
	public double remove(int amount, Inventory inventory) {
		try {
			int remainingAmount = 0;
			double amountRemoved = 0;
			remainingAmount = amount;
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				ItemStack hstack = p.getItemInHand();
				HyperItemStack his = new HyperItemStack(hstack);
				if (hstack != null && !his.hasenchants()) {
					if (hstack.getType() == materialEnum && his.getDamageValue() == data) {
						if (remainingAmount >= hstack.getAmount()) {
							remainingAmount -= hstack.getAmount();
							amountRemoved += hstack.getAmount() * his.getDurabilityMultiplier();
							inventory.clear(p.getInventory().getHeldItemSlot());
						} else {
							amountRemoved += remainingAmount * his.getDurabilityMultiplier();
							hstack.setAmount(hstack.getAmount() - remainingAmount);
							return amountRemoved;
						}
					}
				}
			}
			for (int i = 0; i < inventory.getSize(); i++) {
				ItemStack stack = inventory.getItem(i);
				HyperItemStack his = new HyperItemStack(stack);
				if (stack != null && !his.hasenchants()) {
					if (stack.getType() == materialEnum && his.getDamageValue() == data) {
						if (remainingAmount >= stack.getAmount()) {
							remainingAmount -= stack.getAmount();
							amountRemoved += stack.getAmount() * his.getDurabilityMultiplier();
							inventory.clear(i);
						} else {
							amountRemoved += remainingAmount * his.getDurabilityMultiplier();
							stack.setAmount(stack.getAmount() - remainingAmount);
							return amountRemoved;
						}
					}
				}
			}
			if (remainingAmount != 0) {
				hc.gDB().writeError("removesoldItems() failure.  Items not successfully removed.  Passed data = '" + data + "', amount = '" + amount + "'");
				return amountRemoved;	
			} else {
				return amountRemoved;
			}
		} catch (Exception e) {
			String info = "Transaction removeSoldItems() passed values inventory='" + inventory.getName() + "', data='" + data + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			return -1;
		}
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItemStack(int amount) {
		MaterialData md = new MaterialData(materialEnum);
		md.setData((byte) data);
		ItemStack stack = md.toItemStack();
		if (materialEnum == Material.POTION && data != 0) {
			Potion pot = Potion.fromDamage(data);
			stack = pot.toItemStack(amount);
		}
		stack.setAmount(amount);
		return stack;
	}
	
	public ItemStack getItemStack() {
		return getItemStack(1);
	}
	 
	
}
