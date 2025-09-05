package dido.objects.izers;

import dido.data.*;
import dido.data.NoSuchFieldException;
import dido.data.schema.DataSchemaImpl;
import dido.data.useful.AbstractData;
import dido.data.useful.AbstractFieldGetter;
import dido.how.DataException;
import dido.objects.*;
import dido.objects.stratagy.DestructionStrategy;
import dido.objects.stratagy.ValueGetter;

import java.lang.reflect.Type;
import java.util.*;

/**
 * A
 */
public class DestructionStrategySerializer implements DidoDataSerializer {

    private final Schema schema;

    private DestructionStrategySerializer(Schema schema) {
        this.schema = schema;
    }

    public static DidoSerializerFactory from(DestructionStrategy strategy) {

        return fromPartial(strategy, DataSchema.emptySchema());
    }

    public static DidoSerializerFactory fromPartial(DestructionStrategy strategy, DataSchema schema) {
        return new PartialSerializationFactory(strategy, schema);
    }

    public static DidoSerializerFactory from(DestructionStrategy strategy, DataSchema schema) {
        return new FullSerializationFactory(strategy, schema);
    }

    @Override
    public DataSchema getSchema() {
        return schema;
    }

    @Override
    public DidoData serialize(Object src) {
        return new Data(schema, src);
    }

    static class FullSerializationFactory implements DidoSerializerFactory {

        private final DestructionStrategy strategy;

        private final DataSchema schema;

        FullSerializationFactory(DestructionStrategy strategy, DataSchema schema) {
            this.strategy = strategy;
            this.schema = schema;
        }

        @Override
        public DidoSerializer create(Type type, SerializationCache serializationCache) {

            if (type != strategy.getType()) {
                return null;
            }

            FieldGetter[] getters = new FieldGetter[schema.lastIndex()];
            for (SchemaField schemaField : schema.getSchemaFields()) {
                String name = schemaField.getName();
                if (name == null) {
                    continue;
                }
                ValueGetter valueGetter = strategy.getGetter(name);
                if (valueGetter == null) {
                    continue;
                }
                getters[schemaField.getIndex() - 1] = getterFor(valueGetter,
                        schemaField, serializationCache);
            }

            Schema readableSchema = schema instanceof DataSchemaImpl ?
                    new Schema(getters, (DataSchemaImpl) schema) :
                    new Schema(getters, schema.getSchemaFields(), schema.firstIndex(), schema.lastIndex());

            return new DestructionStrategySerializer(readableSchema);
        }
    }

    static FieldGetter getterFor(ValueGetter valueGetter,
                                 SchemaField schemaField,
                                 SerializationCache serializationCache) {
        if (schemaField.isNested()) {
            DidoSerializer serializer = serializationCache.serializerFor(valueGetter.getType());
            if (serializer == null) {
                throw DataException.of("No way to serialize " + valueGetter.getType());
            }
            if (serializer instanceof RepeatingSerializer) {
                return new RepeatingGetter(valueGetter, ((RepeatingSerializer) serializer));
            } else {
                return new NestedGetter(valueGetter, ((DidoDataSerializer) serializer));
            }
        } else {
            return new SimpleGetter(valueGetter);
        }
    }

    static class PartialSerializationFactory implements DidoSerializerFactory {

        private final DestructionStrategy strategy;

        private final DataSchema schema;

        PartialSerializationFactory(DestructionStrategy strategy, DataSchema schema) {
            this.strategy = strategy;
            this.schema = Objects.requireNonNullElse(schema, DataSchema.emptySchema());
        }

        @Override
        public DidoSerializer create(Type type, SerializationCache serializationCache) {

            if (type != strategy.getType()) {
                return null;
            }

            Collection<ValueGetter> valueGetters = strategy.getGetters();
            List<SchemaField> schemaFields = new ArrayList<>(valueGetters.size());
            FieldGetter[] getters = new FieldGetter[valueGetters.size()];
            int i = 0;
            for (ValueGetter valueGetter : valueGetters) {
                SchemaField schemaField = schema.getSchemaFieldNamed(valueGetter.getName());
                FieldGetter fieldGetter;
                if (schemaField == null) {
                    DidoSerializer serializer = serializationCache.serializerFor(valueGetter.getType());
                    if (serializer == null) {
                        schemaField = SchemaField.of(i + 1, valueGetter.getName(), valueGetter.getType());
                        fieldGetter = new SimpleGetter(valueGetter);
                    } else {
                        if (serializer instanceof RepeatingSerializer) {
                            schemaField = SchemaField.ofRepeating(i + 1,
                                    valueGetter.getName(), serializer.getSchema());
                            fieldGetter = new RepeatingGetter(valueGetter, (RepeatingSerializer) serializer);
                        } else {
                            schemaField = SchemaField.ofNested(i + 1,
                                    valueGetter.getName(), serializer.getSchema());
                            fieldGetter = new NestedGetter(valueGetter, (DidoDataSerializer) serializer);
                        }
                    }
                } else {
                    fieldGetter = getterFor(valueGetter, schemaField, serializationCache);
                }
                schemaFields.add(schemaField);
                getters[i++] = fieldGetter;
            }

            return new DestructionStrategySerializer(new Schema(
                    getters, schemaFields, 1, schemaFields.size()));
        }
    }

    static class SimpleGetter extends AbstractFieldGetter {

        private final ValueGetter getter;

        SimpleGetter(ValueGetter getter) {
            this.getter = getter;
        }

        @Override
        public Object get(DidoData data) {
            return getter.getValue(((Data) data).target);
        }
    }

    static class NestedGetter extends AbstractFieldGetter {

        private final ValueGetter getter;

        private final DidoDataSerializer serializer;

        NestedGetter(ValueGetter getter,
                     DidoDataSerializer serializer) {
            this.getter = getter;
            this.serializer = serializer;
        }

        @Override
        public DidoData get(DidoData data) {
            Object value = getter.getValue(((Data) data).target);
            if (value == null) {
                return null;
            } else {
                return serializer.serialize(value);
            }
        }
    }

    static class RepeatingGetter extends AbstractFieldGetter {

        private final ValueGetter getter;

        private final RepeatingSerializer serializer;

        RepeatingGetter(ValueGetter getter,
                        RepeatingSerializer serializer) {
            this.getter = getter;
            this.serializer = serializer;
        }

        @Override
        public RepeatingData get(DidoData data) {
            Object value = getter.getValue(((Data) data).target);
            if (value == null) {
                return null;
            } else {
                return serializer.serialize(value);
            }
        }
    }

    static class Schema extends DataSchemaImpl implements ReadSchema {

        private final FieldGetter[] getters;

        Schema(FieldGetter[] getters, DataSchemaImpl schema) {
            super(schema);
            this.getters = getters;
        }

        Schema(FieldGetter[] getters, Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {
            super(schemaFields, firstIndex, lastIndex);
            this.getters = getters;
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            return new AbstractFieldGetter() {
                @Override
                public Object get(DidoData data) {
                    try {
                        return getters[index - 1];
                    } catch (NoSuchElementException e) {
                        throw new NoSuchFieldException(index, Schema.this);
                    }
                }
            };
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, Schema.this);
            }
            return getFieldGetterAt(index);
        }
    }

    static class Data extends AbstractData {

        private final Schema schema;

        private final Object target;

        Data(Schema schema,
             Object target) {
            this.schema = schema;
            this.target = target;
        }

        @Override
        public DataSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            return schema.getters[index - 1].get(this);
        }
    }
}
