package iva.client.core.services;

import iva.client.core.model.InterestModel;
import iva.client.core.model.User;

public interface InterestModelService {

	InterestModel findByOwner(User owner);

	InterestModel update(InterestModel model);

	InterestModel resetProperties(InterestModel model);

	InterestModel deleteShortTermCategories(InterestModel model);

	InterestModel deleteLongTermCategories(InterestModel model);

	InterestModel deleteAllCategories(InterestModel model);

	String mapToCategory(String term);

}