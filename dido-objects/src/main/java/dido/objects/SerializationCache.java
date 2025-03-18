package dido.objects;

import java.lang.reflect.Type;

public interface SerializationCache {

    DidoSerializer serializerFor(Type type);

    default DidoDataSerializer dataSerializerFor(Type type) {

        DidoSerializer serializer = serializerFor(type);
        if (serializer instanceof DidoDataSerializer) {
            return (DidoDataSerializer) serializer;
        }
        else {
            return null;
        }

    }
}
