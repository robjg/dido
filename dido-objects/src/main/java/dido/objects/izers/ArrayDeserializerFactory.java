package dido.objects.izers;

import dido.data.RepeatingData;
import dido.data.util.TypeUtil;
import dido.objects.*;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

public class ArrayDeserializerFactory implements DidoDeserializerFactory {

    @Override
    public DidoDeserializer create(Type type, DeserializationCache deserializationCache) {

        Class<?> rawType = TypeUtil.classOf(type);
        if (!rawType.isArray()) {
            return null;
        }

        Class<?> componentType = rawType.getComponentType();

        DidoDataDeserializer<?> componentDeserializer =
                (DidoDataDeserializer<?>) deserializationCache.deserializerFor(componentType);
        if (componentDeserializer == null) {
            return null;
        }

        return new ArrayDeserializer<>(componentDeserializer,  componentType);
    }

    static class ArrayDeserializer<T> implements RepeatingDeserializer<T[]> {

        private final DidoDataDeserializer<?> componentDeserializer;

        private final Class<T> componentType;

        ArrayDeserializer(DidoDataDeserializer<?> componentDeserializer,
                          Class<T> componentType) {
            this.componentDeserializer = componentDeserializer;
            this.componentType = componentType;
        }

        @Override
        public T[] deserialize(RepeatingData repeatingData) {

            T[] array = (T[]) Array.newInstance(componentType, repeatingData.size());
            for (int i = 0; i< array.length; ++i) {
                array[i] = (T) componentDeserializer.deserialize(repeatingData.get(i));
            }
            return array;
        }
    }
}
