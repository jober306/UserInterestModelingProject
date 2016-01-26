package iva.server.categoryextractor.services;

import iva.server.core.model.InterestModel;

import java.util.List;
import java.util.Map;

public interface CategoryExtractorService {

	Map<String, Double> extractCategories(List<String> terms);

	/**
	 * Finds categories that are related to an extracted set of categories by
	 * expanding the categories and comparing the expansions to an existing
	 * interest model.
	 * 
	 * @param categories
	 * @param model
	 * @return extra categories with their scores
	 */
	Map<String, Double> extractExtraCategories(Map<String, Double> categories,
			InterestModel model);

}
