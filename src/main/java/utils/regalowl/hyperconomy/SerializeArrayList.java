package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SerializeArrayList {
	
	public ArrayList<String> stringToArray(String commalist) {
		try {
			ArrayList<String> array = new ArrayList<String>();
			if (commalist.indexOf(",") == 0) {
				commalist = commalist.substring(1, commalist.length());
			}
			if (!commalist.substring(commalist.length() - 1, commalist.length()).equalsIgnoreCase(",")) {
				commalist = commalist + ",";
			}
			while (commalist.contains(",")) {
				array.add(commalist.substring(0, commalist.indexOf(",")));
				if (commalist.indexOf(",") == commalist.lastIndexOf(",")) {
					break;
				}
				commalist = commalist.substring(commalist.indexOf(",") + 1, commalist.length());
			}
			return array;
		} catch (Exception e) {
			ArrayList<String> array = new ArrayList<String>();
			return array;
		}
	}
	public ArrayList<Double> doubleToArray(String commalist) {
		try {
			ArrayList<Double> array = new ArrayList<Double>();
			if (commalist.indexOf(",") == 0) {
				commalist = commalist.substring(1, commalist.length());
			}
			if (!commalist.substring(commalist.length() - 1, commalist.length()).equalsIgnoreCase(",")) {
				commalist = commalist + ",";
			}
			while (commalist.contains(",")) {
				array.add(Double.parseDouble(commalist.substring(0, commalist.indexOf(","))));
				if (commalist.indexOf(",") == commalist.lastIndexOf(",")) {
					break;
				}
				commalist = commalist.substring(commalist.indexOf(",") + 1, commalist.length());
			}
			return array;
		} catch (Exception e) {
			ArrayList<Double> array = new ArrayList<Double>();
			return array;
		}
	}
	public ArrayList<Integer> intToArray(String commalist) {
		try {
			ArrayList<Integer> array = new ArrayList<Integer>();
			if (commalist.indexOf(",") == 0) {
				commalist = commalist.substring(1, commalist.length());
			}
			if (!commalist.substring(commalist.length() - 1, commalist.length()).equalsIgnoreCase(",")) {
				commalist = commalist + ",";
			}
			while (commalist.contains(",")) {
				array.add(Integer.parseInt(commalist.substring(0, commalist.indexOf(","))));
				if (commalist.indexOf(",") == commalist.lastIndexOf(",")) {
					break;
				}
				commalist = commalist.substring(commalist.indexOf(",") + 1, commalist.length());
			}
			return array;
		} catch (Exception e) {
			ArrayList<Integer> array = new ArrayList<Integer>();
			return array;
		}
	}
	
	
	
	public String stringArrayToString(ArrayList<String> array) {
		String string = "";
		int c = 0;
		while (c < array.size()) {
			string = string + array.get(c) + ",";
			c++;
		}
		return string;
	}
	public String doubleArrayToString(ArrayList<Double> array) {
		String string = "";
		int c = 0;
		while (c < array.size()) {
			string = string + array.get(c) + ",";
			c++;
		}
		return string;
	}
	public String intArrayToString(ArrayList<Integer> array) {
		String string = "";
		int c = 0;
		while (c < array.size()) {
			string = string + array.get(c) + ",";
			c++;
		}
		return string;
	}
	
	
	
	
	public ArrayList<String> explode(String string, String delimiter) {
		ArrayList<String> array = new ArrayList<String>();
		if (string == null || delimiter == null || !string.contains(delimiter)) {return array;}
		if (string.indexOf(delimiter) == 0) {string = string.substring(1, string.length());}
		if (!string.substring(string.length() - 1, string.length()).equalsIgnoreCase(delimiter)) {string += delimiter;}
		while (string.contains(delimiter)) {
			array.add(string.substring(0, string.indexOf(delimiter)));
			if (string.indexOf(delimiter) == string.lastIndexOf(delimiter)) {break;}
			string = string.substring(string.indexOf(delimiter) + 1, string.length());
		}
		return array;
	}
	
	public String implode(ArrayList<String> array, String delimiter) {
		if (array == null || delimiter == null) {return "";}
		String string = "";
		for (String cs:array) {
			string += cs + delimiter;
		}
		return string;
	}
	
	
	
	
	public HashMap<String,String> explodeMap(String string) {
		HashMap<String,String> map = new HashMap<String,String>();
		if (string == null || !string.contains(",")) {return map;}
		if (!string.substring(string.length() - 1, string.length()).equalsIgnoreCase(";")) {string += ";";}
		while (string.contains(";")) {
			String mapEntry = string.substring(0, string.indexOf(";"));
			String mapKey = mapEntry.substring(0, mapEntry.indexOf(","));
			String mapValue = mapEntry.substring(mapEntry.indexOf(",") + 1, mapEntry.length());
			map.put(mapKey, mapValue);
			if (string.indexOf(";") == string.lastIndexOf(";")) {break;}
			string = string.substring(string.indexOf(";") + 1, string.length());
		}
		return map;
	}
	public String implodeMap(HashMap<String,String> map) {
		if (map == null) {return "";}
		String string = "";
		for (Map.Entry<String,String> entry : map.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    string += (key + "," + value + ";");
		}
		return string;
	}
	
	
	public HashMap<String,Integer> explodeIntMap(String string) {
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		try {
			if (string == null || !string.contains(",")) {return map;}
			if (!string.substring(string.length() - 1, string.length()).equalsIgnoreCase(";")) {string += ";";}
			while (string.contains(";")) {
				String mapEntry = string.substring(0, string.indexOf(";"));
				String mapKey = mapEntry.substring(0, mapEntry.indexOf(","));
				Integer mapValue = Integer.parseInt(mapEntry.substring(mapEntry.indexOf(",") + 1, mapEntry.length()));
				map.put(mapKey, mapValue);
				if (string.indexOf(";") == string.lastIndexOf(";")) {break;}
				string = string.substring(string.indexOf(";") + 1, string.length());
			}
			return map;
		} catch (Exception e) {
			return new HashMap<String,Integer>();
		}
		
	}
	public String implodeIntMap(HashMap<String,Integer> map) {
		if (map == null) {return "";}
		String string = "";
		for (Map.Entry<String,Integer> entry : map.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    string += (key + "," + value + ";");
		}
		return string;
	}



}
