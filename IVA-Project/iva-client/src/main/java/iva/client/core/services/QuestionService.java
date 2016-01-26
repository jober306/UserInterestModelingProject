package iva.client.core.services;

import iva.client.core.model.Question;
import iva.client.core.model.User;

import java.util.List;

public interface QuestionService {

	Question ask(String question, String username);

	List<Question> findByOwner(User owner);

	Question update(Question question);

	void delete(Question question);

	void deleteAllWithOwner(User owner);

}