package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;

public class HyperAPI implements GeneralAPI {

	public String listShops() {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		return cf.implode(hc.getEconomyManager().listShops(),",");
	}

	public String listEconomies() {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		return cf.implode(hc.getEconomyManager().getEconomyList(),",");
	}

	public int getShopP1X(String shop) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().shopExists(shop)) {
			return hc.getEconomyManager().getShop(shop).getP1x();
		} else {
			return 0;
		}
	}

	public int getShopP1Y(String shop) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().shopExists(shop)) {
			return hc.getEconomyManager().getShop(shop).getP1y();
		} else {
			return 0;
		}
	}

	public int getShopP1Z(String shop) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().shopExists(shop)) {
			return hc.getEconomyManager().getShop(shop).getP1z();
		} else {
			return 0;
		}
	}

	public int getShopP2X(String shop) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().shopExists(shop)) {
			return hc.getEconomyManager().getShop(shop).getP2x();
		} else {
			return 0;
		}
	}

	public int getShopP2Y(String shop) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().shopExists(shop)) {
			return hc.getEconomyManager().getShop(shop).getP2y();
		} else {
			return 0;
		}
	}

	public int getShopP2Z(String shop) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().shopExists(shop)) {
			return hc.getEconomyManager().getShop(shop).getP2z();
		} else {
			return 0;
		}
	}

	public double getPlayerX(String player) {
		for (Player op:Bukkit.getOnlinePlayers()) {
			if (op.getName().equalsIgnoreCase(player)) {
				return op.getLocation().getX();
			}
		}
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().hyperPlayerExists(player)) {
			return hc.getEconomyManager().getHyperPlayer(player).getX();
		} else {
			return 0.0;
		}
	}

	public double getPlayerY(String player) {
		for (Player op:Bukkit.getOnlinePlayers()) {
			if (op.getName().equalsIgnoreCase(player)) {
				return op.getLocation().getY();
			}
		}
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().hyperPlayerExists(player)) {
			return hc.getEconomyManager().getHyperPlayer(player).getY();
		} else {
			return 0.0;
		}
	}

	public double getPlayerZ(String player) {
		for (Player op:Bukkit.getOnlinePlayers()) {
			if (op.getName().equalsIgnoreCase(player)) {
				return op.getLocation().getZ();
			}
		}
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().hyperPlayerExists(player)) {
			return hc.getEconomyManager().getHyperPlayer(player).getZ();
		} else {
			return 0.0;
		}
	}

	public String getPlayerShop(Player player) {
		HyperConomy hc = HyperConomy.hc;
		Shop shop = hc.getEconomyManager().getShop(player);
		if (null == shop){
			return "";
		} else {
			return shop.getName();
		}
	}

	public boolean checkHash(String player, String SHA256Hash) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().hyperPlayerExists(player)) {
			if (hc.getEconomyManager().getHyperPlayer(player).getHash().equals(SHA256Hash)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	public String getSalt(String player) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().hyperPlayerExists(player)) {
			return hc.getEconomyManager().getHyperPlayer(player).getSalt();
		} else {
			return "";
		}
	}

	public double getAPIVersion() {
		return 0;
	}

	public String getShopEconomy(String shop) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getEconomyManager().shopExists(shop)) {
			return hc.getEconomyManager().getShop(shop).getEconomy();
		} else {
			return "";
		}
	}

	public String getGlobalShopAccount() {
		return HyperConomy.hc.gYH().gFC("config").getString("config.global-shop-account");
	}
	
	public boolean isItemDisplay(Item item) {
		try {
			if (item == null) {
				return false;
			}
			ItemDisplayFactory idf = HyperConomy.hc.getItemDisplay();
			if (idf == null) {
				return false;
			} else {
				return idf.isDisplay(item);
			}
		} catch (Exception e) {
			HyperConomy.hc.gDB().writeError(e);
			return false;
		}
	}

	public ArrayList<String> getPlayerShopList() {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<Shop> shops = hc.getEconomyManager().getShops();
		ArrayList<String> names = new ArrayList<String>();
		for (Shop s:shops) {
			if (s instanceof PlayerShop) {
				names.add(s.getName());
			}
		}
		return names;
	}

	public ArrayList<String> getServerShopList() {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<Shop> shops = hc.getEconomyManager().getShops();
		ArrayList<String> names = new ArrayList<String>();
		for (Shop s:shops) {
			if (s instanceof ServerShop) {
				names.add(s.getName());
			}
		}
		return names;
	}

	public Shop getShop(String name) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getEconomyManager().getShop(name);
	}

	public ServerShop getServerShop(String name) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getEconomyManager().getShop(name);
		if (s instanceof ServerShop) {
			return (ServerShop)s;
		}
		return null;
	}

	public PlayerShop getPlayerShop(String name) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getEconomyManager().getShop(name);
		if (s instanceof PlayerShop) {
			return (PlayerShop)s;
		}
		return null;
	}

}
