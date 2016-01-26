package iva.server.web.controllers;

import static iva.server.test.fixtures.Fixtures.questionString;
import static iva.server.test.fixtures.Fixtures.username;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import iva.server.exceptions.UserNotFoundException;
import iva.server.test.setup.QuestionServiceTestSetUp;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@WebAppConfiguration
@IntegrationTest
public class AskQuestionControllerTest extends QuestionServiceTestSetUp {

	private final RestTemplate rest = new TestRestTemplate();
	
	@Test
	public void testAskQuestion() {
		Map<String, String> params = new HashMap<>();
		params.put("question", questionString);
		params.put("username", username);
		
		ResponseEntity<?> response = rest.postForEntity(
				"http://localhost:8080/questions/ask?question={question}&username={username}", 
				null, Object.class, params);
		System.out.println("testAskQuestion "+response);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		
		String redirect = response.getHeaders().getLocation().toString();
		assertThat(redirect, startsWith("http://localhost:8080/questions/"));
	}
	
	@Test
	public void testGetAskQuestion() {
		ResponseEntity<String> response = rest.getForEntity(
				"http://localhost:8080/questions/ask?question={question}", 
				String.class, questionString);
		System.out.println("testGetAskQuestion "+response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testAskWithMissingParams() {
		ResponseEntity<String> response = rest.postForEntity(
				"http://localhost:8080/questions/ask?question={question}", 
				null, String.class, questionString);
		System.out.println("testAskWithMissingParams "+response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testAskWithUsernameNotFound() {
		Map<String, String> params = new HashMap<>();
		params.put("question", questionString);
		params.put("username", "idontexist");
		
		ResponseEntity<String> response = rest.postForEntity(
				"http://localhost:8080/questions/ask?question={question}&username={username}", 
				null, String.class, params);
		System.out.println("testAskWithUsernameNotFound "+response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertThat(response.getBody(), containsString(UserNotFoundException.class.getName()));
	}

}
