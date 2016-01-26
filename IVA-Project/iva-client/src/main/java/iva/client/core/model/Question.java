package iva.client.core.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Question extends Entity {
	private static final long serialVersionUID = -4514649358705841258L;
	
	private Date created = new Date();
	
	private String question;
	
	private Set<String> terms = new HashSet<>(0);
	
	private Map<String, Double> categories = new HashMap<>(0);
	
	private Map<String, Double> extraCategories = new HashMap<>(0);
	
	@JsonIgnore
	private List<Answer> answers = new ArrayList<>(0);
	
	@JsonProperty("answers")
	private List<URI> answerLinks = new ArrayList<>(0);
	
	@JsonProperty("owner")
	private URI ownerLink;
	
	public Question() {
	}
	
	public Date getCreated() {
		return created;
	}

	protected void setCreated(Date created) {
		this.created = created;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
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

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public List<URI> getAnswerLinks() {
		return answerLinks;
	}

	public void setAnswerLinks(List<URI> answerLinks) {
		this.answerLinks = answerLinks;
	}

	public URI getOwnerLink() {
		return ownerLink;
	}

	public void setOwnerLink(URI ownerLink) {
		this.ownerLink = ownerLink;
	}

	@Override
	public String toString() {
		return String.format(
				"Question [created=%s, question=%s, terms=%s, categories=%s, extraCategories=%s, answerLinks=%s, ownerLink=%s, getId()=%s, isNew()=%s]",
				created, question, terms, categories, extraCategories, answerLinks, ownerLink, getId(), isNew());
	}

}
