package dido.objects.izers;

import dido.data.*;
import dido.data.util.TypeUtil;
import dido.how.DataException;
import dido.objects.*;
import dido.objects.stratagy.ConstructionStrategy;
import dido.objects.stratagy.ObjectConstructor;
import dido.objects.stratagy.ValueSetter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ConstructionStrategyDeserializer<T> implements DidoDataDeserializer<T> {

    private final Supplier<ObjectConstructor<T>> objectSupplier;

    private final List<Injector> injectors;

    private ConstructionStrategyDeserializer(Supplier<ObjectConstructor<T>> objectSupplier,
                                             List<Injector> injectors) {
        this.objectSupplier = objectSupplier;
        this.injectors = injectors;
    }

    public static DidoDeserializerFactory from(
            ConstructionStrategy<?> setters, DataSchema schema) {
        if (schema == null) {
            return new UnknownFactoryImpl(setters);
        }
        else {
            return new FactoryImpl(schema, setters);
        }
    }

    static class FactoryImpl implements DidoDeserializerFactory {

        private final DataSchema schema;

        private final ConstructionStrategy<?> setters;

        FactoryImpl(DataSchema schema, ConstructionStrategy<?> setters) {
            this.schema = schema;
            this.setters = setters;
        }

        @Override
        public DidoDeserializer create(Type type, DeserializationCache deserializationCache) {

            if (!TypeUtil.isAssignableFrom(type, setters.getType())) {
                return null;
            }

            List<Injector> injectors = new ArrayList<>();

            ReadSchema readSchema = ReadSchema.from(schema);

            for (SchemaField schemaField : schema.getSchemaFields()) {

                ValueSetter setter = setters.getSetter(schemaField.getName());
                if (setter == null) {
                    continue;
                }

                FieldGetter getter = readSchema.getFieldGetterNamed(setter.getName());

                if (schemaField.isNested()) {

                    DidoDeserializer deserializer = deserializationCache.deserializerFor(setter.getType());
                    if (deserializer == null) {
                        throw DataException.of("No way to deserialize " + setter.getType());
                    }
                    if (schemaField.isRepeating()) {

                        injectors.add(new RepeatingInjector(setter, getter, (RepeatingDeserializer<?>) deserializer));
                    } else {
                        injectors.add(new NestedInjector(setter, getter, (DidoDataDeserializer<?>) deserializer));
                    }
                } else {
                    injectors.add(new SimpleInjector(setter, getter));
                }
            }

            return createWithInfer(setters.getConstructorSupplier(), injectors);
        }
    }

    static class UnknownFactoryImpl implements DidoDeserializerFactory {

        private final ConstructionStrategy<?> setters;

        UnknownFactoryImpl(ConstructionStrategy<?> setters) {
            this.setters = setters;
        }

        @Override
        public DidoDeserializer create(Type type, DeserializationCache deserializationCache) {

            if (!TypeUtil.isAssignableFrom(type, setters.getType())) {
                return null;
            }

            return new UnknownDeserializer<>(setters, deserializationCache);
        }
    }

    static class UnknownDeserializer<T> implements DidoDataDeserializer<T> {

        private final ConstructionStrategy<?> setters;

        private final DeserializationCache deserializationCache;

        private DataSchema lastSchema;

        private DidoDataDeserializer<T> deserializer;

        UnknownDeserializer(ConstructionStrategy<?> setters, DeserializationCache deserializationCache) {
            this.setters = setters;
            this.deserializationCache = deserializationCache;
        }

        @Override
        public T deserialize(DidoData data) {
            DataSchema schema = data.getSchema();
            if (!schema.equals(lastSchema)) {
                this.lastSchema = schema;
                this.deserializer = (DidoDataDeserializer<T>) new FactoryImpl(schema, setters)
                        .create(setters.getType(), deserializationCache);
            }
            return this.deserializer.deserialize(data);
        }
    }

    private static <T> ConstructionStrategyDeserializer<T> createWithInfer(Supplier<ObjectConstructor<T>> objectSupplier,
                                                                           List<Injector> injectors) {
        return new ConstructionStrategyDeserializer<>(objectSupplier, injectors);
    }

    interface Injector {

        void inject(DidoData data, ObjectConstructor<?> instance);
    }

    static class SimpleInjector implements Injector {

        private final ValueSetter setter;

        private final FieldGetter getter;

        SimpleInjector(ValueSetter setter, FieldGetter getter) {
            this.setter = setter;
            this.getter = getter;
        }

        @Override
        public void inject(DidoData data, ObjectConstructor<?> instance) {

            if (getter.has(data)) {
                setter.setValue(instance, getter.get(data));
            }
        }
    }

    static class NestedInjector implements Injector {

        private final ValueSetter setter;

        private final FieldGetter getter;

        private final DidoDataDeserializer<?> deserializer;

        NestedInjector(ValueSetter setter, FieldGetter getter, DidoDataDeserializer<?> deserializer) {
            this.setter = setter;
            this.getter = getter;
            this.deserializer = deserializer;
        }

        @Override
        public void inject(DidoData data, ObjectConstructor<?> instance) {

            if (getter.has(data)) {
                setter.setValue(instance,
                        deserializer.deserialize((DidoData) getter.get(data)));
            }
        }
    }

    static class RepeatingInjector implements Injector {

        private final ValueSetter setter;

        private final FieldGetter getter;

        private final RepeatingDeserializer<?> deserializer;

        RepeatingInjector(ValueSetter setter, FieldGetter getter, RepeatingDeserializer<?> deserializer) {
            this.setter = setter;
            this.getter = getter;
            this.deserializer = deserializer;
        }

        @Override
        public void inject(DidoData data, ObjectConstructor<?> instance) {

            if (getter.has(data)) {
                setter.setValue(instance,
                        deserializer.deserialize((RepeatingData) getter.get(data)));
            }
        }
    }

    @Override
    public T deserialize(DidoData data) {

        ObjectConstructor<T> instance = objectSupplier.get();
        for (Injector injector : injectors) {
            injector.inject(data, instance);
        }

        return instance.actualize();
    }
}
