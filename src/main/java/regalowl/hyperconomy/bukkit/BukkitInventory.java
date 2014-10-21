package regalowl.hyperconomy.bukkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import regalowl.hyperconomy.serializable.SerializableBookMeta;
import regalowl.hyperconomy.serializable.SerializableColor;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableEnchantmentStorageMeta;
import regalowl.hyperconomy.serializable.SerializableFireworkEffect;
import regalowl.hyperconomy.serializable.SerializableFireworkEffectMeta;
import regalowl.hyperconomy.serializable.SerializableFireworkMeta;
import regalowl.hyperconomy.serializable.SerializableItemMeta;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.serializable.SerializableLeatherArmorMeta;
import regalowl.hyperconomy.serializable.SerializableMapMeta;
import regalowl.hyperconomy.serializable.SerializablePotionEffect;
import regalowl.hyperconomy.serializable.SerializablePotionMeta;
import regalowl.hyperconomy.serializable.SerializableSkullMeta;

public class BukkitInventory {
	@SuppressWarnings("deprecation")
	public SerializableItemStack getSerializableItemStack(ItemStack s) {
		if (s == null) return new SerializableItemStack();
		boolean isBlank = (s.getType() == Material.AIR) ? true:false;
        String material = s.getType().toString();
        short durability = s.getDurability();
        byte data = s.getData().getData(); 
        int amount = s.getAmount();
        int maxStackSize = s.getType().getMaxStackSize();
        int maxDurability = s.getType().getMaxDurability();
        SerializableItemStack sis = null;
        if (s.hasItemMeta()) {
        	ItemMeta im = s.getItemMeta();
            String displayName = im.getDisplayName();
            List<String> lore = im.getLore();
            List<SerializableEnchantment> enchantments = new ArrayList<SerializableEnchantment>();
            Map<Enchantment, Integer> enchants = im.getEnchants();
    		Iterator<Enchantment> it = enchants.keySet().iterator();
    		while (it.hasNext()) {
    			Enchantment e = it.next();
    			int lvl = enchants.get(e);
    			enchantments.add(new SerializableEnchantment(e.getName(), lvl));
    		}
    		SerializableItemMeta itemMeta = null;
        	if (im instanceof EnchantmentStorageMeta) {
        		EnchantmentStorageMeta sItemMeta = (EnchantmentStorageMeta)im;
        		List<SerializableEnchantment> storedEnchantments = new ArrayList<SerializableEnchantment>();
    			Map<Enchantment, Integer> stored = sItemMeta.getStoredEnchants();
    			Iterator<Enchantment> iter = stored.keySet().iterator();
    			while (iter.hasNext()) {
    				Enchantment e = iter.next();
    				int lvl = enchants.get(e);
    				storedEnchantments.add(new SerializableEnchantment(e.getName(), lvl));
    			}
        		itemMeta = new SerializableEnchantmentStorageMeta(displayName, lore, enchantments, storedEnchantments);
        	} else if (im instanceof BookMeta) {
        		BookMeta sItemMeta = (BookMeta)im;
        		itemMeta = new SerializableBookMeta(displayName, lore, enchantments, sItemMeta.getAuthor(), sItemMeta.getPages(), sItemMeta.getTitle());
        	} else if (im instanceof FireworkEffectMeta) {
        		FireworkEffectMeta sItemMeta = (FireworkEffectMeta)im;
        		FireworkEffect fe = sItemMeta.getEffect();
        		ArrayList<SerializableColor> colors = new ArrayList<SerializableColor>();
        		for (Color color:fe.getColors()) {
        			colors.add(new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
        		}
        		ArrayList<SerializableColor> fadeColors = new ArrayList<SerializableColor>();
        		for (Color color:fe.getFadeColors()) {
        			fadeColors.add(new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
        		}
        		SerializableFireworkEffect sfe = new SerializableFireworkEffect(colors, fadeColors, fe.getType().toString(), fe.hasFlicker(), fe.hasTrail());
        		itemMeta = new SerializableFireworkEffectMeta(displayName, lore, enchantments, sfe);
        	} else if (im instanceof FireworkMeta) {
        		FireworkMeta sItemMeta = (FireworkMeta)im;
        		ArrayList<SerializableFireworkEffect> fireworkEffects = new ArrayList<SerializableFireworkEffect>();
    			for (FireworkEffect fe:sItemMeta.getEffects()) {
	        		ArrayList<SerializableColor> colors = new ArrayList<SerializableColor>();
	        		for (Color color:fe.getColors()) {
	        			colors.add(new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
	        		}
	        		ArrayList<SerializableColor> fadeColors = new ArrayList<SerializableColor>();
	        		for (Color color:fe.getFadeColors()) {
	        			fadeColors.add(new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
	        		}
	        		fireworkEffects.add(new SerializableFireworkEffect(colors, fadeColors, fe.getType().toString(), fe.hasFlicker(), fe.hasTrail()));
    			}
        		itemMeta = new SerializableFireworkMeta(displayName, lore, enchantments, fireworkEffects, sItemMeta.getPower());
        	} else if (im instanceof LeatherArmorMeta) {
        		LeatherArmorMeta sItemMeta = (LeatherArmorMeta)im;
        		Color color = sItemMeta.getColor();
        		itemMeta = new SerializableLeatherArmorMeta(displayName, lore, enchantments, new SerializableColor(color.getRed(), color.getGreen(), color.getBlue()));
        	} else if (im instanceof PotionMeta) {
        		PotionMeta sItemMeta = (PotionMeta)im;
        		ArrayList<SerializablePotionEffect> potionEffects = new ArrayList<SerializablePotionEffect>();
        		for (PotionEffect pe:sItemMeta.getCustomEffects()) {
        			potionEffects.add(new SerializablePotionEffect(pe.getType().toString(), pe.getAmplifier(), pe.getDuration(), pe.isAmbient()));
        		}
        		itemMeta = new SerializablePotionMeta(displayName, lore, enchantments, potionEffects);
        	} else if (im instanceof SkullMeta) {
        		SkullMeta sItemMeta = (SkullMeta)im;
        		itemMeta = new SerializableSkullMeta(displayName, lore, enchantments, sItemMeta.getOwner());
        	} else if (im instanceof MapMeta) {
        		MapMeta sItemMeta = (MapMeta)im;
        		itemMeta = new SerializableMapMeta(displayName, lore, enchantments, sItemMeta.isScaling());
        	} else {
        		itemMeta = new SerializableItemMeta(displayName, lore, enchantments);
        	}
        	sis = new SerializableItemStack(itemMeta, material, durability, data, amount, maxStackSize, maxDurability);
        }
        sis = new SerializableItemStack(null, material, durability, data, amount, maxStackSize, maxDurability);
        if (isBlank) sis.setBlank();
        return sis;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItemStack(SerializableItemStack sis) {
		if (sis == null || sis.isBlank()) return null;
        ItemStack item = new ItemStack(Material.matchMaterial(sis.getMaterial()));
        item.setAmount(1);
        item.setDurability(sis.getDurability());
        item.getData().setData(sis.getData());
        if (sis.getItemMeta() != null) {
        	SerializableItemMeta sim = sis.getItemMeta();
        	ItemMeta itemMeta = item.getItemMeta();
        	itemMeta.setDisplayName(sim.getDisplayName());
        	itemMeta.setLore(sim.getLore());
    		for (SerializableEnchantment se:sim.getEnchantments()) {
    			itemMeta.addEnchant(Enchantment.getByName(se.getEnchantmentName()), se.getLvl(), true);
    		}
        	if (sim instanceof SerializableEnchantmentStorageMeta) {
        		SerializableEnchantmentStorageMeta sItemMeta = (SerializableEnchantmentStorageMeta)sim;
        		EnchantmentStorageMeta esm = (EnchantmentStorageMeta)itemMeta;
        		for (SerializableEnchantment se:sItemMeta.getEnchantments()) {
        			esm.addStoredEnchant(Enchantment.getByName(se.getEnchantmentName()), se.getLvl(), true);
        		}
        	} else if (sim instanceof SerializableBookMeta) {
        		SerializableBookMeta sItemMeta = (SerializableBookMeta)sim;
        		BookMeta bm = (BookMeta)itemMeta;
        		bm.setPages(sItemMeta.getPages());
        		bm.setAuthor(sItemMeta.getAuthor());
        		bm.setTitle(sItemMeta.getTitle());
        	} else if (sim instanceof SerializableFireworkEffectMeta) {
        		SerializableFireworkEffectMeta sItemMeta = (SerializableFireworkEffectMeta)sim;
        		FireworkEffectMeta fem = (FireworkEffectMeta)itemMeta;
        		SerializableFireworkEffect sfe = sItemMeta.getEffect();
    			Builder fb = FireworkEffect.builder();
    			for (SerializableColor c:sfe.getColors()) {
    				fb.withColor(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
    			}
    			for (SerializableColor c:sfe.getFadeColors()) {
    				fb.withFade(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
    			}
    			fb.with(FireworkEffect.Type.valueOf(sfe.getType()));
    			fb.flicker(sfe.hasFlicker());
    			fb.trail(sfe.hasTrail());
    			fem.setEffect(fb.build());
        	} else if (sim instanceof SerializableFireworkMeta) {
        		SerializableFireworkMeta sItemMeta = (SerializableFireworkMeta)sim;
        		FireworkMeta fm = (FireworkMeta)itemMeta;
        		for (SerializableFireworkEffect sfe:sItemMeta.getEffects()) {
        			Builder fb = FireworkEffect.builder();
        			for (SerializableColor c:sfe.getColors()) {
        				fb.withColor(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
        			}
        			for (SerializableColor c:sfe.getFadeColors()) {
        				fb.withFade(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
        			}
        			fb.with(FireworkEffect.Type.valueOf(sfe.getType()));
        			fb.flicker(sfe.hasFlicker());
        			fb.trail(sfe.hasTrail());
        			fm.addEffect(fb.build());
        		}
        		fm.setPower(sItemMeta.getPower());
        	} else if (sim instanceof SerializableLeatherArmorMeta) {
        		SerializableLeatherArmorMeta sItemMeta = (SerializableLeatherArmorMeta)sim;
        		LeatherArmorMeta lam = (LeatherArmorMeta)itemMeta;
        		SerializableColor sc = sItemMeta.getColor();
        		lam.setColor(Color.fromRGB(sc.getRed(), sc.getGreen(), sc.getBlue()));
        	} else if (sim instanceof SerializablePotionMeta) {
        		SerializablePotionMeta sItemMeta = (SerializablePotionMeta)sim;
        		PotionMeta pm = (PotionMeta)itemMeta;
        		for (SerializablePotionEffect spe:sItemMeta.getPotionEffects()) {
        			PotionEffect pe = new PotionEffect(PotionEffectType.getByName(spe.getType()), spe.getDuration(), spe.getAmplifier(), spe.isAmbient());
        			pm.addCustomEffect(pe, true);
        		}
        	} else if (sim instanceof SerializableSkullMeta) {
        		SerializableSkullMeta sItemMeta = (SerializableSkullMeta)sim;
        		SkullMeta sm = (SkullMeta)itemMeta;
        		sm.setOwner(sItemMeta.getOwner());
        	} else if (sim instanceof SerializableMapMeta) {
        		SerializableMapMeta sItemMeta = (SerializableMapMeta)sim;
        		MapMeta mm = (MapMeta)itemMeta;
        		mm.setScaling(sItemMeta.isScaling());
        	}
        	item.setItemMeta(itemMeta);
        }
        return item;
	}
}
