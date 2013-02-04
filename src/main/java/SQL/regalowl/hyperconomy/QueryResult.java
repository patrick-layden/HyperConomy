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
	
	public String getString(String column) {
		return (String) data.get(colNames.indexOf(column)).get(currentRow);
	}
	
	public Double getDouble(String column) {
		return Double.parseDouble(data.get(colNames.indexOf(column)).get(currentRow));
	}
	
	
	public Integer getInt(String column) {
		return Integer.parseInt(data.get(colNames.indexOf(column)).get(currentRow));
	}
	
	public String getString(Integer column) {
		return (String) data.get(column - 1).get(currentRow);
	}
	
	public Double getDouble(Integer column) {
		return Double.parseDouble(data.get(column - 1).get(currentRow));
	}
	
	public Integer getInt(Integer column) {
		return Integer.parseInt(data.get(column - 1).get(currentRow));
	}
	
	public boolean next() {
		currentRow++;
		if (data.get(0).size() > currentRow) {
			return true;
		} else {
			return false;
		}
	}
	
	public void close() {
		colNames.clear();
		data.clear();
	}
	
	
	
}
