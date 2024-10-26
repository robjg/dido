package dido.data.util;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.ReadStrategy;

import java.util.*;

public class FieldValuesOut {

    private final List<FieldGetter> fieldGetters;

    protected FieldValuesOut(DataSchema dataSchema) {

        ReadStrategy readStrategy = ReadStrategy.fromSchema(dataSchema);

        fieldGetters = new ArrayList<>(dataSchema.lastIndex());

        for (int i = dataSchema.firstIndex(); i > 0; i = dataSchema.nextIndex(i)) {

            fieldGetters.add(readStrategy.getFieldGetterAt(i));
        }
    }

    public static FieldValuesOut forSchema(DataSchema schema) {
        return new FieldValuesOut(schema);
    }

    public static Collection<Object> collectionOf(DidoData data) {
        return new FieldValuesOut(data.getSchema()).toCollection(data);
    }

    public Collection<Object> toCollection(DidoData data) {
        return new ValuesCollection(data);
    }

    class ValuesCollection implements Collection<Object> {

        private final DidoData data;

        ValuesCollection(DidoData data) {
            this.data = data;
        }

        @Override
        public Iterator<Object> iterator() {
            Iterator<FieldGetter> iterator = fieldGetters.iterator();
            return new Iterator<Object>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Object next() {
                    return iterator.next().get(data);
                }
            };
        }

        @Override
        public int size() {
            return fieldGetters.size();
        }

        @Override
        public boolean isEmpty() {
            return fieldGetters.isEmpty();
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
            Object[] array = new Object[fieldGetters.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = fieldGetters.get(i).get(data);
            }
            return array;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T[] toArray(T[] a) {
            int size = fieldGetters.size();
            T[] r = a.length >= size ? a :
                    (T[])java.lang.reflect.Array
                            .newInstance(a.getClass().getComponentType(), size);

            for (int i = 0; i < r.length; ++i) {
                r[i] = (T) fieldGetters.get(i).get(data);
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
