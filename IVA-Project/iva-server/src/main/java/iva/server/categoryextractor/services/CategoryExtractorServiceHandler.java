/**
 * 
 */
package iva.server.categoryextractor.services;

import iva.server.categoryextractor.model.CategoryGraph;
import iva.server.categoryextractor.model.DBpediaPage;
import iva.server.core.model.InterestModel;
import iva.server.exceptions.DBpediaException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Aron
 */
public class CategoryExtractorServiceHandler implements
		CategoryExtractorService {

	private final DBpediaService dbpediaService;
	private final DisambiguationFilter disambiguationFilter;

	private final Map<String, Double> tokenIdfs;
	private final Double defaultIdf;
	private final double idfThreshold;

	public CategoryExtractorServiceHandler(DBpediaService dbpediaService,
			DisambiguationFilter disambiguationFilter,
			Map<String, Double> tokenIdfs, Double defaultIdf,
			double idfThreshold) {
		this.dbpediaService = dbpediaService;
		this.disambiguationFilter = disambiguationFilter;
		this.tokenIdfs = tokenIdfs;
		this.defaultIdf = defaultIdf;
		this.idfThreshold = idfThreshold;
	}

	/**
	 * Extracts categories from a collection of terms. The terms in the
	 * collection are expected to belong to the same sentence.
	 * 
	 * @param terms
	 *            terms extracted from a sentence
	 * @return category names mapped to score
	 */
	@Override
	public Map<String, Double> extractCategories(List<String> terms) {

		Map<String, Set<DBpediaPage>> termPages = new HashMap<>(terms.size());
		List<String> termsNGram = terms.stream()
				.filter(term -> getTermIdf(term) > idfThreshold)
				.collect(Collectors.toList());
		for (int i = 0; i < termsNGram.size() - 2; i++) {

			String term = Character.toUpperCase(terms.get(i).charAt(0))
					+ terms.get(i).substring(1) + "_"
					+ Character.toUpperCase(terms.get(i + 1).charAt(0))
					+ terms.get(i + 1).substring(1) + "_"
					+ Character.toUpperCase(terms.get(i + 2).charAt(0))
					+ terms.get(i + 2).substring(2);
			Set<DBpediaPage> pages = dbpediaService.findDBpediaPages(term);
			if (pages.size() != 0) {
				termsNGram.remove(i);
				termsNGram.remove(i);
				termsNGram.remove(i);
			}
			termPages.put(term, pages);
		}
		for (int i = 0; i < termsNGram.size() - 1; i++) {
			String term = Character.toUpperCase(terms.get(i).charAt(0))
					+ terms.get(i).substring(1) + "_"
					+ Character.toUpperCase(terms.get(i + 1).charAt(0))
					+ terms.get(i + 1).substring(1);
			Set<DBpediaPage> pages = dbpediaService.findDBpediaPages(term);
			if (pages.size() != 0) {
				termsNGram.remove(i);
				termsNGram.remove(i);
			}
			termPages.put(term, pages);
		}
		for (int i = 0; i < termsNGram.size(); i++) {
			String term = Character.toUpperCase(terms.get(i).charAt(0))
					+ terms.get(i).substring(1);
			Set<DBpediaPage> pages = dbpediaService.findDBpediaPages(term);
			termPages.put(term, pages);
		}
		termPages = disambiguationFilter.doFilter(termPages);

		return scoreTermCategories(termPages);
	}

	public double getTermIdf(String term) {
		String[] tokens = term.toLowerCase().split(" ");
		return Arrays
				.stream(tokens)
				.mapToDouble(token -> tokenIdfs.getOrDefault(token, defaultIdf))
				.max().getAsDouble();
	}

	public Map<String, Double> scoreTermCategories(
			Map<String, Set<DBpediaPage>> termPages) {
		Map<String, Double> categoriesMap = new HashMap<>();
		termPages.forEach((term, pages) -> {
			Double score = getTermIdf(term);

			for (DBpediaPage page : pages) {
				Set<String> categories = page.getCategories();

				for (String category : categories) {
					categoriesMap.merge(category, score,
							(s1, s2) -> Math.max(s1, s2));
				}
			}
		});
		return categoriesMap;
	}

	@Override
	public Map<String, Double> extractExtraCategories(
			Map<String, Double> categories, InterestModel model) {
		Map<String, Double> extraCategories = new HashMap<>();

		Set<String> shortTermCategories = model.getShortTermCategories()
				.keySet();
		Set<String> longTermCategories = model.getLongTermCategories().keySet();

		System.out.println("Long term extra categories");
		Set<String> userCategories = new HashSet<>(longTermCategories);

		CategoryGraph graph = new CategoryGraph();
		populateGraph(graph, categories.keySet());
		populateGraph(graph, userCategories, 2);

		for (String category : categories.keySet()) {
			Double score = categories.get(category);

			Set<String> neighbours = categoryNeighbourSet(graph, category, 3);
			for (String neighbour : neighbours) {
				if (userCategories.contains(neighbour)
						&& categories.containsKey(neighbour)) {
					extraCategories.merge(neighbour, score,
							(s1, s2) -> Math.max(s1, s2));
				}
			}
		}

		System.out.println("Short term extra categories");
		userCategories.addAll(shortTermCategories);
		populateGraph(graph, shortTermCategories);

		for (String category : categories.keySet()) {
			Double score = categories.get(category);

			Set<String> neighbours = categoryNeighbourSet(graph, category, 2);
			for (String neighbour : neighbours) {
				if (userCategories.contains(neighbour)
						&& !categories.containsKey(neighbour)) {
					extraCategories.merge(neighbour, score,
							(s1, s2) -> Math.max(s1, s2));
				}
			}
		}
		return extraCategories;
	}

	/**
	 * Creates a graph of the provided categories linked to their expansions.
	 * The graph is created with a max search depth of 1.
	 * 
	 * @param categories
	 *            the initial set of categories to expand
	 * @return a graph of categories linked to their expansions
	 * @throws DBpediaException
	 */
	public void populateGraph(CategoryGraph graph, Set<String> categories) {
		populateGraph(graph, categories, 1);
	}

	/**
	 * Creates a graph of the provided categories linked to their expansions.
	 * The maxSearchDepth is the number of times to recursively expand
	 * categories.
	 * <p>
	 * If we define a child category as having a direct link with its parent:
	 * <ul>
	 * <li>maxSearchDepth=1 connects a category with its children
	 * <li>maxSearchDepth=2 connects a category with its children and connects
	 * those children to their children
	 * <li>maxSearchDepth=0 returns a graph containing the initial set of
	 * categories with no connections
	 * </ul>
	 * </p>
	 * 
	 * @param categories
	 *            the initial set of categories to expand
	 * @param maxSearchDepth
	 *            the number of times to recursively apply search
	 * @return a graph of categories linked to their expansions
	 * @throws DBpediaException
	 */
	public void populateGraph(CategoryGraph graph, Set<String> categories,
			int maxSearchDepth) {
		graph.addAllVertices(categories);
		if (maxSearchDepth <= 0)
			return;

		Set<String> expandedCategories = graph.getExpandedVertices();

		for (String category : categories) {
			Set<String> parentCategories = Collections.singleton(category);

			for (int depth = 0; depth < maxSearchDepth; depth++) {
				Set<String> childCategories = new HashSet<>();

				for (String parentCategory : parentCategories) {
					if (expandedCategories.contains(parentCategory))
						continue;
					expandedCategories.add(parentCategory);

					Set<String> expansions = dbpediaService
							.expandCategory(parentCategory);
					for (String expansion : expansions) {
						if (!expansion.equals(parentCategory)) {
							graph.addVertex(expansion);
							graph.addEdge(parentCategory, expansion);
							childCategories.add(expansion);
						}
					}
				}
				parentCategories = childCategories;
			}
		}
		return;
	}

	/**
	 * Returns the set of vertices found in all recursive spanning trees of a
	 * depth of 1 including the given vertex. In other words the set containing
	 * the vertex and its immediate neighbours is returned.
	 * 
	 * @param graph
	 * @param vertex
	 * @return
	 */
	public Set<String> categoryNeighbourSet(CategoryGraph graph, String vertex) {
		return categoryNeighbourSet(graph, vertex, 1);
	}

	/**
	 * Returns the set of vertices found in all recursive spanning trees of the
	 * given depth including the given vertex. Providing a maxDepth <= 1 will
	 * produce a set containing the vertex and its immediate neighbours.
	 * 
	 * @param graph
	 * @param vertex
	 * @param maxDepth
	 * @return set of vertices
	 */
	public Set<String> categoryNeighbourSet(CategoryGraph graph, String vertex,
			int maxDepth) {
		Set<String> traversedVertices = new HashSet<>();
		traversedVertices.add(vertex);

		List<String> neighbours = graph.getNeighbourListOf(vertex);
		traversedVertices.addAll(neighbours);

		if (maxDepth <= 1)
			return traversedVertices;

		Set<String> parentVertices = new HashSet<>(neighbours);

		for (int depth = 1; depth < maxDepth; depth++) {
			Set<String> childVertices = new HashSet<>();

			for (String parentVertex : parentVertices) {
				neighbours = graph.getNeighbourListOf(parentVertex);

				for (String neighbour : neighbours) {
					if (!traversedVertices.contains(neighbour)) {
						traversedVertices.add(neighbour);
						childVertices.add(neighbour);
					}
				}
			}
			parentVertices = childVertices;
		}
		return traversedVertices;
	}

}
