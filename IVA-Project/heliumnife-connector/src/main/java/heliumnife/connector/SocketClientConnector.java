package heliumnife.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SocketClientConnector implements ClientConnector {

	private Socket socket;
	private Serializer serializer;
	
	private PrintWriter out;
	private BufferedReader in;
	
	protected SocketClientConnector(Socket socket, Class<? extends Serializable> responseClass) throws IOException {
		this.socket = socket;
		this.serializer = new GsonSerializer(responseClass);
		
		this.out = new PrintWriter(new OutputStreamWriter(
				getOutputStream(), StandardCharsets.UTF_8), true);
		this.in = new BufferedReader(new InputStreamReader(
				getInputStream(), StandardCharsets.UTF_8));
	}
	
	public SocketClientConnector(String host, int port, Class<? extends Serializable> responseClass) throws UnknownHostException, IOException {
		this(new Socket(host, port), responseClass);
	}

	public SocketClientConnector(InetAddress address, int port, Class<? extends Serializable> responseClass) throws IOException {
		this(new Socket(address, port), responseClass);
	}

	public SocketClientConnector(String host, int port, InetAddress localAddr, int localPort, Class<? extends Serializable> responseClass) throws IOException {
		this(new Socket(host, port, localAddr, localPort), responseClass);
	}

	public SocketClientConnector(InetAddress address, int port, InetAddress localAddr, int localPort, Class<? extends Serializable> responseClass) throws IOException {
		this(new Socket(address, port, localAddr, localPort), responseClass);
	}

	@Override
	public void sendRequest(Serializable request) throws IOException {
		sendRequest(serializer.serialize(request));
	}

	@Override
	public void sendRequest(String request) throws IOException {
		if(isClosed()) {
			throw new IOException("Socket is closed");
		}
		if(isOutputShutdown()) {
			throw new IOException("Socket output is shutdown");
		}
		out.println(request);
	}

	@Override
	public Serializable waitForResponse() throws IOException {
		if(isClosed()) {
			throw new IOException("Socket is closed");
		}
		if(isInputShutdown()) {
			throw new IOException("Socket input is shutdown");
		}
		return serializer.deserialize(in.readLine());
	}

	/**
	 * @see java.net.Socket#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	/**
	 * @see java.net.Socket#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	/**
	 * @see java.net.Socket#shutdownOutput()
	 */
	public void shutdownOutput() throws IOException {
		out.close();
		socket.shutdownOutput();
	}

	/**
	 * @see java.net.Socket#shutdownInput()
	 */
	public void shutdownInput() throws IOException {
		in.close();
		socket.shutdownInput();
	}

	/**
	 * @see java.net.Socket#isOutputShutdown()
	 */
	public boolean isOutputShutdown() {
		return socket.isOutputShutdown();
	}

	/**
	 * @see java.net.Socket#isInputShutdown()
	 */
	public boolean isInputShutdown() {
		return socket.isInputShutdown();
	}

	@Override
	public URI getRemoteURI() {
		if(!isConnected()) {
			return null;
		}
		
		InetAddress address = getInetAddress();
		String host = address.getCanonicalHostName();
		int port = getPort();
		try {
			return new URI("tcp", null, host, port, null, null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Unable to resolve URI from socket address "+address, e);
		}
	}

	/**
	 * @see java.net.Socket#connect(java.net.SocketAddress)
	 */
	public void connect(SocketAddress endpoint) throws IOException {
		socket.connect(endpoint);
	}

	/**
	 * @see java.net.Socket#connect(java.net.SocketAddress, int)
	 */
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		socket.connect(endpoint, timeout);
	}

	/**
	 * @see java.net.Socket#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	/**
	 * @see java.net.Socket#getRemoteSocketAddress()
	 */
	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}

	/**
	 * @see java.net.Socket#getPort()
	 */
	public int getPort() {
		return socket.getPort();
	}

	/**
	 * @see java.net.Socket#isConnected()
	 */
	public boolean isConnected() {
		return socket.isConnected();
	}

	public URI getLocalURI() {
		if(!isBound()) {
			return null;
		}
		
		InetAddress address = getLocalAddress();
		String host = address.getCanonicalHostName();
		int port = getLocalPort();
		try {
			return new URI("tcp", null, host, port, null, null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Unable to resolve URI from socket address "+address, e);
		}
	}
	
	/**
	 * @see java.net.Socket#bind(java.net.SocketAddress)
	 */
	public void bind(SocketAddress bindpoint) throws IOException {
		socket.bind(bindpoint);
	}

	/**
	 * @see java.net.Socket#getLocalAddress()
	 */
	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}

	/**
	 * @see java.net.Socket#getLocalSocketAddress()
	 */
	public SocketAddress getLocalSocketAddress() {
		return socket.getLocalSocketAddress();
	}

	/**
	 * @see java.net.Socket#getLocalPort()
	 */
	public int getLocalPort() {
		return socket.getLocalPort();
	}

	/**
	 * @see java.net.Socket#isBound()
	 */
	public boolean isBound() {
		return socket.isBound();
	}

	/**
	 * @see java.net.Socket#getSoTimeout()
	 */
	@Override
	public int getTimeout() {
		try {
			return socket.getSoTimeout();
		} catch (SocketException e) {
			throw new RuntimeException("Failed to get socket timeout", e);
		}
	}

	/**
	 * @see java.net.Socket#setSoTimeout(int)
	 */
	@Override
	public void setTimeout(int timeout) {
		try {
			socket.setSoTimeout(timeout);
		} catch (SocketException e) {
			throw new RuntimeException("Failed to set socket timeout", e);
		}
	}

	/**
	 * @see java.net.Socket#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}

	/**
	 * @see java.net.Socket#close()
	 */
	@Override
	public void close() throws IOException {
		out.close();
		in.close();
		socket.close();
	}

	/**
	 * Returns the underlying {@code Socket} of this connector.
	 * @return the delegate socket
	 * @see java.net.Socket
	 */
	protected Socket getSocket() {
		return socket;
	}
	
	/**
	 * Converts this connector to a {@code String} using the {@code toString} 
	 * method of the underlying socket.
	 * @see java.net.Socket#toString()
	 */
	@Override
	public String toString() {
		return socket.toString();
	}

}
