package iva.server.test.runner;

import iva.server.Application;
import iva.server.core.model.InterestModel;
import iva.server.core.model.Question;
import iva.server.core.model.User;
import iva.server.core.services.QuestionService;
import iva.server.persistence.UserRepository;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class,
		TestQuestionsRunner.Config.class })
public class TestQuestionsRunner {

	private static List<String> questionList;
	private final static String MAIN_QUESTION_TEST_SET = "test questions.txt";
	private final static String ASTROPHYSIC_QUESTION_TEST_SET = "questionAstrophysic.txt";
	private final static String RELATIONUS_QUESTION_TEST_SET = "questionEnRelationUS.txt";
	private final static String MOVIETVSHOW_QUESTION_TEST_SET = "questionMovieTVshow.txt";

	@Autowired
	private QuestionService service;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ReportMaker report;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		URI fileLocation = TestQuestionsRunner.class.getClassLoader()
				.getResource(ASTROPHYSIC_QUESTION_TEST_SET).toURI();

		questionList = Files.lines(Paths.get(fileLocation))
				.map(line -> line.trim()).filter(line -> !line.isEmpty())
				.collect(Collectors.toList());
	}

	@Before
	public void setUp() throws Exception {
		User testUser = new User("testuser", ("password").toCharArray(),
				"Test", "User");
		InterestModel model = testUser.getInterestModel();

		// TODO set model properties
		model.setShortTermAgeRate(1.5);

		// TODO add long term categories to user
		Map<String, Double> longTermCategories = model.getLongTermCategories();
		longTermCategories
				.put("World_War_II_bombers_of_the_United_States", 1.0);
		longTermCategories.put("Bomber_aircraft", 1.0);
		longTermCategories.put("Explosive_weapons", 1.0);
		longTermCategories.put("Royal_Navy_ship_names", 1.0);
		longTermCategories.put("Artillery_operation", 1.0);
		longTermCategories.put("United_States_Navy_ship_names", 1.0);
		longTermCategories.put("Rocketry", 1.0);
		longTermCategories.put("American_military_aviation", 1.0);
		longTermCategories.put("Guided_missiles", 1.0);
		longTermCategories.put("Ships", 1.0);
		longTermCategories.put("Ships_of_the_United_States_Navy", 1.0);
		longTermCategories.put("Aircraft", 1.0);

		testUser = userRepo.save(testUser);
		report.init(testUser);
	}

	@After
	public void tearDown() throws Exception {
		userRepo.deleteAll();
	}

	@Test
	public void askQuestionsList() throws Exception {
		try (ReportMaker report = this.report) {
			for (int i = 0; i < questionList.size(); i++) {
				String question = questionList.get(i);
				System.out.println((i + 1) + ". Asking: " + question);

				User testUser = userRepo.findByUsername("testuser");
				Question response = service.askQuestion(question, testUser);
				report.append(i + 1, response);
			}
		}
	}

	@Configuration
	public static class Config {
		@Bean
		public ReportMaker report() {
			return new ReportMakerHandler();
		}
	}

}
