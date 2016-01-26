package iva.server.categoryextractor.util;

import org.tartarus.snowball.ext.englishStemmer;

/**
 * Word stemmer using Snowball Stemmer for the English language.
 * Snowball Stemmer is an implementation of the Porter2 stemming algorithm.
 */
public class EnglishSnowballStemmer implements WordStemmer {
	
	/** Snowball stemmer for the English language. */
	private final englishStemmer stemmer = new englishStemmer();
	
    @Override
	public synchronized String stem(String word) {
		stemmer.setCurrent(word);
		stemmer.stem();
		return stemmer.getCurrent();
    }
	
}
