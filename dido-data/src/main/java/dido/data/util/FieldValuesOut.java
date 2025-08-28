package dido.data.util;

import dido.data.*;

import java.util.*;
import java.util.function.Supplier;

/**
 * Provides the field values of {@link DidoData} as a collection of objects.
 */
public class FieldValuesOut {

    private final DataSchema schema;

    private final FieldGetter[] fieldGetters;

    protected FieldValuesOut(DataSchema dataSchema) {

        this.schema = dataSchema;

        ReadStrategy readStrategy = ReadStrategy.fromSchema(dataSchema);

        fieldGetters = new FieldGetter[dataSchema.lastIndex()];

        for (int i = dataSchema.firstIndex(); i > 0; i = dataSchema.nextIndex(i)) {

            fieldGetters[i - 1] = readStrategy.getFieldGetterAt(i);
        }
    }

    public static FieldValuesOut forSchema(DataSchema schema) {
        return new FieldValuesOut(schema);
    }

    public static Collection<Object> collectionOf(DidoData data) {
        return new FieldValuesOut(data.getSchema()).toCollection(data);
    }

    public static Map<String, Object> mapOf(DidoData data) {
        return new FieldValuesOut(data.getSchema()).toMap(data);
    }

    public Collection<Object> toCollection(DidoData data) {
        return new ValuesCollection(data);
    }

    public Map<String, Object> toMap(DidoData data) {

        return toMap(data, HashMap::new);
    }

    public Map<String, Object> toMap(DidoData data, Supplier<? extends Map<String, Object>> supplier) {
        Map<String, Object> map = supplier.get();
        for (SchemaField schemaField: schema.getSchemaFields()) {

            map.put(schemaField.getName(),
                    fieldGetters[schemaField.getIndex() -1].get(data));
        }
        return map;
    }

    class ValuesCollection implements Collection<Object> {

        private final DidoData data;

        ValuesCollection(DidoData data) {
            this.data = data;
        }

        @Override
        public Iterator<Object> iterator() {
            return new Iterator<>() {
                int i = 0;
                @Override
                public boolean hasNext() {
                    return i < fieldGetters.length;
                }

                @Override
                public Object next() {
                    return fieldGetters[i++].get(data);
                }
            };
        }

        @Override
        public int size() {
            return schema.getSize();
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean contains(Object o) {
            for (FieldGetter getter : fieldGetters) {
                if (Objects.equals(getter.get(data), o)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object[] toArray() {
            Object[] array = new Object[fieldGetters.length];
            for (int i = 0; i < array.length; ++i) {
                array[i] = fieldGetters[i].get(data);
            }
            return array;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T[] toArray(T[] a) {
            int size = fieldGetters.length;
            T[] r = a.length >= size ? a :
                    (T[])java.lang.reflect.Array
                            .newInstance(a.getClass().getComponentType(), size);

            for (int i = 0; i < r.length; ++i) {
                r[i] = (T) fieldGetters[i].get(data);
            }

            for (int i = size; i < r.length; ++i) {
                r[i] = null;
            }

            return r;
        }

        @Override
        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {

            for (Object o : c) {
                if (!contains(o)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ValuesCollection) {
                return data.equals(((ValuesCollection) data).data);
            }
            else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return data.hashCode();
        }

        @Override
        public String toString() {
            return Arrays.toString(toArray());
        }
    }
}
