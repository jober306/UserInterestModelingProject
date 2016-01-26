package iva.server.core.services;

import iva.server.core.model.InterestModel;
import iva.server.persistence.InterestModelRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InterestModelServiceHandler implements InterestModelService {

	private InterestModelRepository modelRepo;
	
	@Autowired
	public InterestModelServiceHandler(InterestModelRepository modelRepo) {
		this.modelRepo = modelRepo;
	}

	@Override
	public InterestModel update(Map<String, Double> newCategories, InterestModel model) {
		// Re-attach instance to session
		model = modelRepo.save(model);
		
		Map<String, Double> shortTermCategories = model.getShortTermCategories();
		Map<String, Double> longTermCategories = model.getLongTermCategories();
		
		double shortTermAgeRate = model.getShortTermAgeRate();
		double longTermAgeRate = model.getLongTermAgeRate();
		
		double promotionThreshold = model.getPromotionThreshold();
		double demotionThreshold = model.getDemotionThreshold();
		double expirationThreshold = model.getExpirationThreshold();
		
		// Add new categories to model
		newCategories.forEach((category, score) -> {
			if (longTermCategories.containsKey(category)) {
				longTermCategories.merge(category, score, (s1, s2) -> s1 + s2);
			}
			else {
				score += shortTermCategories.getOrDefault(category, 0.0);
				
				if(promotionThreshold >= 0 && score >= promotionThreshold) {
					// Promote existing short term category
					shortTermCategories.remove(category);
					longTermCategories.put(category, score);
				} else {
					shortTermCategories.put(category, score);
				}
			}
		});
		
		// Age categories
		longTermCategories.replaceAll((k, score) -> score - longTermAgeRate);
		shortTermCategories.replaceAll((k, score) -> score - shortTermAgeRate);
		
		// Demote long term categories
		new HashMap<>(longTermCategories).forEach((category, score) -> {
			if(score < demotionThreshold) {
				longTermCategories.remove(category);
				shortTermCategories.put(category, score);
			}
		});
		
		// Expire short term categories
		new HashMap<>(shortTermCategories).forEach((category, score) -> {
			if(score < expirationThreshold) {
				shortTermCategories.remove(category);
			}
		});
		
		return modelRepo.save(model);
	}

}
