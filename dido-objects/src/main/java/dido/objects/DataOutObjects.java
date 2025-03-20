package dido.objects;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.objects.izers.ConstructionStrategyDeserializer;
import dido.objects.stratagy.BeanStrategies;
import dido.objects.stratagy.ConstructionStrategy;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Writes {@code DidoData} out as Objects
 *
 * @param <T>
 */
public class DataOutObjects<T> implements DataOutHow<Consumer<? super T>> {

    private final Type typeOfT;

    private final Function<DidoData, T> mapperFunc;

    DataOutObjects(Type typeOfT,
                   Settings settings) {
        this.typeOfT = typeOfT;
        this.mapperFunc = settings.mapperOf(typeOfT);
    }

    public static class Settings {

        private final DidoDeserializers.Builder builder = DidoDeserializers.builder();

        public Settings deserializerFactory(DidoDeserializerFactory deserializerFactory) {
            builder.registerDeserializerFactory(deserializerFactory);
            return this;
        }

        public Settings deserializer(Type type, DidoDeserializer serializerFactory) {
            builder.registerDeserializer(type, serializerFactory);
            return this;
        }

        public BeanSettings beanOf(Class<?> beanClass) {
            return new BeanSettings(this, beanClass);
        }

        public <T> T fromDidoData(DidoData didoData, Type typeOfT) {
            Function<DidoData, T> func = mapperOf(typeOfT);
            return func.apply(didoData);
        }

        public <T> Function<DidoData, T> mapperOf(Type typeOfT) {
            DidoDeserializer deserializer = builder.build().deserializerFor(typeOfT);
            if (deserializer instanceof DidoDataDeserializer) {
                return didoData -> ((DidoDataDeserializer<T>) deserializer).deserialize(didoData);
            } else {
                throw DataException.of("No way of deserializing " + typeOfT);
            }
        }

        public <T> DataOut outTo(Consumer<? super T> consumer, Type typeOfT) {
            return this.<T>makeFor(typeOfT).outTo(consumer);
        }

        public <T> DataOutObjects<T> makeFor(Type typeOfT) {
            return new DataOutObjects<>(typeOfT, this);
        }
    }

    public static class BeanSettings {

        private final Settings settings;

        private final Class<?> beanClass;

        private DataSchema schema;

        private BeanSettings(Settings settings,
                             Class<?> beanClass) {
            this.settings = settings;
            this.beanClass = beanClass;
        }

        public BeanSettings schema(DataSchema schema) {
            this.schema = schema;
            return this;
        }

        public Settings and() {

            ConstructionStrategy<?> strategy = BeanStrategies.with()
                    .from(beanClass);

            DidoDeserializerFactory factory = ConstructionStrategyDeserializer.from(strategy, schema);

            return settings.deserializerFactory(factory);
        }

        public <T> T fromDidoData(DidoData didoData) {
            return and().fromDidoData(didoData, beanClass);
        }

        public <T> Function<DidoData, T> mapper() {
            return and().mapperOf(beanClass);
        }

        public <T> DataOut outTo(Consumer<? super T> outTo) {
            return this.<T>make().outTo(outTo);
        }

        public <T> DataOutObjects<T> make() {
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
    public Class<Consumer<? super T>> getOutType() {
        return null;
    }

    @Override
    public DataOut outTo(Consumer<? super T> outTo) {

        return new DataOut() {
            @Override
            public void close() {
                if (outTo instanceof AutoCloseable) {
                    try {
                        ((AutoCloseable) outTo).close();
                    } catch (Exception e) {
                        throw new DataException("Failed to close " + this, e);
                    }
                }
            }

            @Override
            public void accept(DidoData didoData) {
                outTo.accept(mapperFunc.apply(didoData));
            }

            @Override
            public String toString() {
                return "DataOut for Objects of" + typeOfT;
            }
        };
    }

}
