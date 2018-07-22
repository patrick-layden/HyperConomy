package regalowl.hyperconomy.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.HyperEconomyCreationEvent;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.RequestGUIChangeEvent;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.simpledatalib.SimpleDataLib;
import regalowl.simpledatalib.event.SDLEvent;
import regalowl.simpledatalib.event.SDLEventListener;
import regalowl.simpledatalib.events.LogEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import java.awt.Color;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;

public class MainPanel implements SDLEventListener, HyperEventListener {

	private JFrame frmEconomyEditor;
	private HyperConomy hc;
	private SimpleDataLib sdl;
	
	
	private JTextField addEconomyNameField;
	private JLabel lblAddAnEconomy;
	private JButton addEconomyButton;
	private JButton btnCloneSelected;
	private JButton btnDeleteEconomy;
	private JList<String> economySelectList;
	QuickListModel<String> economyList;
	private JScrollPane scrollPane;
	private JTextField newValueField;
	private JComboBox<String> newValueType;
	private JButton stockToMedianButton;
	private JPanel panel;
	private JLabel remoteGUIStatusTextField;
	private JTextArea remoteGUIInfoTextArea;
	private JScrollPane remoteGUIScrollPane;
	private JPanel panel_1;
	private JPanel panel_2;
	private JLabel guiSyncStatusLabel;
	private JPanel panel_3;
	
	private JDialog waitMessage;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new MainPanel();
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
		
