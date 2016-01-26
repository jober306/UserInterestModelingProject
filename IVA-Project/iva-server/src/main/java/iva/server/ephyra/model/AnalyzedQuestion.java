package iva.server.ephyra.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <p>An <code>AnalyzedQuestion</code> is a data structure representing a
 * syntactic and semantic analysis of a question.</p>
 * 
 * <p>This class implements the interface <code>Serializable</code>.</p>
 * 
 * @author Nico Schlaefer
 * @version 2007-07-17
 */
public class AnalyzedQuestion implements Serializable {
	private static final long serialVersionUID = -49794003933270051L;

	/** Question string. */
	private String question;
	
	/** Normalized question string. */
	private String qn;
	
	/** Question string with stemmed verbs and nouns. */
	private String stemmed;
	
	/** Question string with modified verbs. */
	private String verbMod;
	
	/** Keywords in the question and context. */
	private String[] kws = new String[0];
	
	/** Head word in the question (headWord is the first keyword of a sentence ( first NP after the wh-- question word) */
	private String headWord;
	
	/** Named entities in the question and context. */
	private String[][] nes = new String[0][0];
	
	/** Terms in the question and context. */
	private Term[] terms = new Term[0];
	
	/** Focus word. */
	private String focus;
	
	/** Expected answer types. */
	private String[] ats = new String[0];
	
	/** Indicates that this is a factoid question. */
	private boolean isFactoid = true;
	
	public AnalyzedQuestion() {
	}

	public AnalyzedQuestion(String question) {
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getNormalized() {
		return qn;
	}

	public void setNormalized(String qn) {
		this.qn = qn;
	}

	public String getStemmed() {
		return stemmed;
	}

	public void setStemmed(String stemmed) {
		this.stemmed = stemmed;
	}

	public String getVerbMod() {
		return verbMod;
	}

	public void setVerbMod(String verbMod) {
		this.verbMod = verbMod;
	}

	public String[] getKeywords() {
		return kws;
	}

	public void setKeywords(String[] kws) {
		this.kws = kws;
	}

	public String getHeadWord() {
		return headWord;
	}

	public void setHeadWord(String headWord) {
		this.headWord = headWord;
	}

	public String[][] getNes() {
		return nes;
	}

	public void setNes(String[][] nes) {
		this.nes = nes;
	}

	public Term[] getTerms() {
		return terms;
	}

	public void setTerms(Term[] terms) {
		this.terms = terms;
	}

	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public String[] getAnswerTypes() {
		return ats;
	}

	public void setAnswerTypes(String[] ats) {
		this.ats = ats;
	}

	public boolean isFactoid() {
		return isFactoid;
	}

	public void setFactoid(boolean isFactoid) {
		this.isFactoid = isFactoid;
	}

	@Override
	public String toString() {
		StringBuilder nesBuilder = new StringBuilder("[");
		if(nes.length > 0) {
			nesBuilder.append(Arrays.toString(nes[0]));
			for (int i = 1; i < nes.length; i++)
				nesBuilder.append(", " + Arrays.toString(nes[i]));
		}
		nesBuilder.append("]");
		
		return String.format(
				"AnalyzedQuestion [question=%s, qn=%s, stemmed=%s, verbMod=%s, kws=%s, headWord=%s, nes=%s, focus=%s, ats=%s, isFactoid=%s]",
				question, qn, stemmed, verbMod, Arrays.toString(kws), headWord, nesBuilder, focus, Arrays.toString(ats), isFactoid);
	}
	
}
