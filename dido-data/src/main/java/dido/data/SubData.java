package dido.data;

import java.util.function.Function;

/**
 * Provide data that is a subset of some other data.
 */
public class SubData extends AbstractData implements DidoData {

    private final DataSchema dataSchema;

    private final int[] indices;
    private final IndexedData original;


    private SubData(DataSchema dataSchema, int[] indices, IndexedData original) {
        this.dataSchema = dataSchema;
        this.indices = indices;
        this.original = original;
    }

    private static class MappingFunc implements Function<DidoData, DidoData> {

        private final int[] indices;

        private DataSchema lastSchema;

        private DataSchema subSchema;

        private MappingFunc(int[] indices) {
            this.indices = indices;
        }

        @Override
        public DidoData apply(DidoData original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();
                for (int index : indices) {
                    schemaBuilder.addNamed(lastSchema.getFieldNameAt(index),
                            lastSchema.getTypeAt(index));
                }
                subSchema = schemaBuilder.build();
            }
            return new SubData(subSchema, indices, original);
        }
    }

    public static class FieldMappingFunc implements Function<DidoData, DidoData> {

        private final String[] fields;

        private final boolean withFields;

        private DataSchema lastSchema;

        private DataSchema subSchema;

        private int[] indices;

        private FieldMappingFunc(String[] fields) {
            this(fields, false);
        }

        private FieldMappingFunc(String[] fields, boolean withFields) {
            this.fields = fields;
            this.withFields = withFields;
        }

        @Override
        public DidoData apply(DidoData original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                indices = new int[fields.length];
                SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();
                for (int i = 0; i < indices.length; ++i) {
                    String field = fields[i];
                    int index = lastSchema.getIndexNamed(field);
                    indices[i] = index;
                    if (withFields) {
                        schemaBuilder.addNamed(field, lastSchema.getTypeNamed(field));
                    } else {
                        schemaBuilder.add(lastSchema.getTypeNamed(field));
                    }
                }
                subSchema = schemaBuilder.build();
            }
            return new SubData(subSchema, indices, original);
        }
    }

    public static class Configuration {

        private boolean withFields;

        public Configuration fields(boolean withFields) {
            this.withFields = withFields;
            return this;
        }

        public Configuration fields() {
            return fields(true);
        }

        public Function<DidoData, DidoData> andIndices(int... indices) {

            return new MappingFunc(indices);
        }

        public Function<DidoData, DidoData> andFields(String... fields) {

            return new FieldMappingFunc(fields, this.withFields);
        }
    }

    public static Configuration with() {
        return new Configuration();
    }

    public static Function<DidoData, DidoData> ofIndices(int... indices) {

        return new MappingFunc(indices);
    }

    public static Function<DidoData, DidoData> ofFields(String... fields) {

        return new FieldMappingFunc(fields);
    }

    @Override
    public DataSchema getSchema() {
        return dataSchema;
    }

    @Override
    public Object getAt(int index) {
        return original.getAt(indices[index - 1]);
    }

    @Override
    public boolean hasIndex(int index) {
        return original.hasIndex(indices[index - 1]);
    }

}
