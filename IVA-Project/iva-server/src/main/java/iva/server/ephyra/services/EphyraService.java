package iva.server.ephyra.services;

import iva.server.core.model.Question;
import iva.server.exceptions.EphyraException;

public interface EphyraService {

	Question answerQuestion(String question) throws EphyraException;

}