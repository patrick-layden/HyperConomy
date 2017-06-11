package regalowl.hyperconomy.webpage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectStatus;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.util.History;


public class ShopPage extends HttpServlet implements HyperEventListener {

	private static final long serialVersionUID = 699465359999143309L;
	private HyperConomy_Web hcw;
	private HyperConomy hc;
	private History hist;
	private Shop s;
	private String page = "Loading...  Please wait.  The page will refresh automatically.";
	private ArrayList<TradeObject> modifiedSinceLastUpdate = new ArrayList<TradeObject>();
	
	
	private HashMap<TradeObject, String> hour = null;
	private HashMap<TradeObject, String> sixHours = null;
	private HashMap<TradeObject, String> day = null;
	private HashMap<TradeObject, String> threeDay = null;
	private HashMap<TradeObject, String> week = null;
	private boolean initialLoad = true;

	public ShopPage(Shop shop, HyperConomy_Web hcw) {
		this.hcw = hcw;
		hc = hcw.getHC();
		hist = hc.getHistory();
		s = shop;
		hc.getHyperEventHandler().registerListener(this);
		page = buildLoadPage();
	}
	
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setIntHeader("Refresh", 10);
		response.getWriter().println(page);
		updatePage();
	}

	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof TradeObjectModificationEvent) {
			TradeObjectModificationEvent toe = (TradeObjectModificationEvent)event;
			modifiedSinceLastUpdate.add(toe.getTradeObject());
		}
	}
	
	public void updatePage() {
		if (!initialLoad && modifiedSinceLastUpdate.size() == 0) return;
		
		new Thread(new Runnable() {
			public void run() {
				page = buildPage(s.getEconomy());
			}
		}).start();
	}

	private String buildPage(String economy) {
		try {
			String page = "";
			if (!hc.loaded()) return page;
			if (s == null) return "";
			PlayerShop ps = null;
			if (s instanceof PlayerShop) {
				ps = (PlayerShop) s;
			}
			boolean useHistory = hist.useHistory();
			if (ps != null) {
				useHistory = false;
			}
			ArrayList<TradeObject> objects = s.getTradeableObjects();
			Collections.sort(objects);


			if (useHistory) {
				if (initialLoad) {
					hour = hist.getPercentChange(economy, 1);
					sixHours = hist.getPercentChange(economy, 6);
					day = hist.getPercentChange(economy, 24);
					threeDay = hist.getPercentChange(economy, 72);
					week = hist.getPercentChange(economy, 168);
					initialLoad = false;
				} else {
					for (TradeObject to:modifiedSinceLastUpdate) {
						hour.put(to, hist.getPercentChange(to, 1));
						sixHours.put(to, hist.getPercentChange(to, 6));
						day.put(to, hist.getPercentChange(to, 24));
						threeDay.put(to, hist.getPercentChange(to, 72));
						week.put(to, hist.getPercentChange(to, 168));
					}
					modifiedSinceLastUpdate.clear();
				}
			}
			page += "<html>\n";
			page += "<head>\n";
			page += "<script type='text/javascript'>\n";
			page += "</script>\n";
			page += "<style>\n";
			page += "* {font-family:" + hcw.getFont() + ";font-size:" + hcw.getFontSize() + "px;color:" + hcw.getFontColor() + ";}\n";
			page += "body {background:" + hcw.getBackgroundColor() + ";}\n";
			page += "td {vertical-align:top;border:1px solid " + hcw.getBorderColor() + ";background:" + hcw.getTableDataColor() + ";}\n";
			page += "td.red {vertical-align:top;border:1px solid " + hcw.getBorderColor() + ";background:" + hcw.getDecreaseColor() + ";}\n";
			page += "td.green {vertical-align:top;border:1px solid " + hcw.getBorderColor() + ";background:" + hcw.getIncreaseColor() + ";}\n";
			page += "th {border:1px solid " + hcw.getBorderColor() + ";padding:3px;cursor:pointer;}\n";
			page += "th.header {background:" + hcw.getHeaderColor() + ";}\n";
			page += "tr:hover {background:" + hcw.getHighlightColor() + ";}\n";
			page += "td:hover {background:" + hcw.getHighlightColor() + ";}\n";
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
			page += "Material\n";
			page += "</TH>\n";

			if (useHistory) {
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

			for (TradeObject ho : objects) {
				TradeObjectStatus hos = null;
				if (ho.isShopObject()) {
					hos = ho.getShopObjectStatus();
					if (hos == TradeObjectStatus.NONE) {
						continue;
					}
				}
				if (!hc.loaded()) {
					return "";
				}

				double sellPrice = -1;
				double buyPrice = -1;
				String buyString = "";
				String sellString = "";
				if (ho.getType() == TradeObjectType.ITEM) {
					sellPrice = ho.getSellPrice(1);
					sellPrice -= ho.getSalesTaxEstimate(sellPrice);
					buyPrice = ho.getBuyPrice(1);
					buyPrice += ho.getPurchaseTax(buyPrice);
					buyString = hc.getLanguageFile().fC(CommonFunctions.twoDecimals(buyPrice));
					sellString = hc.getLanguageFile().fC(CommonFunctions.twoDecimals(sellPrice));
				} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
					sellPrice = ho.getSellPrice(EnchantmentClass.DIAMOND);
					sellPrice -= ho.getSalesTaxEstimate(sellPrice);
					buyPrice = ho.getBuyPrice(EnchantmentClass.DIAMOND);
					buyPrice += ho.getPurchaseTax(buyPrice);
					buyString = hc.getLanguageFile().fC(CommonFunctions.twoDecimals(buyPrice));
					sellString = hc.getLanguageFile().fC(CommonFunctions.twoDecimals(sellPrice));
				} else if (ho.getType() == TradeObjectType.EXPERIENCE) {
					sellPrice = ho.getSellPrice(1);
					sellPrice -= ho.getSalesTaxEstimate(sellPrice);
					buyPrice = ho.getBuyPrice(1);
					buyPrice += ho.getPurchaseTax(buyPrice);
					buyString = hc.getLanguageFile().fC(CommonFunctions.twoDecimals(buyPrice));
					sellString = hc.getLanguageFile().fC(CommonFunctions.twoDecimals(sellPrice));
				}
				if (hos != null) {
					if (hos == TradeObjectStatus.BUY) {
						sellString = "N/A";
					} else if (hos == TradeObjectStatus.SELL) {
						buyString = "N/A";
					}
				}
				page += "<TR>\n";
				page += "<TD>\n";
				page += ho.getDisplayName() + "\n";
				page += "</TD>\n";
				page += "<TD>\n";
				page += sellString + "\n";
				page += "</TD>\n";
				page += "<TD>\n";
				page += buyString + "\n";
				page += "</TD>\n";
				page += "<TD>\n";
				page += CommonFunctions.twoDecimals(ho.getStock()) + "\n";
				page += "</TD>\n";
				page += "<TD>\n";

				String material = "N/A";
				if (ho.getType() == TradeObjectType.ITEM) {
					material = ho.getItemStack(1).getMaterial();
				}

				page += material + "\n";
				page += "</TD>\n";

				if (useHistory) {
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

			return page;
		} catch (Exception e) {
			e.printStackTrace();
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
		page += "* {font-family:" + hcw.getFont() + ";font-size:" + hcw.getFontSize() + "px;color:" + hcw.getFontColor() + ";}\n";
		page += "body {background:" + hcw.getBackgroundColor() + ";}\n";
		page += "td {vertical-align:top;border:1px solid " + hcw.getBorderColor() + ";background:" + hcw.getTableDataColor() + ";}\n";
		page += "td.red {vertical-align:top;border:1px solid " + hcw.getBorderColor() + ";background:" + hcw.getDecreaseColor() + ";}\n";
		page += "td.green {vertical-align:top;border:1px solid " + hcw.getBorderColor() + ";background:" + hcw.getIncreaseColor() + ";}\n";
		page += "th {border:1px solid " + hcw.getBorderColor() + ";padding:3px;cursor:pointer;}\n";
		page += "th.header {background:" + hcw.getHeaderColor() + ";}\n";
		page += "tr:hover {background:" + hcw.getHighlightColor() + ";}\n";
		page += "td:hover {background:" + hcw.getHighlightColor() + ";}\n";
		page += "</style>\n";
		page += "</head>\n";
		page += "<body>\n";
		page += "<div align='center' id='results'>\n";
		page += "<TABLE BORDER='0'>\n";
		page += "<TR>\n";
		page += "<TH class='header'>\n";
		page += "Loading...  Please wait.  The page will refresh automatically.\n";
		page += "</TH>\n";
		page += "</TR>\n";
		page += "</TABLE>\n";
		page += "</div>\n";
		page += "</body>\n";
		page += "</html>\n";
		return page;
	}
}
