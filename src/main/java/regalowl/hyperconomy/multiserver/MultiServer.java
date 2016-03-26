package regalowl.hyperconomy.multiserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperBankManager;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.account.HyperPlayerManager;
import regalowl.hyperconomy.event.DisableEvent;
import regalowl.hyperconomy.event.HyperBankModificationEvent;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.event.HyperPlayerModificationEvent;
import regalowl.hyperconomy.event.ShopModificationEvent;
import regalowl.hyperconomy.shop.HyperShopManager;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;


public class MultiServer implements HyperEventListener {

	private HyperConomy hc;
	private int listenPort;
	private ArrayList<RemoteAddress> addresses = new ArrayList<RemoteAddress>();
	private int timeout;
	private boolean runServer;
	private boolean syncShops;
	private boolean syncObjects;
	private boolean syncAccounts;
	private long updateInterval;
	private MultiServerTransferObject sendObject;
	private RemoteUpdater remoteUpdater;
	private Timer t = new Timer();
	
	public MultiServer(HyperConomy hc) {
		this.hc = hc;
		if (hc.getConf().getBoolean("multi-server.enable")) {
			try {
				ArrayList<String> ips = CommonFunctions.explode(hc.getConf().getString("multi-server.remote-server-ip-addresses"), ";");
				for (String ip:ips) {
					ArrayList<String> ipport = CommonFunctions.explode(ip, ",");
					addresses.add(new RemoteAddress(ipport.get(0),Integer.parseInt(ipport.get(1))));
				}
			} catch (Exception e) {
				hc.getMC().logSevere("[HyperConomy]'remote-server-ip-addresses' entry in config.yml is invalid.  Regenerate the config or check the wiki for more info.");
				return;
			}
			listenPort = hc.getConf().getInt("multi-server.port");
			timeout = hc.getConf().getInt("multi-server.connection-timeout-ms");
			updateInterval = hc.getConf().getInt("multi-server.update-interval");
			syncShops = hc.getConf().getBoolean("multi-server.sync-shops");
			syncObjects = hc.getConf().getBoolean("multi-server.sync-trade-objects");
			syncAccounts = hc.getConf().getBoolean("multi-server.sync-accounts");
			runServer = true;
			sendObject = new MultiServerTransferObject(MultiServerTransferType.MULTI_SERVER_REQUEST_UPDATE);
			hc.getHyperEventHandler().registerListener(this);
			receiveUpdate();
			remoteUpdater = new RemoteUpdater();
			t.schedule(remoteUpdater, updateInterval, updateInterval);
		}
	}
	
	private class RemoteAddress {
		private String ip;
		private int port;
		public RemoteAddress(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}
	}

	public void disable() {
		runServer = false;
	}
	
	private void receiveUpdate() {
		new Thread(new Runnable() {
			public void run() {
				while (runServer) {
					MultiServerTransferObject transferObject = null;
					ServerSocket serverSocket = null;
					Socket sClientSocket = null;
					try {
						serverSocket = new ServerSocket(listenPort);
						sClientSocket = serverSocket.accept();
						ObjectInputStream input = new ObjectInputStream(sClientSocket.getInputStream());
						transferObject = (MultiServerTransferObject) input.readObject();
						if (transferObject.getType() == MultiServerTransferType.MULTI_SERVER_REQUEST_UPDATE) {
							processHyperObjects(transferObject.getHyperObjects());
							processHyperPlayers(transferObject.getHyperPlayers());
							processShops(transferObject.getShops());
							processBanks(transferObject.getBanks());
						}
						ObjectOutputStream out = new ObjectOutputStream(sClientSocket.getOutputStream());
						out.writeObject(new MultiServerTransferObject(MultiServerTransferType.MULTI_SERVER_UPDATE_SUCCESSFUL));
						out.flush();
						sClientSocket.close();
						serverSocket.close();
					} catch (Exception e) {
						try {
							//hc.getDebugMode().debugWriteError(e);
							if (sClientSocket != null) sClientSocket.close();
							if (serverSocket != null) serverSocket.close();
						} catch (IOException e1) {}
					}
				}
			}
		}).start();
	}

