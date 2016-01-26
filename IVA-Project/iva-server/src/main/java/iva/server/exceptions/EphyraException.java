package iva.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="Ephyra Error")
public class EphyraException extends RuntimeException {
	private static final long serialVersionUID = -5762542188430724212L;

	public EphyraException() {
		super();
	}

	public EphyraException(String message) {
		super(message);
	}

	public EphyraException(Throwable cause) {
		super(cause);
	}

	public EphyraException(String message, Throwable cause) {
		super(message, cause);
	}

}
