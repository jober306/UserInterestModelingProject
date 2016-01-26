package iva.server.persistence;

import iva.server.core.model.Answer;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;

public interface AnswerRepository extends CrudRepository<Answer, Long> {
	
	List<Answer> findBySourceQuestionId(@Param("questionID") Long questionID);
	
	public static class EventHandler extends AbstractRepositoryEventListener<Answer> {
	}
}
