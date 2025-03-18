package dido.objects;

import java.lang.reflect.Type;

public interface DeserializationCache {

    DidoDeserializer deserializerFor(Type type);
}
