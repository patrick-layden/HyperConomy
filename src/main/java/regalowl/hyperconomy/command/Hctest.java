package regalowl.hyperconomy.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.bukkit.BukkitCommon;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;












public class Hctest extends BaseCommand implements HyperCommand {
	
	public Hctest() {
		super(true);
	}

	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperPlayer hp = data.getHyperPlayer();
		if (args.length == 1) {
			TradeObject ho = dm.getDefaultEconomy().getHyperObject(args[0]);
			updateTradeObject(data, ho);
		} else {
			for (TradeObject ho:dm.getHyperObjects()) {
				updateTradeObject(data, ho);
			}
		}
		

		return data;
	}
	
	
	private void updateTradeObject(CommandData data, TradeObject ho) {
		if (ho.getType() == TradeObjectType.ITEM) {
			String material = ho.getName().substring(0, ho.getName().lastIndexOf("_"));
			int d = Integer.parseInt(ho.getName().substring(ho.getName().lastIndexOf("_") + 1, ho.getName().length()));
			ItemStack ns = null;
			try {
				ns = new ItemStack(Material.matchMaterial(material));
			} catch (Exception e) {
				data.addResponse("itemstack failed " + material + " " + d);
				return;
			}
			ns.getData().setData((byte) d);
			ns.setDurability((short) d);
			HInventory si = hp.getInventory();
			int slot = si.getHeldSlot();
			si.setItem(slot, BukkitCommon.getSerializableItemStack(ns));
			si.updateInventory();
			si = hp.getInventory();
			HItemStack newSIS = si.getItem(slot);
			
			
			ItemStack stack = BukkitCommon.getItemStack(newSIS);
			if (!BukkitCommon.getSerializableItemStack(stack).equals(newSIS)) {
				data.addResponse("1 stacks not equal " + material + " " + d);
			}
			HItemStack s2 = new HItemStack(newSIS.serialize());
			if (!s2.equals(newSIS)) {
				data.addResponse("2 stacks not equal " + material + " " + d);
			}
			
			ho.setItemStack(newSIS);
			Bukkit.broadcastMessage("Changed: " + ho.getDisplayName());
		} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
			String material = ho.getName().substring(0, ho.getName().lastIndexOf("_"));
			int d = Integer.parseInt(ho.getName().substring(ho.getName().lastIndexOf("_") + 1, ho.getName().length()));
			HEnchantment e = new HEnchantment(material, d);
			ho.setData(e.serialize());
			Bukkit.broadcastMessage("Changed: " + ho.getDisplayName());
		}
	}
}
