package dido.objects;

import dido.objects.izers.ArraySerializerFactory;
import dido.objects.izers.CollectionSerializerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DidoSerializers implements SerializationCache {

    private final Map<Type, DidoSerializer> serializers = new HashMap<>();

    private final List<DidoSerializerFactory> serializerFactories;

    DidoSerializers(Builder builder) {

        this.serializerFactories = new ArrayList<>(builder.serializerFactories);
        this.serializerFactories.add(new SerializersSerializerFactory(builder.serializers));
    }


    public static class Builder {

        private final List<DidoSerializerFactory> serializerFactories = new ArrayList<>();

        private final Map<Type, DidoSerializer> serializers = new HashMap<>();

        Builder() {
            serializerFactories.add(new ArraySerializerFactory());
            serializerFactories.add(new CollectionSerializerFactory());
        }

        public Builder registerSerializer(Type type, DidoSerializer serializer) {
            serializers.put(type, serializer);
            return this;
        }

        public Builder registerSerializerFactory(DidoSerializerFactory serializerFactory) {
            serializerFactories.add(serializerFactory);
            return this;
        }

        public SerializationCache create() {
            return new DidoSerializers(this);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public DidoSerializer serializerFor(Type typeOfSrc) {

        DidoSerializer serializer = serializers.get(typeOfSrc);
        if (serializer == null) {
            for (DidoSerializerFactory factory : serializerFactories) {
                serializer = factory.create(typeOfSrc, this);
                if (serializer == null) {
                    continue;
                }
                serializers.put(typeOfSrc, serializer);
                break;
            }
        }
        return serializer;
    }

    static class SerializersSerializerFactory implements DidoSerializerFactory {

        private final Map<Type, DidoSerializer> serializers;

        SerializersSerializerFactory(Map<Type, DidoSerializer> serializers) {
            this.serializers = serializers;
        }

        @Override
        public DidoSerializer create(Type type, SerializationCache serializationCache) {
            return serializers.get(type);
        }
    }
}


