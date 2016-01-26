package iva.client.swing.user.model;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class InterestCategoryTableModel extends AbstractTableModel {
	
	private final String[] columnNames = {"Input", "Interest Mapping"};
	private Map<String, String> interestCategoryMap = new HashMap<String, String>(0);
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	@Override
	public int getRowCount() {
		return interestCategoryMap.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;
		
		Object interest = interestCategoryMap.keySet().toArray()[rowIndex];
		
		switch(columnIndex) {
		case 0:
			value = interest;
			break;
		case 1:
			value = interestCategoryMap.get(interest);
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
	
	public Map<String, String> getInterestCategoryMap() {
		return interestCategoryMap;
	}

	public void setInterestCategoryMap(Map<String, String> interestCategoryMap) {
		this.interestCategoryMap = interestCategoryMap;
		this.fireTableDataChanged();
	}
}