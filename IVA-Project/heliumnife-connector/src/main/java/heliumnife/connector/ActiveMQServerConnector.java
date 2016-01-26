package heliumnife.connector;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQServerConnector implements ServerConnector, ExceptionListener {

	public static final URI VM_BROKER_URL = URI.create("vm://localhost?broker.persistent=false");
	
	private Connection connection;
	private Session session;
	private Queue requestQueue;
	private MessageConsumer requestConsumer;
	private MessageProducer responseProducer;
	
	private URI brokerUrl = VM_BROKER_URL;
	private int timeout = 0;
	private boolean closed = false;
	
	/**
	 * Creates an ActiveMQ connector for the given queue name on the default
	 * broker url (tcp://localhost:61616).
	 * @param requestQueueName name of the queue for receiving messages
	 * @param requestClass the class contained in an object request
	 * @throws JMSException
	 */
	public ActiveMQServerConnector(String requestQueueName, Class<? extends Serializable> requestClass) throws JMSException {
		this(requestQueueName, VM_BROKER_URL, requestClass);
	}
	
	public ActiveMQServerConnector(String requestQueueName, URI brokerUrl, Class<? extends Serializable> requestClass) throws JMSException {
		this.brokerUrl = brokerUrl;
		
		ActiveMQConnectionFactory connectionFactory = 
				new ActiveMQConnectionFactory(brokerUrl);
		connectionFactory.setTransformer(
				new GsonSerializer(requestClass));
		
		this.connection = connectionFactory.createConnection();
		this.connection.start();
		this.connection.setExceptionListener(this);

		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		this.requestQueue = session.createQueue(requestQueueName);
		this.requestConsumer = session.createConsumer(requestQueue);

		this.responseProducer = session.createProducer(null);
		this.responseProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}
	
	@Override
	public heliumnife.connector.Connection waitForConnection() throws IOException {
		if(isClosed()) {
			throw new IOException("Connector is closed");
		}
		try {
			Message message = requestConsumer.receive(getTimeout());
			if(message != null) {
				return new ActiveMQConnection(session, message, responseProducer, brokerUrl);
			} else {
				if(isClosed()) {
					throw new JMSException("MessageConsumer is closed");
				} else {
					throw new JMSException("MessageConsumer timeout exceeded");
				}
			}
		} catch (JMSException e) {
			throw new IOException("Failed to receive message from queue '"+getRequestQueueName()+"' on broker '"+getBoundURI()+"'", e);
		}
	}

	@Override
	public URI getBoundURI() {
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
			responseProducer.close();
		} catch (JMSException e) {
			throw new IOException("Failed to close response producer", e);
		}
		try {
			requestConsumer.close();
		} catch (JMSException e) {
			throw new IOException("Failed to close request consumer", e);
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

	@Override
	public void onException(JMSException e) {
		System.out.println("JMS ExceptionListener notified.");
		e.printStackTrace();
	}

}
