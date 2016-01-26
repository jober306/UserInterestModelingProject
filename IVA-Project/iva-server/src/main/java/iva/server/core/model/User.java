package iva.server.core.model;

import iva.server.security.PasswordHash;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
public class User implements Serializable {
	private static final long serialVersionUID = -841604606040519831L;

	@Id
	@GeneratedValue
	@Column(name = "userID")
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date created = new Date();
	
	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false)
	private String passwordHash;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;
	
	private String firstName;
	private String lastName;
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@PrimaryKeyJoinColumn
	private InterestModel interestModel;
	
	@OneToMany(mappedBy = "owner", orphanRemoval = true)
	@OrderBy("created")
	private List<Question> questionHistory = new ArrayList<>(0);
	
	protected User() {
	}
	
	public User(String username, char[] password, String firstName, String lastName) {
		this.setUsername(username);
		this.setPassword(password);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setInterestModel(new InterestModel(this));
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

	public InterestModel getInterestModel() {
		return interestModel;
	}

	public void setInterestModel(InterestModel interestModel) {
		this.interestModel = interestModel;
	}

	public List<Question> getQuestionHistory() {
		return questionHistory;
	}

	public void setQuestionHistory(List<Question> questionHistory) {
		this.questionHistory = questionHistory;
	}
	
	/**
	 * @see java.util.List#add(E)
	 */
	public boolean addQuestion(Question question) {
		question.setOwner(this);
		return questionHistory.add(question);
	}

	@Override
	public String toString() {
		return String.format(
				"User [id=%s, created=%s, username=%s, passwordHash=%s, lastLogin=%s, firstName=%s, lastName=%s]",
				id, created, username, passwordHash, lastLogin, firstName, lastName);
	}

}
