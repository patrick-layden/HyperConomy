package regalowl.hyperconomy.command;

import java.io.File;



import regalowl.simpledatalib.file.FileTools;
import regalowl.hyperconomy.HyperConomy;

public class Setlanguage extends BaseCommand implements HyperCommand {



	public Setlanguage(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		FileTools ft = hc.getFileTools();
		String folderpath = hc.getSimpleDataLib().getStoragePath() + File.separator + "Languages";
		ft.makeFolder(folderpath);
		try {
			if (args.length == 1) {
				String language = args[0].toLowerCase();
				String filepath = folderpath + File.separator + language + ".hl";
				if (L.languageSupported(language) || ft.fileExists(filepath)) {
					language = L.fixLanguage(language);
					hc.getConf().set("language", language);
					language = L.buildLanguageFile(false);
					data.addResponse(L.f(L.get("LANGUAGE_LOADED"), language));
				} else {
					data.addResponse(L.get("LANGUAGE_NOT_FOUND"));
				}
			} else if (args.length == 2 && args[1].equalsIgnoreCase("o")) {
				String language = args[0].toLowerCase();
				if (L.languageSupported(language)) {
					language = L.fixLanguage(language);
					hc.getConf().set("language", language);
					language = L.buildLanguageFile(true);
					data.addResponse(L.f(L.get("LANGUAGE_LOADED"), language));
				} else {
					data.addResponse(L.get("LANGUAGE_NOT_FOUND"));
				}
			} else {
				data.addResponse(L.get("SETLANGUAGE_INVALID"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("SETLANGUAGE_INVALID"));
		}
		return data;
	}
	
	
}
