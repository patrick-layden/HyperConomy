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
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import java.awt.Color;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class MainPanel {

	private JFrame frame;
	private HyperConomy hc;
	private SimpleDataLib sdl;
	private JTextField stockData;
	JList<String> listObjectSelector;
	QuickListModel<String> listModel;
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
	private JLabel dataLabel;
	private JTextArea dataData;
	private JScrollPane scrollPane_1;
	private JPanel panel;
	private JLabel nameLabel;
	private JTextField nameData;
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
		frame.setBounds(100, 100, 588, 504);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, "Exit?", "Exit Application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		        	if (hc != null) hc.disable(false);
		            System.exit(0);
		        }
		    }
		});
		
		listModel = new QuickListModel<String>();
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 12, 233, 406);
		frame.getContentPane().add(scrollPane_1);
		listObjectSelector = new JList<String>(listModel);
		scrollPane_1.setViewportView(listObjectSelector);
		listObjectSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listObjectSelector.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				TradeObject to = getSelectedObject();
				if (to == null) return;
				nameData.setText(to.getName());
				stockData.setText(to.getStock()+"");
				aliasesData.setText(to.getAliasesString());
				displayNameData.setText(to.getDisplayName());
				staticPricingToggle.setSelected(to.isStatic());
				initialPricingToggle.setSelected(to.useInitialPricing());
				valueData.setText(to.getValue()+"");
				staticPriceData.setText(to.getStaticPrice()+"");
				medianData.setText(to.getMedian()+"");
				startPriceData.setText(to.getStartPrice()+"");
				ceilingData.setText(to.getCeiling()+"");
				floorData.setText(to.getFloor()+"");
				dataData.setText(to.getData());
			}
		});
		listObjectSelector.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		listObjectSelector.setSelectedIndex(0);
		listObjectSelector.setVisibleRowCount(10);
		
		panel = new JPanel();
		panel.setBounds(246, 12, 326, 406);
		frame.getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{38, 32, 12, 0};
		gbl_panel.rowHeights = new int[]{0, 19, 19, 19, 19, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		nameLabel = new JLabel("Name");
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.EAST;
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		panel.add(nameLabel, gbc_nameLabel);
		
		nameData = new JTextField();
		GridBagConstraints gbc_nameData = new GridBagConstraints();
		gbc_nameData.insets = new Insets(0, 0, 5, 0);
		gbc_nameData.fill = GridBagConstraints.BOTH;
		gbc_nameData.gridx = 2;
		gbc_nameData.gridy = 0;
		panel.add(nameData, gbc_nameData);
		nameData.setColumns(10);
		
		displayNameLabel = new JLabel("Display Name");
		GridBagConstraints gbc_displayNameLabel = new GridBagConstraints();
		gbc_displayNameLabel.anchor = GridBagConstraints.EAST;
		gbc_displayNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_displayNameLabel.gridwidth = 2;
		gbc_displayNameLabel.gridx = 0;
		gbc_displayNameLabel.gridy = 1;
		panel.add(displayNameLabel, gbc_displayNameLabel);
		
		displayNameData = new JTextField();
		GridBagConstraints gbc_displayNameData = new GridBagConstraints();
		gbc_displayNameData.fill = GridBagConstraints.BOTH;
		gbc_displayNameData.insets = new Insets(0, 0, 5, 0);
		gbc_displayNameData.gridx = 2;
		gbc_displayNameData.gridy = 1;
		panel.add(displayNameData, gbc_displayNameData);
		displayNameData.setColumns(10);
		
		aliasesLabel = new JLabel("Aliases");
		GridBagConstraints gbc_aliasesLabel = new GridBagConstraints();
		gbc_aliasesLabel.anchor = GridBagConstraints.EAST;
		gbc_aliasesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_aliasesLabel.gridwidth = 2;
		gbc_aliasesLabel.gridx = 0;
		gbc_aliasesLabel.gridy = 2;
		panel.add(aliasesLabel, gbc_aliasesLabel);
		
		aliasesData = new JTextField();
		GridBagConstraints gbc_aliasesData = new GridBagConstraints();
		gbc_aliasesData.fill = GridBagConstraints.BOTH;
		gbc_aliasesData.insets = new Insets(0, 0, 5, 0);
		gbc_aliasesData.gridx = 2;
		gbc_aliasesData.gridy = 2;
		panel.add(aliasesData, gbc_aliasesData);
		aliasesData.setColumns(10);
		
		JLabel stockLabel = new JLabel("Stock");
		GridBagConstraints gbc_stockLabel = new GridBagConstraints();
		gbc_stockLabel.anchor = GridBagConstraints.EAST;
		gbc_stockLabel.insets = new Insets(0, 0, 5, 5);
		gbc_stockLabel.gridwidth = 2;
		gbc_stockLabel.gridx = 0;
		gbc_stockLabel.gridy = 3;
		panel.add(stockLabel, gbc_stockLabel);
		//Font font = new Font("Verdana", Font.PLAIN, 9);

		
		stockData = new JTextField();
		GridBagConstraints gbc_stockData = new GridBagConstraints();
		gbc_stockData.fill = GridBagConstraints.BOTH;
		gbc_stockData.insets = new Insets(0, 0, 5, 0);
		gbc_stockData.gridx = 2;
		gbc_stockData.gridy = 3;
		panel.add(stockData, gbc_stockData);
		stockData.setColumns(10);
		
		JLabel valueLabel = new JLabel("Value");
		GridBagConstraints gbc_valueLabel = new GridBagConstraints();
		gbc_valueLabel.anchor = GridBagConstraints.EAST;
		gbc_valueLabel.insets = new Insets(0, 0, 5, 5);
		gbc_valueLabel.gridwidth = 2;
		gbc_valueLabel.gridx = 0;
		gbc_valueLabel.gridy = 4;
		panel.add(valueLabel, gbc_valueLabel);
				
				valueData = new JTextField();
				GridBagConstraints gbc_valueData = new GridBagConstraints();
				gbc_valueData.fill = GridBagConstraints.BOTH;
				gbc_valueData.insets = new Insets(0, 0, 5, 0);
				gbc_valueData.gridx = 2;
				gbc_valueData.gridy = 4;
				panel.add(valueData, gbc_valueData);
				valueData.setColumns(10);
		
				JLabel medianLabel = new JLabel("Median");
				GridBagConstraints gbc_medianLabel = new GridBagConstraints();
				gbc_medianLabel.anchor = GridBagConstraints.EAST;
				gbc_medianLabel.insets = new Insets(0, 0, 5, 5);
				gbc_medianLabel.gridwidth = 2;
				gbc_medianLabel.gridx = 0;
				gbc_medianLabel.gridy = 5;
				panel.add(medianLabel, gbc_medianLabel);
		
		medianData = new JTextField();
		GridBagConstraints gbc_medianData = new GridBagConstraints();
		gbc_medianData.fill = GridBagConstraints.BOTH;
		gbc_medianData.insets = new Insets(0, 0, 5, 0);
		gbc_medianData.gridx = 2;
		gbc_medianData.gridy = 5;
		panel.add(medianData, gbc_medianData);
		medianData.setColumns(10);
		
		JLabel staticPriceLabel = new JLabel("Static Price");
		GridBagConstraints gbc_staticPriceLabel = new GridBagConstraints();
		gbc_staticPriceLabel.anchor = GridBagConstraints.EAST;
		gbc_staticPriceLabel.insets = new Insets(0, 0, 5, 5);
		gbc_staticPriceLabel.gridwidth = 2;
		gbc_staticPriceLabel.gridx = 0;
		gbc_staticPriceLabel.gridy = 6;
		panel.add(staticPriceLabel, gbc_staticPriceLabel);
		
		staticPriceData = new JTextField();
		GridBagConstraints gbc_staticPriceData = new GridBagConstraints();
		gbc_staticPriceData.fill = GridBagConstraints.BOTH;
		gbc_staticPriceData.insets = new Insets(0, 0, 5, 0);
		gbc_staticPriceData.gridx = 2;
		gbc_staticPriceData.gridy = 6;
		panel.add(staticPriceData, gbc_staticPriceData);
		staticPriceData.setColumns(10);
		
		JLabel startPriceLabel = new JLabel("Start Price");
		GridBagConstraints gbc_startPriceLabel = new GridBagConstraints();
		gbc_startPriceLabel.anchor = GridBagConstraints.EAST;
		gbc_startPriceLabel.insets = new Insets(0, 0, 5, 5);
		gbc_startPriceLabel.gridwidth = 2;
		gbc_startPriceLabel.gridx = 0;
		gbc_startPriceLabel.gridy = 7;
		panel.add(startPriceLabel, gbc_startPriceLabel);
		
		startPriceData = new JTextField();
		GridBagConstraints gbc_startPriceData = new GridBagConstraints();
		gbc_startPriceData.fill = GridBagConstraints.BOTH;
		gbc_startPriceData.insets = new Insets(0, 0, 5, 0);
		gbc_startPriceData.gridx = 2;
		gbc_startPriceData.gridy = 7;
		panel.add(startPriceData, gbc_startPriceData);
		startPriceData.setColumns(10);
		
		JLabel ceilingLabel = new JLabel("Ceiling");
		GridBagConstraints gbc_ceilingLabel = new GridBagConstraints();
		gbc_ceilingLabel.anchor = GridBagConstraints.EAST;
		gbc_ceilingLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ceilingLabel.gridwidth = 2;
		gbc_ceilingLabel.gridx = 0;
		gbc_ceilingLabel.gridy = 8;
		panel.add(ceilingLabel, gbc_ceilingLabel);
		
		ceilingData = new JTextField();
		GridBagConstraints gbc_ceilingData = new GridBagConstraints();
		gbc_ceilingData.fill = GridBagConstraints.BOTH;
		gbc_ceilingData.insets = new Insets(0, 0, 5, 0);
		gbc_ceilingData.gridx = 2;
		gbc_ceilingData.gridy = 8;
		panel.add(ceilingData, gbc_ceilingData);
		ceilingData.setColumns(10);
		
		JLabel floorLabel = new JLabel("Floor");
		GridBagConstraints gbc_floorLabel = new GridBagConstraints();
		gbc_floorLabel.anchor = GridBagConstraints.EAST;
		gbc_floorLabel.insets = new Insets(0, 0, 5, 5);
		gbc_floorLabel.gridwidth = 2;
		gbc_floorLabel.gridx = 0;
		gbc_floorLabel.gridy = 9;
		panel.add(floorLabel, gbc_floorLabel);
		
		floorData = new JTextField();
		GridBagConstraints gbc_floorData = new GridBagConstraints();
		gbc_floorData.fill = GridBagConstraints.BOTH;
		gbc_floorData.insets = new Insets(0, 0, 5, 0);
		gbc_floorData.gridx = 2;
		gbc_floorData.gridy = 9;
		panel.add(floorData, gbc_floorData);
		floorData.setColumns(10);
		
		dataLabel = new JLabel("Data");
		GridBagConstraints gbc_dataLabel = new GridBagConstraints();
		gbc_dataLabel.gridwidth = 2;
		gbc_dataLabel.anchor = GridBagConstraints.EAST;
		gbc_dataLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dataLabel.gridx = 0;
		gbc_dataLabel.gridy = 10;
		panel.add(dataLabel, gbc_dataLabel);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 4;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 10;
		panel.add(scrollPane, gbc_scrollPane);
		
		dataData = new JTextArea();
		dataData.setBackground(Color.WHITE);
		dataData.setLineWrap(true);
		dataData.setWrapStyleWord(true);
		scrollPane.setViewportView(dataData);
		
		initialPricingToggle = new JToggleButton("Initial Pricing");
		GridBagConstraints gbc_initialPricingToggle = new GridBagConstraints();
		gbc_initialPricingToggle.anchor = GridBagConstraints.NORTHWEST;
		gbc_initialPricingToggle.insets = new Insets(0, 0, 5, 0);
		gbc_initialPricingToggle.gridx = 2;
		gbc_initialPricingToggle.gridy = 14;
		panel.add(initialPricingToggle, gbc_initialPricingToggle);
		
		staticPricingToggle = new JToggleButton("Static Pricing");
		GridBagConstraints gbc_staticPricingToggle = new GridBagConstraints();
		gbc_staticPricingToggle.anchor = GridBagConstraints.NORTHWEST;
		gbc_staticPricingToggle.gridx = 2;
		gbc_staticPricingToggle.gridy = 15;
		panel.add(staticPricingToggle, gbc_staticPricingToggle);
				
				JButton updateButton = new JButton("Update");
				updateButton.setBounds(236, 442, 86, 25);
				frame.getContentPane().add(updateButton);
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TradeObject to = getSelectedObject();
				if (to == null) return;
				to.setName(nameData.getText());
				to.setStock(Double.parseDouble(stockData.getText()));
				to.setAliases(CommonFunctions.explode(aliasesData.getText(), ","));
				to.setDisplayName(displayNameData.getText());
				to.setUseInitialPricing(initialPricingToggle.isSelected());
				to.setStatic(staticPricingToggle.isSelected());
				to.setValue(Double.parseDouble(valueData.getText()));
				to.setStaticPrice(Double.parseDouble(staticPriceData.getText()));
				to.setMedian(Double.parseDouble(medianData.getText()));
				to.setStartPrice(Double.parseDouble(startPriceData.getText()));
				to.setCeiling(Double.parseDouble(ceilingData.getText()));
				to.setFloor(Double.parseDouble(floorData.getText()));
				to.setData(dataData.getText());
				updateObjectSelector();
			}
		});
		


		

	}
	
	private class QuickListModel<T> extends AbstractListModel<T> {
		private static final long serialVersionUID = 6026802865693518442L;
		private ArrayList<T> data = new ArrayList<T>();
		@Override
		public T getElementAt(int index) {
			if (data.size() <= index) return null;
			return data.get(index);
		}
		@Override
		public int getSize() {
			return data.size();
		}
		public void clear() {
			data.clear();
		}
		//public void addData(T s) {
		//	data.add(s);
		//	fireIntervalAdded(this, data.indexOf(s), data.indexOf(s));
		//}
		public void addData(List<T> ls) {
			data.addAll(ls);
			fireIntervalAdded(this, 0, data.size() - 1);
		}
	}
	
	private TradeObject getSelectedObject() {
		if (hc != null && hc.enabled()) {
			return hc.getDataManager().getDefaultEconomy().getTradeObject(listObjectSelector.getSelectedValue());
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
		listModel.clear();
		ArrayList<TradeObject> tObjects = hc.getDataManager().getDefaultEconomy().getTradeObjects();
		Collections.sort(tObjects);
		ArrayList<String> names = new ArrayList<String>();
		for (TradeObject t:tObjects) {
			if (t.isCompositeObject()) continue;
			names.add(t.getDisplayName());
		}
		listModel.addData(names);
	}
	
	@EventHandler
	public void onLogMessage(LogEvent event) {
		if (event.getException() != null) event.getException().printStackTrace();
	}
}
