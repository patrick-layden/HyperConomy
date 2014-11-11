package regalowl.hyperconomy.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import regalowl.databukkit.CommonFunctions;
 

public class HBookMeta extends HItemMeta {

	private String author;
	private ArrayList<String> pages;
	private String title;

	public HBookMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, String author, ArrayList<String> pages, String title) {
		super(displayName, lore, enchantments);
		this.author = author;
		this.pages = pages;
		this.title = title;
	}
	

	public HBookMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.author = data.get("author");
		this.pages = CommonFunctions.explode(data.get("pages"));;
		this.title = data.get("title");
    }
	
	@Override
	public String serialize() {
		HashMap<String,String> data = super.getMap();
		data.put("author", author);
		data.put("pages", CommonFunctions.implode(pages));
		data.put("title", title);
		return CommonFunctions.implodeMap(data);
	}
	
	@Override
	public HItemMetaType getType() {
		return HItemMetaType.BOOK;
	}
	

	public ArrayList<String> getPages() {
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