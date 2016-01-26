package iva.client.swing.qa.model;

import iva.client.core.model.Answer;
import iva.client.core.services.AnswerServiceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class AnswerTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -1833914723836980512L;
	
	private final String[] columnNames = {"#", "Answer", "Helpful?"};
	private List<Answer> results = new ArrayList<>(0);
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return results.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;

		switch(columnIndex) {
		case 0:
			value = rowIndex + 1;
			break;
		case 1:
			value = results.get(rowIndex).getAnswer();
			break;
		case 2:
			Map<String, Integer> ratingMap = AnswerServiceHandler.getRatingMap();
			int rating = results.get(rowIndex).getRating();
			
			if( rating == ratingMap.get("Yes") ) {
				value = "Yes";
			} else if( rating == ratingMap.get("No") ) {
				value = "No";
			} else {
				value = "N/A";
			}
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

	public void removeAllRows() {
		int lastRow = getRowCount() - 1;
		if( lastRow >= 0 ) {
			results = new ArrayList<>(0);
			fireTableRowsDeleted(0, lastRow);
		}
	}
	
	public String getRatingString(int rowIndex) {
		return (String) getValueAt(rowIndex, 2);
	}
	
	public List<Answer> getAnswers() {
		return results;
	}
	
	public Answer getAnswer(int rowIndex) {
		return results.get(rowIndex);
	}
	
	public void setAnswer(List<Answer> results) {
		this.results = results;
		fireTableDataChanged();
	}
}