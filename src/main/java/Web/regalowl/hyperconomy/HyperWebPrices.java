package regalowl.hyperconomy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


public class HyperWebPrices extends AbstractHandler {
	
	private HyperConomy hc;
	private Calculation calc;
	private History hist;
	private HyperWebStart hws;
	private String page = "Please wait, the price page is loading...  Refresh your page in a few seconds.";
	
	/** The WebAPI object */
	private AbstractHandler webAPI;
	
	@SuppressWarnings("deprecation")
	public HyperWebPrices(HyperWebStart hyperws) {
		hc = HyperConomy.hc;
		calc = hc.getCalculation();
		hist = hc.getHistory();
		hws = hyperws;
		hc.getServer().getScheduler().scheduleAsyncRepeatingTask(hc, new Runnable() {
			public void run() {
				try {
					page = buildPage(hws.getPageEconomy());
				} catch (Exception e) {
					new HyperError(e);
				}
			}
		}, 400L, 6000L);
		
		// Initialize the WebAPI
		this.webAPI = null;
		try {
			this.webAPI = (AbstractHandler) Class.forName("regalowl.hyperconomy.HyperWebAPI").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	//Check if the user try to call the API 
    	if (this.webAPI != null) {
    		String[] lParts = baseRequest.getRequestURI().split("/");
        	List<String> lPartList = new ArrayList<String>();
        	for (String lObj : lParts) {
        		if (lObj.trim().length() > 0) {
        			lPartList.add(lObj.trim());
        		}
        	}
        	
        	if (lPartList.size() > 0 && lPartList.get(0).equals("API")) {
        		this.webAPI.handle(target, baseRequest, request, response);
        		return;
        	}
    	}
    	
    	//Else call classic WebSite
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println(page);
        //response.getWriter().println(testpage);
    }
    
	/**
	 * 
	 * 
	 * This function calculates the theoretical value for items, ignoring durability.
	 * 
	 */
    /*
		public double getThreadSafeValue(String name, String playerecon){
			DataHandler sf = hc.getDataFunctions();
			try {
				double cost = 0.0;
				HyperObject ho = sf.getHyperObject(name, playerecon);
				if (ho == null) {
					return 99999999;
				}
				boolean initial = Boolean.parseBoolean(ho.getInitiation());
				boolean isstatic = Boolean.parseBoolean(ho.getIsstatic());
				if (isstatic) {
					double statprice = ho.getStaticprice();
					cost = calc.applyCeilingFloor(name, playerecon, statprice);
					return cost;
				} else {
					double shopstock = 0;
					double value = 0;
					double median = 0;
					double icost = 0;
					shopstock = ho.getStock();
					value = ho.getValue();
					median = ho.getMedian();
					icost = ho.getStartprice();
					double rawcost = ((median * value)/shopstock);
					cost = calc.applyCeilingFloor(name, playerecon, rawcost);
					if (initial == true){
						cost = calc.applyCeilingFloor(name, playerecon, icost);
					}
					if (cost < Math.pow(10, 10)) {
						cost = calc.twoDecimals(cost);
					} else {
						cost = 3235624645000.7;
					}
					return cost;
				}
			} catch (Exception e) {				
				new HyperError(e);
				double cost = 99999999;
				return cost;			
			}
		}
		*/
		
		/**
		 * 
		 * 
		 * This function calculates the theoretical value for items, ignoring durability.
		 * 
		 */
		/*
			public double getThreadSafeCost(String name, String playerecon){
				DataHandler sf = hc.getDataFunctions();
				try {
					double cost = 0.0;
					HyperObject ho = sf.getHyperObject(name, playerecon);
					if (ho == null) {
						return -1;
					}
					boolean initial = Boolean.parseBoolean(ho.getInitiation());
					boolean isstatic = Boolean.parseBoolean(ho.getIsstatic());
					if (isstatic) {
						double statprice = ho.getStaticprice();
						cost = calc.applyCeilingFloor(name, playerecon, statprice);
						return cost;
					} else {
						double shopstock = 0;
						double value = 0;
						double median = 0;
						double icost = 0;
						shopstock = ho.getStock() - 1;
						value = ho.getValue();
						median = ho.getMedian();
						icost = ho.getStartprice();
						double rawcost = ((median * value)/shopstock);
						cost = calc.applyCeilingFloor(name, playerecon, rawcost);
						if (initial == true){
							cost = calc.applyCeilingFloor(name, playerecon, icost);
						}
						if (cost < Math.pow(10, 10)) {
							cost = calc.twoDecimals(cost);
						} else {
							cost = 3235624645000.7;
						}
						
						return cost;
					}
				} catch (Exception e) {				
					new HyperError(e);
					double cost = -1;
					return cost;			
				}
			}
		
		*/
		
		private String buildPage(String economy) {
			
			String page = "";
			if (!hc.fullLock()) {
				DataHandler sf = hc.getDataFunctions();
				ArrayList<String> names = sf.getNames();
				ArrayList<Integer> timevalues = new ArrayList<Integer>();
				timevalues.add(1);
				timevalues.add(6);
				timevalues.add(24);
				timevalues.add(72);
				timevalues.add(168);
				Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
				
				
				page += "<html>\n";
				page += "<head>\n";
				
				page += "<script type='text/javascript'>\n";
				
				/*
				page += "window.onload = setupRefresh();\n";
				page += "function setupRefresh() {\n";
				page += "setTimeout('refreshPage();', 60000);}\n";
				page += "function refreshPage() {\n";
				page += "window.location = location.href;}\n";
				*/
				
				page += "</script>\n";
				
				
				page += "<style>\n";
				
				
				
				page += "* {font-family:verdana;font-size:12px;color:" + hws.getFontColor() + ";}\n";
				page += "body {background:" + hws.getBackgroundColor() + ";}\n";
				page += "td {vertical-align:top;border:1px solid " + hws.getBorderColor() + ";}\n";
				page += "td.red {vertical-align:top;border:1px solid " + hws.getBorderColor() + ";background:" + hws.getDecreaseColor() + ";}\n";
				page += "td.green {vertical-align:top;border:1px solid " + hws.getBorderColor() + ";background:" + hws.getIncreaseColor() + ";}\n";
				//page += "td.blue {vertical-align:top;border:1px solid #D8DFE4;background:#E0E0FC;}\n";
				page += "th {border:1px solid " + hws.getBorderColor() + ";padding:3px;cursor:pointer;}\n";
				page += "th.header {background:" + hws.getHeaderColor() + ";}\n";
				page += "tr:hover {background:" + hws.getHighlightColor() + ";}\n";
				page += "td:hover {background:" + hws.getHighlightColor() + ";}\n";
				page += "</style>\n";
				/*
				page += "* {font-family:verdana;font-size:12px;color:#333333;}\n";
				page += "body {background:#EEEEF6;}\n";
				page += "td {vertical-align:top;border:1px solid #D8DFE4;}\n";
				page += "td.red {vertical-align:top;border:1px solid #D8DFE4;background:#FFA0A0;}\n";
				page += "td.green {vertical-align:top;border:1px solid #D8DFE4;background:#A0FFB0;}\n";
				page += "td.blue {vertical-align:top;border:1px solid #D8DFE4;background:#E0E0FC;}\n";
				page += "th {border:1px solid #D8DFE4;padding:3px;cursor:pointer;}\n";
				page += "tr:hover {background:#B4FAF9;}\n";
				page += "td:hover {background:#B4FAF9;}\n";
				page += "</style>\n";
				*/
				
				page += "</head>\n";
				page += "<body>\n";
				page += "<div align='center' id='results'>\n";
				page += "<TABLE BORDER='0'>\n";
				
				
				page += "<TR>\n";
				page += "<TH class='header'>\n";
				page += "Name\n";
				page += "</TH>\n";
				page += "<TH class='header'>\n";
				page += "Sell\n";
				page += "</TH>\n";
				page += "<TH class='header'>\n";
				page += "Buy\n";
				page += "</TH>\n";
				page += "<TH class='header'>\n";
				page += "Stock\n";
				page += "</TH>\n";
				page += "<TH class='header'>\n";
				page += "ID\n";
				page += "</TH>\n";
				//page += "<TH>\n";
				//page += "Status\n";
				//page += "</TH>\n";
				//page += "<TH>\n";
				//page += "Type\n";
				//page += "</TH>\n";
				
				if (hws.getUseHistory()) {
					page += "<TH class='header'>\n";
					page += "1 Hour\n";
					page += "</TH>\n";
					page += "<TH class='header'>\n";
					page += "6 Hour\n";
					page += "</TH>\n";
					page += "<TH class='header'>\n";
					page += "1 Day\n";
					page += "</TH>\n";
					page += "<TH class='header'>\n";
					page += "3 Days\n";
					page += "</TH>\n";
					page += "<TH class='header'>\n";
					page += "1 Week";
					page += "</TH>\n";
					page += "</TR>\n";
				}
				
				for (int i = 0; i < names.size(); i++) {
					
					
					String type = "";
					HyperObject ho = sf.getHyperObject(names.get(i), economy);
					if (ho == null) {
						continue;
					}
					if (Boolean.parseBoolean(ho.getInitiation())) {
						type = "initial";
					} else {
						type = "dynamic";
					}
					if (Boolean.parseBoolean(ho.getIsstatic())) {
						type = "static";
					}
					HyperObjectType otype = ho.getType();
					
					double tax = 0.0;
					double stax = hws.getSalesTax();
					
					if (type.equalsIgnoreCase("static")) {
						tax = hws.getStaticTax();
					} else if (type.equalsIgnoreCase("initial")) {
						if (otype == HyperObjectType.ENCHANTMENT) {
							tax = hws.getEnchantTax();
						} else {
							tax = hws.getInitialTax();
						}
					} else if (type.equalsIgnoreCase("dynamic")) {
						if (otype == HyperObjectType.ENCHANTMENT) {
							tax = hws.getEnchantTax();
						} else {
							tax = hws.getTax();
						}
					}
					double scost = ho.getValue(1);
					double bcost = ho.getCost(1);
					
					
					
					page += "<TR>\n";
					page += "<TD>\n";
					page += names.get(i) + "\n";
					page += "</TD>\n";
					page += "<TD>\n";
					page += hws.getCurrencySymbol() + calc.twoDecimals(scost - (scost * (stax/100))) + "\n";
					page += "</TD>\n";
					page += "<TD>\n";
					page += hws.getCurrencySymbol() + calc.twoDecimals(((bcost * (tax/100)) + bcost)) + "\n";
					page += "</TD>\n";
					page += "<TD>\n";
					page += ho.getStock() + "\n";
					page += "</TD>\n";
					page += "<TD>\n";
					page += ho.getId() + "\n";
					page += "</TD>\n";
					/*
					page += "<TD>\n";
					page += type + "\n";
					page += "</TD>\n";
					page += "<TD>\n";
					page += otype + "\n";
					page += "</TD>\n";
					*/
					
					if (hws.getUseHistory()) {
						for (int j = 0; j < timevalues.size(); j++) {
							String pc = hist.getPercentChange(ho, timevalues.get(j));
							String iclass = "";
							if (pc.indexOf("-") != -1) {
								iclass = "red";
							} else if (pc.indexOf("?") != -1 || pc.equalsIgnoreCase("0.0")) {
								iclass = "none";
							} else {
								iclass = "green";
							}
							page += "<TD " + "class='" + iclass + "'>\n";
							page += hist.getPercentChange(ho, timevalues.get(j)) + "%\n";
							page += "</TD>\n";
						}
					}
				}

				page += "</TABLE>\n";
				page += "</div>\n";
				page += "</body>\n";
				page += "</html>\n";
			}

			
			return page;
		}
}
