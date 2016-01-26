package heliumnife.server.ephyra.model.transfer;

import java.io.Serializable;

public class ResultDTO implements Serializable{

	private static final long serialVersionUID = 8337070113242958661L;
	
	public String answer;
	
	public  AnalyzedQuestionDTO analyzedQuestion;
	
	public  double score;
	
	public  TermDTO[] terms;
	
	public  String docID;
	
	public  String queryString;
}
