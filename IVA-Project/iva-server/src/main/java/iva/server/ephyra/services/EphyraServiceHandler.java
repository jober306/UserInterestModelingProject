package iva.server.ephyra.services;

import heliumnife.connector.ClientConnector;
import heliumnife.server.ephyra.model.transfer.ResultDTO;
import iva.server.core.model.Answer;
import iva.server.core.model.Question;
import iva.server.ephyra.model.AnalyzedQuestion;
import iva.server.ephyra.model.Query;
import iva.server.ephyra.model.Result;
import iva.server.ephyra.model.Term;
import iva.server.exceptions.EphyraException;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class EphyraServiceHandler implements EphyraService {

	private ClientConnector ephyraConnector;

	public EphyraServiceHandler(ClientConnector ephyraConnector) {
		this.ephyraConnector = ephyraConnector;
	}

	@Override
	public Question answerQuestion(String question) throws EphyraException {
		try {
			question = question.replaceAll("[\\Q\"\\E]", "").trim();

			ephyraConnector.sendRequest(question);
			ResultDTO[] results = (ResultDTO[]) ephyraConnector
					.waitForResponse();

			return convertResults(convertResultDTO(results));
		} catch (IOException e) {
			URI ephyraUri = ephyraConnector.getRemoteURI();
			throw new EphyraException("Unable to query Ephyra at " + ephyraUri,
					e);
		}
	}

	public static Result[] convertResultDTO(ResultDTO[] resultsDTO) {
		Result[] results = new Result[resultsDTO.length];

		for (int i = 0; i < resultsDTO.length; i++) {
			results[i] = new Result(resultsDTO[i].answer);
			Query query = new Query();
			query.setOriginalQueryString(resultsDTO[i].analyzedQuestion.question);
			AnalyzedQuestion aq = new AnalyzedQuestion(
					resultsDTO[i].analyzedQuestion.question);
			Term[] terms = new Term[resultsDTO[i].analyzedQuestion.terms.length];
			for (int j = 0; j < terms.length; j++) {
				terms[j] = new Term(
						resultsDTO[i].analyzedQuestion.terms[j].text);
			}
			aq.setTerms(terms);
			query.setAnalyzedQuestion(aq);
			results[i].setQuery(query);
		}
		return results;
	}

	public static Question convertResults(Result[] results) {
		/*
		 * The OpenEphyra server is expected to return a single result with a
		 * null answer if no answers are found. An empty array is an error.
		 */
		if (results.length == 0) {
			return new Question(new AnalyzedQuestion());
		}

		AnalyzedQuestion aq = results[0].getQuery().getAnalyzedQuestion();
		Question question = new Question(aq);

		if (results[0].getAnswer() != null) {
			List<Answer> answers = new ArrayList<>(results.length);
			for (Result result : results) {
				Answer answer = new Answer(result);
				answer.setSourceQuestion(question);
				answers.add(answer);
			}
			question.setAnswers(answers);
		}

		return question;
	}
}
