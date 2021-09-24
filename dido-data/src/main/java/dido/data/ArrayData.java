package dido.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class ArrayData<T> implements GenericData<T> {

    private final DataSchema<T> schema;

    private final Object[] data;

    private ArrayData(DataSchema<T> schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    public static <T> GenericData<T> of(Object... data) {
        Objects.requireNonNull(data);

        DataSchema<T> schema = new DataSchema<T>() {
            @Override
            public Class<T> getFieldType() {
                throw new UnsupportedOperationException();
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
        };

        return new ArrayData<>(schema, data);
    }

    @Override
    public DataSchema<T> getSchema() {
        return schema;
    }

    @Override
    public <T1> T1 getObjectAt(int index, Class<T1> type) {
        return type.cast(data[index -1]);
    }

    @Override
    public boolean hasIndex(int index) {
        return data[index -1] != null;
    }
}
