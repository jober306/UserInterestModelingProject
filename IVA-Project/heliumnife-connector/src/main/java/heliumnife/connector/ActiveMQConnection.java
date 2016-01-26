package heliumnife.connector;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

public class ActiveMQConnection implements Connection {

	private Session session;
	private Message message;
	private MessageProducer responseProducer;
	
	private URI uri = null;
	private boolean closed = false;

	public ActiveMQConnection(Session session, Message message, MessageProducer responseProducer) throws IOException {
		this.session = session;
		this.message = message;
		this.responseProducer = responseProducer;
	}
	
	public ActiveMQConnection(Session session, Message message, MessageProducer responseProducer, URI uri) throws IOException {
		this(session, message, responseProducer);
		this.uri = uri;
	}

	@Override
	public Serializable receiveRequest() throws IOException {
		if(isClosed()) {
			throw new IOException("Connection is closed");
		}
		try {
			if(message instanceof ObjectMessage) {
				return ((ObjectMessage) message).getObject();
			}
			else if(message instanceof TextMessage) {
				return ((TextMessage) message).getText();
			}
			else if(message instanceof BytesMessage) {
				BytesMessage bytesMessage = (BytesMessage) message;
				int length = bytesMessage.readInt();
				byte[] request = new byte[length];
				bytesMessage.readBytes(request);
				return request;
			}
			else {
				throw new JMSException("Message type not found: "+message.getClass());
			}
		} catch (JMSException e) {
			throw new IOException("Failed to read request string from Message", e);
		}
	}

	@Override
	public void sendResponse(Serializable response) throws IOException {
		if(isClosed()) {
			throw new IOException("Connection is closed");
		}
		try {
			ObjectMessage responseMessage = session.createObjectMessage(response);
			responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
			responseProducer.send(message.getJMSReplyTo(), responseMessage);
		} catch (JMSException e) {
			throw new IOException("Failed to send response ObjectMessage", e);
		}
	}

	@Override
	public void sendResponse(String response) throws IOException {
		if(isClosed()) {
			throw new IOException("Connection is closed");
		}
		try {
			TextMessage responseMessage = session.createTextMessage(response);
			responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());
			responseProducer.send(message.getJMSReplyTo(), responseMessage);
		} catch (JMSException e) {
			throw new IOException("Failed to send response TextMessage", e);
		}
	}

	public void sendResponse(byte[] reply) throws IOException {
		if(isClosed()) {
			throw new IOException("Connection is closed");
		}
		try {
			BytesMessage replyMessage = session.createBytesMessage();
			replyMessage.writeInt(reply.length);
			replyMessage.writeBytes(reply);
			replyMessage.setJMSCorrelationID(message.getJMSCorrelationID());
			responseProducer.send(message.getJMSReplyTo(), replyMessage);
		} catch (JMSException e) {
			throw new IOException("Failed to send response BytesMessage", e);
		}
	}

	@Override
	public URI getURI() {
		return uri;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() throws IOException {
		closed = true;
	}

}
