package iva.client.web.repositories;

import static iva.server.test.fixtures.Fixtures.answerString;
import static iva.server.test.fixtures.Fixtures.newCategoryResponse;
import static iva.server.test.fixtures.Fixtures.newExtraCategoryResponse;
import static iva.server.test.fixtures.Fixtures.questionString;
import static iva.server.test.fixtures.Fixtures.username;
import static org.junit.Assert.assertEquals;

import iva.client.core.model.Answer;
import iva.client.core.model.Question;
import iva.server.test.setup.QuestionServiceTestSetUp;

import java.util.List;

import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@IntegrationTest
public class AskQuestionRestRepositoryTest extends QuestionServiceTestSetUp {

	private final QuestionRestRepository restRepo = new QuestionRestRepository();

	@Test
	public void testAsk() {
		Question question = restRepo.ask(questionString, username);
		List<Answer> answers = question.getAnswers();
		
		assertEquals(questionString, question.getQuestion());
		assertEquals(newCategoryResponse(), question.getCategories());
		assertEquals(newExtraCategoryResponse(), question.getExtraCategories());
		assertEquals(answerString, answers.get(0).getAnswer());
	}

}
