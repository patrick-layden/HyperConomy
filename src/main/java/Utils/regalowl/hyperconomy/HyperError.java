package regalowl.hyperconomy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.bukkit.Bukkit;

public class HyperError {
	
	private HyperConomy hc;
	private Exception e;
	private String info;
	private int errornumber;
	
	
	
	HyperError(String infor) {
		hc = HyperConomy.hc;
		e = null;
		info = infor;
		errornumber = hc.getErrorCount();
		hc.raiseErrorCount();
		handleError();
	}
	
	
	HyperError(Exception ex, String infor) {
		hc = HyperConomy.hc;
		e = ex;
		info = infor;
		errornumber = hc.getErrorCount();
		hc.raiseErrorCount();
		handleError();
	}
	
	
	HyperError(Exception ex) {
		hc = HyperConomy.hc;
		e = ex;
		info = "";
		errornumber = hc.getErrorCount();
		hc.raiseErrorCount();
		handleError();
	}

	
	@SuppressWarnings("deprecation")
	private void handleError() {
		boolean logError = hc.logErrors();
		
		
		if (logError) {
			hc.incrementErrorCount();
			hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
				public void run() {
					FileTools ft = new FileTools();
					String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "errors";
					ft.makeFolder(path);
					path = path + File.separator + errornumber;
					ft.makeFolder(path);
					FileOutputStream fos;
					try {
						if (e != null) {
							fos = new FileOutputStream(new File(path + File.separator + "stacktrace.txt"));
							PrintStream ps = new PrintStream(fos);  
							e.printStackTrace(ps); 
						}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					DataFunctions sf = hc.getDataFunctions();
					info = ft.getTimeStamp() + "\r\n"
					+ "HyperConomy version: " + hc.getServerVersion() + "\r\n"
					+ Bukkit.getName() + " version: " + Bukkit.getServer().getBukkitVersion() + "\r\n"
					+ "UseSQL='" + hc.useSQL() + "'\r\nDataBuilt='" + sf.dataBuilt() + "'\r\nSQLLoaded='" + sf.sqlLoaded()
					+ "'\r\n" + info;
					ft.writeStringToFile(info, path + File.separator + "info.txt");
					LanguageFile L = hc.getLanguageFile();
					new ThreadSafeMessage(L.f(L.get("ERROR_HAS_OCCURRED"), errornumber), "hyperconomy.error", true);
				}
			}, 0L);
		} else {
			LanguageFile L = hc.getLanguageFile();
			new ThreadSafeMessage(L.f(L.get("ERROR_HAS_OCCURRED"), errornumber), "hyperconomy.error", true);
		}
	}
}
