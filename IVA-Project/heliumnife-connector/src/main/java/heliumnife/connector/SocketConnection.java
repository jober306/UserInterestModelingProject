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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SocketConnection implements Connection {

	private Socket clientSocket;
	private Serializer serializer;
	
	private BufferedReader in;
	private PrintWriter out;
	
	public SocketConnection(Socket socket, Class<? extends Serializable> requestClass) throws IOException {
		this.clientSocket = socket;
		this.serializer = new GsonSerializer(requestClass);
		
		this.in = new BufferedReader(new InputStreamReader(
				getInputStream(), StandardCharsets.UTF_8));
		this.out = new PrintWriter(new OutputStreamWriter(
				getOutputStream(), StandardCharsets.UTF_8), true);
	}

	public SocketConnection(InetAddress address, int port, Class<? extends Serializable> requestClass) throws IOException {
		this(new Socket(address, port), requestClass);
	}

	public SocketConnection(String host, int port, Class<? extends Serializable> requestClass) throws UnknownHostException, IOException {
		this(new Socket(host, port), requestClass);
	}

	public SocketConnection(InetAddress address, int port, InetAddress localAddr, int localPort, Class<? extends Serializable> requestClass) throws IOException {
		this(new Socket(address, port, localAddr, localPort), requestClass);
	}

	public SocketConnection(String host, int port, InetAddress localAddr, int localPort, Class<? extends Serializable> requestClass) throws IOException {
		this(new Socket(host, port, localAddr, localPort), requestClass);
	}

	@Override
	public Serializable receiveRequest() throws IOException {
		if(isClosed()) {
			throw new IOException("Socket is closed");
		}
		if(isInputShutdown()) {
			throw new IOException("Socket input is shutdown");
		}
		return serializer.deserialize(in.readLine());
	}

	@Override
	public void sendResponse(Serializable response) throws IOException {
		sendResponse(serializer.serialize(response));
	}

	@Override
	public void sendResponse(String response) throws IOException {
		if(isClosed()) {
			throw new IOException("Socket is closed");
		}
		if(isOutputShutdown()) {
			throw new IOException("Socket output is shutdown");
		}
		out.println(response);
	}
	
	public InputStream getInputStream() throws IOException {
		return clientSocket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return clientSocket.getOutputStream();
	}

	public void shutdownInput() throws IOException {
		in.close();
		clientSocket.shutdownInput();
	}

	public void shutdownOutput() throws IOException {
		out.close();
		clientSocket.shutdownOutput();
	}

	public boolean isInputShutdown() {
		return clientSocket.isInputShutdown();
	}

	public boolean isOutputShutdown() {
		return clientSocket.isOutputShutdown();
	}

	@Override
	public URI getURI() {
		if(!clientSocket.isBound()) {
			return null;
		}
		
		InetAddress address = clientSocket.getLocalAddress();
		String host = address.getCanonicalHostName();
		int port = clientSocket.getLocalPort();
		try {
			return new URI("tcp", null, host, port, null, null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Unable to resolve URI from socket address "+address, e);
		}
	}

	@Override
	public boolean isClosed() {
		return clientSocket.isClosed();
	}

	@Override
	public void close() throws IOException {
		out.close();
		in.close();
		clientSocket.close();
	}

	/**
	 * Returns the underlying {@code Socket} of this connection.
	 * @return the delegate socket
	 * @see java.net.Socket
	 */
	public Socket getSocket() {
		return clientSocket;
	}
	
	@Override
	public String toString() {
		return clientSocket.toString();
	}

}
