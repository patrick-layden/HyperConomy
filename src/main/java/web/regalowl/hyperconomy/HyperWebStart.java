package regalowl.hyperconomy;

import org.bukkit.scheduler.BukkitTask;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HyperWebStart {

	private HyperConomy hc;
	private BukkitTask serverTask;
	private Server server;
	private ShopFactory sf;

	HyperWebStart() {
		hc = HyperConomy.hc;
		sf = hc.getShopFactory();
		startServer();
	}

	private void startServer() {
		try {
			serverTask = hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
				public void run() {
					System.setProperty("org.eclipse.jetty.LEVEL", "WARN");
					server = new Server(hc.s().getPort());

					ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
					context.setContextPath("/");
					server.setHandler(context);

					//Start API
					context.addServlet(new ServletHolder(new HyperWebAPI()), "/API/*");

					if (hc.s().useWebPage()) {
						context.addServlet(new ServletHolder(new MainPage()), "/");
						for (Shop s : sf.getShops()) {
							context.addServlet(new ServletHolder(new ShopPage(s)), "/" + s.getName() + "/*");
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
		} catch (Exception e) {
			new HyperError(e);
		}
	}

	public void endServer() {
		if (server != null) {
			try {
				server.stop();
			} catch (Exception e) {
				new HyperError(e);
			}
		}
		serverTask.cancel();
	}

}