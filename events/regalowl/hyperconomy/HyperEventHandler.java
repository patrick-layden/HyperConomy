package regalowl.hyperconomy;

import java.util.concurrent.CopyOnWriteArrayList;

public class HyperEventHandler {
	
    private CopyOnWriteArrayList<DataLoadListener> dataLoadListeners = new CopyOnWriteArrayList<DataLoadListener>();
    private CopyOnWriteArrayList<TransactionListener> transactionListeners = new CopyOnWriteArrayList<TransactionListener>();
    private CopyOnWriteArrayList<ShopCreationListener> shopCreationListeners = new CopyOnWriteArrayList<ShopCreationListener>();
    
    public void clearListeners() {
    	dataLoadListeners.clear();
    	transactionListeners.clear();
    	shopCreationListeners.clear();
    }
    
    public synchronized void registerDataLoadListener(DataLoadListener listener) {
    	dataLoadListeners.add(listener);
    }
    public synchronized void fireDataLoadEvent() {
        for (DataLoadListener listener : dataLoadListeners) {
        	listener.onDataLoad();
        }
    }
    
    public synchronized void registerTransactionListener(TransactionListener listener) {
    	transactionListeners.add(listener);
    }
    public synchronized void fireTransactionEvent(PlayerTransaction transaction, TransactionResponse response) {
        for (TransactionListener listener : transactionListeners) {
        	listener.onTransaction(transaction, response);
        }
    }
    
    public synchronized void registerShopCreationListener(ShopCreationListener listener) {
    	shopCreationListeners.add(listener);
    }
    public synchronized void fireShopCreationEvent(Shop s) {
        for (ShopCreationListener listener : shopCreationListeners) {
        	listener.onShopCreation(s);
        }
    }
    

}