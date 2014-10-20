package regalowl.hyperconomy.command;

import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;












public class Hctest extends BaseCommand implements HyperCommand {
	
	public Hctest() {
		super(true);
	}

	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		for (HyperObject ho:dm.getHyperObjects()) {
			SerializableItemStack sis = ho.getItem();
			SerializableInventory si = hp.getInventory();
			int slot = si.getHeldSlot();
			hp.getInventory().setItem(slot, sis);
			si.updateInventory();
			si = hp.getInventory();
			SerializableItemStack newSIS = si.getItem(slot);
			ho.setItemStack(newSIS);
		}
		return data;
	}
}
