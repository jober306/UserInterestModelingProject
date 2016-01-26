package iva.client.web.repositories;

import iva.client.core.model.InterestModel;
import iva.client.core.model.Question;
import iva.client.core.model.User;
import iva.client.web.model.CollectionResource;
import iva.client.web.model.wrappers.InterestModelResource;
import iva.client.web.model.wrappers.QuestionResourceList;
import iva.client.web.model.wrappers.UserResource;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

public class UserRestRepository extends RestRepository<User> {

	public UserRestRepository() {
		super(UserResource.class, "user");
	}

	@Override
	protected void processReceivedEntity(User entity) {
		URI userId = entity.getId();
		
		URI modelId = findLinkedInterestModel(userId).getId();
		entity.setInterestModelLink(modelId);
		
		List<URI> questionIds = findLinkedQuestions(userId).stream()
				.map(question -> question.getId())
				.collect(Collectors.toList());
		entity.setQuestionHistoryLinks(questionIds);
	}

	public Optional<User> findByUsername(String username) {
		return findIdByUsername(username).map(id -> findOne(id));
	}

	public Optional<URI> findIdByUsername(String username) {
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				entityUri+"/search/findByUsername?username={username}", 
				CollectionResource.class, username);
		
		Link link = response.getBody().getLink(entityRel);
		return Optional.ofNullable(link).map(idLink -> URI.create(idLink.getHref()));
	}

	protected InterestModel findLinkedInterestModel(URI userId) {
		URI link = URI.create(userId.toString()+"/interestModel");
		
		RequestEntity<?> request = RequestEntity.get(link)
				.accept(MediaType.APPLICATION_JSON).build();
		
		ResponseEntity<InterestModelResource> response = 
				rest.exchange(request, InterestModelResource.class);
		
		return response.getBody().getContent();
	}

	protected List<Question> findLinkedQuestions(URI userId) {
		URI link = URI.create(userId.toString()+"/questionHistory");
		
		RequestEntity<?> request = RequestEntity.get(link)
				.accept(MediaType.APPLICATION_JSON).build();
		
		ResponseEntity<QuestionResourceList> response = 
				rest.exchange(request, QuestionResourceList.class);
		
		return response.getBody().toList().stream()
				.map(resource -> resource.getContent())
				.collect(Collectors.toList());
	}

}
