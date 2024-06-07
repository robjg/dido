package dido.operators;

import dido.data.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Flatten {

    public static  List<DidoData> flatten(String field, IndexedData<String> data) {
        return new DynamicFlatten(extractorForFieldOrIndex(field, 0)).apply(data);
    }

    public static List<DidoData> flattenAt(int index, IndexedData<String> data) {
        return new DynamicFlatten(extractorForFieldOrIndex((String) null, 0)).apply(data);
    }

    public static Function<IndexedData<String>, List<DidoData>> field(String field) {

        return new DynamicFlatten(extractorForFieldOrIndex(field, 0));
    }

    public static Function<IndexedData<String>, List<DidoData>> fields(String... fields) {

        Collection<Extractor<String>> extractors = Arrays.stream(fields)
                .map(f -> extractorForFieldOrIndex(f, 0))
                .collect(Collectors.toList());

        return new DynamicIterableFlatten(extractors);
    }

    public static Function<IndexedData<String>, List<DidoData>> indices(int... indices) {

        Collection<Extractor<String>> extractors = Arrays.stream(indices)
                .mapToObj(i -> extractorForFieldOrIndex((String) null, i))
                .collect(Collectors.toList());

        return new DynamicIterableFlatten(extractors);
    }

    public static Function<IndexedData<String>, List<DidoData>> fieldOfSchema(String field,
                                                                                   DataSchema<String> schema) {
        return fieldOrIndexOfSchema(field, 0, schema);
    }

    public static Function<IndexedData<String>, List<DidoData>> fieldOrIndexOfSchema(String field,
                                                                                          int index,
                                                                                          DataSchema<String> schema) {

        Extractor<String> extractor = extractorForFieldOrIndex(field, index);

        return extractorOfSchema(extractor, schema);
    }

    static Function<IndexedData<String>, List<DidoData>> extractorOfSchema(Extractor<String> extractor,
                                                                                          DataSchema<String> schema) {

        DataSchema<String> nestedSchema = Objects.requireNonNull(extractor.getSchema(schema),
                "No Nested Schema for " + extractor );

        Concatenator<String> concatenator = extractor.bodgeFields(Concatenator.withSettings())
                .makeFromSchemas(schema, nestedSchema);

        return new KnownRepeatingFlatten(concatenator, extractor);
    }


    static class KnownRepeatingFlatten implements Function<IndexedData<String>, List<DidoData>> {

        private final Concatenator<String> concatenator;

        private final Extractor<String> extractor;

        KnownRepeatingFlatten(Concatenator<String> concatenator, Extractor<String> extractor) {
            this.concatenator = concatenator;
            this.extractor = extractor;
        }

        @Override
        public List<DidoData> apply(IndexedData<String> data) {

            @SuppressWarnings("unchecked")
            RepeatingData nested = (RepeatingData) extractor.extract(data);

            List<DidoData> flattened = new ArrayList<>(nested.size());
            for (IndexedData<String> element : nested) {
                flattened.add(DidoData.adapt(concatenator.concat(data, element)));
            }

            return flattened;
        }
    }

    static Function<IndexedData<String>, List<DidoData>> strategyFlatten(DataSchema<String> schema,
                                                                              Collection<Extractor<String>> extractors) {

        Map<Integer, Extractor<String>> extractorMap = new HashMap<>();
        SchemaBuilder<String> schemaBuilder = SchemaBuilder.impliedType();

        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {

            Extractor<String> extractor = null;

            for (Extractor<String> e : extractors) {

                if (e.isForIndexInSchema(index, schema)) {
                    extractor = e;
                    break;
                }
            }

            SchemaField<String> existingSchemaField = schema.getSchemaFieldAt(index);
            if (extractor == null) {

                schemaBuilder.addSchemaField(existingSchemaField);
            }
            else {

                extractorMap.put(index, extractor);

                Class<?> type = schema.getTypeAt(index);
                Class<?> newType;
                if (type.isArray()) {
                    newType = Primitives.wrap(type.getComponentType());
                }
                else {
                    newType = Object.class;
                }
                schemaBuilder.addSchemaField(
                        SchemaField.of(1, newType)
                                .mapTo(existingSchemaField.getIndex(), existingSchemaField.getField()));
            }
        }

        return new KnownIterableFlatten(extractorMap, schema, schemaBuilder.build());
    }


    static class KnownIterableFlatten implements Function<IndexedData<String>, List<DidoData>> {

        private final Map<Integer, Extractor<String>> extractors;

        private final DataSchema<String> schema;

        private final DataSchema<String> newSchema;

        KnownIterableFlatten(Map<Integer, Extractor<String>> extractors, DataSchema<String> schema, DataSchema<String> newSchema) {
            this.extractors = extractors;
            this.schema = schema;
            this.newSchema = newSchema;
        }

        @Override
        public List<DidoData> apply(IndexedData<String> data) {

            int maxSize = 1;

            Map<Integer, List<Object>> lists = new HashMap<>(extractors.size());

            for (Map.Entry<Integer, Extractor<String>> entry : extractors.entrySet()) {

                int index = entry.getKey();
                Extractor<String> extractor = entry.getValue();

                Object value = extractor.extract(data);
                if (value == null) {
                    lists.put(index, Collections.emptyList());
                    continue;
                }

                List<Object> list;
                Class<?> type = extractor.getType(schema);
                if (type.isArray()) {
                    Class<?> component = type.getComponentType();
                    if (component.isPrimitive()) {
                        if (component == int.class) {
                            int[] ia = (int[]) value;
                            list = Arrays.stream(ia).mapToObj(Integer::valueOf).collect(Collectors.toList());
                        }
                        else if (component == double.class) {
                            double[] da = (double[]) value;
                            list = Arrays.stream(da).mapToObj(Double::valueOf).collect(Collectors.toList());

                        }
                        else if (component == long.class) {
                            long[] la = (long[]) value;
                            list = Arrays.stream(la).mapToObj(Long::valueOf).collect(Collectors.toList());

                        }
                        else {
                            throw new IllegalArgumentException("No implemented " + type);
                        }
                    }
                    else {
                        list = Arrays.asList((Object[]) value);
                    }
                }
                else {
                    list = StreamSupport.stream(((Iterable<Object>) value).spliterator(), false)
                            .collect(Collectors.toList());
                }
                maxSize = Math.max(maxSize, list.size());
                lists.put(index, list);
            }

            List<DidoData> flattened = new ArrayList<>(maxSize);

            for (int l = 0; l < maxSize; ++l) {

                ArrayData.Builder arrayData = ArrayData.builderForSchema(newSchema);

                for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {

                    List<Object> list = lists.get(i);
                    if (list == null) {
                        arrayData.setAt(i, data.getAt(i));
                    } else {
                        if (l < list.size()) {
                            arrayData.setAt(i, list.get(l));
                        }
                    }
                }

                flattened.add(arrayData.build());
            }

            return flattened;
        }
    }

    /**
     * Compares previous schemas so we can maybe shortcut.
     *
     */
    public static class DynamicIterableFlatten implements Function<IndexedData<String>, List<DidoData>> {

        private final Collection<Extractor<String>> extractors;

        private Function<? super IndexedData<String>, ? extends List<DidoData>> last;

        private DataSchema<String> previous;

        public DynamicIterableFlatten(Collection<Extractor<String>> extractors) {
            this.extractors = extractors;
        }


        @Override
        public List<DidoData> apply(IndexedData<String> indexedData) {

            if (last == null || !indexedData.getSchema().equals(previous)) {
                previous = indexedData.getSchema();
                last = strategyFlatten(previous, extractors);
            }

            return last.apply(indexedData);
        }
    }

    /**
     * Compares previous schemas so we can maybe shortcut.
     *
     */
    public static class DynamicFlatten implements Function<IndexedData<String>, List<DidoData>> {

        private final Extractor<String> extractor;

        private Function<? super IndexedData<String>, ? extends List<DidoData>> last;

        private DataSchema<String> previous;

        public DynamicFlatten(Extractor<String> extractor) {
            this.extractor = extractor;
        }

        @Override
        public List<DidoData> apply(IndexedData<String> indexedData) {

            if (last == null || !indexedData.getSchema().equals(previous)) {
                previous = indexedData.getSchema();
                last = extractorOfSchema(extractor, previous);
            }

            return last.apply(indexedData);
        }
    }

    interface Extractor<F> {

        Object extract(IndexedData<F> data);

        Class<?> getType(DataSchema<F> schema);

        <N> DataSchema<N> getSchema(DataSchema<F> schema);

        Concatenator.Settings<F> bodgeFields(Concatenator.Settings<F> settings);

        boolean isForIndexInSchema(int index, DataSchema<F> schema);

    }

    static <F> Extractor<F> extractorForFieldOrIndex(F field, int index) {

        if (field == null && index == 0) {
            throw new IllegalStateException("Field Or Index must be provided");
        }

        if (field == null) {
            return new IndexExtractor<>(index);
        }
        else {
            return new FieldExtractor<>(field);
        }
    }

    static class FieldExtractor<F> implements Extractor<F> {

        private final F field;

        FieldExtractor(F field) {
            this.field = field;
        }

        @Override
        public Object extract(IndexedData<F> data) {
            return GenericData.from(data).get(field);
        }

        @Override
        public Class<?> getType(DataSchema<F> schema) {
            return schema.getType(field);
        }

        @Override
        public <N> DataSchema<N> getSchema(DataSchema<F> schema) {
            return schema.getSchema(field);
        }

        @Override
        public Concatenator.Settings<F> bodgeFields(Concatenator.Settings<F> settings) {
            return settings.excludeFields(field);
        }

        @Override
        public boolean isForIndexInSchema(int index, DataSchema<F> schema) {
            return schema.getIndex(field) == index;
        }

        @Override
        public String toString() {
            return "Field=" + field;
        }
    }

    static class IndexExtractor<F> implements Extractor<F> {

        private final int index;

        IndexExtractor(int index) {
            this.index = index;

        }

        @Override
        public Object extract(IndexedData<F> data) {
            return data.getAt(index);
        }

        @Override
        public Class<?> getType(DataSchema<F> schema) {
            return schema.getTypeAt(index);
        }

        @Override
        public <N> DataSchema<N> getSchema(DataSchema<F> schema) {
            return schema.getSchemaAt(index);
        }

        @Override
        public Concatenator.Settings<F> bodgeFields(Concatenator.Settings<F> settings) {
            return settings;
        }

        @Override
        public boolean isForIndexInSchema(int index, DataSchema<F> schema) {
            return index == this.index;
        }

        @Override
        public String toString() {
            return "Index=" + index;
        }
    }
}
