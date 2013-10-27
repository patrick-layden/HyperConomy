package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;


public class Hctest {
	
	
	/*
	
	Hctest() {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy em = hc.getEconomyManager().getEconomy("default");
		FileConfiguration composites = hc.gYH().gFC("composites");
		ArrayList<String> names = em.getItemNames();
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			composites.set(name, null);
			HyperObject ho = em.getHyperObject(name);
			String newtype = HyperObjectType.getString(ho.getType());
			String newcategory = ho.getCategory();
			String newmaterial = ho.getMaterial();
			int newid = ho.getId();
			int newdata = ho.getData();
			composites.set(name + ".information.type", newtype);
			composites.set(name + ".information.category", newcategory);
			composites.set(name + ".information.material", newmaterial);
			composites.set(name + ".information.id", newid);
			composites.set(name + ".information.data", newdata);
			String componentString = "";
			if (name.contains("potion")) {
				boolean modified = false;
				if (name.contains("spotion")) {
					componentString += "gunpowder,1;";
					modified = true;
				}
				if (name.contains("2")) {
					componentString += "glowstonedust,1;";
					modified = true;
				}
				if (name.contains("ext")) {
					componentString += "redstone,1;";
					modified = true;
				}
				if (modified) {
					String basePotion = name.replace("spotion", "potion").replace("2", "").replace("ext", "");
					componentString += basePotion+",1;";
				}
			} else {
				componentString = ";";
			}
			composites.set(name + ".components", componentString);
		}
		hc.gYH().saveYamls();
	}
	
	
	
	
	
	Hctest(String args[], CommandSender sender) {
		/*
		HyperConomy hc = HyperConomy.hc;
		for (HyperObject ho:hc.getEconomyManager().getHyperObjects()) {
			ho.setCategory(ho.getCategory());
			ho.setCeiling(ho.getCeiling());
			ho.setData(ho.getData());
			ho.setDurability(ho.getDurability());
			ho.setEconomy(ho.getEconomy());
			ho.setFloor(ho.getFloor());
			ho.setId(ho.getId());
			ho.setInitiation(ho.getInitiation());
			ho.setIsstatic(ho.getIsstatic());
			ho.setMaterial(ho.getMaterial());
			ho.setMaxstock(ho.getMaxstock());
			ho.setMedian(ho.getMedian());
			ho.setName(ho.getName());
			ho.setStartprice(ho.getStartprice());
			ho.setStaticprice(ho.getStaticprice());
			ho.setStock(ho.getStock());
			ho.setType(ho.getType().toString());
			ho.setValue(ho.getValue());
		}
		*/
		
		
		
		
		
		
		/*
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy em = hc.getEconomyManager().getEconomy("default");
		FileConfiguration composites = hc.gYH().gFC("composites");
		ArrayList<String> names = em.getItemNames();
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			composites.set(name, null);
			HyperObject ho = em.getHyperObject(name);
			String newtype = HyperObjectType.getString(ho.getType());
			String newcategory = ho.getCategory();
			String newmaterial = ho.getMaterial();
			int newid = ho.getId();
			int newdata = ho.getData();
			composites.set(name + ".information.type", newtype);
			composites.set(name + ".information.category", newcategory);
			composites.set(name + ".information.material", newmaterial);
			composites.set(name + ".information.id", newid);
			composites.set(name + ".information.data", newdata);
			String componentString = "";
			if (name.contains("potion")) {
				boolean modified = false;
				if (name.contains("spotion")) {
					componentString += "gunpowder,1;";
					modified = true;
				}
				if (name.contains("2")) {
					componentString += "glowstonedust,1;";
					modified = true;
				}
				if (name.contains("ext")) {
					componentString += "redstone,1;";
					modified = true;
				}
				if (modified) {
					String basePotion = name.replace("spotion", "potion").replace("2", "").replace("ext", "");
					componentString += basePotion+",1;";
				}
			} else {
				componentString = ";";
			}
			composites.set(name + ".components", componentString);
		}
		hc.gYH().saveYamls();
		
		
		
		
	}
	*/
}
