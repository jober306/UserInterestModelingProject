package iva.client.swing.qa.panels;

import iva.client.core.model.Answer;
import iva.client.core.model.Question;
import iva.client.core.model.User;
import iva.client.core.services.AnswerServiceHandler;
import iva.client.core.services.QuestionService;
import iva.client.core.services.QuestionServiceHandler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Shows past queries of the user, and how they rated them.
 * @author Kyle
 * @author Aron
 */
@SuppressWarnings("serial")
public class QuestionHistoryPanel extends JPanel {
	
	private final QuestionService service = new QuestionServiceHandler();
	
	private JTable tableQuestions;
	private final QuestionTableModel tableModelQuestions = new QuestionTableModel();
	private JTable tableAnswers;
	private final AnswerTableModel tableModelAnswers = new AnswerTableModel();
	private AnswerDetailsHtmlPane htmlAnswerDetails;
	
	public QuestionHistoryPanel() {
		super();
		initializeGUI();
	}
	
	private void initializeGUI() {
		setLayout(new BorderLayout());
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(400);
		splitPane.setResizeWeight(0.5);
		add(splitPane, BorderLayout.CENTER);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setResizeWeight(0.5);
		splitPane.setLeftComponent(splitPane_1);
		
		JPanel panelQuestionTable = new JPanel();
		panelQuestionTable.setLayout(new BorderLayout());
		splitPane_1.setTopComponent(panelQuestionTable);
		
		JPanel panel_4 = new JPanel();
		panelQuestionTable.add(panel_4, BorderLayout.NORTH);
		
		JLabel lblQuestions = new JLabel("Questions");
		panel_4.add(lblQuestions);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panelQuestionTable.add(scrollPane_1, BorderLayout.CENTER);
		
		tableQuestions = new JTable(tableModelQuestions) {
			// Gray text of questions that have zero answers
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				
				int modelRow = convertRowIndexToModel(row);
				int numAnswers = (int) getModel().getValueAt(modelRow, 3);
				if (numAnswers == 0) {
					c.setForeground(Color.GRAY);
				} else {
					c.setForeground(Color.BLACK);
				}
				return c;
			}
		};
		tableQuestions.setAutoCreateRowSorter(true);
		tableQuestions.setFillsViewportHeight(true);
		
		// TODO replace hard coded column widths
		tableQuestions.getColumnModel().getColumn(0).setPreferredWidth(36);
		tableQuestions.getColumnModel().getColumn(1).setPreferredWidth(100);
		tableQuestions.getColumnModel().getColumn(2).setPreferredWidth(300);
		tableQuestions.getColumnModel().getColumn(3).setPreferredWidth(80);
		
		tableQuestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableQuestions.getSelectionModel().addListSelectionListener(new QuestionSelectionHandler());
		scrollPane_1.setViewportView(tableQuestions);
		
		JPanel panelAnswerTable = new JPanel();
		panelAnswerTable.setLayout(new BorderLayout());
		splitPane_1.setBottomComponent(panelAnswerTable);
		
		JPanel panel_5 = new JPanel();
		panelAnswerTable.add(panel_5, BorderLayout.NORTH);
		
		JLabel lblAnswers = new JLabel("Answers");
		panel_5.add(lblAnswers);
		
		JScrollPane scrollPane = new JScrollPane();
		panelAnswerTable.add(scrollPane, BorderLayout.CENTER);
		
		tableAnswers = new JTable(tableModelAnswers);
		tableAnswers.setAutoCreateRowSorter(true);
		tableAnswers.setFillsViewportHeight(true);
		
		// TODO Replace hard coded column widths
		tableAnswers.getColumnModel().getColumn(0).setPreferredWidth(36);
		tableAnswers.getColumnModel().getColumn(1).setPreferredWidth(350);
		tableAnswers.getColumnModel().getColumn(2).setPreferredWidth(60);
		
		tableAnswers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableAnswers.getSelectionModel().addListSelectionListener(new AnswerSelectionHandler());
		scrollPane.setViewportView(tableAnswers);
		
		JPanel panelAnswerDetails = new JPanel();
		panelAnswerDetails.setLayout(new BorderLayout());
		splitPane.setRightComponent(panelAnswerDetails);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panelAnswerDetails.add(scrollPane_2);
		
		JPanel panel_8 = new JPanel();
		scrollPane_2.setColumnHeaderView(panel_8);
		
		JLabel lblAnswerDetails = new JLabel("Answer Details");
		panel_8.add(lblAnswerDetails);
		
