package iva.client.swing.user.model;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class CategoryTableModel extends AbstractTableModel {
	
	private final String[] columnNames = {"Category", "Score"};
	private Map<String, Double> categoryMap = new HashMap<>(0);
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return categoryMap.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;
		
		Object category = categoryMap.keySet().toArray()[rowIndex];
		
		switch(columnIndex) {
		case 0:
			value = category;
			break;
		case 1:
			value = categoryMap.get(category);
			break;
		default:
			throw new RuntimeException("Invalid table column accessed: "+columnIndex);
		}
		return value;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Class<?> columnClass;
		if( getRowCount() > 0 ) {
			columnClass = getValueAt(0, columnIndex).getClass();
		} else {
			columnClass = Object.class;
		}
		return columnClass;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	public Map<String, Double> getCategories() {
		return categoryMap;
	}

	public void setCategories(Map<String, Double> categories) {
		this.categoryMap = categories;
		this.fireTableDataChanged();
	}
	
	public void removeAllRows() {
		int lastRow = getRowCount() - 1;
		if( lastRow >= 0 ) {
			categoryMap = new HashMap<>(0);
			this.fireTableRowsDeleted(0, lastRow);
		}
	}
}