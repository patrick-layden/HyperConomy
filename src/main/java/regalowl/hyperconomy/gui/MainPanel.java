package regalowl.hyperconomy.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.SimpleDataLib;
import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.simpledatalib.file.YamlHandler;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JTextField;

public class MainPanel {

	private JFrame frame;
	
	private SimpleDataLib sdl;
	private HyperConomy hc;
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
		frame = new JFrame();
		frame.setBounds(100, 100, 956, 993);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		notificationText = new JTextArea();
		notificationText.setBounds(65, 567, 812, 363);
		frame.getContentPane().add(notificationText);
		notificationText.setColumns(10);

		
		try {
			JList<String> list = new JList<String>();
			MineCraftConnector mc = new GUIConnector();
			this.hc = mc.getHC();
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
			notificationText.setText(error);
		}
		

	}
}
