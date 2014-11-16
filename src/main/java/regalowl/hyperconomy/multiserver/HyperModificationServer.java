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
import regalowl.simpledatalib.event.EventHandler;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperBankManager;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.account.HyperPlayerManager;
import regalowl.hyperconomy.event.DisableEvent;
import regalowl.hyperconomy.event.HyperBankModificationEvent;
import regalowl.hyperconomy.event.HyperObjectModificationEvent;
import regalowl.hyperconomy.event.HyperPlayerModificationEvent;
import regalowl.hyperconomy.event.ShopModificationEvent;
import regalowl.hyperconomy.shop.HyperShopManager;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;


public class HyperModificationServer {

	private HyperConomy hc;
	private int port;
	private ArrayList<String> ipAddresses = new ArrayList<String>();
	private int timeout;
	private boolean runServer;
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
			ipAddresses = CommonFunctions.explode(hc.getConf().getString("multi-server.remote-server-ip-addresses"), ",");
			port = hc.getConf().getInt("multi-server.port");
			timeout = hc.getConf().getInt("multi-server.connection-timeout-ms");
			updateInterval = hc.getConf().getInt("multi-server.update-interval");
			runServer = true;
			sendObject = new HyperTransferObject(HyperTransferType.REQUEST_UPDATE);
			hc.getHyperEventHandler().registerListener(this);
			receiveUpdate();
			remoteUpdater = new RemoteUpdater();
			t.schedule(remoteUpdater, updateInterval, updateInterval);
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
						serverSocket = new ServerSocket(port);
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
			if (ho != null && ho.getEconomy() != null && !ho.getEconomy().equalsIgnoreCase("")) {
				HyperEconomy he = hc.getDataManager().getEconomy(ho.getEconomy());
				if (he != null) {
					if (ho.isShopObject()) {
						PlayerShop ps = ho.getShop();
						if (ps != null) ps.updateHyperObject(ho);
					} else {
						he.removeTradeObject(ho.getName());
						he.addTradeObject(ho);
					}
				}
			}
		}
	}
	private void processHyperPlayers(ArrayList<HyperPlayer> objects) {
		HyperPlayerManager hpm = hc.getHyperPlayerManager();
		for (HyperPlayer hp:objects) {
			if (hp == null || hp.getName() == null || hp.getName().equalsIgnoreCase("")) continue;
			if (hpm.playerAccountExists(hp.getName())) {
				hpm.removeHyperPlayer(hpm.getHyperPlayer(hp.getName()));
			}
			hpm.addHyperPlayer(hp);
		}
	}
	
	private void processShops(ArrayList<Shop> objects) {
		HyperShopManager hsm = hc.getHyperShopManager();
		for (Shop s:objects) {
			if (s == null || s.getName() == null || s.getName().equalsIgnoreCase("")) continue;
			if (hsm.shopExists(s.getName())) {
				hsm.removeShop(s.getName());
			}
			hsm.addShop(s);
		}
	}
	
	private void processBanks(ArrayList<HyperBank> objects) {
		HyperBankManager hbm = hc.getHyperBankManager();
		for (HyperBank hb:objects) {
			if (hb == null || hb.getName() == null || hb.getName().equalsIgnoreCase("")) continue;
			hbm.removeHyperBank(hb.getName());
			hbm.addHyperBank(hb);
		}
	}
	
	
	@EventHandler
	public void onHyperObjectModification(HyperObjectModificationEvent event) {
		sendObject.addHyperObject(event.getHyperObject());
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
			for (String ip:ipAddresses) {
				try {
					clientSocket = new Socket();
					clientSocket.connect(new InetSocketAddress(ip, port), timeout);
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
					hc.getDebugMode().debugWriteError(e);
					continue;
				}
			}
		}
	}





	
}

