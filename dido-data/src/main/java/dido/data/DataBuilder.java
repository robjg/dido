package dido.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A Fluent builder for creating data from an {@link WritableSchema}.
 *
 * @param <D> The data type.
 */
public class DataBuilder<D extends DidoData> {

    private final WritableSchema<D> schema;

    private final DataFactory<D> dataFactory;

    private final Map<String, Setter> setters;

    public DataBuilder(WritableSchema<D> schema) {
        this.schema = Objects.requireNonNull(schema);
        this.dataFactory = schema.newDataFactory();
        Map<String, Setter> setters = new HashMap<>();
        for (String name : schema.getFieldNames()) {
            setters.put(name, dataFactory.getSetterNamed(name));
        }
        this.setters = setters;
    }

    public DataBuilder<D> to(Consumer<? super DidoData> consumer) {
        consumer.accept(this.build());
        return this;
    }

    protected WritableSchema<D> getSchema() {
        return schema;
    }

    private Setter getSetterWithNameCheck(String name) {
        Setter setter = setters.get(name);
        if (setter == null) {
            throw new NoSuchFieldException(name, schema);
        }
        return setter;
    }

    public DataBuilder<D> with(String field, Object value) {
        getSetterWithNameCheck(field).set(value);
        return this;
    }

    public DataBuilder<D> withBoolean(String field, boolean value) {
        getSetterWithNameCheck(field).setBoolean(value);
        return this;
    }

    public DataBuilder<D> withByte(String field, byte value) {
        getSetterWithNameCheck(field).setByte(value);
        return this;
    }

    public DataBuilder<D> withChar(String field, char value) {
        getSetterWithNameCheck(field).setChar(value);
        return this;
    }

    public DataBuilder<D> withShort(String field, short value) {
        getSetterWithNameCheck(field).setShort(value);
        return this;
    }

    public DataBuilder<D> withInt(String field, int value) {
        getSetterWithNameCheck(field).setInt(value);
        return this;
    }

    public DataBuilder<D> withLong(String field, long value) {
        getSetterWithNameCheck(field).setLong(value);
        return this;
    }

    public DataBuilder<D> withFloat(String field, float value) {
        getSetterWithNameCheck(field).setFloat(value);
        return this;
    }

    public DataBuilder<D> withDouble(String field, double value) {
        getSetterWithNameCheck(field).setDouble(value);
        return this;
    }

    public DataBuilder<D> withString(String field, String value) {
        getSetterWithNameCheck(field).setString(value);
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
