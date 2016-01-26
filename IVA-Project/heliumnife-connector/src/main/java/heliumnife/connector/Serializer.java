package heliumnife.connector;

import java.io.Serializable;

public interface Serializer {

	String serialize(Serializable object);

	Serializable deserialize(String serializedObject);

}