package dido.objects.izers;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.RepeatingData;
import dido.data.util.TypeUtil;
import dido.objects.*;

import java.lang.reflect.Type;

public class ArraySerializerFactory implements DidoSerializerFactory {

    @Override
    public DidoSerializer create(Type type, SerializationCache serializationCache) {

        Class<?> rawType = TypeUtil.classOf(type);
        if (!rawType.isArray()) {
            return null;
        }

        Class<?> componentType = rawType.getComponentType();

        DidoSerializer componentSerializer  = serializationCache.serializerFor(componentType);
        if (!(componentSerializer instanceof DidoDataSerializer) ) {
            return null;
        }

        return new ArraySerializer((DidoDataSerializer) componentSerializer);
    }

    static class ArraySerializer implements RepeatingSerializer {

        private final DidoDataSerializer serializer;

        ArraySerializer(DidoDataSerializer serializer) {
            this.serializer = serializer;
        }

        @Override
        public DataSchema getSchema() {
            return serializer.getSchema();
        }

        @Override
        public RepeatingData serialize(Object src) {

            Object[] array = (Object[]) src;
            DidoData[] rows = new DidoData[array.length];
            for (int i = 0; i < rows.length; i++) {
                rows[i] = serializer.serialize(array[i]);
            }

            return RepeatingData.of(rows);
        }
    }

}
