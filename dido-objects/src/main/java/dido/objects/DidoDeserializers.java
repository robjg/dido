package dido.objects;

import dido.objects.izers.ArrayDeserializerFactory;
import dido.objects.izers.CollectionDeserializerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DidoDeserializers implements DeserializationCache {

    private final List<DidoDeserializerFactory> deserializerFactories;

    private final Map<Type, DidoDeserializer> deserializers = new HashMap<>();

    DidoDeserializers(Builder builder) {
        this.deserializerFactories = new ArrayList<>(builder.deserializerFactories);
        this.deserializerFactories.add(new DeserializersDeserializerFactory(builder.deserializers));
    }

    public static class Builder {

        private final List<DidoDeserializerFactory> deserializerFactories = new ArrayList<>();

        private final Map<Type, DidoDeserializer> deserializers = new HashMap<>();

        private Builder() {
            deserializerFactories.add(new ArrayDeserializerFactory());
            deserializerFactories.add(new CollectionDeserializerFactory());
        }

        public Builder registerDeserializer(Type type, DidoDeserializer deserializer) {
            deserializers.put(type, deserializer);
            return this;
        }

        public Builder registerDeserializerFactory(DidoDeserializerFactory deserializerFactory) {
            deserializerFactories.add(deserializerFactory);
            return this;
        }

        public DidoDeserializers build() {
            return new DidoDeserializers(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public DidoDeserializer deserializerFor(Type typeOfT) {

        DidoDeserializer deserializer = deserializers.get(typeOfT);
        if (deserializer == null) {
            for (DidoDeserializerFactory factory : deserializerFactories) {
                deserializer = factory.create(typeOfT, this);
                if (deserializer == null) {
                    continue;
                }
                deserializers.put(typeOfT, deserializer);
                break;
            }
        }
        return deserializer;
    }

    static class DeserializersDeserializerFactory implements DidoDeserializerFactory {

        private final Map<Type, DidoDeserializer> deserializers;

        DeserializersDeserializerFactory(Map<Type, DidoDeserializer> deserializers) {
            this.deserializers = new HashMap<>(deserializers);
        }

        @Override
        public DidoDeserializer create(Type type, DeserializationCache deserializationCache) {
            return deserializers.get(type);
        }
    }

}
