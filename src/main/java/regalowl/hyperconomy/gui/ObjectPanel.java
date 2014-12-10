package regalowl.hyperconomy.gui;



import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.simpledatalib.CommonFunctions;


import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComboBox;
import java.awt.Color;


public class ObjectPanel extends JFrame {


	private static final long serialVersionUID = 6133250795371176846L;
	private HyperConomy hc;
	private HyperEconomy he;
	private JTextField stockData;
	JList<String> listObjectSelector;
	QuickListModel<String> tradeObjectList;
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
	private JScrollPane scrollPane_1;
	private JPanel settingsPanel;
	private JLabel nameLabel;
	private JTextField nameData;
	private JButton editCategoriesButton;
	private JButton btnEditObjectData;
	private boolean fieldsUpdating;
	JComboBox<String> categoryComboBox;
	private ObjectPanel economyPanel;
	private boolean loadingCategories;
	private JTextField purchasePriceField;
	private JTextField sellPriceField;


	/**
	 * Create the application.
	 */
	public ObjectPanel(HyperConomy hc, HyperEconomy he) {
		setTitle("Object Editor");
		this.hc = hc;
		this.he = he;
		this.economyPanel = this;
		initialize();
		loadCatgories();
		loadObjects();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		fieldsUpdating = false;
		loadingCategories = false;
		setBounds(100, 100, 642, 527);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);
		
