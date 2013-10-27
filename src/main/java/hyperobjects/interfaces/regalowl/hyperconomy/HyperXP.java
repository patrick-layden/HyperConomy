package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public interface HyperXP extends HyperObject {

	public int getBarXpPoints(Player player);
	public int getXpForNextLvl(int lvl);
	public int getLvlXpPoints(int lvl);
	public int getTotalXpPoints(Player player);
	public int getLvlFromXP(int exp);
	
	public boolean addXp(Player p, int amount);
	public boolean removeXp(Player p, int amount);
	
	public double getValue(int amount);
	public double getCost(int amount);
	
}
