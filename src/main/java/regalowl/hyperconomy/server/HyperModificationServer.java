package regalowl.hyperconomy.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.bukkit.scheduler.BukkitRunnable;

import regalowl.hyperconomy.HyperBankManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.HyperPlayerManager;
import regalowl.hyperconomy.HyperShopManager;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.DisableListener;
import regalowl.hyperconomy.event.HyperBankModificationListener;
import regalowl.hyperconomy.event.HyperObjectModificationListener;
import regalowl.hyperconomy.event.HyperPlayerModificationListener;
import regalowl.hyperconomy.event.ShopModificationListener;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.shop.Shop;


public class HyperModificationServer implements HyperObjectModificationListener, HyperPlayerModificationListener, 
ShopModificationListener, HyperBankModificationListener, DisableListener {

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
	
	public HyperModificationServer() {
		this.hc = HyperConomy.hc;
		if (hc.getConf().getBoolean("multi-server.enable")) {
			ipAddresses = hc.gCF().explode(hc.getConf().getString("multi-server.remote-server-ip-addresses"), ",");
			port = hc.getConf().getInt("multi-server.port");
			timeout = hc.getConf().getInt("multi-server.connection-timeout-ms");
			updateInterval = hc.getConf().getInt("multi-server.update-interval");
			runServer = true;
			sendObject = new HyperTransferObject(HyperTransferType.REQUEST_UPDATE);
			hc.getHyperEventHandler().registerListener(this);
			receiveUpdate();
			remoteUpdater = new RemoteUpdater();
			remoteUpdater.runTaskTimerAsynchronously(hc, updateInterval, updateInterval);
		}
	}

	public void disable() {
		runServer = false;
	}
	
	private void receiveUpdate() {
		HyperConomy.hc.getServer().getScheduler().runTaskAsynchronously(HyperConomy.hc, new Runnable() {
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
							e.printStackTrace();
							if (sClientSocket != null) sClientSocket.close();
							if (serverSocket != null) serverSocket.close();
						} catch (IOException e1) {}
					}
				}
			}
		});
	}

	private void processHyperObjects(ArrayList<HyperObject> objects) {
		for (HyperObject ho:objects) {
			if (ho != null && ho.getEconomy() != null && !ho.getEconomy().equalsIgnoreCase("")) {
				HyperEconomy he = hc.getDataManager().getEconomy(ho.getEconomy());
				if (he != null) {
					he.removeHyperObject(ho.getName());
					he.addHyperObject(ho);
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
	
	
	
	
	
	@Override
	public void onHyperObjectModification(HyperObject ho) {
		sendObject.addHyperObject(ho);
	}
	@Override
	public void onHyperPlayerModification(HyperPlayer hp) {
		sendObject.addHyperPlayer(hp);
	}
	@Override
	public void onHyperBankModification(HyperBank hb) {
		sendObject.addBank(hb);
	}
	@Override
	public void onShopModification(Shop s) {
		sendObject.addShop(s);
	}
	
	
	private class RemoteUpdater extends BukkitRunnable {
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
					e.printStackTrace();
					continue;
				}
			}
		}
	}
	
	
	
	@Override
	public void onDisable() {
		runServer = false;
		remoteUpdater.cancel();
		try {
			if (sClientSocket != null) sClientSocket.close();
			if (serverSocket != null) serverSocket.close();
			if (clientSocket != null) clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}






	
}

