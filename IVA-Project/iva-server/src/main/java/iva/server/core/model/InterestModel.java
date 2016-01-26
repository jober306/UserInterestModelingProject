package iva.server.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

@Entity
public class InterestModel implements Serializable {
	private static final long serialVersionUID = -702825436865695417L;

	@Id
	@Column(name = "userID")
	private Long id;
	
	@MapsId
	@OneToOne(mappedBy = "interestModel")
	@JoinColumn(name = "userID")
	private User owner;
	
	@ElementCollection
	@CollectionTable(joinColumns=@JoinColumn(name = "userID"))
	@MapKeyColumn(name = "category")
	@Column(name = "score")
	private Map<String, Double> shortTermCategories = new HashMap<>(0);
	
	@ElementCollection
	@CollectionTable(joinColumns=@JoinColumn(name = "userID"))
	@MapKeyColumn(name = "category")
	@Column(name = "score")
	private Map<String, Double> longTermCategories = new HashMap<>(0);
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(joinColumns=@JoinColumn(name = "userID"))
	@MapKeyColumn(name = "property")
	@Column(name = "value")
	private Map<String, String> properties = new HashMap<>();
	
	protected InterestModel() {
	}

	public InterestModel(User owner) {
		this.setOwner(owner);
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Map<String, Double> getShortTermCategories() {
		return shortTermCategories;
	}

	public void setShortTermCategories(Map<String, Double> shortTermCategories) {
		this.shortTermCategories = shortTermCategories;
	}

	public Map<String, Double> getLongTermCategories() {
		return longTermCategories;
	}

	public void setLongTermCategories(Map<String, Double> longTermCategories) {
		this.longTermCategories = longTermCategories;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Double getShortTermAgeRate() {
		return Double.valueOf(properties
				.getOrDefault("shortTermAgeRate", Double.toString(0.0)) );
	}

	public void setShortTermAgeRate(Double shortTermAgeRate) {
		properties.put("shortTermAgeRate", shortTermAgeRate.toString());
	}

	public Double getLongTermAgeRate() {
		return Double.valueOf(properties
				.getOrDefault("longTermAgeRate", Double.toString(0.0)) );
	}

	public void setLongTermAgeRate(Double longTermAgeRate) {
		properties.put("longTermAgeRate", longTermAgeRate.toString());
	}

	public Double getPromotionThreshold() {
		return Double.valueOf(properties
				.getOrDefault("promotionThreshold", Double.toString(-1.0)) );
	}

	public void setPromotionThreshold(Double promotionThreshold) {
		properties.put("promotionThreshold", promotionThreshold.toString());
	}

	public Double getDemotionThreshold() {
		return Double.valueOf(properties
				.getOrDefault("demotionThreshold", Double.toString(0.0)) );
	}

	public void setDemotionThreshold(Double demotionThreshold) {
		properties.put("demotionThreshold", demotionThreshold.toString());
	}

	public Double getExpirationThreshold() {
		return Double.valueOf(properties
				.getOrDefault("expirationThreshold", Double.toString(0.0)) );
	}

	public void setExpirationThreshold(Double expirationThreshold) {
		properties.put("expirationThreshold", expirationThreshold.toString());
	}

	@Override
	public String toString() {
		return String.format(
				"InterestModel [id=%s, shortTermCategories=%s, longTermCategories=%s, properties=%s]",
				id, shortTermCategories, longTermCategories, properties);
	}

}
