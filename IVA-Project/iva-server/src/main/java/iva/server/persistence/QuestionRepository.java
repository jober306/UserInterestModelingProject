package iva.server.persistence;

import iva.server.core.model.Question;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;

public interface QuestionRepository extends CrudRepository<Question, Long> {
	
	List<Question> findByOwnerUsername(@Param("username") String username);
	
	public static class EventHandler extends AbstractRepositoryEventListener<Question> {
	}
}
