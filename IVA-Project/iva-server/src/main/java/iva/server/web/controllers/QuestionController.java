package iva.server.web.controllers;

import iva.server.core.model.Question;
import iva.server.core.model.User;
import iva.server.core.services.QuestionService;
import iva.server.exceptions.UserNotFoundException;
import iva.server.persistence.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value="/questions/ask")
public class QuestionController {
	
	private final QuestionService service;
	private final UserRepository userRepo;
	
	@Autowired
	public QuestionController(QuestionService service, UserRepository userRepo) {
		this.service = service;
		this.userRepo = userRepo;
	}

	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> askQuestion(@RequestParam String question, @RequestParam String username) {
		
		User user = userRepo.findByUsername(username);
		if(user == null) throw new UserNotFoundException(username);
		
		Question response = service.askQuestion(question, user);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path("/questions/{id}")
				.buildAndExpand(response.getId())
				.toUri());
		
		return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
	}

	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public Question askQuestion(@RequestParam String question) {
		Question response = service.askQuestion(question);
		return response;
	}
	
}
