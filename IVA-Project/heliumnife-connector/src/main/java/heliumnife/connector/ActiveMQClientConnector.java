package heliumnife.connector;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQClientConnector implements ClientConnector {

	public static final URI DEFAULT_BROKER_URL;
	static {
		final String uri = "tcp://localhost:61616";
		try {
			DEFAULT_BROKER_URL = new URI(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException("URI syntax error in "+uri, e);
		}
	}
	
	private URI brokerUrl;
	private int timeout = 0;
	private boolean closed = false;
	
	private Connection connection;
	private Session session;
	private Queue requestQueue;
	private TemporaryQueue responseQueue;
	private MessageProducer requestProducer;
	private MessageConsumer responseConsumer;
	
	/**
	 * Creates an ActiveMQ connector for the given queue name on the default
	 * broker url (tcp://localhost:61616).
	 * @param requestQueueName name of the queue for sending messages
	 * @param responseClass 
	 * @throws JMSException
	 */
	public ActiveMQClientConnector(String requestQueueName, Class<? extends Serializable> responseClass) throws JMSException {
		this(requestQueueName, DEFAULT_BROKER_URL, responseClass);
	}
	
	public ActiveMQClientConnector(String requestQueueName, URI brokerUrl, Class<? extends Serializable> responseClass) throws JMSException {
		this.brokerUrl = brokerUrl;
		
		ActiveMQConnectionFactory connectionFactory = 
				new ActiveMQConnectionFactory(brokerUrl);
		connectionFactory.setTransformer(
				new GsonSerializer(responseClass));
		
		this.connection = connectionFactory.createConnection();
		this.connection.start();

		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		this.requestQueue = session.createQueue(requestQueueName);
		this.responseQueue = session.createTemporaryQueue();
		
		this.requestProducer = session.createProducer(requestQueue);
		this.requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		this.responseConsumer = session.createConsumer(responseQueue);
	}

	@Override
	public void sendRequest(Serializable request) throws IOException {
		if(isClosed()) {
			throw new IOException("Connector is closed");
		}
		try {
			ObjectMessage requestMessage = session.createObjectMessage(request);
			requestMessage.setJMSReplyTo(responseQueue);
			requestMessage.setJMSCorrelationID(newJMSCorrelationID());
			requestProducer.send(requestMessage);
		} catch (JMSException e) {
			throw new IOException("Failed to send ObjectMssage", e);
		}
	}

	@Override
	public void sendRequest(String request) throws IOException {
		if(isClosed()) {
			throw new IOException("Connector is closed");
		}
		try {
			TextMessage requestMessage = session.createTextMessage(request);
			requestMessage.setJMSReplyTo(responseQueue);
			requestMessage.setJMSCorrelationID(newJMSCorrelationID());
			requestProducer.send(requestMessage);
		} catch (JMSException e) {
			throw new IOException("Failed to send TextMessage", e);
		}
	}

	public void sendRequest(byte[] request) throws IOException {
		if(isClosed()) {
			throw new IOException("Connector is closed");
		}
		try {
			BytesMessage requestMessage = session.createBytesMessage();
			requestMessage.writeBytes(request);
			requestMessage.setJMSReplyTo(responseQueue);
			requestMessage.setJMSCorrelationID(newJMSCorrelationID());
			requestProducer.send(requestMessage);
		} catch (JMSException e) {
			throw new IOException("Failed to send BytesMessage", e);
		}
	}

	private static String newJMSCorrelationID() {
		return Long.toHexString(new Random(System.currentTimeMillis()).nextLong());
	}

	@Override
	public Serializable waitForResponse() throws IOException {
		if(isClosed()) throw new IOException("Connector is closed");
		
		Message message;
		try {
			message = responseConsumer.receive(getTimeout());
			if(message == null) {
				if(isClosed())
					throw new JMSException("MessageConsumer closed while waiting for message");
				else
					throw new IOException("MessageConsumer timeout exceeded");
			}
		} catch (JMSException e) {
			throw new IOException("Failed to receive response Message", e);
		}
		try {
			return extractResponse(message);
		} catch (JMSException e) {
			throw new IOException("Failed to read response Message", e);
		}
	}

	private static Serializable extractResponse(Message message) throws JMSException {
		if(message instanceof ObjectMessage) {
			return ((ObjectMessage) message).getObject();
		}
		else if(message instanceof TextMessage) {
			return ((TextMessage) message).getText();
		}
		else if(message instanceof BytesMessage) {
			BytesMessage bytesMessage = (BytesMessage) message;
			int length = bytesMessage.readInt();
			byte[] response = new byte[length];
			bytesMessage.readBytes(response);
			return response;
		}
		else {
			throw new JMSException("Message type not found "+message.getClass());
		}
	}

	@Override
	public URI getRemoteURI() {
		return brokerUrl;
	}

	@Override
	public int getTimeout() {
		return timeout;
	}

	@Override
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() throws IOException {
		try {
			requestProducer.close();
		} catch (JMSException e) {
			throw new IOException("Failed to close request producer", e);
		}
		try {
			responseConsumer.close();
		} catch (JMSException e) {
			throw new IOException("Failed to close response consumer", e);
		}
		try {
			session.close();
		} catch (JMSException e) {
			throw new IOException("Failed to close JMS session", e);
		}
		try {
			connection.close();
		} catch (JMSException e) {
			throw new IOException("Failed to close JMS connection", e);
		}
		closed = true;
	}

	public String getRequestQueueName() {
		try {
			return requestQueue.getQueueName();
		} catch (JMSException e) {
			throw new RuntimeException("Failed to get request queue name", e);
		}
	}

}
