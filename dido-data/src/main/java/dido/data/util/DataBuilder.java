package dido.data.util;

import dido.data.NoSuchFieldException;
import dido.data.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A Fluent builder for creating {@link DidoData} data for a Known schema from a {@link DataFactory} or
 * from an Unknown schema from an {@link DataFactoryProvider}.
 */
abstract public class DataBuilder {

    abstract public DataBuilder with(String field, Object value);

    abstract public DataBuilder withBoolean(String field, boolean value);

    abstract public DataBuilder withByte(String field, byte value);

    abstract public DataBuilder withChar(String field, char value);

    abstract public DataBuilder withShort(String field, short value);

    abstract public DataBuilder withInt(String field, int value);

    abstract public DataBuilder withLong(String field, long value);

    abstract public DataBuilder withFloat(String field, float value);

    abstract public DataBuilder withDouble(String field, double value);

    abstract public DataBuilder withString(String field, String value);

    abstract  protected void setUnsetType(String name, Class<?> type);

    abstract public DidoData build();

    public static DataBuilder forSchema(DataSchema schema) {
        return new Known(DataFactoryProvider.newInstance().factoryFor(schema));
    }

    public static DataBuilder newInstance() {
        return new Unknown(DataFactoryProvider.newInstance());
    }

    public static DataBuilder forFactory(DataFactory dataFactory) {
        return new Known(dataFactory);
    }

    public static DataBuilder forProvider(DataFactoryProvider dataFactoryProvider) {
        return new Unknown(dataFactoryProvider);
    }

    static class Known extends DataBuilder {

        private final DataFactory dataFactory;

        private final Map<String, FieldSetter> setters;

        private Known(DataFactory dataFactory) {
            this.dataFactory = dataFactory;
            DataSchema schema = dataFactory.getSchema();
            WriteStrategy writeStrategy = WriteStrategy.fromSchema(schema);

            Map<String, FieldSetter> setters = new HashMap<>();
            for (String name : schema.getFieldNames()) {
                setters.put(name, writeStrategy.getFieldSetterNamed(name));
            }
            this.setters = setters;
        }

        public DataBuilder to(Consumer<? super DidoData> consumer) {
            consumer.accept(this.build());
            return this;
        }

        protected DataSchema getSchema() {
            return dataFactory.getSchema();
        }

        private FieldSetter getSetterWithNameCheck(String name) {
            FieldSetter setter = setters.get(name);
            if (setter == null) {
                throw new NoSuchFieldException(name, getSchema());
            }
            return setter;
        }

        public DataBuilder with(String field, Object value) {
            getSetterWithNameCheck(field).set(dataFactory.getWritableData(), value);
            return this;
        }

        public DataBuilder withBoolean(String field, boolean value) {
            getSetterWithNameCheck(field).setBoolean(dataFactory.getWritableData(), value);
            return this;
        }

        public DataBuilder withByte(String field, byte value) {
            getSetterWithNameCheck(field).setByte(dataFactory.getWritableData(), value);
            return this;
        }

        public DataBuilder withChar(String field, char value) {
            getSetterWithNameCheck(field).setChar(dataFactory.getWritableData(), value);
            return this;
        }

        public DataBuilder withShort(String field, short value) {
            getSetterWithNameCheck(field).setShort(dataFactory.getWritableData(), value);
            return this;
        }

        public DataBuilder withInt(String field, int value) {
            getSetterWithNameCheck(field).setInt(dataFactory.getWritableData(), value);
            return this;
        }

        public DataBuilder withLong(String field, long value) {
            getSetterWithNameCheck(field).setLong(dataFactory.getWritableData(), value);
            return this;
        }

        public DataBuilder withFloat(String field, float value) {
            getSetterWithNameCheck(field).setFloat(dataFactory.getWritableData(), value);
            return this;
        }

        public DataBuilder withDouble(String field, double value) {
            getSetterWithNameCheck(field).setDouble(dataFactory.getWritableData(), value);
            return this;
        }

