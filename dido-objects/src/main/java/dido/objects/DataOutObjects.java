package dido.objects;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.how.DataException;
import dido.how.DataOut;
import dido.how.DataOutHow;
import dido.objects.izers.ConstructionStrategyDeserializer;
import dido.objects.izers.DestructionStrategySerializer;
import dido.objects.stratagy.BeanStrategies;
import dido.objects.stratagy.ConstructionStrategy;
import dido.objects.stratagy.DestructionStrategy;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Writes {@code DidoData} out as Objects
 *
 * @param <T>
 */
public class DataOutObjects<T> implements DataOutHow<Consumer<? super T>> {

    private final Function<DidoData, T> mapperFunc;

    DataOutObjects(Type typeOfT,
                   Settings settings) {
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

            ConstructionStrategy strategy = BeanStrategies.with()
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

        public <T> DataOutObjects<T> make() {
            return new DataOutObjects<>(beanClass, and());
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
        return null;
    }

}
