package heliumnife.connector;

import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.MessageTransformerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class GsonSerializer extends MessageTransformerSupport implements Serializer {

	private static final Logger LOG = LoggerFactory.getLogger(GsonSerializer.class);
	
	private static final Gson gson = new GsonBuilder()
	.serializeSpecialFloatingPointValues()
	.serializeNulls()
	.create();
	
	private Type deserializedType;
	
	/**
	 * This constructor should be used if deserializing a non-generic type.
	 * @param deserializedClass
	 */
	public GsonSerializer(Class<? extends Serializable> deserializedClass) {
		this.deserializedType = deserializedClass;
	}
	
	/**
	 * This constructor should be used if deserializing a generic type.
	 * @param deserializedType
	 */
	public GsonSerializer(Type deserializedType) {
		this.deserializedType = deserializedType;
	}

	@Override
	public String serialize(Serializable object) {
		return toJson(object);
	}

	@Override
	public Serializable deserialize(String serializedObject) {
		if(deserializedType.equals(String.class)) {
			return serializedObject;
		}
		try {
			return fromJson(serializedObject);
		} catch (JsonSyntaxException e) {
			LOG.warn("Cannot parse JSON from String '"+serializedObject+"' handling as plaintext", e);
			return serializedObject;
		}
	}

	@Override
	public Message consumerTransform(Session session, MessageConsumer consumer, Message message) throws JMSException {
		if(!(message instanceof TextMessage) || deserializedType.equals(String.class)) {
			return message;
		}
		String json = ((TextMessage) message).getText();
		try {
			Serializable obj = fromJson(json);
			ObjectMessage objectMessage = session.createObjectMessage(obj);
			copyProperties(message, objectMessage);
			return objectMessage;
		} catch (JsonSyntaxException e) {
			LOG.warn("Cannot parse JSON from TextMessage '"+json+"' handling as plaintext", e);
			return message;
		}
	}

	@Override
	public Message producerTransform(Session session, MessageProducer producer, Message message) throws JMSException {
		if(!(message instanceof ObjectMessage)) {
			return message;
		}
		Serializable obj = ((ObjectMessage) message).getObject();
		String json = toJson(obj);
		TextMessage textMessage = session.createTextMessage(json);
		copyProperties(message, textMessage);
		return textMessage;
	}

	/**
	 * @see com.google.gson.Gson#fromJson(java.io.Reader, java.lang.reflect.Type)
	 */
	public <T> T fromJson(Reader json) throws JsonIOException, JsonSyntaxException {
		return gson.fromJson(json, deserializedType);
	}

	/**
	 * @see com.google.gson.Gson#fromJson(java.lang.String, java.lang.reflect.Type)
	 */
	public <T> T fromJson(String json) throws JsonSyntaxException {
		return gson.fromJson(json, deserializedType);
	}

	/**
	 * @see com.google.gson.Gson#fromJson(java.io.Reader, java.lang.Class)
	 */
	public static <T> T fromJson(Reader json, Class<T> classOfT)
			throws JsonSyntaxException, JsonIOException {
		return gson.fromJson(json, classOfT);
	}

	/**
	 * @see com.google.gson.Gson#fromJson(java.io.Reader, java.lang.reflect.Type)
	 */
	public static <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException,
			JsonSyntaxException {
		return gson.fromJson(json, typeOfT);
	}

	/**
	 * @see com.google.gson.Gson#fromJson(java.lang.String, java.lang.Class)
	 */
	public static <T> T fromJson(String json, Class<T> classOfT)
			throws JsonSyntaxException {
		return gson.fromJson(json, classOfT);
	}

	/**
	 * @see com.google.gson.Gson#fromJson(java.lang.String, java.lang.reflect.Type)
	 */
	public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
		return gson.fromJson(json, typeOfT);
	}

	/**
	 * @see com.google.gson.Gson#toJson(java.lang.Object, java.lang.Appendable)
	 */
	public static void toJson(Object src, Appendable writer) throws JsonIOException {
		gson.toJson(src, writer);
	}

	/**
	 * @see com.google.gson.Gson#toJson(java.lang.Object, java.lang.reflect.Type, java.lang.Appendable)
	 */
	public static void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
		gson.toJson(src, typeOfSrc, writer);
	}

	/**
	 * @see com.google.gson.Gson#toJson(java.lang.Object, java.lang.reflect.Type)
	 */
	public static String toJson(Object src, Type typeOfSrc) {
		return gson.toJson(src, typeOfSrc);
	}

	/**
	 * @see com.google.gson.Gson#toJson(java.lang.Object)
	 */
	public static String toJson(Object src) {
		return gson.toJson(src);
	}

}
