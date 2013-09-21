package regalowl.hyperconomy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainPage extends HttpServlet {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 699465359999143309L;
	private HyperConomy hc;
	private String page = "Loading...";
	//private String mainPage;
	
	
	@SuppressWarnings("deprecation")
	public MainPage() {
		hc = HyperConomy.hc;
		//try {
			//InetAddress addr = InetAddress.getLocalHost();
			//mainPage = addr.getAddress() + ":" + hc.s().getPort() + "/";
		//} catch (UnknownHostException e1) {
		//	new HyperError(e1);
		//}
		page = buildLoadPage();
		
		hc.getServer().getScheduler().scheduleAsyncRepeatingTask(hc, new Runnable() {
			public void run() {
				try {
					page = buildPage();
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
    
    
    
    
    
    
    private String buildPage() {
		
		String page = "";
		if (!hc.fullLock() && hc.enabled()) {
			ArrayList<Shop> shops = hc.getEconomyManager().getShops();

			Collections.sort(shops);

			
			
			page += "<html>\n";
			page += "<head>\n";
			page += "<script type='text/javascript'>\n";
			page += "</script>\n";
			page += "<style>\n";
			page += "* {font-family:"+hc.s().getFont()+";font-size:"+(hc.s().getFontSize()*2)+"px;color:" + hc.s().getFontColor() + ";}\n";
			page += "body {background:" + hc.s().getBackgroundColor() +  ";}\n";
			page += "td {vertical-align:top;text-align:center;padding: 3px;border:1px solid " + hc.s().getBorderColor() + ";background:" + hc.s().getTableDataColor() + ";}\n";
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
			page += "Shops\n";
			page += "</TH>\n";
			page += "</TR>\n";

			

			if (shops != null && shops.size() > 0) {

				for (Shop s : shops) {
					if (!hc.enabled()) {
						return "";
					}
					page += "<TR>\n";
					page += "<TD>\n";
					page += "<a href=\"" + s.getName() + "\" style=\"display:block;text-decoration:none;\">" + s.getDisplayName() + "</a>\n";
					page += "</TD>\n";
					page += "</TR>\n";
				}
			}

			page += "</TABLE>\n";
			page += "</div>\n";
			page += "</body>\n";
			page += "</html>\n";
		}

		
		return page;
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
