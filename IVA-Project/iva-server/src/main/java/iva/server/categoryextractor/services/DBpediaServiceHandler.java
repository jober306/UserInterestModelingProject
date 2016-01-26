package iva.server.categoryextractor.services;

import iva.server.categoryextractor.model.DBpediaPage;
import iva.server.exceptions.DBpediaException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

/**
 * Provides methods for querying a DBpedia Ontology SPARQL endpoint.
 * 
 * @author Aron
 */
public class DBpediaServiceHandler implements DBpediaService {
	private static final Logger log = LoggerFactory
			.getLogger(DBpediaServiceHandler.class);

	// public static final String PRIVATE_SERVICE_URI =
	// "http://se.lakeheadu.ca:3030/dbpedia/sparql";
	public static final String PUBLIC_SERVICE_URI = "http://dbpedia.org/sparql";
	public static final String DEFAULT_SERVICE_URI = PUBLIC_SERVICE_URI;

	private static final String SPARQL_PREFIXES = ""
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX category: <http://dbpedia.org/resource/Category:> "
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX dcterms: <http://purl.org/dc/terms/> ";

	private String serviceURI;

	/**
	 * Creates a DBpedia Ontology Service using the default service URI.
	 */
	public DBpediaServiceHandler() {
		this.serviceURI = DEFAULT_SERVICE_URI;
	}

	/**
	 * Creates a DBpedia Ontology Service using the given service URI.
	 * 
	 * @param serviceURI
	 *            the URI of the ontology service endpoint to use
	 */
	public DBpediaServiceHandler(String serviceURI) {
		this.serviceURI = serviceURI;
	}

	@Override
	public Set<DBpediaPage> findDBpediaPages(String term)
			throws DBpediaException {
		Set<DBpediaPage> dbpediaPages = new HashSet<>();
		log.info("Querying dbpedia with term: " + term);

		String redirect = findRedirect(term);
		if (redirect == "") {
			log.info("Redirect: No redirect found.");
			return dbpediaPages;
		}
		log.info("Redirect: " + redirect);

		Set<String> pages = findDisambiguations(redirect);
		log.info(pages.size() + " pages found");

		for (String page : pages) {
			DBpediaPage dbpediaPage = new DBpediaPage(page, term);

			Set<String> categories = findCategories(page);
			log.debug(categories.size() + " categories found");
			dbpediaPage.setCategories(categories);

			Set<String> pageLinks = findPageLinks(page);
			log.debug(categories.size() + " links found");
			dbpediaPage.setPageLinks(pageLinks);

			dbpediaPages.add(dbpediaPage);
		}
		return dbpediaPages;
	}

	/**
	 * Pings the given ontology service endpoint with a lightweight query to
	 * determine if the service endpoint is available.
	 * 
	 * @param serviceURI
	 *            the URI of the ontology service endpoint to test
	 * @return true if the ping returned a result; false otherwise
	 */
	public static boolean isServiceAvailable(String serviceURI) {
		boolean isAvailable = false;

		String query = "ASK { }";

		try (QueryExecution qe = QueryExecutionFactory.sparqlService(
				serviceURI, query)) {
			if (qe.execAsk()) {
				isAvailable = true;
			}
		} catch (QueryExceptionHTTP e) {
			isAvailable = false;
		}
		return isAvailable;
	}

	/**
	 * Pings the ontology service endpoint with a lightweight query to determine
	 * if the service endpoint is available.
	 * 
	 * @return true if the ping returned a result; false otherwise
	 */
	public boolean isServiceAvailable() {
		return isServiceAvailable(this.serviceURI);
	}

	/**
	 * Sends a query to the dbpedia ontology service to retrieve the
	 * pre-processed subject name of the Wikipedia redirect for the given
	 * subject name.
	 * 
	 * @param subject
	 *            the name of the subject to query
	 * @return the redirected subject name pre-processed for subsequent queries
	 * @throws DBpediaException
	 */
	public String findRedirect(String subject) throws DBpediaException {
		String redirect = encodeSubjectName(subject);

		String subjectURI = "dbpedia:" + redirect;
		String predicateURI = "dbpedia-owl:wikiPageRedirects";
		Set<String> redirectURIs = selectObjectURIs(subjectURI, predicateURI);

		if (redirectURIs.iterator().hasNext()) {
			assert (redirectURIs.size() == 1);
			String redirectURI = redirectURIs.iterator().next();
			String uriPrefix = "http://dbpedia.org/resource/";
			redirect = removeURIPrefix(redirectURI, uriPrefix);
		} else {
			return "";
		}
		return decodeSubjectName(redirect);
	}

