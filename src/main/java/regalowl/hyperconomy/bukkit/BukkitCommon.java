package regalowl.hyperconomy.bukkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.display.SignType;
import regalowl.hyperconomy.inventory.HBannerMeta;
import regalowl.hyperconomy.inventory.HBookMeta;
import regalowl.hyperconomy.inventory.HColor;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HEnchantmentStorageMeta;
import regalowl.hyperconomy.inventory.HFireworkEffect;
import regalowl.hyperconomy.inventory.HFireworkEffectMeta;
import regalowl.hyperconomy.inventory.HFireworkMeta;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HInventoryType;
import regalowl.hyperconomy.inventory.HItemFlag;
import regalowl.hyperconomy.inventory.HItemMeta;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.inventory.HLeatherArmorMeta;
import regalowl.hyperconomy.inventory.HMapMeta;
import regalowl.hyperconomy.inventory.HPattern;
import regalowl.hyperconomy.inventory.HPotionData;
import regalowl.hyperconomy.inventory.HPotionEffect;
import regalowl.hyperconomy.inventory.HPotionMeta;
import regalowl.hyperconomy.inventory.HSkullMeta;
import regalowl.hyperconomy.inventory.HSpawnEggMeta;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;

public class BukkitCommon {

	protected static final BlockFace[] planeFaces = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
	protected static final BlockFace[] allFaces = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.DOWN, BlockFace.UP};
	private HyperConomy hc;
	
	protected BukkitCommon(HyperConomy hc) {
		this.hc = hc;
	}
	
	
	
	protected Location getLocation(HLocation l) {
		if (l == null) return null;
		return new Location(Bukkit.getWorld(l.getWorld()), l.getX(), l.getY(), l.getZ());
	}

	protected HLocation getLocation(Location l) {
		if (l == null) return null;
		return new HLocation(l.getWorld().getName(), l.getX(), l.getY(), l.getZ());
	}
	
	protected Block getBlock(HLocation location) {
		if (location == null) return null;
		Location l = getLocation(location);
		return l.getBlock();
	}
	
	protected HBlock getBlock(Block b) {
		if (b == null) return null;
		HLocation l = getLocation(b.getLocation());
		return new HBlock(hc, l);
	}
	
	protected Sign getSign(HLocation l) {
		if (l == null) return null;
		BlockState bs = getBlock(l).getState();
		if (bs instanceof Sign) {
			return (Sign)bs;
		}
		return null;
	}
	

	protected boolean isTransactionSign(HLocation l) {
		Block b = getBlock(l);
		if (b != null && (b.getType().equals(Material.SIGN) || b.getType().equals(Material.WALL_SIGN))) {
			Sign s = (Sign) b.getState();
			String line3 = ChatColor.stripColor(s.getLine(2)).trim();
			if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
				return true;
			}
		}
		return false;
	}

	protected boolean isInfoSign(HLocation l) {
		Block b = getBlock(l);
		if (b != null && (b.getType().equals(Material.SIGN) || b.getType().equals(Material.WALL_SIGN))) {
			Sign s = (Sign) b.getState();
			String type = ChatColor.stripColor(s.getLine(2)).trim().replace(":", "").replace(" ", "");
			if (SignType.isSignType(type)) return true;
		}
		return false;
	}
	
	protected boolean isChestShopChest(HLocation l) {
		Block b = getBlock(l);
		if (b == null) return false;
		if (!(b.getState() instanceof Chest)) return false;
		Chest chest = (Chest) b.getState();
		HLocation sl = getLocation(chest.getLocation());
		sl.setY(sl.getY() + 1);
		if (!isChestShopSign(sl)) return false;
		return true;
	}
	
	protected boolean isChestShopSign(HLocation l) {
		Block b = getBlock(l);
		if (b == null) return false;
		if (!(b.getState() instanceof Sign)) return false;
		Sign s = (Sign) b.getState();
		String line1 = ChatColor.stripColor(s.getLine(0)).trim();
		if (!line1.equalsIgnoreCase("ChestShop")) return false;
		HLocation sl = new HLocation(l);
		sl.setY(sl.getY() - 1);
		Block cb = getBlock(sl);
		if (cb == null) return false;
		if (!(cb.getState() instanceof Chest)) return false;
		return true;
	}
	
	protected boolean isChestShopSignBlock(HLocation l) {
		HSign sign = getAttachedSign(l);
		if (sign == null) return false;
		if (!isChestShopSign(sign.getLocation())) return false;
		return true;
	}
	
	protected HSign getAttachedSign(HLocation l) {
		Block b = getBlock(l);
		if (b == null) return null;
		for (BlockFace cface : planeFaces) {
			Block block = b.getRelative(cface);
			if (block.getType().equals(Material.WALL_SIGN)) {
				org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getState().getData();
				BlockFace attachedface = sign.getFacing();
				if (block.getRelative(attachedface.getOppositeFace()).equals(b)) {
					Sign s = (Sign) block.getState();
					ArrayList<String> lines = new ArrayList<String>();
					for (String li:s.getLines()) {
						lines.add(li);
					}
					HLocation sl = getLocation(s.getLocation());
					return new HSign(hc, sl, lines, true);
				}
			}
		}
		return null;
	}
	
	protected boolean isPartOfChestShop(HLocation l) {
		if (isChestShopChest(l)) return true;
		if (isChestShopSign(l)) return true;
		if (isChestShopSignBlock(l)) return true;
		return false;
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected HyperPlayer getPlayer(Player p) {
		if (p == null) return null;
		return hc.getHyperPlayerManager().getHyperPlayer(p.getName());
	}

	protected Player getPlayer(HyperPlayer hp) {
		if (hp.getName() == null) return null;
		return Bukkit.getPlayer(hp.getName());
	}
	
	
	
	
	protected HInventory getInventory(HyperPlayer hp) {
		if (hp == null) return null;
		Player p = Bukkit.getPlayer(hp.getName());
		return getInventory(p.getInventory());
	}

	protected HInventory getInventory(Inventory i) {
		if (i == null) return null;
		ArrayList<HItemStack> items = new ArrayList<HItemStack>();
		HInventoryType type = null;
		if (i.getType() == InventoryType.PLAYER) {
			type = HInventoryType.PLAYER;
		} else if (i.getType() == InventoryType.CHEST) {
			type = HInventoryType.CHEST;
		}
		int size = i.getStorageContents().length;
		for (int c = 0; c < size; c++) {
	        items.add(getSerializableItemStack(i.getItem(c)));
		}
		HInventory si = new HInventory(hc, items, type);
		if (i.getType() == InventoryType.PLAYER) {
			if (i.getHolder() instanceof Player) {
				Player p = (Player)i.getHolder();
				si.setOwner(p.getName());
				si.setHeldSlot(p.getInventory().getHeldItemSlot());
			}
		} else if (i.getType() == InventoryType.CHEST) {
			if (i.getHolder() instanceof Chest) {
				Chest c = (Chest)i.getHolder();
				si.setLocation(getLocation(c.getLocation()));
			}
		}
		return si;
	}
	

	protected HInventory getChestInventory(HLocation l) {
		if (l == null) return null;
		Location loc = getLocation(l);
		if (loc.getBlock().getState() instanceof Chest) {
			Chest chest = (Chest)loc.getBlock().getState();
			Inventory i = chest.getInventory();
			ArrayList<HItemStack> items = new ArrayList<HItemStack>();
			int size = i.getSize();
			for (int c = 0; c < size; c++) {
		        items.add(getSerializableItemStack(i.getItem(c)));
			}
			HInventory si = new HInventory(hc, items, HInventoryType.CHEST);
			si.setLocation(l);
			return si;
		}
		return null;
	}

	protected void setInventory(HInventory inventory) {
		if (inventory == null) return;
		ArrayList<HItemStack> newInventory = inventory.getItems();
		ArrayList<HItemStack> currentInventory = null;
		Inventory bukkitInv = null;
		Player p = null;
		if (inventory.getInventoryType() == HInventoryType.PLAYER) {
			HyperPlayer hp = inventory.getHyperPlayer();
			p = Bukkit.getPlayer(hp.getName());
			p.getInventory().setHeldItemSlot(inventory.getHeldSlot());
			currentInventory = getInventory(hp).getItems();
			bukkitInv = p.getInventory();
		} else if (inventory.getInventoryType() == HInventoryType.CHEST) {
			Location loc = getLocation(inventory.getLocation());
			if (!(loc.getBlock().getState() instanceof Chest)) return;
			Chest chest = (Chest)loc.getBlock().getState();
			currentInventory = getChestInventory(inventory.getLocation()).getItems();
			bukkitInv = chest.getInventory();
		}
		if (currentInventory.size() != newInventory.size()) return;
		for (int i = 0; i < newInventory.size(); i++) {
			if (newInventory.get(i).equals(currentInventory.get(i))) continue;
			ItemStack is = getItemStack(newInventory.get(i));
			if (is == null) {
				bukkitInv.clear(i);
			} else {
				bukkitInv.setItem(i, is);
			}
		}
		if (inventory.getInventoryType() == HInventoryType.PLAYER) p.updateInventory();
	}
	
	
	protected void setItem(HyperPlayer hp, HItemStack item, int slot) {
		Player p = Bukkit.getPlayer(hp.getName());
		if (p == null) return;
		Inventory bukkitInv = p.getInventory();
		if (item.isBlank()) {
			bukkitInv.clear(slot);
		} else {
			bukkitInv.setItem(slot, getItemStack(item));
		}
	}
	protected void setItem(HLocation location, HItemStack item, int slot) {
		Location loc = getLocation(location);
		if (!(loc.getBlock().getState() instanceof Chest)) return;
		Chest chest = (Chest)loc.getBlock().getState();
		Inventory bukkitInv = chest.getInventory();
		if (item.isBlank()) {
			bukkitInv.clear(slot);
		} else {
			bukkitInv.setItem(slot, getItemStack(item));
		}
	}
	
	
	protected void setItemQuantity(HLocation location, int amount, int slot) {
		Location loc = getLocation(location);
		if (!(loc.getBlock().getState() instanceof Chest)) return;
		Chest chest = (Chest)loc.getBlock().getState();
		Inventory bukkitInv = chest.getInventory();
		if (amount <= 0) {
			bukkitInv.clear(slot);
		} else {
			bukkitInv.getItem(slot).setAmount(amount);
		}
	}
	protected void setItemQuantity(HyperPlayer hp, int amount, int slot) {
		if (hp == null) return;
		Player p = Bukkit.getPlayer(hp.getName());
		Inventory bukkitInv = p.getInventory();
		if (amount <= 0) {
			bukkitInv.clear(slot);
		} else {
			bukkitInv.getItem(slot).setAmount(amount);
		}
	}	
	
	
	
	protected void setBukkitInventory(Inventory bukkitInventory, HInventory inventory) {
		if (inventory == null || bukkitInventory == null) return;
		if (bukkitInventory.getSize()!= inventory.getSize()) return;
		for (int i = 0; i < inventory.getSize(); i++) {
			if (inventory.getItem(i).isBlank()) continue;
			ItemStack is = getItemStack(inventory.getItem(i));
			if (is == null) {
				bukkitInventory.clear(i);
			} else {
				bukkitInventory.setItem(i, is);
			}
		}
		if (inventory.getInventoryType() == HInventoryType.PLAYER) {
			if (bukkitInventory.getHolder() instanceof Player) {
				Player p = (Player)bukkitInventory.getHolder();
				p.updateInventory();
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public HItemStack getSerializableItemStack(ItemStack s) {
		if (s == null) return hc.getBlankStack();
		boolean isBlank = (s.getType() == Material.AIR) ? true:false;
        String material = s.getType().toString();
        short durability = s.getDurability();
        byte data = s.getData().getData(); 
        int amount = s.getAmount();
        int maxStackSize = s.getType().getMaxStackSize();
        int maxDurability = s.getType().getMaxDurability();
        HItemStack sis = new HItemStack(hc, new HItemMeta("", new ArrayList<String>(), new ArrayList<HEnchantment>(), new ArrayList<HItemFlag>(), false, 0), material, durability, data, amount, maxStackSize, maxDurability);
        if (isBlank) sis.setBlank();
        if (s.hasItemMeta()) {
        	ItemMeta im = s.getItemMeta();
            String displayName = (im.getDisplayName() == null) ? "":im.getDisplayName();
            ArrayList<String> lore = new ArrayList<String>();
            if (im.getLore() != null) lore.addAll(im.getLore());
            ArrayList<HEnchantment> enchantments = new ArrayList<HEnchantment>();
            Map<Enchantment, Integer> enchants = im.getEnchants();
    		Iterator<Enchantment> it = enchants.keySet().iterator();
    		while (it.hasNext()) {
    			Enchantment e = it.next();
    			int lvl = enchants.get(e);
				enchantments.add(new HEnchantment(e.getKey().getKey(), lvl));
    		}
            ArrayList<HItemFlag> itemFlags = new ArrayList<HItemFlag>();
            Set<ItemFlag> flags = im.getItemFlags();
    		Iterator<ItemFlag> it2 = flags.iterator();
    		while (it2.hasNext()) {
    			ItemFlag f = it2.next();
    			itemFlags.add(new HItemFlag(f.toString()));
    		}
    		boolean unbreakable = im.isUnbreakable();
    		int repairCost = 0;
    		if (im instanceof Repairable) {
    			Repairable rp = (Repairable)im;
    			repairCost = rp.getRepairCost();
    		}
    		HItemMeta itemMeta = null;
        	if (im instanceof EnchantmentStorageMeta) {
        		EnchantmentStorageMeta sItemMeta = (EnchantmentStorageMeta)im;
        		ArrayList<HEnchantment> storedEnchantments = new ArrayList<HEnchantment>();
    			Map<Enchantment, Integer> stored = sItemMeta.getStoredEnchants();
    			Iterator<Enchantment> iter = stored.keySet().iterator();
    			while (iter.hasNext()) {
    				Enchantment e = iter.next();
    				int lvl = stored.get(e);
					storedEnchantments.add(new HEnchantment(e.getKey().getKey(), lvl));
    			}
        		itemMeta = new HEnchantmentStorageMeta(displayName, lore, itemFlags, unbreakable, repairCost, storedEnchantments);
        	} else if (im instanceof BookMeta) {
        		BookMeta sItemMeta = (BookMeta)im;
        		ArrayList<String> pages = new ArrayList<String>();
        		if (sItemMeta.getPages() != null) pages.addAll(sItemMeta.getPages());
        		itemMeta = new HBookMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost, sItemMeta.getAuthor(), pages, sItemMeta.getTitle());
        	} else if (im instanceof FireworkEffectMeta) {
        		FireworkEffectMeta sItemMeta = (FireworkEffectMeta)im;
        		FireworkEffect fe = sItemMeta.getEffect();
        		HFireworkEffect sfe = null;
        		if (fe != null) {
        			ArrayList<HColor> colors = new ArrayList<HColor>();
	        		for (Color color:fe.getColors()) {
	        			colors.add(new HColor(color.getRed(), color.getGreen(), color.getBlue()));
	        		}
	        		ArrayList<HColor> fadeColors = new ArrayList<HColor>();
	        		for (Color color:fe.getFadeColors()) {
	        			fadeColors.add(new HColor(color.getRed(), color.getGreen(), color.getBlue()));
	        		}
	        		sfe = new HFireworkEffect(colors, fadeColors, fe.getType().toString(), fe.hasFlicker(), fe.hasTrail());
        		}
        		itemMeta = new HFireworkEffectMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost, sfe);
        	} else if (im instanceof FireworkMeta) {
        		FireworkMeta sItemMeta = (FireworkMeta)im;
        		ArrayList<HFireworkEffect> fireworkEffects = new ArrayList<HFireworkEffect>();
        		if (sItemMeta.getEffects() != null) {
	    			for (FireworkEffect fe:sItemMeta.getEffects()) {
		        		ArrayList<HColor> colors = new ArrayList<HColor>();
		        		ArrayList<HColor> fadeColors = new ArrayList<HColor>();
		        		for (Color color:fe.getColors()) {
		        			colors.add(new HColor(color.getRed(), color.getGreen(), color.getBlue()));
		        		}
		        		for (Color color:fe.getFadeColors()) {
		        			fadeColors.add(new HColor(color.getRed(), color.getGreen(), color.getBlue()));
		        		}
		        		fireworkEffects.add(new HFireworkEffect(colors, fadeColors, fe.getType().toString(), fe.hasFlicker(), fe.hasTrail()));
	    			}
        		}
        		itemMeta = new HFireworkMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost, fireworkEffects, sItemMeta.getPower());
        	} else if (im instanceof LeatherArmorMeta) {
        		LeatherArmorMeta sItemMeta = (LeatherArmorMeta)im;
        		Color color = sItemMeta.getColor();
        		itemMeta = new HLeatherArmorMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost, new HColor(color.getRed(), color.getGreen(), color.getBlue()));
        	} else if (im instanceof PotionMeta) {
        		PotionMeta sItemMeta = (PotionMeta)im;
        		PotionData pd = sItemMeta.getBasePotionData();
        		HPotionData potionData = new HPotionData(pd.getType().toString(), pd.isExtended(), pd.isUpgraded());
        		ArrayList<HPotionEffect> potionEffects = new ArrayList<HPotionEffect>();
        		if (sItemMeta.hasCustomEffects()) {
	        		for (PotionEffect pe:sItemMeta.getCustomEffects()) {
	        			potionEffects.add(new HPotionEffect(pe.getType().getName(), pe.getAmplifier(), pe.getDuration(), pe.isAmbient()));
	        		}
        		}
        		itemMeta = new HPotionMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost, potionEffects, potionData);
        	} else if (im instanceof SkullMeta) {
        		SkullMeta sItemMeta = (SkullMeta)im;
        		itemMeta = new HSkullMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost, sItemMeta.getOwner());
        	} else if (im instanceof MapMeta) {
        		MapMeta sItemMeta = (MapMeta)im;
        		itemMeta = new HMapMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost, sItemMeta.isScaling());
        	} else if (im instanceof BannerMeta) {
        		BannerMeta sItemMeta = (BannerMeta)im;
        		DyeColor dyeColor = sItemMeta.getBaseColor();
        		String baseColor = "WHITE";
        		if (dyeColor != null) baseColor = sItemMeta.getBaseColor().toString();
        		ArrayList<HPattern> patterns = new ArrayList<HPattern>();
        		for (Pattern p:sItemMeta.getPatterns()) {
        			patterns.add(new HPattern(p.getColor().toString(), p.getPattern().toString()));
        		}
        		itemMeta = new HBannerMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost, baseColor, patterns);
        	} else if (im instanceof SpawnEggMeta) {
        		SpawnEggMeta sItemMeta = (SpawnEggMeta)im;
        		itemMeta = new HSpawnEggMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost, sItemMeta.getSpawnedType().name());
        	} else {
        		itemMeta = new HItemMeta(displayName, lore, enchantments, itemFlags, unbreakable, repairCost);
        	}
        	sis.setHItemMeta(itemMeta);
        }   
        return sis;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getItemStack(HItemStack hItemStack) {
		if (hItemStack == null || hItemStack.isBlank()) return null;
        ItemStack generatedItemStack = new ItemStack(Material.matchMaterial(hItemStack.getMaterial()));
        generatedItemStack.setAmount(hItemStack.getAmount());
        generatedItemStack.setDurability(hItemStack.getDurability());
        generatedItemStack.getData().setData(hItemStack.getData());
        if (hItemStack.getItemMeta() != null) {
        	HItemMeta hItemMeta = hItemStack.getItemMeta();
        	ItemMeta itemMeta = generatedItemStack.getItemMeta();
        	itemMeta.setDisplayName(hItemMeta.getDisplayName());
        	itemMeta.setLore(hItemMeta.getLore());
    		for (HEnchantment se:hItemMeta.getEnchantments()) {
				itemMeta.addEnchant(Enchantment.getByKey(se.getEnchantmentKey()), se.getLvl(), true);
    		}
    		for (HItemFlag f:hItemMeta.getItemFlags()) {
    			itemMeta.addItemFlags(ItemFlag.valueOf(f.getItemFlag()));
    		}
    		itemMeta.setUnbreakable(hItemMeta.unbreakable());
    		if (itemMeta instanceof Repairable) {
    			Repairable rp = (Repairable)itemMeta;
    			rp.setRepairCost(hItemMeta.getRepairCost());
    		}
        	if (hItemMeta instanceof HEnchantmentStorageMeta) {
        		HEnchantmentStorageMeta sItemMeta = (HEnchantmentStorageMeta)hItemMeta;
        		EnchantmentStorageMeta esm = (EnchantmentStorageMeta)itemMeta;
        		for (HEnchantment se:sItemMeta.getEnchantments()) {
					esm.addStoredEnchant(Enchantment.getByKey(se.getEnchantmentKey()), se.getLvl(), true);
        		}
        	} else if (hItemMeta instanceof HBookMeta) {
        		HBookMeta sItemMeta = (HBookMeta)hItemMeta;
        		BookMeta bm = (BookMeta)itemMeta;
        		bm.setPages(sItemMeta.getPages());
        		bm.setAuthor(sItemMeta.getAuthor());
        		bm.setTitle(sItemMeta.getTitle());
        	} else if (hItemMeta instanceof HFireworkEffectMeta) {
        		HFireworkEffectMeta sItemMeta = (HFireworkEffectMeta)hItemMeta;
        		FireworkEffectMeta fem = (FireworkEffectMeta)itemMeta;
        		HFireworkEffect sfe = sItemMeta.getEffect();
        		if (sfe != null) {
	    			Builder fb = FireworkEffect.builder();
	    			for (HColor c:sfe.getColors()) {
	    				fb.withColor(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
	    			}
	    			for (HColor c:sfe.getFadeColors()) {
	    				fb.withFade(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
	    			}
	    			fb.with(FireworkEffect.Type.valueOf(sfe.getType()));
	    			fb.flicker(sfe.hasFlicker());
	    			fb.trail(sfe.hasTrail());
	    			fem.setEffect(fb.build());
        		}
        	} else if (hItemMeta instanceof HFireworkMeta) {
        		HFireworkMeta sItemMeta = (HFireworkMeta)hItemMeta;
        		FireworkMeta fm = (FireworkMeta)itemMeta;
        		for (HFireworkEffect sfe:sItemMeta.getEffects()) {
        			Builder fb = FireworkEffect.builder();
        			for (HColor c:sfe.getColors()) {
        				fb.withColor(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
        			}
        			for (HColor c:sfe.getFadeColors()) {
        				fb.withFade(Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue()));
        			}
        			fb.with(FireworkEffect.Type.valueOf(sfe.getType()));
        			fb.flicker(sfe.hasFlicker());
        			fb.trail(sfe.hasTrail());
        			fm.addEffect(fb.build());
        		}
        		fm.setPower(sItemMeta.getPower());
        	} else if (hItemMeta instanceof HLeatherArmorMeta) {
        		HLeatherArmorMeta sItemMeta = (HLeatherArmorMeta)hItemMeta;
        		LeatherArmorMeta lam = (LeatherArmorMeta)itemMeta;
        		HColor sc = sItemMeta.getColor();
        		lam.setColor(Color.fromRGB(sc.getRed(), sc.getGreen(), sc.getBlue()));
        	} else if (hItemMeta instanceof HPotionMeta) {
        		HPotionMeta sItemMeta = (HPotionMeta)hItemMeta;
        		PotionMeta pm = (PotionMeta)itemMeta;
        		for (HPotionEffect spe:sItemMeta.getPotionEffects()) {
        			PotionEffect pe = new PotionEffect(PotionEffectType.getByName(spe.getType()), spe.getDuration(), spe.getAmplifier(), spe.isAmbient());
        			pm.addCustomEffect(pe, true);
        		}
        		HPotionData pd = sItemMeta.getPotionData();
        		pm.setBasePotionData(new PotionData(PotionType.valueOf(pd.getPotionType()), pd.isExtended(), pd.isUpgraded()));
        	} else if (hItemMeta instanceof HSkullMeta) {
        		HSkullMeta sItemMeta = (HSkullMeta)hItemMeta;
        		SkullMeta sm = (SkullMeta)itemMeta;
        		sm.setOwner(sItemMeta.getOwner());
        	} else if (hItemMeta instanceof HMapMeta) {
        		HMapMeta sItemMeta = (HMapMeta)hItemMeta;
        		MapMeta mm = (MapMeta)itemMeta;
        		mm.setScaling(sItemMeta.isScaling());
        	} else if (hItemMeta instanceof HBannerMeta) {
        		HBannerMeta sItemMeta = (HBannerMeta)hItemMeta;
        		BannerMeta bm = (BannerMeta)itemMeta;
        		for (HPattern hp:sItemMeta.getPatterns()) {
        			bm.addPattern(new Pattern(DyeColor.valueOf(hp.getDyeColor()), PatternType.valueOf(hp.getPatternType())));
        		}
        		bm.setBaseColor(DyeColor.valueOf(sItemMeta.getBaseColor()));
        	} else if (hItemMeta instanceof HSpawnEggMeta) {
        		HSpawnEggMeta sItemMeta = (HSpawnEggMeta)hItemMeta;
        		SpawnEggMeta sem = (SpawnEggMeta)itemMeta;
        		sem.setSpawnedType(EntityType.valueOf(sItemMeta.getEntityType()));
        	}
    		generatedItemStack.setItemMeta(itemMeta);
        }
        return generatedItemStack;
	}
	
	
	
	protected String applyColor(String message) {
		message = message.replace("&0", ChatColor.BLACK + "");
		message = message.replace("&1", ChatColor.DARK_BLUE + "");
		message = message.replace("&2", ChatColor.DARK_GREEN + "");
		message = message.replace("&3", ChatColor.DARK_AQUA + "");
		message = message.replace("&4", ChatColor.DARK_RED + "");
		message = message.replace("&5", ChatColor.DARK_PURPLE + "");
		message = message.replace("&6", ChatColor.GOLD + "");
		message = message.replace("&7", ChatColor.GRAY + "");
		message = message.replace("&8", ChatColor.DARK_GRAY + "");
		message = message.replace("&9", ChatColor.BLUE + "");
		message = message.replace("&a", ChatColor.GREEN + "");
		message = message.replace("&b", ChatColor.AQUA + "");
		message = message.replace("&c", ChatColor.RED + "");
		message = message.replace("&d", ChatColor.LIGHT_PURPLE + "");
		message = message.replace("&e", ChatColor.YELLOW + "");
		message = message.replace("&f", ChatColor.WHITE + "");
		message = message.replace("&k", ChatColor.MAGIC + "");
		message = message.replace("&l", ChatColor.BOLD + "");
		message = message.replace("&m", ChatColor.STRIKETHROUGH + "");
		message = message.replace("&n", ChatColor.UNDERLINE + "");
		message = message.replace("&o", ChatColor.ITALIC + "");
		message = message.replace("&r", ChatColor.RESET + "");
		return message;
	}
	
	
	protected String removeColor(String message) {
		message = message.replace("&0", "");
		message = message.replace("&1", "");
		message = message.replace("&2", "");
		message = message.replace("&3", "");
		message = message.replace("&4", "");
		message = message.replace("&5", "");
		message = message.replace("&6", "");
		message = message.replace("&7", "");
		message = message.replace("&8", "");
		message = message.replace("&9", "");
		message = message.replace("&a", "");
		message = message.replace("&b", "");
		message = message.replace("&c", "");
		message = message.replace("&d", "");
		message = message.replace("&e", "");
		message = message.replace("&f", "");
		message = message.replace("&k", "");
		message = message.replace("&l", "");
		message = message.replace("&m", "");
		message = message.replace("&n", "");
		message = message.replace("&o", "");
		message = message.replace("&r", "");
		return ChatColor.stripColor(message);
	}
	
	
	
	protected Item getItem(HItem i) {
		if (i == null) return null;
		Location l = getLocation(i.getLocation());
		if (l == null) return null;
		for (Entity e:l.getWorld().getEntities()) {
			if (e instanceof Item) {
				Item item = (Item)e;
				if (item.getEntityId() == i.getId()) {
					return item;
				}
			}
		}
		return null;
	}
	
	protected HItem getItem(Item i) {
		if (i == null) return null;
		HLocation l = getLocation(i.getLocation());
		HItemStack stack = getSerializableItemStack(i.getItemStack());
		return new HItem(hc, l, i.getEntityId(), stack);
	}
	
	protected boolean chunkContainsLocation(HLocation l, Chunk c) {
		if (l == null || c == null) return false;
		Location loc = getLocation(l);
		if (loc.getChunk().equals(c)) return true;
		return false;
	}
	
	
}
