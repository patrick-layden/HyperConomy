package regalowl.hyperconomy.gui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
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
import regalowl.hyperconomy.api.ServerConnectionType;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DisableEvent;
import regalowl.hyperconomy.event.GUIChangeType;
import regalowl.hyperconomy.event.HyperBankModificationEvent;
import regalowl.hyperconomy.event.HyperEconomyCreationEvent;
import regalowl.hyperconomy.event.HyperEconomyDeletionEvent;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.event.TradeObjectModificationType;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.event.HyperPlayerModificationEvent;
import regalowl.hyperconomy.event.RequestGUIChangeEvent;
import regalowl.hyperconomy.event.ShopModificationEvent;


public class RemoteGUIServer {

	private HyperConomy hc;
	private boolean remoteGUIEnabled;
	private boolean isServer;
	private RemoteAddress remoteServerAddress;
	private int listenPort;
	private int remoteGUITimeout;
	private int refreshRate;
	private boolean runServer;
	private boolean connected = false; //used differently depending on if server or client
	private boolean currentlyUpdating = false;  //flags when receiving update in order to ignore modification events - both for server and client
	private GUITransferObject serverSideChanges;
	private GUITransferObject clientSideChanges;
	private PeriodicGUIUpdater periodicUpdater;
	private Timer t = new Timer();
	private String authKey;
	private boolean guiSynchronized = false;
	public boolean resetDisconnectTimer = false;
	private final int disconnectTimerMilliseconds = 150000;
	
