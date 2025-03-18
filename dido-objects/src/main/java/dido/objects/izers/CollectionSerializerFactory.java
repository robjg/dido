package dido.objects.izers;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.RepeatingData;
import dido.data.util.TypeUtil;
import dido.how.conversion.$Gson$Types;
import dido.objects.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionSerializerFactory implements DidoSerializerFactory {

    @Override
    public DidoSerializer create(Type type, SerializationCache serializationCache) {

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


        DidoSerializer componentSerializer  = serializationCache.serializerFor(componentType);
        if (!(componentSerializer instanceof DidoDataSerializer) ) {
            return null;
        }

        return new CollectionSerializer((DidoDataSerializer) componentSerializer);
    }

    static class CollectionSerializer implements RepeatingSerializer {

        private final DidoDataSerializer serializer;

        CollectionSerializer(DidoDataSerializer serializer) {
            this.serializer = serializer;
        }

        @Override
        public DataSchema getSchema() {
            return serializer.getSchema();
        }

        @Override
        public RepeatingData serialize(Object src) {

            List<DidoData> rows = ((Collection<?>) src)
                    .stream().map(serializer::serialize)
                    .collect(Collectors.toList());

            return RepeatingData.of(rows);
        }
    }

}
