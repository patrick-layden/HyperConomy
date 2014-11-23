package regalowl.hyperconomy.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import regalowl.hyperconomy.tradeobject.TradeObject;

public class DataEditor extends JFrame {


	private static final long serialVersionUID = 595777527534644481L;
	private JFrame dataEditor;
	private JPanel contentPane;
	private TradeObject tradeObject;
	JTextArea dataTextArea;

	/**
	 * Create the frame.
	 */
	public DataEditor(TradeObject to) {
		dataEditor = this;
		this.tradeObject = to;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 538);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 12, 474, 451);
		contentPane.add(scrollPane);
		
		dataTextArea = new JTextArea();
		dataTextArea.setLineWrap(true);
		dataTextArea.setWrapStyleWord(true);
		if (tradeObject != null) dataTextArea.setText(tradeObject.getData());
		scrollPane.setViewportView(dataTextArea);
		dataTextArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {handleChange();}
			public void removeUpdate(DocumentEvent e) {handleChange();}
			public void insertUpdate(DocumentEvent e) {handleChange();}
			public void handleChange() {
				tradeObject.setData(dataTextArea.getText());
			}
		});
		
		JButton exitButton = new JButton("Close");
		exitButton.setBounds(185, 475, 117, 25);
		contentPane.add(exitButton);
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dataEditor.dispose();
			}
		});
	}
}
