package iva.server.categoryextractor.services;

import java.util.Optional;

import iva.server.exceptions.DBpediaException;

public interface CategoryMapperService {

	/**
	 * Searches for a dbpedia category name that best matches the given term.
	 * If there is one category that has that exact name it's the match, 
	 * otherwise it picks the category name with the most words in common.
	 * If no category can be found then {@code null} is returned.
	 * @param term
	 * @return optional mapped category name
	 * @throws DBpediaException 
	 */
	Optional<String> mapToCategory(String term) throws DBpediaException;

}