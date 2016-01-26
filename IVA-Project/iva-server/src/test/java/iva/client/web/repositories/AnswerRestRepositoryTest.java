package iva.client.web.repositories;

import static iva.client.web.repositories.RestRepository.getIdAsLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import iva.client.core.model.Answer;
import iva.client.core.model.Question;
import iva.server.test.setup.TestDatabaseSetUp;

import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@IntegrationTest
public class AnswerRestRepositoryTest extends TestDatabaseSetUp {

	private final AnswerRestRepository restRepo = new AnswerRestRepository();
	private URI testId;

	@Before
	public void setUp() throws Exception {
		testId = restRepo.findAllIds().stream()
				.filter(id -> testAnswer.getId().equals(getIdAsLong(id)))
				.findAny().get();
	}

	@Test
	public void testFindByQuestion() {
		URI questionId = new QuestionRestRepository().findAllIds().stream()
				.filter(id -> testQuestion.getId().equals(getIdAsLong(id)))
				.findAny().get();
		
		List<Answer> answers = restRepo.findByQuestion(questionId);
		for(Answer answer : answers) {
			assertFalse(answer.isNew());
			assertNotNull(answer.getSourceQuestionLink());
		}
		assertTrue(answers.stream().anyMatch(answer -> 
			testAnswer.getAnswer().equals(answer.getAnswer())
		));
	}

	@Test
	public void testFindLinkedQuestion() {
		Question question = restRepo.findLinkedQuestion(testId);
		
		assertEquals(testQuestion.getQuestion(), question.getQuestion());
		assertEquals(testQuestion.getCreated(), question.getCreated());
		assertEquals(testQuestion.getTerms(), question.getTerms());
	}

	@Test
	public void testSave() {
		Answer answer = restRepo.findOne(testId);
		answer.setAnswer("Edited");
		
		assertFalse(answer.isNew());
		answer = restRepo.save(answer);
		assertFalse(answer.isNew());
		
		assertEquals("Edited", answer.getAnswer());
	}

	@Test
	public void testSaveNew() {
		Answer answer = new Answer();
		answer.setAnswer("New");
		
		URI questionId = new QuestionRestRepository().findAllIds().get(0);
		answer.setSourceQuestionLink(questionId);
		
		assertTrue(answer.isNew());
		answer = restRepo.save(answer);
		assertFalse(answer.isNew());
		
		assertEquals("New", answer.getAnswer());
	}

	@Test
	public void testFindOne() {
		Answer answer = restRepo.findOne(testId);
		assertFalse(answer.isNew());
		assertEquals(testAnswer.getAnswer(), answer.getAnswer());
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
		List<Answer> answers = restRepo.findAll();
		for(Answer answer : answers) {
			assertFalse(answer.isNew());
		}
		assertTrue(answers.stream().anyMatch(answer -> 
			testAnswer.getAnswer().equals(answer.getAnswer())
		));
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
		Answer answer = restRepo.findOne(testId);
		restRepo.delete(answer);
		assertFalse(restRepo.exists(testId));
	}

	@Test
	public void testDeleteAll() {
		restRepo.deleteAll();
		assertFalse(restRepo.exists(testId));
		assertEquals(0, restRepo.count());
	}

}
