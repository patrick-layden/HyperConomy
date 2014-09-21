package regalowl.hyperconomy.event;


import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.scheduler.BukkitRunnable;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;

public class HyperEventHandler {
	
	private HyperConomy hc;
	private CopyOnWriteArrayList<HyperListener> listeners = new CopyOnWriteArrayList<HyperListener>();
	
    public HyperEventHandler() {
    	hc = HyperConomy.hc;
    }
    
    public void clearListeners() {
    	listeners.clear();
    }
    
    public synchronized void registerListener(HyperListener listener) {
    	listeners.add(listener);
    }
    public synchronized void unRegisterListener(HyperListener listener) {
    	if (listeners.contains(listener)) {
    		listeners.remove(listener);
    	}
    }
    public void fireEconomyLoadEvent() {
		hc.getServer().getScheduler().runTask(hc, new Runnable() {
			public void run() {
		        for (HyperListener listener : listeners) {
		        	if (listener instanceof EconomyLoadListener) {
		        		EconomyLoadListener l = (EconomyLoadListener)listener;
		        		l.onEconomyLoad();
		        	}
		        }
			}
		});
    }    
    
    public void fireDataLoadEvent() {
		hc.getServer().getScheduler().runTask(hc, new Runnable() {
			public void run() {
		        for (HyperListener listener : listeners) {
		        	if (listener instanceof DataLoadListener) {
		        		DataLoadListener l = (DataLoadListener)listener;
		        		l.onDataLoad();
		        	}
		        }
			}
		});
    }

    
    public void fireTransactionEvent(PlayerTransaction transaction, TransactionResponse response) {
    	new TransactionTask(transaction, response).runTask(hc);
    }
    private class TransactionTask extends BukkitRunnable {
    	private PlayerTransaction transaction;
    	private TransactionResponse response;
    	public TransactionTask(PlayerTransaction transaction, TransactionResponse response) {
    		this.transaction = transaction;
    		this.response = response;
    	}
		public void run() {
	        for (HyperListener listener : listeners) {
	        	if (listener instanceof TransactionListener) {
	        		TransactionListener l = (TransactionListener)listener;
	        		l.onTransaction(transaction, response);
	        	}
	        }
		}
    }
    
    
    
    public void fireShopCreationEvent(Shop s) {
    	new ShopCreationTask(s).runTask(hc);
    }
    private class ShopCreationTask extends BukkitRunnable {
    	private Shop s;
    	public ShopCreationTask(Shop s) {
    		this.s = s;
    	}
		public void run() {
	        for (HyperListener listener : listeners) {
	        	if (listener instanceof ShopCreationListener) {
	        		ShopCreationListener l = (ShopCreationListener)listener;
	        		l.onShopCreation(s);
	        	}
	        }
		}
    }
    
    
    public void fireHyperObjectModificationEvent(HyperObject ho) {
    	new HyperObjectModificationTask(ho).runTask(hc);
    }
    private class HyperObjectModificationTask extends BukkitRunnable {
    	private HyperObject ho;
    	public HyperObjectModificationTask(HyperObject ho) {
    		this.ho = ho;
    	}
		public void run() {
	        for (HyperListener listener : listeners) {
	        	if (listener instanceof HyperObjectModificationListener) {
	        		HyperObjectModificationListener l = (HyperObjectModificationListener)listener;
	        		l.onHyperObjectModification(ho);
	        	}
	        }
		}
    }
    
    public void fireHyperPlayerModificationEvent(HyperPlayer hp) {
    	new HyperPlayerModificationTask(hp).runTask(hc);
    }
    private class HyperPlayerModificationTask extends BukkitRunnable {
    	private HyperPlayer hp;
    	public HyperPlayerModificationTask(HyperPlayer hp) {
    		this.hp = hp;
    	}
		public void run() {
	        for (HyperListener listener : listeners) {
	        	if (listener instanceof HyperPlayerModificationListener) {
	        		HyperPlayerModificationListener l = (HyperPlayerModificationListener)listener;
	        		l.onHyperPlayerModification(hp);
	        	}
	        }
		}
    }
    
    public void fireHyperBankModificationEvent(HyperBank hb) {
    	new HyperBankModificationTask(hb).runTask(hc);
    }
    private class HyperBankModificationTask extends BukkitRunnable {
    	private HyperBank hb;
    	public HyperBankModificationTask(HyperBank hb) {
    		this.hb = hb;
    	}
		public void run() {
	        for (HyperListener listener : listeners) {
	        	if (listener instanceof HyperBankModificationListener) {
	        		HyperBankModificationListener l = (HyperBankModificationListener)listener;
	        		l.onHyperBankModification(hb);
	        	}
	        }
		}
    }
    
    public void fireShopModificationEvent(Shop s) {
    	new ShopModificationTask(s).runTask(hc);
    }
    private class ShopModificationTask extends BukkitRunnable {
    	private Shop s;
    	public ShopModificationTask(Shop s) {
    		this.s = s;
    	}
		public void run() {
	        for (HyperListener listener : listeners) {
	        	if (listener instanceof ShopModificationListener) {
	        		ShopModificationListener l = (ShopModificationListener)listener;
	        		l.onShopModification(s);
	        	}
	        }
		}
    }
    
	public void fireDisableEvent() {
		for (HyperListener listener : listeners) {
			if (listener instanceof DisableListener) {
				DisableListener l = (DisableListener) listener;
				l.onDisable();
			}
		}
	}
	
	public void fireEconomyCreationEvent() {
		for (HyperListener listener : listeners) {
			if (listener instanceof HyperEconomyCreationListener) {
				HyperEconomyCreationListener l = (HyperEconomyCreationListener) listener;
				l.onEconomyCreation();
			}
		}
	}

}