	private void processHyperObjects(ArrayList<TradeObject> objects) {
		if (!syncObjects) return;
		for (TradeObject ho:objects) {
			//System.out.println("Received: " + ho.getDisplayName());
			if (ho == null || ho.getEconomy() == null || ho.getEconomy().equalsIgnoreCase("")) continue;
			HyperEconomy he = hc.getDataManager().getEconomy(ho.getEconomy());
			if (he == null) continue;
			ho.setHyperConomy(hc);
			if (ho.isShopObject()) {
				PlayerShop ps = ho.getShopObjectShop();
				if (ps != null) ps.updateHyperObject(ho);
			} else {
				he.removeObject(ho.getName());
				he.addObject(ho);
			}
		}
	}
	
	private void processShops(ArrayList<Shop> objects) {
		if (!syncShops) return;
		HyperShopManager hsm = hc.getHyperShopManager();
		for (Shop s:objects) {
			//System.out.println("Received: " + s.getDisplayName());
			if (s == null || s.getName() == null || s.getName().equalsIgnoreCase("")) continue;
			if (hsm.shopExists(s.getName())) hsm.removeShop(s.getName());
			s.setHyperConomy(hc);
			hsm.addShop(s);
		}
	}
	
	private void processHyperPlayers(ArrayList<HyperPlayer> objects) {
		if (!syncAccounts) return;
		HyperPlayerManager hpm = hc.getHyperPlayerManager();
		for (HyperPlayer hp:objects) {
			//System.out.println("Received: " + hp.getName());
			if (hp == null || hp.getName() == null || hp.getName().equalsIgnoreCase("")) continue;
			if (hpm.hyperPlayerExists(hp.getName())) hpm.removeHyperPlayer(hpm.getHyperPlayer(hp.getName()));
			hp.setHyperConomy(hc);
			hpm.addHyperPlayer(hp);
		}
	}
	
	private void processBanks(ArrayList<HyperBank> objects) {
		if (!syncAccounts) return;
		HyperBankManager hbm = hc.getHyperBankManager();
		for (HyperBank hb:objects) {
			if (hb == null || hb.getName() == null || hb.getName().equalsIgnoreCase("")) continue;
			if (hbm.hasBank(hb.getName())) hbm.removeHyperBank(hb.getName());
			hb.setHyperConomy(hc);
			hbm.addHyperBank(hb);
		}
	}
	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof TradeObjectModificationEvent) {
			TradeObjectModificationEvent hevent = (TradeObjectModificationEvent) event;
			sendObject.addHyperObject(hevent.getTradeObject());
		} else if (event instanceof HyperPlayerModificationEvent) {
			HyperPlayerModificationEvent hevent = (HyperPlayerModificationEvent) event;
			sendObject.addHyperPlayer(hevent.getHyperPlayer());
		} else if (event instanceof HyperBankModificationEvent) {
			HyperBankModificationEvent hevent = (HyperBankModificationEvent) event;
			sendObject.addBank(hevent.getHyperBank());
		} else if (event instanceof ShopModificationEvent) {
			ShopModificationEvent hevent = (ShopModificationEvent) event;
			sendObject.addShop(hevent.getShop());
		} else if (event instanceof DisableEvent) {
			//DisableEvent hevent = (DisableEvent) event;
			runServer = false;
			remoteUpdater.cancel();
		}
		
	}
	
	
	

	private class RemoteUpdater extends TimerTask {
		@Override
		public void run() {
			if (sendObject.isEmpty()) return;
			for (RemoteAddress ra:addresses) {
				try {
					Socket clientSocket = new Socket();
					clientSocket.connect(new InetSocketAddress(ra.ip, ra.port), timeout);
					ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
					out.writeObject(sendObject);
					out.flush();
					ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
					MultiServerTransferObject response = (MultiServerTransferObject) input.readObject();
					if (!response.getType().equals(MultiServerTransferType.MULTI_SERVER_UPDATE_SUCCESSFUL)) {
						clientSocket.close();
						continue;
					}
					clientSocket.close();
				} catch (Exception e) {
					//hc.getDebugMode().debugWriteMessage("The error below occurred when connecting to ip: " + ra.ip + " and port: " + ra.port);
					//hc.getDebugMode().debugWriteError(e);
					continue;
				}
			}
			sendObject.clear();
		}
	}







	
}

