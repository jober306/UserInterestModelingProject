package iva.client.web.repositories;

import iva.client.core.model.Answer;
import iva.client.core.model.Question;
import iva.client.core.model.User;
import iva.client.web.model.CollectionResource;
import iva.client.web.model.wrappers.AnswerResourceList;
import iva.client.web.model.wrappers.QuestionResource;
import iva.client.web.model.wrappers.UserResource;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

public class QuestionRestRepository extends RestRepository<Question> {

	public QuestionRestRepository() {
		super(QuestionResource.class, "question");
	}

	@Override
	protected void processReceivedEntity(Question entity) {
		URI questionId = entity.getId();
		
		List<Answer> answers = findLinkedAnswers(questionId);
		List<URI> answerIds = answers.stream()
				.map(answer -> answer.getId())
				.collect(Collectors.toList());
		
		entity.setAnswerLinks(answerIds);
		
		// Answer entities are included for convenience
		for(Answer answer : answers) {
			answer.setSourceQuestion(entity);
			answer.setSourceQuestionLink(questionId);
		}
		entity.setAnswers(answers);
		
		// Owner relation is optional
		try {
			URI userId = findLinkedUser(questionId).getId();
			entity.setOwnerLink(userId);
		} catch (HttpClientErrorException e) {
			if(!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				throw e;
			}
		}
	}

	public Question ask(String question, String username) {
		URI id = rest.postForLocation(
				entityUri+"/ask?question={question}&username={username}", 
				null, question, username);
		return findOne(id);
	}

	public List<Question> findByOwnerUsername(String username) {
		List<URI> ids = findIdsByOwnerUsername(username);
		return findAll(ids);
	}

	public List<URI> findIdsByOwnerUsername(String username) {
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				entityUri+"/search/findByOwnerUsername?username={username}", 
				CollectionResource.class, username);
		
		return response.getBody().getLinks().stream()
				.map(link -> URI.create(link.getHref()))
				.collect(Collectors.toList());
	}

	protected List<Answer> findLinkedAnswers(URI questionId) {
		URI link = URI.create(questionId.toString()+"/answers");
		
		RequestEntity<?> request = RequestEntity.get(link)
				.accept(MediaType.APPLICATION_JSON).build();
		
		ResponseEntity<AnswerResourceList> response = 
				rest.exchange(request, AnswerResourceList.class);
		
		return response.getBody().toList().stream()
				.map(resource -> resource.getContent())
				.collect(Collectors.toList());
	}

	protected User findLinkedUser(URI questionId) throws HttpClientErrorException {
		URI link = URI.create(questionId.toString()+"/owner");
		
		RequestEntity<?> request = RequestEntity.get(link)
				.accept(MediaType.APPLICATION_JSON).build();
		
		ResponseEntity<UserResource> response = 
				rest.exchange(request, UserResource.class);
		
		return response.getBody().getContent();
	}

}
