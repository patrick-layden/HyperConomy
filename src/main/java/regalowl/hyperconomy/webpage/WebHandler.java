package regalowl.hyperconomy.webpage;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.scheduler.BukkitTask;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.shop.Shop;
import regalowl.simpledatalib.event.EventHandler;

public class WebHandler {

	private HyperConomy hc;
	private HyperConomy_Web hcw;
	private BukkitTask serverTask;
	private BukkitTask updateTask;
	private Server server;
	private ServletContextHandler context;
	private ArrayList<ShopPage> shopPages = new ArrayList<ShopPage>();
	private Shop s;
	private AtomicBoolean serverStarted = new AtomicBoolean();

	WebHandler(HyperConomy_Web hcw) {
		this.hcw = hcw;
		hc = hcw.getHC();
		hc.getHyperEventHandler().registerListener(this);
		serverStarted.set(false);
	}
	
	@EventHandler
	public void onShopCreation(Shop s) {
		addShop(s);
	}
	
	
	

	public void startServer() {
		try {
			serverTask = hcw.getServer().getScheduler().runTaskAsynchronously(hcw, new Runnable() {
				public void run() {
					System.setProperty("org.eclipse.jetty.LEVEL", "WARN");
					server = new Server(hcw.getPort());
					context = new ServletContextHandler(ServletContextHandler.SESSIONS);
					context.setContextPath("/");
					server.setHandler(context);
					if (hcw.useWebAPI()) {
						context.addServlet(new ServletHolder(new HyperWebAPI(hcw.getWebAPIPath())), "/"+hcw.getWebAPIPath()+"/*");
					}
					context.addServlet(new ServletHolder(new MainPage(hcw)), "/");
					for (Shop s : hc.getHyperShopManager().getShops()) {
						ShopPage sp = new ShopPage(s, hcw);
						shopPages.add(sp);
						context.addServlet(new ServletHolder(sp), "/" + s.getName());
					}
					try {
						server.start();
						server.join();
					} catch (Exception e) {
						hcw.getSimpleDataLib().getErrorWriter().writeError(e);
					}
					serverStarted.set(true);
				}
			});
			updateTask = hcw.getServer().getScheduler().runTaskTimerAsynchronously(hcw, new Runnable() {
				public void run() {
					try {
						for (ShopPage sp:shopPages) {
							sp.updatePage();
						}
					} catch (Exception e) {
						hcw.getSimpleDataLib().getErrorWriter().writeError(e);
					}
				}
			}, 400L, 6000L);
			hcw.getLog().info("[HyperConomy_Web]Web server enabled.  Running on port " + hcw.getPort() + ".");
		} catch (Exception e) {
			hcw.getSimpleDataLib().getErrorWriter().writeError(e);;
		}
	}
	
	
	public void updatePages() {
		hcw.getServer().getScheduler().runTaskAsynchronously(hcw, new Runnable() {
			public void run() {
				try {
					for (ShopPage sp:shopPages) {
						sp.updatePage();
					}
				} catch (Exception e) {
					hcw.getSimpleDataLib().getErrorWriter().writeError(e);
				}
			}
		});
	}

	
	public void addShop(Shop shop) {
		s = shop;
		hcw.getServer().getScheduler().runTaskAsynchronously(hcw, new Runnable() {
			public void run() {
				ShopPage sp = new ShopPage(s, hcw);
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
					hcw.getSimpleDataLib().getErrorWriter().writeError("Context failed to stop.");
				}
			} catch (Exception e) {
				hcw.getSimpleDataLib().getErrorWriter().writeError(e);
			}
		}
		if (server != null) {
			try {
				server.stop();
				if (!server.isStopped()) {
					hcw.getSimpleDataLib().getErrorWriter().writeError("Server failed to stop.");
				}
			} catch (Exception e) {
				hcw.getSimpleDataLib().getErrorWriter().writeError(e);
			}
		}
		if (serverTask != null) {
			serverTask.cancel();
		}
	}
	
	public Server getServer() {
		return server;
	}
	
	public boolean serverStarted() {
		return serverStarted.get();
	}




}