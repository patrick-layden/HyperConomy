package regalowl.hyperconomy;

import static regalowl.hyperconomy.Messages.CURRENCY;

import org.bukkit.entity.Player;

public class FormatString {
	
	
	
	public String formatString(String inputstring, double amount, double price, String name, String economy) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%e",economy+"");
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",CURRENCY);
		return inputstring;
	}
	
	public String formatString(String inputstring, double amount, double price, String name, double tax) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%t",tax+"");
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",CURRENCY);
		return inputstring;
	}
	
	public String formatString(String inputstring, double amount, double price, String name) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",CURRENCY);
		return inputstring;
	}
	
	public String formatString(String inputstring, String name) {
		inputstring = inputstring.replace("%n",name);
		return inputstring;
	}
	
	public String formatString(String inputstring, double value) {
		inputstring = inputstring.replace("%v",value+"");
		return inputstring;
	}
	
	public String formatString(String inputstring, int amount, String name) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%n",name);
		return inputstring;
	}
	
	public String formatString(String inputstring, double amount, String name) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%n",name);
		return inputstring;
	}
	
	public String formatString(String inputstring, int amount, double price, String name, String owner) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%zc",owner);
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",CURRENCY);
		return inputstring;
	}
	
	public String formatString(String inputstring, int amount, double price, String name, Player player) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%y",player.getName());
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",CURRENCY);
		return inputstring;
	}
	
	public String formatString(String inputstring, int amount, double price, String name, String isstatic, String isinitial, Player player) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%y",player.getName());
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",CURRENCY);
		inputstring = inputstring.replace("%za",isstatic);
		inputstring = inputstring.replace("%zb",isinitial);
		return inputstring;
	}
	
	public String formatString(String inputstring, int amount, double price, String name, String isstatic, String isinitial, Player player, String owner) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%y",player.getName());
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",CURRENCY);
		inputstring = inputstring.replace("%za",isstatic);
		inputstring = inputstring.replace("%zb",isinitial);
		inputstring = inputstring.replace("%zc",owner);
		return inputstring;
	}
}
