package iva.client.web.repositories;

import static iva.server.test.fixtures.Fixtures.username;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import iva.client.core.model.InterestModel;
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
public class UserRestRepositoryTest extends TestDatabaseSetUp {

	private final UserRestRepository restRepo = new UserRestRepository();
	private URI testId;

	@Before
	public void setUp() throws Exception {
		testId = restRepo.findIdByUsername(username).get();
	}

	@Test
	public void testFindByUsername() {
		User user = restRepo.findByUsername(username).get();
		assertIsTestUser(user);
	}

	@Test
	public void testFindLinkedInterestModel() {
		InterestModel model = restRepo.findLinkedInterestModel(testId);
		assertEquals(testModel.getShortTermCategories(), model.getShortTermCategories());
	}

	@Test
	public void testFindLinkedQuestions() {
		List<Question> questions = restRepo.findLinkedQuestions(testId);
		assertEquals(questions.size(), testUser.getQuestionHistory().size());
	}

	@Test
	public void testSave() {
		User user = restRepo.findOne(testId);
		user.setFirstName("Edited");
		
		assertFalse(user.isNew());
		user = restRepo.save(user);
		assertFalse(user.isNew());
		
		assertEquals("Edited", user.getFirstName());
		
		// Test association values
		InterestModel model = new InterestModelRestRepository().findOne(user.getInterestModelLink());
		assertEquals(testUser.getInterestModel().getProperties(), model.getProperties());
		assertEquals(testUser.getInterestModel().getShortTermCategories(), model.getShortTermCategories());
		assertEquals(testUser.getInterestModel().getLongTermCategories(), model.getLongTermCategories());
		
		List<Question> questions = new QuestionRestRepository().findAll(user.getQuestionHistoryLinks());
		assertEquals(testUser.getQuestionHistory().size(), questions.size());
		assertTrue(questions.stream().anyMatch(question -> 
			testQuestion.getQuestion().equals(question.getQuestion())
			&& testQuestion.getCreated().equals(question.getCreated())
			&& testQuestion.getTerms().equals(question.getTerms())
		));
	}

	@Test
	public void testSaveNew() {
		User user = new User("newuser", ("newpass").toCharArray(), "New", "User");
		
		assertTrue(user.isNew());
		user = restRepo.save(user);
		assertFalse(user.isNew());
		
		assertEquals("New", user.getFirstName());
	}

	@Test
	public void testFindOne() {
		User user = restRepo.findOne(testId);
		assertIsTestUser(user);
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
		List<User> users = restRepo.findAll();
		User user = users.get(0);
		assertIsTestUser(user);
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
		User user = restRepo.findOne(testId);
		URI modelId = user.getInterestModelLink();
		List<URI> questionIds = user.getQuestionHistoryLinks();
		
		restRepo.delete(user);
		assertFalse(restRepo.exists(testId));
		
		assertFalse(new InterestModelRestRepository().exists(modelId));
		QuestionRestRepository questionRestRepo = new QuestionRestRepository();
		for(URI questionId : questionIds) {
			assertFalse(questionRestRepo.exists(questionId));
		}
	}

	@Test
	public void testDeleteAll() {
		restRepo.deleteAll();
		assertFalse(restRepo.exists(testId));
		assertEquals(0, restRepo.count());
	}

	private void assertIsTestUser(User user) {
		assertFalse(user.isNew());
		assertEquals(testUser.getUsername(), user.getUsername());
		assertEquals(testUser.getCreated(), user.getCreated());
		assertEquals(testUser.getFirstName(), user.getFirstName());
		assertEquals(testUser.getLastName(), user.getLastName());
		assertEquals(testUser.getQuestionHistory().size(), user.getQuestionHistoryLinks().size());
	}

}
