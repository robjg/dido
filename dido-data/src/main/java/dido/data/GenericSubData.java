package dido.data;

import java.util.function.Function;

/**
 * Provide data that is a subset of some other data.
 *
 * @param <F> The field type.
 */
public class GenericSubData<F> extends AbstractGenericData<F> implements GenericData<F> {

    private final GenericDataSchema<F> dataSchema;

    private final int[] indices;
    private final GenericData<F> original;


    private GenericSubData(GenericDataSchema<F> dataSchema, int[] indices, GenericData<F> original) {
        this.dataSchema = dataSchema;
        this.indices = indices;
        this.original = original;
    }

    private static class MappingFunc<F> implements Function<GenericData<F>, GenericData<F>> {

        private final int[] indices;

        private final boolean withFields;

        private GenericDataSchema<F> lastSchema;

        private GenericDataSchema<F> subSchema;
        private MappingFunc(int[] indices) {
            this(indices, false);
        }

        private MappingFunc(int[] indices, boolean withFields) {
            this.indices = indices;
            this.withFields = withFields;
        }

        @Override
        public GenericData<F> apply(GenericData<F> original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();
                for (int index : indices) {
                    if (withFields) {
                        schemaBuilder.addField(lastSchema.getFieldAt(index),
                                lastSchema.getTypeAt(index));
                    } else {
                        schemaBuilder.add(lastSchema.getTypeAt(index));
                    }
                }
                subSchema = schemaBuilder.build();
            }
            return new GenericSubData<>(subSchema, indices, original);
        }
    }

    public static class FieldMappingFunc<F> implements Function<GenericData<F>, GenericData<F>> {

        private final F[] fields;

        private final boolean withFields;
        private GenericDataSchema<F> lastSchema;

        private GenericDataSchema<F> subSchema;

        private int[] indices;

        private FieldMappingFunc(F[] fields) {
            this(fields, false);
        }

        private FieldMappingFunc(F[] fields, boolean withFields) {
            this.fields = fields;
            this.withFields = withFields;
        }
        @Override
        public GenericData<F> apply(GenericData<F> original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                indices = new int[fields.length];
                SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();
                for (int i = 0; i < indices.length; ++i) {
                    F field = fields[i];
                    int index = lastSchema.getIndex(field);
                    indices[i] = index;
                    if (withFields) {
                        schemaBuilder.addField(field, lastSchema.getTypeOf(field));
                    }
                    else {
                        schemaBuilder.add(lastSchema.getTypeOf(field));
                    }
                }
                subSchema = schemaBuilder.build();
            }
            return new GenericSubData<>(subSchema, indices, original);
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

        public Function<GenericData<F>, GenericData<F>> andIndices(int... indices) {

            return new MappingFunc<>(indices, fields);
        }

        public Function<GenericData<F>, GenericData<F>> andFields(F... fields) {

            return new FieldMappingFunc<>(fields);
        }
    }

    public static <F> Configuration<F> with() {
        return new Configuration<>();
    }

    public static <F> Configuration<F> withFields() {
        return new Configuration<F>().fields();
    }

    public static <F> Function<GenericData<F>, GenericData<F>> ofIndices(int... indices) {

        return new MappingFunc<>(indices);
    }

    public static <F> Function<GenericData<F>, GenericData<F>> ofFields(F... fields) {

        return new FieldMappingFunc<>(fields);
    }
    @Override
    public GenericDataSchema<F> getSchema() {
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
