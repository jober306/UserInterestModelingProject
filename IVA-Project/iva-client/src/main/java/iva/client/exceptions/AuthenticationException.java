package iva.client.exceptions;

public class AuthenticationException extends Exception {
	private static final long serialVersionUID = 5107956987096062673L;
	
	public AuthenticationException() {
		super("Username or password is incorrect");
	}

	public AuthenticationException(Throwable cause) {
		super("Username or password is incorrect", cause);
	}
	
}