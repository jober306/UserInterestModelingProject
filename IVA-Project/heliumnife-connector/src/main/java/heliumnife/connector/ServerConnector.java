package heliumnife.connector;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

public interface ServerConnector extends Closeable {

	Connection waitForConnection() throws IOException;

	URI getBoundURI();
	
	int getTimeout();

	void setTimeout(int timeout);

	boolean isClosed();

}