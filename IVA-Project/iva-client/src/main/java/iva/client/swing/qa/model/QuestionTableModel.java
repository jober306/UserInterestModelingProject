package iva.client.swing.qa.model;

import iva.client.core.model.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class QuestionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1036883342370513475L;

	private final String[] columnNames = {"#", "Question", "Answers Found"};
	
	private final List<String> questionStrings = new ArrayList<>();
	private final List<Question> questionList = new ArrayList<>();
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return questionStrings.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;

		switch(columnIndex) {
		case 0:
			value = rowIndex + 1;
			break;
		case 1:
			value = questionStrings.get(rowIndex);
			break;
		case 2:
			value = 0;
			if( rowIndex < questionList.size() ) {
				Question query = questionList.get(rowIndex);
				if( query != null ) {
					value = query.getAnswerLinks().size();
				}
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
	
	public boolean addQuestion(String question) {
		boolean result = questionStrings.add(question);
		
		int rowIndex = questionStrings.size() - 1;
		fireTableRowsInserted(rowIndex, rowIndex);
		
		return result;
	}
	
	public boolean addQuestions(Collection<? extends String> questions) {
		int firstRow = questionStrings.size();
		
		boolean result = questionStrings.addAll(questions);
		
		int lastRow = questionStrings.size() - 1;
		fireTableRowsInserted(firstRow, lastRow);
		
		return result;
	}
	
	public boolean addQueries(Collection<? extends Question> questions) {
		int firstRow = questionList.size();
		
		boolean result = questionList.addAll(questions);
		
		int lastRow = questionList.size() - 1;
		fireTableRowsInserted(firstRow, lastRow);
		
		return result;
	}
	
	public void removeAllRows() {
		int lastRow = getRowCount() - 1;
		if( lastRow >= 0 ) {
			questionStrings.clear();
			questionList.clear();
			fireTableRowsDeleted(0, lastRow);
		}
	}

	public List<String> getQuestionStrings() {
		return questionStrings;
	}
	
	public List<Question> getQuestionList() {
		return questionList;
	}
	
	public Question getQuery(int rowIndex) {
		Question question = null;
		if( rowIndex < getQuestionList().size() ) {
			question = getQuestionList().get(rowIndex);
		}
		return question;
	}
	
}