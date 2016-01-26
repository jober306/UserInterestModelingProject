package iva.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="DBpedia Unavailable")
public class DBpediaException extends RuntimeException {
	private static final long serialVersionUID = 8539199033007535160L;

	public DBpediaException() {
		super();
	}

	public DBpediaException(String message) {
		super(message);
	}

	public DBpediaException(Throwable cause) {
		super(cause);
	}

	public DBpediaException(String message, Throwable cause) {
		super(message, cause);
	}

}