        public DataBuilder withString(String field, String value) {
            getSetterWithNameCheck(field).setString(dataFactory.getWritableData(), value);
            return this;
        }

        protected void setUnsetType(String name, Class<?> type) {
            getSetterWithNameCheck(name).clear(dataFactory.getWritableData());
        }

        public DidoData build() {
            return dataFactory.toData();
        }
    }

    public static class Unknown extends DataBuilder {

        private final DataFactoryProvider dataFactoryProvider;

        private final List<SchemaField> schemaFields = new LinkedList<>();

        private final List<Object> values = new LinkedList<>();

        public Unknown(DataFactoryProvider dataFactoryProvider) {
            this.dataFactoryProvider = dataFactoryProvider;
        }

        public Unknown with(String field, Object value) {
            return setField(field, value, value == null ? void.class : value.getClass());
        }

        public Unknown withBoolean(String field, boolean value) {
            return setField(field, value, boolean.class);
        }

        public Unknown withByte(String field, byte value) {
            return setField(field, value, byte.class);
        }

        public Unknown withChar(String field, char value) {
            return setField(field, value, char.class);
        }

        public Unknown withShort(String field, short value) {
            return setField(field, value, short.class);
        }

        public Unknown withInt(String field, int value) {
            return setField(field, value, int.class);
        }

        public Unknown withLong(String field, long value) {
            return setField(field, value, long.class);
        }

        public Unknown withFloat(String field, float value) {
            return setField(field, value, float.class);
        }

        public Unknown withDouble(String field, double value) {
            return setField(field, value, double.class);
        }

        public Unknown withString(String field, String value) {
            return setField(field, value, String.class);
        }

        protected void setUnsetType(String name, Class<?> type) {
            setField(name, null, type);
        }

        private Unknown setField(String field, Object value, Class<?> type) {
            values.add(value);
            schemaFields.add(SchemaField.of(schemaFields.size() + 1, field, type));
            return this;
        }

        public DidoData build() {

            SchemaFactory schemaFactory = dataFactoryProvider.getSchemaFactory();

            for (SchemaField schemaField : this.schemaFields) {
                schemaFactory.addSchemaField(schemaField);
            }

            DataSchema schema = schemaFactory.toSchema();

            DidoData data = FieldValuesIn.withDataFactory(
                            dataFactoryProvider.factoryFor(schema))
                    .ofCollection(values);

            schemaFields.clear();
            values.clear();

            return data;
        }
    }

    public DataBuilder copy(DidoData from) {

        DataSchema schema = from.getSchema();
        for (SchemaField schemaField : schema.getSchemaFields()) {
            String name = schemaField.getName();
            Class<?> type = schemaField.getType();
            if (type == boolean.class && from.hasNamed(name)) {
                withBoolean(name, from.getBooleanNamed(name));
            }
            else if (type == byte.class && from.hasNamed(name)) {
                withByte(name, from.getByteNamed(name));
            }
            else if (type == char.class && from.hasNamed(name)) {
                withChar(name, from.getCharNamed(name));
            }
            else if (type == short.class && from.hasNamed(name)) {
                withShort(name, from.getShortNamed(name));
            }
            else if (type == int.class && from.hasNamed(name)) {
                withInt(name, from.getIntNamed(name));
            }
            else if (type == long.class && from.hasNamed(name)) {
                withLong(name, from.getLongNamed(name));
            }
            else if (type == float.class && from.hasNamed(name)) {
                withFloat(name, from.getFloatNamed(name));
            }
            else if (type == double.class && from.hasNamed(name)) {
                withDouble(name, from.getDoubleNamed(name));
            }
            else if (type == String.class && from.hasNamed(name)) {
                withString(name, from.getStringNamed(name));
            }
            else if (from.hasNamed(name)) {
                with(name, from.getNamed(name));
            }
            else {
                setUnsetType(name, type);
            }
        }
        return this;
    }
}
