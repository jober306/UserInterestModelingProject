/**
 * 
 */
package iva.client.swing.qa.panels;

import iva.client.core.model.Answer;
import iva.client.core.model.Question;
import iva.client.core.model.User;
import iva.client.core.services.AnswerService;
import iva.client.core.services.AnswerServiceHandler;
import iva.client.swing.ApplicationWindow;
import iva.client.swing.FileInputPanel;
import iva.client.swing.qa.model.AnswerTableModel;
import iva.client.swing.qa.model.QuestionTableModel;
import iva.client.swing.qa.workers.AnswerQuestionsWorker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker.StateValue;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

/**
 * The QAPanel is the primary interface of the program.
 * The active user can submit questions, view answers, and rate the answers.
 * @author Aron
 * @author Kyle
 */
@SuppressWarnings("serial")
public class QAPanel extends JPanel {
	
	private JTextField txtQuestion;
	private JButton btnAddQuestion;
	private JButton btnSelectFile;
	private JButton btnAnswerQuestions;
	private JButton btnClearQuestions;
	private JPanel panelProgress;
	private JProgressBar progressBar;
	private JButton btnCancel;
	private SelectRatingPanel panelSelectRating;
	private JButton btnViewReport;
	
	private JTable tableQuestions;
	private final QuestionTableModel tableModelQuestions = new QuestionTableModel();
	private JTable tableAnswers;
	private final AnswerTableModel tableModelAnswers = new AnswerTableModel();
	private AnswerDetailsHtmlPane htmlAnswerDetails;
	
	private final Action actionAddQuestion = new AddQuestionAction();
	private final Action actionAddFileQuestions = new AddFileQuestionsAction();
	private final Action actionClearQuestions = new ClearQuestionsAction();
	private final Action actionAnswerQuestions = new AnswerQuestionsAction();
	private final Action actionCancelAnswerQuestions = new CancelAnswerQuestionAction();
	private AnswerQuestionsWorker worker;
	private final Action actionSubmitRating = new SubmitRatingAction();
	private final Action actionViewReport = new ViewReportAction();
	
	/* Components to be disabled during the QA pipeline */
	private JComponent[] inputComponents;
	
	// TODO replace with user variable 
	private ApplicationWindow window;
	
	public QAPanel() {
		super();
		initializeGUI();
		
		JComponent[] inputComponents = {
				txtQuestion,
				btnAddQuestion,
				btnSelectFile,
				btnAnswerQuestions,
				btnClearQuestions,
				//btnViewReport // TODO report is not functional
		};
		this.inputComponents = inputComponents;
	}
	
	public QAPanel(ApplicationWindow window) {
		this();
		this.window = window;
	}
	
