package regalowl.hyperconomy.command;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.serializable.SerializableItemStack;





public class Hctest implements CommandExecutor {
	/*
	private HyperConomy hc;
	private EconomyManager em;
	private HyperEconomy de;
	private Player player;
	private Inventory inv;
	private FileConfiguration config;
	private HyperPlayer hp;
*/
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		HyperConomy hc = HyperConomy.hc;
		ItemStack stack = new ItemStack(Material.ICE);
		stack.setAmount(11);
		SerializableItemStack sis = new SerializableItemStack(stack);
		String serialData;
		serialData = sis.serialize();

		Bukkit.broadcastMessage(serialData);

		
		
		SerializableItemStack sis2 = new SerializableItemStack(serialData);
		ItemStack stack2 = sis2.getItem();
		Bukkit.broadcastMessage(stack2.getType().toString());
		Bukkit.broadcastMessage(stack2.getAmount()+"");
		
		return true;
	}
	
	


}
