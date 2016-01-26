package iva.client.swing.qa.workers;

import iva.client.core.model.Question;
import iva.client.core.services.QuestionService;
import iva.client.core.services.QuestionServiceHandler;
import iva.client.swing.qa.model.QuestionTableModel;

import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

public class AnswerQuestionsWorker extends SwingWorker<Void, Question> {
	private static final Logger log = Logger.getLogger(AnswerQuestionsWorker.class);
	
	private static final QuestionService service = new QuestionServiceHandler();
	private final QuestionTableModel tableModel;
	private final String username;
	
	public AnswerQuestionsWorker(QuestionTableModel tableModel, String username) {
		this.tableModel = tableModel;
		this.username = username;
	}
	
	/* (non-Javadoc)
	 * This method is invoked by AnswerQuestionsWorker#execute()
	 */
	@Override
	protected Void doInBackground() {
		try {
			List<String> questions = tableModel.getQuestionStrings();
			tableModel.getQuestionList().clear();
			
			for(int i=0; i < questions.size(); i++)
			{
				if(isCancelled()) break;
				
				String question = questions.get(i);
				Question query = service.ask(question, username);
				
				setProgress( Math.round((i+1)*100.f/questions.size()) );
				publish(query);
			}
			return null;
		} catch (Exception e) {
			log.fatal("Unhandled worker exception", e);
			throw e;
		}
	}
	
	/* (non-Javadoc)
	 * Invoked on Event Dispatcher thread after several calls to 
	 * AnswerQuestionsWorker#publish(QueryEntity) the argument to each
	 * publish call is appended to the list argument of this method
	 */
	@Override
	protected void process(List<Question> questionBatch) {
		tableModel.addQueries(questionBatch);
	}
	
}