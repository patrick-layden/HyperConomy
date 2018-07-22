package regalowl.hyperconomy.webpage;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.ShopCreationEvent;
import regalowl.hyperconomy.shop.Shop;

public class WebHandler implements HyperEventListener {

	private HyperConomy hc;
	private HyperConomy_Web hcw;
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
	

	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof ShopCreationEvent) {
			ShopCreationEvent hevent = (ShopCreationEvent)event;
			addShop(hevent.getShop());
		}
		
	}

	public void startServer() {
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.setProperty("org.eclipse.jetty.LEVEL", "WARN");
					server = new Server(hcw.getPort());
					context = new ServletContextHandler(ServletContextHandler.SESSIONS);
					context.setContextPath("/");
					server.setHandler(context);
					if (hcw.useWebAPI()) {
						context.addServlet(new ServletHolder(new HyperWebAPI(hc, hcw.getWebAPIPath())), "/"+hcw.getWebAPIPath()+"/*");
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
						hc.gSDL().getErrorWriter().writeError(e);
					}
					serverStarted.set(true);
				}
			}).start();
			hc.getMC().logInfo("[HyperConomy_Web]Web server enabled.  Running on port " + hcw.getPort() + ".");
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);;
		}
	}
	
	
	public void addShop(Shop shop) {
		s = shop;
		new Thread(new Runnable() {
			@Override
			public void run() {
				ShopPage sp = new ShopPage(s, hcw);
				shopPages.add(sp);
				context.addServlet(new ServletHolder(sp), "/" + s.getName() + "/*");
			}
		}).start();
	}
	
	

	public void endServer() {
		if (context != null) {
			try {
				context.stop();
				if (!context.isStopped()) {
					hc.gSDL().getErrorWriter().writeError("Context failed to stop.");
				}
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
			}
		}
		if (server != null) {
			try {
				server.stop();
				if (!server.isStopped()) {
					hc.gSDL().getErrorWriter().writeError("Server failed to stop.");
				}
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
			}
		}
	}
	
	public Server getServer() {
		return server;
	}
	
	public boolean serverStarted() {
		return serverStarted.get();
	}





}