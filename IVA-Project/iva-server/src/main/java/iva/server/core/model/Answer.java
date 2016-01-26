package iva.server.core.model;

import iva.server.ephyra.model.Query;
import iva.server.ephyra.model.Result;
import iva.server.ephyra.model.Term;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

@Entity
public class Answer implements Serializable {
	private static final long serialVersionUID = -8102991120442758266L;

	@Id
	@GeneratedValue
	@Column(name = "answerID")
	private Long id;
	
	@Column(nullable = false)
	private String answer;
	
	private Double score = 0.0;
	
	private Integer rating = 0;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "questionID", nullable = false)
	private Question sourceQuestion;
	
	private String sourceQuery;
	
	private String sourceDocument;
	
	@ElementCollection
	@CollectionTable(joinColumns=@JoinColumn(name = "answerID") )
	@Column(name = "term")
	private Set<String> terms = new HashSet<>(0);
	
	@ElementCollection
	@CollectionTable(joinColumns=@JoinColumn(name = "answerID") )
	@MapKeyColumn(name = "category")
	@Column(name = "score")
	private Map<String, Double> categories = new HashMap<>(0);
	
	@ElementCollection
	@CollectionTable(joinColumns=@JoinColumn(name = "answerID") )
	@MapKeyColumn(name = "category")
	@Column(name = "score")
	private Map<String, Double> extraCategories = new HashMap<>(0);
	
	protected Answer() {
	}
	
	public Answer(Result result) {
		this.setAnswer(result.getAnswer());
		this.setScore(result.getScore());
		
		Term[] resultTerms = result.getTerms();
		if (resultTerms == null)
			resultTerms = new Term[0];
		
		Set<String> terms = new HashSet<>();
		for(Term term : resultTerms) {
			terms.add(term.getText());
		}
		this.setTerms(terms);
		
		Query query = result.getQuery();
		this.setSourceQuery(query != null ? query.getQueryString() : null);
		this.setSourceDocument(result.getDocID());
	}
	
	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
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

	@Override
	public String toString() {
		return String.format(
				"Answer [id=%s, answer=%s, score=%s, rating=%s, sourceQuery=%s, sourceDocument=%s, terms=%s, categories=%s, extraCategories=%s]",
				id, answer, score, rating, sourceQuery, sourceDocument, terms, categories, extraCategories);
	}
	
}
