package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


 

public class SerializableBookMeta extends SerializableItemMeta {
	private static final long serialVersionUID = -1095975801937823837L;

	private String author;
	private List<String> pages;
	private String title;

	public SerializableBookMeta(ItemMeta im) {
		super(im);
		if (im instanceof BookMeta) {
			BookMeta bm = (BookMeta)im;
			this.author = bm.getAuthor();
			this.pages = bm.getPages();
			this.title = bm.getTitle();
		}
    }

	public SerializableBookMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableBookMeta)) {return;}
			SerializableBookMeta bm = (SerializableBookMeta)o;
			this.author = bm.getAuthor();
			this.pages = bm.getPages();
			this.title = bm.getTitle();
    	} catch (Exception e) {
    		
    	}
    }
	
	
	@Override
	public ItemMeta getItemMeta() {
		ItemStack s = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta)s.getItemMeta();
		bm.setDisplayName(displayName);
		bm.setLore(lore);
		for (SerializableEnchantment se:enchantments) {
			bm.addEnchant(se.getEnchantment(), se.getLvl(), true);
		}
		bm.setPages(pages);
		bm.setAuthor(author);
		bm.setTitle(title);
		return bm;
	}
	
	public List<String> getPages() {
		return pages;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((pages == null) ? 0 : pages.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		SerializableBookMeta other = (SerializableBookMeta) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (pages == null) {
			if (other.pages != null)
				return false;
		} else if (!pages.equals(other.pages))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}