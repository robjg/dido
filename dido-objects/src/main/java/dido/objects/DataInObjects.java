package dido.objects;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataIn;
import dido.how.DataInHow;
import dido.objects.izers.DestructionStrategySerializer;
import dido.objects.stratagy.BeanStrategies;
import dido.objects.stratagy.DestructionStrategy;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Read {@link DidoData} in from a Stream of Objects. THis is slightly confusing because the Objects are
 * already 'In' unlike CSV or JSON data, however we stick with it to follow the patter. To confuse matters
 * worse we think of serialisation in terms of the Objects, not the data. So we use {@link DidoSerializer}s
 * to read the {@code DidoData} in.
 *
 * @see DataOutObjects
 *
 * @param <T> The type of object.
 */
public class DataInObjects<T> implements DataInHow<Stream<T>> {

    private final Function<T, DidoData> mapperFunc;

    private DataInObjects(Type typeOfT,
                          Settings settings) {

        this.mapperFunc = settings.mapperOf(typeOfT);
    }

    public static class Settings {

        private final DidoSerializers.Builder builder = new DidoSerializers.Builder();

        public Settings serializerFactory(DidoSerializerFactory serializerFactory) {
            builder.registerSerializerFactory(serializerFactory);
            return this;
        }

        public Settings serializer(Type type, DidoSerializer serializerFactory) {
            builder.registerSerializer(type, serializerFactory);
            return this;
        }

        public BeanSettings beanOf(Class<?> beanClass) {
            return new BeanSettings(this, beanClass);
        }

        public DidoData toDidoData(Object obj) {
            return builder.create().dataSerializerFor(obj.getClass()).serialize(obj);
        }

        public Function<Object, DidoData> mapper() {
            SerializationCache cache = builder.create();
            return o -> {
                Class<?> type = o.getClass();
                DidoDataSerializer serializer = cache.dataSerializerFor(type);
                if (serializer == null) {
                    throw DataException.of("No way of serializing " + type);
                }
                else {
                    return serializer.serialize(o);
                }
            };
        }

        public <T> Function<T, DidoData> mapperOf(Type typeOfT) {
            SerializationCache cache = builder.create();
            DidoDataSerializer serializer = cache.dataSerializerFor(typeOfT);
            if (serializer == null) {
                throw DataException.of("No way of serializing " + typeOfT);
            }

            return serializer::serialize;
        }

        public <T> DataIn inFrom(Stream<T> dataIn, Type typeOfT) {
            return this.<T>makeFor(typeOfT).inFrom(dataIn);
        }

        public <T> DataInObjects<T> makeFor(Type typeOfT) {
            return new DataInObjects<>(typeOfT, this);
        }

    }

    public static class BeanSettings {

        private final Settings settings;

        private final Class<?> beanClass;

        private DataSchema schema;

        private boolean partialSchema;

        private boolean includeClass;

        private String[] fields;

        private BeanSettings(Settings settings,
                             Class<?> beanClass) {
            this.settings = settings;
            this.beanClass = beanClass;
        }

        public BeanSettings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public BeanSettings partialSchema(boolean partialSchema) {
            this.partialSchema = partialSchema;
            return this;
        }

        public BeanSettings partialSchema(DataSchema schema) {
            this.schema = schema;
            this.partialSchema = true;
            return this;
        }

        public BeanSettings includeClass(boolean includeClass) {
            this.includeClass = includeClass;
            return this;
        }

        public BeanSettings fields(String... fields) {
            this.fields = fields;
            return this;
        }

        public Settings and() {

            DestructionStrategy strategy = BeanStrategies.with()
                    .includeClass(includeClass)
                    .fields(fields)
                    .from(beanClass);

            DidoSerializerFactory factory;

            if (partialSchema || schema == null) {
                factory = DestructionStrategySerializer.fromPartial(strategy, schema);
            }
            else {
                factory = DestructionStrategySerializer.from(strategy, schema);
            }

            return settings.serializerFactory(factory);
        }

        public DidoData toDidoData(Object obj) {
            return and().toDidoData(obj);
        }

        public <T> Function<T, DidoData> mapper() {
            return and().mapperOf(beanClass);
        }

        public <T> DataIn inFrom(Stream<T> dataIn) {
            return this.<T>make().inFrom(dataIn);
        }

        public <T> DataInObjects<T> make() {
            return and().makeFor(beanClass);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public static BeanSettings beanOf(Class<?> beanClass) {
        return new BeanSettings(new Settings(), beanClass);
    }

    @Override
    public Class<Stream<T>> getInType() {
        return null;
    }

    @Override
    public DataIn inFrom(Stream<T> dataIn) {

        return new DataIn() {
            @Override
            public Iterator<DidoData> iterator() {
                return stream().iterator();
            }

            @Override
            public Stream<DidoData> stream() {
                return dataIn.map(mapperFunc);
            }
        };
    }

}
