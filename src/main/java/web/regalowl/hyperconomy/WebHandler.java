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
	private ServletContextHandler context;
	private ArrayList<ShopPage> shopPages = new ArrayList<ShopPage>();
	private Shop s;

	WebHandler() {
		hc = HyperConomy.hc;
	}

	public void startServer() {
		if (!hc.s().useWebPage()) {
			return;
		}
		try {
			hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
				public void run() {
					System.setProperty("org.eclipse.jetty.LEVEL", "WARN");
					server = new Server(hc.s().getPort());

					context = new ServletContextHandler(ServletContextHandler.SESSIONS);
					context.setContextPath("/");
					server.setHandler(context);

					// context.addServlet(new ServletHolder(new HyperWebAPI()),
					// "/API/*");
					context.addServlet(new ServletHolder(new MainPage()), "/");
					for (Shop s : hc.getEconomyManager().getShops()) {
						ShopPage sp = new ShopPage(s);
						shopPages.add(sp);
						context.addServlet(new ServletHolder(sp), "/" + s.getName() + "/*");
					}

					try {
						server.start();
						server.join();
					} catch (Exception e) {
						new HyperError(e);
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
		if (!hc.s().useWebPage()) {
			return;
		}
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
	
	public void addShop(Shop shop) {
		if (!hc.s().useWebPage()) {
			return;
		}
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
		if (updateTask != null) {
			updateTask.cancel();
		}
		if (context != null) {
			try {
				context.stop();
				if (!context.isStopped()) {
					new HyperError("Context failed to stop.");
				}
			} catch (Exception e) {
				new HyperError(e);
			}
		}
		if (server != null) {
			try {
				server.stop();
				if (!server.isStopped()) {
					new HyperError("Server failed to stop.");
				}
			} catch (Exception e) {
				new HyperError(e);
			}
		}
	}
	
	public Server getServer() {
		return server;
	}


}