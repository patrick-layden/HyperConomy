package regalowl.hyperconomy.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import java.awt.GridBagLayout;

import javax.swing.JComboBox;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.HyperEconomyCreationEvent;
import regalowl.simpledatalib.SimpleDataLib;
import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.events.LogEvent;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;

public class MainPanel {

	private JFrame frame;
	private HyperConomy hc;
	private SimpleDataLib sdl;
	private JComboBox<String> economyComboBox;
	private JTextField addEconomyNameField;
	private JLabel lblAddAnEconomy;
	private JButton addEconomyButton;
	private JButton btnCloneSelected;
	private JButton btnDeleteEconomy;

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
		frame.setBounds(100, 100, 392, 300);
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
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{191, 191, 0};
		gridBagLayout.rowHeights = new int[]{15, 24, 25, 25, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel economyLabel = new JLabel("Economy");
		GridBagConstraints gbc_economyLabel = new GridBagConstraints();
		gbc_economyLabel.fill = GridBagConstraints.VERTICAL;
		gbc_economyLabel.insets = new Insets(0, 0, 5, 5);
		gbc_economyLabel.gridx = 0;
		gbc_economyLabel.gridy = 0;
		frame.getContentPane().add(economyLabel, gbc_economyLabel);
		
		lblAddAnEconomy = new JLabel("New Economy Name");
		GridBagConstraints gbc_lblAddAnEconomy = new GridBagConstraints();
		gbc_lblAddAnEconomy.fill = GridBagConstraints.VERTICAL;
		gbc_lblAddAnEconomy.insets = new Insets(0, 0, 5, 0);
		gbc_lblAddAnEconomy.gridx = 1;
		gbc_lblAddAnEconomy.gridy = 0;
		frame.getContentPane().add(lblAddAnEconomy, gbc_lblAddAnEconomy);
		
		JButton btnEditEconomy = new JButton("Edit");
		btnEditEconomy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HyperEconomy he = getSelectedEconomy();
				if (he == null) return;
				EconomyPanel ep = new EconomyPanel(hc, he);
				ep.setVisible(true);
			}
		});
		
		economyComboBox = new JComboBox<String>();
		economyComboBox.setToolTipText("Select an economy.");
		GridBagConstraints gbc_economyComboBox = new GridBagConstraints();
		gbc_economyComboBox.fill = GridBagConstraints.BOTH;
		gbc_economyComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_economyComboBox.gridx = 0;
		gbc_economyComboBox.gridy = 1;
		frame.getContentPane().add(economyComboBox, gbc_economyComboBox);
		
		addEconomyNameField = new JTextField();
		addEconomyNameField.setToolTipText("Input the new economy's name.");
		GridBagConstraints gbc_addEconomyNameField = new GridBagConstraints();
		gbc_addEconomyNameField.fill = GridBagConstraints.BOTH;
		gbc_addEconomyNameField.insets = new Insets(0, 0, 5, 0);
		gbc_addEconomyNameField.gridx = 1;
		gbc_addEconomyNameField.gridy = 1;
		frame.getContentPane().add(addEconomyNameField, gbc_addEconomyNameField);
		addEconomyNameField.setColumns(10);
		GridBagConstraints gbc_btnEditEconomy = new GridBagConstraints();
		gbc_btnEditEconomy.fill = GridBagConstraints.BOTH;
		gbc_btnEditEconomy.insets = new Insets(0, 0, 5, 5);
		gbc_btnEditEconomy.gridx = 0;
		gbc_btnEditEconomy.gridy = 2;
		frame.getContentPane().add(btnEditEconomy, gbc_btnEditEconomy);
		
		btnCloneSelected = new JButton("Clone");
		btnCloneSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newName = addEconomyNameField.getText();
				if (newName == null || newName == "") return;
				if (hc.getDataManager().economyExists(newName)) return;
				HyperEconomy he = getSelectedEconomy();
				if (he == null) return;
				hc.getDataManager().createNewEconomy(newName, he.getName(), true);
			}
		});
		
		addEconomyButton = new JButton("Add");
		addEconomyButton.setToolTipText("Adds a new economy with the name specified above.");
		addEconomyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newName = addEconomyNameField.getText();
				if (newName == null || newName == "") return;
				if (hc.getDataManager().economyExists(newName)) return;
				hc.getDataManager().createNewEconomy(newName, "default", false);
			}
		});
		GridBagConstraints gbc_addEconomyButton = new GridBagConstraints();
		gbc_addEconomyButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_addEconomyButton.insets = new Insets(0, 0, 5, 0);
		gbc_addEconomyButton.gridx = 1;
		gbc_addEconomyButton.gridy = 2;
		frame.getContentPane().add(addEconomyButton, gbc_addEconomyButton);
		
		btnDeleteEconomy = new JButton("Delete");
		btnDeleteEconomy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HyperEconomy he = getSelectedEconomy();
				if (he == null) return;
				if (he.getName().equals("default")) {
					JOptionPane.showMessageDialog(null, "You can't delete the default economy.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				he.delete();
				refreshEconomyList();
			}
		});
		GridBagConstraints gbc_btnDeleteEconomy = new GridBagConstraints();
		gbc_btnDeleteEconomy.fill = GridBagConstraints.BOTH;
		gbc_btnDeleteEconomy.insets = new Insets(0, 0, 0, 5);
		gbc_btnDeleteEconomy.gridx = 0;
		gbc_btnDeleteEconomy.gridy = 3;
		frame.getContentPane().add(btnDeleteEconomy, gbc_btnDeleteEconomy);
		btnCloneSelected.setToolTipText("Clones the economy selected on the left to the name specified above.");
		GridBagConstraints gbc_btnCloneSelected = new GridBagConstraints();
		gbc_btnCloneSelected.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCloneSelected.gridx = 1;
		gbc_btnCloneSelected.gridy = 3;
		frame.getContentPane().add(btnCloneSelected, gbc_btnCloneSelected);
	}
	
	private HyperEconomy getSelectedEconomy() {
		if (hc != null && hc.enabled()) {
			return hc.getDataManager().getEconomy(economyComboBox.getSelectedItem().toString());
		}
		return null;
	}
	
	private void refreshEconomyList() {
		economyComboBox.removeAllItems();
		for (HyperEconomy he:hc.getDataManager().getEconomies()) {
			economyComboBox.addItem(he.getName());
		}
	}
	
	@EventHandler
	public void onDataLoad(DataLoadEvent event) {
		if (event.loadType == DataLoadType.COMPLETE) {
			refreshEconomyList();
		}
	}
	
	@EventHandler
	public void onEconomyCreation(HyperEconomyCreationEvent event) {
		refreshEconomyList();
	}
	
	
	@EventHandler
	public void onLogMessage(LogEvent event) {
		if (event.getException() != null) event.getException().printStackTrace();
	}

}
