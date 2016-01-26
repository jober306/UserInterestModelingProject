package iva.server.categoryextractor.services;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import iva.server.config.CategoryExtractorConfig;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CategoryExtractorConfig.class)
public class DBpediaServiceIntegrationTest {

	private static final String term = "Key_board";
	private static final String redirect = "Keyboard";
	private static final String page = "Computer_keyboard";
	private static final String category = "Computer_keyboards";
	private static final String regex = "keyboard";

	@Autowired
	private DBpediaServiceHandler dbpediaService;
	
	@Before
	public void setUp() {
		assertTrue(dbpediaService.getServiceURI()+" is unavailable", 
				dbpediaService.isServiceAvailable());
	}
	
	@Test
	public void testFindRedirect() {
		assertThat(dbpediaService.findRedirect(term), is(redirect));
	}

	@Test
	public void testFindDisambiguations() {
		Set<String> disambiguations = dbpediaService.findDisambiguations(redirect);
		assertThat(disambiguations, hasItem(page));
	}

	@Test
	public void testFindPageLinks() {
		Set<String> pageLinks = dbpediaService.findPageLinks(page);
		assertThat(pageLinks, is(not(empty())));
	}

	@Test
	public void testFindCategories() {
		Set<String> categories = dbpediaService.findCategories(page);
		assertThat(categories, hasItem(category));
	}

	// This test has a long execution time so consider disabling unless it is needed. 
	//@Test
	public void testFindCategoriesContaining() {
		Set<String> categories = dbpediaService.findCategoriesContaining(regex);
		assertThat(categories, hasItem(category));
	}

}
