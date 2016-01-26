package iva.client.core.model;

import iva.client.security.PasswordHash;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User extends Entity {
	private static final long serialVersionUID = 4225494629210354984L;
	
	private Date created = new Date();
	
	private String username;
	
	private String passwordHash;
	
	private Date lastLogin = null;
	
	private String firstName;
	
	private String lastName;
	
	@JsonProperty("interestModel")
	private URI interestModelLink;
	
	@JsonProperty("questionHistory")
	private List<URI> questionHistoryLinks = new ArrayList<>(0);
	
	protected User() {
	}
	
	public User(String username, char[] password, String firstName, String lastName) {
		this.setUsername(username);
		this.setPassword(password);
		this.setFirstName(firstName);
		this.setLastName(lastName);
	}

	public Date getCreated() {
		return created;
	}

	protected void setCreated(Date created) {
		this.created = created;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	protected void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public void setPassword(char[] password) {
		try {
			this.setPasswordHash(PasswordHash.createHash(password));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		} finally {
			Arrays.fill(password, Character.MIN_VALUE);
		}
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public URI getInterestModelLink() {
		return interestModelLink;
	}

	public void setInterestModelLink(URI interestModelLink) {
		this.interestModelLink = interestModelLink;
	}

	public List<URI> getQuestionHistoryLinks() {
		return questionHistoryLinks;
	}

	public void setQuestionHistoryLinks(List<URI> questionHistoryLinks) {
		this.questionHistoryLinks = questionHistoryLinks;
	}

	@Override
	public String toString() {
		return String.format(
				"User [created=%s, username=%s, passwordHash=%s, lastLogin=%s, firstName=%s, lastName=%s, interestModelLink=%s, questionHistoryLinks=%s, getId()=%s, isNew()=%s]",
				created, username, passwordHash, lastLogin, firstName, lastName, interestModelLink, questionHistoryLinks, getId(), isNew());
	}

}
