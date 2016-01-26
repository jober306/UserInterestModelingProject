package iva.server.ephyra.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A <code>Result</code> is a data structure representing a result returned
 * by the QA engine.</p>
 * 
 * <p>It comprises the following elements:
 * <ul>
 * <li>answer string</li>
 * <li>score which is a confidence measure for the answer</li>
 * <li>query used to obtain the answer (optional)</li>
 * <li>ID (e.g. a URL) of a document containing the answer (optional)</li>
 * <li>hit position at which the answer was returned by a search engine
 *     (optional)</li>
 * <li>flag indicating whether the answer was judged correct (optional)</li>
 * <li>other elements depending on the granularity of the answer string
 *     (e.g. sentence, factoid)</li>
 * </ul></p>
 * 
 * <p>This class implements the interfaces <code>Comparable</code> and
 * <code>Serializable</code>. Note: it has a natural ordering that is
 * inconsistent with <code>equals()</code>.</p>
 * 
 * @author Nico Schlaefer
 * @version 2008-01-29
 */
public class Result implements Comparable<Result>, Serializable {
	private static final long serialVersionUID = -4287433393272198852L;

	/** The answer string. */
	private String answer;
	
	/** A confidence measure for the answer, initially 0. */
	private double score = 0;
	
	/** A normalized confidence measure for the answer (optional). */
	private double normScore = 0;
	
	/** The <code>Query</code> that was used to obtain the answer (optional). */
	private Query query;
	
	/** The ID (e.g. a URL) of a document containing the answer (optional). */
	private String docID;
	
	/** The ID of the document in the search engine cache (optional). */
	private String cacheID;
	
	/** The hit position of the answer, starting from 0 (optional). */
	private int hitPos = -1;
	
	/** A flag indicating whether the answer was judged correct (optional). */
	private boolean correct;
	
	/** Hashmap holding intermediate scores so they don't influence sorting*/
	private HashMap<String, Double> extraScores = new HashMap<>();
	
	/** If there's 2 Knowledge Annotator who return an answer, the one with the lowest number will be the prioritise one*/
	private double priority = Double.POSITIVE_INFINITY;
	
	/**
	 * If this is a sentence-level answer, named entities extracted from the
	 * sentence and their types (optional).
	 */
	private Map<String, String[]> nes;
	
	/**
	 * If this is a sentence-level answer, terms extracted from the sentence
	 * (optional).
	 */
	private Term[] terms;
	
	/**
	 * If this is a factoid answer, a sentence in the supporting document the
	 * answer was extracted from (optional).
	 */
	private String sentence;
	
	/** If this is a factoid answer, the named entity types (optional). */
	private String[] neTypes;
	
	/**
	 * If this is a factoid answer, the techniques used to extract it
	 * (optional).
	 */
	private String[] extractionTechniques;
	
	/**
	 * If this is an answer to an 'other' question, list to keep the IDs of
	 * covered nugget (optional).
	 */
	private ArrayList<String> coveredNuggets = new ArrayList<>();
	
	public Result() {
	}
	
	public Result(String answer) {
		this.answer = answer;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getNormScore() {
		return normScore;
	}

	public void setNormScore(double normScore) {
		this.normScore = normScore;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public String getDocID() {
		return docID;
	}

	public void setDocID(String docID) {
		this.docID = docID;
	}

	public String getCacheID() {
		return cacheID;
	}

	public void setCacheID(String cacheID) {
		this.cacheID = cacheID;
	}

	public int getHitPos() {
		return hitPos;
	}

	public void setHitPos(int hitPos) {
		this.hitPos = hitPos;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public HashMap<String, Double> getExtraScores() {
		return extraScores;
	}

	public void setExtraScores(HashMap<String, Double> extraScores) {
		this.extraScores = extraScores;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public Map<String, String[]> getNes() {
		return nes;
	}

	public void setNes(Map<String, String[]> nes) {
		this.nes = nes;
	}

	public Term[] getTerms() {
		return terms;
	}

	public void setTerms(Term[] terms) {
		this.terms = terms;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String[] getNeTypes() {
		return neTypes;
	}

	public void setNeTypes(String[] neTypes) {
		this.neTypes = neTypes;
	}

	public String[] getExtractionTechniques() {
		return extractionTechniques;
	}

	public void setExtractionTechniques(String[] extractionTechniques) {
		this.extractionTechniques = extractionTechniques;
	}

	public ArrayList<String> getCoveredNuggets() {
		return coveredNuggets;
	}

	public void setCoveredNuggets(ArrayList<String> coveredNuggets) {
		this.coveredNuggets = coveredNuggets;
	}

	/**
	 * Compares two results by comparing their scores.
	 * 
	 * @param result the result to be compared
	 * @return a negative integer, zero or a positive integer as this result is
	 *         less than, equal to or greater than the specified result
	 */
	@Override
	public int compareTo(Result result) {
		double diff = score - result.getScore();
		
		if (diff < 0)
			return -1;
		else if (diff > 0)
			return 1;
		else
		{
			double priorityDiffScore = priority - result.priority;
			if(priorityDiffScore < 0)
				return -1;
			else if( priorityDiffScore > 0)
				return 1;
			else
				return 0;
		}
	}
	
	@Override
	public String toString() {
		return String.format(
				"Result [answer=%s, score=%s, normScore=%s, docID=%s, cacheID=%s, hitPos=%s, correct=%s, extraScores=%s, priority=%s, nes=%s, sentence=%s, neTypes=%s, extractionTechniques=%s, coveredNuggets=%s]",
				answer, score, normScore, docID, cacheID, hitPos, correct, extraScores, priority, nes, sentence, Arrays.toString(neTypes), Arrays.toString(extractionTechniques), coveredNuggets);
	}
	
}
