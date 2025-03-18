package dido.objects.izers;

import dido.data.RepeatingData;
import dido.data.util.TypeUtil;
import dido.how.conversion.$Gson$Types;
import dido.objects.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionDeserializerFactory implements DidoDeserializerFactory {

    @Override
    public DidoDeserializer create(Type type, DeserializationCache deserializationCache) {

        Class<?> rawType = TypeUtil.classOf(type);
        if (!Collection.class.isAssignableFrom(rawType)) {
            return null;
        }

        Type componentType;
        if (rawType == type) {
            componentType = Object.class;
        }
        else {
            componentType = $Gson$Types.getCollectionElementType(type, rawType);
        }

        DidoDataDeserializer<?> componentDeserializer =
                (DidoDataDeserializer<?>) deserializationCache.deserializerFor(componentType);

        if (componentDeserializer == null) {
            return null;
        }

        if (rawType.isAssignableFrom(List.class)) {
            return new ListDeserializer<>(componentDeserializer);
        }

        if (rawType.isAssignableFrom(Set.class)) {
            return  new SetDeserializer<>(componentDeserializer);
        }

        return null;
    }

    static class ListDeserializer<T> implements RepeatingDeserializer<List<T>> {

        private final DidoDataDeserializer<T> componentDeserializer;

        ListDeserializer(DidoDataDeserializer<T> componentDeserializer) {
            this.componentDeserializer = componentDeserializer;
        }

        @Override
        public List<T> deserialize(RepeatingData repeatingData) {
            return repeatingData.stream()
                    .map(componentDeserializer::deserialize)
                    .collect(Collectors.toList());
        }
    }

    static class SetDeserializer<T> implements RepeatingDeserializer<Set<T>> {

        private final DidoDataDeserializer<T> componentDeserializer;

        SetDeserializer(DidoDataDeserializer<T> componentDeserializer) {
            this.componentDeserializer = componentDeserializer;
        }

        @Override
        public Set<T> deserialize(RepeatingData repeatingData) {
            return repeatingData.stream()
                    .map(componentDeserializer::deserialize)
                    .collect(Collectors.toSet());
        }
    }
}
