package regalowl.hyperconomy.event;

import java.util.concurrent.CopyOnWriteArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;

public class HyperEventHandler {
	
	private HyperConomy hc;
    private CopyOnWriteArrayList<DataLoadListener> dataLoadListeners = new CopyOnWriteArrayList<DataLoadListener>();
    private CopyOnWriteArrayList<EconomyLoadListener> economyLoadListeners = new CopyOnWriteArrayList<EconomyLoadListener>();
    private CopyOnWriteArrayList<TransactionListener> transactionListeners = new CopyOnWriteArrayList<TransactionListener>();
    private CopyOnWriteArrayList<ShopCreationListener> shopCreationListeners = new CopyOnWriteArrayList<ShopCreationListener>();
    
    
    public HyperEventHandler() {
    	hc = HyperConomy.hc;
    }
    
    public void clearListeners() {
    	dataLoadListeners.clear();
    	transactionListeners.clear();
    	shopCreationListeners.clear();
    	economyLoadListeners.clear();
    }
    
    public synchronized void registerEconomyLoadListener(EconomyLoadListener listener) {
    	economyLoadListeners.add(listener);
    }
    public synchronized void fireEconomyLoadEvent() {
		hc.getServer().getScheduler().runTask(hc, new Runnable() {
			public void run() {
		        for (EconomyLoadListener listener : economyLoadListeners) {
		        	listener.onEconomyLoad();
		        }
			}
		});
    }    
    
    public synchronized void registerDataLoadListener(DataLoadListener listener) {
    	dataLoadListeners.add(listener);
    }
    public synchronized void fireDataLoadEvent() {
		hc.getServer().getScheduler().runTask(hc, new Runnable() {
			public void run() {
		        for (DataLoadListener listener : dataLoadListeners) {
		        	listener.onDataLoad();
		        }
			}
		});
    }
    
    public synchronized void registerTransactionListener(TransactionListener listener) {
    	transactionListeners.add(listener);
    }
    public synchronized void fireTransactionEvent(PlayerTransaction transaction, TransactionResponse response) {
    	new TransactionEvent(transaction, response);
    }
    private class TransactionEvent {
    	private PlayerTransaction transaction;
    	private TransactionResponse response;
    	TransactionEvent(PlayerTransaction trans, TransactionResponse resp) {
    		transaction = trans;
    		response = resp;
    		hc.getServer().getScheduler().runTask(hc, new Runnable() {
    			public void run() {
    		        for (TransactionListener listener : transactionListeners) {
    		        	listener.onTransaction(transaction, response);
    		        }
    			}
    		});
    	}
    }
    
    public synchronized void registerShopCreationListener(ShopCreationListener listener) {
    	shopCreationListeners.add(listener);
    }
    public synchronized void fireShopCreationEvent(Shop s) {
    	new ShopCreationEvent(s);
    }
    private class ShopCreationEvent {
    	private Shop s;
    	ShopCreationEvent(Shop shop) {
    		s = shop;
    		hc.getServer().getScheduler().runTask(hc, new Runnable() {
    			public void run() {
    		        for (ShopCreationListener listener : shopCreationListeners) {
    		        	listener.onShopCreation(s);
    		        }
    			}
    		});
    	}
    }

}