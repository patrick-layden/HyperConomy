package regalowl.hyperconomy.multiserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.event.EventHandler;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperBankManager;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.account.HyperPlayerManager;
import regalowl.hyperconomy.event.DisableEvent;
import regalowl.hyperconomy.event.HyperBankModificationEvent;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.event.HyperPlayerModificationEvent;
import regalowl.hyperconomy.event.ShopModificationEvent;
import regalowl.hyperconomy.shop.HyperShopManager;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;


public class HyperModificationServer {

	private HyperConomy hc;
	private int listenPort;
	private ArrayList<RemoteAddress> addresses = new ArrayList<RemoteAddress>();
	private int timeout;
	private boolean runServer;
	private boolean syncShops;
	private long updateInterval;
	private HyperTransferObject sendObject;
	
	private ServerSocket serverSocket;
	private Socket sClientSocket;
	private Socket clientSocket;
	private RemoteUpdater remoteUpdater;
	private Timer t = new Timer();
	
	public HyperModificationServer(HyperConomy hc) {
		this.hc = hc;
		if (hc.getConf().getBoolean("multi-server.enable")) {
			HashMap<String,Integer> ips = null;
			try {
				ips = CommonFunctions.convertToIntMap(CommonFunctions.explodeMap(hc.getConf().getString("multi-server.remote-server-ip-addresses")));
			} catch (Exception e) {
				hc.getMC().logSevere("[HyperConomy]'remote-server-ip-addresses' entry in config.yml is invalid.  Regenerate the config or check the wiki for more info.");
				return;
			}
			for (Map.Entry<String,Integer> entry : ips.entrySet()) {
			    String ip = entry.getKey();
			    Integer port = entry.getValue();
			    addresses.add(new RemoteAddress(ip,port));
			}
			listenPort = hc.getConf().getInt("multi-server.port");
			timeout = hc.getConf().getInt("multi-server.connection-timeout-ms");
			updateInterval = hc.getConf().getInt("multi-server.update-interval");
			syncShops = hc.getConf().getBoolean("multi-server.sync-shops");
			runServer = true;
			sendObject = new HyperTransferObject(HyperTransferType.REQUEST_UPDATE);
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
					HyperTransferObject transferObject = null;
					try {
						serverSocket = new ServerSocket(listenPort);
						sClientSocket = serverSocket.accept();
						ObjectInputStream input = new ObjectInputStream(sClientSocket.getInputStream());
						transferObject = (HyperTransferObject) input.readObject();
						if (transferObject.getType() == HyperTransferType.REQUEST_UPDATE) {
							processHyperObjects(transferObject.getHyperObjects());
							processHyperPlayers(transferObject.getHyperPlayers());
							processShops(transferObject.getShops());
							processBanks(transferObject.getBanks());
						}
						ObjectOutputStream out = new ObjectOutputStream(sClientSocket.getOutputStream());
						out.writeObject(new HyperTransferObject(HyperTransferType.UPDATE_SUCCESSFUL));
						out.flush();
						sClientSocket.close();
						serverSocket.close();
					} catch (Exception e) {
						try {
							hc.getDebugMode().debugWriteError(e);
							if (sClientSocket != null) sClientSocket.close();
							if (serverSocket != null) serverSocket.close();
						} catch (IOException e1) {}
					}
				}
			}
		}).start();
	}

	private void processHyperObjects(ArrayList<TradeObject> objects) {
		for (TradeObject ho:objects) {
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
			if (s == null || s.getName() == null || s.getName().equalsIgnoreCase("")) continue;
			if (hsm.shopExists(s.getName())) hsm.removeShop(s.getName());
			s.setHyperConomy(hc);
			hsm.addShop(s);
		}
	}
	
	private void processHyperPlayers(ArrayList<HyperPlayer> objects) {
		HyperPlayerManager hpm = hc.getHyperPlayerManager();
		for (HyperPlayer hp:objects) {
			if (hp == null || hp.getName() == null || hp.getName().equalsIgnoreCase("")) continue;
			if (hpm.playerAccountExists(hp.getName())) hpm.removeHyperPlayer(hpm.getHyperPlayer(hp.getName()));
			hp.setHyperConomy(hc);
			hpm.addHyperPlayer(hp);
		}
	}
	
	private void processBanks(ArrayList<HyperBank> objects) {
		HyperBankManager hbm = hc.getHyperBankManager();
		for (HyperBank hb:objects) {
			if (hb == null || hb.getName() == null || hb.getName().equalsIgnoreCase("")) continue;
			if (hbm.hasBank(hb.getName())) hbm.removeHyperBank(hb.getName());
			hb.setHyperConomy(hc);
			hbm.addHyperBank(hb);
		}
	}
	
	
	@EventHandler
	public void onHyperObjectModification(TradeObjectModificationEvent event) {
		sendObject.addHyperObject(event.getTradeObject());
	}
	
	@EventHandler
	public void onHyperPlayerModification(HyperPlayerModificationEvent event) {
		sendObject.addHyperPlayer(event.getHyperPlayer());
	}
	
	@EventHandler
	public void onHyperBankModification(HyperBankModificationEvent event) {
		sendObject.addBank(event.getHyperBank());
	}
	
	@EventHandler
	public void onShopModification(ShopModificationEvent event) {
		sendObject.addShop(event.getShop());
	}
	
	@EventHandler
	public void onDisableEvent(DisableEvent event) {
		runServer = false;
		remoteUpdater.cancel();
		try {
			if (sClientSocket != null) sClientSocket.close();
			if (serverSocket != null) serverSocket.close();
			if (clientSocket != null) clientSocket.close();
		} catch (Exception e) {
			hc.getDebugMode().debugWriteError(e);
		}
	}
	

	private class RemoteUpdater extends TimerTask {
		@Override
		public void run() {
			if (sendObject.isEmpty()) return;
			for (RemoteAddress ra:addresses) {
				try {
					clientSocket = new Socket();
					clientSocket.connect(new InetSocketAddress(ra.ip, ra.port), timeout);
					ObjectOutputStream out;
					out = new ObjectOutputStream(clientSocket.getOutputStream());
					out.writeObject(sendObject);
					out.flush();
					ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
					HyperTransferObject response = (HyperTransferObject) input.readObject();
					if (!response.getType().equals(HyperTransferType.UPDATE_SUCCESSFUL)) {
						clientSocket.close();
						continue;
					}
					clientSocket.close();
					sendObject.clear();
				} catch (Exception e) {
					hc.getDebugMode().debugWriteMessage("The error below occurred when connecting to ip: " + ra.ip + " and port: " + ra.port);
					hc.getDebugMode().debugWriteError(e);
					continue;
				}
			}
		}
	}





	
}

