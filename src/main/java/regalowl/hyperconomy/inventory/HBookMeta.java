package regalowl.hyperconomy.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.simpledatalib.CommonFunctions;
 

public class HBookMeta extends HItemMeta {


	private static final long serialVersionUID = -1654237920132537906L;
	private String author;
	private ArrayList<String> pages;
	private String title;

	public HBookMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HItemFlag> itemFlags, boolean unbreakable, int repairCost, String author, ArrayList<String> pages, String title) {
		super(displayName, lore, enchantments, itemFlags, unbreakable, repairCost);
		this.author = author;
		this.pages = pages;
		this.title = title;
	}
	

	public HBookMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.author = data.get("author");
		this.pages = CommonFunctions.explode(data.get("pages"));
		this.title = data.get("title");
    }
	
	public HBookMeta(HBookMeta meta) {
		super(meta);
		this.author = meta.author;
		this.pages = new ArrayList<String>(meta.pages);
		this.title = meta.title;
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
	public ArrayList<String> displayInfo(HyperPlayer p, String color1, String color2) {
		ArrayList<String> info = super.displayInfo(p, color1, color2);
		info.add(color1 + "Author: " + color2 + author);
		info.add(color1 + "Title: " + color2 + title);
		info.add(color1 + "Page Count: " + color2 + pages.size());
		return info;
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