package regalowl.hyperconomy;

import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.eclipse.jetty.server.Server;




public class HyperWebStart {

	private Logger log = Logger.getLogger("Minecraft");
	private HyperConomy hc;
	private HyperWebStart hws;
	private int serverid;
	private Server server;
	private int port;
	private LanguageFile L;
	
	
	
	HyperWebStart() {
		hc = HyperConomy.hc;
		hws = this;
		L = hc.getLanguageFile();
		
		FileConfiguration conf = hc.getYaml().getConfig();
		useWebPage = conf.getBoolean("config.web-page.use-web-page");
		backgroundColor = "#" + conf.getString("config.web-page.background-color");
		fontColor = "#" + conf.getString("config.web-page.font-color");
		borderColor = "#" + conf.getString("config.web-page.border-color");
		backgroundColor = "#" + conf.getString("config.web-page.background-color");
		increaseColor = "#" + conf.getString("config.web-page.increase-value-color");
		decreaseColor = "#" + conf.getString("config.web-page.decrease-value-color");
		highlightColor = "#" + conf.getString("config.web-page.highlight-row-color");
		headerColor = "#" + conf.getString("config.web-page.header-color");
		tax = conf.getDouble("config.purchasetaxpercent");
		enchanttax = conf.getDouble("config.enchanttaxpercent");
		salestax = conf.getDouble("config.sales-tax-percent");
		initialtax = conf.getDouble("config.initialpurchasetaxpercent");
		statictax = conf.getDouble("config.statictaxpercent");
		currencySymbol = HyperConomy.currency;
		port = conf.getInt("config.web-page.port");
		useHistory = conf.getBoolean("config.store-price-history");
		pageEconomy = conf.getString("config.web-page.web-page-economy");
		
		if (useWebPage) {
			startServer();
	    	log.info(L.get("WEB_PAGE_ENABLED"));
		}
	}
	
	@SuppressWarnings("deprecation")
	private void startServer() {
		try {
    		serverid = hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
    			public void run() {
    			System.setProperty("org.eclipse.jetty.LEVEL", "WARN");
	            server = new Server(port);
	            server.setHandler(new HyperWebPrices(hws));
	            try {
					server.start();
					server.join();
				} catch (Exception e) {
					//SILENCE
				}
            
    			}
    		}, 0L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void endServer() {
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				//SILENCE
			}
		}
		hc.getServer().getScheduler().cancelTask(serverid);
	}


	private boolean useWebPage;
	private String backgroundColor;
	private String fontColor;
	private String borderColor;
	private String increaseColor;
	private String decreaseColor;
	private String highlightColor;
	private String headerColor;
	private double salestax;
	private double tax;
	private double statictax;
	private double enchanttax;
	private double initialtax;
	private String currencySymbol;
	private boolean useHistory;
	private String pageEconomy;
	
	
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public String getFontColor() {
		return fontColor;
	}
	public String getBorderColor() {
		return borderColor;
	}
	public String getIncreaseColor() {
		return increaseColor;
	}
	public String getDecreaseColor() {
		return decreaseColor;
	}
	public String getHighlightColor() {
		return highlightColor;
	}
	public String getHeaderColor() {
		return headerColor;
	}
	public double getSalesTax() {
		return salestax;
	}
	public double getTax() {
		return tax;
	}
	public double getStaticTax() {
		return statictax;
	}
	public double getEnchantTax() {
		return enchanttax;
	}
	public double getInitialTax() {
		return initialtax;
	}
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public boolean getUseHistory() {
		return useHistory;
	}
	public String getPageEconomy() {
		return pageEconomy;
	}
	}