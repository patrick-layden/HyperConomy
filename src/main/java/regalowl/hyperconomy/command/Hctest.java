package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HyperConomy;




public class Hctest extends BaseCommand implements HyperCommand {
	
	public Hctest(HyperConomy hc) {
		super(hc, false);
	}

	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		
		/*
		FileConfiguration cat = hc.getYamlHandler().getFileConfiguration("categories");
		if (cat != null) {
			for (String key:cat.getTopLevelKeys()) {
				ArrayList<String> names = CommonFunctions.explode(cat.getString(key), ",");
				for (String name:names) {
					for (HyperEconomy he:hc.getDataManager().getEconomies()) {
						TradeObject to = he.getTradeObject(name);
						if (to == null) continue;
						to.addCategory(key);
					}
				}
			}
		}
		*/
		/*
		BukkitConnector bc = (BukkitConnector)hc.getMC();
		for (TradeObject ho:hc.getDataManager().getTradeObjects()) {
			if (ho.getType() == TradeObjectType.ITEM) {
				SerializableItemStack sis = new SerializableItemStack(ho.getData());
				ItemStack stack = sis.getItem();
				if (stack == null) continue;
				HItemStack his = bc.getBukkitCommon().getSerializableItemStack(stack);
				ho.setData(his.serialize());
			} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
				SerializableEnchantment sis = new SerializableEnchantment(ho.getData());
				HEnchantment he = new HEnchantment(sis.getEnchantmentName(), sis.getLvl());
				ho.setData(he.serialize());
			}
		}
		*/
		
		return data;
	}

}