		htmlAnswerDetails = new AnswerDetailsHtmlPane();
		htmlAnswerDetails.setText("No answer selected");
		scrollPane_2.setViewportView(htmlAnswerDetails);
	}
	
	public void loadUser(User user) {
		List<Question> questionHistory = service.findByOwner(user);
		tableModelQuestions.setQuestions(questionHistory);
	}
	
	public void reset() {
		tableModelQuestions.removeAllRows();
		tableModelAnswers.removeAllRows();
	}

	private static class QuestionTableModel extends AbstractTableModel {
		
		private String[] columnNames = {"#", "Date", "Question", "Answers"};
		private List<Question> questions = new ArrayList<>(0);
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return questions.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object value = null;
			Question question = questions.get(rowIndex);

			switch(columnIndex) {
			case 0:
				value = rowIndex + 1;
				break;
			case 1:
				value = question.getCreated();
				break;
			case 2:
				value = question.getQuestion();
				break;
			case 3:
				value = question.getAnswers().size();
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
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		public Question getQuestion(int rowIndex) {
			Question question = null;
			if( rowIndex < getRowCount() ) {
				question = questions.get(rowIndex);
			}
			return question;
		}
		
		public void removeAllRows() {
			int lastRow = getRowCount() - 1;
			if( lastRow >= 0 ) {
				questions = new ArrayList<>(0);
				fireTableRowsDeleted(0, lastRow);
			}
		}
		
		public void setQuestions(List<Question> questions) {
			this.questions = questions;
			fireTableDataChanged();
		}
	}

	private static class AnswerTableModel extends AbstractTableModel {

		private String[] columnNames = {"#", "Answer", "Helpful?"};
		private List<Answer> answers = new ArrayList<>(0);

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return answers.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object value = null;
			Answer result = answers.get(rowIndex);

			switch(columnIndex) {
			case 0:
				value = rowIndex + 1;
				break;
			case 1:
				value = result.getAnswer();
				break;
			case 2:
				Map<String, Integer> ratingMap = AnswerServiceHandler.getRatingMap();
				int rating = answers.get(rowIndex).getRating();
				
				if( rating == ratingMap.get("Yes") ) {
					value = "Yes";
				} else if( rating == ratingMap.get("No") ) {
					value = "No";
				} else {
					value = "N/A";
				}
				break;
			default:
				throw new RuntimeException("Invalid table column accessed");
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
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		public void removeAllRows() {
			int lastRow = getRowCount() - 1;
			if( lastRow >= 0 ) {
				answers = new ArrayList<>(0);
				fireTableRowsDeleted(0, lastRow);
			}
		}
		
		public Answer getResult(int rowIndex) {
			return answers.get(rowIndex);
		}
		
		public void setAnswers(List<Answer> answers) {
			this.answers = answers;
			fireTableDataChanged();
		}
	}

	private class QuestionSelectionHandler implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				int index = tableQuestions.getSelectedRow();
				
				if( index != -1 ) {
					index = tableQuestions.convertRowIndexToModel(index);
					
					Question query = tableModelQuestions.getQuestion(index);
					if( query != null ) 
					{
						List<Answer> results = query.getAnswers();
						if( results.isEmpty() ) {
							htmlAnswerDetails.displayAnswerDetails(query);
						} else {
							tableModelAnswers.setAnswers(results);
							
							// Select first answer
							if( tableModelAnswers.getRowCount() > 0 
									&& tableAnswers.getSelectedRow() == -1)
							{
								tableAnswers.setRowSelectionInterval(0, 0);
							}
						}
					} else {
						htmlAnswerDetails.setText("Question has not been answered");
						tableModelAnswers.removeAllRows();
					}
				} else {
					htmlAnswerDetails.setText("No answer selected");
					tableModelAnswers.removeAllRows();
				}
			}
		}
	}

	private class AnswerSelectionHandler implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				int index = tableAnswers.getSelectedRow();
				
				if( index != -1 ) {
					index = tableAnswers.convertRowIndexToModel(index);
					
					Answer result = tableModelAnswers.getResult(index);
					htmlAnswerDetails.displayAnswerDetails(result);
				} else {
					htmlAnswerDetails.setText("No answer selected");
				}
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * Display the panel in a simple JFrame.
	 */
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(() -> {
			try {
				javax.swing.UIManager.setLookAndFeel(
						javax.swing.UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native LAF: " + e);
			}
			
			QuestionHistoryPanel testPanel = new QuestionHistoryPanel();
			
			javax.swing.JFrame frame = new javax.swing.JFrame(
					testPanel.getClass().getSimpleName());
			
			frame.getContentPane().add(testPanel);
			frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			frame.setPreferredSize(new java.awt.Dimension(800, 600));
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}   

}
