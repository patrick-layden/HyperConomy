package regalowl.hyperconomy;


import org.bukkit.command.CommandSender;

public class RestoreSQL {
	
	private CommandSender sender;
	
	public void restore(CommandSender s) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		SQLWrite sw = hc.getSQLWrite();
		//sw.writeData(statements);
		hc.getDataFunctions().load();
		sender.sendMessage(L.get("SQL_TABLES_IMPORTED"));
	}
	
	
}
