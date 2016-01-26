package iva.client.web.repositories;

import iva.client.core.model.Answer;
import iva.client.core.model.Question;
import iva.client.web.model.CollectionResource;
import iva.client.web.model.wrappers.AnswerResource;
import iva.client.web.model.wrappers.QuestionResource;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

public class AnswerRestRepository extends RestRepository<Answer> {

	public AnswerRestRepository() {
		super(AnswerResource.class, "answer");
	}

	@Override
	protected void processReceivedEntity(Answer entity) {
		URI answerId = entity.getId();
		URI questionId = findLinkedQuestion(answerId).getId();
		entity.setSourceQuestionLink(questionId);
	}

	public List<Answer> findByQuestion(URI questionId) {
		List<URI> ids = findIdsByQuestion(questionId);
		return findAll(ids);
	}

	public List<URI> findIdsByQuestion(URI questionId) {
		long idValue = getIdAsLong(questionId);
		
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				entityUri+"/search/findBySourceQuestionId?questionID={id}", 
				CollectionResource.class, idValue);
		
		return response.getBody().getLinks().stream()
				.map(link -> URI.create(link.getHref()))
				.collect(Collectors.toList());
	}

	protected Question findLinkedQuestion(URI answerId) {
		URI link = URI.create(answerId.toString()+"/sourceQuestion");
		
		RequestEntity<?> request = RequestEntity.get(link)
				.accept(MediaType.APPLICATION_JSON)
				.build();
		
		ResponseEntity<QuestionResource> response = 
				rest.exchange(request, QuestionResource.class);
		
		return response.getBody().getContent();
	}

}
