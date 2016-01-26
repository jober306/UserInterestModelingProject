/**
 * 
 */
package iva.client.core.services;

import iva.client.core.model.Question;
import iva.client.core.model.User;
import iva.client.web.repositories.QuestionRestRepository;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * This module is responsible for sending user queries to OpenEphyra
 * and converting the results to their matching persistence entities.
 * @author Aron
 */
@Service
public class QuestionServiceHandler implements QuestionService {
	
	private final QuestionRestRepository questionRepo;
	
	public QuestionServiceHandler() {
		this(new QuestionRestRepository());
	}
	
	public QuestionServiceHandler(QuestionRestRepository questionRepo) {
		this.questionRepo = questionRepo;
	}
	
	@Override
	public Question ask(String question, String username) {
		return questionRepo.ask(question, username);
	}

	@Override
	public List<Question> findByOwner(User owner) {
		return questionRepo.findByOwnerUsername(owner.getUsername());
	}
	
	@Override
	public Question update(Question question) {
		if(!question.isNew()) {
			return questionRepo.save(question);
		} else {
			return question;
		}
	}
	
	@Override
	public void delete(Question question) {
		questionRepo.delete(question);
	}
	
	@Override
	public void deleteAllWithOwner(User owner) {
		owner.getQuestionHistoryLinks().clear();
		List<Question> questions = questionRepo.findByOwnerUsername(owner.getUsername());
		questionRepo.delete(questions);
	}
	
}
