package iva.server.web.controllers;

import static iva.server.test.fixtures.Fixtures.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import iva.client.web.model.CollectionResource;
import iva.client.web.model.wrappers.InterestModelResource;
import iva.client.web.model.wrappers.QuestionResourceList;
import iva.client.web.model.wrappers.UserResource;
import iva.server.core.model.User;
import iva.server.test.setup.TestDatabaseSetUp;

import java.net.URI;
import java.util.Collections;
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
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@WebAppConfiguration
@IntegrationTest
public class UserControllerTest extends TestDatabaseSetUp {

	protected final RestTemplate rest = new TestRestTemplate();
	
	@Test
	public void testGetAll() {
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				"http://localhost:8080/users", CollectionResource.class);
		System.out.println("testGetAll "+response);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().hasLink("user"));
	}
	
	@Test
	public void testGet() {
		ResponseEntity<UserResource> response = rest.getForEntity(
				"http://localhost:8080/users/{id}", UserResource.class, testUser.getId());
		System.out.println("testGet "+response);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(username, response.getBody().getContent().getUsername());
	}

	@Test
	public void testFindByUsername() throws Exception {
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				"http://localhost:8080/users/search/findByUsername?username={username}", 
				CollectionResource.class, username);
		System.out.println("testFindByUsername "+response);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().hasLink("user"));
	}
	
	@Test
	public void testFindByUsernameNotFound() throws Exception {
		ResponseEntity<CollectionResource> response = rest.getForEntity(
				"http://localhost:8080/users/search/findByUsername?username={username}", 
				CollectionResource.class, "idontexist");
		System.out.println("testFindByUsernameNotFound "+response);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertFalse(response.getBody().hasLinks());
	}
	
	@Test
	public void testInterestModelLink() throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<InterestModelResource> response = rest.exchange(
				"http://localhost:8080/users/{id}/interestModel", 
				HttpMethod.GET, request, InterestModelResource.class, testUser.getId());
		System.out.println("testInterestModelLink "+response.getBody().getContent());
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(testUser.getInterestModel().getShortTermCategories(), 
				response.getBody().getContent().getShortTermCategories());
	}
	
	@Test
	public void testQuestionHistoryLink() throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<QuestionResourceList> response = rest.exchange(
				"http://localhost:8080/users/{id}/questionHistory", 
				HttpMethod.GET, request, QuestionResourceList.class, testUser.getId());
		System.out.println("testQuestionHistoryLink "+response);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertThat(response.getBody().toList(), hasSize(testUser.getQuestionHistory().size()));
		assertEquals(testUser.getQuestionHistory().get(0).getQuestion(), 
				response.getBody().toList().get(0).getContent().getQuestion());
	}
	
	@Test
	public void testDeleteQuestionHistory() throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<?> request = new HttpEntity<>(headers);
		
		ResponseEntity<String> response;
		
		response = rest.exchange("http://localhost:8080/users/{id}/questionHistory", 
				HttpMethod.GET, request, String.class, testUser.getId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotEquals("{ }", response.getBody());
		
		// We need to traverse association links to delete questions
		Matcher matcher = Pattern.compile("\"(http://.+/questions/([0-9]+))\"")
				.matcher(response.getBody());
		while(matcher.find()) {
			String link = matcher.group(1);
			rest.delete(link);
		}
		
		response = rest.exchange("http://localhost:8080/users/{id}/questionHistory", 
				HttpMethod.GET, request, String.class, testUser.getId());
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("{ }", response.getBody());
	}
	
	@Test
	public void testPost() throws Exception {
		User newUser = newTestUser();
		newUser.setUsername("newaccount");
		newUser.setFirstName("New");
		newUser.setLastName("Account");
		newUser.setPassword("differentPassword".toCharArray());
		
		HttpEntity<User> request = new HttpEntity<>(newUser);
		
		ResponseEntity<?> response = rest.postForEntity(
				"http://localhost:8080/users", request, Object.class);
		System.out.println("testPost "+response);
		
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		String redirect = response.getHeaders().getLocation().toString();
		assertThat(redirect, startsWith("http://localhost:8080/users/"));
	}
	
	@Test
	public void testPostExistingUsername() throws Exception {
		User newUser = newTestUser();
		newUser.setUsername(username);
		newUser.setFirstName("New");
		newUser.setLastName("Account");
		newUser.setPassword("differentPassword".toCharArray());
		
		HttpEntity<User> request = new HttpEntity<>(newUser);
		
		ResponseEntity<?> response = rest.postForEntity(
				"http://localhost:8080/users", request, Object.class);
		System.out.println("testPostExistingUsername "+response);
		
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
	}
	
	@Test
	public void testPut() throws Exception {
		getLinkedModel(); // TODO
		getLinkedQuestions();
		
		testUser.setFirstName("Edited");
		
		HttpEntity<User> request = new HttpEntity<>(testUser);
		rest.put("http://localhost:8080/users/{id}", request, testUser.getId());
		
		ResponseEntity<UserResource> response = rest.getForEntity(
				"http://localhost:8080/users/{id}", UserResource.class, testUser.getId());
		System.out.println("after testPut "+response.getBody().getContent());
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(testUser.getFirstName(), response.getBody().getContent().getFirstName());
		
		getLinkedModel(); // TODO
		getLinkedQuestions();
	}

	private void getLinkedModel() {
		URI url = new UriTemplate("http://localhost:8080/users/{id}/interestModel").expand(testUser.getId());
		RequestEntity<?> request = RequestEntity.get(url).accept(MediaType.APPLICATION_JSON).build();
		String modelJson = rest.exchange(request, String.class).getBody();
		System.err.println(modelJson);
	}

	private void getLinkedQuestions() {
		URI url = new UriTemplate("http://localhost:8080/users/{id}/questionHistory").expand(testUser.getId());
		RequestEntity<?> request = RequestEntity.get(url).accept(MediaType.APPLICATION_JSON).build();
		String questionsJson = rest.exchange(request, String.class).getBody();
		System.err.println(questionsJson);
	}
	
	@Test
	public void testDelete() throws Exception {
		rest.delete("http://localhost:8080/users/{id}", testUser.getId());
		
		ResponseEntity<String> response = rest.getForEntity(
				"http://localhost:8080/users/{id}", String.class, testUser.getId());
		System.out.println("after testDelete "+response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

}
