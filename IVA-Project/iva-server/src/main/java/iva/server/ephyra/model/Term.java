package iva.server.ephyra.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A <code>Term</code> comprises one or more tokens of text that form a unit
 * of meaning. It can be an individual word, a compound noun or a named entity.
 * </p>
 * 
 * <p>This class implements the interface <code>Serializable</code>.</p>
 * 
 * @author Nico Schlaefer
 * @version 2008-01-23
 */
public class Term implements Serializable {
	private static final long serialVersionUID = 6512151122835991305L;
	
	/** The textual representation of the term. */
	private String text;
	
	/** The lemma of the term. */
	private String lemma;
	
	/**
	 * The part of speech of the term or <code>COMPOUND</code> to indicate that
	 * it comprises multiple tokens.
	 */
	private String pos;
	
	/** The named entity types of the term (optional). */
	private String[] neTypes = new String[0];
	
	/** Relative frequency of the term. */
	private double relFrequency;
	
	/** Maps expansions of the term to their weights. */
	private Map<String, Double> expansions = new HashMap<>(0);
	
	/** Maps lemmas of the expansions to their weights. */
	private Map<String, Double> expansionLemmas = new HashMap<>(0);
	
	public Term() {
	}
	
	public Term(String text) {
		this.setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String[] getNeTypes() {
		return neTypes;
	}

	public void setNeTypes(String[] neTypes) {
		this.neTypes = neTypes;
	}

	public double getRelFrequency() {
		return relFrequency;
	}

	public void setRelFrequency(double relFrequency) {
		this.relFrequency = relFrequency;
	}

	public Map<String, Double> getExpansions() {
		return expansions;
	}

	public void setExpansions(Map<String, Double> expansions) {
		this.expansions = expansions;
	}

	public Map<String, Double> getExpansionLemmas() {
		return expansionLemmas;
	}

	public void setExpansionLemmas(Map<String, Double> expansionLemmas) {
		this.expansionLemmas = expansionLemmas;
	}

	@Override
	public String toString() {
		return String.format(
				"Term [text=%s, lemma=%s, pos=%s, neTypes=%s, relFrequency=%s, expansions=%s, expansionLemmas=%s]",
				text, lemma, pos, Arrays.toString(neTypes), relFrequency, expansions, expansionLemmas);
	}
}
