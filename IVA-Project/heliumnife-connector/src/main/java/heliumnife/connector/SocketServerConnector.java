package heliumnife.connector;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;

public class SocketServerConnector implements ServerConnector {

	private ServerSocket serverSocket;
	private Class<? extends Serializable> requestClass;
	
	protected SocketServerConnector(ServerSocket socket, Class<? extends Serializable> requestClass) {
		this.serverSocket = socket;
		this.requestClass = requestClass;
	}

	public SocketServerConnector(int port, Class<? extends Serializable> requestClass) throws IOException {
		this(new ServerSocket(port), requestClass);
	}
	
	public SocketServerConnector(int port, int backlog, Class<? extends Serializable> requestClass) throws IOException {
		this(new ServerSocket(port, backlog), requestClass);
	}

	public SocketServerConnector(int port, int backlog, InetAddress bindAddr, Class<? extends Serializable> requestClass) throws IOException {
		this(new ServerSocket(port, backlog, bindAddr), requestClass);
	}

	@Override
	public Connection waitForConnection() throws IOException {
		return new SocketConnection(serverSocket.accept(), requestClass);
	}

	@Override
	public URI getBoundURI() {
		if(!isBound()) {
			return null;
		}
		
		InetAddress address = getInetAddress();
		String host = address.getCanonicalHostName();
		int port = getLocalPort();
		try {
			return new URI("tcp", null, host, port, null, null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Unable to resolve URI from socket address "+address, e);
		}
	}

	public void bind(SocketAddress endpoint, int backlog) throws IOException {
		serverSocket.bind(endpoint, backlog);
	}

	public InetAddress getInetAddress() {
		return serverSocket.getInetAddress();
	}

	public int getLocalPort() {
		return serverSocket.getLocalPort();
	}

	public SocketAddress getLocalSocketAddress() {
		return serverSocket.getLocalSocketAddress();
	}

	public boolean isBound() {
		return serverSocket.isBound();
	}

	@Override
	public int getTimeout() {
		try {
			return serverSocket.getSoTimeout();
		} catch (IOException e) {
			throw new RuntimeException("Failed to get socket timeout", e);
		}
	}

	@Override
	public void setTimeout(int timeout) {
		try {
			serverSocket.setSoTimeout(timeout);
		} catch (SocketException e) {
			throw new RuntimeException("Failed to set socket timeout", e);
		}
	}

	@Override
	public boolean isClosed() {
		return serverSocket.isClosed();
	}

	@Override
	public void close() throws IOException {
		serverSocket.close();
	}

	/**
	 * Returns the underlying {@code ServerSocket} of this connector.
	 * @return the delegate socket
	 * @see java.net.ServerSocket
	 */
	protected ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	@Override
	public String toString() {
		return serverSocket.toString();
	}
	
}
