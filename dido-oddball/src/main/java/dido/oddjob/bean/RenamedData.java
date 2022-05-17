package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.IndexedData;
import dido.data.SchemaBuilder;

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
    public Object getAt(int index) {
        return original.getAt(index);
    }

    @Override
    public boolean hasIndex(int index) {
        return original.hasIndex(index);
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
        return IndexedData.hashCode(this);
    }

    @Override
    public String toString() {
        return GenericData.toString(this);
    }

    static <F, T> DataSchema<T> renamedSchema(Map<F, T> mapping, DataSchema<F> fromSchema) {

        return fromSchema.getSchemaFields().stream()
                .reduce(SchemaBuilder.<T>impliedType(),
                        (b, sf) -> b.addSchemaField(sf.mapToField(mapping.get(sf.getField()))),
                        (b1, b2) -> b1)
                .build();
    }
}
