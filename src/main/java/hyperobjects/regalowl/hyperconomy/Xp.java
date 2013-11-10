package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Xp extends BasicObject implements HyperXP {

	public Xp(String name, String economy, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock) {
		super(name, economy, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
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

}
