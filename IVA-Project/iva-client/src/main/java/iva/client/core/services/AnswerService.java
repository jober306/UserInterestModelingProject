package iva.client.core.services;

import iva.client.core.model.Answer;

public interface AnswerService {

	/**
	 * Performs the use case for rating an answer by modifying the score
	 * of the given result entity and updating its database entry.
	 * The rating string is mapped to a numerical value prior to being 
	 * applied to the result score.
	 * @param answer the result entity being rated
	 * @param rating the rating string of "yes" or "no"
	 * @see #rateAnswer(ResultEntity, int)
	 */
	Answer rateAnswer(Answer answer, String rating);

	/**
	 * Performs the use case for rating an answer by modifying the score
	 * of the given result entity and updating its database entry.
	 * @param answer the answer being rated
	 * @param rating the rating value
	 */
	Answer rateAnswer(Answer answer, int rating);

}