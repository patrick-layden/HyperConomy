package regalowl.hyperconomy.webpage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bukkit.scheduler.BukkitTask;

import regalowl.hyperconomy.shop.Shop;

public class MainPage extends HttpServlet {

	private static final long serialVersionUID = 699465359999143309L;
	private HyperConomy_Web hcw;
	private String page = "Loading...";
	private BukkitTask updateTask;

	public MainPage(HyperConomy_Web hcweb) {
		this.hcw = hcweb;
		page = buildLoadPage();
		
		updateTask = hcw.getServer().getScheduler().runTaskTimerAsynchronously(hcw, new Runnable() {
			public void run() {
				try {
					page = buildPage();
				} catch (Exception e) {
					hcw.getSimpleDataLib().getErrorWriter().writeError(e);
				}
			}
		}, 40L, 6000L);
	}
	
	public void disable() {
		updateTask.cancel();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println(page);
	}
    
    
    
    
    
    
    private String buildPage() {
		
		String page = "";
			ArrayList<Shop> shops = hcw.getHC().getHyperShopManager().getShops();
			Collections.sort(shops);

			page += "<html>\n";
			page += "<head>\n";
			page += "<script type='text/javascript'>\n";
			page += "</script>\n";
			page += "<style>\n";
			page += "* {font-family:"+hcw.getFont()+";font-size:"+(hcw.getFontSize()*2)+"px;color:" + hcw.getFontColor() + ";}\n";
			page += "body {background:" +hcw.getBackgroundColor() +  ";}\n";
			page += "td {vertical-align:top;text-align:center;padding: 3px;border:1px solid " + hcw.getBorderColor() + ";background:" + hcw.getTableDataColor() + ";}\n";
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
			page += "Shops\n";
			page += "</TH>\n";
			page += "</TR>\n";

			

			if (shops != null && shops.size() > 0) {
				for (Shop s : shops) {
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
		

		
		return page;
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
