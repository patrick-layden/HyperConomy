package regalowl.hyperconomy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class HyperError {
	
	private HyperConomy hc;
	private Exception e;
	private String info;
	private int errornumber;
	
	
	
	HyperError(Exception ex, String infor) {
		e = ex;
		info = infor;
		hc = HyperConomy.hc;
		FileConfiguration conf = hc.getYaml().getConfig();
		errornumber = conf.getInt("config.error-count");
		conf.set("config.error-count", errornumber + 1);
		handleError();
	}
	
	
	HyperError(Exception ex) {
		e = ex;
		info = "";
		hc = HyperConomy.hc;
		FileConfiguration conf = hc.getYaml().getConfig();
		errornumber = conf.getInt("config.error-count");
		conf.set("config.error-count", errornumber + 1);
		handleError();
	}
	
	
	
	private void handleError() {
		hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
			public void run() {
				FileTools ft = new FileTools();
				String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "errors";
				ft.makeFolder(path);
				path = path + File.separator + errornumber;
				ft.makeFolder(path);
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(new File(path + File.separator + "stacktrace.txt"));
					PrintStream ps = new PrintStream(fos);  
					e.printStackTrace(ps); 
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				SQLFunctions sf = hc.getSQLFunctions();
				info = ft.getTimeStamp() + "\n\n" + "UseSQL='" + hc.useSQL() + "', DataBuilt='" + sf.dataBuilt() + "', SQLLoaded='" + sf.sqlLoaded() + "'\n\n" + info;
				ft.writeStringToFile(info, path + File.separator + "info.txt");
				Bukkit.broadcast(ChatColor.DARK_RED + "An error has occurred. [#" + errornumber + "] Check the errors folder for more info.", "hyperconomy.error");
			}
		}, 0L);
	}
	
	
}
