package regalowl.hyperconomy.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.JList;
import javax.swing.JScrollPane;

import regalowl.hyperconomy.tradeobject.TradeObject;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

public class CategoryEditor extends JFrame {


	private static final long serialVersionUID = -7483650005071403119L;
	private ObjectPanel economyPanel;
	private TradeObject tradeObject;
	private JPanel contentPane;
	private JList<String> selectedCategories;
	QuickListModel<String> selectedModel = new QuickListModel<String>();
	JList<String> unselectedCategories;
	QuickListModel<String> unselectedModel = new QuickListModel<String>();
	private JTextField newCategoryField;

	/**
	 * Create the frame.
	 */
	public CategoryEditor(TradeObject to, ObjectPanel ePanel) {
		setTitle("Economy Editor");
		this.tradeObject = to;
		this.economyPanel = ePanel;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 569, 620);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(24, 30, 244, 453);
		contentPane.add(scrollPane_1);
		
		unselectedCategories = new JList<String>(unselectedModel);
		scrollPane_1.setViewportView(unselectedCategories);
		UnselectedMouseAdapter unselectedMouseAdapter = new UnselectedMouseAdapter();
		unselectedCategories.addMouseListener(unselectedMouseAdapter);
		unselectedCategories.addMouseMotionListener(unselectedMouseAdapter);
		unselectedCategories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(296, 30, 244, 453);
		contentPane.add(scrollPane);
		
		selectedCategories = new JList<String>(selectedModel);
		scrollPane.setViewportView(selectedCategories);
		
		JLabel availableCatLabel = new JLabel("Available Categories");
		availableCatLabel.setBounds(24, 12, 244, 15);
		contentPane.add(availableCatLabel);
		
		JLabel currentCatLabel = new JLabel("Object Categories");
		currentCatLabel.setBounds(296, 12, 244, 15);
		contentPane.add(currentCatLabel);
		
		JButton addNewCategoryButton = new JButton("Add New Category");
		addNewCategoryButton.setBounds(164, 489, 244, 25);
		contentPane.add(addNewCategoryButton);
		addNewCategoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String text = newCategoryField.getText();
				if (text == null || text == "") return;
				if (unselectedModel.contains(text)) return;
				if (selectedModel.contains(text)) return;
				selectedModel.addData(text);
				tradeObject.addCategory(text);
				economyPanel.loadCatgories();
			}
		});
		
		newCategoryField = new JTextField();
		newCategoryField.setToolTipText("Type the new category here and press Add New Category.");
		newCategoryField.setBounds(164, 519, 244, 25);
		contentPane.add(newCategoryField);
		newCategoryField.setColumns(10);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnClose.setBounds(226, 558, 117, 25);
		contentPane.add(btnClose);
		SelectedMouseAdapter selectedMouseAdapter = new SelectedMouseAdapter();
		selectedCategories.addMouseListener(selectedMouseAdapter);
		selectedCategories.addMouseMotionListener(selectedMouseAdapter);
		selectedCategories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		refreshLists();
	}
	
	private void refreshLists() {
		unselectedModel.clear();
		selectedModel.clear();
		ArrayList<String> otherCategories = tradeObject.getOtherCategories();
		ArrayList<String> categories = tradeObject.getCategories();
		Collections.sort(otherCategories);
		Collections.sort(categories);
		unselectedModel.addData(otherCategories);
		selectedModel.addData(categories);
		economyPanel.loadCatgories();
	}
	
	
    private class UnselectedMouseAdapter extends MouseInputAdapter { 
		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				String selected = unselectedCategories.getSelectedValue();
				tradeObject.addCategory(selected);
				refreshLists();
			}
		}
	}
    private class SelectedMouseAdapter extends MouseInputAdapter { 
		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				String selected = selectedCategories.getSelectedValue();
				tradeObject.removeCategory(selected);
				refreshLists();
			}
		}
	}
}
