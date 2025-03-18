package dido.objects;


import java.lang.reflect.Type;

public interface DidoSerializerFactory {

  DidoSerializer create(Type type, SerializationCache serializationCache);
}