	private void initializeGUI() {
		setLayout(new BorderLayout());
		
		JPanel panelQuestionInput = new JPanel();
		add(panelQuestionInput, BorderLayout.NORTH);
		
		JLabel lblQuestion = new JLabel("Question");
		panelQuestionInput.add(lblQuestion);
		
		txtQuestion = new JTextField();
		lblQuestion.setLabelFor(txtQuestion);
		txtQuestion.setColumns(30);
		txtQuestion.setAction(actionAddQuestion);
		panelQuestionInput.add(txtQuestion);
		
		btnAddQuestion = new JButton();
		btnAddQuestion.setAction(actionAddQuestion);
		panelQuestionInput.add(btnAddQuestion);
		
		btnSelectFile = new JButton();
		btnSelectFile.setAction(actionAddFileQuestions);
		panelQuestionInput.add(btnSelectFile);
		
		btnViewReport = new JButton();
		btnViewReport.setAction(actionViewReport);
		btnViewReport.setEnabled(false);
		panelQuestionInput.add(btnViewReport);
		
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
		
		btnAnswerQuestions = new JButton();
		panel_4.add(btnAnswerQuestions);
		btnAnswerQuestions.setAction(actionAnswerQuestions);
		
		btnClearQuestions = new JButton();
		btnClearQuestions.setAction(actionClearQuestions);
		panel_4.add(btnClearQuestions);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panelQuestionTable.add(scrollPane_1, BorderLayout.CENTER);
		
		tableQuestions = new JTable(tableModelQuestions) {
			// Gray text of questions that have zero answers
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				
				int modelRow = convertRowIndexToModel(row);
				int numAnswers = (int) getModel().getValueAt(modelRow, 2);
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
		tableQuestions.getColumnModel().getColumn(1).setPreferredWidth(300);
		tableQuestions.getColumnModel().getColumn(2).setPreferredWidth(100);
		
		tableQuestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableQuestions.getSelectionModel().addListSelectionListener(new QuestionSelectionHandler());
		scrollPane_1.setViewportView(tableQuestions);
		
		panelProgress = new JPanel();
		panelProgress.setLayout(new BorderLayout());
		panelProgress.setVisible(false);
		panelQuestionTable.add(panelProgress, BorderLayout.SOUTH);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		panelProgress.add(progressBar, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panelProgress.add(panel_1, BorderLayout.EAST);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setAction(actionCancelAnswerQuestions);
		panel_1.add(btnCancel);
		
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
		
		panelSelectRating = new SelectRatingPanel(actionSubmitRating);
		panelSelectRating.setVisible(false);
		panelAnswerDetails.add(panelSelectRating, BorderLayout.SOUTH);
	}
	
	public void reset() {
		// Reset worker
		if( worker != null && worker.getState() == StateValue.STARTED ) {
			worker.cancel(false);
		}
		// Reset tables
		actionClearQuestions.actionPerformed(null);
	}
	
	private class QuestionSelectionHandler implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				int index = tableQuestions.getSelectedRow();
				
				if( index != -1 ) {
					index = tableQuestions.convertRowIndexToModel(index);
					
					Question query = tableModelQuestions.getQuery(index);
					if( query != null )
					{
						List<Answer> results = query.getAnswers();
						if( results.isEmpty() ) {
							htmlAnswerDetails.displayAnswerDetails(query);
						} else {
							tableModelAnswers.setAnswer(results);
							
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
					
					Answer result = tableModelAnswers.getAnswer(index);
					htmlAnswerDetails.displayAnswerDetails(result);
					
					String ratingStr = tableModelAnswers.getRatingString(index);
					panelSelectRating.setSelectedRadioButton(ratingStr);
					panelSelectRating.setVisible(true);
				} else {
					htmlAnswerDetails.setText("No answer selected");
					
					panelSelectRating.setVisible(false);
					panelSelectRating.setSelectedRadioButton("N/A");
				}
			}
		}
	}

	private class AddQuestionAction extends AbstractAction {
		public AddQuestionAction() {
			putValue(NAME, "Add");
			putValue(SHORT_DESCRIPTION, "Add the user entered question to the question list");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String question = txtQuestion.getText().trim();
			
			if( !question.isEmpty() ) {
				tableModelQuestions.addQuestion(question);
			}
			txtQuestion.setText("");
		}
	}
	
	private class AddFileQuestionsAction extends AbstractAction {
		private final FileInputPanel fileInputPanel = new FileInputPanel();
		
		public AddFileQuestionsAction() {
			putValue(NAME, "Add From File...");
			putValue(SHORT_DESCRIPTION, "Adds questions from a line deliminated text file");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int option = JOptionPane.showConfirmDialog( 
					QAPanel.this, 
					fileInputPanel, 
					"Input Question File", 
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE );
			
			if(option == JOptionPane.OK_OPTION) {
				List<String> questions = fileInputPanel.readSelectedFileByLine();
				tableModelQuestions.addQuestions(questions);
			}
		}
	}

	private class ClearQuestionsAction extends AbstractAction {
		public ClearQuestionsAction() {
			putValue(NAME, "Clear All");
			putValue(SHORT_DESCRIPTION, "Remove all the questions from the question list");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			tableModelAnswers.removeAllRows();
			tableModelQuestions.removeAllRows();
			btnViewReport.setEnabled(false);
		}
	}

	private class AnswerQuestionsAction extends AbstractAction {
		public AnswerQuestionsAction() {
			putValue(NAME, "Answer All");
			putValue(SHORT_DESCRIPTION, 
					"Search for answers to all the questions in the question list");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			User activeUser = (window!=null) ? window.getActiveUser() : null;
			
			// Create and start worker thread
			worker = new AnswerQuestionsWorker(tableModelQuestions, activeUser.getUsername());
			worker.addPropertyChangeListener(event -> {
				switch( event.getPropertyName() ) {
				case "progress":
					progressBar.setIndeterminate(false);
					progressBar.setValue( (Integer) event.getNewValue() );
					break;
				case "state":
					switch( (StateValue) event.getNewValue() ) {
					case PENDING:
					case STARTED:
						tableAnswers.clearSelection();
						tableQuestions.clearSelection();
						
						for(JComponent input1 : inputComponents) {
							input1.setEnabled(false);
						}
						
						progressBar.setValue(0);
						progressBar.setString(null);
						progressBar.setIndeterminate(false);
						
						panelProgress.setVisible(true);
						break;
					case DONE:
						panelProgress.setVisible(false);
						
						progressBar.setIndeterminate(false);
						progressBar.setString(null);
						
						for(JComponent input2 : inputComponents) {
							input2.setEnabled(true);
						}
						
						// Select first answer if no answer is selected
						if( tableModelQuestions.getRowCount() > 0 
								&& tableQuestions.getSelectedRow() == -1) 
						{
							tableQuestions.setRowSelectionInterval(0, 0);
						}
						break;
					}
					break;
				}
			});
			worker.execute();
		}
	}

	private class CancelAnswerQuestionAction extends AbstractAction {
		public CancelAnswerQuestionAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Stop the QA pipeline");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if( worker != null ) {
				progressBar.setIndeterminate(true);
				progressBar.setString("Canceling...");
				worker.cancel(true);
			}
		}
	}

	private class SubmitRatingAction extends AbstractAction {
		private AnswerService feedbackService = new AnswerServiceHandler();
		
		public SubmitRatingAction() {
			putValue(NAME, "Rate");
			putValue(SHORT_DESCRIPTION, "Submit the selected rating");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = tableAnswers.getSelectedRow();
			Answer selectedResult = tableModelAnswers.getAnswers().get(index);
			
			String rating = e.getActionCommand();
			feedbackService.rateAnswer(selectedResult, rating);
			
			tableModelAnswers.fireTableRowsUpdated(index, index);
		}
	}
	
	private class ViewReportAction extends AbstractAction {
		public ViewReportAction() {
			putValue(NAME, "View Report...");
			putValue(SHORT_DESCRIPTION, "Create a report from the set of answered questions");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO method stub
		}
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(() -> {
			try {
				javax.swing.UIManager.setLookAndFeel(
						javax.swing.UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native LAF: " + e);
			}
			
			QAPanel testPanel = new QAPanel();
			
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
