package iva.server.categoryextractor.services;

import iva.server.categoryextractor.model.DBpediaPage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DisambiguationFilter {
	public final double pageFactor;
	public final double linkFactor;
	public final double categoryFactor;
	
	public DisambiguationFilter(double pageFactor, double linkFactor, double categoryFactor) {
		this.pageFactor = pageFactor;
		this.linkFactor = linkFactor;
		this.categoryFactor = categoryFactor;
	}
	
	public Map<String, Set<DBpediaPage>> doFilter(Map<String, Set<DBpediaPage>> termPages) {
		Map<String, Set<DBpediaPage>> filteredTermPages = new HashMap<>(termPages.size());
		
		for(String term : termPages.keySet()) {
			Set<DBpediaPage> pages = termPages.get(term);
			
			DBpediaPage topPage = null;
			for(DBpediaPage page : pages) {
				
				int pCount = 0;
				int lCount = 0;
				int cCount = 0;
				
				for(String otherTerm : termPages.keySet())
				{
					if(otherTerm.equals(term)) continue;
					Set<DBpediaPage> otherPages = termPages.get(otherTerm);
					
					for(DBpediaPage otherPage : otherPages)
					{
						if( page.getName().equals(otherPage.getName()) ) {
							pCount++;
						} else {
							for(String link : otherPage.getPageLinks()) {
								if( page.getName().equals(link) ) {
									lCount++;
								}
							}
							for(String category : otherPage.getCategories()) {
								if( page.getCategories().contains(category) ) {
									cCount++;
								}
							}
						}
					}
				}
				double score = pageFactor*pCount + linkFactor*lCount + categoryFactor*cCount;
				page.setScore(score);
				
				if (topPage == null || score > topPage.getScore()) {
					topPage = page;
				}
			}
			
			if(topPage != null && topPage.getScore() > 0) {
				filteredTermPages.put(term, Collections.singleton(topPage));
			} else {
				filteredTermPages.put(term, pages);
			}
		}
		return filteredTermPages;
	}
}