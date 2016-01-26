package iva.client.core.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InterestModel extends Entity {
	private static final long serialVersionUID = -702825436865695417L;
	
	private Map<String, Double> shortTermCategories = new HashMap<>(0);
	
	private Map<String, Double> longTermCategories = new HashMap<>(0);
	
	private Map<String, String> properties = new HashMap<>();
	
	@JsonProperty("owner")
	private URI ownerLink;
	
	public InterestModel() {
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

	public URI getOwnerLink() {
		return ownerLink;
	}

	public void setOwnerLink(URI ownerLink) {
		this.ownerLink = ownerLink;
	}

	@Override
	public String toString() {
		return String.format(
				"InterestModel [shortTermCategories=%s, longTermCategories=%s, properties=%s, ownerLink=%s, getId()=%s, isNew()=%s]",
				shortTermCategories, longTermCategories, properties, ownerLink, getId(), isNew());
	}

}