	/**
	 * Sends a query to the dbpedia ontology service to retrieve a list of
	 * disambiguations for the given subject name. If no disambiguations exist
	 * then a single element list containing the subject name is returned.
	 * 
	 * @param subject
	 *            the name of the subject to query
	 * @return a list of disambiguations for the subject; or a single element
	 *         list containing the subject if no disambiguations exist
	 * @throws DBpediaException
	 */
	public Set<String> findDisambiguations(String subject)
			throws DBpediaException {
		Set<String> disambiguations = new HashSet<>();

		String subjectURI = "dbpedia:" + encodeSubjectName(subject);
		String predicateURI = "dbpedia-owl:wikiPageDisambiguates";
		Set<String> disambiguationURIs = selectObjectURIs(subjectURI,
				predicateURI);

		for (String disambiguationURI : disambiguationURIs) {
			String uriPrefix = "http://dbpedia.org/resource/";
			String disambiguation = removeURIPrefix(disambiguationURI,
					uriPrefix);
			disambiguation = decodeSubjectName(disambiguation);
			disambiguations.add(disambiguation);
		}

		// Default case if the subject is not ambiguous
		if (disambiguations.isEmpty()) {
			disambiguations.add(subject);
		}
		return disambiguations;
	}

	/**
	 * Sends a query to the dbpedia ontology service to retrieve a list of
	 * dbpedia instances linked from the instance matching the given subject.
	 * 
	 * @param subject
	 *            the name of the subject to query
	 * @return a list of page links for the subject
	 * @throws DBpediaException
	 */
	public Set<String> findPageLinks(String subject) throws DBpediaException {
		Set<String> pageLinks = new HashSet<>();

		String subjectURI = "dbpedia:" + encodeSubjectName(subject);
		String predicateURI = "dbpedia-owl:wikiPageWikiLink";
		Set<String> pageLinkURIs = selectObjectURIs(subjectURI, predicateURI);

		for (String pageLinkURI : pageLinkURIs) {
			String uriPrefix = "http://dbpedia.org/resource/";
			String pageLink = removeURIPrefix(pageLinkURI, uriPrefix);
			pageLink = decodeSubjectName(pageLink);
			pageLinks.add(pageLink);
		}
		return pageLinks;
	}

	/**
	 * Sends a query to the dbpedia ontology service to retrieve a category list
	 * matching the given subject name.
	 * 
	 * @param subject
	 *            the name of the subject to query
	 * @return a list of categories for the subject
	 * @throws DBpediaException
	 */
	public Set<String> findCategories(String subject) throws DBpediaException {
		Set<String> categories = new HashSet<>();

		String subjectURI = "dbpedia:" + encodeSubjectName(subject);
		String predicateURI = "dcterms:subject";
		Set<String> categoryURIs = selectObjectURIs(subjectURI, predicateURI);

		for (String categoryURI : categoryURIs) {
			String uriPrefix = "http://dbpedia.org/resource/Category:";
			String category = removeURIPrefix(categoryURI, uriPrefix);
			category = decodeSubjectName(category);
			categories.add(category);
		}
		return categories;
	}

	@Override
	public Set<String> expandCategory(String category) throws DBpediaException {
		Set<String> expansions = new HashSet<>(
				findBroaderNarrowerCategories(category));
		expansions.addAll(findRelatedCategories(category));
		return expansions;
	}

	/**
	 * Query for categories with a skos:broader relation to the given category.
	 * 
	 * @param category
	 * @return
	 * @throws DBpediaException
	 */
	public Set<String> findBroaderCategories(String category)
			throws DBpediaException {
		Set<String> broaderCategories = new HashSet<>();

		String subjectURI = "category:" + encodeSubjectName(category);
		String predicateURI = "skos:broader";
		Set<String> categoryURIs = selectObjectURIs(subjectURI, predicateURI);

		for (String categoryURI : categoryURIs) {
			String uriPrefix = "http://dbpedia.org/resource/Category:";
			String broaderCategory = removeURIPrefix(categoryURI, uriPrefix);
			broaderCategory = decodeSubjectName(broaderCategory);
			broaderCategories.add(broaderCategory);
		}
		return broaderCategories;
	}

