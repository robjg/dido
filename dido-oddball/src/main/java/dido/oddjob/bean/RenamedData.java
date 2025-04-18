package dido.oddjob.bean;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.useful.AbstractData;
import dido.data.useful.DataSchemaImpl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Todo: What does this do.
 */
public class RenamedData extends AbstractData {

    private final Schema toSchema;

    private final DidoData original;

    RenamedData(Schema toSchema, DidoData original) {
        this.toSchema = toSchema;
        this.original = original;
    }

    static class Transform implements Function<DidoData, DidoData> {

        private final Map<String, String> fieldMap;

        private Schema schemaOut;

        private DataSchema lastIn;

        Transform(Map<String, String> fieldMap) {
            this.fieldMap = fieldMap;
        }

        @Override
        public DidoData apply(DidoData dataIn) {
            if (schemaOut == null || lastIn != dataIn.getSchema()) {
                lastIn = dataIn.getSchema();
                schemaOut = renamedSchema(fieldMap, lastIn);
            }

            return new RenamedData(schemaOut, dataIn);
        }
    }

    public static TransformBuilder transformer() {

        return new TransformBuilder(t -> {
            throw new IllegalArgumentException("Name Clash " + t);
        });
    }

    public static TransformBuilder transformerWithNameClash(Function<? super String, ? extends String> policy) {

        return new TransformBuilder(policy);
    }

    public static class TransformBuilder {

        private Map<String, String> map = new LinkedHashMap<>();

        private final Function<? super String, ? extends String> nameClash;

        public TransformBuilder(Function<? super String, ? extends String> nameClash) {
            this.nameClash = nameClash;
        }


        public Function<DidoData, DidoData> build() {
            Map<String, String> copy = this.map;
            this.map = new LinkedHashMap<>();
            return new Transform(copy);
        }

        public TransformBuilder addMapping(String from, String to) {
            if (map.containsValue(to)) {
                return addMapping(from, nameClash.apply(to));
            } else {
                map.put(from, to);
                return this;
            }
        }
    }

    @Override
    public ReadSchema getSchema() {
        return toSchema;
    }

    @Override
    public Object getAt(int index) {
        return original.getAt(index);
    }

    @Override
    public boolean hasAt(int index) {
        return original.hasAt(index);
    }

    static Schema renamedSchema(Map<String, String> mapping, DataSchema fromSchema) {

        DataSchemaFactory schemaFactory = DataSchemaFactory.newInstance();
        for (SchemaField schemaField : fromSchema.getSchemaFields()) {
            schemaFactory.addSchemaField(schemaField.mapToFieldName(
                    mapping.get(schemaField.getName())));
        }
        return new Schema((DataSchemaImpl) schemaFactory.toSchema(), fromSchema);
    }

    static class Schema extends DataSchemaImpl implements ReadSchema {

        private final ReadSchema fromSchema;

        Schema(DataSchemaImpl schema,
               DataSchema fromSchema) {
            super(schema);
            this.fromSchema = ReadSchema.from(fromSchema);
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            return fromSchema.getFieldGetterAt(index);
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = Schema.this.getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(index, Schema.this);
            }
            return getFieldGetterAt(index);
        }
    }

}
