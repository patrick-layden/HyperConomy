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
		errornumber = getErrorCount();
		setErrorCount(errornumber + 1);
		handleError();
	}
	
	
	HyperError(Exception ex, String infor) {
		hc = HyperConomy.hc;
		e = ex;
		info = infor;
		errornumber = getErrorCount();
		setErrorCount(errornumber + 1);
		handleError();
	}
	
	
	HyperError(Exception ex) {
		hc = HyperConomy.hc;
		e = ex;
		info = "";
		errornumber = getErrorCount();
		setErrorCount(errornumber + 1);
		handleError();
	}
	
	
	private int getErrorCount() {
		FileTools ft = new FileTools();
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "errors";
		ft.makeFolder(path);
		path += File.separator + "counter.txt";
		if (ft.fileExists(path)) {
			try {
				return Integer.parseInt(ft.getStringFromFile(path));
			} catch (Exception e) {
				ft.deleteFile(path);
				ft.writeStringToFile("0", path);
				return 0;
			}
		} else {
			ft.writeStringToFile("0", path);
			return 0;
		}
	}
	
	private void setErrorCount(int count) {
		FileTools ft = new FileTools();
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "errors";
		ft.makeFolder(path);
		path += File.separator + "counter.txt";
		ft.deleteFile(path);
		ft.writeStringToFile(count + "", path);
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
					path = path + File.separator + "e" + errornumber;
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
					DataFunctions sf = hc.getSQLFunctions();
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
