package dido.data;

import java.util.function.Function;

/**
 * Provide data that is a subset of some other data.
 *
 * @param <F> The field type.
 */
public class SubData<F> extends AbstractGenericData<F> implements GenericData<F> {

    private final DataSchema<F> dataSchema;

    private final int[] indices;
    private final IndexedData<F> original;


    private SubData(DataSchema<F> dataSchema, int[] indices, IndexedData<F> original) {
        this.dataSchema = dataSchema;
        this.indices = indices;
        this.original = original;
    }

    private static class MappingFunc<F> implements Function<IndexedData<F>, GenericData<F>> {

        private final int[] indices;

        private final boolean withFields;

        private DataSchema<F> lastSchema;

        private DataSchema<F> subSchema;
        private MappingFunc(int[] indices) {
            this(indices, false);
        }

        private MappingFunc(int[] indices, boolean withFields) {
            this.indices = indices;
            this.withFields = withFields;
        }

        @Override
        public GenericData<F> apply(IndexedData<F> original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();
                for (int i = 0; i < indices.length; ++i) {
                    if (withFields) {
                        schemaBuilder.addField(lastSchema.getFieldAt(indices[i]),
                                lastSchema.getTypeAt(indices[i]));
                    }
                    else {
                        schemaBuilder.add(lastSchema.getTypeAt(indices[i]));
                    }
                }
                subSchema = schemaBuilder.build();
            }
            return new SubData<>(subSchema, indices, original);
        }
    }

    public static class FieldMappingFunc<F> implements Function<IndexedData<F>, GenericData<F>> {

        private final F[] fields;

        private final boolean withFields;
        private DataSchema<F> lastSchema;

        private DataSchema<F> subSchema;

        private int[] indices;

        private FieldMappingFunc(F[] fields) {
            this(fields, false);
        }

        private FieldMappingFunc(F[] fields, boolean withFields) {
            this.fields = fields;
            this.withFields = withFields;
        }
        @Override
        public GenericData<F> apply(IndexedData<F> original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                indices = new int[fields.length];
                SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();
                for (int i = 0; i < indices.length; ++i) {
                    F field = fields[i];
                    int index = lastSchema.getIndex(field);
                    indices[i] = index;
                    if (withFields) {
                        schemaBuilder.addField(field, lastSchema.getType(field));
                    }
                    else {
                        schemaBuilder.add(lastSchema.getType(field));
                    }
                }
                subSchema = schemaBuilder.build();
            }
            return new SubData<>(subSchema, indices, original);
        }
    }

    public static class Configuration<F> {

        private boolean fields;

        public Configuration<F> fields(boolean withFields) {
            this.fields = withFields;
            return this;
        }

        public Configuration<F> fields() {
            return fields(true);
        }

        public <F> Function<IndexedData<F>, GenericData<F>> andIndices(int... indices) {

            return new MappingFunc<>(indices, fields);
        }

        public <F> Function<IndexedData<F>, GenericData<F>> andFields(F... fields) {

            return new FieldMappingFunc<>(fields);
        }
    }

    public static <F> Configuration<F> with() {
        return new Configuration<>();
    }

    public static <F> Configuration<F> withFields() {
        return new Configuration<F>().fields();
    }

    public static <F> Function<IndexedData<F>, GenericData<F>> ofIndices(int... indices) {

        return new MappingFunc<>(indices);
    }

    public static <F> Function<IndexedData<F>, GenericData<F>> ofFields(F... fields) {

        return new FieldMappingFunc<>(fields);
    }
    @Override
    public DataSchema<F> getSchema() {
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

    @Override
    public int hashCode() {
        return IndexedData.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IndexedData) {
            return IndexedData.equals(this, (IndexedData<?>) obj);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return IndexedData.toString(this);
    }

}
