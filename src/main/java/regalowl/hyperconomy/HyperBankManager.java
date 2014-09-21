package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.databukkit.sql.QueryResult;
import regalowl.hyperconomy.account.HyperBank;

public class HyperBankManager {
	
	private HyperConomy hc;
	//private DataManager dm;
	private ConcurrentHashMap<String, HyperBank> hyperBanks = new ConcurrentHashMap<String, HyperBank>();
	
	
	public HyperBankManager(DataManager dm) {
		hc = HyperConomy.hc;
		//this.dm = dm;
	}
	
	
	public void loadData() {
		hyperBanks.clear();
		QueryResult bankData = hc.getSQLRead().select("SELECT * FROM hyperconomy_banks");
		while (bankData.next()) {
			HyperBank hBank = new HyperBank(bankData.getString("NAME"), bankData.getDouble("BALANCE"), bankData.getString("OWNERS"), bankData.getString("MEMBERS"));
			hyperBanks.put(hBank.getName().toLowerCase(), hBank);
		}
		bankData.close();
		hc.getDebugMode().ayncDebugConsoleMessage("Banks loaded.");
	}
	
	
	
	public HyperBank getHyperBank(String name) {
		if (name == null) {return null;}
		String bankName = name.toLowerCase();
		if (hyperBanks.containsKey(bankName.toLowerCase())) {
			return hyperBanks.get(bankName.toLowerCase());
		}
		return null;
	}

	public void addHyperBank(HyperBank hb) {
		if (hb == null) {return;}
		if (!hyperBanks.contains(hb)) {
			hyperBanks.put(hb.getName().toLowerCase(), hb);
		}
	}
	
	public void removeHyperBank(HyperBank hb) {
		if (hb == null) {return;}
		if (hyperBanks.contains(hb)) {
			hyperBanks.remove(hb.getName().toLowerCase());
		}
	}
	
	public void removeHyperBank(String name) {
		if (name == null) return;
		if (!hasBank(name)) return;
		hyperBanks.remove(name.toLowerCase());
	}
	
	public boolean hasBank(String name) {
		if (name == null) {return false;}
		return hyperBanks.containsKey(name.toLowerCase());
	}
	
	public ArrayList<HyperBank> getHyperBanks() {
		ArrayList<HyperBank> hbs = new ArrayList<HyperBank>();
		for (HyperBank hb:hyperBanks.values()) {
			hbs.add(hb);
		}
		return hbs;
	}
	
	public ArrayList<String> getHyperBankNames() {
		ArrayList<String> hbs = new ArrayList<String>();
		for (HyperBank hb:hyperBanks.values()) {
			hbs.add(hb.getName());
		}
		return hbs;
	}
	/*
	public void renameBanksWithThisName(String name) {
		if (hasBank(name)) {
			HyperBank hb = getHyperBank(name);
			int c = 0;
			while (hasBank(name + c)) {c++;}
			hb.setName(name + c);
		}
	}
	*/
	
}


