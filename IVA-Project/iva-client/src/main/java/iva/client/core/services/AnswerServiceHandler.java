/**
 * 
 */
package iva.client.core.services;

import iva.client.core.model.Answer;
import iva.client.web.repositories.AnswerRestRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * The explicit feedback module is responsible for applying user generated 
 * feedback to OpenEphyra results.
 * @author Aron
 */
@Service
public class AnswerServiceHandler implements AnswerService {
	
	private static final Map<String, Integer> ratingMap = new HashMap<>();
	static {
		getRatingMap().put("yes", 1);
		getRatingMap().put("no", -1);
	}
	
	private final AnswerRestRepository answerRepo;
	
	public AnswerServiceHandler() {
		this(new AnswerRestRepository());
	}
	
	public AnswerServiceHandler(AnswerRestRepository answerRepo) {
		this.answerRepo = answerRepo;
	}
	
	@Override
	public Answer rateAnswer(Answer answer, String rating) {
		rating = rating.toLowerCase();
		int numRating = ratingMap.getOrDefault(rating, 0);
		return rateAnswer(answer, numRating);
	}

	@Override
	public Answer rateAnswer(Answer answer, int rating) {
		answer.setRating(rating);
		
		if(!answer.isNew()) {
			return answerRepo.save(answer);
		} else {
			return answer;
		}
	}
	
	public static Map<String, Integer> getRatingMap() {
		return ratingMap;
	}
	
}