		categoryComboBox = new JComboBox<String>();
		categoryComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!loadingCategories) loadObjects();
			}
		});
		categoryComboBox.setToolTipText("Select a category.");
		categoryComboBox.setBounds(12, 12, 266, 24);
		getContentPane().add(categoryComboBox);


		tradeObjectList = new QuickListModel<String>();
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 35, 266, 352);
		getContentPane().add(scrollPane_1);
		
		settingsPanel = new JPanel();
		settingsPanel.setBackground(new Color(248, 248, 255));
		settingsPanel.setBounds(288, 12, 328, 375);
		getContentPane().add(settingsPanel);
		GridBagLayout gbl_settingsPanel = new GridBagLayout();
		gbl_settingsPanel.columnWidths = new int[]{38, 32, 12, 0};
		gbl_settingsPanel.rowHeights = new int[]{0, 19, 19, 19, 19, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_settingsPanel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_settingsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		settingsPanel.setLayout(gbl_settingsPanel);
		
		nameLabel = new JLabel("Name");
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.anchor = GridBagConstraints.EAST;
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 0;
		settingsPanel.add(nameLabel, gbc_nameLabel);
		
		nameData = new JTextField();
		GridBagConstraints gbc_nameData = new GridBagConstraints();
		gbc_nameData.insets = new Insets(0, 0, 5, 0);
		gbc_nameData.fill = GridBagConstraints.BOTH;
		gbc_nameData.gridx = 2;
		gbc_nameData.gridy = 0;
		settingsPanel.add(nameData, gbc_nameData);
		nameData.setColumns(10);
		
		displayNameLabel = new JLabel("Display Name");
		GridBagConstraints gbc_displayNameLabel = new GridBagConstraints();
		gbc_displayNameLabel.anchor = GridBagConstraints.EAST;
		gbc_displayNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_displayNameLabel.gridwidth = 2;
		gbc_displayNameLabel.gridx = 0;
		gbc_displayNameLabel.gridy = 1;
		settingsPanel.add(displayNameLabel, gbc_displayNameLabel);
		
		displayNameData = new JTextField();
		GridBagConstraints gbc_displayNameData = new GridBagConstraints();
		gbc_displayNameData.fill = GridBagConstraints.BOTH;
		gbc_displayNameData.insets = new Insets(0, 0, 5, 0);
		gbc_displayNameData.gridx = 2;
		gbc_displayNameData.gridy = 1;
		settingsPanel.add(displayNameData, gbc_displayNameData);
		displayNameData.setColumns(10);
		
		aliasesLabel = new JLabel("Aliases");
		GridBagConstraints gbc_aliasesLabel = new GridBagConstraints();
		gbc_aliasesLabel.anchor = GridBagConstraints.EAST;
		gbc_aliasesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_aliasesLabel.gridwidth = 2;
		gbc_aliasesLabel.gridx = 0;
		gbc_aliasesLabel.gridy = 2;
		settingsPanel.add(aliasesLabel, gbc_aliasesLabel);
		
		aliasesData = new JTextField();
		GridBagConstraints gbc_aliasesData = new GridBagConstraints();
		gbc_aliasesData.fill = GridBagConstraints.BOTH;
		gbc_aliasesData.insets = new Insets(0, 0, 5, 0);
		gbc_aliasesData.gridx = 2;
		gbc_aliasesData.gridy = 2;
		settingsPanel.add(aliasesData, gbc_aliasesData);
		aliasesData.setColumns(10);

		
		JLabel stockLabel = new JLabel("Stock");
		GridBagConstraints gbc_stockLabel = new GridBagConstraints();
		gbc_stockLabel.anchor = GridBagConstraints.EAST;
		gbc_stockLabel.insets = new Insets(0, 0, 5, 5);
		gbc_stockLabel.gridwidth = 2;
		gbc_stockLabel.gridx = 0;
		gbc_stockLabel.gridy = 3;
		settingsPanel.add(stockLabel, gbc_stockLabel);
		//Font font = new Font("Verdana", Font.PLAIN, 9);

		
		stockData = new JTextField();
		GridBagConstraints gbc_stockData = new GridBagConstraints();
		gbc_stockData.fill = GridBagConstraints.BOTH;
		gbc_stockData.insets = new Insets(0, 0, 5, 0);
		gbc_stockData.gridx = 2;
		gbc_stockData.gridy = 3;
		settingsPanel.add(stockData, gbc_stockData);
		stockData.setColumns(10);

		JLabel valueLabel = new JLabel("Value");
		GridBagConstraints gbc_valueLabel = new GridBagConstraints();
		gbc_valueLabel.anchor = GridBagConstraints.EAST;
		gbc_valueLabel.insets = new Insets(0, 0, 5, 5);
		gbc_valueLabel.gridwidth = 2;
		gbc_valueLabel.gridx = 0;
		gbc_valueLabel.gridy = 4;
		settingsPanel.add(valueLabel, gbc_valueLabel);

		valueData = new JTextField();
		GridBagConstraints gbc_valueData = new GridBagConstraints();
		gbc_valueData.fill = GridBagConstraints.BOTH;
		gbc_valueData.insets = new Insets(0, 0, 5, 0);
		gbc_valueData.gridx = 2;
		gbc_valueData.gridy = 4;
		settingsPanel.add(valueData, gbc_valueData);
		valueData.setColumns(10);


		JLabel medianLabel = new JLabel("Median");
		GridBagConstraints gbc_medianLabel = new GridBagConstraints();
		gbc_medianLabel.anchor = GridBagConstraints.EAST;
		gbc_medianLabel.insets = new Insets(0, 0, 5, 5);
		gbc_medianLabel.gridwidth = 2;
		gbc_medianLabel.gridx = 0;
		gbc_medianLabel.gridy = 5;
		settingsPanel.add(medianLabel, gbc_medianLabel);

		medianData = new JTextField();
		GridBagConstraints gbc_medianData = new GridBagConstraints();
		gbc_medianData.fill = GridBagConstraints.BOTH;
		gbc_medianData.insets = new Insets(0, 0, 5, 0);
		gbc_medianData.gridx = 2;
		gbc_medianData.gridy = 5;
		settingsPanel.add(medianData, gbc_medianData);
		medianData.setColumns(10);

		
		JLabel staticPriceLabel = new JLabel("Static Price");
		GridBagConstraints gbc_staticPriceLabel = new GridBagConstraints();
		gbc_staticPriceLabel.anchor = GridBagConstraints.EAST;
		gbc_staticPriceLabel.insets = new Insets(0, 0, 5, 5);
		gbc_staticPriceLabel.gridwidth = 2;
		gbc_staticPriceLabel.gridx = 0;
		gbc_staticPriceLabel.gridy = 6;
		settingsPanel.add(staticPriceLabel, gbc_staticPriceLabel);
		
		staticPriceData = new JTextField();
		GridBagConstraints gbc_staticPriceData = new GridBagConstraints();
		gbc_staticPriceData.fill = GridBagConstraints.BOTH;
		gbc_staticPriceData.insets = new Insets(0, 0, 5, 0);
		gbc_staticPriceData.gridx = 2;
		gbc_staticPriceData.gridy = 6;
		settingsPanel.add(staticPriceData, gbc_staticPriceData);
		staticPriceData.setColumns(10);

		
		JLabel startPriceLabel = new JLabel("Start Price");
		GridBagConstraints gbc_startPriceLabel = new GridBagConstraints();
		gbc_startPriceLabel.anchor = GridBagConstraints.EAST;
		gbc_startPriceLabel.insets = new Insets(0, 0, 5, 5);
		gbc_startPriceLabel.gridwidth = 2;
		gbc_startPriceLabel.gridx = 0;
		gbc_startPriceLabel.gridy = 7;
		settingsPanel.add(startPriceLabel, gbc_startPriceLabel);
		
		startPriceData = new JTextField();
		GridBagConstraints gbc_startPriceData = new GridBagConstraints();
		gbc_startPriceData.fill = GridBagConstraints.BOTH;
		gbc_startPriceData.insets = new Insets(0, 0, 5, 0);
		gbc_startPriceData.gridx = 2;
		gbc_startPriceData.gridy = 7;
		settingsPanel.add(startPriceData, gbc_startPriceData);
		startPriceData.setColumns(10);
		
		JLabel ceilingLabel = new JLabel("Ceiling");
		GridBagConstraints gbc_ceilingLabel = new GridBagConstraints();
		gbc_ceilingLabel.anchor = GridBagConstraints.EAST;
		gbc_ceilingLabel.insets = new Insets(0, 0, 5, 5);
		gbc_ceilingLabel.gridwidth = 2;
		gbc_ceilingLabel.gridx = 0;
		gbc_ceilingLabel.gridy = 8;
		settingsPanel.add(ceilingLabel, gbc_ceilingLabel);
		
		ceilingData = new JTextField();
		GridBagConstraints gbc_ceilingData = new GridBagConstraints();
		gbc_ceilingData.fill = GridBagConstraints.BOTH;
		gbc_ceilingData.insets = new Insets(0, 0, 5, 0);
		gbc_ceilingData.gridx = 2;
		gbc_ceilingData.gridy = 8;
		settingsPanel.add(ceilingData, gbc_ceilingData);
		ceilingData.setColumns(10);
		
		JLabel floorLabel = new JLabel("Floor");
		GridBagConstraints gbc_floorLabel = new GridBagConstraints();
		gbc_floorLabel.anchor = GridBagConstraints.EAST;
		gbc_floorLabel.insets = new Insets(0, 0, 5, 5);
		gbc_floorLabel.gridwidth = 2;
		gbc_floorLabel.gridx = 0;
		gbc_floorLabel.gridy = 9;
		settingsPanel.add(floorLabel, gbc_floorLabel);
		
		floorData = new JTextField();
		GridBagConstraints gbc_floorData = new GridBagConstraints();
		gbc_floorData.fill = GridBagConstraints.BOTH;
		gbc_floorData.insets = new Insets(0, 0, 5, 0);
		gbc_floorData.gridx = 2;
		gbc_floorData.gridy = 9;
		settingsPanel.add(floorData, gbc_floorData);
		floorData.setColumns(10);
		
		initialPricingToggle = new JToggleButton("Initial Pricing");

		GridBagConstraints gbc_initialPricingToggle = new GridBagConstraints();
		gbc_initialPricingToggle.fill = GridBagConstraints.BOTH;
		gbc_initialPricingToggle.insets = new Insets(0, 0, 5, 0);
		gbc_initialPricingToggle.gridx = 2;
		gbc_initialPricingToggle.gridy = 10;
		settingsPanel.add(initialPricingToggle, gbc_initialPricingToggle);
		
		staticPricingToggle = new JToggleButton("Static Pricing");

		GridBagConstraints gbc_staticPricingToggle = new GridBagConstraints();
		gbc_staticPricingToggle.fill = GridBagConstraints.BOTH;
		gbc_staticPricingToggle.insets = new Insets(0, 0, 5, 0);
		gbc_staticPricingToggle.gridx = 2;
		gbc_staticPricingToggle.gridy = 11;
		settingsPanel.add(staticPricingToggle, gbc_staticPricingToggle);
		
		editCategoriesButton = new JButton("Edit Categories");
		editCategoriesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TradeObject to = getSelectedObject();
				if (to == null) return;
				CategoryEditor frame = new CategoryEditor(to, economyPanel);
				frame.setVisible(true);
			}
		});
		GridBagConstraints gbc_editCategoriesButton = new GridBagConstraints();
		gbc_editCategoriesButton.insets = new Insets(0, 0, 5, 0);
		gbc_editCategoriesButton.fill = GridBagConstraints.BOTH;
		gbc_editCategoriesButton.gridx = 2;
		gbc_editCategoriesButton.gridy = 12;
		settingsPanel.add(editCategoriesButton, gbc_editCategoriesButton);
		
		btnEditObjectData = new JButton("Edit Object Data");
		GridBagConstraints gbc_btnEditObjectData = new GridBagConstraints();
		gbc_btnEditObjectData.fill = GridBagConstraints.BOTH;
		gbc_btnEditObjectData.gridx = 2;
		gbc_btnEditObjectData.gridy = 13;
		settingsPanel.add(btnEditObjectData, gbc_btnEditObjectData);
		
		JPanel pricePanel = new JPanel();
		pricePanel.setBackground(new Color(248, 248, 255));
		pricePanel.setBounds(12, 398, 600, 80);
		getContentPane().add(pricePanel);
		GridBagLayout gbl_pricePanel = new GridBagLayout();
		gbl_pricePanel.columnWidths = new int[]{300, 300, 0};
		gbl_pricePanel.rowHeights = new int[]{15, 0, 0, 0};
		gbl_pricePanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_pricePanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		pricePanel.setLayout(gbl_pricePanel);
		
		JLabel purchasePriceLabel = new JLabel("Purchase Price");
		GridBagConstraints gbc_purchasePriceLabel = new GridBagConstraints();
		gbc_purchasePriceLabel.insets = new Insets(0, 0, 5, 5);
		gbc_purchasePriceLabel.gridx = 0;
		gbc_purchasePriceLabel.gridy = 0;
		pricePanel.add(purchasePriceLabel, gbc_purchasePriceLabel);
		
		purchasePriceField = new JTextField();
		purchasePriceField.setBackground(new Color(250, 250, 210));
		purchasePriceField.setEditable(false);
		GridBagConstraints gbc_purchasePriceField = new GridBagConstraints();
		gbc_purchasePriceField.insets = new Insets(0, 0, 5, 0);
		gbc_purchasePriceField.fill = GridBagConstraints.BOTH;
		gbc_purchasePriceField.gridx = 1;
		gbc_purchasePriceField.gridy = 0;
		pricePanel.add(purchasePriceField, gbc_purchasePriceField);
		purchasePriceField.setColumns(10);
		
		JLabel sellPriceLabel = new JLabel("Sell Price");
		GridBagConstraints gbc_sellPriceLabel = new GridBagConstraints();
		gbc_sellPriceLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sellPriceLabel.gridx = 0;
		gbc_sellPriceLabel.gridy = 1;
		pricePanel.add(sellPriceLabel, gbc_sellPriceLabel);
		
		sellPriceField = new JTextField();
		sellPriceField.setBackground(new Color(250, 250, 210));
		sellPriceField.setEditable(false);
		GridBagConstraints gbc_sellPriceField = new GridBagConstraints();
		gbc_sellPriceField.insets = new Insets(0, 0, 5, 0);
		gbc_sellPriceField.fill = GridBagConstraints.BOTH;
		gbc_sellPriceField.gridx = 1;
		gbc_sellPriceField.gridy = 1;
		pricePanel.add(sellPriceField, gbc_sellPriceField);
		sellPriceField.setColumns(10);
		
		JButton saveButton = new JButton("Save Changes");
		GridBagConstraints gbc_saveButton = new GridBagConstraints();
		gbc_saveButton.fill = GridBagConstraints.BOTH;
		gbc_saveButton.insets = new Insets(0, 0, 0, 5);
		gbc_saveButton.gridx = 0;
		gbc_saveButton.gridy = 2;
		pricePanel.add(saveButton, gbc_saveButton);
		saveButton.setBackground(new Color(152, 251, 152));
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				to.setName(nameData.getText());
				to.setName(nameData.getText());
				to.setDisplayName(displayNameData.getText());
				try {
					to.setAliases(CommonFunctions.explode(aliasesData.getText(), ","));
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The aliases must be a comma separated string.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				try {
					double value = Double.parseDouble(stockData.getText());
					to.setStock(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The stock must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				try {
					double value = Double.parseDouble(valueData.getText());
					to.setValue(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The value must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				try {
					double value = Double.parseDouble(medianData.getText());
					to.setMedian(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The median must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				try {
					double value = Double.parseDouble(staticPriceData.getText());
					to.setStaticPrice(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The static price must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				try {
					double value = Double.parseDouble(startPriceData.getText());
					to.setStartPrice(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The start price must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				try {
					double value = Double.parseDouble(ceilingData.getText());
					to.setCeiling(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The ceiling value must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				try {
					double value = Double.parseDouble(floorData.getText());
					to.setFloor(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The floor value must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				to.setUseInitialPricing(initialPricingToggle.isSelected());
				to.setStatic(staticPricingToggle.isSelected());
				updatePrice(to);
				loadObjects();
			}
		});
		
		JButton deleteButton = new JButton("Delete Object");
		GridBagConstraints gbc_deleteButton = new GridBagConstraints();
		gbc_deleteButton.fill = GridBagConstraints.BOTH;
		gbc_deleteButton.gridx = 1;
		gbc_deleteButton.gridy = 2;
		pricePanel.add(deleteButton, gbc_deleteButton);
		deleteButton.setBackground(new Color(245, 222, 179));
		listObjectSelector = new JList<String>(tradeObjectList);
		listObjectSelector.setBounds(12, 35, 264, 352);
		getContentPane().add(listObjectSelector);
		listObjectSelector.setBackground(new Color(248, 248, 255));
		listObjectSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listObjectSelector.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				TradeObject to = getSelectedObject();
				if (to == null) return;
				fieldsUpdating = true;
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
				updatePrice(to);
				fieldsUpdating = false;
			}
		});
		
				listObjectSelector.setSelectedIndex(0);
				listObjectSelector.setVisibleRowCount(10);
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
		        if (JOptionPane.showConfirmDialog(getContentPane(), "Are you sure you want to delete this object?", "Object Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
		        	return;
		        }
				to.delete();
				loadObjects();
			}
		});
		btnEditObjectData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TradeObject to = getSelectedObject();
				if (to == null) return;
				DataEditor frame = new DataEditor(to);
				frame.setVisible(true);
			}
		});


		
	}
	
	public void loadCatgories() {
		loadingCategories = true;
		int selected = categoryComboBox.getSelectedIndex();
		if (selected < 0) selected = 0;
		categoryComboBox.removeAllItems();
		ArrayList<String> categories = hc.getDataManager().getCategories();
		Collections.sort(categories);
		categoryComboBox.addItem("all");
		categoryComboBox.addItem("uncategorized");
		for (String cat:categories) {
			categoryComboBox.addItem(cat);
		}
		categoryComboBox.setSelectedIndex(selected);
		loadingCategories = false;
	}
	
	private void loadObjects() {
		String category = categoryComboBox.getSelectedItem().toString();
		tradeObjectList.clear();
		ArrayList<TradeObject> tObjects = he.getTradeObjects();
		Collections.sort(tObjects);
		ArrayList<String> names = new ArrayList<String>();
		if (category.equals("all")) {
			for (TradeObject t:tObjects) {
				names.add(t.getDisplayName());
			}
		} else if (category.equals("uncategorized")) {
			for (TradeObject t:tObjects) {
				if (t.getCategories().size() == 0) names.add(t.getDisplayName());
			}
		} else {
			for (TradeObject t:tObjects) {
				if (t.inCategory(category)) names.add(t.getDisplayName());
			}
		}
		tradeObjectList.addData(names);
	}
	
	private TradeObject getSelectedObject() {
		if (hc != null && hc.enabled()) {
			return he.getTradeObject(listObjectSelector.getSelectedValue());
		}
		return null;
	}
	
	private void updatePrice(TradeObject to) {
		sellPriceField.setText(CommonFunctions.twoDecimals(to.getSellPrice(1))+"");
		purchasePriceField.setText(CommonFunctions.twoDecimals(to.getBuyPrice(1))+"");
	}
	
	public void displayInfoBox(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	public void displayErrorBox(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}
}
