package iva.server.core.model;

import iva.server.ephyra.model.AnalyzedQuestion;
import iva.server.ephyra.model.Term;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Question implements Serializable {
	private static final long serialVersionUID = -4514649358705841258L;

	@Id
	@GeneratedValue
	@Column(name = "questionID")
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date created;

	@ManyToOne
	@JoinColumn(name = "userID")
	private User owner;

	@Column(nullable = false)
	private String question;

	@ElementCollection
	@CollectionTable(joinColumns = @JoinColumn(name = "questionID"))
	@Column(name = "term")
	private List<String> terms = new ArrayList<>(0);

	@ElementCollection
	@CollectionTable(joinColumns = @JoinColumn(name = "questionID"))
	@MapKeyColumn(name = "category")
	@Column(name = "score")
	private Map<String, Double> categories = new HashMap<>(0);

	@ElementCollection
	@CollectionTable(joinColumns = @JoinColumn(name = "questionID"))
	@MapKeyColumn(name = "category")
	@Column(name = "score")
	private Map<String, Double> extraCategories = new HashMap<>(0);

	@OneToMany(mappedBy = "sourceQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("score DESC")
	private List<Answer> answers = new ArrayList<>(0);

	protected Question() {
	}

	public Question(AnalyzedQuestion aq) {
		this.setCreated(new Date());
		this.setQuestion(aq.getQuestion());

		Term[] aqTerms = aq.getTerms();
		if (aqTerms == null)
			aqTerms = new Term[0];

		List<String> terms = new ArrayList<>();
		for (Term term : aqTerms) {
			terms.add(term.getText());
		}
		this.setTerms(terms);
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	protected void setCreated(Date created) {
		this.created = created;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getTerms() {
		return terms;
	}

	public void setTerms(List<String> terms) {
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

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	/**
	 * @see java.util.List#add(E)
	 */
	public boolean addAnswer(Answer answer) {
		answer.setSourceQuestion(this);
		return answers.add(answer);
	}

	@Override
	public String toString() {
		return String
				.format("Question [id=%s, created=%s, question=%s, terms=%s, categories=%s, extraCategories=%s]",
						id, created, question, terms, categories,
						extraCategories);
	}

}
