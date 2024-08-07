package dido.data.generic;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Provide an {@link GenericData} structure backed by a Map.
 */
public class GenericMapData<F> extends AbstractGenericData<F> {

    private final GenericDataSchema<F> schema;

    private final Map<F, ?> map;

    private GenericMapData(GenericDataSchema<F> schema, Map<F, ?> map) {
        this.schema = schema;
        this.map = map;
    }

    public static <F> Of<F> with(Function<? super String, ? extends F> fieldNameMapping) {
        return new Of<>(fieldNameMapping);
    }

    public static class Of<F> {

        private final Function<? super String, ? extends F> fieldNameMapping;

        public Of(Function<? super String, ? extends F> fieldNameMapping) {
            this.fieldNameMapping = fieldNameMapping;
        }

        public GenericData<F> of() {
            return fromInputs();
        }

        public GenericData<F> of(F f1, Object v1) {
            return fromInputs(f1, v1);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2) {
            return fromInputs(f1, v1, f2, v2);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3) {
            return fromInputs(f1, v1, f2, v2, f3, v3);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                 F f7, Object v7) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                 F f7, Object v7, F f8, Object v8) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                 F f7, Object v7, F f8, Object v8, F f9, Object v9) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9);
        }

        public GenericData<F> of(F f1, Object v1, F f2, Object v2, F f3, Object v3,
                                 F f4, Object v4, F f5, Object v5, F f6, Object v6,
                                 F f7, Object v7, F f8, Object v8, F f9, Object v9,
                                 F f10, Object v10) {
            return fromInputs(f1, v1, f2, v2, f3, v3, f4, v4, f5, v5, f6, v6, f7, v7, f8, v8, f9, v9, f10, v10);
        }

        private GenericData<F> fromInputs(Object... args) {

            BuilderNoSchema<F> builder = new BuilderNoSchema<>(fieldNameMapping);
            for (int i = 0; i < args.length; i = i + 2) {
                //noinspection unchecked
                builder.with((F) args[i], args[i + 1]);
            }
            return builder.build();
        }

        public GenericDataSchema<F> schemaFromMap(Map<F, ?> map) {

            GenericSchemaBuilder<F> schemaBuilder = GenericSchemaBuilder.impliedType(fieldNameMapping);
            for (Map.Entry<F, ?> entry : map.entrySet()) {
                schemaBuilder.addField(entry.getKey(), entry.getValue().getClass());
            }
            return schemaBuilder.build();
        }

        public BuilderNoSchema<F> newBuilderNoSchema() {

            return new BuilderNoSchema<>(fieldNameMapping);
        }
    }


    public static <F> BuilderWithSchema<F> newBuilder(GenericDataSchema<F> schema) {

        return new BuilderWithSchema<>(schema);
    }

    public static <F> GenericDataBuilders.Values<F> valuesFor(GenericDataSchema<F> schema) {

        return new BuilderWithSchema<>(schema).values();
    }

    public static <F> BuilderWithSchema<F> copy(GenericData<F> from) {

        return new BuilderWithSchema<>(from.getSchema()).copy(from);
    }

    @Override
    public Object getAt(int index) {
        return get(schema.getFieldAt(index));
    }

    @Override
    public boolean hasIndex(int index) {
        return this.has(schema.getFieldAt(index));
    }

    @Override
    public Object get(F field) {
        return map.get(field);
    }

    @Override
    public boolean has(F field) {
        return map.containsKey(field);
    }

    @Override
    public GenericDataSchema<F> getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return GenericData.toStringFieldsOnly(this);
    }

    /**
     * A Builder that knows the schema. Setter don't validate the type.
     *
     * @param <F> The field type.
     */
    public static class BuilderWithSchema<F>
            extends GenericDataBuilders.KnownSchema<F, GenericData<F>, BuilderWithSchema<F>> {

        private Map<F, Object> map = new HashMap<>();

        BuilderWithSchema(GenericDataSchema<F> schema) {
            super(schema);
        }

        @Override
        public GenericData<F> build() {
            GenericData<F> data = new GenericMapData<>(getSchema(), map);
            this.map = new HashMap<>();
            return data;
        }

        @Override
        public BuilderWithSchema<F> with(F field, Object value) {
            map.put(field, value);
            return this;
        }

        @Override
        public BuilderWithSchema<F> withAt(int index, Object value) {
            with(getSchema().getFieldAt(index), value);
            return this;
        }
    }

    public static class BuilderNoSchema<F> extends GenericDataBuilders.Fields<F, BuilderNoSchema<F>> {

        private final Function<? super String, ? extends F> fieldNameMapping;

        private Map<F, Object> map = new LinkedHashMap<>();

        private GenericSchemaBuilder<F> schemaBuilder;

        public BuilderNoSchema(Function<? super String, ? extends F> fieldNameMapping) {
            this.fieldNameMapping = fieldNameMapping;
            this.schemaBuilder = GenericSchemaBuilder.impliedType(fieldNameMapping);
        }

        @Override
        public GenericData<F> build() {
            GenericData<F> data = new GenericMapData<>(schemaBuilder.build(), map);
            this.map = new LinkedHashMap<>();
            this.schemaBuilder = GenericSchemaBuilder.impliedType(fieldNameMapping);
            return data;
        }

        @Override
        public BuilderNoSchema<F> with(F field, Object value) {
            map.put(field, value);
            schemaBuilder.addField(field, value == null ? void.class : value.getClass());
            return this;
        }

        @Override
        public BuilderNoSchema<F> withBoolean(F field, boolean value) {
            map.put(field, value);
            schemaBuilder.addField(field, boolean.class);
            return this;
        }

        @Override
        public BuilderNoSchema<F> withByte(F field, byte value) {
            map.put(field, value);
            schemaBuilder.addField(field, byte.class);
            return this;
        }

        @Override
        public BuilderNoSchema<F> withChar(F field, char value) {
            map.put(field, value);
            schemaBuilder.addField(field, char.class);
            return this;
        }

        @Override
        public BuilderNoSchema<F> withShort(F field, short value) {
            map.put(field, value);
            schemaBuilder.addField(field, short.class);
            return this;
        }

        @Override
        public BuilderNoSchema<F> withInt(F field, int value) {
            map.put(field, value);
            schemaBuilder.addField(field, int.class);
            return this;
        }

        @Override
        public BuilderNoSchema<F> withLong(F field, long value) {
            map.put(field, value);
            schemaBuilder.addField(field, long.class);
            return this;
        }

        @Override
        public BuilderNoSchema<F> withFloat(F field, float value) {
            map.put(field, value);
            schemaBuilder.addField(field, float.class);
            return this;
        }

        @Override
        public BuilderNoSchema<F> withDouble(F field, double value) {
            map.put(field, value);
            schemaBuilder.addField(field, double.class);
            return this;
        }

        @Override
        public BuilderNoSchema<F> withString(F field, String value) {
            map.put(field, value);
            schemaBuilder.addField(field, String.class);
            return this;
        }

    }

    /**
     * For A fluent way of providing MapData
     *
     * @param <F> The field type.
     */
    public static class Values<F> {

        private final GenericDataSchema<F> schema;

        Values(GenericDataSchema<F> schema) {
            this.schema = Objects.requireNonNull(schema);
        }

        public GenericData<F> of(Object... values) {
            Map<F, Object> map = new HashMap<>();
            for (int i = 0; i < values.length; ++i) {
                F field = schema.getFieldAt(i + 1);
                if (field == null) {
                    throw new IllegalArgumentException("No field for index " + i + 1);
                }
                map.put(field, values[i]);
            }
            return new GenericMapData<>(schema, map);
        }
    }
}
