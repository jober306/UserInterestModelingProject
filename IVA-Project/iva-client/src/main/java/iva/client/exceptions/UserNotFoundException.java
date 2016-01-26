package iva.client.exceptions;

public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1171783486798598702L;

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
