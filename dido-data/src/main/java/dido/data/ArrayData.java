package dido.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class ArrayData<T> implements GenericData<T> {

    private final DataSchema<T> schema;

    private final Object[] data;

    private volatile int hash = 0;

    private ArrayData(DataSchema<T> schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static <T> GenericData<T> of(Object... data) {
        Objects.requireNonNull(data);

        DataSchema<T> schema = new DataSchema<T>() {

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
                }
                else {
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

    @Override
    public DataSchema<T> getSchema() {
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
    public boolean equals(Object o) {
        if (o instanceof IndexedData) {
            return IndexedData.equals(this, (IndexedData<?>) o);
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
        return IndexedData.toString(this);
    }
}
