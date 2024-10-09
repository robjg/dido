package dido.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A Fluent builder for creating data from an {@link WriteSchema}.
 *
 * @param <D> The data type.
 */
public class DataBuilder<D extends DidoData> {

    private final DataFactory<D> dataFactory;

    private final Map<String, FieldSetter> setters;

    public DataBuilder(DataFactory<D> dataFactory) {
        this.dataFactory = dataFactory;
        DataSchema schema = dataFactory.getSchema();
        WriteStrategy writeStrategy = WriteStrategy.fromSchema(schema);

        Map<String, FieldSetter> setters = new HashMap<>();
        for (String name : schema.getFieldNames()) {
            setters.put(name, writeStrategy.getFieldSetterNamed(name));
        }
        this.setters = setters;
    }

    public DataBuilder<D> to(Consumer<? super DidoData> consumer) {
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

    public DataBuilder<D> with(String field, Object value) {
        getSetterWithNameCheck(field).set(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> withBoolean(String field, boolean value) {
        getSetterWithNameCheck(field).setBoolean(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> withByte(String field, byte value) {
        getSetterWithNameCheck(field).setByte(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> withChar(String field, char value) {
        getSetterWithNameCheck(field).setChar(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> withShort(String field, short value) {
        getSetterWithNameCheck(field).setShort(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> withInt(String field, int value) {
        getSetterWithNameCheck(field).setInt(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> withLong(String field, long value) {
        getSetterWithNameCheck(field).setLong(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> withFloat(String field, float value) {
        getSetterWithNameCheck(field).setFloat(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> withDouble(String field, double value) {
        getSetterWithNameCheck(field).setDouble(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> withString(String field, String value) {
        getSetterWithNameCheck(field).setString(dataFactory.getWritableData(), value);
        return this;
    }

    public DataBuilder<D> copy(DidoData from) {

        DataSchema schema = from.getSchema();
        for (String field : schema.getFieldNames()) {
            with(field, from.getNamed(field));
        }
        return this;
    }

    public D build() {
        return dataFactory.toData();
    }

    public static class ValuesTo<D extends DidoData> {

        private final Consumer<? super D> consumer;
        private final Values<D> owner;

        ValuesTo(Consumer<? super D> consumer, Values<D> owner) {
            this.consumer = consumer;
            this.owner = owner;
        }

        public ValuesTo<D> of(Object... values) {
            D data = owner.of(values);
            consumer.accept(data);
            return this;
        }
    }
}
