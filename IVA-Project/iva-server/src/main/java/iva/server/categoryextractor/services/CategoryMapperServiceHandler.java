/**
 * 
 */
package iva.server.categoryextractor.services;

import iva.server.categoryextractor.util.WordMatcher;
import iva.server.exceptions.DBpediaException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Aron
 */
public class CategoryMapperServiceHandler implements CategoryMapperService {
	
	private final DBpediaService dbpedia;
	
	public CategoryMapperServiceHandler(DBpediaService dbpedia) {
		this.dbpedia = dbpedia;
	}
	
	@Override
	public Optional<String> mapToCategory(String term) throws DBpediaException {
		// TODO build a full text index on the ontology and search that index
		
		/* This solution retrieves all categories then applies a regex filter 
		 * to select categories containing the stemmed term.
		 */
		Set<String> categories = dbpedia.findCategoriesContaining(term);
		
		Map<String, Double> categoryScores = new HashMap<>();
		
		for(String category : categories) {
			if(WordMatcher.stemmedEquals(term, category)) {
				return Optional.of(category);
			}
			
			int count = WordMatcher.countWordMatches(term, category);
			int totalWords = WordMatcher.countTotalWords(category);
			double score = (double) count / totalWords;
			if (count > 0) {
				categoryScores.put(category, score);
			}
		}
		return categoryScores.keySet().stream()
				.max((c1, c2) -> categoryScores.get(c1).compareTo(categoryScores.get(c2)));
	}

}
