package dido.operators;

import dido.data.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Flatten {

    public static <F> List<GenericData<F>> flatten(F field, IndexedData<F> data) {
        return new DynamicFlatten<>(extractorForFieldOrIndex(field, 0)).apply(data);
    }

    public static <F> List<GenericData<F>> flattenAt(int index, IndexedData<F> data) {
        return new DynamicFlatten<>(extractorForFieldOrIndex((F) null, 0)).apply(data);
    }

    public static <F> Function<IndexedData<F>, List<GenericData<F>>> field(F field) {

        return new DynamicFlatten<>(extractorForFieldOrIndex(field, 0));
    }

    public static <F> Function<IndexedData<F>, List<GenericData<F>>> fields(F... fields) {

        Collection<Extractor<F>> extractors = Arrays.stream(fields)
                .map(f -> extractorForFieldOrIndex(f, 0))
                .collect(Collectors.toList());

        return new DynamicIterableFlatten<>(extractors);
    }

    public static <F> Function<IndexedData<F>, List<GenericData<F>>> indices(int... indices) {

        Collection<Extractor<F>> extractors = Arrays.stream(indices)
                .mapToObj(i -> (Extractor<F>) extractorForFieldOrIndex(null, i))
                .collect(Collectors.toList());

        return new DynamicIterableFlatten<>(extractors);
    }

    public static <F> Function<IndexedData<F>, List<GenericData<F>>> fieldOfSchema(F field,
                                                                                   DataSchema<F> schema) {
        return fieldOrIndexOfSchema(field, 0, schema);
    }

    public static <F> Function<IndexedData<F>, List<GenericData<F>>> fieldOrIndexOfSchema(F field,
                                                                                          int index,
                                                                                          DataSchema<F> schema) {

        Extractor<F> extractor = extractorForFieldOrIndex(field, index);

        return extractorOfSchema(extractor, schema);
    }

    static <F> Function<IndexedData<F>, List<GenericData<F>>> extractorOfSchema(Extractor<F> extractor,
                                                                                          DataSchema<F> schema) {

        DataSchema<F> nestedSchema = Objects.requireNonNull(extractor.getSchema(schema),
                "No Nested Schema for " + extractor );

        Concatenator<F> concatenator = extractor.bodgeFields(Concatenator.withSettings())
                .makeFromSchemas(schema, nestedSchema);

        return new KnownRepeatingFlatten<>(concatenator, extractor);
    }


    static class KnownRepeatingFlatten<F> implements Function<IndexedData<F>, List<GenericData<F>>> {

        private final Concatenator<F> concatenator;

        private final Extractor<F> extractor;

        KnownRepeatingFlatten(Concatenator<F> concatenator, Extractor<F> extractor) {
            this.concatenator = concatenator;
            this.extractor = extractor;
        }

        @Override
        public List<GenericData<F>> apply(IndexedData<F> data) {

            @SuppressWarnings("unchecked")
            RepeatingData<F> nested = (RepeatingData<F>) extractor.extract(data);

            List<GenericData<F>> flattened = new ArrayList<>(nested.size());
            for (IndexedData<F> element : nested) {
                flattened.add(concatenator.concat(data, element));
            }

            return flattened;
        }
    }

    static <F> Function<IndexedData<F>, List<GenericData<F>>> strategyFlatten(DataSchema<F> schema,
                                                                              Collection<Extractor<F>> extractors) {

        Map<Integer, Extractor<F>> extractorMap = new HashMap<>();
        SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();

        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {

            Extractor<F> extractor = null;

            for (Extractor<F> e : extractors) {

                if (e.isForIndexInSchema(index, schema)) {
                    extractor = e;
                    break;
                }
            }

            SchemaField<F> existingSchemaField = schema.getSchemaFieldAt(index);
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

        return new KnownIterableFlatten<>(extractorMap, schema, schemaBuilder.build());
    }


    static class KnownIterableFlatten<F> implements Function<IndexedData<F>, List<GenericData<F>>> {

        private final Map<Integer, Extractor<F>> extractors;

        private final DataSchema<F> schema;

        private final DataSchema<F> newSchema;

        KnownIterableFlatten(Map<Integer, Extractor<F>> extractors, DataSchema<F> schema, DataSchema<F> newSchema) {
            this.extractors = extractors;
            this.schema = schema;
            this.newSchema = newSchema;
        }

        @Override
        public List<GenericData<F>> apply(IndexedData<F> data) {

            int maxSize = 1;

            Map<Integer, List<Object>> lists = new HashMap<>(extractors.size());

            for (Map.Entry<Integer, Extractor<F>> entry : extractors.entrySet()) {

                int index = entry.getKey();
                Extractor<F> extractor = entry.getValue();

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

            List<GenericData<F>> flattened = new ArrayList<>(maxSize);

            for (int l = 0; l < maxSize; ++l) {

                ArrayData.Builder<F> arrayData = ArrayData.builderForSchema(newSchema);

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
     * @param <F> Field Type.
     */
    public static class DynamicIterableFlatten<F> implements Function<IndexedData<F>, List<GenericData<F>>> {

        private final Collection<Extractor<F>> extractors;

        private Function<IndexedData<F>, List<GenericData<F>>> last;

        private DataSchema<F> previous;

        public DynamicIterableFlatten(Collection<Extractor<F>> extractors) {
            this.extractors = extractors;
        }


        @Override
        public List<GenericData<F>> apply(IndexedData<F> indexedData) {

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
     * @param <F> Field Type.
     */
    public static class DynamicFlatten<F> implements Function<IndexedData<F>, List<GenericData<F>>> {

        private final Extractor<F> extractor;

        private Function<IndexedData<F>, List<GenericData<F>>> last;

        private DataSchema<F> previous;

        public DynamicFlatten(Extractor<F> extractor) {
            this.extractor = extractor;
        }

        @Override
        public List<GenericData<F>> apply(IndexedData<F> indexedData) {

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
