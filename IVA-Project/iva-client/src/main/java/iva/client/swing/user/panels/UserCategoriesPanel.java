package iva.client.swing.user.panels;

import iva.client.core.model.InterestModel;
import iva.client.swing.user.model.CategoryTableModel;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class UserCategoriesPanel extends JPanel {
	private JTable tableCategories;
	private final CategoryTableModel tableModelCategories = new CategoryTableModel();

	public UserCategoriesPanel() {
		super();
		initializeGUI();
	}

	private void initializeGUI() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		tableCategories = new JTable(tableModelCategories);
		tableCategories.setAutoCreateRowSorter(true);
		tableCategories.setFillsViewportHeight(true);
		scrollPane.setViewportView(tableCategories);
	}
	
	public void clearTable() {
		tableModelCategories.removeAllRows();
	}
	
	public Map<String, Double> getCategories() {
		return tableModelCategories.getCategories();
	}

	public void setInterestModel(InterestModel model) {
		tableModelCategories.setCategories(model.getShortTermCategories());
	}
	
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(() -> {
			try {
				javax.swing.UIManager.setLookAndFeel(
						javax.swing.UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native LAF: " + e);
			}
			
			UserCategoriesPanel testPanel = new UserCategoriesPanel();
			
			javax.swing.JFrame frame = new javax.swing.JFrame(
					testPanel.getClass().getSimpleName());
			
			frame.getContentPane().add(testPanel);
			frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}

}