	/**
	 * Inverse of the broader category relation.
	 * 
	 * @param category
	 * @return
	 * @throws DBpediaException
	 */
	public Set<String> findNarrowerCategories(String category)
			throws DBpediaException {
		Set<String> narrowerCategories = new HashSet<>();

		String subjectURI = "category:" + encodeSubjectName(category);
		String predicateURI = "skos:broader";
		Set<String> categoryURIs = selectSubjectURIs(subjectURI, predicateURI);

		for (String categoryURI : categoryURIs) {
			String uriPrefix = "http://dbpedia.org/resource/Category:";
			String narrowerCategory = removeURIPrefix(categoryURI, uriPrefix);
			narrowerCategory = decodeSubjectName(narrowerCategory);
			narrowerCategories.add(narrowerCategory);
		}
		return narrowerCategories;
	}

	/**
	 * Union of broader and narrower category relations.
	 * 
	 * @param category
	 * @return
	 * @throws DBpediaException
	 */
	public Set<String> findBroaderNarrowerCategories(String category)
			throws DBpediaException {
		Set<String> resultCategories = new HashSet<>();

		String subjectURI = "category:" + encodeSubjectName(category);
		String predicateURI = "skos:broader";
		Set<String> categoryURIs = selectObjectURIsBidirectional(subjectURI,
				predicateURI);

		for (String categoryURI : categoryURIs) {
			String uriPrefix = "http://dbpedia.org/resource/Category:";
			String resultCategory = removeURIPrefix(categoryURI, uriPrefix);
			resultCategory = decodeSubjectName(resultCategory);
			resultCategories.add(resultCategory);
		}
		return resultCategories;
	}

	/**
	 * skos:related category relations.
	 * 
	 * @param category
	 * @return
	 * @throws DBpediaException
	 */
	public Set<String> findRelatedCategories(String category)
			throws DBpediaException {
		Set<String> relatedCategories = new HashSet<>();

		String subjectURI = "category:" + encodeSubjectName(category);
		String predicateURI = "skos:related";
		Set<String> categoryURIs = selectObjectURIsBidirectional(subjectURI,
				predicateURI);

		for (String categoryURI : categoryURIs) {
			String uriPrefix = "http://dbpedia.org/resource/Category:";
			String relatedCategory = removeURIPrefix(categoryURI, uriPrefix);
			relatedCategory = decodeSubjectName(relatedCategory);
			relatedCategories.add(relatedCategory);
		}
		return relatedCategories;
	}

	@Override
	public Set<String> findCategoriesContaining(String regex)
			throws DBpediaException {
		String query = SPARQL_PREFIXES + "SELECT DISTINCT ?object WHERE { "
				+ "[] dcterms:subject ?object . "
				+ "?object rdfs:label ?label . "
				+ "FILTER regex(str(?label), \"(" + regex + ")\", \"i\") "
				+ "} ";

		Set<String> categoryURIs = execSelectQuery(query);
		Set<String> categories = new HashSet<>(categoryURIs.size());

		for (String categoryURI : categoryURIs) {
			String uriPrefix = "http://dbpedia.org/resource/Category:";
			String category = removeURIPrefix(categoryURI, uriPrefix);
			category = decodeSubjectName(category);

			categories.add(category);
		}
		return categories;
	}

	/**
	 * Find the label for the given subjectURI.
	 * 
	 * @param subjectURI
	 * @return
	 * @throws DBpediaException
	 */
	// Presently not used
	public String findLabel(String subjectURI) throws DBpediaException {
		String label = "";

		String predicateURI = "rdfs:label";
		Set<String> labels = selectObjectURIs("<" + subjectURI + ">",
				predicateURI);

		if (labels.iterator().hasNext()) {
			assert (labels.size() == 1);
			label = labels.iterator().next();
		}
		return label;
	}

	/**
	 * @return the URI of the ontology service endpoint to use
	 */
	public String getServiceURI() {
		return this.serviceURI;
	}

	/**
	 * @param serviceURI
	 *            the URI of the ontology service endpoint to use
	 */
	public void setServiceURI(String serviceURI) {
		this.serviceURI = serviceURI;
	}

