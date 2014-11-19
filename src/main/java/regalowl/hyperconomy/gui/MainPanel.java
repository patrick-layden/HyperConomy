package regalowl.hyperconomy.gui;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.SimpleDataLib;
import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.events.LogEvent;
import regalowl.simpledatalib.events.LogLevel;
import regalowl.simpledatalib.file.FileTools;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import java.awt.Color;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;


public class MainPanel {

	private JFrame frame;
	private HyperConomy hc;
	private SimpleDataLib sdl;
	private JTextField stockData;
	JList<String> listObjectSelector;
	private JComboBox<String> tradeObjectSelector;
	private JTextField displayNameData;
	private JLabel displayNameLabel;
	private JTextField aliasesData;
	private JLabel aliasesLabel;
	private JToggleButton initialPricingToggle;
	private JToggleButton staticPricingToggle;
	private JTextField valueData;
	private JTextField staticPriceData;
	private JTextField medianData;
	private JTextField startPriceData;
	private JTextField ceilingData;
	private JTextField floorData;
	private JTextField maxStockData;
	private JLabel maxStockLabel;
	private JLabel dataLabel;
	private JTextArea dataData;
	private JScrollPane scrollPane;
	
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
		MineCraftConnector mc = new GUIConnector();
		this.hc = mc.getHC();
		hc.load();
		hc.getSimpleDataLib().getEventPublisher().registerListener(this);
		this.sdl = hc.gSDL();
		sdl.setDebug(true);
		hc.enable();
		
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 822, 704);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, 
		            "Exit?", "Exit Application", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        	if (hc != null) hc.disable(false);
		            System.exit(0);
		        }
		    }
		});
		//Font font = new Font("Verdana", Font.PLAIN, 9);

		
		stockData = new JTextField();
		stockData.setBounds(158, 128, 297, 24);
		frame.getContentPane().add(stockData);
		stockData.setColumns(10);
		
		JLabel stockLabel = new JLabel("Stock");
		stockLabel.setBounds(52, 132, 107, 15);
		frame.getContentPane().add(stockLabel);
		
		displayNameData = new JTextField();
		displayNameData.setColumns(10);
		displayNameData.setBounds(158, 52, 297, 24);
		frame.getContentPane().add(displayNameData);
		
		displayNameLabel = new JLabel("Display Name");
		displayNameLabel.setBounds(52, 62, 107, 15);
		frame.getContentPane().add(displayNameLabel);
		
		aliasesData = new JTextField();
		aliasesData.setColumns(10);
		aliasesData.setBounds(158, 90, 297, 24);
		frame.getContentPane().add(aliasesData);
		
		aliasesLabel = new JLabel("Aliases");
		aliasesLabel.setBounds(52, 94, 107, 15);
		frame.getContentPane().add(aliasesLabel);
		
		staticPricingToggle = new JToggleButton("Static Pricing");
		staticPricingToggle.setBounds(158, 562, 297, 24);
		frame.getContentPane().add(staticPricingToggle);
		
		initialPricingToggle = new JToggleButton("Initial Pricing");
		initialPricingToggle.setBounds(158, 600, 297, 24);
		frame.getContentPane().add(initialPricingToggle);

		

		valueData = new JTextField();
		valueData.setColumns(10);
		valueData.setBounds(158, 166, 297, 24);
		frame.getContentPane().add(valueData);
		
		JLabel valueLabel = new JLabel("Value");
		valueLabel.setBounds(52, 170, 107, 15);
		frame.getContentPane().add(valueLabel);
		
		staticPriceData = new JTextField();
		staticPriceData.setColumns(10);
		staticPriceData.setBounds(158, 242, 297, 24);
		frame.getContentPane().add(staticPriceData);
		
		JLabel staticPriceLabel = new JLabel("Static Price");
		staticPriceLabel.setBounds(52, 246, 107, 15);
		frame.getContentPane().add(staticPriceLabel);
		
		medianData = new JTextField();
		medianData.setColumns(10);
		medianData.setBounds(158, 204, 297, 24);
		frame.getContentPane().add(medianData);
		
		JLabel medianLabel = new JLabel("Median");
		medianLabel.setBounds(52, 208, 107, 15);
		frame.getContentPane().add(medianLabel);
		
		startPriceData = new JTextField();
		startPriceData.setColumns(10);
		startPriceData.setBounds(158, 280, 297, 24);
		frame.getContentPane().add(startPriceData);
		
		JLabel startPriceLabel = new JLabel("Start Price");
		startPriceLabel.setBounds(52, 284, 107, 15);
		frame.getContentPane().add(startPriceLabel);
		
		ceilingData = new JTextField();
		ceilingData.setColumns(10);
		ceilingData.setBounds(158, 318, 297, 24);
		frame.getContentPane().add(ceilingData);
		
		JLabel ceilingLabel = new JLabel("Ceiling");
		ceilingLabel.setBounds(52, 322, 107, 15);
		frame.getContentPane().add(ceilingLabel);
		
		floorData = new JTextField();
		floorData.setColumns(10);
		floorData.setBounds(158, 356, 297, 24);
		frame.getContentPane().add(floorData);
		
		JLabel floorLabel = new JLabel("Floor");
		floorLabel.setBounds(52, 360, 107, 15);
		frame.getContentPane().add(floorLabel);
		
		maxStockData = new JTextField();
		maxStockData.setColumns(10);
		maxStockData.setBounds(158, 394, 297, 24);
		frame.getContentPane().add(maxStockData);
		
		maxStockLabel = new JLabel("Max Stock");
		maxStockLabel.setBounds(52, 398, 107, 15);
		frame.getContentPane().add(maxStockLabel);
		
		dataLabel = new JLabel("Data");
		dataLabel.setBounds(52, 484, 107, 15);
		frame.getContentPane().add(dataLabel);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(158, 432, 297, 116);
		frame.getContentPane().add(scrollPane);
		
		dataData = new JTextArea();
		scrollPane.setViewportView(dataData);
		dataData.setLineWrap(true);
		dataData.setWrapStyleWord(true);
		
		
		
		
		tradeObjectSelector = new JComboBox<String>();
		tradeObjectSelector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (!(event.getStateChange() == ItemEvent.SELECTED)) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				stockData.setText(to.getStock()+"");
				aliasesData.setText(to.getAliasesString());
				displayNameData.setText(to.getDisplayName());
				staticPricingToggle.setSelected(Boolean.parseBoolean(to.getIsstatic()));
				initialPricingToggle.setSelected(Boolean.parseBoolean(to.getInitiation()));
				valueData.setText(to.getValue()+"");
				staticPriceData.setText(to.getStaticprice()+"");
				medianData.setText(to.getMedian()+"");
				startPriceData.setText(to.getStartprice()+"");
				ceilingData.setText(to.getCeiling()+"");
				floorData.setText(to.getFloor()+"");
				maxStockData.setText(to.getMaxStock()+"");
				dataData.setText(to.getData());
			}
		});
		tradeObjectSelector.setBounds(158, 14, 297, 24);
		frame.getContentPane().add(tradeObjectSelector);
		
		JButton updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TradeObject to = getSelectedObject();
				if (to == null) return;
				to.setStock(Double.parseDouble(stockData.getText()));
				to.setAliases(CommonFunctions.explode(aliasesData.getText(), ","));
				to.setDisplayName(displayNameData.getText());
				to.setInitiation(initialPricingToggle.isSelected()+"");
				to.setIsstatic(staticPricingToggle.isSelected()+"");
				
				
				to.setValue(Double.parseDouble(valueData.getText()));
				to.setStaticprice(Double.parseDouble(staticPriceData.getText()));
				to.setMedian(Double.parseDouble(medianData.getText()));
				to.setStartprice(Double.parseDouble(startPriceData.getText()));
				to.setCeiling(Double.parseDouble(ceilingData.getText()));
				to.setFloor(Double.parseDouble(floorData.getText()));
				to.setMaxStock(Integer.parseInt(maxStockData.getText()));
				to.setData(dataData.getText());

				updateObjectSelector();
			}
		});
		updateButton.setBounds(158, 638, 297, 24);
		frame.getContentPane().add(updateButton);
		
		listObjectSelector = new JList<String>();
		listObjectSelector.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
			}
		});
		listObjectSelector.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		listObjectSelector.setBounds(495, 155, 178, 231);
		frame.getContentPane().add(listObjectSelector);
		


		

	}
	
	private TradeObject getSelectedObject() {
		if (hc != null && hc.enabled()) {
			return hc.getDataManager().getDefaultEconomy().getTradeObject(tradeObjectSelector.getSelectedItem().toString());
		}
		return null;
	}
	
	
	
	@EventHandler
	public void onDataLoad(DataLoadEvent event) {
		if (event.loadType == DataLoadType.COMPLETE) {
			updateObjectSelector();
		}
	}
	
	
	private void updateObjectSelector() {
		int index = tradeObjectSelector.getSelectedIndex();
		tradeObjectSelector.removeAllItems();
		ArrayList<TradeObject> tObjects = hc.getDataManager().getDefaultEconomy().getTradeObjects();
		Collections.sort(tObjects);
		for (TradeObject t:tObjects) {
			if (t.isCompositeObject()) continue;
			tradeObjectSelector.addItem(t.getDisplayName());
		}
		tradeObjectSelector.repaint();
		if (tradeObjectSelector.getItemAt(index) != null) tradeObjectSelector.setSelectedIndex(index);
	}
	
	@EventHandler
	public void onLogMessage(LogEvent event) {
		if (event.getException() != null) event.getException().printStackTrace();
	}
}
