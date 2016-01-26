package iva.server.categoryextractor.services;

import iva.server.categoryextractor.model.DBpediaPage;
import iva.server.exceptions.DBpediaException;

import java.util.Set;

public interface DBpediaService {

	/**
	 * Creates a Set of {@link DBpediaPage} by querying dbpedia with the
	 * given term. An empty Set will be returned upon an HttpException.
	 * @param term
	 * @return set of DBpeidaPages found for the term
	 * @throws DBpediaException if the dbpedia endpoint is unavailable.
	 */
	Set<DBpediaPage> findDBpediaPages(String term) throws DBpediaException;

	/**
	 * Expands a category by querying for related categories.
	 * @param category
	 * @return set of category expansions not including category
	 * @throws DBpediaException if the dbpedia endpoint is unavailable.
	 */
	Set<String> expandCategory(String category) throws DBpediaException;

	/**
	 * Search for all categories containing the specified regex.
	 * This method does not use a full text index and may be slow.
	 * @param regex
	 * @return set of category matching the regex pattern
	 * @throws DBpediaException if the dbpedia endpoint is unavailable.
	 */
	Set<String> findCategoriesContaining(String regex) throws DBpediaException;

}