	/**
	 * Pre-process a given subject name for compatibility with SPARQL queries.
	 * 
	 * @param encoded
	 *            the subject name to pre-process
	 * @return a subject name compatible with SPARQL queries
	 */
	public static String encodeSubjectName(String subject) {
		String encoded = subject;

		// Remove characters known to cause QueryParseExceptions
		if (encoded.startsWith("-") || encoded.startsWith(".")) {
			encoded = encoded.substring(1);
		}
		if (encoded.endsWith(".")) {
			encoded = encoded.substring(0, encoded.length() - 1);
		}

		// Replace spaces with underscores and capitalize first letter
		encoded = encoded.replace(' ', '_');
		encoded = encoded.substring(0, 1).toUpperCase() + encoded.substring(1);

		try {
			encoded = URLEncoder.encode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return encoded;
	}

	public static String decodeSubjectName(String subject) {
		String decoded = subject;
		try {
			decoded = URLDecoder.decode(decoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return decoded;
	}

	/**
	 * Sends a generic SPARQL query to the service URI to retrieve the list of
	 * objects matching the given subject and predicate.
	 * 
	 * @param subjectURI
	 *            the subject of the SPARQL query
	 * @param predicateURI
	 *            the predicate of the SPARQL query
	 * @return the object URIs returned by the SPARQL query
	 * @throws DBpediaException
	 */
	protected Set<String> selectObjectURIs(String subjectURI,
			String predicateURI) throws DBpediaException {
		String query = SPARQL_PREFIXES + "SELECT ?object WHERE { " + subjectURI
				+ " " + predicateURI + " ?object " + " } ";

		return execSelectQuery(query);
	}

	/**
	 * Inverse of selectObjectURIs.
	 * 
	 * @param objectURI
	 * @param predicateURI
	 * @return the subject URIs returned by the SPARQL query
	 * @throws DBpediaException
	 */
	protected Set<String> selectSubjectURIs(String objectURI,
			String predicateURI) throws DBpediaException {
		String query = SPARQL_PREFIXES + "SELECT ?object WHERE { " + "?object "
				+ predicateURI + " " + objectURI + " " + " } ";

		return execSelectQuery(query);
	}

	/**
	 * Union of selectObjectURIs and selectSubjectURIs for querying both sides
	 * of a relation.
	 * 
	 * @param subjectURI
	 * @param predicateURI
	 * @return the subject and object URIs returned by the SPARQL query
	 * @throws DBpediaException
	 */
	protected Set<String> selectObjectURIsBidirectional(String subjectURI,
			String predicateURI) throws DBpediaException {
		String query = SPARQL_PREFIXES + "SELECT ?object WHERE { " + " { "
				+ subjectURI + " " + predicateURI + " ?object } " + " UNION "
				+ " { ?object " + predicateURI + " " + subjectURI + " } "
				+ " } ";

		return execSelectQuery(query);
	}

	protected Set<String> execSelectQuery(String query) throws DBpediaException {
		Set<String> resultURIs = new HashSet<>();

		try (QueryExecution qe = QueryExecutionFactory.sparqlService(
				this.serviceURI, query)) {
			ResultSet results = qe.execSelect();

			while (results.hasNext()) {
				QuerySolution qs = results.next();
				String resultURI = qs.get("?object").toString();
				resultURIs.add(resultURI);
			}
		} catch (QueryExceptionHTTP e) {
			throw new DBpediaException("DBpedia endpoint is unavailable at "
					+ getServiceURI(), e);
		} catch (QueryParseException e) {
			log.error("QueryParseException caused by query: " + query, e);
		}
		return resultURIs;
	}

	/**
	 * Removes the URI prefix from the given object URI.
	 * 
	 * @param objectURI
	 * @param objectURIPrefix
	 * @return the object name result from removing the object URI prefix
	 */
	protected String removeURIPrefix(String objectURI, String objectURIPrefix) {
		String object = objectURI;

		if (objectURI.startsWith(objectURIPrefix)) {
			object = objectURI.substring(objectURIPrefix.length());
		} else {
			log.warn("'" + objectURI + "' is missing the expected prefix "
					+ "'" + objectURIPrefix + "'");
		}
		return object;
	}

}
