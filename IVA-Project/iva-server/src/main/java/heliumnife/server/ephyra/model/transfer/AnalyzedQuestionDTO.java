package heliumnife.server.ephyra.model.transfer;

import java.io.Serializable;

public class AnalyzedQuestionDTO implements Serializable{
	
	private static final long serialVersionUID = -4280332967281655947L;

	public String question;
	
	public TermDTO[] terms;
}
