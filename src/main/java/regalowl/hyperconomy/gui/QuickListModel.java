package regalowl.hyperconomy.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

public class QuickListModel<T> extends AbstractListModel<T> {
	private static final long serialVersionUID = 6026802865693518442L;
	private ArrayList<T> data = new ArrayList<T>();
	@Override
	public T getElementAt(int index) {
		if (data.size() <= index) return null;
		return data.get(index);
	}
	@Override
	public int getSize() {
		return data.size();
	}
	
	public boolean contains(T d) {
		return data.contains(d);
	}
	
	public T getData(int index) {
		if (index < data.size()) {
			return data.get(index);
		}
		return null;
	}
	
	public int indexOf(T d) {
		return data.indexOf(d);
	}
	
	public void removeIndex(int index) {
		if (data.size() > index) {
			data.remove(index);
			fireIntervalRemoved(this, 0, data.size());
		}
	}
	public void removeData(T s) {
		if (data.contains(s)) {
			data.remove(s);
			fireIntervalRemoved(this, 0, data.size());
		}
	}
	public void clear() {
		data.clear();
		fireIntervalRemoved(this, 0, data.size());
	}
	
	public void addIndex(int index, T s) {
		if (s == null) return;
		if (index < data.size()) {
			data.add(index, s);
			fireIntervalAdded(this, index, index);
		}
	}
	public void addData(T s) {
		if (s == null) return;
		data.add(s);
		fireIntervalAdded(this, data.indexOf(s), data.indexOf(s));
	}
	public void addData(List<T> ls) {
		if (ls == null || ls.size() == 0) return;
		data.addAll(ls);
		fireIntervalAdded(this, 0, data.size() - 1);
	}
}
