package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;


import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;
 

public class HBookMeta extends HItemMeta implements Serializable {
	private static final long serialVersionUID = -1095975801937823837L;

	private String author;
	private List<String> pages;
	private String title;

	public HBookMeta(String displayName, List<String> lore, List<HEnchantment> enchantments, String author, List<String> pages, String title) {
		super(displayName, lore, enchantments);
		this.author = author;
		this.pages = pages;
		this.title = title;
	}
	

	public HBookMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HBookMeta)) {return;}
			HBookMeta bm = (HBookMeta)o;
			this.author = bm.getAuthor();
			this.pages = bm.getPages();
			this.title = bm.getTitle();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
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
		HBookMeta other = (HBookMeta) obj;
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