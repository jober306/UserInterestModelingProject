package iva.server.core.services;

import iva.server.core.model.User;
import iva.server.core.model.Question;

public interface QuestionService {

	Question askQuestion(String question, User account);
	
	Question askQuestion(String question);
	
}
