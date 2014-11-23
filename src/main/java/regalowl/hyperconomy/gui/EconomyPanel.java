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


import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class EconomyPanel extends JFrame {


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
	private JPanel panel;
	private JLabel nameLabel;
	private JTextField nameData;
	private JButton editCategoriesButton;
	private JButton btnEditObjectData;
	private boolean fieldsUpdating;
	


	/**
	 * Create the application.
	 */
	public EconomyPanel(HyperConomy hc, HyperEconomy he) {
		this.hc = hc;
		this.he = he;
		initialize();
		loadObjects();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		fieldsUpdating = false;

		setBounds(100, 100, 620, 405);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);

		tradeObjectList = new QuickListModel<String>();
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 12, 266, 356);
		getContentPane().add(scrollPane_1);
		listObjectSelector = new JList<String>(tradeObjectList);
		scrollPane_1.setViewportView(listObjectSelector);
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
				fieldsUpdating = false;
			}
		});

		listObjectSelector.setSelectedIndex(0);
		listObjectSelector.setVisibleRowCount(10);
		
		panel = new JPanel();
		panel.setBounds(278, 12, 328, 356);
		getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{38, 32, 12, 0};
		gbl_panel.rowHeights = new int[]{0, 19, 19, 19, 19, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
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
		nameData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				to.setName(nameData.getText());
			}
		});
		
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
		displayNameData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				to.setDisplayName(displayNameData.getText());
			}
		});
		
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
		aliasesData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				try {
					to.setAliases(CommonFunctions.explode(aliasesData.getText(), ","));
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The aliases must be a comma separated string.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
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
		stockData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				try {
					double value = Double.parseDouble(stockData.getText());
					to.setStock(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The stock must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

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
		valueData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				try {
					double value = Double.parseDouble(valueData.getText());
					to.setValue(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The value must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

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
		medianData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				try {
					double value = Double.parseDouble(medianData.getText());
					to.setMedian(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The median must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
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
		staticPriceData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				try {
					double value = Double.parseDouble(staticPriceData.getText());
					to.setStaticPrice(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The static price must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
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
		startPriceData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				try {
					double value = Double.parseDouble(startPriceData.getText());
					to.setStartPrice(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The start price must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
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
		ceilingData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				try {
					double value = Double.parseDouble(ceilingData.getText());
					to.setCeiling(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The ceiling value must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
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
		floorData.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				try {
					double value = Double.parseDouble(floorData.getText());
					to.setFloor(value);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "The floor value must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		initialPricingToggle = new JToggleButton("Initial Pricing");
		initialPricingToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				to.setUseInitialPricing(initialPricingToggle.isSelected());
			}
		});
		GridBagConstraints gbc_initialPricingToggle = new GridBagConstraints();
		gbc_initialPricingToggle.fill = GridBagConstraints.BOTH;
		gbc_initialPricingToggle.insets = new Insets(0, 0, 5, 0);
		gbc_initialPricingToggle.gridx = 2;
		gbc_initialPricingToggle.gridy = 10;
		panel.add(initialPricingToggle, gbc_initialPricingToggle);
		
		staticPricingToggle = new JToggleButton("Static Pricing");
		staticPricingToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fieldsUpdating) return;
				TradeObject to = getSelectedObject();
				if (to == null) return;
				to.setStatic(staticPricingToggle.isSelected());
			}
		});
		GridBagConstraints gbc_staticPricingToggle = new GridBagConstraints();
		gbc_staticPricingToggle.fill = GridBagConstraints.BOTH;
		gbc_staticPricingToggle.insets = new Insets(0, 0, 5, 0);
		gbc_staticPricingToggle.gridx = 2;
		gbc_staticPricingToggle.gridy = 11;
		panel.add(staticPricingToggle, gbc_staticPricingToggle);
		
		editCategoriesButton = new JButton("Edit Categories");
		editCategoriesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TradeObject to = getSelectedObject();
				if (to == null) return;
				CategoryEditor frame = new CategoryEditor(to);
				frame.setVisible(true);
			}
		});
		GridBagConstraints gbc_editCategoriesButton = new GridBagConstraints();
		gbc_editCategoriesButton.insets = new Insets(0, 0, 5, 0);
		gbc_editCategoriesButton.fill = GridBagConstraints.BOTH;
		gbc_editCategoriesButton.gridx = 2;
		gbc_editCategoriesButton.gridy = 12;
		panel.add(editCategoriesButton, gbc_editCategoriesButton);
		
		btnEditObjectData = new JButton("Edit Object Data");
		GridBagConstraints gbc_btnEditObjectData = new GridBagConstraints();
		gbc_btnEditObjectData.fill = GridBagConstraints.BOTH;
		gbc_btnEditObjectData.gridx = 2;
		gbc_btnEditObjectData.gridy = 13;
		panel.add(btnEditObjectData, gbc_btnEditObjectData);
		btnEditObjectData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				TradeObject to = getSelectedObject();
				if (to == null) return;
				DataEditor frame = new DataEditor(to);
				frame.setVisible(true);
			}
		});

		
	}
	
	private void loadObjects() {
		tradeObjectList.clear();
		ArrayList<TradeObject> tObjects = he.getTradeObjects();
		Collections.sort(tObjects);
		ArrayList<String> names = new ArrayList<String>();
		for (TradeObject t:tObjects) {
			if (t.isCompositeObject()) continue;
			names.add(t.getDisplayName());
		}
		tradeObjectList.addData(names);
	}
	
	private TradeObject getSelectedObject() {
		if (hc != null && hc.enabled()) {
			return he.getTradeObject(listObjectSelector.getSelectedValue());
		}
		return null;
	}
	

}
