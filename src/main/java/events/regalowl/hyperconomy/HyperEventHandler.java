package regalowl.hyperconomy;

import java.util.concurrent.CopyOnWriteArrayList;

public class HyperEventHandler {
	
    private CopyOnWriteArrayList<HyperListener> listeners = new CopyOnWriteArrayList<HyperListener>();
    
    public void clearListeners() {
    	listeners.clear();
    }
    
    public synchronized void registerDataLoadListener(DataLoadListener listener) {
    	listeners.add(listener);
    }
    public synchronized void fireDataLoadEvent() {
        for (HyperListener listener : listeners) {
        	if (listener instanceof DataLoadListener) {
        		DataLoadListener dListener = (DataLoadListener)listener;
        		dListener.onDataLoad();
        	}
        }
    }
    
    public synchronized void registerTransactionListener(TransactionListener listener) {
    	listeners.add(listener);
    }
    public synchronized void fireTransactionEvent(PlayerTransaction transaction, TransactionResponse response) {
        for (HyperListener listener : listeners) {
        	if (listener instanceof TransactionListener) {
        		TransactionListener dListener = (TransactionListener)listener;
        		dListener.onTransaction(transaction, response);
        	}
        }
    }
    

}