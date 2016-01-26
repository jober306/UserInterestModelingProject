package heliumnife.connector;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

public interface Connection extends Closeable {

	Serializable receiveRequest() throws IOException;
	
	void sendResponse(Serializable response) throws IOException;

	void sendResponse(String response) throws IOException;
	
	URI getURI();

	boolean isClosed();

}