package iva.server.core.services;

import iva.server.core.model.InterestModel;

import java.util.Map;

public interface InterestModelService {

	/**
	 * Adds a set of categories and applies aging, promotion, demotion, and 
	 * expiration rules to the given interest model instance.
	 * @param categories the categories to add to the interest model
	 * @param model the interest model instance to update
	 * @return TODO
	 */
	InterestModel update(Map<String, Double> categories, InterestModel model);

}