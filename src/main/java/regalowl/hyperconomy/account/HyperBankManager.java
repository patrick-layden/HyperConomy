package regalowl.hyperconomy.account;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;

public class HyperBankManager {
	
	private transient HyperConomy hc;
	private ConcurrentHashMap<String, HyperBank> hyperBanks = new ConcurrentHashMap<String, HyperBank>();
	
	
	public HyperBankManager(HyperConomy hc) {
		this.hc = hc;
		hc.getHyperEventHandler().registerListener(this);
	}
	
	@EventHandler
	public void onDataLoad(DataLoadEvent event) {
		if (!(event.loadType == DataLoadType.PLAYER)) return;
		new Thread(new Runnable() {
			public void run() {
				loadData();
			}
		}).start();
	}
	
	private void loadData() {
		hyperBanks.clear();
		QueryResult bankData = hc.getSQLRead().select("SELECT * FROM hyperconomy_banks");
		while (bankData.next()) {
			HyperBank hBank = new HyperBank(hc, bankData.getString("NAME"), bankData.getDouble("BALANCE"), bankData.getString("OWNERS"), bankData.getString("MEMBERS"));
			hyperBanks.put(hBank.getName().toLowerCase(), hBank);
		}
		bankData.close();
		hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.BANK));
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
		if (!hyperBanks.containsKey(hb.getName().toLowerCase())) {
			hyperBanks.put(hb.getName().toLowerCase(), hb);
		}
	}
	
	public void removeHyperBank(HyperBank hb) {
		if (hb == null) {return;}
		if (hyperBanks.contains(hb.getName().toLowerCase())) {
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

}


