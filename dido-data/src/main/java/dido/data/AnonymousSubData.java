package dido.data;

import java.util.function.Function;

/**
 * Provide anonymous data that is a subset of some other data.
 *
 */
public class AnonymousSubData {


    private static class MappingFunc implements Function<IndexedData, AnonymousData> {

        private final int[] indices;

        private IndexedSchema lastSchema;

        private Function<IndexedData, AnonymousData> func;

        private MappingFunc(int[] indices) {
            this.indices = indices;
        }

        @Override
        public AnonymousData apply(IndexedData original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                func = AnonymousDatas.partialCopyOfSchema(lastSchema, indices);
            }
            return func.apply(original);
        }
    }

    public static class FieldMappingFunc implements Function<DidoData, AnonymousData> {

        private final String[] fields;

        private DataSchema lastSchema;

        private Function<IndexedData, AnonymousData> func;

        private FieldMappingFunc(String[] fields) {
            this.fields = fields;
        }

        @Override
        public AnonymousData apply(DidoData original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                int[] indices = new int[fields.length];
                for (int i = 0; i < indices.length; ++i) {
                    String field = fields[i];
                    int index = lastSchema.getIndexNamed(field);
                    if (index == 0) {
                        throw new IllegalArgumentException("No field named " + field);
                    }
                    indices[i] = index;
                }
                func = AnonymousDatas.partialCopyOfSchema(lastSchema, indices);
            }
            return func.apply(original);
        }
    }

    public static class Configuration {

        public Function<IndexedData, AnonymousData> andIndices(int... indices) {

            return new MappingFunc(indices);
        }

        public Function<DidoData, AnonymousData> andFields(String... names) {

            return new FieldMappingFunc(names);
        }
    }

    public static Configuration with() {
        return new Configuration();
    }

    public static Function<IndexedData, AnonymousData> ofIndices(int... indices) {

        return new MappingFunc(indices);
    }

    public static Function<DidoData, AnonymousData> ofFields(String... fields) {

        return new FieldMappingFunc(fields);
    }

}
