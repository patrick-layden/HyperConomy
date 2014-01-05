package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class ShopXp extends BasicShopObject implements PlayerShopObject, HyperXP {

	ShopXp(PlayerShop playerShop, HyperObject ho, double stock, double buyPrice, double sellPrice, int maxStock, HyperObjectStatus status) {
		super(playerShop, ho, stock, buyPrice, sellPrice, maxStock, status);
	}

	public int getBarXpPoints(Player player) {
		int lvl = player.getLevel();
		int exppoints = (int) Math.floor(((3.5 * lvl) + 6.7) * player.getExp() + .5);
		return exppoints;
	}

	public int getXpForNextLvl(int lvl) {
		int exppoints = (int) Math.floor(((3.5 * lvl) + 6.7) + .5);
		return exppoints;
	}

	public int getLvlXpPoints(int lvl) {
		int exppoints = (int) Math.floor((1.75 * Math.pow(lvl, 2)) + (5 * lvl) + .5);
		return exppoints;
	}

	public int getTotalXpPoints(Player player) {
		int lvl = player.getLevel();
		int lvlxp = getLvlXpPoints(lvl);
		int barxp = getBarXpPoints(player);
		int totalxp = lvlxp + barxp;
		return totalxp;
	}

	public int getLvlFromXP(int exp) {
		double lvlraw = (Math.sqrt((exp * 7.0) + 25.0) - 5.0) * (2.0 / 7.0);
		int lvl = (int) Math.floor(lvlraw);
		if ((double) lvl > lvlraw) {
			lvl = lvl - 1;
		}
		return lvl;
	}
	
	public boolean addXp(Player p, int amount) {
		if (p == null || amount < 0) {return false;}
		int totalxp = getTotalXpPoints(p);
		int newxp = totalxp + amount;
		int newlvl = getLvlFromXP(newxp);
		newxp = newxp - getLvlXpPoints(newlvl);
		float xpbarxp = (float) newxp / (float) getXpForNextLvl(newlvl);
		p.setLevel(newlvl);
		p.setExp(xpbarxp);
		return true;
	}
	
	public boolean removeXp(Player p, int amount) {
		if (p == null || amount < 0) {return false;}
		int totalxp = getTotalXpPoints(p);
		int newxp = totalxp - amount;
		if (newxp < 0) {return false;}
		int newlvl = getLvlFromXP(newxp);
		newxp = newxp - getLvlXpPoints(newlvl);
		float xpbarxp = (float) newxp / (float) getXpForNextLvl(newlvl);
		p.setLevel(newlvl);
		p.setExp(xpbarxp);
		return true;
	}
	
	public double getValue(int amount) {
		if (sellPrice != 0.0) {
			return sellPrice * amount;
		} else {
			return ((HyperXP)ho).getValue(amount);
		}
	}

	public double getCost(int amount) {
		if (buyPrice != 0.0) {
			return buyPrice * amount;
		} else {
			return ((HyperXP)ho).getCost(amount);
		}
	}

}
