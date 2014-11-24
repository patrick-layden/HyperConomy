package regalowl.hyperconomy.command;



import java.util.ArrayList;
import java.util.HashMap;






import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.tradeobject.ComponentTradeItem;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class Additem extends BaseCommand implements HyperCommand {
	
	private ArrayList<String> usedNames = new ArrayList<String>();
	
	public Additem(HyperConomy hc) {
		super(hc, true);
	}

	
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			String displayName = "";
			if (args.length >= 1) {
				displayName = args[0];
				if (displayName.equalsIgnoreCase("all")) {
					addAll(hp);
					data.addResponse(L.get("INVENTORY_ADDED"));
					return data;
				}
				if (displayName.equalsIgnoreCase("help")) {
					data.addResponse(L.get("ADDITEM_INVALID"));
					return data;
				}
			}
			double value = 0;
			if (args.length >= 2) {
				value = Double.parseDouble(args[1]);
			}
			if (hp.getItemInHand().getMaterial().equalsIgnoreCase("AIR")) {
				data.addResponse(L.get("AIR_CANT_BE_TRADED"));
				return data;
			}
			HyperEconomy econ = super.getEconomy();
			TradeObject ho =  econ.getTradeObject(hp.getItemInHand());
			if (ho != null) {
				data.addResponse(L.get("ALREADY_IN_DATABASE"));
				return data;
			}
			HItemStack stack = hp.getItemInHand();
			TradeObject hobj = generateNewHyperObject(stack, econ.getName(), displayName, value);
			addItem(hobj, econ.getName());
			data.addResponse(L.get("ITEM_ADDED"));
			return data;
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
			return data;
		}
	}

	
	
	public boolean addItem(TradeObject hobj, String economy) {
		DataManager em = hc.getDataManager();
		if (hobj == null || economy == null) {return false;}
		HyperEconomy he = em.getEconomy(economy);
		if (he == null) {return false;}
		if (he.objectTest(hobj.getName())) {return false;}
		if (he.objectTest(hobj.getDisplayName())) {return false;}
		for (String alias:hobj.getAliases()) {
			if (he.objectTest(alias)) {return false;}
		}
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", hobj.getName());
		values.put("DISPLAY_NAME", hobj.getDisplayName());
		values.put("ALIASES", hobj.getAliasesString());
		values.put("ECONOMY", hobj.getEconomy());
		values.put("TYPE", hobj.getType().toString());
		values.put("VALUE", hobj.getValue()+"");
		values.put("STATIC", hobj.isStatic()+"");
		values.put("STATICPRICE", hobj.getStaticPrice()+"");
		values.put("STOCK", hobj.getStock()+"");
		values.put("MEDIAN", hobj.getMedian()+"");
		values.put("INITIATION", hobj.useInitialPricing()+"");
		values.put("STARTPRICE",hobj.getStartPrice()+"");
		values.put("CEILING", hobj.getCeiling()+"");
		values.put("FLOOR", hobj.getFloor()+"");
		values.put("MAXSTOCK", hobj.getMaxStock()+"");
		values.put("DATA", hobj.getData());
		hc.getSQLWrite().performInsert("hyperconomy_objects", values);
		he.addObject(hobj);
		return true;
	}
	
	public TradeObject generateNewHyperObject(HItemStack stack, String economy) {
		return generateNewHyperObject(stack, economy, "", 0);
	}
	
	public TradeObject generateNewHyperObject(HItemStack sis, String economy, String displayName, double value) {
		if (sis == null || economy == null || displayName == null) {return null;}
		DataManager em = hc.getDataManager();
		if (sis.isBlank()) {return null;}
		HyperEconomy econ = em.getEconomy(economy);
		if (econ == null) {return null;}
		TradeObject ho =  econ.getTradeObject(sis);
		if (ho != null) {return null;}
		String name = sis.getMaterial() + "_" + sis.getDurability();
		if (econ.objectTest(name) || name.equalsIgnoreCase("")) {
			name = generateName(sis);
		}
		if (econ.objectTest(displayName) || displayName.equalsIgnoreCase("")) {
			displayName = name;
		}
		String aliases = displayName.replace("_", "");
		if (econ.objectTest(aliases)) {
			aliases = "";
		} else {
			aliases += ",";
		}
		if (value <= 0) {
			value = 10.0;
		}
		int median = 0;
		if (value >= 100000) {
			median = 10;
		} else if (value >= 10000) {
			median = 100;
		} else if (value >= 1000) {
			median = 500;
		} else if (value >= 100) {
			median = 1000;
		} else if (value >= 50) {
			median = 5000;
		} else if (value >= 1) {
			median = 10000;
		} else {
			median = 25000;
		}
		TradeObject hobj = new ComponentTradeItem(hc, name, economy, displayName, aliases, "", "item", value, "false", value*2,
				0, median, "true", value*2, 1000000000,0, 1000000000, sis.serialize());
		return hobj;
	}
	
	
	
	private String generateName(HItemStack stack) {
		String name = generateGenericName();
		usedNames.add(name);
		return name;
	}
	
	private String generateGenericName() {
		String name = "object1";
		int counter = 1;
		while (nameInUse(name)) {
			name = "object" + counter;
			counter++;
		}
		return name;
	}
	
	private boolean nameInUse(String name) {
		if (hc.getDataManager().getDefaultEconomy().objectTest(name)) {
			return true;
		}
		for (String cName:usedNames) {
			if (cName.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
		
	}
	
	private void addAll(HyperPlayer p) {
		HInventory inventory = p.getInventory();
		String economy = hp.getEconomy();
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			HItemStack stack = inventory.getItem(slot);
			TradeObject hobj = generateNewHyperObject(stack, economy);
			addItem(hobj, economy);
		}
	}
}
