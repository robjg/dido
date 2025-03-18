package dido.objects;


import java.lang.reflect.Type;

public interface DidoDeserializerFactory {

  DidoDeserializer create(Type type, DeserializationCache deserializationCache);
}
