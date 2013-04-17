package regalowl.hyperconomy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShopPage extends HttpServlet {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 699465359999143309L;
	private HyperConomy hc;
	private Calculation calc;
	private History hist;
	private HyperWebStart hws;
	private Shop s;
	private String page = "Please wait, the price page is loading...  Refresh your page in a few seconds.";
	
	
	@SuppressWarnings("deprecation")
	public ShopPage(HyperWebStart hyperws, Shop shop) {
		hc = HyperConomy.hc;
		calc = hc.getCalculation();
		hist = hc.getHistory();
		hws = hyperws;
		s = shop;
		hc.getServer().getScheduler().scheduleAsyncRepeatingTask(hc, new Runnable() {
			public void run() {
				try {
					page = buildPage(hws.getPageEconomy());
				} catch (Exception e) {
					new HyperError(e);
				}
			}
		}, 400L, 6000L);
		

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(page);
	}
    
    
    
    
    
    
    private String buildPage(String economy) {
		
		String page = "";
		if (!hc.fullLock() && hc.enabled()) {
			ArrayList<HyperObject> objects = s.getAvailableObjects();
			Collections.sort(objects);
			
			
			HashMap<HyperObject, String> hour = hist.getPercentChange(economy, 1);
			HashMap<HyperObject, String> sixHours = hist.getPercentChange(economy, 6);
			HashMap<HyperObject, String> day = hist.getPercentChange(economy, 24);
			HashMap<HyperObject, String> threeDay = hist.getPercentChange(economy, 72);
			HashMap<HyperObject, String> week = hist.getPercentChange(economy, 168);
			
			
			page += "<html>\n";
			page += "<head>\n";
			page += "<script type='text/javascript'>\n";
			page += "</script>\n";
			page += "<style>\n";
			page += "* {font-family:verdana;font-size:12px;color:" + hws.getFontColor() + ";}\n";
			page += "body {background:" + hws.getBackgroundColor() + ";}\n";
			page += "td {vertical-align:top;border:1px solid " + hws.getBorderColor() + ";}\n";
			page += "td.red {vertical-align:top;border:1px solid " + hws.getBorderColor() + ";background:" + hws.getDecreaseColor() + ";}\n";
			page += "td.green {vertical-align:top;border:1px solid " + hws.getBorderColor() + ";background:" + hws.getIncreaseColor() + ";}\n";
			page += "th {border:1px solid " + hws.getBorderColor() + ";padding:3px;cursor:pointer;}\n";
			page += "th.header {background:" + hws.getHeaderColor() + ";}\n";
			page += "tr:hover {background:" + hws.getHighlightColor() + ";}\n";
			page += "td:hover {background:" + hws.getHighlightColor() + ";}\n";
			page += "</style>\n";
			
			
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
			
			for (HyperObject ho:objects) {
				if (!hc.enabled()) {
					return "";
				}
				
				String type = "";
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
				page += ho.getName() + "\n";
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

				if (hws.getUseHistory()) {
					String pc = hour.get(ho);
					String iclass = "";
					if (pc.indexOf("-") != -1) {
						iclass = "red";
					} else if (pc.indexOf("?") != -1 || pc.equalsIgnoreCase("0.0")) {
						iclass = "none";
					} else {
						iclass = "green";
					}
					page += "<TD " + "class='" + iclass + "'>\n";
					page += hour.get(ho) + "%\n";
					page += "</TD>\n";
					
					pc = sixHours.get(ho);
					iclass = "";
					if (pc.indexOf("-") != -1) {
						iclass = "red";
					} else if (pc.indexOf("?") != -1 || pc.equalsIgnoreCase("0.0")) {
						iclass = "none";
					} else {
						iclass = "green";
					}
					page += "<TD " + "class='" + iclass + "'>\n";
					page += sixHours.get(ho) + "%\n";
					page += "</TD>\n";
					
					pc = day.get(ho);
					iclass = "";
					if (pc.indexOf("-") != -1) {
						iclass = "red";
					} else if (pc.indexOf("?") != -1 || pc.equalsIgnoreCase("0.0")) {
						iclass = "none";
					} else {
						iclass = "green";
					}
					page += "<TD " + "class='" + iclass + "'>\n";
					page += day.get(ho) + "%\n";
					page += "</TD>\n";
					
					pc = threeDay.get(ho);
					iclass = "";
					if (pc.indexOf("-") != -1) {
						iclass = "red";
					} else if (pc.indexOf("?") != -1 || pc.equalsIgnoreCase("0.0")) {
						iclass = "none";
					} else {
						iclass = "green";
					}
					page += "<TD " + "class='" + iclass + "'>\n";
					page += threeDay.get(ho) + "%\n";
					page += "</TD>\n";
					
					pc = week.get(ho);
					iclass = "";
					if (pc.indexOf("-") != -1) {
						iclass = "red";
					} else if (pc.indexOf("?") != -1 || pc.equalsIgnoreCase("0.0")) {
						iclass = "none";
					} else {
						iclass = "green";
					}
					page += "<TD " + "class='" + iclass + "'>\n";
					page += week.get(ho) + "%\n";
					page += "</TD>\n";
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
