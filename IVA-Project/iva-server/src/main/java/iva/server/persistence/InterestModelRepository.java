package iva.server.persistence;

import iva.server.core.model.InterestModel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;

public interface InterestModelRepository extends CrudRepository<InterestModel, Long> {
	
	InterestModel findByOwnerUsername(@Param("username") String username);
	
	public static class EventHandler extends AbstractRepositoryEventListener<InterestModel> {
	}
}
