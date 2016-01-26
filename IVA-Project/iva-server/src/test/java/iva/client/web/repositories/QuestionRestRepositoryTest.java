package iva.client.web.repositories;

import static iva.client.web.repositories.RestRepository.getIdAsLong;
import static iva.server.test.fixtures.Fixtures.username;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import iva.client.core.model.Answer;
import iva.client.core.model.Question;
import iva.client.core.model.User;
import iva.server.test.setup.TestDatabaseSetUp;

import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@IntegrationTest
public class QuestionRestRepositoryTest extends TestDatabaseSetUp {

	private final QuestionRestRepository restRepo = new QuestionRestRepository();
	private URI testId;

	@Before
	public void setUp() throws Exception {
		testId = restRepo.findAllIds().stream()
				.filter(link -> testQuestion.getId().equals(getIdAsLong(link)))
				.findAny().get();
	}

	@Test
	public void testFindByOwnerUsername() {
		List<Question> questions = restRepo.findByOwnerUsername(username);
		
		assertEquals(questions.size(), testUser.getQuestionHistory().size());
		for(Question question : questions) {
			assertFalse(question.isNew());
			assertFalse(question.getAnswerLinks().isEmpty());
			assertNotNull(question.getOwnerLink());
		}
		assertHasTestQuestion(questions);
	}

	@Test
	public void testFindLinkedAnswers() {
		List<Answer> answers = restRepo.findLinkedAnswers(testId);
		assertEquals(answers.size(), testQuestion.getAnswers().size());
	}

	@Test
	public void testFindLinkedUser() {
		User user = restRepo.findLinkedUser(testId);
		assertEquals(testUser.getUsername(), user.getUsername());
	}

	@Test
	public void testSave() {
		Question question = restRepo.findOne(testId);
		question.setQuestion("Edited");
		
		assertFalse(question.isNew());
		question = restRepo.save(question);
		assertFalse(question.isNew());
		
		assertEquals("Edited", question.getQuestion());
		assertEquals(testQuestion.getAnswers().size(), question.getAnswerLinks().size());
	}

	@Test
	public void testSaveNew() {
		Question question = new Question();
		question.setQuestion("New");
		
		assertTrue(question.isNew());
		question = restRepo.save(question);
		assertFalse(question.isNew());
		
		assertEquals("New", question.getQuestion());
	}

	@Test
	public void testFindOne() {
		Question question = restRepo.findOne(testId);
		assertIsTestQuestion(question);
	}

	@Test
	public void testExists() {
		assertTrue(restRepo.exists(testId));
		
		String idString = testId.toString();
		idString = idString.substring(0, idString.lastIndexOf("/"))+"/0";
		URI nonexistingId = URI.create(idString);
		assertFalse(restRepo.exists(nonexistingId));
	}

	@Test
	public void testFindAll() {
		List<Question> questions = restRepo.findAll();
		for(Question question : questions) {
			assertFalse(question.isNew());
		}
		assertHasTestQuestion(questions);
	}

	@Test
	public void testCount() {
		assertTrue(restRepo.count() > 0);
	}

	@Test
	public void testDeleteId() {
		restRepo.delete(testId);
		assertFalse(restRepo.exists(testId));
	}

	@Test
	public void testDelete() {
		Question question = restRepo.findOne(testId);
		restRepo.delete(question);
		assertFalse(restRepo.exists(testId));
	}

	@Test
	public void testDeleteAll() {
		restRepo.deleteAll();
		assertFalse(restRepo.exists(testId));
		assertEquals(0, restRepo.count());
	}

	private void assertIsTestQuestion(Question question) {
		assertFalse(question.isNew());
		assertEquals(testQuestion.getQuestion(), question.getQuestion());
		assertEquals(testQuestion.getCreated(), question.getCreated());
		assertEquals(testQuestion.getTerms(), question.getTerms());
		
		List<Answer> answers = question.getAnswers();
		assertEquals(answers.size(), testQuestion.getAnswers().size());
		for(int i=0; i < answers.size(); i++) {
			Answer answer = answers.get(i);
			iva.server.core.model.Answer testAnswer = testQuestion.getAnswers().get(i);
			assertFalse(answer.isNew());
			assertEquals(testAnswer.getAnswer(), answer.getAnswer());
		}
	}

	private void assertHasTestQuestion(List<Question> questions) {
		assertTrue(questions.stream().anyMatch(question -> 
			testQuestion.getQuestion().equals(question.getQuestion())
			&& testQuestion.getCreated().equals(question.getCreated())
			&& testQuestion.getTerms().equals(question.getTerms())
		));
	}

}
