package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


 

public class SerializableSkullMeta extends SerializableItemMeta implements Serializable {

	private static final long serialVersionUID = -711270187445211416L;
	
	private UUID owner;

	public SerializableSkullMeta(ItemMeta im) {
		super(im);
		if (im instanceof SkullMeta) {
			SkullMeta sm = (SkullMeta)im;
			this.owner = sm.getOwningPlayer().getUniqueId();
		}
    }

	public SerializableSkullMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableSkullMeta)) {return;}
			SerializableSkullMeta ssm = (SerializableSkullMeta)o;
			this.owner = ssm.getOwnerUUID();
    	} catch (Exception e) {
    		
    	}
    }
	
	
	@Override
	public ItemMeta getItemMeta() {
		ItemStack s = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta sm = (SkullMeta)s.getItemMeta();
		sm.setDisplayName(displayName);
		sm.setLore(lore);
		for (SerializableEnchantment se:enchantments) {
			sm.addEnchant(se.getEnchantment(), se.getLvl(), true);
		}
		sm.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
		return sm;
	}
	
	public String getOwnerName() {
		return Bukkit.getPlayer(owner).getDisplayName();
	}

	public UUID getOwnerUUID() {
		return owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SerializableSkullMeta other = (SerializableSkullMeta) obj;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

}