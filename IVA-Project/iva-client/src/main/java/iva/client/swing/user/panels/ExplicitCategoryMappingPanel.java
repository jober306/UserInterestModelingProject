package iva.client.swing.user.panels;

import iva.client.swing.user.model.InterestCategoryTableModel;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class ExplicitCategoryMappingPanel extends JPanel {
	private JTable interestMappingtable;
	private final InterestCategoryTableModel tableModelInterestMapping = new InterestCategoryTableModel();
	
	public ExplicitCategoryMappingPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		interestMappingtable = new JTable(tableModelInterestMapping);
		interestMappingtable.setAutoCreateRowSorter(true);
		interestMappingtable.setFillsViewportHeight(true);
		scrollPane.setViewportView(interestMappingtable);
	}

	public Map<String, String> getInterestCategoryMap() {
		return tableModelInterestMapping.getInterestCategoryMap();
	}

	public void setCategoryMappings(Map<String, String> interestCategoryMap) {
		tableModelInterestMapping.setInterestCategoryMap(interestCategoryMap);
	}

}
