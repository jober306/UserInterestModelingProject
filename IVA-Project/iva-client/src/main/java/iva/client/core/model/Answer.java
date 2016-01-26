package iva.client.core.model;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Answer extends Entity {
	private static final long serialVersionUID = -8102991120442758266L;
	
	private String answer;
	
	private Double score = 0.0;
	
	private Integer rating = 0;
	
	@JsonIgnore
	private Question sourceQuestion;
	
	private String sourceQuery;
	
	private String sourceDocument;
	
	private Set<String> terms = new HashSet<>(0);
	
	private Map<String, Double> categories = new HashMap<>(0);
	
	private Map<String, Double> extraCategories = new HashMap<>(0);
	
	@JsonProperty("sourceQuestion")
	private URI sourceQuestionLink;
	
	public Answer() {
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Question getSourceQuestion() {
		return sourceQuestion;
	}

	public void setSourceQuestion(Question sourceQuestion) {
		this.sourceQuestion = sourceQuestion;
	}

	public String getSourceQuery() {
		return sourceQuery;
	}

	public void setSourceQuery(String sourceQuery) {
		this.sourceQuery = sourceQuery;
	}

	public String getSourceDocument() {
		return sourceDocument;
	}

	public void setSourceDocument(String sourceDocument) {
		this.sourceDocument = sourceDocument;
	}

	public Set<String> getTerms() {
		return terms;
	}

	public void setTerms(Set<String> terms) {
		this.terms = terms;
	}

	public Map<String, Double> getCategories() {
		return categories;
	}

	public void setCategories(Map<String, Double> categories) {
		this.categories = categories;
	}

	public Map<String, Double> getExtraCategories() {
		return extraCategories;
	}

	public void setExtraCategories(Map<String, Double> extraCategories) {
		this.extraCategories = extraCategories;
	}

	public URI getSourceQuestionLink() {
		return sourceQuestionLink;
	}

	public void setSourceQuestionLink(URI sourceQuestionLink) {
		this.sourceQuestionLink = sourceQuestionLink;
	}

	@Override
	public String toString() {
		return String.format(
				"Answer [answer=%s, score=%s, rating=%s, sourceQuestion=%s, sourceQuery=%s, sourceDocument=%s, terms=%s, categories=%s, extraCategories=%s, getId()=%s, isNew()=%s]",
				answer, score, rating, sourceQuestionLink, sourceQuery, sourceDocument, terms, categories, extraCategories, getId(), isNew());
	}
	
}
