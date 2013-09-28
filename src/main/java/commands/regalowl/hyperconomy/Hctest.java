package regalowl.hyperconomy;


import org.bukkit.command.CommandSender;


public class Hctest {
	Hctest(String args[], CommandSender sender) {
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
	}
}