		MineCraftConnector mc = new GUIConnector();
		this.hc = mc.getHC();
		hc.load();
		hc.getHyperEventHandler().registerListener(this);
		hc.getSimpleDataLib().getEventPublisher().registerListener(this);
		this.sdl = hc.gSDL();
		sdl.setDebug(true);
		hc.enable();
		
		
		JOptionPane optionPane = new JOptionPane("Loading and downloading dependencies...please wait.", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		waitMessage = new JDialog();
		waitMessage.setTitle("Loading....");
		waitMessage.setModal(true);
		waitMessage.setContentPane(optionPane);
		waitMessage.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		waitMessage.pack();
		waitMessage.setVisible(true);
		
		
		
		

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		
		frmEconomyEditor = new JFrame();
		frmEconomyEditor.setTitle("Economy Editor");
		frmEconomyEditor.setBounds(100, 100, 600, 580);
		frmEconomyEditor.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frmEconomyEditor.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frmEconomyEditor, "Exit?", "Exit Application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		    		if (hc.getRemoteGUIServer().enabled()) {
		    			hc.getRemoteGUIServer().disconnect();
		    		}
		        	if (hc != null) hc.disable(false);
		            System.exit(0);
		        }
		    }
		});
		frmEconomyEditor.getContentPane().setLayout(null);
		
		panel = new JPanel();
		panel.setBackground(new Color(248, 248, 255));
		panel.setBounds(207, 12, 363, 502);
		frmEconomyEditor.getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{175, 175, 0};
		gbl_panel.rowHeights = new int[]{40, 40, 40, 40, 40, 40, 40, 32, 32, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		lblAddAnEconomy = new JLabel("New Economy Name");
		lblAddAnEconomy.setBackground(SystemColor.controlHighlight);
		GridBagConstraints gbc_lblAddAnEconomy = new GridBagConstraints();
		gbc_lblAddAnEconomy.fill = GridBagConstraints.VERTICAL;
		gbc_lblAddAnEconomy.insets = new Insets(0, 0, 5, 5);
		gbc_lblAddAnEconomy.gridx = 0;
		gbc_lblAddAnEconomy.gridy = 0;
		panel.add(lblAddAnEconomy, gbc_lblAddAnEconomy);
		
		JLabel lblNewValue = new JLabel("New Value");
		lblNewValue.setBackground(SystemColor.controlHighlight);
		GridBagConstraints gbc_lblNewValue = new GridBagConstraints();
		gbc_lblNewValue.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewValue.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewValue.gridx = 1;
		gbc_lblNewValue.gridy = 0;
		panel.add(lblNewValue, gbc_lblNewValue);
		
		addEconomyNameField = new JTextField();
		GridBagConstraints gbc_addEconomyNameField = new GridBagConstraints();
		gbc_addEconomyNameField.fill = GridBagConstraints.BOTH;
		gbc_addEconomyNameField.insets = new Insets(0, 0, 5, 5);
		gbc_addEconomyNameField.gridx = 0;
		gbc_addEconomyNameField.gridy = 1;
		panel.add(addEconomyNameField, gbc_addEconomyNameField);
		addEconomyNameField.setToolTipText("Input the new economy's name.");
		addEconomyNameField.setColumns(10);
		
		newValueField = new JTextField();
		newValueField.setToolTipText("Changes will be applied to all objects in the economy.");
		GridBagConstraints gbc_newValueField = new GridBagConstraints();
		gbc_newValueField.insets = new Insets(0, 0, 5, 0);
		gbc_newValueField.fill = GridBagConstraints.BOTH;
		gbc_newValueField.gridx = 1;
		gbc_newValueField.gridy = 1;
		panel.add(newValueField, gbc_newValueField);
		newValueField.setColumns(10);
		
		addEconomyButton = new JButton("Add");
		GridBagConstraints gbc_addEconomyButton = new GridBagConstraints();
		gbc_addEconomyButton.fill = GridBagConstraints.BOTH;
		gbc_addEconomyButton.insets = new Insets(0, 0, 5, 5);
		gbc_addEconomyButton.gridx = 0;
		gbc_addEconomyButton.gridy = 2;
		panel.add(addEconomyButton, gbc_addEconomyButton);
		addEconomyButton.setToolTipText("Adds a new economy with the name specified above.");
		
		newValueType = new JComboBox<String>();
		newValueType.setToolTipText("Changes will be applied to all objects in the economy.");
		GridBagConstraints gbc_newValueType = new GridBagConstraints();
		gbc_newValueType.insets = new Insets(0, 0, 5, 0);
		gbc_newValueType.fill = GridBagConstraints.BOTH;
		gbc_newValueType.gridx = 1;
		gbc_newValueType.gridy = 2;
		panel.add(newValueType, gbc_newValueType);
		newValueType.addItem("Stock");
		newValueType.addItem("Use Initial Pricing");
		newValueType.addItem("Use Static Pricing");
		newValueType.addItem("Value");
		newValueType.addItem("Median");
		newValueType.addItem("Ceiling");
		newValueType.addItem("Floor");
		newValueType.addItem("Static Price");
		newValueType.addItem("Initial Price");
		newValueType.addItem("Floor");
		
		btnCloneSelected = new JButton("Clone");
		GridBagConstraints gbc_btnCloneSelected = new GridBagConstraints();
		gbc_btnCloneSelected.fill = GridBagConstraints.BOTH;
		gbc_btnCloneSelected.insets = new Insets(0, 0, 5, 5);
		gbc_btnCloneSelected.gridx = 0;
		gbc_btnCloneSelected.gridy = 3;
		panel.add(btnCloneSelected, gbc_btnCloneSelected);
		btnCloneSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = addEconomyNameField.getText();
				if (newName == null || newName.equals("")) return;
				if (hc.getDataManager().economyExists(newName)) return;
				HyperEconomy he = getSelectedEconomy();
				if (he == null) return;
				hc.getDataManager().createNewEconomy(newName, he.getName(), true);
			}
		});
		btnCloneSelected.setToolTipText("Clones the economy selected on the left to the name specified above.");
		
		JButton btnUpdateEconomy = new JButton("Update Economy");
		btnUpdateEconomy.setToolTipText("Changes will be applied to all objects in the economy.");
		btnUpdateEconomy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String type = newValueType.getSelectedItem().toString();
				HyperEconomy he = getSelectedEconomy();
				if (he == null) return;
		        if (JOptionPane.showConfirmDialog(frmEconomyEditor, "Are you sure you want to update all objects in the economy?", "Update Economy", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
		        	return;
		        }
				if (type.equals("Stock")) {
					try {
						double value = Double.parseDouble(newValueField.getText());
						for (TradeObject to:he.getTradeObjects()) {
							if (to.isCompositeObject()) continue;
							to.setStock(value);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "The stock must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (type.equals("Use Initial Pricing")) {
					try {
						boolean value = Boolean.parseBoolean(newValueField.getText());
						for (TradeObject to:he.getTradeObjects()) {
							if (to.isCompositeObject()) continue;
							to.setUseInitialPricing(value);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "The value must be true or false.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (type.equals("Use Static Pricing")) {
					try {
						boolean value = Boolean.parseBoolean(newValueField.getText());
						for (TradeObject to:he.getTradeObjects()) {
							if (to.isCompositeObject()) continue;
							to.setStatic(value);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "The value must be true or false.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (type.equals("Value")) {
					try {
						double value = Double.parseDouble(newValueField.getText());
						for (TradeObject to:he.getTradeObjects()) {
							if (to.isCompositeObject()) continue;
							to.setValue(value);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "The value must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (type.equals("Median")) {
					try {
						double value = Double.parseDouble(newValueField.getText());
						for (TradeObject to:he.getTradeObjects()) {
							if (to.isCompositeObject()) continue;
							to.setMedian(value);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "The median must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (type.equals("Ceiling")) {
					try {
						double value = Double.parseDouble(newValueField.getText());
						for (TradeObject to:he.getTradeObjects()) {
							if (to.isCompositeObject()) continue;
							to.setCeiling(value);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "The ceiling value must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (type.equals("Floor")) {
					try {
						double value = Double.parseDouble(newValueField.getText());
						for (TradeObject to:he.getTradeObjects()) {
							if (to.isCompositeObject()) continue;
							to.setFloor(value);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "The floor value must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (type.equals("Static Price")) {
					try {
						double value = Double.parseDouble(newValueField.getText());
						for (TradeObject to:he.getTradeObjects()) {
							if (to.isCompositeObject()) continue;
							to.setStaticPrice(value);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "The static price must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else if (type.equals("Initial Price")) {
					try {
						double value = Double.parseDouble(newValueField.getText());
						for (TradeObject to:he.getTradeObjects()) {
							if (to.isCompositeObject()) continue;
							to.setStartPrice(value);
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "The start price must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		GridBagConstraints gbc_btnUpdateEconomy = new GridBagConstraints();
		gbc_btnUpdateEconomy.fill = GridBagConstraints.BOTH;
		gbc_btnUpdateEconomy.insets = new Insets(0, 0, 5, 0);
		gbc_btnUpdateEconomy.gridx = 1;
		gbc_btnUpdateEconomy.gridy = 3;
		panel.add(btnUpdateEconomy, gbc_btnUpdateEconomy);
		
		JButton btnEditEconomy = new JButton("Edit");
		GridBagConstraints gbc_btnEditEconomy = new GridBagConstraints();
		gbc_btnEditEconomy.fill = GridBagConstraints.BOTH;
		gbc_btnEditEconomy.insets = new Insets(0, 0, 5, 5);
		gbc_btnEditEconomy.gridx = 0;
		gbc_btnEditEconomy.gridy = 4;
		panel.add(btnEditEconomy, gbc_btnEditEconomy);
		
		stockToMedianButton = new JButton("Set Stock to Median");
		stockToMedianButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		        if (JOptionPane.showConfirmDialog(frmEconomyEditor, "Are you sure you want to set stocks to their median value for this economy?", 
		        		"Set Stock To Median", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					HyperEconomy he = getSelectedEconomy();
					if (he == null) return;
					for (TradeObject to:he.getTradeObjects()) {
						if (to.isCompositeObject()) continue;
						to.setStock(to.getMedian());
					}
		        }
			}
		});
		stockToMedianButton.setToolTipText("Sets the stock of all objects in the economy to the median.");
		GridBagConstraints gbc_stockToMedianButton = new GridBagConstraints();
		gbc_stockToMedianButton.fill = GridBagConstraints.BOTH;
		gbc_stockToMedianButton.insets = new Insets(0, 0, 5, 0);
		gbc_stockToMedianButton.gridx = 1;
		gbc_stockToMedianButton.gridy = 4;
		panel.add(stockToMedianButton, gbc_stockToMedianButton);
		
		btnDeleteEconomy = new JButton("Delete");
		btnDeleteEconomy.setForeground(new Color(255, 250, 250));
		btnDeleteEconomy.setBackground(new Color(220, 20, 60));
		GridBagConstraints gbc_btnDeleteEconomy = new GridBagConstraints();
		gbc_btnDeleteEconomy.insets = new Insets(0, 0, 5, 5);
		gbc_btnDeleteEconomy.fill = GridBagConstraints.BOTH;
		gbc_btnDeleteEconomy.gridx = 0;
		gbc_btnDeleteEconomy.gridy = 5;
		panel.add(btnDeleteEconomy, gbc_btnDeleteEconomy);
		
		panel_1 = new JPanel();
		panel_1.setBackground(UIManager.getColor("MenuItem.selectionForeground"));
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, SystemColor.control, null, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 7;
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{175, 0};
		gbl_panel_1.rowHeights = new int[]{32, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblRemoteGui = new JLabel("Remote GUI");
		lblRemoteGui.setForeground(Color.WHITE);
		lblRemoteGui.setBackground(UIManager.getColor("OptionPane.errorDialog.titlePane.foreground"));
		GridBagConstraints gbc_lblRemoteGui = new GridBagConstraints();
		gbc_lblRemoteGui.fill = GridBagConstraints.VERTICAL;
		gbc_lblRemoteGui.gridx = 0;
		gbc_lblRemoteGui.gridy = 0;
		panel_1.add(lblRemoteGui, gbc_lblRemoteGui);
		
		panel_2 = new JPanel();
		panel_2.setBackground(Color.LIGHT_GRAY);
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.insets = new Insets(0, 0, 5, 5);
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 8;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{175, 0};
		gbl_panel_2.rowHeights = new int[]{32, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		remoteGUIStatusTextField = new JLabel();
		remoteGUIStatusTextField.setText("Disabled");
		GridBagConstraints gbc_remoteGUIStatusTextField = new GridBagConstraints();
		gbc_remoteGUIStatusTextField.fill = GridBagConstraints.VERTICAL;
		gbc_remoteGUIStatusTextField.gridx = 0;
		gbc_remoteGUIStatusTextField.gridy = 0;
		panel_2.add(remoteGUIStatusTextField, gbc_remoteGUIStatusTextField);
		//remoteGUIStatusTextField.setColumns(10);
		if (hc.getRemoteGUIServer().enabled()) {
			panel_2.setBackground(Color.YELLOW);
			remoteGUIStatusTextField.setText("Connecting...");
		} else {
			remoteGUIStatusTextField.setText("Disabled");
		}
		
		panel_3 = new JPanel();
		panel_3.setBackground(Color.LIGHT_GRAY);
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 8;
		panel.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{175, 0};
		gbl_panel_3.rowHeights = new int[]{32, 0};
		gbl_panel_3.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		guiSyncStatusLabel = new JLabel("N/A");
		if (hc.getRemoteGUIServer().enabled()) {
			guiSyncStatusLabel.setText("Synchronizing...");
			panel_3.setBackground(Color.YELLOW);
		}
		GridBagConstraints gbc_guiSyncStatusLabel = new GridBagConstraints();
		gbc_guiSyncStatusLabel.fill = GridBagConstraints.VERTICAL;
		gbc_guiSyncStatusLabel.gridx = 0;
		gbc_guiSyncStatusLabel.gridy = 0;
		panel_3.add(guiSyncStatusLabel, gbc_guiSyncStatusLabel);
		guiSyncStatusLabel.setToolTipText("Displays the remote GUI synchronization state.");
		
		
		remoteGUIScrollPane = new JScrollPane();
		GridBagConstraints gbc_remoteGUIScrollPane = new GridBagConstraints();
		gbc_remoteGUIScrollPane.gridwidth = 2;
		gbc_remoteGUIScrollPane.fill = GridBagConstraints.BOTH;
		gbc_remoteGUIScrollPane.gridx = 0;
		gbc_remoteGUIScrollPane.gridy = 9;
		panel.add(remoteGUIScrollPane, gbc_remoteGUIScrollPane);
		
		remoteGUIInfoTextArea = new JTextArea();
		remoteGUIScrollPane.setViewportView(remoteGUIInfoTextArea);
		remoteGUIInfoTextArea.setEnabled(false);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(9, 12, 186, 502);
		frmEconomyEditor.getContentPane().add(scrollPane);
		
		economyList = new QuickListModel<String>();
		economySelectList = new JList<String>(economyList);
		economySelectList.setBounds(9, 12, 184, 157);
		//frmEconomyEditor.getContentPane().add(economySelectList);
		economySelectList.setBackground(new Color(248, 248, 255));
		economySelectList.setToolTipText("Select an economy.");
		scrollPane.setViewportView(economySelectList);
		btnDeleteEconomy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HyperEconomy he = getSelectedEconomy();
				if (he == null) return;
				if (he.getName().equals("default")) {
					JOptionPane.showMessageDialog(null, "You can't delete the default economy.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
		        if (JOptionPane.showConfirmDialog(frmEconomyEditor, "Are you sure you want to delete the economy?", "Delete Economy", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					he.delete();
					refreshEconomyList();
		        }
			}
		});
		btnEditEconomy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HyperEconomy he = getSelectedEconomy();
				if (he == null) return;
				ObjectPanel ep = new ObjectPanel(hc, he);
				ep.setVisible(true);
			}
		});
		addEconomyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = addEconomyNameField.getText();
				if (newName == null || newName.equals("")) return;
				if (hc.getDataManager().economyExists(newName)) return;
				hc.getDataManager().createNewEconomy(newName, "default", false);
			}
		});
	}
	
	private HyperEconomy getSelectedEconomy() {
		if (hc != null && hc.loaded()) {
			return hc.getDataManager().getEconomy(economySelectList.getSelectedValue());
		}
		return null;
	}
	
	private void refreshEconomyList() {
		int selected = economySelectList.getSelectedIndex();
		economyList.clear();
		ArrayList<String> economies = hc.getDataManager().getEconomyList();
		Collections.sort(economies);
		economyList.addData(economies);
		economySelectList.setSelectedIndex(selected);
	}

	public void popupMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message);
	}

	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof HyperEconomyCreationEvent) {
			refreshEconomyList();
		} else if (event instanceof RequestGUIChangeEvent) {
			RequestGUIChangeEvent hevent = (RequestGUIChangeEvent)event;
			switch (hevent.getType()) {
				case CONNECTED:
					remoteGUIStatusTextField.setText("Loading...");
					panel_2.setBackground(Color.CYAN);
					break;
				case ERROR:
					remoteGUIStatusTextField.setText("Error");
					panel_2.setBackground(Color.RED);
					remoteGUIInfoTextArea.append(hevent.getMessage() + "\n");
					break;
				case INFO:
					break;
				case INVALID_KEY:
					remoteGUIStatusTextField.setText("Invalid Key");
					panel_2.setBackground(Color.PINK);
					remoteGUIInfoTextArea.append(hevent.getMessage() + "\n");
					break;
				case INVALID_RESPONSE:
					remoteGUIStatusTextField.setText("Invalid Response");
					panel_2.setBackground(Color.RED);
					break;
				case LOADED:
					remoteGUIStatusTextField.setText("Loaded");
					panel_2.setBackground(UIManager.getColor("OptionPane.questionDialog.titlePane.background"));
					refreshEconomyList();
					break;
				case SYNCHRONIZED:
					guiSyncStatusLabel.setText("Synchronized");
					panel_3.setBackground(UIManager.getColor("OptionPane.questionDialog.titlePane.background"));
					break;
				case NOT_SYNCHRONIZED:
					guiSyncStatusLabel.setText("Synchronizing...");
					panel_3.setBackground(Color.YELLOW);
					break;
				case SERVER_CHANGE_ECONOMY:
					refreshEconomyList();
					remoteGUIInfoTextArea.append(hevent.getMessage() + "\n");
					break;
				case SERVER_CHANGE_OBJECT:
					remoteGUIInfoTextArea.append(hevent.getMessage() + "\n");
					break;
				default:
					break;
			}
		} else if (event instanceof DataLoadEvent) {
			DataLoadEvent hevent = (DataLoadEvent)event;
			if (hevent.loadType == DataLoadType.COMPLETE) {
				initialize();
				waitMessage.dispose();
				
				economySelectList.setSelectedIndex(economyList.indexOf("default"));
				frmEconomyEditor.setVisible(true);
				refreshEconomyList();
			} else if (hevent.loadType == DataLoadType.LIBRARIES) {
				if (hc.getLibraryManager().dependencyError()) {
					waitMessage.dispose();

					final JOptionPane optionPane = new JOptionPane("There was a problem downloading libraries.  Please check your internet connection or manually install the libraries.  Shutting down...", 
							JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
					final JDialog dialog = new JDialog();
					dialog.setTitle("Error");
					dialog.setModal(true);
					dialog.setContentPane(optionPane);
					dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					dialog.pack();
					//remove error message after 8 seconds
					Timer timer = new Timer(8000, new AbstractAction() {
						private static final long serialVersionUID = 1820275341492338555L;
						@Override
					    public void actionPerformed(ActionEvent ae) {
					        dialog.dispose();
				        	if (hc != null) hc.disable(false);
				            System.exit(0);
					    }
					});
					timer.setRepeats(false);
					timer.start();
					dialog.setVisible(true);
				}
			}
		}
		
	}

	@Override
	public void handleSDLEvent(SDLEvent event) {
		if (event instanceof LogEvent) {
			LogEvent levent = (LogEvent)event;
			if (levent.getException() != null) levent.getException().printStackTrace();
		}
		
	}
}
