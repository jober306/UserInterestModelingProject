package iva.server.categoryextractor.util;

/**
 * Interface for stemming a single word or token.
 */
public interface WordStemmer {

	/**
	 * Stems a single word.
	 * @param word the word to be stemmed
	 * @return stemmed word
	 */
	String stem(String word);

}