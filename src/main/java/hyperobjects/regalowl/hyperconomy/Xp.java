package regalowl.hyperconomy;


public class Xp extends BasicObject implements HyperObject {

	public Xp(String name, String economy, String displayName, String aliases, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock) {
		super(name, economy, displayName, aliases, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
	}
	
	@Override
	public void add(int amount, HyperPlayer hp) {
		if (hp == null || amount < 0) {return;}
		int totalxp = hp.getTotalXpPoints();
		int newxp = totalxp + amount;
		int newlvl = hp.getLvlFromXP(newxp);
		newxp = newxp - hp.getLvlXpPoints(newlvl);
		float xpbarxp = (float) newxp / (float) hp.getXpForNextLvl(newlvl);
		hp.getPlayer().setLevel(newlvl);
		hp.getPlayer().setExp(xpbarxp);
	}
	
	@Override
	public double remove(int amount, HyperPlayer hp) {
		if (hp == null || amount < 0) {return 0.0;}
		int totalxp = hp.getTotalXpPoints();
		int newxp = totalxp - amount;
		if (newxp < 0) {return 0.0;}
		int newlvl = hp.getLvlFromXP(newxp);
		newxp = newxp - hp.getLvlXpPoints(newlvl);
		float xpbarxp = (float) newxp / (float) hp.getXpForNextLvl(newlvl);
		hp.getPlayer().setLevel(newlvl);
		hp.getPlayer().setExp(xpbarxp);
		return amount;
	}

}
