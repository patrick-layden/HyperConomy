package regalowl.hyperconomy;

import java.util.ArrayList;




public class QueryResult {
	
	
	private ArrayList<String> colNames = new ArrayList<String>();
	private ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	private int currentRow;
	
	QueryResult() {
		currentRow = -1;
	}
	public void addColumnName(String name) {
		colNames.add(name);
		data.add(new ArrayList<String>());
	}
	public void addData(int columnIndex, String object) {
		data.get(columnIndex - 1).add(object);
	}
	
	
	
	
	
	
	public boolean next() {
		currentRow++;
		if (data.isEmpty()) {
			return false;
		} else if (data.get(0).size() > currentRow) {
			return true;
		} else {
			return false;
		}
	}
	public void close() {
		colNames.clear();
		data.clear();
	}	
	
	
	

	
	
	
	public String getString(String column) {
		if (colNames.indexOf(column) == -1) {
			return null;
		}
		return (String) data.get(colNames.indexOf(column)).get(currentRow);
	}
	public Double getDouble(String column) {
		if (colNames.indexOf(column) == -1) {
			return null;
		}
		String dat = data.get(colNames.indexOf(column)).get(currentRow);
		if (dat == null) {
			return 0.0;
		} else {
			return Double.parseDouble(dat);
		}
	}
	public Integer getInt(String column) {
		if (colNames.indexOf(column) == -1) {
			return null;
		}
		String dat = data.get(colNames.indexOf(column)).get(currentRow);
		if (dat == null) {
			return 0;
		} else {
			return Integer.parseInt(dat);
		}
	}
	
	
	
	
	
	public String getString(Integer column) {
		return (String) data.get(column - 1).get(currentRow);
	}
	public Double getDouble(Integer column) {
		String dat = data.get(column - 1).get(currentRow);
		if (dat == null) {
			return 0.0;
		} else {
			return Double.parseDouble(dat);
		}
	}
	public Integer getInt(Integer column) {
		String dat = data.get(column - 1).get(currentRow);
		if (dat == null) {
			return 0;
		} else {
			return Integer.parseInt(dat);
		}
	}
	

	
	
	
}
