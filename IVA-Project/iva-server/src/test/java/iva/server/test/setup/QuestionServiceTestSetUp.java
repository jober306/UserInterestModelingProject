package iva.server.test.setup;

import static iva.server.test.fixtures.Fixtures.newCategoryResponse;
import static iva.server.test.fixtures.Fixtures.newExtraCategoryResponse;
import static iva.server.test.fixtures.Fixtures.newQAResponse;
import static iva.server.test.fixtures.Fixtures.questionString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.when;

import iva.server.Application;
import iva.server.categoryextractor.services.CategoryExtractorService;
import iva.server.core.model.InterestModel;
import iva.server.ephyra.services.EphyraService;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringApplicationConfiguration(classes = {
		Application.class, QuestionServiceTestSetUp.Config.class
})
public class QuestionServiceTestSetUp extends TestDatabaseSetUp {

	@Autowired // configured as @Mock
	private EphyraService qaService;
	
	@Autowired // configured as @Mock
	private CategoryExtractorService categoryService;
	
	@Before
	public void setUpMocks() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		when(qaService.answerQuestion(questionString)).thenReturn(newQAResponse());
		
		when(categoryService.extractCategories(anySetOf(String.class)))
				.thenReturn(newCategoryResponse());
		
		when(categoryService.extractExtraCategories(
				anyMapOf(String.class, Double.class), any(InterestModel.class)))
				.thenReturn(newExtraCategoryResponse());
	}
	
	@Configuration
	public static class Config {
		@Bean
		public EphyraService qaService() {
			return Mockito.mock(EphyraService.class);
		}
		@Bean
		public CategoryExtractorService categoryService() {
			return Mockito.mock(CategoryExtractorService.class);
		}
	}
}