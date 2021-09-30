package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.GenericData;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class RenamedData<F, T> implements GenericData<T> {

    private final DataSchema<T> toSchema;

    private final GenericData<F> original;

    public RenamedData(DataSchema<T> toSchema, GenericData<F> original) {
        this.toSchema = toSchema;
        this.original = original;
    }

    static class Transform<F, T> implements Function<GenericData<F>, GenericData<T>> {

        private final Map<F, T> fieldMap;

        private DataSchema<T> schemaOut;

        private DataSchema<F> lastIn;

        Transform(Map<F, T> fieldMap) {
            this.fieldMap = fieldMap;
        }

        @Override
        public GenericData<T> apply(GenericData<F> dataIn) {
            if (schemaOut == null || lastIn != dataIn.getSchema()) {
                lastIn = dataIn.getSchema();
                schemaOut = renamedSchema(fieldMap, lastIn);
            }

            return new RenamedData<>(schemaOut, dataIn);
        }
    }

    public static <F, T> TransformBuilder<F, T> transformer() {

        return new TransformBuilder<>(t -> {
            throw new IllegalArgumentException("Name Clash " + t);
        });
    }

    public static <F, T> TransformBuilder<F, T> transformerWithNameClash(Function<T, T> policy) {

        return new TransformBuilder<>(policy);
    }

    public static class TransformBuilder<F, T> {

        private Map<F, T> map = new LinkedHashMap<>();

        private final Function<T, T> nameClash;

        public TransformBuilder(Function<T, T> nameClash) {
            this.nameClash = nameClash;
        }


        public Function<GenericData<F>, GenericData<T>> build() {
            Map<F, T> copy = this.map;
            this.map = new LinkedHashMap<>();
            return new Transform<>(copy);
        }

        public TransformBuilder<F, T> addMapping(F from, T to) {
            if (map.containsValue(to)) {
                return addMapping(from, nameClash.apply(to));
            }
            else {
                map.put(from, to);
                return this;
            }
        }
    }

    @Override
    public DataSchema<T> getSchema() {
        return toSchema;
    }

    @Override
    public <T1> T1 getObjectAt(int index, Class<T1> type) {
        return original.getObjectAt(index, type);
    }

    @Override
    public boolean hasIndex(int index) {
        return original.hasIndex(index);
    }

    static <F, T> DataSchema<T> renamedSchema(Map<F, T> mapping, DataSchema<F> fromSchema) {

        @SuppressWarnings("unchecked")
        T[] fields = (T[]) new Object[fromSchema.lastIndex()];
        Map<T, Integer> indexes = new LinkedHashMap<>();

        for (Map.Entry<F, T> entry : mapping.entrySet()) {
            int index = fromSchema.getIndex(entry.getKey());
            fields[index - 1] = entry.getValue();
            indexes.put(entry.getValue(),  index);
        }

        return new RenamedSchema<>(indexes, fields, fromSchema);
    }

    static class RenamedSchema<F, T> implements DataSchema<T> {

        private final Map<T, Integer> fieldMap;

        private final T[] toFields;

        private final DataSchema<F> original;

        RenamedSchema(Map<T, Integer> fieldIndexes, T[] toFields, DataSchema<F> original) {
            this.fieldMap = fieldIndexes;
            this.original = original;
            this.toFields = toFields;
        }

        @Override
        public T getFieldAt(int index) {
            return toFields[index - 1];
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return original.getTypeAt(index);
        }

        @Override
        public <N> DataSchema<N> getSchemaAt(int index) {
            return original.getSchemaAt(index);
        }

        @Override
        public int getIndex(T field) {
            return this.fieldMap.get(field);
        }

        @Override
        public int firstIndex() {
            return original.firstIndex();
        }

        @Override
        public int nextIndex(int index) {
            return original.nextIndex(index);
        }

        @Override
        public int lastIndex() {
            return original.lastIndex();
        }

        @Override
        public Collection<T> getFields() {
            return this.fieldMap.keySet();
        }
    }



}
