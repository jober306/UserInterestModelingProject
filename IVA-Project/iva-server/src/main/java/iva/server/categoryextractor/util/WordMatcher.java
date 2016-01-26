/**
 * 
 */
package iva.server.categoryextractor.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Aron
 */
public class WordMatcher {
	
	private static final Set<String> stopwords = readStopwordsFile("stopwords.txt");
	private static final WordStemmer stemmer = new EnglishSnowballStemmer();
	
	public static boolean containsWordMatch(String sentence0, String sentence1) {
		String[] words0 = getStemmedWords(sentence0);
		String[] words1 = getStemmedWords(sentence1);
		
		// Return true if any of the words match
		for(String word0 : words0) {
			for(String word1 : words1) {
				if(word0.equalsIgnoreCase(word1)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static int countWordMatches(String sentence0, String sentence1) {
		String[] words0 = getStemmedWords(sentence0);
		String[] words1 = getStemmedWords(sentence1);
		
		// Count if any of the words match
		int count = 0;
		for(String word0 : words0) {
			for(String word1 : words1) {
				if(word0.equalsIgnoreCase(word1)) {
					count++;
				}
			}
		}
		return count;
	}
	
	public static int countTotalWords(String sentence) {
		return getStemmedWords(sentence).length;
	}
	
	public static boolean stemmedEquals(String sentence0, String sentence1) {
		String stemmed0 = getStemmedSentence(sentence0);
		String stemmed1 = getStemmedSentence(sentence1);
		
		return stemmed0.equalsIgnoreCase(stemmed1);
	}
	
	public static String getStemmedSentence(String sentence) {
		String stemmed = "";
		
		String[] words = getWords(sentence);
		
		if (words.length > 0) stemmed += stemmer.stem(words[0]);
		for (int i = 1; i < words.length; i++)
			stemmed += " " + stemmer.stem(words[i]);
		
		return stemmed;
	}

	public static String[] getStemmedWords(String sentence) {
		String[] words = getWords(sentence);
		String[] stemmedWords = new String[words.length];
		
		for(int i=0; i < words.length; i++) {
			stemmedWords[i] = stemmer.stem(words[i]);
		}
		return stemmedWords;
	}
	
	public static String[] getWords(String sentence) {
		sentence = preprocess(sentence);
		
		List<String> words = new ArrayList<>();
		
		Pattern wordRegex = Pattern.compile("(?i)\\b(\\w+?)\\b");
		Matcher wordMatcher = wordRegex.matcher(sentence);
		
		while(wordMatcher.find()) {
			String word = wordMatcher.group();
			words.add(word);
		}
		return words.toArray(new String[words.size()]);
	}

	public static String preprocess(String sentence) {
		sentence = sentence.replace('_', ' ');
		for(String stopword : stopwords) {
			sentence = sentence.replaceAll("(?i)\\b\\Q"+stopword+"\\E\\b[^']", "");
		}
		return sentence.trim();
	}
	
	private static Set<String> readStopwordsFile(String fileName) {
		URL resource = WordMatcher.class.getClassLoader().getResource(fileName);
		try {
			Path file = Paths.get(resource.toURI());
			return Files.lines(file)
					.map(line -> line.trim())
					.filter(line -> !line.isEmpty())
					.collect(Collectors.toSet());
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Unable to read stopwords file: "+resource, e);
		}
	}

}
