package iva.server.core.services;

import iva.server.categoryextractor.services.CategoryExtractorService;
import iva.server.core.model.InterestModel;
import iva.server.core.model.Question;
import iva.server.core.model.User;
import iva.server.ephyra.services.EphyraService;
import iva.server.persistence.QuestionRepository;
import iva.server.persistence.UserRepository;
import iva.server.utils.TransactionDebug;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuestionServiceHandler implements QuestionService {

	private EphyraService qaService;
	private CategoryExtractorService categoryService;
	private InterestModelService modelService;
	private UserRepository userRepo;
	private QuestionRepository questionRepo;
	
	@Autowired
	public QuestionServiceHandler(EphyraService qaService, 
			CategoryExtractorService categoryService, 
			InterestModelService modelService, 
			UserRepository userRepo, QuestionRepository questionRepo)
	{
		this.qaService = qaService;
		this.categoryService = categoryService;
		this.modelService = modelService;
		this.userRepo = userRepo;
		this.questionRepo = questionRepo;
	}
	
	@Override
	public Question askQuestion(String question, User user) {
		TransactionDebug.transactionRequired(this.getClass().getName()+"#askQuestion(String, String)");
		
		// Re-attach instance to session
		user = userRepo.save(user);
		InterestModel model = user.getInterestModel();
		
		Question response = askQuestion(question);
		Map<String, Double> categories = response.getCategories();
		
		System.out.println("Querying for extra categories");
		Map<String, Double> extraCategories = categoryService
				.extractExtraCategories(categories, model);
		response.setExtraCategories(extraCategories);
		
		user.addQuestion(response);
		
		Map<String, Double> questionCategories = new HashMap<>(categories);
		extraCategories.forEach((category, score) -> {
			questionCategories.merge(category, score, (s1, s2) -> s1 + s2);
		});
		
		model = modelService.update(questionCategories, model);
		user.setInterestModel(model);
		
		user = userRepo.save(user);
		return questionRepo.save(response);
	}
	
	@Override
	public Question askQuestion(String question) {
		Question response = qaService.answerQuestion(question);
		
		Map<String, Double> categories = categoryService
				.extractCategories(response.getTerms());
		response.setCategories(categories);
		
		return response;
	}

}
