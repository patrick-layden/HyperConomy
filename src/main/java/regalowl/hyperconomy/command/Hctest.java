package regalowl.hyperconomy.command;

import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.bukkit.BukkitCommon;
import regalowl.hyperconomy.bukkit.BukkitConnector;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;



public class Hctest extends BaseCommand implements HyperCommand {
	
	public Hctest(HyperConomy hc) {
		super(hc, true);
	}

	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
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
		return data;
	}

}
