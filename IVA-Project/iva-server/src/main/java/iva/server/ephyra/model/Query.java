package iva.server.ephyra.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <p>A <code>Query</code> is a data structure representing a search engine
 * query.</p>
 * 
 * <p>The required fields are a query string, the analyzed question, a score
 * that is the higher the more specific the query and the extraction techniques
 * applied to results retrieved with this query.</p>
 * 
 * <p>This class implements the interface <code>Serializable</code>.</p>
 * 
 * @author Nico Schlaefer
 * @version 2007-05-01
 */
public class Query implements Serializable {
	private static final long serialVersionUID = 2051134801069513901L;

	/** The query string. */
	private String queryString;
	
	/** the original query String before normalization*/
	private String originalQueryString;
	
	/** The analyzed question. */
	private AnalyzedQuestion analyzedQuestion;
	/**
	 * The score of the query. More specific queries receive a higher score than
	 * simple keyword queries. The score is used by the answer selection module
	 * to score the results retrieved with this query.
	 */
	private float score;
	/**
	 * The answer extraction techniques applied to results retrieved with this
	 * query.
	 */
	private String[] extractionTechniques;
	
	public Query() {
	}
	
	/**
	 * Returns the query string.
	 * 
	 * @return query string
	 */
	public String getQueryString() {
		return queryString;
	}
	
	/**
	 * Sets the query string.
	 * 
	 * @param queryString query string
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @return the original query String, before normalization
	 */
	public String getOriginalQueryString() {
		return originalQueryString;
	}
	
	/**
	 * @param originalQueryString the original query String
	 */
	public void setOriginalQueryString(String originalQueryString) {
		this.originalQueryString = originalQueryString;
	}
	
	/**
	 * Returns the analyzed question
	 * 
	 * @return analyzed question
	 */
	public AnalyzedQuestion getAnalyzedQuestion() {
		return analyzedQuestion;
	}
	
	/**
	 * Sets the analyzed question.
	 * 
	 * @param analyzedQuestion analyzed question
	 */
	public void setAnalyzedQuestion(AnalyzedQuestion analyzedQuestion) {
		this.analyzedQuestion = analyzedQuestion;
	}
	
	/**
	 * Returns the score of the query.
	 * 
	 * @return score of the query
	 */
	public float getScore() {
		return score;
	}

	/**
	 * Sets the score of the query.
	 * 
	 * @param score score of the query
	 */
	public void setScore(float score) {
		this.score = score;
	}
	
	public String[] getExtractionTechniques() {
		return extractionTechniques;
	}

	/**
	 * Sets the answer extraction techniques that are applied to results
	 * retrieved with this query.
	 * 
	 * @param techniques answer extraction techniques
	 */
	public void setExtractionTechniques(String[] techniques) {
		extractionTechniques = techniques;
	}

	@Override
	public String toString() {
		return String.format(
				"Query [queryString=%s, originalQueryString=%s, score=%s, extractionTechniques=%s]",
				queryString, originalQueryString, score, Arrays.toString(extractionTechniques));
	}
	
}
