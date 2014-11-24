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
import regalowl.simpledatalib.SimpleDataLib;
import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.events.LogEvent;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class MainPanel {

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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainPanel window = new MainPanel();
					window.frmEconomyEditor.setVisible(true);
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
		
		
		
		frmEconomyEditor = new JFrame();
		frmEconomyEditor.setTitle("Economy Editor");
		frmEconomyEditor.setBounds(100, 100, 398, 211);
		frmEconomyEditor.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frmEconomyEditor.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frmEconomyEditor, "Exit?", "Exit Application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		        	if (hc != null) hc.disable(false);
		            System.exit(0);
		        }
		    }
		});
		frmEconomyEditor.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(207, 12, 175, 159);
		frmEconomyEditor.getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{175, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		lblAddAnEconomy = new JLabel("New Economy Name");
		GridBagConstraints gbc_lblAddAnEconomy = new GridBagConstraints();
		gbc_lblAddAnEconomy.fill = GridBagConstraints.BOTH;
		gbc_lblAddAnEconomy.insets = new Insets(0, 0, 5, 0);
		gbc_lblAddAnEconomy.gridx = 0;
		gbc_lblAddAnEconomy.gridy = 0;
		panel.add(lblAddAnEconomy, gbc_lblAddAnEconomy);
		
		addEconomyNameField = new JTextField();
		GridBagConstraints gbc_addEconomyNameField = new GridBagConstraints();
		gbc_addEconomyNameField.fill = GridBagConstraints.BOTH;
		gbc_addEconomyNameField.insets = new Insets(0, 0, 5, 0);
		gbc_addEconomyNameField.gridx = 0;
		gbc_addEconomyNameField.gridy = 1;
		panel.add(addEconomyNameField, gbc_addEconomyNameField);
		addEconomyNameField.setToolTipText("Input the new economy's name.");
		addEconomyNameField.setColumns(10);
		
		addEconomyButton = new JButton("Add");
		GridBagConstraints gbc_addEconomyButton = new GridBagConstraints();
		gbc_addEconomyButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_addEconomyButton.insets = new Insets(0, 0, 5, 0);
		gbc_addEconomyButton.gridx = 0;
		gbc_addEconomyButton.gridy = 2;
		panel.add(addEconomyButton, gbc_addEconomyButton);
		addEconomyButton.setToolTipText("Adds a new economy with the name specified above.");
		
		btnCloneSelected = new JButton("Clone");
		GridBagConstraints gbc_btnCloneSelected = new GridBagConstraints();
		gbc_btnCloneSelected.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCloneSelected.insets = new Insets(0, 0, 5, 0);
		gbc_btnCloneSelected.gridx = 0;
		gbc_btnCloneSelected.gridy = 3;
		panel.add(btnCloneSelected, gbc_btnCloneSelected);
		btnCloneSelected.addActionListener(new ActionListener() {
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
		
		JButton btnEditEconomy = new JButton("Edit");
		GridBagConstraints gbc_btnEditEconomy = new GridBagConstraints();
		gbc_btnEditEconomy.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEditEconomy.insets = new Insets(0, 0, 5, 0);
		gbc_btnEditEconomy.gridx = 0;
		gbc_btnEditEconomy.gridy = 4;
		panel.add(btnEditEconomy, gbc_btnEditEconomy);
		
		btnDeleteEconomy = new JButton("Delete");
		GridBagConstraints gbc_btnDeleteEconomy = new GridBagConstraints();
		gbc_btnDeleteEconomy.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDeleteEconomy.gridx = 0;
		gbc_btnDeleteEconomy.gridy = 5;
		panel.add(btnDeleteEconomy, gbc_btnDeleteEconomy);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(9, 12, 186, 159);
		frmEconomyEditor.getContentPane().add(scrollPane);
		
		economyList = new QuickListModel<String>();
		economySelectList = new JList<String>(economyList);
		economySelectList.setToolTipText("Select an economy.");
		scrollPane.setViewportView(economySelectList);
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
		btnEditEconomy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HyperEconomy he = getSelectedEconomy();
				if (he == null) return;
				ObjectPanel ep = new ObjectPanel(hc, he);
				ep.setVisible(true);
			}
		});
		addEconomyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newName = addEconomyNameField.getText();
				if (newName == null || newName.equals("")) return;
				if (hc.getDataManager().economyExists(newName)) return;
				hc.getDataManager().createNewEconomy(newName, "default", false);
			}
		});
	}
	
	private HyperEconomy getSelectedEconomy() {
		if (hc != null && hc.enabled()) {
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
	
	@EventHandler
	public void onDataLoad(DataLoadEvent event) {
		if (event.loadType == DataLoadType.COMPLETE) {
			refreshEconomyList();
			economySelectList.setSelectedIndex(economyList.indexOf("default"));
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
