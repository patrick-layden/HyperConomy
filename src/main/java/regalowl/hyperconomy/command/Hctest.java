package regalowl.hyperconomy.command;

import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.bukkit.BukkitCommon;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;



public class Hctest extends BaseCommand implements HyperCommand {
	
	public Hctest() {
		super(true);
	}

	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		for (TradeObject ho:HC.hc.getDataManager().getHyperObjects()) {
			if (ho.getType() == TradeObjectType.ITEM) {
				SerializableItemStack sis = new SerializableItemStack(ho.getData());
				ItemStack stack = sis.getItem();
				if (stack == null) continue;
				HItemStack his = BukkitCommon.getSerializableItemStack(stack);
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
