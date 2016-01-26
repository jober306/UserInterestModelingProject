package iva.server.test.setup;

import static iva.server.test.fixtures.Fixtures.newTestAnswer;
import static iva.server.test.fixtures.Fixtures.newTestQuestion;
import static iva.server.test.fixtures.Fixtures.newTestUser;

import iva.server.Application;
import iva.server.core.model.Answer;
import iva.server.core.model.InterestModel;
import iva.server.core.model.Question;
import iva.server.core.model.User;
import iva.server.persistence.QuestionRepository;
import iva.server.persistence.UserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TestDatabaseSetUp {

	@Autowired
	protected UserRepository userRepo;
	
	@Autowired
	protected QuestionRepository questionRepo;
	
	protected User testUser;
	protected InterestModel testModel;
	protected Question testQuestion;
	protected Answer testAnswer;
	
	@Before
	public void setUpDatabase() throws Exception {
		User user = userRepo.save(newTestUser());
		
		Question question = newTestQuestion("test question 1");
		question.addAnswer(newTestAnswer("test answer 1a"));
		question.addAnswer(newTestAnswer("test answer 1b"));
		user.addQuestion(question);
		
		question = newTestQuestion("test question 2");
		question.addAnswer(newTestAnswer("test answer 2a"));
		question.addAnswer(newTestAnswer("test answer 2b"));
		user.addQuestion(question);
		
		Iterable<Question> questions = questionRepo.save(user.getQuestionHistory());
		
		testUser = userRepo.save(user);
		testModel = testUser.getInterestModel();
		testQuestion = questions.iterator().next();
		testAnswer = testQuestion.getAnswers().get(0);
	}
	
	@After
	public void tearDownDatabase() throws Exception {
		questionRepo.deleteAll();
		userRepo.deleteAll();
	}

}