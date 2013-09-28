package regalowl.hyperconomy;


import java.io.File;
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
	private Shop s;
	private String page = "Loading...";
	private String webFolder;
	

	public ShopPage(Shop shop) {
		hc = HyperConomy.hc;
		calc = hc.getCalculation();
		hist = hc.getHistory();
		s = shop;
		page = buildLoadPage();
		FileTools ft = new FileTools();
		webFolder = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "web" + File.separator;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(page);
	}
    
    
    
    public void updatePage() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				page = buildPage(s.getEconomy());
			}
		});
    }
    
    
	private String buildPage(String economy) {
		try {
			String page = "";
			if (!hc.fullLock() && hc.enabled()) {
				if (s == null) {
					return "";
				}
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
				page += "* {font-family:" + hc.s().getFont() + ";font-size:" + hc.s().getFontSize() + "px;color:" + hc.s().getFontColor() + ";}\n";
				page += "body {background:" + hc.s().getBackgroundColor() + ";}\n";
				page += "td {vertical-align:top;border:1px solid " + hc.s().getBorderColor() + ";background:" + hc.s().getTableDataColor() + ";}\n";
				page += "td.red {vertical-align:top;border:1px solid " + hc.s().getBorderColor() + ";background:" + hc.s().getDecreaseColor() + ";}\n";
				page += "td.green {vertical-align:top;border:1px solid " + hc.s().getBorderColor() + ";background:" + hc.s().getIncreaseColor() + ";}\n";
				page += "th {border:1px solid " + hc.s().getBorderColor() + ";padding:3px;cursor:pointer;}\n";
				page += "th.header {background:" + hc.s().getHeaderColor() + ";}\n";
				page += "tr:hover {background:" + hc.s().getHighlightColor() + ";}\n";
				page += "td:hover {background:" + hc.s().getHighlightColor() + ";}\n";
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

				if (hc.s().getUseHistory()) {
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
				}
				page += "</TR>\n";

				for (HyperObject ho : objects) {
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
					double stax = hc.s().getSalesTax();

					if (type.equalsIgnoreCase("static")) {
						tax = hc.s().getStaticTax();
					} else if (type.equalsIgnoreCase("initial")) {
						if (otype == HyperObjectType.ENCHANTMENT) {
							tax = hc.s().getEnchantTax();
						} else {
							tax = hc.s().getInitialTax();
						}
					} else if (type.equalsIgnoreCase("dynamic")) {
						if (otype == HyperObjectType.ENCHANTMENT) {
							tax = hc.s().getEnchantTax();
						} else {
							tax = hc.s().getTax();
						}
					}
					double scost = ho.getValue(1);
					double bcost = ho.getCost(1);

					page += "<TR>\n";
					/*
					 * page += "<TD>\n";
					 * 
					 * if (ho.getId() == 1) { File sourceimage = new
					 * File(webFolder+ho.getId()+".png"); try { Image image =
					 * ImageIO.read(sourceimage); image. page +=
					 * "<img src=\""+webFolder+ho.getId()+".png\">\n"; } catch
					 * (IOException e) { // TODO Auto-generated catch block
					 * e.printStackTrace(); }
					 * 
					 * }
					 * 
					 * page += "</TD>\n";
					 */
					page += "<TD>\n";
					page += ho.getName() + "\n";
					page += "</TD>\n";
					page += "<TD>\n";
					page += hc.getLanguageFile().fC(calc.twoDecimals(scost - (scost * (stax / 100)))) + "\n";
					page += "</TD>\n";
					page += "<TD>\n";
					page += hc.getLanguageFile().fC(calc.twoDecimals(((bcost * (tax / 100)) + bcost))) + "\n";
					page += "</TD>\n";
					page += "<TD>\n";
					page += calc.twoDecimals(ho.getStock()) + "\n";
					page += "</TD>\n";
					page += "<TD>\n";
					page += ho.getId() + "\n";
					page += "</TD>\n";

					if (hc.s().getUseHistory()) {
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
		} catch (Exception e) {
			return "This page didn't load properly.  Please wait for it to reload.";
		}
	}
    
    
	private String buildLoadPage() {
		String page = "";
		page += "<html>\n";
		page += "<head>\n";
		page += "<script type='text/javascript'>\n";
		page += "</script>\n";
		page += "<style>\n";
		page += "* {font-family:" + hc.s().getFont() + ";font-size:" + hc.s().getFontSize() + "px;color:" + hc.s().getFontColor() + ";}\n";
		page += "body {background:" + hc.s().getBackgroundColor() + ";}\n";
		page += "td {vertical-align:top;border:1px solid " + hc.s().getBorderColor() + ";background:" + hc.s().getTableDataColor() + ";}\n";
		page += "td.red {vertical-align:top;border:1px solid " + hc.s().getBorderColor() + ";background:" + hc.s().getDecreaseColor() + ";}\n";
		page += "td.green {vertical-align:top;border:1px solid " + hc.s().getBorderColor() + ";background:" + hc.s().getIncreaseColor() + ";}\n";
		page += "th {border:1px solid " + hc.s().getBorderColor() + ";padding:3px;cursor:pointer;}\n";
		page += "th.header {background:" + hc.s().getHeaderColor() + ";}\n";
		page += "tr:hover {background:" + hc.s().getHighlightColor() + ";}\n";
		page += "td:hover {background:" + hc.s().getHighlightColor() + ";}\n";
		page += "</style>\n";
		page += "</head>\n";
		page += "<body>\n";
		page += "<div align='center' id='results'>\n";
		page += "<TABLE BORDER='0'>\n";
		page += "<TR>\n";
		page += "<TH class='header'>\n";
		page += "...HyperConomy Loading...\n";
		page += "</TH>\n";
		page += "</TR>\n";
		page += "</TABLE>\n";
		page += "</div>\n";
		page += "</body>\n";
		page += "</html>\n";
		return page;
	}
}
