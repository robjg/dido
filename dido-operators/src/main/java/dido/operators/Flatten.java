package dido.operators;

import dido.data.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Flatten {

    public static List<DidoData> flatten(String field, DidoData data) {
        return new DynamicFlatten(extractorForFieldOrIndex(field, 0)).apply(data);
    }

    public static List<DidoData> flattenAt(int index, DidoData data) {
        return new DynamicFlatten(extractorForFieldOrIndex((String) null, 0)).apply(data);
    }

    public static Function<DidoData, List<DidoData>> field(String field) {

        return new DynamicFlatten(extractorForFieldOrIndex(field, 0));
    }

    public static Function<DidoData, List<DidoData>> fields(String... fields) {

        Collection<Extractor> extractors = Arrays.stream(fields)
                .map(f -> extractorForFieldOrIndex(f, 0))
                .collect(Collectors.toList());

        return new DynamicIterableFlatten(extractors);
    }

    public static Function<DidoData, List<DidoData>> indices(int... indices) {

        Collection<Extractor> extractors = Arrays.stream(indices)
                .mapToObj(i -> extractorForFieldOrIndex(null, i))
                .collect(Collectors.toList());

        return new DynamicIterableFlatten(extractors);
    }

    public static Function<DidoData, List<DidoData>> fieldOfSchema(String field,
                                                                      DataSchema schema) {
        return fieldOrIndexOfSchema(field, 0, schema);
    }

    public static Function<DidoData, List<DidoData>> fieldOrIndexOfSchema(String field,
                                                                             int index,
                                                                             DataSchema schema) {

        Extractor extractor = extractorForFieldOrIndex(field, index);

        return extractorOfSchema(extractor, schema);
    }

    static Function<DidoData, List<DidoData>> extractorOfSchema(Extractor extractor,
                                                                DataSchema schema) {

        DataSchema nestedSchema = Objects.requireNonNull(extractor.getSchema(schema),
                "No Nested Schema for " + extractor);

        Concatenator concatenator = extractor.bodgeFields(Concatenator.withSettings())
                .makeFromSchemas(schema, nestedSchema);

        return new KnownRepeatingFlatten(concatenator, extractor);
    }


    static class KnownRepeatingFlatten implements Function<DidoData, List<DidoData>> {

        private final Concatenator concatenator;

        private final Extractor extractor;

        KnownRepeatingFlatten(Concatenator concatenator, Extractor extractor) {
            this.concatenator = concatenator;
            this.extractor = extractor;
        }

        @Override
        public List<DidoData> apply(DidoData data) {

            RepeatingData nested = (RepeatingData) extractor.extract(data);

            List<DidoData> flattened = new ArrayList<>(nested.size());
            for (DidoData element : nested) {
                flattened.add(concatenator.concat(data, element));
            }

            return flattened;
        }
    }

    static Function<DidoData, List<DidoData>> strategyFlatten(DataSchema schema,
                                                              Collection<Extractor> extractors) {

        Map<Integer, Extractor> extractorMap = new HashMap<>();
        SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {

            Extractor extractor = null;

            for (Extractor e : extractors) {

                if (e.isForIndexInSchema(index, schema)) {
                    extractor = e;
                    break;
                }
            }

            SchemaField existingSchemaField = schema.getSchemaFieldAt(index);
            if (extractor == null) {

                schemaBuilder.addSchemaField(existingSchemaField);
            } else {

                extractorMap.put(index, extractor);

                Class<?> type = schema.getTypeAt(index);
                Class<?> newType;
                if (type.isArray()) {
                    newType = Primitives.wrap(type.getComponentType());
                } else {
                    newType = Object.class;
                }
                schemaBuilder.addSchemaField(
                        SchemaField.of(existingSchemaField.getIndex(), existingSchemaField.getName(), newType));
            }
        }

        return new KnownIterableFlatten(extractorMap, schema, schemaBuilder.build());
    }


    static class KnownIterableFlatten implements Function<DidoData, List<DidoData>> {

        private final Map<Integer, Extractor> extractors;

        private final DataSchema schema;

        private final DataSchema newSchema;

        KnownIterableFlatten(Map<Integer, Extractor> extractors, DataSchema schema, DataSchema newSchema) {
            this.extractors = extractors;
            this.schema = schema;
            this.newSchema = newSchema;
        }

        @Override
        public List<DidoData> apply(DidoData data) {

            int maxSize = 1;

            Map<Integer, List<Object>> lists = new HashMap<>(extractors.size());

            for (Map.Entry<Integer, Extractor> entry : extractors.entrySet()) {

                int index = entry.getKey();
                Extractor extractor = entry.getValue();

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
                        } else if (component == double.class) {
                            double[] da = (double[]) value;
                            list = Arrays.stream(da).mapToObj(Double::valueOf).collect(Collectors.toList());

                        } else if (component == long.class) {
                            long[] la = (long[]) value;
                            list = Arrays.stream(la).mapToObj(Long::valueOf).collect(Collectors.toList());

                        } else {
                            throw new IllegalArgumentException("No implemented " + type);
                        }
                    } else {
                        list = Arrays.asList((Object[]) value);
                    }
                } else {
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
                        arrayData.withAt(i, data.getAt(i));
                    } else {
                        if (l < list.size()) {
                            arrayData.withAt(i, list.get(l));
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
     */
    public static class DynamicIterableFlatten implements Function<DidoData, List<DidoData>> {

        private final Collection<Extractor> extractors;

        private Function<? super DidoData, ? extends List<DidoData>> last;

        private DataSchema previous;

        public DynamicIterableFlatten(Collection<Extractor> extractors) {
            this.extractors = extractors;
        }

        @Override
        public List<DidoData> apply(DidoData indexedData) {

            if (last == null || !indexedData.getSchema().equals(previous)) {
                previous = indexedData.getSchema();
                last = strategyFlatten(previous, extractors);
            }

            return last.apply(indexedData);
        }
    }

    /**
     * Compares previous schemas so we can maybe shortcut.
     */
    public static class DynamicFlatten implements Function<DidoData, List<DidoData>> {

        private final Extractor extractor;

        private Function<? super DidoData, ? extends List<DidoData>> last;

        private DataSchema previous;

        public DynamicFlatten(Extractor extractor) {
            this.extractor = extractor;
        }

        @Override
        public List<DidoData> apply(DidoData indexedData) {

            if (last == null || !indexedData.getSchema().equals(previous)) {
                previous = indexedData.getSchema();
                last = extractorOfSchema(extractor, previous);
            }

            return last.apply(indexedData);
        }
    }

    interface Extractor {

        Object extract(DidoData data);

        Class<?> getType(DataSchema schema);

        DataSchema getSchema(DataSchema schema);

        Concatenator.Settings bodgeFields(Concatenator.Settings settings);

        boolean isForIndexInSchema(int index, DataSchema schema);

    }

    static Extractor extractorForFieldOrIndex(String field, int index) {

        if (field == null && index == 0) {
            throw new IllegalStateException("Field Or Index must be provided");
        }

        if (field == null) {
            return new IndexExtractor(index);
        } else {
            return new FieldExtractor(field);
        }
    }

    static class FieldExtractor implements Extractor {

        private final String field;

        FieldExtractor(String field) {
            this.field = field;
        }

        @Override
        public Object extract(DidoData data) {
            return data.getNamed(field);
        }

        @Override
        public Class<?> getType(DataSchema schema) {
            return schema.getTypeNamed(field);
        }

        @Override
        public DataSchema getSchema(DataSchema schema) {
            return schema.getSchemaNamed(field);
        }

        @Override
        public Concatenator.Settings bodgeFields(Concatenator.Settings settings) {
            return settings.excludeFields(field);
        }

        @Override
        public boolean isForIndexInSchema(int index, DataSchema schema) {
            return schema.getIndexNamed(field) == index;
        }

        @Override
        public String toString() {
            return "Field=" + field;
        }
    }

    static class IndexExtractor implements Extractor {

        private final int index;

        IndexExtractor(int index) {
            this.index = index;

        }

        @Override
        public Object extract(DidoData data) {
            return data.getAt(index);
        }

        @Override
        public Class<?> getType(DataSchema schema) {
            return schema.getTypeAt(index);
        }

        @Override
        public DataSchema getSchema(DataSchema schema) {
            return schema.getSchemaAt(index);
        }

        @Override
        public Concatenator.Settings bodgeFields(Concatenator.Settings settings) {
            return settings;
        }

        @Override
        public boolean isForIndexInSchema(int index, DataSchema schema) {
            return index == this.index;
        }

        @Override
        public String toString() {
            return "Index=" + index;
        }
    }
}
