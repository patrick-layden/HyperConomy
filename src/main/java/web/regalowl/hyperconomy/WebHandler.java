package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.scheduler.BukkitTask;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebHandler {

	private HyperConomy hc;
	private BukkitTask updateTask;
	private Server server;
	private ShopFactory sf;
	private ServletContextHandler context;
	private ArrayList<ShopPage> shopPages = new ArrayList<ShopPage>();
	private ServerShop s;

	WebHandler() {
		hc = HyperConomy.hc;
		sf = hc.getShopFactory();
		startServer();
	}

	private void startServer() {
		try {
			hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
				public void run() {
					System.setProperty("org.eclipse.jetty.LEVEL", "WARN");
					server = new Server(hc.s().getPort());

					context = new ServletContextHandler(ServletContextHandler.SESSIONS);
					context.setContextPath("/");
					server.setHandler(context);

					if (hc.s().useWebPage()) {
						//context.addServlet(new ServletHolder(new HyperWebAPI()), "/API/*");
						context.addServlet(new ServletHolder(new MainPage()), "/");
						for (ServerShop s : sf.getShops()) {
							ShopPage sp = new ShopPage(s);
							shopPages.add(sp);
							context.addServlet(new ServletHolder(sp), "/" + s.getName() + "/*");
						}
					}

					try {
						server.start();
						server.join();
					} catch (Exception e) {
						endServer();
					}

				}
			});
			updateTask = hc.getServer().getScheduler().runTaskTimerAsynchronously(hc, new Runnable() {
				public void run() {
					try {
						for (ShopPage sp:shopPages) {
							sp.updatePage();
						}
					} catch (Exception e) {
						new HyperError(e);
					}
				}
			}, 400L, 6000L);
		} catch (Exception e) {
			new HyperError(e);
		}
	}
	
	
	public void updatePages() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				try {
					for (ShopPage sp:shopPages) {
						sp.updatePage();
					}
				} catch (Exception e) {
					new HyperError(e);
				}
			}
		});
	}
	
	public void addShop(ServerShop shop) {
		s = shop;
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				ShopPage sp = new ShopPage(s);
				shopPages.add(sp);
				context.addServlet(new ServletHolder(sp), "/" + s.getName() + "/*");
			}
		});
	}
	
	

	public void endServer() {
		updateTask.cancel();
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				new HyperError(e);
			}
		}
	}

}