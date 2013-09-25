package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LanguageFile {
	
	private FileTools ft;
	private HashMap<String, String> language = new HashMap<String, String>();
	private HashMap<String, String> languageBackup = new HashMap<String, String>();
	private ArrayList<String> supportedLanguages = new ArrayList<String>();
	private HashMap<String, String> languageConversions = new HashMap<String, String>();

	
	LanguageFile() {		
		languageConversions.put("french", "frFR");
		languageConversions.put("français", "frFR");
		languageConversions.put("le français", "frFR");
		languageConversions.put("english", "enUS");
		languageConversions.put("russian", "ruRU");
		languageConversions.put("ру́сский язы́к", "ruRU");
		languageConversions.put("russkij jazyk", "ruRU");
		
		
		supportedLanguages.add("enUS");
		supportedLanguages.add("frFR");
		supportedLanguages.add("ruRU");
		buildLanguageFile(false);
	}
	
	
	
	
	
	
	public String buildLanguageFile(boolean overwrite) {
		ft = new FileTools();
		String lang = HyperConomy.hc.gYH().gFC("config").getString("config.language");
		if (lang == null) {
			lang = "enUS";
		}
		lang = lang.replace(" ", "").replace("\"", "").replace("'", "");
		boolean validLanguage = false;
		for (int i = 0; i < supportedLanguages.size(); i++) {
			if (supportedLanguages.get(i).contains(lang)) {
				lang = supportedLanguages.get(i);
				validLanguage = true;
				break;
			}
		}
		
		String folderpath = Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder() + File.separator + "Languages";
		ft.makeFolder(folderpath);
		String filepath = folderpath + File.separator + lang + ".hl";
		String backuppath = folderpath + File.separator + "enUS_b.hl";
		try {
		ft.copyFileFromJar("Languages/enUS.hl", backuppath);
		} catch (Exception e) {
			HyperConomy.hc.gDB().writeError(e);
		}
		buildBackupHashMap(backuppath);
		
		if (ft.fileExists(filepath) && !overwrite) {
			buildHashMap(filepath);
		} else {
			if (!validLanguage) {
				lang = "enUS";
			}
			filepath = folderpath + File.separator + lang + ".hl";
			ft.makeFolder(folderpath);
			if (!ft.fileExists(filepath) || overwrite) {
				if (ft.fileExists(filepath)) {
					ft.deleteFile(filepath);
				}
				ft.copyFileFromJar("Languages/" + lang + ".hl", filepath);
			}

			buildHashMap(filepath);
		}
		return lang;
	}
	
	
	public void updateBackup() {
		String folderpath = Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder() + File.separator + "Languages";
		String backuppath = folderpath + File.separator + "enUS_b.hl";
		ft.copyFileFromJar("Languages/enUS.hl", backuppath);
		languageBackup.clear();
		buildBackupHashMap(backuppath);
	}
	
	
	
	
	private void buildHashMap(String filepath) {
		try {
			ArrayList<String> lines = ft.getStringArrayFromFile(filepath);
			for (int i = 0; i < lines.size(); i++) {
				String name = lines.get(i).substring(0, lines.get(i).indexOf(":"));
				String text = lines.get(i).substring(lines.get(i).indexOf(":") + 1, lines.get(i).length());
				if (text.startsWith(" ")) {
					text = text.substring(1, text.length());
				}
				text = formatMessage(text);
				language.put(name, text);
			}
			language.put("CC", "\u00A7");
		} catch (Exception e) {
			Logger log = Logger.getLogger("Minecraft");
			log.severe("[HyperConomy]You likely have an error in your language file...using a backup.");
		}
	}
	
	
	private void buildBackupHashMap(String filepath) {
		ArrayList<String> lines = ft.getStringArrayFromFile(filepath);
		for (int i = 0; i < lines.size(); i++) {
			String name = lines.get(i).substring(0, lines.get(i).indexOf(":"));
			String text = lines.get(i).substring(lines.get(i).indexOf(":") + 1, lines.get(i).length());
			if (text.startsWith(" ")) {
				text = text.substring(1, text.length());
			}
			text = formatMessage(text);
			languageBackup.put(name, text);
		}
		languageBackup.put("CC", "\u00A7");
	}
	

	public String get(String key) {
		String message = "";
		if (language.containsKey(key)) {
			message = language.get(key);
		} else {
			if (languageBackup.containsKey(key)) {
				message = languageBackup.get(key);
			} else {
				message = "[" + key + "] NOT FOUND";
			}
		}
		return message;
	}
	
	
	public boolean languageSupported(String language) {
		if (languageConversions.containsKey(language.toLowerCase())) {
			return true;
		}
		if (supportedLanguages.contains(language.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	public String fixLanguage(String language) {
		language = language.toLowerCase();
		if (languageConversions.containsKey(language)) {
			return languageConversions.get(language);
		} 
		for (String lang : supportedLanguages) {
			if (lang.equalsIgnoreCase(language)) {
				return lang;
			}
		}
		return language;
	}
	
	public String gC(boolean fullName) {
		String currency = get("CURRENCY");
		if (currency == null) {currency = "$";}
		if (!fullName && currency.length() > 1) {
			currency = currency.trim();
			currency = currency.substring(0, 1);
		}
		return currency;
	}
	
	public String fC(String amount) {
		String formatted = gC(true) + amount;
		if (HyperConomy.hc.gYH().gFC("config").getBoolean("config.show-currency-symbol-after-price")) {
			formatted = amount + gC(true);
		}
		return formatted;
	}
	public String fC(double amount) {
		String formatted = gC(true) + amount;
		if (HyperConomy.hc.gYH().gFC("config").getBoolean("config.show-currency-symbol-after-price")) {
			formatted = amount + gC(true);
		}
		return formatted;
	}
	public String fCS(double amount) {
		String formatted = gC(false) + amount;
		if (HyperConomy.hc.gYH().gFC("config").getBoolean("config.show-currency-symbol-after-price")) {
			formatted = amount + gC(false);
		}
		return formatted;
	}
	public String fCS(String amount) {
		String formatted = gC(false) + amount;
		if (HyperConomy.hc.gYH().gFC("config").getBoolean("config.show-currency-symbol-after-price")) {
			formatted = amount + gC(false);
		}
		return formatted;
	}
	
	public String formatMessage(String message) {
		message = message.replace("&0", ChatColor.BLACK+"");
		message = message.replace("&1", ChatColor.DARK_BLUE+"");
		message = message.replace("&2", ChatColor.DARK_GREEN+"");
		message = message.replace("&3", ChatColor.DARK_AQUA+"");
		message = message.replace("&4", ChatColor.DARK_RED+"");
		message = message.replace("&5", ChatColor.DARK_PURPLE+"");
		message = message.replace("&6", ChatColor.GOLD+"");
		message = message.replace("&7", ChatColor.GRAY+"");
		message = message.replace("&8", ChatColor.DARK_GRAY+"");
		message = message.replace("&9", ChatColor.BLUE+"");
		message = message.replace("&a", ChatColor.GREEN+"");
		message = message.replace("&b", ChatColor.AQUA+"");
		message = message.replace("&c", ChatColor.RED+"");
		message = message.replace("&d", ChatColor.LIGHT_PURPLE+"");
		message = message.replace("&e", ChatColor.YELLOW+"");
		message = message.replace("&f", ChatColor.WHITE+"");
		message = message.replace("&k", ChatColor.MAGIC+"");
		message = message.replace("&l", ChatColor.BOLD+"");
		message = message.replace("&m", ChatColor.STRIKETHROUGH+"");
		message = message.replace("&n", ChatColor.UNDERLINE+"");
		message = message.replace("&o", ChatColor.ITALIC+"");
		message = message.replace("&r", ChatColor.RESET+"");
		return message;
	}
	
	
	
	
	
	public String f(String inputstring, int value, int value2) {
		inputstring = inputstring.replace("%v",value+"");
		inputstring = inputstring.replace("%w",value2+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	
	public String f(String inputstring, String name, String extra) {
		inputstring = inputstring.replace("%e",extra);
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, String name, String extra, int i) {
		inputstring = inputstring.replace("%e",extra);
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%i",i+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, double amount, double price, String name, String extra) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%e",extra+"");
		inputstring = inputstring.replace("%zc",extra);
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, double amount, double price, String name, double tax) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%t",tax+"");
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, double amount, double price, String name) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, String name) {
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, double value) {
		inputstring = inputstring.replace("%v",value+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, int value) {
		inputstring = inputstring.replace("%v",value+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, int amount, String name) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, double amount, String name) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%zc",name);
		inputstring = inputstring.replace("%p",amount+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, double value, boolean status) {
		inputstring = inputstring.replace("%s",status+"");
		inputstring = inputstring.replace("%v",value+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}

	public String f(String inputstring, int amount, double price, String name, Player player) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%y",player.getName());
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		return inputstring;
	}
	
	public String f(String inputstring, int amount, double price, String name, String isstatic, String isinitial, Player player) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%y",player.getName());
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		inputstring = inputstring.replace("%za",isstatic);
		inputstring = inputstring.replace("%zb",isinitial);
		return inputstring;
	}
	
	public String f(String inputstring, int amount, double price, String name, String isstatic, String isinitial, Player player, String owner) {
		inputstring = inputstring.replace("%a",amount+"");
		inputstring = inputstring.replace("%y",player.getName());
		inputstring = inputstring.replace("%n",name);
		inputstring = inputstring.replace("%p",price+"");
		inputstring = inputstring.replace("%c",get("CURRENCY"));
		inputstring = inputstring.replace("%za",isstatic);
		inputstring = inputstring.replace("%zb",isinitial);
		inputstring = inputstring.replace("%zc",owner);
		return inputstring;
	}
	
	
}
