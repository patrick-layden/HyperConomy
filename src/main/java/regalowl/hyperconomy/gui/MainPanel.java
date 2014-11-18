package regalowl.hyperconomy.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.SimpleDataLib;
import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.events.LogEvent;
import regalowl.simpledatalib.events.LogLevel;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


public class MainPanel {

	private JFrame frame;
	private HyperConomy hc;
	private SimpleDataLib sdl;

	private JTextArea notificationText;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainPanel window = new MainPanel();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainPanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		redirectSystemStreams();
		frame = new JFrame();
		frame.setBounds(100, 100, 956, 993);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		notificationText = new JTextArea();
		notificationText.setBounds(65, 130, 812, 800);
		frame.getContentPane().add(notificationText);
		notificationText.setColumns(10);

		
		try {
			JList<String> list = new JList<String>();
			MineCraftConnector mc = new GUIConnector(notificationText);
			this.hc = mc.getHC();
			hc.load();
			hc.getSimpleDataLib().getEventPublisher().registerListener(this);
			hc.enable();
			this.sdl = hc.gSDL();
			ArrayList<TradeObject> tObjects = hc.getDataManager().getDefaultEconomy().getTradeObjects();
			String[] objectNames = new String[tObjects.size()];
			for (int i = 0; i < tObjects.size(); i++) {
				objectNames[i] = tObjects.get(i).getDisplayName();
			}
			list.setListData(objectNames);
			
			
			list.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
			list.setBounds(224, 236, -156, -146);
			frame.getContentPane().add(list);
		} catch (Exception e) {
			String error = CommonFunctions.getErrorString(e);
			updateTextArea(error);
		}
		

	}
	
	@EventHandler
	public void onLogMessage(LogEvent event) {
		if (event.getMessage() != null) updateTextArea(event.getMessage());
	}
	
	
	private void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				notificationText.append(text);
			}
		});
	}

	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}
}
