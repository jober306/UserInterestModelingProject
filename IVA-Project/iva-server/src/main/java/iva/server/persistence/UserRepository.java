package iva.server.persistence;

import iva.server.core.model.InterestModel;
import iva.server.core.model.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;

public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByUsername(@Param("username") String username);
	
	User findByInterestModelId(@Param("interestModelID") Long id);
	
	public static class EventHandler extends AbstractRepositoryEventListener<User> {
		@Override
		protected void onBeforeCreate(User user) {
			if (user.getInterestModel() == null) {
				user.setInterestModel(new InterestModel(user));
			}
		}
	}
}
