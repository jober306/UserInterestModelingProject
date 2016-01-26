package iva.server.ephyra.services;

import static org.junit.Assert.*;
import iva.server.config.EphyraConfig;
import iva.server.core.model.Answer;
import iva.server.core.model.Question;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * An integration test for the QAService.
 * Requires a running OpenEphyra server. 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EphyraConfig.class)
public class QAServiceIntegrationTest {

	@Autowired
	private EphyraService qaService;
	
	@Test
	public void testAnswerQuestion() {
		String question = "What is a cat?";
		
		Question response = qaService.answerQuestion(question);
		System.out.println(response);
		
		List<Answer> answers = response.getAnswers();
		answers.forEach(System.out::println);
		
		assertEquals(question, response.getQuestion());
	}

}
