package regalowl.hyperconomy.event;


import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.scheduler.BukkitRunnable;

import regalowl.hyperconomy.HyperConomy;
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
    
    
    public void fireHyperObjectModificationEvent(HyperObject ho, HModType type) {
    	new HyperObjectModificationTask(ho, type).runTask(hc);
    }
    private class HyperObjectModificationTask extends BukkitRunnable {
    	private HyperObject ho;
    	private HModType type;
    	public HyperObjectModificationTask(HyperObject ho, HModType type) {
    		this.ho = ho;
    		this.type = type;
    	}
		public void run() {
	        for (HyperListener listener : listeners) {
	        	if (listener instanceof HyperObjectModificationListener) {
	        		HyperObjectModificationListener l = (HyperObjectModificationListener)listener;
	        		l.onHyperObjectModification(ho, type);
	        	}
	        }
		}
    }

}