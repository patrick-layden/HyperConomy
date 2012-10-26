package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class LanguageFile {
	
	private FileTools ft;
	private HashMap<String, String> language = new HashMap<String, String>();
	private ArrayList<String> supportedLanguages = new ArrayList<String>();
	private Logger log = Logger.getLogger("Minecraft");
	
	LanguageFile() {		
		supportedLanguages.add("english");
		
		ft = new FileTools();
		String lang = HyperConomy.hc.getYaml().getConfig().getString("config.language");
		lang = lang.toLowerCase().replace(" ", "").replace("\"", "").replace("'", "");
		boolean validLanguage = false;
		for (int i = 0; i < supportedLanguages.size(); i++) {
			if (supportedLanguages.get(i).contains(lang)) {
				lang = supportedLanguages.get(i);
				validLanguage = true;
				break;
			}
		}
		
		if (!validLanguage) {
			log.severe("Unsupported language specified, defaulting to English.");
			lang = "english";
		}
		
		String folderpath = Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder() + File.separator + "Languages";
		String filepath = folderpath + File.separator + lang + ".txt";
		if (ft.fileExists(filepath)) {
			buildHashMap(filepath);
		} else {
			ft.makeFolder(folderpath);
			ft.copyFileFromJar("Languages/" + lang + ".txt", filepath);
			buildHashMap(filepath);
		}
	}
	
	
	
	private void buildHashMap(String filepath) {
		ArrayList<String> lines = ft.getStringArrayFromFile(filepath);
		for (int i = 0; i < lines.size(); i++) {
			String name = lines.get(i).substring(0, lines.get(i).indexOf(":"));
			String text = lines.get(i).substring(lines.get(i).indexOf("\"") + 1, lines.get(i).lastIndexOf("\""));
			language.put(name, text);
		}
	}
	
	
	public String getConstant(String constant) {
		if (language.get(constant) != null) {
			return language.get(constant);
		} else {
			return "Language File Error!";
		}
	}
	
}
