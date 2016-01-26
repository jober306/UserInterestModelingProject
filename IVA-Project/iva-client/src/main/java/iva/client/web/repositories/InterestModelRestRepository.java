package iva.client.web.repositories;

import iva.client.core.model.InterestModel;
import iva.client.core.model.User;
import iva.client.web.model.CollectionResource;
import iva.client.web.model.wrappers.InterestModelResource;
import iva.client.web.model.wrappers.UserResource;

import java.net.URI;
import java.util.Optional;

import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

public class InterestModelRestRepository extends RestRepository<InterestModel> {

	public InterestModelRestRepository() {
		super(InterestModelResource.class, "interestModel");
	}

	@Override
	protected void processReceivedEntity(InterestModel entity) {
		URI modelId = entity.getId();
		URI userId = findLinkedUser(modelId).getId();
		entity.setOwnerLink(userId);
	}

	public Optional<InterestModel> findByOwnerUsername(String username) {
		return findIdByOwnerUsername(username).map(id -> findOne(id));
	}

	public Optional<URI> findIdByOwnerUsername(String username) {
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				entityUri+"/search/findByOwnerUsername?username={username}", 
				CollectionResource.class, username);
		
		Link link = response.getBody().getLink(entityRel);
		return Optional.ofNullable(link).map(idLink -> URI.create(idLink.getHref()));
	}

	public String mapToCategory(String term) {
		return rest.getForEntity(entityUri+"/mapToCategory?term={term}", 
				String.class, term).getBody();
	}

	protected User findLinkedUser(URI modelId) {
		URI link = URI.create(modelId.toString()+"/owner");
		
		RequestEntity<?> request = RequestEntity.get(link)
				.accept(MediaType.APPLICATION_JSON)
				.build();
		
		ResponseEntity<UserResource> response = 
				rest.exchange(request, UserResource.class);
		
		return response.getBody().getContent();
	}

}
