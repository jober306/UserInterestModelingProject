package iva.server.web.controllers;

import static iva.server.test.fixtures.Fixtures.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import iva.client.web.model.CollectionResource;
import iva.client.web.model.wrappers.AnswerResourceList;
import iva.client.web.model.wrappers.QuestionResource;
import iva.client.web.model.wrappers.UserResource;
import iva.server.core.model.Answer;
import iva.server.core.model.Question;
import iva.server.test.setup.TestDatabaseSetUp;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@WebAppConfiguration
@IntegrationTest
public class QuestionControllerTest extends TestDatabaseSetUp {

	protected final RestTemplate rest = new TestRestTemplate();
	
	@Test
	public void testGetAll() {
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				"http://localhost:8080/questions", CollectionResource.class);
		System.out.println("testGetAll "+response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().hasLink("question"));
	}

	@Test
	public void testGet() {
		ResponseEntity<QuestionResource> response = rest.getForEntity(
				"http://localhost:8080/questions/{id}", QuestionResource.class, testQuestion.getId());
		System.out.println("testGet "+response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		iva.client.core.model.Question responseQuestion = response.getBody().getContent();
		System.out.println(responseQuestion);
		assertEquals(testQuestion.getQuestion(), responseQuestion.getQuestion());
		assertEquals(testQuestion.getTerms(), responseQuestion.getTerms());
		assertEquals(testQuestion.getCategories(), responseQuestion.getCategories());
	}

	@Test
	public void testFindByOwnerUsername() {
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				"http://localhost:8080/questions/search/findByOwnerUsername?username={username}", 
				CollectionResource.class, username);
		System.out.println("testFindByOwnerUsername "+response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().hasLink("question"));
	}

	@Test
	public void testFindByOwnerUsernameNotFound() {
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				"http://localhost:8080/questions/search/findByOwnerUsername?username={username}", 
				CollectionResource.class, "idontexist");
		System.out.println("testFindByOwnerUsernameNotFound "+response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertFalse(response.getBody().hasLinks());
	}

	@Test
	public void testAnswersLink() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<AnswerResourceList> response = rest.exchange(
				"http://localhost:8080/questions/{id}/answers", 
				HttpMethod.GET, request, AnswerResourceList.class, testQuestion.getId());
		System.out.println("testAnswersLink "+response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertThat(response.getBody().toList(), hasSize(testQuestion.getAnswers().size()));
		// TODO more precise assertion
	}

	@Test
	public void testOwnerLink() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<UserResource> response = rest.exchange(
				"http://localhost:8080/questions/{id}/owner", 
				HttpMethod.GET, request, UserResource.class, testQuestion.getId());
		System.out.println("testOwnerLink "+response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testPost() {
		Question newQuestion = newTestQuestion("new question");
		
		HttpEntity<Question> request = new HttpEntity<>(newQuestion);
		ResponseEntity<?> response = rest.postForEntity(
				"http://localhost:8080/questions", request, Object.class);
		System.out.println("testPost "+response);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		String redirect = response.getHeaders().getLocation().toString();
		assertThat(redirect, startsWith("http://localhost:8080/questions/"));
	}

	@Test
	public void testPut() {
		testQuestion.setQuestion("Edited question");
		testQuestion.setTerms(new HashSet<>(Arrays.asList("edited", "question")));
		
		HttpEntity<Question> request = new HttpEntity<>(testQuestion);
		rest.put("http://localhost:8080/questions/{id}", request, testQuestion.getId());
		
		ResponseEntity<QuestionResource> response = rest.getForEntity(
				"http://localhost:8080/questions/{id}", QuestionResource.class, testQuestion.getId());
		iva.client.core.model.Question responseQuestion = response.getBody().getContent();
		System.out.println("after testPut "+responseQuestion);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(testQuestion.getQuestion(), responseQuestion.getQuestion());
		assertEquals(testQuestion.getTerms(), responseQuestion.getTerms());
	}
	
	@Test
	public void testPutAnswerRating() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<String> response = rest.exchange(
				"http://localhost:8080/questions/{id}/answers", 
				HttpMethod.GET, request, String.class, testQuestion.getId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		Matcher matcher = Pattern.compile("\"(http://.+/answers/([0-9]+))\"").matcher(response.getBody());
		while(matcher.find()) {
			String link = matcher.group(1);
			long id = Long.parseLong(matcher.group(2));
			
			for(Answer answer : testQuestion.getAnswers()) {
				if(answer.getId().equals(id)) {
					answer.setRating(1);
					System.err.println(new HttpEntity<>(answer));
					rest.put(link, new HttpEntity<>(answer));
					break;
				}
			}
		}
		response = rest.exchange(
				"http://localhost:8080/questions/{id}/answers", 
				HttpMethod.GET, request, String.class, testQuestion.getId());
		//System.out.println("after testPutAnswerRating "+response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertThat(response.getBody(), containsString("\"rating\" : 1"));
	}

	@Test
	public void testDelete() {
		rest.delete("http://localhost:8080/questions/{id}", testQuestion.getId());
		
		ResponseEntity<String> response = rest.getForEntity(
				"http://localhost:8080/questions/{id}", String.class, testQuestion.getId());
		System.out.println("after testDelete "+response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

}
