package heliumnife.connector;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

public interface ClientConnector extends Closeable {

	void sendRequest(Serializable request) throws IOException;
	
	void sendRequest(String request) throws IOException;
	
	Serializable waitForResponse() throws IOException;
	
	URI getRemoteURI();
	
	int getTimeout();
	
	void setTimeout(int timeout);
	
	boolean isClosed();

}