	public RemoteGUIServer(HyperConomy hc) {
		this.hc = hc;
		runServer = true;
		remoteGUIEnabled = hc.getConf().getBoolean("remote-gui.enable");
		if (!remoteGUIEnabled) return;
		hc.getHyperEventHandler().registerListener(this);
		isServer = hc.getConf().getBoolean("remote-gui.server");
		listenPort = hc.getConf().getInt("remote-gui.listen-port");
		remoteServerAddress = new RemoteAddress(hc.getConf().getString("remote-gui.remote-server-ip"), hc.getConf().getInt("remote-gui.remote-server-port"));
		remoteGUITimeout = hc.getConf().getInt("remote-gui.connection-timeout-ms");
		refreshRate = hc.getConf().getInt("remote-gui.refresh-rate-ms");
		authKey = hc.getConf().getString("remote-gui.auth-key");
		serverSideChanges = new GUITransferObject(GUITransferType.UPDATE_GUI, authKey);
		clientSideChanges = new GUITransferObject(GUITransferType.UPDATE_SERVER, authKey);
		startServer();
	}
	
	
	@EventHandler
	public void onDataLoad(DataLoadEvent event) {
		if (!(event.loadType == DataLoadType.COMPLETE)) return;
		if (hc.getMC().getServerConnectionType() != ServerConnectionType.GUI) return;
		if (isServer) return;

		
		new Thread(new Runnable() {
			public void run() {
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(remoteServerAddress.ip, remoteServerAddress.port), remoteGUITimeout);
					//test connection
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(new GUITransferObject(GUITransferType.CONNECTION_TEST, authKey));
					out.flush();
					ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
					GUITransferObject response = (GUITransferObject) input.readObject();
					if (response.getType().equals(GUITransferType.SUCCESS)) {
						hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.CONNECTED));
						socket.close();
					} else if (response.getType().equals(GUITransferType.NOT_AUTHORIZED)) {
						String message = "Your auth-key in your config.yml file is incorrect.  Make sure it matches your server's auth-key.";
						hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.INVALID_KEY, message));
						hc.getDebugMode().debugWriteMessage(message);
						socket.close();
						return;
					} else {
						hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.INVALID_RESPONSE));
						socket.close();
						return;
					}
					socket = new Socket();
					socket.connect(new InetSocketAddress(remoteServerAddress.ip, remoteServerAddress.port), remoteGUITimeout);
					out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(new GUITransferObject(GUITransferType.REQUEST_GUI_INITIALIZATION, authKey));
					out.flush();
					input = new ObjectInputStream(socket.getInputStream());
					response = (GUITransferObject) input.readObject();
					if (response.getType().equals(GUITransferType.GUI_INITIALIZATION)) {
						connected = true;
						currentlyUpdating = true;
						hc.getDataManager().setEconomies(response.getHyperEconomies());
						currentlyUpdating = false;
						guiSynchronized = true;
						hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.SYNCHRONIZED));
						response = new GUITransferObject(GUITransferType.SUCCESS, authKey);
						out = new ObjectOutputStream(socket.getOutputStream());
						out.writeObject(response);
						out.flush();
						socket.close();
						periodicUpdater = new PeriodicGUIUpdater();
						t.schedule(periodicUpdater, refreshRate, refreshRate);
						hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.LOADED));
						hc.getDebugMode().debugWriteMessage("GUI Initialization Succeeded");
					} else {
						hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.INVALID_RESPONSE));
						hc.getDebugMode().debugWriteMessage("GUI Initialization Failed: Server returned invalid response.");
						socket.close();
					}
					
				} catch (Exception e) {
					String error = CommonFunctions.getErrorString(e);
					hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.ERROR, error));
					hc.getDebugMode().debugWriteMessage("GUI initialization error occurred when connecting to ip: " + remoteServerAddress.ip + " and port: " + remoteServerAddress.port);
					hc.getDebugMode().debugWriteError(e);
				}
			}
		}).start();
	}
	
	//updates the GUI with changes that occurred on the remote server, and sends changes to remote server made by the GUI
	private class PeriodicGUIUpdater extends TimerTask {
		@Override
		public void run() {
			try {
				//get changes from server
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(remoteServerAddress.ip, remoteServerAddress.port), remoteGUITimeout);
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(new GUITransferObject(GUITransferType.REQUEST_UPDATE_GUI, authKey));
				out.flush();
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				GUITransferObject response = (GUITransferObject) input.readObject();
				if (response.getType().equals(GUITransferType.UPDATE_GUI)) {
					currentlyUpdating = true;
					processHyperEconomiesGUI(response.getHyperEconomies());
					processHyperObjectsGUI(response.getHyperObjects()); 
					processDeletedTradeObjectsGUI(response.getDeletedTradeObjects());
					processDeletedHyperEconomiesGUI(response.getDeletedEconomies());
					currentlyUpdating = false;
					response = new GUITransferObject(GUITransferType.SUCCESS, authKey);
					out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(response);
					out.flush();
				} else if (response.getType().equals(GUITransferType.NOTHING_TO_UPDATE)) {
					
				} else {
					hc.getDebugMode().debugWriteMessage("GUI Periodic Update Failed: Server returned invalid response.");
				}
				socket.close();
				//send local GUI made changes to server
				if (!clientSideChanges.isEmpty()) {
					socket = new Socket();
					socket.connect(new InetSocketAddress(remoteServerAddress.ip, remoteServerAddress.port), remoteGUITimeout);
					out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(clientSideChanges);
					out.flush();
					input = new ObjectInputStream(socket.getInputStream());
					response = (GUITransferObject) input.readObject();
					if (response.getType().equals(GUITransferType.SUCCESS)) {
						clientSideChanges.clear();
						guiSynchronized = true;
						hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.SYNCHRONIZED));
					} else {
						hc.getDebugMode().debugWriteMessage("GUI Server Update Failed: Server returned invalid response.");
					}
				}
				socket.close();
			} catch (Exception e) {
				hc.getDebugMode().debugWriteMessage("GUI Periodic Update error occurred when connecting to ip: " + remoteServerAddress.ip + " and port: " + remoteServerAddress.port);
				hc.getDebugMode().debugWriteError(e);
			}
		}
	}
	

	
	private void processHyperEconomiesGUI(ArrayList<HyperEconomy> economies) {
		for (HyperEconomy he:economies) {
			if (he == null || he.getName() == null || he.getName().equalsIgnoreCase("")) continue;
			he.setHyperConomy(hc);
			hc.getDataManager().addEconomy(he);
			he.save();
			hc.getDebugMode().debugWriteMessage("Economy added server side: " + he.getName());
			hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.SERVER_CHANGE_ECONOMY, "Economy added on remote server: " + he.getName()));
		}
	}
	private void processDeletedHyperEconomiesGUI(ArrayList<String> economies) {
		for (String he:economies) {
			if (he == null || he.equalsIgnoreCase("") || !hc.getDataManager().economyExists(he)) continue;
			hc.getDataManager().getEconomy(he).delete();
			hc.getDebugMode().debugWriteMessage("Economy deleted server side: " + he);
			hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.SERVER_CHANGE_ECONOMY, "Economy deleted on remote server: " + he));
		}
	}
	private void processHyperObjectsGUI(ArrayList<TradeObject> objects) {
		for (TradeObject ho:objects) {
			if (ho == null || ho.getEconomy() == null || ho.getEconomy().equalsIgnoreCase("")) continue;
			HyperEconomy he = hc.getDataManager().getEconomy(ho.getEconomy());
			if (he == null) continue;
			ho.setHyperConomy(hc);
			if (ho.isShopObject()) {
				PlayerShop ps = ho.getShopObjectShop();
				if (ps != null) {
					ho.setHyperConomy(hc);
					ps.updateHyperObject(ho);
					ho.save();
					hc.getDebugMode().debugWriteMessage("Shop object changed server side: " + ho.getDisplayName());
					hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.SERVER_CHANGE_OBJECT, "Shop object modified on remote server: " + ho.getDisplayName()));
				}
			} else {
				ho.setHyperConomy(hc);
				he.removeObject(ho.getName());
				he.addObject(ho);
				ho.save();
				hc.getDebugMode().debugWriteMessage("Regular trade object changed server side: " + ho.getDisplayName());
				hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.SERVER_CHANGE_OBJECT, "Trade object modified on remote server: " + ho.getDisplayName()));
			}
		}
	}
	private void processDeletedTradeObjectsGUI(ArrayList<TradeObject> objects) {
		for (TradeObject ho:objects) {
			if (ho == null || ho.getEconomy() == null || ho.getEconomy().equalsIgnoreCase("")) continue;
			HyperEconomy he = hc.getDataManager().getEconomy(ho.getEconomy());
			if (he == null) continue;
			ho.setHyperConomy(hc);
			if (ho.isShopObject()) {
				PlayerShop ps = ho.getShopObjectShop();
				if (ps != null) {
					ho.setHyperConomy(hc);
					ps.updateHyperObject(ho);
					ho.delete();
					hc.getDebugMode().debugWriteMessage("Shop object deleted server side: " + ho.getDisplayName());
					hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.SERVER_CHANGE_OBJECT, "Shop object deleted on remote server: " + ho.getDisplayName()));
				}
			} else {
				ho.setHyperConomy(hc);
				he.removeObject(ho.getName());
				he.addObject(ho);
				ho.delete();
				hc.getDebugMode().debugWriteMessage("Regular object deleted server side: " + ho.getDisplayName());
				hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.SERVER_CHANGE_OBJECT, "Trade object deleted on remote server: " + ho.getDisplayName()));
			}
		}
	}
	
	
	private void processHyperEconomiesServer(ArrayList<HyperEconomy> economies) {
		for (HyperEconomy he:economies) {
			if (he == null || he.getName() == null || he.getName().equalsIgnoreCase("")) continue;
			he.setHyperConomy(hc);
			hc.getDataManager().addEconomy(he);
			he.save();
			hc.getDebugMode().debugWriteMessage("Economy added via GUI: " + he.getName());
		}
	}
	private void processDeletedHyperEconomiesServer(ArrayList<String> economies) {
		for (String he:economies) {
			if (he == null || he.equalsIgnoreCase("") || !hc.getDataManager().economyExists(he)) continue;
			hc.getDataManager().getEconomy(he).delete();
			hc.getDebugMode().debugWriteMessage("Economy deleted via GUI: " + he);
		}
	}
	private void processHyperObjectsServer(ArrayList<TradeObject> objects) {
		for (TradeObject ho:objects) {
			if (ho == null || ho.getEconomy() == null || ho.getEconomy().equalsIgnoreCase("")) continue;
			HyperEconomy he = hc.getDataManager().getEconomy(ho.getEconomy());
			if (he == null) continue;
			ho.setHyperConomy(hc);
			if (ho.isShopObject()) {
				PlayerShop ps = ho.getShopObjectShop();
				if (ps != null) {
					ho.setHyperConomy(hc);
					ps.updateHyperObject(ho);
					ho.save();
					hc.getDebugMode().debugWriteMessage("Shop object changed via GUI: " + ho.getDisplayName());
				}
			} else {
				ho.setHyperConomy(hc);
				he.removeObject(ho.getName());
				he.addObject(ho);
				ho.save();
				hc.getDebugMode().debugWriteMessage("Regular object changed via GUI: " + ho.getDisplayName());
			}
		}
	}
	private void processDeletedTradeObjectsServer(ArrayList<TradeObject> objects) {
		for (TradeObject ho:objects) {
			if (ho == null || ho.getEconomy() == null || ho.getEconomy().equalsIgnoreCase("")) continue;
			HyperEconomy he = hc.getDataManager().getEconomy(ho.getEconomy());
			if (he == null) continue;
			ho.setHyperConomy(hc);
			if (ho.isShopObject()) {
				PlayerShop ps = ho.getShopObjectShop();
				if (ps != null) {
					ho.setHyperConomy(hc);
					ps.updateHyperObject(ho);
					ho.delete();
					hc.getDebugMode().debugWriteMessage("Shop object deleted via GUI: " + ho.getDisplayName());
				}
			} else {
				ho.setHyperConomy(hc);
				he.removeObject(ho.getName());
				he.addObject(ho);
				ho.delete();
				hc.getDebugMode().debugWriteMessage("Regular object deleted via GUI: " + ho.getDisplayName());
			}
		}
	}
	
	
	private class RemoteAddress {
		String ip;
		int port;
		public RemoteAddress(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}
	}

	public void disable() {
		runServer = false;
	}
	
	private void startServer() {
		hc.getDebugMode().debugWriteMessage("GUI Server started on port: " + listenPort);
		new Thread(new Runnable() {
			public void run() {
				while (runServer) {
					GUITransferObject incomingTransfer = null;
					ServerSocket serverSocket = null;
					Socket socket = null;
					try {
						serverSocket = new ServerSocket(listenPort);
						socket = serverSocket.accept();
						ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
						incomingTransfer = (GUITransferObject) input.readObject();
						
						GUITransferObject response = null;
						
						if (!incomingTransfer.getAuthKey().equals(authKey)) {
							hc.getDebugMode().debugWriteMessage("Invalid Authorization Key Provided: " + incomingTransfer.getAuthKey() + " from IP: " + socket.getRemoteSocketAddress());
							response = new GUITransferObject(GUITransferType.NOT_AUTHORIZED, "");
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject(response);
							out.flush();
							socket.close();
							serverSocket.close();
							return;
						}
						resetDisconnectTimer = true;
						if (incomingTransfer.getType() == GUITransferType.REQUEST_GUI_INITIALIZATION) {
							response = new GUITransferObject(GUITransferType.GUI_INITIALIZATION, authKey);
							for (HyperEconomy he:hc.getDataManager().getEconomies()) {
								response.addEconomy(he);
							}
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject(response);
							out.flush();
							input = new ObjectInputStream(socket.getInputStream());
							response = (GUITransferObject) input.readObject();
							if (response.getType().equals(GUITransferType.SUCCESS)) {
								connected = true;
								//if no activity for 5 minutes disconnects GUI and stops accumulating server side changes to prevent memory leak
								startDisconnectTimer();
							}
						} else if (incomingTransfer.getType() == GUITransferType.REQUEST_UPDATE_GUI) {
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							if (serverSideChanges.isEmpty()) {
								out.writeObject(new GUITransferObject(GUITransferType.NOTHING_TO_UPDATE, authKey));
								out.flush();
							} else {
								out.writeObject(serverSideChanges);
								out.flush();
								input = new ObjectInputStream(socket.getInputStream());
								response = (GUITransferObject) input.readObject();
								if (response.getType().equals(GUITransferType.SUCCESS)) {
									serverSideChanges.clear();
									guiSynchronized = true;							
								}
							}

						} else if (incomingTransfer.getType() == GUITransferType.UPDATE_SERVER) {
							currentlyUpdating = true;
							processHyperEconomiesServer(incomingTransfer.getHyperEconomies());
							processHyperObjectsServer(incomingTransfer.getHyperObjects()); 
							processDeletedTradeObjectsServer(incomingTransfer.getDeletedTradeObjects());
							processDeletedHyperEconomiesServer(incomingTransfer.getDeletedEconomies());
							currentlyUpdating = false;
							response = new GUITransferObject(GUITransferType.SUCCESS, authKey);
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject(response);
							out.flush();
						} else if (incomingTransfer.getType() == GUITransferType.CONNECTION_TEST) {
							response = new GUITransferObject(GUITransferType.SUCCESS, authKey);
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject(response);
							out.flush();
						} else if (incomingTransfer.getType() == GUITransferType.DISCONNECT) {
							connected = false;
							serverSideChanges.clear();
							t.cancel();
							t = new Timer();
							response = new GUITransferObject(GUITransferType.SUCCESS, authKey);
							ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject(response);
							out.flush();
						}
						socket.close();
						serverSocket.close();
					} catch (BindException be) {
						runServer = false;
						hc.getDebugMode().debugWriteError(be);
						hc.getDebugMode().debugWriteMessage("Remote GUI disabled.  Port already in use by something else.  Check your config.");
					} catch (Exception e) {
						try {
							hc.getDebugMode().debugWriteError(e);
							if (socket != null) socket.close();
							if (serverSocket != null) serverSocket.close();
						} catch (IOException e1) {}
					}
				}
			}
		}).start();
	}
	
	public void startDisconnectTimer() {
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				if (resetDisconnectTimer) {
					resetDisconnectTimer = false;
					startDisconnectTimer(); //runs until GUI sends disconnect request or 5 minutes passes with no connection from GUI
				} else {
					connected = false;
					serverSideChanges.clear();
				}
			}
		}, disconnectTimerMilliseconds);
	}

	@EventHandler
	public void onHyperObjectModification(TradeObjectModificationEvent event) {
		if (!connected || currentlyUpdating) return;
		if (event.getTradeObjectModificationType() == TradeObjectModificationType.DELETED) {
			if (isServer) {
				serverSideChanges.addDeletedTradeObject(event.getTradeObject());
			} else {
				clientSideChanges.addDeletedTradeObject(event.getTradeObject());
			}
		} else {
			if (isServer) {
				serverSideChanges.addHyperObject(event.getTradeObject());
			} else {
				clientSideChanges.addHyperObject(event.getTradeObject());
			}
		}
		if (guiSynchronized) {
			guiSynchronized = false;
			hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.NOT_SYNCHRONIZED));
		}
	}
	@EventHandler
	public void onEconomyCreation(HyperEconomyCreationEvent event) {
		if (!connected || currentlyUpdating) return;
		if (isServer) {
			serverSideChanges.addEconomy(event.getHyperEconomy());
		} else {
			clientSideChanges.addEconomy(event.getHyperEconomy());
		}
		if (guiSynchronized) {
			guiSynchronized = false;
			hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.NOT_SYNCHRONIZED));
		}
	}
	@EventHandler
	public void onEconomyDeletion(HyperEconomyDeletionEvent event) {
		if (!connected || currentlyUpdating) return;
		if (isServer) {
			serverSideChanges.addDeletedEconomy(event.getHyperEconomyName());
		} else {
			clientSideChanges.addDeletedEconomy(event.getHyperEconomyName());
		}
		if (guiSynchronized) {
			guiSynchronized = false;
			hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.NOT_SYNCHRONIZED));
		}
	}
	@EventHandler
	public void onHyperPlayerModification(HyperPlayerModificationEvent event) {
		if (!connected || !isServer) return;
	}
	@EventHandler
	public void onHyperBankModification(HyperBankModificationEvent event) {
		if (!connected || !isServer) return;
	}
	@EventHandler
	public void onShopModification(ShopModificationEvent event) {
		if (!connected || !isServer) return;
	}
	
	@EventHandler
	public void onDisableEvent(DisableEvent event) {
		runServer = false;
		//remoteUpdater.cancel();
	}

	public boolean connected() {
		return connected;
	}
	
	public boolean enabled() {
		return remoteGUIEnabled;
	}

	public void disconnect() {
		if (isServer) return;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(remoteServerAddress.ip, remoteServerAddress.port), 5000);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(new GUITransferObject(GUITransferType.DISCONNECT, authKey));
			out.flush();
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			GUITransferObject response = (GUITransferObject) input.readObject();
			if (!response.getType().equals(GUITransferType.SUCCESS)) {
				hc.getDebugMode().debugWriteMessage("Server failed to properly disconnect from GUI.");
			}
			socket.close();
			connected = false;
		} catch (Exception e) {
			String error = CommonFunctions.getErrorString(e);
			hc.getHyperEventHandler().fireEventFromAsyncThread(new RequestGUIChangeEvent(GUIChangeType.ERROR, error));
			hc.getDebugMode().debugWriteMessage("Disconnect error occurred when connecting to ip: " + remoteServerAddress.ip + " and port: " + remoteServerAddress.port);
			hc.getDebugMode().debugWriteError(e);
		}
	}


	
}

