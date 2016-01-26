package iva.client.core.services;

import iva.client.core.model.InterestModel;
import iva.client.core.model.User;
import iva.client.exceptions.UserNotFoundException;
import iva.client.web.repositories.InterestModelRestRepository;

import org.springframework.stereotype.Service;

@Service
public class InterestModelServiceHandler implements InterestModelService {

	private final InterestModelRestRepository modelRepo;

	public InterestModelServiceHandler() {
		this(new InterestModelRestRepository());
	}

	public InterestModelServiceHandler(InterestModelRestRepository modelRepo) {
		this.modelRepo = modelRepo;
	}

	@Override
	public InterestModel findByOwner(User owner) {
		String username = owner.getUsername();
		return modelRepo.findByOwnerUsername(username)
				.orElseThrow(() -> new UserNotFoundException(username));
	}

	@Override
	public InterestModel update(InterestModel model) {
		if(!model.isNew()) {
			return modelRepo.save(model);
		} else {
			return model;
		}
	}

	@Override
	public InterestModel resetProperties(InterestModel model) {
		model.getProperties().clear();
		return modelRepo.save(model);
	}

	@Override
	public InterestModel deleteShortTermCategories(InterestModel model) {
		model.getShortTermCategories().clear();
		return modelRepo.save(model);
	}

	@Override
	public InterestModel deleteLongTermCategories(InterestModel model) {
		model.getLongTermCategories().clear();
		return modelRepo.save(model);
	}

	@Override
	public InterestModel deleteAllCategories(InterestModel model) {
		model.getShortTermCategories().clear();
		model.getLongTermCategories().clear();
		return modelRepo.save(model);
	}

	@Override
	public String mapToCategory(String term) {
		return modelRepo.mapToCategory(term);
	}

}
