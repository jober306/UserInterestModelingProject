package iva.server.core.services;

import static iva.server.test.fixtures.Fixtures.newCategoryResponse;
import static iva.server.test.fixtures.Fixtures.newExtraCategoryResponse;
import static iva.server.test.fixtures.Fixtures.newQAResponse;
import static iva.server.test.fixtures.Fixtures.questionString;
import static iva.server.test.fixtures.Fixtures.username;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import iva.server.core.model.Question;
import iva.server.test.setup.QuestionServiceTestSetUp;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class QuestionServiceTest extends QuestionServiceTestSetUp {

	@Autowired // Mocks injected
	private QuestionService questionService;
	
	@Test
	public void askQuestion() throws Exception {
		Question response = questionService.askQuestion(questionString, testUser);
		
		assertThat(response.getQuestion(), is(questionString));
		assertThat(response.getOwner().getUsername(), is(username));
		assertThat(response.getAnswers(), hasSize(1));
		assertThat(response.getTerms(), is(newQAResponse().getTerms()));
		assertThat(response.getCategories(), is(newCategoryResponse()));
		assertThat(response.getExtraCategories(), is(newExtraCategoryResponse()));
	}
	
	@Test
	public void askQuestionTransient() throws Exception {
		Question response = questionService.askQuestion(questionString);
		
		assertThat(response.getQuestion(), is(questionString));
		assertThat(response.getAnswers(), hasSize(1));
		assertThat(response.getTerms(), is(newQAResponse().getTerms()));
		assertThat(response.getCategories(), is(newCategoryResponse()));
	}

}
