package iva.server.test.fixtures;

import iva.server.core.model.Answer;
import iva.server.core.model.InterestModel;
import iva.server.core.model.Question;
import iva.server.core.model.User;
import iva.server.ephyra.model.AnalyzedQuestion;
import iva.server.ephyra.model.Result;
import iva.server.ephyra.model.Term;

import java.util.HashMap;
import java.util.Map;

public final class Fixtures {

	public static final String questionString = "What is 42?";
	public static final String answerString = "The answer to the ultimate question of life, the universe, and everything.";
	public static final String username = "testuser";
	
	public static User newTestUser() {
		return newTestUser(username);
	}
	
	public static User newTestUser(String username) {
		User user = new User(username, ("password").toCharArray(), "Test", "User");
		InterestModel model = user.getInterestModel();
		
		model.setShortTermAgeRate(10.0);
		model.setLongTermAgeRate(1.0);
		model.setPromotionThreshold(100.0);
		model.setDemotionThreshold(100.0);
		model.setExpirationThreshold(0.0);
		
		Map<String, Double> longTermCategories = model.getLongTermCategories();
		longTermCategories.put("long_term_category", 
				model.getDemotionThreshold() + model.getLongTermAgeRate()*2);
		longTermCategories.put("demoting_category", 
				model.getDemotionThreshold() + model.getLongTermAgeRate());
		
		Map<String, Double> shortTermCategories = model.getShortTermCategories();
		shortTermCategories.put("short_term_category", 
				(model.getExpirationThreshold() + model.getPromotionThreshold())/2);
		shortTermCategories.put("promoting_category", 
				model.getPromotionThreshold() + model.getShortTermAgeRate()*2);
		shortTermCategories.put("expiring_category", 
				model.getExpirationThreshold() + model.getShortTermAgeRate());
		
		return user;
	}

	public static Question newQAResponse() {
		Question question = newTestQuestion(questionString);
		question.addAnswer(newTestAnswer(answerString));
		question.addAnswer(newTestAnswer("2nd "+answerString));
		return question;
	}

	public static Question newTestQuestion(String questionString) {
		AnalyzedQuestion aq = new AnalyzedQuestion(questionString);
		
		String[] split = questionString.split(" ");
		Term[] terms = new Term[split.length];
		for (int i = 0; i < split.length; i++) {
			terms[i] = new Term(split[i]);
		}
		aq.setTerms(terms);
		
		return new Question(aq);
	}

	public static Answer newTestAnswer(String answerString) {
		Result result = new Result(answerString);
		return new Answer(result);
	}

	public static Map<String, Double> newCategoryResponse() {
		Map<String, Double> categories = new HashMap<>();
		categories.put("short_term_category", 10.0);
		categories.put("new_category", 25.0);
		categories.put("new_promoting_category", 200.0);
		categories.put("new_expiring_category", 1.0);
		return categories;
	}

	public static Map<String, Double> newExtraCategoryResponse() {
		Map<String, Double> extraCategories = new HashMap<>();
		extraCategories.put("long_term_category", 10.0);
		return extraCategories;
	}

}
