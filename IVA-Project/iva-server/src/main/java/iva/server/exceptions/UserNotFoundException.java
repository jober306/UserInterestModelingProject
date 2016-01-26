package iva.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="User does not exist")
public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 6635878670448028230L;

	public UserNotFoundException() {
		super();
	}
	
	public UserNotFoundException(Long userId) {
		this(userId.toString());
	}
	
	public UserNotFoundException(String username) {
		super("Unable to find user '"+username+"'.");
	}
	
	public UserNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public UserNotFoundException(Long userId, Throwable cause) {
		this(userId.toString(), cause);
	}
	
	public UserNotFoundException(String username, Throwable cause) {
		super("Unable to find user '"+username+"'.", cause);
	}

}
