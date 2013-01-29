package regalowl.hyperconomy;

import java.util.ArrayList;

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
			array.add(-1234567890.0);
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
			array.add(-1234567890);
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
	
	
	
	
	
	
	
	
	public String stringArrayToStringNL(ArrayList<String> array) {
		String string = "";
		for (String item:array) {
			string += (item + "\n");
		}
		return string;
	}
	
	
	public String stringArrayToStringA(ArrayList<String> array) {
		String string = "";
		for (String item:array) {
			string += (item + "#");
		}
		return string;
	}
	
	
	
	public ArrayList<String> stringToArrayA(String list) {
		try {
			ArrayList<String> array = new ArrayList<String>();
			if (list.indexOf("#") == 0) {
				list = list.substring(1, list.length());
			}
			if (!list.substring(list.length() - 1, list.length()).equalsIgnoreCase("#")) {
				list = list + "#";
			}
			while (list.contains("#")) {
				array.add(list.substring(0, list.indexOf("#")));
				if (list.indexOf("#") == list.lastIndexOf("#")) {
					break;
				}
				list = list.substring(list.indexOf("#") + 1, list.length());
			}
			return array;
		} catch (Exception e) {
			ArrayList<String> array = new ArrayList<String>();
			return array;
		}
	}
	

}
