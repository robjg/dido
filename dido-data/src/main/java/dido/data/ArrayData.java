package dido.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * {@link GenericData} stored in an Array.
 *
 * @param <F> The field type.
 */
public class ArrayData<F> extends AbstractGenericData<F> implements GenericData<F> {

    private final DataSchema<F> schema;

    private final Object[] data;

    private volatile int hash = 0;

    private ArrayData(DataSchema<F> schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static <T> GenericData<T> of(Object... data) {
        Objects.requireNonNull(data);

        DataSchema<T> schema = new DataSchema<>() {

            @Override
            public SchemaField<T> getSchemaFieldAt(int index) {
                return SchemaFields.of(index, Object.class);
            }

            @Override
            public T getFieldAt(int index) {
                return null;
            }

            @Override
            public Class<?> getTypeAt(int index) {
                return index > 0 && index <= data.length ? Object.class : null;
            }

            @Override
            public <N> DataSchema<N> getSchemaAt(int index) {
                return null;
            }

            @Override
            public int getIndex(T field) {
                return 0;
            }

            @Override
            public int firstIndex() {
                return data.length > 0 ? 1 : 0;
            }

            @Override
            public int nextIndex(int index) {
                return index > 0 && index < data.length ? index + 1 : 0;
            }

            @Override
            public int lastIndex() {
                return data.length;
            }

            @Override
            public Collection<T> getFields() {
                return Collections.emptyList();
            }

            @Override
            public int hashCode() {
                return DataSchema.hashCode(this);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof DataSchema) {
                    return DataSchema.equals(this, (DataSchema<?>) obj);
                } else {
                    return false;
                }
            }

            @Override
            public String toString() {
                return DataSchema.toString(this);
            }
        };

        return new ArrayData<>(schema, data);
    }

    public static <T> Builder<T> builderForSchema(DataSchema<T> schema) {

        return new Builder<>(schema);
    }

    public static <T> Values<T> valuesFor(DataSchema<T> schema) {

        return new Values<>(schema);
    }

    @Override
    public DataSchema<F> getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        return data[index -1];
    }

    @Override
    public boolean hasIndex(int index) {
        return data[index -1] != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexedData) {
            return IndexedData.equals(this, (IndexedData<?>) obj);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = IndexedData.hashCode(this);
        }
        return hash;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }

    public static class Builder<F> {

        private final DataSchema<F> schema;

        private Object[] values;

        Builder(DataSchema<F> schema) {
            this.schema = Objects.requireNonNull(schema);
            values = new Object[schema.lastIndex()];
        }

        public Builder<F> setAt(int index, Object value) {
            values[index - 1] = value;
            return this;
        }

        public GenericData<F> build() {
            Object[] values = this.values;
            this.values = new Object[schema.lastIndex()];
            return new ArrayData<>(schema, values);
        }

        public GenericData<F> build(Object... values) {
            return new ArrayData<>(schema, values);
        }
    }

    public static class Values<F> {

        private final DataSchema<F> schema;

        Values(DataSchema<F> schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        public GenericData<F> of(Object... values) {
            return new ArrayData<>(schema, Arrays.copyOf(values, schema.lastIndex()));
        }
    }
}
