package iva.server.config;

import iva.server.categoryextractor.services.CategoryExtractorService;
import iva.server.categoryextractor.services.CategoryExtractorServiceHandler;
import iva.server.categoryextractor.services.CategoryMapperService;
import iva.server.categoryextractor.services.CategoryMapperServiceHandler;
import iva.server.categoryextractor.services.DBpediaService;
import iva.server.categoryextractor.services.DBpediaServiceHandler;
import iva.server.categoryextractor.services.DisambiguationFilter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("iva.server.categoryextractor")
public class CategoryExtractorConfig {

	@Value("${iva.categoryextractor.idf-resource:CategoryIDFs.txt}")
	private String idfResource;

	@Value("${iva.categoryextractor.idf-threshold:0.0}")
	private Double idfThreshold;

	@Value("${iva.categoryextractor.disambiguation.page-factor:100.0}")
	private Double pageFactor;

	@Value("${iva.categoryextractor.disambiguation.link-factor:10.0}")
	private Double linkFactor;

	@Value("${iva.categoryextractor.disambiguation.category-factor:1.0}")
	private Double categoryFactor;

	@Value("${iva.dbpedia.url:http://dbpedia.org/sparql}")
	private String dbpediaUrl;

	@Bean
	public CategoryExtractorService categoryService() throws IOException {
		Map<String, Double> tokenIdfs = getCategoryIDFs(idfResource);
		Double defaultIdf = Collections.max(tokenIdfs.values());

		return new CategoryExtractorServiceHandler(dbpediaService(),
				disambiguationFilter(), tokenIdfs, defaultIdf, idfThreshold);
	}

	@Bean
	public CategoryMapperService categoryMapperService() {
		return new CategoryMapperServiceHandler(dbpediaService());
	}

	@Bean
	public DBpediaService dbpediaService() {
		return new DBpediaServiceHandler();
	}

	@Bean
	public DisambiguationFilter disambiguationFilter() {
		return new DisambiguationFilter(pageFactor, linkFactor, categoryFactor);
	}

	private static Map<String, Double> getCategoryIDFs(String resourceName)
			throws IOException {
		URL resource = CategoryExtractorServiceHandler.class.getClassLoader()
				.getResource(resourceName);

		if (resource != null) {
			try {
				return getCategoryIDFs(Paths.get(resource.toURI()));
			} catch (URISyntaxException e) {
				throw new RuntimeException("Unable to get URI for "
						+ resource.toString(), e);
			}
		} else {
			throw new IOException("IDF resource not found: " + resourceName);
		}
	}

	private static Map<String, Double> getCategoryIDFs(Path idfFile)
			throws IOException {
		return Files
				.lines(idfFile, StandardCharsets.UTF_8)
				.map(line -> line.toLowerCase().split(":"))
				.filter(kv -> kv.length == 2)
				.collect(
						Collectors.toMap(kv -> kv[0],
								kv -> Double.valueOf(kv[1]),
								(idf1, idf2) -> Math.min(idf1, idf2)));
	}

}
